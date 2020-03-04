package ml.iks.md.repositories;

import ml.iks.md.models.SimGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimGroupRepository extends JpaRepository<SimGroup, Long> {

    Optional<SimGroup> findByName(String name);
}
