package ml.iks.md.repositories;

import ml.iks.md.models.Sim;
import ml.iks.md.models.SimGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface SimRepository extends JpaRepository<Sim, Long> {
    Collection<Sim> findByGroup(SimGroup g);
    Collection<Sim> findByGroupName(String group);
    Optional<Sim> findByGateway(String gateway);
}
