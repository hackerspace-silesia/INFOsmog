package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.CountyRegion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface CountyRepository : JpaRepository<CountyRegion, Long> {
    fun findByName(name: String): Set<CountyRegion>

    fun findByCode(code: Int): CountyRegion
}
