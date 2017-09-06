package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.CityRegion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
interface CityRepository : JpaRepository<CityRegion, Long> {
    fun findByName(name: String): Set<CityRegion>

    fun findByCode(code: Int?): Optional<CityRegion>
}
