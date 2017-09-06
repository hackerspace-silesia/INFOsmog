package pl.org.smok.infosmog.reader

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import pl.org.smok.infosmog.exception.HttpCommunicationException
import pl.org.smok.infosmog.facade.CountyFacade
import pl.org.smok.infosmog.model.CountyRegion
import pl.org.smok.infosmog.repository.CountyRepository
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

import java.io.IOException
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.HashMap
import java.util.stream.Collectors

@Controller
class WeatherReader(val countyFacade: CountyFacade,
                    val countyRepository: CountyRepository) : HttpReader() {

    override fun read() {
        try {
            log.info("Start weather updating")
            val oneDayAgo = ZonedDateTime.now().minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond()

            val countyDOs = countyFacade.getCountiesWithDataAfter(oneDayAgo)
                    .filter { countyRegion -> null != countyRegion.weatherApiId }
                    .distinct()

            for (county in countyDOs) {
                updateWeather(county)
                Thread.sleep(1100)
            }

            countyRepository.flush()
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            log.info("Finished weather updating")
        }
    }

    @Throws(HttpCommunicationException::class, IOException::class)
    private fun updateWeather(county: CountyRegion) {
        val response = sendGet(API_URL + "?id=" + county.weatherApiId + "&appid=" + API_ID + "&units=metric")
        val node = MAPPER.readValue<JsonNode>(response, JsonNode::class.java)

        val icon = node.get("weather").get(0).get("icon").asText()
        val temp = node.get("main").get("temp").asDouble()
        val type: Int = WEATHER_MAPPER.getOrDefault(icon, 0)
        county.weatherDegree = temp
        county.weatherType = type

        countyRepository.save(county)
    }

    companion object {
        private val log = Logger.getLogger(WeatherReader::class.java)
        private val API_ID = "df462144dd7e45196f69dfc28667bd00"
        private val API_URL = "http://api.openweathermap.org/data/2.5/weather"
        private val MAPPER = ObjectMapper()

        private val WEATHER_MAPPER: MutableMap<String, Int>

        init {
            WEATHER_MAPPER = HashMap<String, Int>()

            // clear sky
            WEATHER_MAPPER.put("01d", 5)
            WEATHER_MAPPER.put("01n", 5)

            // few clouds
            WEATHER_MAPPER.put("02d", 6)
            WEATHER_MAPPER.put("02n", 6)

            // fog
            WEATHER_MAPPER.put("50d", 6)
            WEATHER_MAPPER.put("50n", 6)

            // scattered clouds
            WEATHER_MAPPER.put("03d", 8)
            WEATHER_MAPPER.put("03n", 8)

            // broken clouds
            WEATHER_MAPPER.put("04d", 3)
            WEATHER_MAPPER.put("04n", 3)

            // shower rain
            WEATHER_MAPPER.put("05d", 1)
            WEATHER_MAPPER.put("05n", 1)

            // rain
            WEATHER_MAPPER.put("06d", 2)
            WEATHER_MAPPER.put("06n", 2)

            // thunderstorm
            WEATHER_MAPPER.put("07d", 4)
            WEATHER_MAPPER.put("07n", 4)

            // snow
            WEATHER_MAPPER.put("08d", 7)
            WEATHER_MAPPER.put("08n", 7)
        }
    }
}
