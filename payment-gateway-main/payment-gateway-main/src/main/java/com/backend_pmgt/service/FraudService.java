package com.backend_pmgt.service;

import com.backend_pmgt.dto.fraud.FraudResponseAPI;
import com.backend_pmgt.dto.fraud.FraudResponseDTO;
import com.backend_pmgt.dto.fraud.FraudSearchRequestDTO;
import com.backend_pmgt.entity.Fraud;
import com.backend_pmgt.repository.FraudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class FraudService {
    @Autowired
    FraudRepository fraudRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public FraudResponseAPI getAllFraud(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Fraud> fraudsPage = fraudRepository.findAllByOrderByFraudDateDesc(pageable);

        long totalCount = fraudsPage.getTotalElements();


        List<FraudResponseDTO> fraudResponses = new ArrayList<>();

        for(Fraud fraud:fraudsPage){
            FraudResponseDTO result = FraudResponseDTO.builder()
                    .fraudID(fraud.getFraudID())
                    .fraudLabel(fraud.getFraudLabel())
                    .fraudAccount(fraud.getFraudAccount())
                    .fraudDescription(fraud.getFraudDescription())
                    .fraudDate(fraud.getFraudDate())
                    .build();
            fraudResponses.add(result);
        }
        return FraudResponseAPI.builder().frauds(fraudResponses).totalCount(totalCount).build();
    }

    public FraudResponseAPI getFraudByAccountAndOrLabel(String account, String label, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Fraud> fraudsPage = fraudRepository.findByAccountAndLabel(account,label, pageable);

        long totalCount = fraudsPage.getTotalElements();

        List<FraudResponseDTO> fraudResponses = new ArrayList<>();

        for(Fraud fraud:fraudsPage.getContent()){
            FraudResponseDTO result = FraudResponseDTO.builder()
                    .fraudID(fraud.getFraudID())
                    .fraudLabel(fraud.getFraudLabel())
                    .fraudAccount(fraud.getFraudAccount())
                    .fraudDescription(fraud.getFraudDescription())
                    .fraudDate(fraud.getFraudDate())
                    .build();
            fraudResponses.add(result);
        }
        return FraudResponseAPI.builder().frauds(fraudResponses).totalCount(totalCount).build();
    }

    public FraudResponseAPI searchFraud(FraudSearchRequestDTO fraudSearchRequestDTO, int page, int size) {
        if (fraudSearchRequestDTO.getFraudDateEnd() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fraudSearchRequestDTO.getFraudDateEnd());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            fraudSearchRequestDTO.setFraudDateEnd(cal.getTime());
        }

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<Fraud> cq = cb.createQuery(Fraud.class);
        Root<Fraud> root = cq.from(Fraud.class);

        List<Predicate> predicates = new ArrayList<>();

        if (fraudSearchRequestDTO.getFraudAccount() != null && !Objects.equals(fraudSearchRequestDTO.getFraudAccount(), "")) {
            predicates.add(cb.equal(root.get("fraudAccount"), fraudSearchRequestDTO.getFraudAccount()));
        }
        if (fraudSearchRequestDTO.getFraudLabel() != null && !Objects.equals(fraudSearchRequestDTO.getFraudLabel(), "")) {
            predicates.add(cb.equal(root.get("fraudLabel"), fraudSearchRequestDTO.getFraudLabel()));
        }
        if (fraudSearchRequestDTO.getFraudDateStart() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fraudDate"), fraudSearchRequestDTO.getFraudDateStart()));
        }
        if (fraudSearchRequestDTO.getFraudDateEnd() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fraudDate"), fraudSearchRequestDTO.getFraudDateEnd()));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("fraudDate")));

        Pageable pageable = PageRequest.of(page, size);

        List<Fraud> frauds = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalCount = entityManager.createQuery(cq.createCountQuery()).getSingleResult();

        List<FraudResponseDTO> fraudResponses = new ArrayList<>();

        for(Fraud fraud:frauds){
            FraudResponseDTO result = FraudResponseDTO.builder()
                    .fraudID(fraud.getFraudID())
                    .fraudLabel(fraud.getFraudLabel())
                    .fraudAccount(fraud.getFraudAccount())
                    .fraudDescription(fraud.getFraudDescription())
                    .fraudDate(fraud.getFraudDate())
                    .build();
            fraudResponses.add(result);
        }
        return FraudResponseAPI.builder().frauds(fraudResponses).totalCount(totalCount).build();
    }

}
