package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.Station
import pl.org.smok.infosmog.type.StationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StationRepository : JpaRepository<Station, Long> {
    fun findByType(type: StationType): List<Station>
    fun findByTypeAndProviderCode(type: StationType, providerCode: String): Optional<Station>
}
