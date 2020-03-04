package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.InMessage;
import ml.iks.md.models.Payment;
import ml.iks.md.models.Sim;
import ml.iks.md.repositories.InMessageRepository;
import ml.iks.md.repositories.SimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InMessageService {

    @Autowired
    InMessageRepository repository;

    @Autowired
    EntityManager em;

    public List<InMessage> paginate(Filter<InMessage> filter) {
        List<InMessage> commands = new ArrayList<>();
        Pageable pageable = PageRequest.of(filter.getFirst()/filter.getPageSize(), filter.getPageSize(), Sort.by("id").descending());

        if (filter.getParams().isEmpty()) {
            Page<InMessage> page = repository.findAll(pageable);
            return page.getContent();
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InMessage> criteriaQuery = criteriaBuilder.createQuery(InMessage.class);
        Root<InMessage> root = criteriaQuery.from(InMessage.class);

        List<Predicate> predicates = new ArrayList<>();
        filter.getParams().forEach((k,v) -> {
            Predicate predicate =  criteriaBuilder.like(root.get(k), "%" + v + "%");
            predicates.add(predicate);
        });

        Predicate[] args = new Predicate[predicates.size()];
        for (int i = 0; i < predicates.size(); i++) {
            args[i] = predicates.get(i);
        }

        criteriaQuery.where(criteriaBuilder.or(args));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        return em.createQuery(criteriaQuery).getResultList();

    }

    public List<InMessage> filterMessage(String txt) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<InMessage> criteriaQuery = criteriaBuilder.createQuery(InMessage.class);
        Root<InMessage> root = criteriaQuery.from(InMessage.class);

        Predicate predicateAddress = criteriaBuilder.like(root.get("address"), "%" + txt + "%");
        Predicate predicateContent = criteriaBuilder.like(root.get("text"), "%" + txt + "%");
        Predicate predicateGateway = criteriaBuilder.like(root.get("gatewayId"), "%" + txt + "%");

        criteriaQuery.where(
                criteriaBuilder.or(
                        predicateAddress,
                        predicateContent,
                        predicateGateway)
        );
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        return em.createQuery(criteriaQuery).getResultList();
    }

    public void remove(InMessage param) {
        repository.delete(param);
    }

    public long count(Filter<InMessage> filter) {
        return repository.count();
    }

    public InMessage findById(Long id) {
        Optional<InMessage> c = repository.findById(id);
        return c.get();
    }

    public void update(InMessage param) {
        repository.save(param);
    }

    public void insert(InMessage param) {
        repository.save(param);
    }
}
