package ninja.hudy.infosmog.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(RegionType.Values.COUNTY)
public class CountyRegion extends Region {

    public Set<CityRegion> getCities() {
        return getNestedRegions().stream()
                .filter(region -> RegionType.CITY
                        .toString()
                        .equals(region.getType()))
                .map(region -> (CityRegion) region)
                .collect(Collectors.toSet());
    }

}
