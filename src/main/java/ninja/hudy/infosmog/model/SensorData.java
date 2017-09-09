package ninja.hudy.infosmog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class SensorData implements Serializable {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal temperature;

    @Column(nullable = false)
    private BigDecimal levelOfPollution;

    @Column(nullable = false)
    private Long pressure;

    @Column(nullable = false)
    private Long creationDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getLevelOfPollution() {
        return levelOfPollution;
    }

    public void setLevelOfPollution(BigDecimal levelOfPollution) {
        this.levelOfPollution = levelOfPollution;
    }

    public Long getPressure() {
        return pressure;
    }

    public void setPressure(Long pressure) {
        this.pressure = pressure;
    }

    public Long getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Long creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
