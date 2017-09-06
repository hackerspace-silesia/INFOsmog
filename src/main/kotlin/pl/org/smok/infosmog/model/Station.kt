package pl.org.smok.infosmog.model

import com.fasterxml.jackson.annotation.JsonIgnore
import pl.org.smok.infosmog.type.StationType

import javax.persistence.*

@Entity
@Table(indexes = arrayOf(Index(columnList = "id"), Index(columnList = "city_id"), Index(columnList = "providerCode"), Index(columnList = "type")))
class Station(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,

        @JsonIgnore
        @ManyToOne
        @JoinColumn(nullable = false)
        var city: CityRegion,

        @Column(nullable = false)
        var name: String,

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        var type: StationType,

        @Column(nullable = false)
        var providerCode: String
)
