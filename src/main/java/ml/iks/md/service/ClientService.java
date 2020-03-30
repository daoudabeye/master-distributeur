package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.*;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.*;
import ml.iks.md.util.BeanLocator;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.awt.font.NumericShaper;
import java.io.Serializable;
import java.util.*;

@Service
public class ClientService implements Serializable {

    @Autowired
    ClientRepository repository;

    @Autowired
    EntityManager em;

    public List<Client> paginate(Filter<Client> filter, boolean somTotal) {
        List<Client> commands = new ArrayList<>();
        Pageable pageable = null;
        int page = filter.getFirst()/filter.getPageSize();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);

        if (!StringUtils.isEmpty(filter.getSortField())){
            pageable = PageRequest.of(page, filter.getPageSize(),
                    filter.getSortOrder().isAscending()? Sort.by(filter.getSortField()).ascending(): Sort.by(filter.getSortField()).descending());

            criteriaQuery.orderBy(filter.getSortOrder().isAscending()?
                    criteriaBuilder.asc(root.get(filter.getSortField())) : criteriaBuilder.desc(root.get(filter.getSortField())));
        } else {
            pageable = PageRequest.of(page, filter.getPageSize(), Sort.by("id").descending());
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        }

//        if(somTotal)
//            pageable = PageRequest.of(page, filter.getPageSize(), Sort.by("totalClient").descending());
//        else
//            pageable = PageRequest.of(page, filter.getPageSize());

        if (filter.getParams().isEmpty()) {
            Page<Client> paged = repository.findAll(pageable);
            return paged.getContent();
        }

        List<Predicate> predicatesCl = buildClientPredicates(filter, criteriaBuilder, root);
        Predicate[] args = new Predicate[predicatesCl.size()];
        for (int i = 0; i < predicatesCl.size(); i++) {
            args[i] = predicatesCl.get(i);
        }

        criteriaQuery.where(criteriaBuilder.and(args));
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(Payment.class)));
        Long count = em.createQuery(countQuery).getSingleResult();

//        System.out.println(count);

//        criteriaQuery.where(criteriaBuilder.and(args));

        TypedQuery<Client> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page);
        typedQuery.setMaxResults(filter.getPageSize());

        return typedQuery.getResultList();
    }

    private List<Predicate> buildClientPredicates(Filter<Client> filter, CriteriaBuilder criteriaBuilder, Root<Client> root){
        List<Predicate> predicatesClient = new ArrayList<>();

        filter.getParams().forEach((k,v) -> {
            Predicate predicate = null;
            predicate = criteriaBuilder.like(root.get(k), "%" + v + "%");
            predicatesClient.add(predicate);
        });

        return predicatesClient;
    }

    public List<Client> filterClient(String txt) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Client> criteriaQuery = criteriaBuilder.createQuery(Client.class);
        Root<Client> root = criteriaQuery.from(Client.class);

        Predicate predicateAddress = criteriaBuilder.like(root.get("numero"), "%" + txt + "%");
        Predicate predicateContent = criteriaBuilder.like(root.get("nom"), "%" + txt + "%");
        Predicate predicateGateway = criteriaBuilder.like(root.get("address"), "%" + txt + "%");
        Predicate predicateInscripteur = criteriaBuilder.like(root.get("numeroInscripteur"), "%" + txt + "%");

        criteriaQuery.where(
                criteriaBuilder.or(
                        predicateAddress,
                        predicateContent,
                        predicateGateway,
                        predicateInscripteur)
        );

        return em.createQuery(criteriaQuery).getResultList();
    }

    public void remove(Client param) {
        repository.delete(param);
    }

    public long count(Filter<Client> filter) {
        return repository.count();
    }

    public long count() {
        return repository.count();
    }

    public long count(NumProfile profile) {
        return BeanLocator.find(MobileNumberRepository.class).countMobileNumberByProfile(profile);
    }

    public Client findById(Long id) {
        Optional<Client> c = repository.findById(id);
        return c.get();
    }

    public void update(Client param) {
        repository.save(param);
    }

    public void insert(Client param) {
        repository.save(param);
    }

    @Transactional
    public void deleteClient(Client selected) {
        BeanLocator.find(MobileNumberRepository.class).deleteByClient(selected);
        BeanLocator.find(ClientRepository.class).delete(selected);
    }

    public List<Client> findClientByInscriptor(Client selected) {
        if(selected == null)
            return new ArrayList<>();
        return repository.findByNumeroInscripteur(selected.getNumeroInscripteur());
    }
}
