package pl.org.smok.infosmog.model

import javax.persistence.*

@Entity
@Table(indexes = arrayOf(Index(columnList = "id"), Index(columnList = "name")))
class MeasurementType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var name: String? = null

    @Column(nullable = false)
    var displayName: String? = null

    @Column(nullable = false)
    var acceptable: Int? = null

    @Column(nullable = false)
    var informative: Int? = null

    @Column(nullable = false)
    var alarm: Int? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as MeasurementType?

        if (if (id != null) id != that?.id else that?.id != null) return false
        if (if (name != null) name != that?.name else that?.name != null) return false
        return if (displayName != null) displayName == that?.displayName else that?.displayName == null

    }

    override fun hashCode(): Int {
        var result = if (id != null) id?.hashCode() else 0
        result = 31 * result + if (name != null) name?.hashCode() else 0
        result = 31 * result + if (displayName != null) displayName?.hashCode() else 0
        return result
    }
}
