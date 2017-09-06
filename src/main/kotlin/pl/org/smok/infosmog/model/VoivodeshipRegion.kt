package pl.org.smok.infosmog.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import java.util.stream.Collectors

@Entity
@DiscriminatorValue(RegionType.Values.VOIVODESHIP)
class VoivodeshipRegion : Region() {

    val couties: Set<CountyRegion>
        get() = nestedRegions.stream()
                .filter { region ->
                    RegionType.COUNTY
                            .toString() == region.type
                }
                .map { region -> region as CountyRegion }
                .collect<Set<CountyRegion>, Any>(Collectors.toSet<CountyRegion>())
}
