package pl.org.smok.infosmog.api.v1

import pl.org.smok.infosmog.api.v1.to.*
import pl.org.smok.infosmog.exception.InfoSmogException
import pl.org.smok.infosmog.facade.CountyFacade
import pl.org.smok.infosmog.model.CountyRegion
import pl.org.smok.infosmog.model.Measurement
import pl.org.smok.infosmog.model.MeasurementType
import pl.org.smok.infosmog.model.Station
import pl.org.smok.infosmog.repository.CountyRepository
import pl.org.smok.infosmog.repository.MeasurementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*

import java.text.Collator
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.stream.Collectors

@CrossOrigin
@RestController
@RequestMapping("/api/v1/county")
class CountyController(val countyRepository: CountyRepository,
                       val measurementRepository: MeasurementRepository,
                       val countyFacade: CountyFacade) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    @Throws(InfoSmogException::class)
    internal fun findAll(): Collection<CountyLightTO> {
        try {
            val oneDayAgo = ZonedDateTime.now().minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond()

            val result = countyFacade.getCountiesWithDataAfter(oneDayAgo)
                    .sort { c1, c2 ->

                        var voivode = POLISH_COLLATOR.compare(c1.superName, c2.superName)

                        if (0 == voivode) {
                            voivode = countyFacade.getCountiesWithDataAfter(oneDayAgo)
                                    .sorted(c1.name, c2.name)
                        }

                        return voivode
                    }
                    .map<CountyLightTO>(Function<CountyRegion, CountyLightTO> { Converter.getCountyLightTO(it) })
                    .collect<List<CountyLightTO>, Any>(Collectors.toList<CountyLightTO>())

            return result
        } catch (e: Exception) {
            throw InfoSmogException(e)
        }

    }

    @Cacheable(value = "county")
    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/{countyId}")
    @Throws(InfoSmogException::class)
    internal operator fun get(@PathVariable countyId: Long): CountyTO {
        try {
            val county = countyRepository.findById(countyId) ?: throw InfoSmogException("Unknown county id")

            val current = ZonedDateTime.now()
            val oneDayAgo = current.minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond()

            val measurementByType = county?.let { it.get() }.cities
                    .filter { city -> !city.stations.isEmpty() }
                    .flatMap { city -> city.stations }
                    .flatMap { station -> findMeasurements(station, oneDayAgo) }
                    .collect<Map<MeasurementType, List<Measurement>>, Any>(Collectors.groupingBy<Measurement, MeasurementType>(Function<Measurement, MeasurementType> { it.getType() }))

            val measurementTypeTOs = LinkedList<MeasurementTypeTO>()

            measurementByType.entries.forEach { entry ->
                val measurementByHour = entry.value.stream()
                        .collect(Collectors.groupingBy<Measurement, Int> { maa ->
                            ZonedDateTime
                                    .ofInstant(Instant.ofEpochSecond(maa.timestamp!!), ZoneId.of(ZONE_ID))
                                    .hour
                        })

                val measurementTOs = LinkedList<MeasurementTO>()

                measurementByHour.entries.forEach { hour ->
                    val measurement = getMax(hour.value)

                    if (null != measurement) {
                        val to = Converter.getMeasurementTO(measurement, hour.key)
                        measurementTOs.add(to)
                    }
                }

                measurementTOs.sort { m1, m2 -> m1.timestamp!!.compareTo(m2.timestamp!!) }

                val type = Converter.getMeasurementTypeTO(entry.key, measurementTOs)
                measurementTypeTOs.add(type)

            }

            return Converter.getCountyTO(county, measurementTypeTOs, current)
        } catch (e: Exception) {
            throw InfoSmogException(e)
        }

    }

    private fun getMax(measurements: List<Measurement>): Measurement? {
        var result: Measurement? = null
        var max = -9999.0

        for (measurement in measurements) {
            if (measurement.value!! > max) {
                result = measurement
                max = measurement.value!!
            }
        }

        return result
    }

    private fun findMeasurements(station: Station, timestamp: Long): Set<Measurement> {
        return measurementRepository.findByStationIdAndTimestampAfter(station.id, timestamp)
    }

    private val ZONE_ID = "Europe/Warsaw"
    private val POLISH_COLLATOR = Collator.getInstance(Locale("pl"))
}
