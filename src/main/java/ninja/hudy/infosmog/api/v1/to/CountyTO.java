package ninja.hudy.infosmog.api.v1.to;

import java.util.List;

public class CountyTO {

    private String name;
    private String now;
    private List<MeasurementTypeTO> measurementTypes;
    private int weatherType;
    private long weatherDegree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public List<MeasurementTypeTO> getMeasurementTypes() {
        return measurementTypes;
    }

    public void setMeasurementTypes(List<MeasurementTypeTO> measurementTypes) {
        this.measurementTypes = measurementTypes;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    public long getWeatherDegree() {
        return weatherDegree;
    }

    public void setWeatherDegree(long weatherDegree) {
        this.weatherDegree = weatherDegree;
    }
}
