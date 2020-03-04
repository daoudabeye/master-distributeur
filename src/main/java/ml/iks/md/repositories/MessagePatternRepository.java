package ml.iks.md.repositories;

import ml.iks.md.models.MessagePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessagePatternRepository extends JpaRepository<MessagePattern, Long> {
//    List<MessagePattern> findAllBySimGroup(SimGroup simGroup);

    @Query("SELECT p FROM MessagePattern p order by p.priority desc")
    List<MessagePattern> findAll();
}
