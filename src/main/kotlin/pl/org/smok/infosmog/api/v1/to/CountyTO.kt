package pl.org.smok.infosmog.api.v1.to

class CountyTO {
    var name: String? = null
    var now: String? = null
    var measurementTypes: List<MeasurementTypeTO>? = null
    var weatherType: Int = 0
    var weatherDegree: Long = 0
}
