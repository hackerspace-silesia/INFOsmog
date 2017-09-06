package pl.org.smok.infosmog.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import java.util.stream.Collectors

@Entity
@DiscriminatorValue(RegionType.Values.COUNTY)
class CountyRegion : Region() {

    val cities: Set<CityRegion>
        get() = nestedRegions
                .filter { RegionType.CITY.toString() == it.type }
                .map { it as CityRegion }
                .distinct()
                .toSet()

}
