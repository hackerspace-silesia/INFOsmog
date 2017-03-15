package ninja.hudy.infosmog.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.hudy.infosmog.exception.HttpCommunicationException;
import ninja.hudy.infosmog.facade.CountyFacade;
import ninja.hudy.infosmog.model.CountyRegion;
import ninja.hudy.infosmog.repository.CountyRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WeatherReader extends HttpReader {
    private static Logger log = Logger.getLogger(WeatherReader.class);
    private static final String API_ID = "df462144dd7e45196f69dfc28667bd00";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private CountyFacade countyFacade;

    @Autowired
    private CountyRepository countyRepository;

    private static final Map<String, Integer> WEATHER_MAPPER;

    static {
        WEATHER_MAPPER = new HashMap<>();

        // clear sky
        WEATHER_MAPPER.put("01d", 5);
        WEATHER_MAPPER.put("01n", 5);

        // few clouds
        WEATHER_MAPPER.put("02d", 6);
        WEATHER_MAPPER.put("02n", 6);

        // fog
        WEATHER_MAPPER.put("50d", 6);
        WEATHER_MAPPER.put("50n", 6);

        // scattered clouds
        WEATHER_MAPPER.put("03d", 8);
        WEATHER_MAPPER.put("03n", 8);

        // broken clouds
        WEATHER_MAPPER.put("04d", 3);
        WEATHER_MAPPER.put("04n", 3);

        // shower rain
        WEATHER_MAPPER.put("05d", 1);
        WEATHER_MAPPER.put("05n", 1);

        // rain
        WEATHER_MAPPER.put("06d", 2);
        WEATHER_MAPPER.put("06n", 2);

        // thunderstorm
        WEATHER_MAPPER.put("07d", 4);
        WEATHER_MAPPER.put("07n", 4);

        // snow
        WEATHER_MAPPER.put("08d", 7);
        WEATHER_MAPPER.put("08n", 7);
    }

    @Override
    public void read() {
        try {
            log.info("Start weather updating");
            long oneDayAgo = ZonedDateTime.now().minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond();

            List<CountyRegion> countyDOs = countyFacade.getCountiesWithDataAfter(oneDayAgo)
                    .filter(countyRegion -> null != countyRegion.getWeatherApiId())
                    .distinct()
                    .collect(Collectors.toList());

            for (CountyRegion county : countyDOs) {
                updateWeather(county);
                Thread.sleep(1100);
            }

            countyRepository.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("Finished weather updating");
        }
    }

    private void updateWeather(CountyRegion county) throws HttpCommunicationException, IOException {
        String response = sendGet(API_URL + "?id=" + county.getWeatherApiId() + "&appid=" + API_ID + "&units=metric");
        JsonNode node = MAPPER.readValue(response, JsonNode.class);

        String icon = node.get("weather").get(0).get("icon").asText();
        Double temp = node.get("main").get("temp").asDouble();
        Integer type = 0;
        if(WEATHER_MAPPER.containsKey(icon)) {
            type = WEATHER_MAPPER.get(icon);
        }
        county.setWeatherDegree(temp);
        county.setWeatherType(type);

        countyRepository.save(county);
    }
}
