package ml.iks.md.repositories;

import ml.iks.md.models.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {

    Optional<DeviceInfo> findByGateway(String gateway);
    void deleteByGateway(String gateway);
}
