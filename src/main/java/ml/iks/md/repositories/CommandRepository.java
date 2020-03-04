package ml.iks.md.repositories;

import ml.iks.md.models.Command;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.CmdMode;
import ml.iks.md.models.data.CmdType;
import ml.iks.md.models.data.NumProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findByProfileAndModeExecution(NumProfile simGroup, CmdMode mode);

    Optional<Command> findFirstByCarrierAndProfileAndType(Carrier network, NumProfile profileAgent, CmdType commandType);
}
