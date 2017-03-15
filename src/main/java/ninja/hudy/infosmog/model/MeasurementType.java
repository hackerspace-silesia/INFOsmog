package ninja.hudy.infosmog.model;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "name")
})
public class MeasurementType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private Integer acceptable;

    @Column(nullable = false)
    private Integer informative;

    @Column(nullable = false)
    private Integer alarm;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getAcceptable() {
        return acceptable;
    }

    public void setAcceptable(Integer acceptable) {
        this.acceptable = acceptable;
    }

    public Integer getInformative() {
        return informative;
    }

    public void setInformative(Integer informative) {
        this.informative = informative;
    }

    public Integer getAlarm() {
        return alarm;
    }

    public void setAlarm(Integer alarm) {
        this.alarm = alarm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementType that = (MeasurementType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return displayName != null ? displayName.equals(that.displayName) : that.displayName == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}
