package ninja.hudy.infosmog.repository;

import ninja.hudy.infosmog.model.Station;
import ninja.hudy.infosmog.type.StationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByType(StationType type);
    Optional<Station> findByTypeAndProviderCode(StationType type, String providerCode);
}
