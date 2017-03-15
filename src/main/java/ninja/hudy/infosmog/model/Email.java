package ninja.hudy.infosmog.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Email {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    private String id;

    @Column(nullable = false)
    private String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "email_region", joinColumns = @JoinColumn(name = "email_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "region_id", referencedColumnName = "id"))
    private Set<Region> regions = new HashSet<>();
}
