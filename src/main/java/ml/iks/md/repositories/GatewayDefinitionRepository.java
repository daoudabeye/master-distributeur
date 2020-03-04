package ml.iks.md.repositories;

import ml.iks.md.models.GatewayDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface GatewayDefinitionRepository extends JpaRepository<GatewayDefinition, Long> {

    Collection<GatewayDefinition> findByProfile(String profile);

    Collection<GatewayDefinition> findByProfileAndEnabled(String profile, boolean enabled);

    Collection<GatewayDefinition> findByProfileAndEnabledOrderByPriorityDesc(String profile, boolean enabled);

    Optional<GatewayDefinition> findByGatewayId(String gateway);

    void deleteByGatewayId(String gateway);

    GatewayDefinition findBySenderId(String senderId);
}
