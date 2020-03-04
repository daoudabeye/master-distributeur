package ml.iks.md.repositories;

import ml.iks.md.models.InMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface InMessageRepository extends JpaRepository<InMessage, Long> {

    Page<InMessage> findByTextContains(String key, Pageable pageable);

    @Query(value = "SELECT m FROM in_message m where Date(m.receiveDate) = ?1", nativeQuery = true)
    Page<InMessage> findByReceiveDate(Date from, Pageable pageable);

    @Query(value = "SELECT * FROM in_message where Date(receive_date) = ?2 AND text LIKE ?1", nativeQuery = true)
    Page<InMessage> findByTextAndReceiveDate(String key, Date from, Pageable pageable);

//    @Query(value = "SELECT * FROM in_message where address LIKE ?1 AND lus = ?2 AND Date(receive_date) = Date(?3) order by id desc ", nativeQuery = true)
//    List<InMessage> findByAddressAndReceiveDate(String address, boolean lus, Date date);

    @Query(value = "SELECT * FROM in_message where address LIKE ?1 AND lus = ?2 AND receive_date between DATE_SUB(?3, INTERVAL 5 MINUTE) and ?3 order by id desc ", nativeQuery = true)
    List<InMessage> findByAddressAndReceiveDate(String address, boolean lus, Date date);
}
