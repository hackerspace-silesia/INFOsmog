package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.Measurement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
interface MeasurementRepository : JpaRepository<Measurement, Long> {
    fun findByStationIdAndTimestampAndTypeId(id: Long?, timestamp: Long?, typeId: Long?): Optional<Measurement>

    fun findByStationIdAndTimestampAfter(id: Long?, timestamp: Long?): Set<Measurement>

    fun findByTimestampAfter(timestamp: Long?): Set<Measurement>
}
