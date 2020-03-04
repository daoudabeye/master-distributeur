package ml.iks.md.repositories;

import ml.iks.md.models.Commission;
import ml.iks.md.models.data.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Optional<Commission> findFirstByCarrier(Carrier carrier);


}
