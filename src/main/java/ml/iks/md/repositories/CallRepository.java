package ml.iks.md.repositories;

import ml.iks.md.models.Call;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallRepository extends JpaRepository<Call, Long> {
}
