package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.OutMessage;
import ml.iks.md.repositories.OutMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OutMessageService {

    @Autowired
    OutMessageRepository repository;

    @Autowired
    EntityManager em;

    public List<OutMessage> paginate(Filter<OutMessage> filter) {
        List<OutMessage> commands = new ArrayList<>();
        int page = filter.getFirst()/filter.getPageSize();
        Pageable pageable = PageRequest.of(page, filter.getPageSize(), Sort.by("id").descending());

        if (filter.getParams().isEmpty()) {
            Page<OutMessage> paged = repository.findAll(pageable);
            return paged.getContent();
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<OutMessage> criteriaQuery = criteriaBuilder.createQuery(OutMessage.class);
        Root<OutMessage> root = criteriaQuery.from(OutMessage.class);

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

        TypedQuery<OutMessage> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page);
        typedQuery.setMaxResults(filter.getPageSize());

        return typedQuery.getResultList();

    }

    public List<OutMessage> filterMessage(String txt) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<OutMessage> criteriaQuery = criteriaBuilder.createQuery(OutMessage.class);
        Root<OutMessage> root = criteriaQuery.from(OutMessage.class);

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

    public void remove(OutMessage param) {
        repository.delete(param);
    }

    public long count(Filter<OutMessage> filter) {
        return repository.count();
    }

    public OutMessage findById(Long id) {
        Optional<OutMessage> c = repository.findById(id);
        return c.get();
    }

    public void update(OutMessage param) {
        repository.save(param);
    }

    public void insert(OutMessage param) {
        repository.save(param);
    }
}
