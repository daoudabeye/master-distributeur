package ml.iks.md.repositories;


import ml.iks.md.models.Client;
import ml.iks.md.models.data.NumProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByNumero(String numero);

    @Query(value = "SELECT c FROM Client c WHERE c.id in (SELECT m.client.id FROM MobileNumber m where m.profile = ?1 group by m.client.id)")
    List<Client> findByProfile(NumProfile profile);

    @Query(value = "SELECT * FROM client where client.nom like ?1 or numero like ?1 " +
            "or client.id in (SELECT client_id FROM mobile_number where mobile_number.number like ?1)", nativeQuery = true)
    Page<Client> findByText(String txt, Pageable pageable);

    @Query(value = "SELECT * FROM client where client.nom like ?1 or numero like ?1 " +
            "or client.id in (SELECT client_id FROM mobile_number where mobile_number.number like ?1) " +
            "and date_inscription BETWEEN ?2 and ?3", nativeQuery = true)
    Page<Client> findByTextAndDateInscription(String txt, Date from, Date to, Pageable pageable);

    Page<Client> findByDateInscriptionBetween(Date from, Date to, Pageable pageable);

    @Query("SELECT c FROM Client c group by c.numeroInscripteur")
    Page<Client> groupByInscriptor(Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.dateInscription BETWEEN ?1 and ?2 group by c.numeroInscripteur")
    Page<Client> groupByInscBetween(Date from, Date to, Pageable pageable);

    @Query(value = "SELECT c1.id as id, c1.nom, c1.numero, count(*) as incris, " +
            "(SELECT sum(p.amount) FROM payment p where p.payer in (select c2.numero from client c2 where c2.numero_inscripteur=c1.numero and c2.date_inscription between ?1 and ?2) and p.status like ?3) as cumul, " +
            "c1.date_inscription FROM client c1 group by numero_inscripteur order by cumul desc",
            nativeQuery = true)
    Page<Object[]> countInscription(Date from, Date to, String sentStatus, Pageable pageable);

    @Query(value = "SELECT count(*) as nbr, month(c.date_inscription) as mois FROM client c where month(c.date_inscription)=month(?1) and numero_inscripteur=?2",
            nativeQuery = true)
    List<Object[]> countByMonth(Date date, String inscripteur);

    @Query(value = "SELECT count(*) FROM mobile_number where mobile_number.client_id in (select client.id from client where client.numero_inscripteur like ?1)" +
            " and profile like ?2 group by profile",
            nativeQuery = true)
    List<Object[]> countProfileByInscriptor(String inscriptor, String profile);

    @Query(value = "SELECT count(*) FROM mobile_number where mobile_number.client_id in (select client.id from client where client.numero_inscripteur like ?1)" +
            " and profile like ?2 and operator like ?3 group by profile",
            nativeQuery = true)
    List<Object[]> countProfileByInscriptorAndCarrier(String inscriptor, String profile, String operator);

    List<Client> findByNumeroInscripteur(String inscripteur);

}
