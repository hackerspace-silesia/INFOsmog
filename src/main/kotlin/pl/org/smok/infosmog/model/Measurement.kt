package pl.org.smok.infosmog.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

@Entity
@Table(indexes = arrayOf(Index(columnList = "id"), Index(columnList = "station_id"), Index(columnList = "timestamp"), Index(columnList = "type_id")))
class Measurement {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    var id: String? = null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    var station: Station? = null

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    var type: MeasurementType? = null

    @Column(nullable = false)
    var timestamp: Long? = null

    @Column(nullable = false)
    var value: Double? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as Measurement?

        if (id != that!!.id) return false
        if (station != that.station) return false
        if (type != that.type) return false
        if (timestamp != that.timestamp) return false
        return value == that.value

    }

    override fun hashCode(): Int {
        var result = id!!.hashCode()
        result = 31 * result + station!!.hashCode()
        result = 31 * result + type!!.hashCode()
        result = 31 * result + timestamp!!.hashCode()
        result = 31 * result + value!!.hashCode()
        return result
    }
}
