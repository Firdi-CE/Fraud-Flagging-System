package com.backend_pmgt.repository;

import com.backend_pmgt.entity.RulesValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RulesValueRepository extends JpaRepository<RulesValue, Long> {

    List<RulesValue> findAllByOrderByRulesIDAsc();

}