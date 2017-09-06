package pl.org.smok.infosmog.api.v1.to

class MeasurementTypeTO {
    var id: Long? = null
    var name: String? = null
    var displayName: String? = null
    var thresholds: ThresholdTO? = null
    var measurements: List<MeasurementTO>? = null
}
