package ninja.hudy.infosmog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ninja.hudy.infosmog.type.StationType;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "city_id"),
        @Index(columnList = "providerCode"),
        @Index(columnList = "type")
})
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private CityRegion city;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StationType type;

    @Column(nullable = false)
    private String providerCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CityRegion getCity() {
        return city;
    }

    public void setCity(CityRegion city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StationType getType() {
        return type;
    }

    public void setType(StationType type) {
        this.type = type;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }
}
