package pl.org.smok.infosmog.api.v1.to

import com.fasterxml.jackson.annotation.JsonIgnore

class MeasurementTO {
    var hour: Int? = null

    var value: Double? = null

    @JsonIgnore
    var timestamp: Long? = null
}
