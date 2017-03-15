package ninja.hudy.infosmog.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.hudy.infosmog.exception.HttpCommunicationException;
import ninja.hudy.infosmog.exception.InfoSmogException;
import ninja.hudy.infosmog.model.CityRegion;
import ninja.hudy.infosmog.model.Measurement;
import ninja.hudy.infosmog.model.MeasurementType;
import ninja.hudy.infosmog.model.Station;
import ninja.hudy.infosmog.name.DefaultName;
import ninja.hudy.infosmog.type.StationType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GIOSReader extends HttpReader {
    private static Logger log = Logger.getLogger(GIOSReader.class);
    private static final String API_URL = "http://powietrze.gios.gov.pl/pjp/current";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final StationType STATION_TYPE = StationType.GIOS;
    private static final Integer DAYS = 2;

    private class StationCurrent {
        private String name;
        private Map<String, Double> measurement = new HashMap<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Double> getMeasurement() {
            return measurement;
        }

        public void addMeasurement(String type, Double value) {
            measurement.put(type, value);
        }
    }

    @Override
    public void read() {
        try {
            log.info("Start GIOS updating");
            long currentTimestamp = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).toEpochSecond();
            Map<Integer, StationCurrent> stations = getStations();
            createMissingStations(stations);
            getMeasurement(stations, currentTimestamp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("Finished GIOS updating");
        }
    }

    private void getMeasurement(Map<Integer, StationCurrent> stations, long currentTimestamp) throws HttpCommunicationException, IOException {
        stations.entrySet().stream().parallel().forEach(entry -> {
            Integer id = entry.getKey();
            try {
                String response = sendGet(API_URL + "/get_data_chart?days="+DAYS.toString()+"&stationId="+id.toString());
                JsonNode rootNode = MAPPER.readValue(response, JsonNode.class);
                JsonNode chartElements = rootNode.get("chartElements");
                Map<String, Map<Long, Double>> measurement = new HashMap<>();

                chartElements.forEach(element -> {
                    String type = element.get("key").asText();
                    JsonNode values = element.get("values");

                    Map<Long, Double> val = new HashMap<>();
                    if (StringUtils.hasText(type) && values.size() > 0) {
                        values.forEach(v -> {
                            Long millis = v.get(0).asLong() / 1000;
                            Double value = null;

                            if (!v.get(1).isNull()) {
                                value = v.get(1).asDouble();
                            } else if (millis >= currentTimestamp) {
                                value = entry.getValue().getMeasurement().get(type);
                            }

                            if (null != value) {
                                val.put(millis, value);
                            }
                        });
                    }

                    if (!val.isEmpty()) {
                        measurement.put(type, val);
                    }
                });

                createMissingTypes(measurement.keySet());
                addMeasurement(measurement, id.toString());
            } catch (Exception e) {
                log.error("Cannot get measurement for " + id, e);
            }
        });
    }

    private void addMeasurement(Map<String, Map<Long, Double>> measurementByType, String providerCode) {
        Station station = stationRepository.findByTypeAndProviderCode(STATION_TYPE, providerCode).get();
        List<MeasurementType> measurementTypes = measurementTypeRepository.findAll();

        for (Map.Entry<String, Map<Long, Double>> entry : measurementByType.entrySet()) {
            Optional<MeasurementType> typeDO = measurementTypes.stream().filter(t -> t.getName().equalsIgnoreCase(entry.getKey())).findAny();

            if (typeDO.isPresent()) {
                MeasurementType type = typeDO.get();
                for (Map.Entry<Long, Double> m : entry.getValue().entrySet()) {
                    Long timestamp = m.getKey();
                    Optional<Measurement> measurement = measurementRepository.findByStationIdAndTimestampAndTypeId(station.getId(), timestamp, type.getId());

                    if (!measurement.isPresent()) {
                        Measurement newOne = new Measurement();
                        newOne.setTimestamp(timestamp);
                        newOne.setStation(station);
                        newOne.setType(type);
                        newOne.setValue(m.getValue());
                        measurementRepository.save(newOne);
                    } else if (measurement.get().getValue() == null || !measurement.get().getValue().equals(m.getValue())) {
                        Measurement update = measurement.get();
                        update.setValue(m.getValue());

                        measurementRepository.save(update);
                    }
                }
            }
        }

        measurementRepository.flush();
    }

    private void createMissingTypes(Set<String> types) {
        List<MeasurementType> typeDOs = measurementTypeRepository.findAll();
        long count = 0;

        for (String type : types) {
            Optional<MeasurementType> typeDO = typeDOs.stream()
                    .filter(t -> t.getName()
                            .equalsIgnoreCase(type))
                    .findAny();

            if (!typeDO.isPresent()) {
                MeasurementType newType = new MeasurementType();

                newType.setName(type.toUpperCase());
                newType.setDisplayName(type);
                newType.setAcceptable(50);
                newType.setInformative(200);
                newType.setAlarm(300);

                measurementTypeRepository.save(newType);

                ++count;
            }
        }

        if (count > 0) {
            measurementTypeRepository.flush();
            log.info("Added " + count + " new types");
        }
    }

    private Map<Integer, StationCurrent> getStations() throws HttpCommunicationException, IOException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("param", "AQI");

        String response = sendGet(API_URL + "/getAQIDetailsList?param=AQI");

        JsonNode rootNode = MAPPER.readValue(response, JsonNode.class);

        Map<Integer, StationCurrent> stations = new HashMap<>();

        rootNode.forEach(node -> {
            int id = node.get("stationId").asInt();
            String name = node.get("stationName").asText();

            if (StringUtils.hasText(name) && !stations.containsKey(id)) {
                StationCurrent current = new StationCurrent();
                current.setName(name);

                JsonNode values = node.get("values");
                if (!values.isNull()) {
                    Iterator<String> iterator = values.fieldNames();
                    while (iterator.hasNext()) {
                        String type = iterator.next();
                        Double value = values.get(type).asDouble();
                        current.addMeasurement(type, value);
                    }
                }

                stations.put(id, current);
            }
        });

        return stations;
    }

    private void createMissingStations(Map<Integer, StationCurrent> stations) throws InfoSmogException {
        Set<CityRegion> undefinedCities = cityRepository.findByName(DefaultName.UNDEFINED_NAME.getName());

        if (undefinedCities.isEmpty()) {
            throw new InfoSmogException("Missing undefined city!");
        }

        Optional<CityRegion> undefinedCity = undefinedCities.stream().findAny();

        Set<String> existingIds = stationRepository.findByType(STATION_TYPE)
                .stream()
                .map(Station::getProviderCode)
                .collect(Collectors.toSet());

        long count = 0;

        for (Map.Entry<Integer, StationCurrent> entry : stations.entrySet()) {
            Integer id = entry.getKey();
            StationCurrent current = entry.getValue();

            if (!existingIds.contains(id.toString())) {
                Station station = new Station();
                station.setName(current.getName());

                String cityName = station.getName().split(",")[0].trim();
                Set<CityRegion> cities = cityRepository.findByName(cityName);
                CityRegion city = undefinedCity.get();

                if (cities.size() == 1) {
                    city = cities.stream().findAny().get();
                }

                station.setCity(city);
                station.setType(STATION_TYPE);
                station.setProviderCode(id.toString());

                stationRepository.save(station);
                ++count;
            }
        }

        if (count > 0) {
            stationRepository.flush();
            log.info("Added " + count + " new stations");
        }
    }
}
