package ninja.hudy.infosmog.repository;

import ninja.hudy.infosmog.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {

    Optional<MeasurementType> findByName(String name);
}
