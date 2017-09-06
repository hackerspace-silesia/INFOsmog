package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.MeasurementType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
interface MeasurementTypeRepository : JpaRepository<MeasurementType, Long> {

    fun findByName(name: String): Optional<MeasurementType>
}
