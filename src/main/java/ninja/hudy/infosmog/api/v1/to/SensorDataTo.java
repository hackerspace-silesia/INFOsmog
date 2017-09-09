package ninja.hudy.infosmog.api.v1.to;

import java.math.BigDecimal;

public class SensorDataTo {

    private BigDecimal temperature;
    private BigDecimal levelOfPollution;
    private Long pressure;

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
}
