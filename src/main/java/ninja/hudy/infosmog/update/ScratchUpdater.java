package ninja.hudy.infosmog.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.hudy.infosmog.exception.UpdateException;
import ninja.hudy.infosmog.model.*;
import ninja.hudy.infosmog.name.DefaultName;
import ninja.hudy.infosmog.repository.*;
import ninja.hudy.infosmog.type.StationType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

@Component
public class ScratchUpdater {
    private static Logger log = Logger.getLogger(ScratchUpdater.class);

    @Autowired
    private VoivodeshipRepository voivodeshipRepository;

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private MeasurementTypeRepository measurementTypeRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Transactional
    public void createFromScratch() throws UpdateException {
        log.info("Load configuration from scratch");
        createUndefinedRegions();
        createVoivodeships();
        createCounties();
        createCities();
        setCapitalCities();
        createMeasurementTypes();
    }

    private void createVoivodeships() throws UpdateException {
        log.info("Importing voivodeships");
        try {
            String path = "/configuration/voivodeships.json";

            InputStream in = this.getClass().getResourceAsStream(path);

            JsonNode regions = MAPPER.readValue(in, JsonNode.class).get("regions");

            regions.forEach(node -> {
                VoivodeshipRegion region = new VoivodeshipRegion();
                region.setName(node.get("name").asText());
                region.setCode(node.get("code").asInt());

                if (node.has("weather_id")) {
                    region.setWeatherApiId(node.get("weather_id").asLong());
                }

                voivodeshipRepository.save(region);
            });

            voivodeshipRepository.flush();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void createCounties() throws UpdateException {
        log.info("Importing counties");
        try {
            String path = "/configuration/counties.json";

            InputStream in = this.getClass().getResourceAsStream(path);

            JsonNode regions = MAPPER.readValue(in, JsonNode.class).get("regions");

            regions.forEach(node -> {
                int code = node.get("code").asInt();
                int superCode = getSuperCode(code);
                Optional<VoivodeshipRegion> voivodeship = voivodeshipRepository.findByCode(superCode);

                if (voivodeship.isPresent()) {
                    CountyRegion region = new CountyRegion();
                    region.setName(node.get("name").asText());
                    region.setCode(node.get("code").asInt());

                    if (node.has("weather_id")) {
                        region.setWeatherApiId(node.get("weather_id").asLong());
                    }

                    region.setSuperRegion(voivodeship.get());

                    countyRepository.save(region);
                }
            });

            countyRepository.flush();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void createCities() throws UpdateException {
        log.info("Importing cities");
        try {
            String path = "/configuration/cities.json";

            InputStream in = this.getClass().getResourceAsStream(path);

            JsonNode regions = MAPPER.readValue(in, JsonNode.class).get("regions");

            regions.forEach(node -> {
                int code = node.get("code").asInt();
                int superCode = getSuperCode(code);
                Optional<CountyRegion> county = countyRepository.findByCode(superCode);

                if (county.isPresent()) {
                    CityRegion region = new CityRegion();
                    region.setName(node.get("name").asText());
                    region.setCode(node.get("code").asInt());

                    if (node.has("weather_id")) {
                        region.setWeatherApiId(node.get("weather_id").asLong());
                    }

                    region.setSuperRegion(county.get());

                    cityRepository.save(region);
                }
            });

            cityRepository.flush();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void setCapitalCities() {
        log.info("Assigning capital cities");
        //placeholder
    }

    public void assignKnownStations() throws UpdateException {
        log.info("Assigning known stations");
        try {
            String path = "/configuration/stations.json";

            InputStream in = this.getClass().getResourceAsStream(path);

            JsonNode stations = MAPPER.readValue(in, JsonNode.class).get("stations");

            stations.forEach(node -> {
                int stationId = node.get("stationId").asInt();
                int regionCode = node.get("regionCode").asInt();
                String stationName = node.get("stationName").asText();

                String cityName = stationName.split(",")[0].trim();

                Set<CityRegion> cities = cityRepository.findByName(cityName);

                if (cities.isEmpty()) {
                    log.warn("No city for     :" + stationName);
                } else if (cities.size() > 1) {
                    log.warn("More cities for :" + stationName);
                } else {
                    CityRegion city = cities.stream().findAny().get();
                    Optional<Station> stationDO = stationRepository.findByTypeAndProviderCode(StationType.GIOS, stationId + "");

                    if (stationDO.isPresent()) {
                        Station station = stationDO.get();
                        station.setCity(city);

                        stationRepository.save(station);
                    } else {
                        log.warn("Missing station " + stationName + " and id " + stationId);
                    }

                }
            });

            stationRepository.flush();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void createUndefinedRegions() throws UpdateException {
        log.info("Creating undefined regions");
        try {
            String defaultName = DefaultName.UNDEFINED_NAME.getName();

            Set<VoivodeshipRegion> voivodeships = voivodeshipRepository.findByName(defaultName);
            VoivodeshipRegion voivodeship;

            if (voivodeships.isEmpty()) {
                voivodeship = new VoivodeshipRegion();
                voivodeship.setName(defaultName);
                voivodeship.setCode(-1);
                voivodeshipRepository.saveAndFlush(voivodeship);
            } else {
                voivodeship = voivodeships.stream().findAny().get();
            }

            Set<CountyRegion> counties = countyRepository.findByName(defaultName);
            CountyRegion county;

            if (counties.isEmpty()) {
                county = new CountyRegion();
                county.setName(defaultName);
                county.setCode(-1);
                county.setSuperRegion(voivodeship);
                countyRepository.saveAndFlush(county);
            } else {
                county = counties.stream().findAny().get();
            }

            Set<CityRegion> cities = cityRepository.findByName(defaultName);

            if (cities.isEmpty()) {
                CityRegion city = new CityRegion();
                city.setName(defaultName);
                city.setCode(-1);
                city.setSuperRegion(county);
                cityRepository.saveAndFlush(city);
            }

            countyRepository.saveAndFlush(county);
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private void createMeasurementTypes() throws UpdateException {
        log.info("Creating measurement types");
        try {
            String path = "/configuration/types.json";

            InputStream in = this.getClass().getResourceAsStream(path);

            JsonNode types = MAPPER.readValue(in, JsonNode.class).get("types");

            types.forEach(node -> {
                String name = node.get("name").asText();
                String displayName = node.get("displayName").asText();
                int acceptable = node.get("acceptable").asInt();
                int informative = node.get("informative").asInt();
                int alarm = node.get("alarm").asInt();

                MeasurementType measurementType = new MeasurementType();
                measurementType.setName(name);
                measurementType.setDisplayName(displayName);
                measurementType.setAcceptable(acceptable);
                measurementType.setInformative(informative);
                measurementType.setAlarm(alarm);

                measurementTypeRepository.save(measurementType);
            });

            measurementTypeRepository.flush();
        } catch (Exception e) {
            throw new UpdateException(e);
        }
    }

    private int getSuperCode(int code) {
        return code / 100;
    }
}
