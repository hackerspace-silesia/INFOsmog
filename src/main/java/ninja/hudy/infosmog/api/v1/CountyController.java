package ninja.hudy.infosmog.api.v1;

import ninja.hudy.infosmog.api.v1.to.*;
import ninja.hudy.infosmog.exception.InfoSmogException;
import ninja.hudy.infosmog.facade.CountyFacade;
import ninja.hudy.infosmog.model.CountyRegion;
import ninja.hudy.infosmog.model.Measurement;
import ninja.hudy.infosmog.model.MeasurementType;
import ninja.hudy.infosmog.model.Station;
import ninja.hudy.infosmog.repository.CountyRepository;
import ninja.hudy.infosmog.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/county")
public class CountyController {

    private static final String ZONE_ID = "Europe/Warsaw";
    private static final Collator POLISH_COLLATOR = Collator.getInstance(new Locale("pl"));

    @Autowired
    private CountyRepository countyRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private CountyFacade countyFacade;

    @RequestMapping(method = RequestMethod.GET)
    Collection<CountyLightTO> findAll() throws InfoSmogException {
        try {
            long oneDayAgo = ZonedDateTime.now().minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond();

            List<CountyLightTO> result = countyFacade.getCountiesWithDataAfter(oneDayAgo)
                    .sorted((c1, c2) -> {

                        int voivode = POLISH_COLLATOR.compare(c1.getSuperName(), c2.getSuperName());

                        if (0 == voivode) {
                            return POLISH_COLLATOR.compare(c1.getName(), c2.getName());
                        }

                        return voivode;
                    })
                    .map(Converter::getCountyLightTO)
                    .collect(Collectors.toList());

            return result;
        } catch (Exception e) {
            throw new InfoSmogException(e);
        }
    }

    @Cacheable(value = "county")
    @RequestMapping(method = RequestMethod.GET, value = "/{countyId}")
    CountyTO get(@PathVariable Long countyId) throws InfoSmogException {
        try {
            CountyRegion county = countyRepository.findOne(countyId);

            if (null == county) {
                throw new InfoSmogException("Unknown county id");
            }

            ZonedDateTime current = ZonedDateTime.now();
            long oneDayAgo = current.minusHours(24).truncatedTo(ChronoUnit.HOURS).toEpochSecond();

            Map<MeasurementType, List<Measurement>> measurementByType = county.getCities()
                    .stream()
                    .filter(city -> !city.getStations().isEmpty())
                    .flatMap(city -> city.getStations().stream())
                    .flatMap(station -> findMeasurements(station, oneDayAgo).stream())
                    .collect(Collectors.groupingBy(Measurement::getType));

            List<MeasurementTypeTO> measurementTypeTOs = new LinkedList<>();

            measurementByType.entrySet().forEach(entry -> {
                Map<Integer, List<Measurement>> measurementByHour = entry.getValue().stream()
                        .collect(Collectors.groupingBy(maa -> ZonedDateTime
                                .ofInstant(Instant.ofEpochSecond(maa.getTimestamp()), ZoneId.of(ZONE_ID))
                                .getHour()));

                List<MeasurementTO> measurementTOs = new LinkedList<>();

                measurementByHour.entrySet().forEach(hour -> {
                    Measurement measurement = getMax(hour.getValue());

                    if (null != measurement) {
                        MeasurementTO to = Converter.getMeasurementTO(measurement, hour.getKey());
                        measurementTOs.add(to);
                    }
                });

                measurementTOs.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

                MeasurementTypeTO type = Converter.getMeasurementTypeTO(entry.getKey(), measurementTOs);
                measurementTypeTOs.add(type);

            });

            return Converter.getCountyTO(county, measurementTypeTOs, current);
        } catch (Exception e) {
            throw new InfoSmogException(e);
        }
    }

    private Measurement getMax(List<Measurement> measurements) {
        Measurement result = null;
        double max = -9999.0;

        for (Measurement measurement : measurements) {
            if (measurement.getValue() > max) {
                result = measurement;
                max = measurement.getValue();
            }
        }

        return result;
    }

    private Set<Measurement> findMeasurements(Station station, long timestamp) {
        return measurementRepository.findByStationIdAndTimestampAfter(station.getId(), timestamp);
    }
}
