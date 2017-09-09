package ninja.hudy.infosmog.repository;

import ninja.hudy.infosmog.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SensorRepository extends JpaRepository<SensorData, Long> {

    @Query(value = "SELECT * FROM sensor_data AS SD ORDER BY SD.creation_date_time DESC", nativeQuery = true)
    Set<SensorData> findSensorDataOrderByCreationDateTime();
}
