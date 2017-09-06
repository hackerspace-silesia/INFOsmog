package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.VoivodeshipRegion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
interface VoivodeshipRepository : JpaRepository<VoivodeshipRegion, Long> {
    fun findByName(name: String): Set<VoivodeshipRegion>

    fun findByCode(code: Int?): Optional<VoivodeshipRegion>
}
