package ninja.hudy.infosmog.api.v1.to;

import ninja.hudy.infosmog.model.CountyRegion;
import ninja.hudy.infosmog.model.Measurement;
import ninja.hudy.infosmog.model.MeasurementType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class Converter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String ZONE_ID = "Europe/Warsaw";

    private Converter() {
        // Utils class
    }

    public static MeasurementTO getMeasurementTO(Measurement measurement, Integer hour) {
        MeasurementTO measurementTO = new MeasurementTO();

        measurementTO.setTimestamp(measurement.getTimestamp());
        measurementTO.setValue(measurement.getValue());
        measurementTO.setHour(hour);

        return measurementTO;
    }

    public static MeasurementTypeTO getMeasurementTypeTO(MeasurementType measurementType, List<MeasurementTO> measurementTOs) {
        MeasurementTypeTO measurementTypeTO = new MeasurementTypeTO();

        measurementTypeTO.setId(measurementType.getId());
        measurementTypeTO.setName(measurementType.getName());
        measurementTypeTO.setDisplayName(measurementType.getDisplayName());
        measurementTypeTO.setMeasurements(measurementTOs);
        measurementTypeTO.setThresholds(getThresholdTO(measurementType));

        return measurementTypeTO;
    }

    public static ThresholdTO getThresholdTO(MeasurementType measurementType) {
        ThresholdTO thresholdTO = new ThresholdTO();

        thresholdTO.setAcceptable(measurementType.getAcceptable());
        thresholdTO.setInformative(measurementType.getInformative());
        thresholdTO.setAlarm(measurementType.getAlarm());

        return thresholdTO;
    }

    public static CountyTO getCountyTO(CountyRegion county, List<MeasurementTypeTO> measurementTypeTOs, ZonedDateTime dateTime) {
        CountyTO countyTO = new CountyTO();
        String now = dateTime.withZoneSameInstant(ZoneId.of(ZONE_ID)).format(DATE_TIME_FORMATTER);

        countyTO.setName(county.getName());
        countyTO.setNow(now);
        countyTO.setWeatherType(null == county.getWeatherType() ? 0 : county.getWeatherType());
        countyTO.setWeatherDegree(null == county.getWeatherDegree() ? 0 : Math.round(county.getWeatherDegree()));

        countyTO.setMeasurementTypes(measurementTypeTOs);

        return countyTO;
    }

    public static CountyLightTO getCountyLightTO(CountyRegion county) {
        CountyLightTO countyLightTO = new CountyLightTO();

        countyLightTO.setName(county.getName());
        countyLightTO.setId(county.getId());
        countyLightTO.setVoivodeship(county.getSuperName());

        return countyLightTO;
    }
}
