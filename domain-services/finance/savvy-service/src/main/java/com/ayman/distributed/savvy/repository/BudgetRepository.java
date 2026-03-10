package com.ayman.distributed.savvy.repository;

import com.ayman.distributed.savvy.model.entity.Budget;
import com.ayman.distributed.savvy.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findAllByUser(User user);
    Optional<Budget> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
    Optional<Budget> findByCategoryAndUser(String category, User user);
}
