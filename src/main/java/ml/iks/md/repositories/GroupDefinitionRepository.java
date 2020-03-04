package ml.iks.md.repositories;

import ml.iks.md.models.GroupDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface GroupDefinitionRepository extends JpaRepository<GroupDefinition, Long> {

    Collection<GroupDefinition> findByProfile(String profile);
}
