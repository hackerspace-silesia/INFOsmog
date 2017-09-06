package pl.org.smok.infosmog.update

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import pl.org.smok.infosmog.exception.UpdateException
import pl.org.smok.infosmog.model.VoivodeshipRegion
import pl.org.smok.infosmog.name.DefaultName
import pl.org.smok.infosmog.repository.VoivodeshipRepository
import pl.org.smok.infosmog.repository.CountyRepository
import pl.org.smok.infosmog.repository.CityRepository
import pl.org.smok.infosmog.repository.StationRepository
import pl.org.smok.infosmog.repository.MeasurementTypeRepository
import pl.org.smok.infosmog.type.StationType
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.org.smok.infosmog.model.CityRegion
import pl.org.smok.infosmog.model.CountyRegion
import pl.org.smok.infosmog.model.MeasurementType

import java.io.InputStream
import java.util.Optional

@Component
class ScratchUpdater {

    @Autowired
    private lateinit var voivodeshipRepository: VoivodeshipRepository;

    @Autowired
    private lateinit var countyRepository: CountyRepository;

    @Autowired
    private lateinit var cityRepository: CityRepository;

    @Autowired
    private lateinit var stationRepository: StationRepository;

    @Autowired
    private lateinit var measurementTypeRepository: MeasurementTypeRepository;

    @Transactional
    @Throws(UpdateException::class)
    fun createFromScratch() {
        log.info("Load configuration from scratch")
        createUndefinedRegions()
        createVoivodeships()
        createCounties()
        createCities()
        setCapitalCities()
        createMeasurementTypes()
    }

    @Throws(UpdateException::class)
    private fun createVoivodeships() {
        log.info("Importing voivodeships")
        try {
            val path = "/configuration/voivodeships.json"

            val `in` = this.javaClass.getResourceAsStream(path)

            val regions = MAPPER.readValue<JsonNode>(`in`, JsonNode::class.java).get("regions")

            regions.forEach({ node ->
                val region = VoivodeshipRegion()
                region.name = node.get("name").asText()
                region.code = node.get("code").asInt()

                if (node.has("weather_id")) {
                    region.weatherApiId = node.get("weather_id").asLong()
                }

                voivodeshipRepository.save(region)
            })

            voivodeshipRepository.flush()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    @Throws(UpdateException::class)
    private fun createCounties() {
        log.info("Importing counties")
        try {
            val path = "/configuration/counties.json"

            val `in` = this.javaClass.getResourceAsStream(path)

            val regions = MAPPER.readValue<JsonNode>(`in`, JsonNode::class.java).get("regions")

            regions.forEach({ node ->
                val code = node.get("code").asInt()
                val superCode = getSuperCode(code)
                val voivodeship = voivodeshipRepository.findByCode(superCode)

                if (voivodeship.isPresent) {
                    val region = CountyRegion()
                    region.name = node.get("name").asText()
                    region.code = node.get("code").asInt()

                    if (node.has("weather_id")) {
                        region.weatherApiId = node.get("weather_id").asLong()
                    }

                    region.superRegion = voivodeship.get()

                    countyRepository!!.save(region)
                }
            })

            countyRepository!!.flush()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    @Throws(UpdateException::class)
    private fun createCities() {
        log.info("Importing cities")
        try {
            val path = "/configuration/cities.json"

            val `in` = this.javaClass.getResourceAsStream(path)

            val regions = MAPPER.readValue<JsonNode>(`in`, JsonNode::class.java!!).get("regions")

            regions.forEach({ node ->
                val code = node.get("code").asInt()
                val superCode = getSuperCode(code)
                val county = countyRepository!!.findByCode(superCode)

                if (county.isPresent) {
                    val region = CityRegion()
                    region.name = node.get("name").asText()
                    region.code = node.get("code").asInt()

                    if (node.has("weather_id")) {
                        region.weatherApiId = node.get("weather_id").asLong()
                    }

                    region.superRegion = county.get()

                    cityRepository!!.save(region)
                }
            })

            cityRepository!!.flush()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    private fun setCapitalCities() {
        log.info("Assigning capital cities")
        //placeholder
    }

    @Throws(UpdateException::class)
    fun assignKnownStations() {
        log.info("Assigning known stations")
        try {
            val path = "/configuration/stations.json"

            val `in` = this.javaClass.getResourceAsStream(path)

            val stations = MAPPER.readValue<JsonNode>(`in`, JsonNode::class.java).get("stations")

            stations.forEach({ node ->
                val stationId = node.get("stationId").asInt()
                val regionCode = node.get("regionCode").asInt()
                val stationName = node.get("stationName").asText()

                val cityName = stationName.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0].trim({ it <= ' ' })

                val cities = cityRepository.findByName(cityName)

                if (cities.isEmpty()) {
                    log.warn("No city for     :" + stationName)
                } else if (cities.size > 1) {
                    log.warn("More cities for :" + stationName)
                } else {
                    val city = cities.stream().findAny().get()
                    val stationDO = stationRepository.findByTypeAndProviderCode(StationType.GIOS, "$stationId")

                    if (stationDO.isPresent) {
                        val station = stationDO.get()
                        station.city = city

                        stationRepository.save(station)
                    } else {
                        log.warn("Missing station $stationName and id $stationId")
                    }

                }
            })

            stationRepository.flush()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    @Throws(UpdateException::class)
    private fun createUndefinedRegions() {
        log.info("Creating undefined regions")
        try {
            val defaultName = DefaultName.UNDEFINED_NAME.name

            val voivodeships = voivodeshipRepository.findByName(defaultName)
            val voivodeship: VoivodeshipRegion

            if (voivodeships.isEmpty()) {
                voivodeship = VoivodeshipRegion()
                voivodeship.name = defaultName
                voivodeship.code = -1
                voivodeshipRepository.saveAndFlush(voivodeship)
            } else {
                voivodeship = voivodeships.stream().findAny().get()
            }

            val counties = countyRepository.findByName(defaultName)
            val county: CountyRegion

            if (counties.isEmpty()) {
                county = CountyRegion()
                county.name = defaultName
                county.code = -1
                county.superRegion = voivodeship
                countyRepository.saveAndFlush(county)
            } else {
                county = counties.stream().findAny().get()
            }

            val cities = cityRepository.findByName(defaultName)

            if (cities.isEmpty()) {
                val city = CityRegion()
                city.name = defaultName
                city.code = -1
                city.superRegion = county
                cityRepository.saveAndFlush(city)
            }

            countyRepository.saveAndFlush(county)
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    @Throws(UpdateException::class)
    private fun createMeasurementTypes() {
        log.info("Creating measurement types")
        try {
            val path = "/configuration/types.json"

            val `in` = this.javaClass.getResourceAsStream(path)

            val types = MAPPER.readValue<JsonNode>(`in`, JsonNode::class.java!!).get("types")

            types.forEach({ node ->
                val name = node.get("name").asText()
                val displayName = node.get("displayName").asText()
                val acceptable = node.get("acceptable").asInt()
                val informative = node.get("informative").asInt()
                val alarm = node.get("alarm").asInt()

                val measurementType = MeasurementType()
                measurementType.name = name
                measurementType.displayName = displayName
                measurementType.acceptable = acceptable
                measurementType.informative = informative
                measurementType.alarm = alarm

                measurementTypeRepository!!.save(measurementType)
            })

            measurementTypeRepository!!.flush()
        } catch (e: Exception) {
            throw UpdateException(e)
        }

    }

    private fun getSuperCode(code: Int): Int {
        return code / 100
    }

    companion object {
        private val log = Logger.getLogger(ScratchUpdater::class.java!!)

        private val MAPPER = ObjectMapper()
    }
}
