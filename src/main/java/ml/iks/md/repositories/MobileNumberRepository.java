package ml.iks.md.repositories;

import ml.iks.md.models.Client;
import ml.iks.md.models.MobileNumber;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MobileNumberRepository extends JpaRepository<MobileNumber, Long> {

    List<MobileNumber> findAll();

    List<MobileNumber> findByClientAndProfile(Client client, NumProfile profile);

    List<MobileNumber> findByClientAndProfileAndNumber(Client client, NumProfile profile, String number);

    List<MobileNumber> findByClientAndProfileAndOperatorAndDefaultNumberOrderByIdDesc(Client client, NumProfile profile, Carrier op, boolean isDefault);

    List<MobileNumber> findByClientAndProfileAndDefaultNumberOrderByIdDesc(Client client, NumProfile profile, boolean isDefault);

    List<MobileNumber> findByClientAndPrioriterAndProfileOrderByIdDesc(Client client, int priority, NumProfile profile);

    List<MobileNumber> findByClientId(Long client);

    List<MobileNumber> findByClient(Client client);

    List<MobileNumber> findAllByProfile(NumProfile profile);

    void deleteByClient(Client client);

    Long countMobileNumberByProfile(NumProfile profile);
}
