package ninja.hudy.infosmog.api.v1.to;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MeasurementTO {
    private Integer hour;

    private Double value;

    @JsonIgnore
    private Long timestamp;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
