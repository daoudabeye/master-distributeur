package ml.iks.md.repositories;

import ml.iks.md.models.RoutingTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface RoutingTableRepository extends JpaRepository<RoutingTable, Long> {
    Optional<RoutingTable> findByAddressRegEx(String regex);

    Collection<RoutingTable> findByProfile(String profile);
}
