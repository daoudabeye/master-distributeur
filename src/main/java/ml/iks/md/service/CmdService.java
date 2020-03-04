package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.infra.model.SortOrder;
import ml.iks.md.models.Command;
import ml.iks.md.repositories.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.adminfaces.template.util.Assert.has;

@Service
public class CmdService implements Serializable {

    @Autowired
    CommandRepository repository;

    public List<Command> paginate(Filter<Command> filter) {
        List<Command> commands = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());
        if(has(filter.getSortOrder()) && !SortOrder.UNSORTED.equals(filter.getSortOrder())) {
            if (filter.getSortOrder().isAscending()) {
                pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());
            } else {
                pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());
            }
        }

        if (filter.getParams().isEmpty()) {
            Page<Command> page = repository.findAll(pageable);
            commands = page.getContent();
        }

        return commands;
    }

    public void remove(Command command) {
        repository.delete(command);
    }

    public long count(Filter<Command> filter) {
        return repository.count();
    }

    public Command findById(Long id) {
        Optional<Command> c = repository.findById(id);
        return c.get();
    }

    public void update(Command command) {
        repository.save(command);
    }

    public void insert(Command command) {
        repository.save(command);
    }
}
