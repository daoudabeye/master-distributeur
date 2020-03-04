package ml.iks.md.repositories;

import ml.iks.md.models.NumberRouteDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NumberRouteDefinitionRepository extends JpaRepository<NumberRouteDefinition, Long> {

    Collection<NumberRouteDefinition> findByProfile(String profile);
}
