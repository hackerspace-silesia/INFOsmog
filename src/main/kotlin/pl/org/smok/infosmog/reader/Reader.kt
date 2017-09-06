package pl.org.smok.infosmog.reader

import pl.org.smok.infosmog.repository.CityRepository
import pl.org.smok.infosmog.repository.MeasurementRepository
import pl.org.smok.infosmog.repository.MeasurementTypeRepository
import pl.org.smok.infosmog.repository.StationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

abstract class Reader(val measurementTypeRepository: MeasurementTypeRepository,
                      val measurementRepository: MeasurementRepository,
                      val stationRepository: StationRepository,
                      val cityRepository: CityRepository) {

    @Transactional
    abstract fun read()
}
