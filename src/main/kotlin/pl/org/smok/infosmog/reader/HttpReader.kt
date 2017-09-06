package pl.org.smok.infosmog.reader

import pl.org.smok.infosmog.exception.HttpCommunicationException
import org.hibernate.validator.constraints.NotBlank
import org.springframework.http.*
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import pl.org.smok.infosmog.repository.CityRepository
import pl.org.smok.infosmog.repository.MeasurementRepository
import pl.org.smok.infosmog.repository.MeasurementTypeRepository
import pl.org.smok.infosmog.repository.StationRepository

import javax.validation.constraints.NotNull

abstract class HttpReader(val restTemplate: RestTemplate,
                          measurementTypeRepository: MeasurementTypeRepository,
                          measurementRepository: MeasurementRepository,
                          stationRepository: StationRepository,
                          cityRepository: CityRepository) :
        Reader( measurementTypeRepository,
                measurementRepository,
                stationRepository,
                cityRepository) {

    @Throws(HttpCommunicationException::class)
    protected fun sendPost(@NotBlank address: String, @NotNull params: MultiValueMap<String, String>): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val request = HttpEntity(params, headers)

        val response = restTemplate.postForEntity(address, request, String::class.java)

        if (HttpStatus.OK != response.statusCode) {
            throw HttpCommunicationException("Send http request to " + address + " failed with code: " + response.statusCodeValue)
        }

        return response.body
    }

    @Throws(HttpCommunicationException::class)
    protected fun sendGet(@NotBlank address: String): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val response = restTemplate.getForEntity(address, String::class.java)

        if (HttpStatus.OK != response.statusCode) {
            throw HttpCommunicationException("Send http request to " + address + " failed with code: " + response.statusCodeValue)
        }

        return response.body
    }
}
