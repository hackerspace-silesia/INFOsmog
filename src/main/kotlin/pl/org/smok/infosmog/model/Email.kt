package pl.org.smok.infosmog.model

import org.hibernate.annotations.GenericGenerator

import javax.persistence.*
import java.util.HashSet

@Entity
class Email {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    private val id: String? = null

    @Column(nullable = false)
    private val email: String? = null

    @ManyToMany(cascade = arrayOf(CascadeType.ALL))
    @JoinTable(name = "email_region", joinColumns = JoinColumn(name = "email_id", referencedColumnName = "id"), inverseJoinColumns = JoinColumn(name = "region_id", referencedColumnName = "id"))
    private val regions = HashSet<Region>()
}
