package ml.iks.md.repositories;

import ml.iks.md.models.Payment;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.ikslib.gateway.message.OutboundMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAll();

    List<Payment> findByRecipientAndAmountAndGatewayAndConfirmedOrderByIdAsc(String payer, double amount, String gateway, boolean confirmed);

    List<Payment> findByPayerAndStatusOrderByDateCreationDesc(String payer, OutboundMessage.SentStatus status);

    @Query("SELECT p.carrier, count(p) as val FROM Payment p group by p.carrier")
    List<Object[]> countByCarrier();

    @Query("SELECT sum(p.amount) as total FROM Payment p")
    List<Object[]> sumCash();

    @Query(value = "SELECT count(*) as nbr, date(date_creation) as jour FROM payment where date(date_creation)=?1",
    nativeQuery = true)
    List<Object[]> findByDateCreation(Date dateCreation);

    @Query(value = "SELECT sum(amount), count(*) FROM payment where payer=?1",
            nativeQuery = true)
    List<Object[]> sumPayment(String payer);

    @Query(value = "SELECT sum(amount), count(*) FROM payment where payer=?1 and operateur=?2",
            nativeQuery = true)
    List<Object[]> sumPayment(String payer, String operateur);

    @Query(value = "SELECT sum(amount), count(*) FROM payment where payer=?1 and operateur=?2  and date_creation between ?3 and ?4", nativeQuery = true)
    List<Object[]> sumPayment(String payer, String operateur, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT sum(amount) FROM payment where nom=?1 and date_creation between ?2 and ?3",
            nativeQuery = true)
    Long sumPayment(String type, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT count(*) FROM payment where nom=?1 and date_creation between ?2 and ?3",
            nativeQuery = true)
    Long count(String type, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT count(*) FROM payment where status like ?1 and date_creation between ?2 and ?3",
            nativeQuery = true)
    Long count(OutboundMessage.SentStatus status, LocalDateTime from, LocalDateTime to);

    @Query(value = "SELECT sum(amount) FROM payment where nom like ?1 and agent_network like ?2 and recipient_network like ?3 and recipient_profile like ?4 and date_creation between ?5 and ?6", nativeQuery = true)
    Long sumPayment(String type, String agentNetwork, String recipientNetwork, String recipientProfile, LocalDateTime from, LocalDateTime to);
}
