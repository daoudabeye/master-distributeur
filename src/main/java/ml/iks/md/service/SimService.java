package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.GatewayDefinition;
import ml.iks.md.models.Sim;
import ml.iks.md.repositories.GatewayDefinitionRepository;
import ml.iks.md.repositories.SimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SimService implements Serializable {

    @Autowired
    SimRepository repository;

    public List<Sim> paginate(Filter<Sim> filter) {
        List<Sim> commands = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());
        if (filter.getParams().isEmpty()) {
            Page<Sim> page = repository.findAll(pageable);
            commands = page.getContent();
        }

        return commands;
    }

    public void remove(Sim command) {
        repository.delete(command);
    }

    public long count(Filter<Sim> filter) {
        return repository.count();
    }

    public Sim findById(Long id) {
        Optional<Sim> c = repository.findById(id);
        return c.get();
    }

    public void update(Sim command) {
        repository.save(command);
    }

    public void insert(Sim command) {
        repository.save(command);
    }
}
