package pl.org.smok.infosmog.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Configuration {
    @Column(nullable = false)
    @Id
    var version: Int? = null

    var description: String? = null
}
