package ml.iks.md.service;

import ml.iks.md.infra.model.Filter;
import ml.iks.md.models.Payment;
import ml.iks.md.models.data.Carrier;
import ml.iks.md.models.data.NumProfile;
import ml.iks.md.repositories.PaymentRepository;
import ml.iks.md.util.AppUtil;
import ml.ikslib.gateway.message.OutboundMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PaymentService implements Serializable {

    @Autowired
    PaymentRepository repository;

    @Autowired
    EntityManager em;

    private long count = 0L;
    public List<Payment> paginate(Filter<Payment> filter) {
        List<Payment> payments = new ArrayList<>();
        Pageable pageable = null;

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
        Root<Payment> root = criteriaQuery.from(Payment.class);

        int page = filter.getFirst()/filter.getPageSize();

        if (!StringUtils.isEmpty(filter.getSortField())){
            pageable = PageRequest.of(page, filter.getPageSize(),
                    filter.getSortOrder().isAscending()? Sort.by(filter.getSortField()).ascending(): Sort.by(filter.getSortField()).descending());

            criteriaQuery.orderBy(filter.getSortOrder().isAscending()?
                    criteriaBuilder.asc(root.get(filter.getSortField())) : criteriaBuilder.desc(root.get(filter.getSortField())));
        } else {
            pageable = PageRequest.of(page, filter.getPageSize(), Sort.by("id").descending());
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
        }
        if (filter.getParams().isEmpty()) {
            Page<Payment> paged = repository.findAll(pageable);
            payments = paged.getContent();
            return payments;
        }

        List<Predicate> predicates = buildPredicates(filter, criteriaBuilder, root);
        Predicate[] args = new Predicate[predicates.size()];
        for (int i = 0; i < predicates.size(); i++) {
            args[i] = predicates.get(i);
        }

        criteriaQuery.where(criteriaBuilder.and(args));
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(Payment.class)));
        count = em.createQuery(countQuery).getSingleResult();

//        System.out.println(count);

//        criteriaQuery.where(criteriaBuilder.and(args));

        TypedQuery<Payment> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page);
        typedQuery.setMaxResults(filter.getPageSize());

        return typedQuery.getResultList();
    }

    private List<Predicate> buildPredicates(Filter<Payment> filter, CriteriaBuilder criteriaBuilder, Root<Payment> root){
        List<Predicate> predicates = new ArrayList<>();

        filter.getParams().forEach((k,v) -> {
            Predicate predicate = null;

            if("status".equals(k))
                predicate = criteriaBuilder.equal(root.get(k), outStatus(String.valueOf(v)));
            else if("dateCreation".equals(k)){
                System.out.println(v);
            }
            else
                predicate = criteriaBuilder.like(root.get(k), "%" + v + "%");
            predicates.add(predicate);
        });

        return predicates;
    }

    private OutboundMessage.SentStatus outStatus(String v){
        if("Queued".equals(v))
            return OutboundMessage.SentStatus.Queued;
        if("Sent".equals(v))
            return OutboundMessage.SentStatus.Sent;
        if("Unsent".equals(v))
            return OutboundMessage.SentStatus.Unsent;
        if("Failed".equals(v))
            return OutboundMessage.SentStatus.Failed;

        return OutboundMessage.SentStatus.Sent;
    }

    public void remove(Payment param) {
        repository.delete(param);
    }

    public long count(Filter<Payment> filter) {
        if (filter.getParams().isEmpty())
            return repository.count();

        else
            return this.count;
    }

    public Payment findById(Long id) {
        Optional<Payment> c = repository.findById(id);
        return c.get();
    }

    public void update(Payment param) {
        repository.save(param);
    }

    public void insert(Payment param) {
        repository.save(param);
    }

    public Long count() {
        return repository.count();
    }


    public List<Payment> filter(LocalDate date) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
        Root<Payment> root = criteriaQuery.from(Payment.class);

        Predicate predicateDate = criteriaBuilder.between(root.get("dateCreation"),
                AppUtil.convertToDate(date.atStartOfDay()), AppUtil.convertToDate(date.atTime(LocalTime.MAX)));

        criteriaQuery.where(
                criteriaBuilder.or(
                        predicateDate)
        );

        return em.createQuery(criteriaQuery).getResultList();
    }

    public List<Payment> filter(boolean onlyPayer, boolean onlyRecipient, String txt) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
        Root<Payment> root = criteriaQuery.from(Payment.class);

        Predicate predicatePayer = criteriaBuilder.like(root.get("payer"), "%" + txt + "%");
        Predicate predicateRecipient = criteriaBuilder.like(root.get("recipient"), "%" + txt + "%");

        Predicate predicateNom = criteriaBuilder.between(root.get("dateCreation"), new Date(), new Date());

        criteriaQuery.where(
                criteriaBuilder.or(
                        predicatePayer,
                        predicateRecipient,
                        predicateNom)
        );

        return em.createQuery(criteriaQuery).getResultList();
    }

    public List<Payment> filter(LocalDate date, NumProfile profile, OutboundMessage.SentStatus status, String port, String text,
                                int page, int size) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Payment> criteriaQuery = criteriaBuilder.createQuery(Payment.class);
        Root<Payment> root = criteriaQuery.from(Payment.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(text)) {
            Predicate predicatePayer = criteriaBuilder.like(root.get("payer"), "%" + text + "%");
            Predicate predicateRecipient = criteriaBuilder.like(root.get("recipient"), "%" + text + "%");
            Predicate predicatePayerName = criteriaBuilder.like(root.get("payerName"), "%" + text + "%");

            Predicate predicateTexte = criteriaBuilder.or(predicatePayer, predicateRecipient, predicatePayerName);
            predicates.add(predicateTexte);
        }
        if (!StringUtils.isEmpty(port)) {
            Predicate predicatePortPayment = criteriaBuilder.or(criteriaBuilder.like(root.get("gateway"), port), criteriaBuilder.like(root.get("outGateway"), port));
            predicates.add(predicatePortPayment);
        }
        if (status != null) {
            Predicate predicateStatus = criteriaBuilder.equal(root.get("status"), status);
            predicates.add(predicateStatus);
        }
        if (date != null) {
            Predicate predicateDate = criteriaBuilder.between(root.get("dateCreation"), AppUtil.convertToDate(date.atStartOfDay()),
                    AppUtil.convertToDate(date.atTime(LocalTime.MAX)));
            predicates.add(predicateDate);
        }
        if (profile != null) {
            Predicate predicateRecipientProfile = criteriaBuilder.equal(root.get("recipientProfile"), profile);
            Predicate predicateAgentProfile = criteriaBuilder.equal(root.get("agentProfile"), profile);
            Predicate predicateProfile = criteriaBuilder.or(predicateRecipientProfile, predicateAgentProfile);
            predicates.add(predicateProfile);
        }

        Predicate[] varargs = new Predicate[predicates.size()];
        for (int i = 0; i < predicates.size(); i++) {
            varargs[i] = predicates.get(i);
        }

        criteriaQuery.where(criteriaBuilder.and(varargs));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        CriteriaQuery<Long> countQuery = criteriaBuilder
                .createQuery(Long.class);
        countQuery.select(criteriaBuilder
                .count(countQuery.from(Payment.class)));
        Long count = em.createQuery(countQuery)
                .getSingleResult();

        criteriaQuery.where(criteriaBuilder.and(varargs));

        TypedQuery<Payment> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public Long sum(String payement, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.sumPayment(payement, startDate, endDate);
    }

    public Long sum(String type, Carrier agentNetwork, Carrier recipientNetwork, NumProfile recipientProfile, LocalDateTime from, LocalDateTime to) {
        return repository.sumPayment(type, agentNetwork.name(), recipientNetwork.name(), recipientProfile.name(), from, to);
    }

    public Long count(String payement, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.count(payement, startDate, endDate);
    }

    public Long count(OutboundMessage.SentStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.count(status, startDate, endDate);
    }


}
