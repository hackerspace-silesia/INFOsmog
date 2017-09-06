package pl.org.smok.infosmog.reader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import pl.org.smok.infosmog.exception.HttpCommunicationException
import pl.org.smok.infosmog.exception.InfoSmogException
import pl.org.smok.infosmog.model.Measurement
import pl.org.smok.infosmog.model.MeasurementType
import pl.org.smok.infosmog.model.Station
import pl.org.smok.infosmog.name.DefaultName
import pl.org.smok.infosmog.type.StationType
import org.apache.log4j.Logger
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate
import pl.org.smok.infosmog.repository.CityRepository
import pl.org.smok.infosmog.repository.MeasurementRepository
import pl.org.smok.infosmog.repository.MeasurementTypeRepository
import pl.org.smok.infosmog.repository.StationRepository

import java.io.IOException
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@Controller
class GIOSReader(restTemplate: RestTemplate,
                 measurementTypeRepository: MeasurementTypeRepository,
                 measurementRepository: MeasurementRepository,
                 stationRepository: StationRepository,
                 cityRepository: CityRepository) :
        HttpReader(restTemplate,
                measurementTypeRepository,
                measurementRepository,
                stationRepository,
                cityRepository) {

    private inner class StationCurrent(val name: String) {
        private val measurement = HashMap<String, Double>()

        fun getMeasurement(): Map<String, Double> {
            return measurement
        }

        fun addMeasurement(type: String, value: Double) {
            measurement.put(type, value)
        }
    }

    override fun read() {
        try {
            log.info("Start GIOS updating")
            val currentTimestamp = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).toEpochSecond()
            val stations = stations
            createMissingStations(stations)
            getMeasurement(stations, currentTimestamp)
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            log.info("Finished GIOS updating")
        }
    }

    @Throws(HttpCommunicationException::class, IOException::class)
    private fun getMeasurement(stations: Map<Int, StationCurrent>, currentTimestamp: Long) {
        stations.entries.stream().parallel().forEach { entry ->
            val id = entry.key
            try {
                val response = sendGet(API_URL + "/get_data_chart?days=" + DAYS.toString() + "&stationId=" + id.toString())
                val rootNode = MAPPER.readValue<JsonNode>(response, JsonNode::class.java)
                val chartElements = rootNode.get("chartElements")
                val measurement = HashMap<String, Map<Long, Double>>()

                chartElements.forEach({ element ->
                    val type = element.get("key").asText()
                    val values = element.get("values")

                    val `val` = HashMap<Long, Double>()
                    if (StringUtils.hasText(type) && values.size() > 0) {
                        values.forEach({ v ->
                            val millis = v.get(0).asLong() / 1000
                            var value: Double? = null

                            if (!v.get(1).isNull()) {
                                value = v.get(1).asDouble()
                            } else if (millis >= currentTimestamp) {
                                value = entry.value.getMeasurement()[type]
                            }

                            if (null != value) {
                                `val`.put(millis, value)
                            }
                        })
                    }

                    if (!`val`.isEmpty()) {
                        measurement.put(type, `val`)
                    }
                })

                createMissingTypes(measurement.keys)
                addMeasurement(measurement, id.toString())
            } catch (e: Exception) {
                log.error("Cannot get measurement for " + id, e)
            }
        }
    }

    private fun addMeasurement(measurementByType: Map<String, Map<Long, Double>>, providerCode: String) {
        val station = stationRepository!!.findByTypeAndProviderCode(STATION_TYPE, providerCode).get()
        val measurementTypes = measurementTypeRepository!!.findAll()

        for ((key, value) in measurementByType) {
            val typeDO = measurementTypes.stream().filter { t -> t.name.equals(key, ignoreCase = true) }.findAny()

            if (typeDO.isPresent) {
                val type = typeDO.get()
                for (m in value.entries) {
                    val timestamp = m.key
                    val measurement = measurementRepository!!.findByStationIdAndTimestampAndTypeId(station.id, timestamp, type.id)

                    if (!measurement.isPresent) {
                        val newOne = Measurement()
                        newOne.timestamp = timestamp
                        newOne.station = station
                        newOne.type = type
                        newOne.value = m.value
                        measurementRepository!!.save(newOne)
                    } else if (measurement.get().value == null || measurement.get().value != m.value) {
                        val update = measurement.get()
                        update.value = m.value

                        measurementRepository!!.save(update)
                    }
                }
            }
        }

        measurementRepository!!.flush()
    }

    private fun createMissingTypes(types: Set<String>) {
        val typeDOs = measurementTypeRepository!!.findAll()
        var count: Long = 0

        for (type in types) {
            val typeDO = typeDOs.stream()
                    .filter { t ->
                        t.name
                                .equals(type, ignoreCase = true)
                    }
                    .findAny()

            if (!typeDO.isPresent) {
                val newType = MeasurementType()

                newType.name = type.toUpperCase()
                newType.displayName = type
                newType.acceptable = 50
                newType.informative = 200
                newType.alarm = 300

                measurementTypeRepository!!.save(newType)

                ++count
            }
        }

        if (count > 0) {
            measurementTypeRepository!!.flush()
            log.info("Added $count new types")
        }
    }

    private val stations: Map<Int, StationCurrent>
        @Throws(HttpCommunicationException::class, IOException::class)
        get() {
            val params = LinkedMultiValueMap<String, String>()
            params.add("param", "AQI")

            val response = sendGet(API_URL + "/getAQIDetailsList?param=AQI")

            val rootNode = MAPPER.readValue<JsonNode>(response, JsonNode::class.java)

            val stations = HashMap<Int, StationCurrent>()

            rootNode.forEach({ node ->
                val id = node.get("stationId").asInt()
                val name = node.get("stationName").asText()

                if (StringUtils.hasText(name) && !stations.containsKey(id)) {
                    val current = StationCurrent(name)

                    val values = node.get("values")
                    if (!values.isNull()) {
                        val iterator = values.fieldNames()
                        while (iterator.hasNext()) {
                            val type = iterator.next()
                            val value = values.get(type).asDouble()
                            current.addMeasurement(type, value)
                        }
                    }

                    stations.put(id, current)
                }
            })

            return stations
        }

    @Throws(InfoSmogException::class)
    private fun createMissingStations(stations: Map<Int, StationCurrent>) {
        val undefinedCities = cityRepository.findByName(DefaultName.UNDEFINED_NAME.name)

        if (undefinedCities.isEmpty()) {
            throw InfoSmogException("Missing undefined city!")
        }

        val undefinedCity = undefinedCities.stream().findAny()

        val existingIds = stationRepository.findByType(STATION_TYPE)
                .map { it.providerCode }
                .toSet()

        val count = stations
                .filter { existingIds.contains(it.key.toString()) }
                .map { (id, current) ->
                    val cityName = current.name.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0].trim({ it <= ' ' })
                    val cities = cityRepository.findByName(cityName)
                    var city = undefinedCity.get()

                    if (cities.size == 1) {
                        city = cities.stream().findAny().get()
                    }

                    val station = Station(name=current.name,
                            city = city,
                            type = STATION_TYPE,
                            providerCode = id.toString())

                    stationRepository.save(station)

                }
                .count()

        if (count > 0) {
            stationRepository.flush()
            log.info("Added $count new stations")
        }
    }

    private val log = Logger.getLogger(GIOSReader::class.java)
    private val API_URL = "http://powietrze.gios.gov.pl/pjp/current"
    private val MAPPER = ObjectMapper()
    private val STATION_TYPE = StationType.GIOS
    private val DAYS = 2
}
