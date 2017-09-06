package pl.org.smok.infosmog.api.v1.to

import pl.org.smok.infosmog.model.CountyRegion
import pl.org.smok.infosmog.model.Measurement
import pl.org.smok.infosmog.model.MeasurementType

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Converter {
    private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val ZONE_ID = "Europe/Warsaw"

    fun getMeasurementTO(measurement: Measurement, hour: Int?): MeasurementTO {
        val measurementTO = MeasurementTO()

        measurementTO.timestamp = measurement.timestamp
        measurementTO.value = measurement.value
        measurementTO.hour = hour

        return measurementTO
    }

    fun getMeasurementTypeTO(measurementType: MeasurementType, measurementTOs: List<MeasurementTO>): MeasurementTypeTO {
        val measurementTypeTO = MeasurementTypeTO()

        measurementTypeTO.id = measurementType.id
        measurementTypeTO.name = measurementType.name
        measurementTypeTO.displayName = measurementType.displayName
        measurementTypeTO.measurements = measurementTOs
        measurementTypeTO.thresholds = getThresholdTO(measurementType)

        return measurementTypeTO
    }

    fun getThresholdTO(measurementType: MeasurementType): ThresholdTO {
        val thresholdTO = ThresholdTO()

        thresholdTO.acceptable = measurementType.acceptable
        thresholdTO.informative = measurementType.informative
        thresholdTO.alarm = measurementType.alarm

        return thresholdTO
    }

    fun getCountyTO(county: CountyRegion, measurementTypeTOs: List<MeasurementTypeTO>, dateTime: ZonedDateTime): CountyTO {
        val countyTO = CountyTO()
        val now = dateTime.withZoneSameInstant(ZoneId.of(ZONE_ID)).format(DATE_TIME_FORMATTER)

        countyTO.name = county.name
        countyTO.now = now
        countyTO.weatherType = if (null == county.weatherType) 0 else county.weatherType
        countyTO.weatherDegree = if (null == county.weatherDegree) 0 else Math.round(county.weatherDegree!!)

        countyTO.measurementTypes = measurementTypeTOs

        return countyTO
    }

    fun getCountyLightTO(county: CountyRegion): CountyLightTO {
        val countyLightTO = CountyLightTO()

        countyLightTO.name = county.name
        countyLightTO.id = county.id
        countyLightTO.voivodeship = county.superName

        return countyLightTO
    }
}// Utils class
