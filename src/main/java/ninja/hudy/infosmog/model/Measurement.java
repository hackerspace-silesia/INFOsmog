package ninja.hudy.infosmog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "id"),
        @Index(columnList = "station_id"),
        @Index(columnList = "timestamp"),
        @Index(columnList = "type_id")
})
public class Measurement {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true)
    private String id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private Station station;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    private MeasurementType type;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Double value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public MeasurementType getType() {
        return type;
    }

    public void setType(MeasurementType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        if (!id.equals(that.id)) return false;
        if (!station.equals(that.station)) return false;
        if (!type.equals(that.type)) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + station.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
