package ml.iks.md.repositories;

import ml.iks.md.models.OutMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface OutMessageRepository extends JpaRepository<OutMessage, Long> {

    Optional<OutMessage> findFirstByAddressAndOperatorMessageIdAndGatewayId(String address, String operatorMsgId, String gatewayId);

    Optional<OutMessage> findByMessageId(String messageId);

    Optional<OutMessage> findByOperatorMessageId(String messageId);

    Optional<OutMessage> findByOperatorMessageIdAndGatewayId(String messageId, String gateway);

    Optional<OutMessage> findByOperatorMessageIdAndGatewayIdAndAddressContains(String messageId, String gateway, String address);

    Collection<OutMessage> findBySentStatus(String sentStatus);

}
