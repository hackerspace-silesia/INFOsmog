package ninja.hudy.infosmog.api.v1;

import ninja.hudy.infosmog.api.v1.to.SensorDataTo;
import ninja.hudy.infosmog.constants.JSONResponseCode;
import ninja.hudy.infosmog.model.SensorData;
import ninja.hudy.infosmog.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;

@RestController
@RequestMapping("/api/v1/sensor")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody Set<SensorData> findAllSensorData() {
        long oneDayAgo = ZonedDateTime.now().minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond();
        Set<SensorData> sensorDataOrderByCreationDateTime = sensorRepository.findSensorDataOrderByCreationDateTime();
        return sensorDataOrderByCreationDateTime.isEmpty() ? Collections.EMPTY_SET : sensorDataOrderByCreationDateTime;
    }

    @PostMapping("/save")
    public @ResponseBody
    String sensorDataSave(@RequestBody SensorDataTo sensorJSON) {
        SensorData sensorData = new SensorData();
        if (sensorJSON != null) {
            sensorData.setTemperature(sensorJSON.getTemperature());
            sensorData.setLevelOfPollution(sensorJSON.getLevelOfPollution());
            sensorData.setPressure(sensorJSON.getPressure());
            sensorData.setCreationDateTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            sensorRepository.save(sensorData);
            sensorRepository.flush();
            return JSONResponseCode.SENSOR_OK_STATUS;
        } else {
            return JSONResponseCode.SENSOR_FAIL_STATUS;
        }
    }
}
