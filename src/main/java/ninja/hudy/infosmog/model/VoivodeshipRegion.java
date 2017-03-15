package ninja.hudy.infosmog.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(RegionType.Values.VOIVODESHIP)
public class VoivodeshipRegion extends Region {

    public Set<CountyRegion> getCouties() {
        return getNestedRegions().stream()
                .filter(region -> RegionType.COUNTY
                        .toString()
                        .equals(region.getType()))
                .map(region -> (CountyRegion) region)
                .collect(Collectors.toSet());
    }
}
