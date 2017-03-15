package ninja.hudy.infosmog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "name"),
        @Index(columnList = "code"),
        @Index(columnList = "type")
})
public abstract class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer code;

    @ManyToOne
    private Region superRegion;

    @OneToMany(mappedBy = "superRegion", fetch = FetchType.LAZY)
    private Set<Region> nestedRegions = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "regions")
    private Set<Email> emails = new HashSet<>();

    @Column(insertable = false, updatable = false)
    private String type;

    @Column(nullable = false)
    private Integer weatherType = 0;

    @Column(nullable = false)
    private Double weatherDegree = 0.0;

    private Long weatherApiId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Region getSuperRegion() {
        return superRegion;
    }

    public void setSuperRegion(Region superRegion) {
        this.superRegion = superRegion;
    }

    public Set<Region> getNestedRegions() {
        return nestedRegions;
    }

    public void setNestedRegions(Set<Region> nestedRegions) {
        this.nestedRegions = nestedRegions;
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public String getType() {
        return type;
    }

    public Integer getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(Integer weatherType) {
        this.weatherType = weatherType;
    }

    public Double getWeatherDegree() {
        return weatherDegree;
    }

    public void setWeatherDegree(Double weatherDegree) {
        this.weatherDegree = weatherDegree;
    }

    public Long getWeatherApiId() {
        return weatherApiId;
    }

    public void setWeatherApiId(Long weatherApiId) {
        this.weatherApiId = weatherApiId;
    }

    public String getSuperName() {
        if (null != superRegion) {
            return superRegion.getName();
        }

        return "";
    }
}
