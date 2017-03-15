package ninja.hudy.infosmog.facade;

import ninja.hudy.infosmog.model.CountyRegion;
import ninja.hudy.infosmog.model.Measurement;
import ninja.hudy.infosmog.model.RegionType;
import ninja.hudy.infosmog.name.DefaultName;
import ninja.hudy.infosmog.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Stream;

@Component
public class CountyFacade {

    @Autowired
    private MeasurementRepository measurementRepository;


    public Stream<CountyRegion> getCountiesWithDataAfter(Long timestamp) {
        Stream<CountyRegion> result = findMeasurements(timestamp).stream()
                .filter(measurement -> "PM10".equalsIgnoreCase(measurement.getType().getName()) || "NO2".equalsIgnoreCase(measurement.getType().getName()))
                .map(measurement -> measurement.getStation())
                .distinct()
                .map(measurement -> measurement.getCity())
                .distinct()
                .map(region -> region.getSuperRegion())
                .distinct()
                .filter(region -> RegionType.Values.COUNTY.equals(region.getType()) && !DefaultName.UNDEFINED_NAME.getName().equals(region.getName()))
                .map(region -> (CountyRegion) region);

        return result;
    }


    private Set<Measurement> findMeasurements(long timestamp) {
        return measurementRepository.findByTimestampAfter(timestamp);
    }
}
