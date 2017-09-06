package pl.org.smok.infosmog.model

import pl.org.smok.infosmog.model.RegionType.Values.CITY
import javax.persistence.Entity
import javax.persistence.DiscriminatorValue
import javax.persistence.OneToMany
import java.util.HashSet

@Entity
@DiscriminatorValue(value = CITY)
class CityRegion : Region() {

    @OneToMany(mappedBy = "city")
    var stations: Set<Station> = HashSet()
}
