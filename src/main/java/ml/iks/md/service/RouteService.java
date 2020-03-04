package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.infra.model.SortOrder;
import ml.iks.md.models.Command;
import ml.iks.md.models.RoutingTable;
import ml.iks.md.repositories.CommandRepository;
import ml.iks.md.repositories.RoutingTableRepository;
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
public class RouteService implements Serializable {
    @Autowired
    RoutingTableRepository repository;

    public List<RoutingTable> paginate(Filter<RoutingTable> filter) {
        List<RoutingTable> routes = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize());

        if (filter.getParams().isEmpty()) {
            Page<RoutingTable> page = repository.findAll(pageable);
            routes = page.getContent();
        }

        return routes;
    }

    public void remove(RoutingTable route) {
        repository.delete(route);
    }

    public long count(Filter<RoutingTable> filter) {
        return repository.count();
    }

    public RoutingTable findById(Long id) {
        Optional<RoutingTable> c = repository.findById(id);
        return c.get();
    }

    public void update(RoutingTable route) {
        repository.save(route);
    }

    public void insert(RoutingTable route) {
        repository.save(route);
    }
}
