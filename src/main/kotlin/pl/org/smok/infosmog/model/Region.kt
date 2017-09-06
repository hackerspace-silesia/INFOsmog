package pl.org.smok.infosmog.model

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*
import java.util.HashSet

@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(indexes = arrayOf(Index(columnList = "id"), Index(columnList = "name"), Index(columnList = "code"), Index(columnList = "type")))
abstract class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var code: Int? = null

    @ManyToOne
    var superRegion: Region? = null

    @OneToMany(mappedBy = "superRegion", fetch = FetchType.LAZY)
    var nestedRegions: Set<Region> = HashSet()

    @JsonIgnore
    @ManyToMany(mappedBy = "regions")
    var emails: Set<Email> = HashSet()

    @Column(insertable = false, updatable = false)
    val type: String? = null

    @Column(nullable = false)
    var weatherType: Int = 0

    @Column(nullable = false)
    var weatherDegree: Double? = 0.0

    var weatherApiId: Long? = null

    val superName: String
        get() {
            if (null != superRegion) {
                return "$superRegion.name"
            }

            return ""
        }
}
