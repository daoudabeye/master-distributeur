package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.MessagePattern;
import ml.iks.md.repositories.MessagePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MsgPatternService {
    @Autowired
    MessagePatternRepository repository;

    public List<MessagePattern> paginate(Filter<MessagePattern> filter) {
        List<MessagePattern> commands = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());


        if (filter.getParams().isEmpty()) {
            Page<MessagePattern> page = repository.findAll(pageable);
            commands = page.getContent();
        }

        return commands;
    }

    public void remove(MessagePattern command) {
        repository.delete(command);
    }

    public long count(Filter<MessagePattern> filter) {
        return repository.count();
    }

    public MessagePattern findById(Long id) {
        Optional<MessagePattern> c = repository.findById(id);
        return c.get();
    }

    public void update(MessagePattern command) {
        repository.save(command);
    }

    public void insert(MessagePattern command) {
        repository.save(command);
    }
}
