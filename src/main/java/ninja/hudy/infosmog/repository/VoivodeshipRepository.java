package ninja.hudy.infosmog.repository;

import ninja.hudy.infosmog.model.VoivodeshipRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface VoivodeshipRepository extends JpaRepository<VoivodeshipRegion, Long> {

    Set<VoivodeshipRegion> findByName(String name);

    Optional<VoivodeshipRegion> findByCode(Integer code);
}
