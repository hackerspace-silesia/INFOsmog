package pl.org.smok.infosmog.facade

import pl.org.smok.infosmog.name.DefaultName
import pl.org.smok.infosmog.repository.MeasurementRepository
import pl.org.smok.infosmog.model.CountyRegion
import pl.org.smok.infosmog.model.RegionType
import pl.org.smok.infosmog.model.Measurement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CountyFacade {

    @Autowired
    lateinit var measurementRepository: MeasurementRepository;


    fun getCountiesWithDataAfter(timestamp: Long): List<CountyRegion> {
        val result = findMeasurements(timestamp)
                .filter {
                    "PM10".equals(it.type?.name, ignoreCase = true) || "NO2".equals(it.type?.name, ignoreCase = true)
                }
                .map { it.station }
                .distinct()
                .mapNotNull { it?.city }
                .distinct()
                .mapNotNull { it.superRegion }
                .distinct()
                .filter { RegionType.Values.COUNTY == it.type && DefaultName.UNDEFINED_NAME.name != it.name }
                .map { region -> region as CountyRegion }

        return result
    }


    private fun findMeasurements(timestamp: Long): Set<Measurement> {
        return measurementRepository.findByTimestampAfter(timestamp)
    }
}
