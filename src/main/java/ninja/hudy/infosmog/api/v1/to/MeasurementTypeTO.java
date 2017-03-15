package ninja.hudy.infosmog.api.v1.to;

import java.util.List;

public class MeasurementTypeTO {
    private Long id;
    private String name;
    private String displayName;
    private ThresholdTO thresholds;
    private List<MeasurementTO> measurements;

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

    public List<MeasurementTO> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementTO> measurements) {
        this.measurements = measurements;
    }

    public ThresholdTO getThresholds() {
        return thresholds;
    }

    public void setThresholds(ThresholdTO thresholds) {
        this.thresholds = thresholds;
    }
}
