package com.ayman.distributed.savvy.model.repository;

import com.ayman.distributed.savvy.model.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findAllByUserId(Long userId);
}
