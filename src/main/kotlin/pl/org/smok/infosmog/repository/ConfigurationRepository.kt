package pl.org.smok.infosmog.repository

import pl.org.smok.infosmog.model.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigurationRepository : JpaRepository<Configuration, Long>
