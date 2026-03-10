package com.ayman.distributed.savvy.repository;

import com.ayman.distributed.savvy.model.entity.Income;
import com.ayman.distributed.savvy.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // User-scoped queries
    List<Income> findAllByUser(User user);

    List<Income> findByDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user")
    Double sumAllIncomesByUser(@Param("user") User user);

    Optional<Income> findFirstByUserOrderByDateDesc(User user);

    Optional<Income> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);

    // Global queries (kept for backward compatibility)
    List<Income> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(i.amount) FROM Income i")
    Double sumAllIncomes();

    Optional<Income> findFirstByOrderByDateDesc();

    // Budget support: sum expenses by category and date range for a user
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user = :user AND i.category = :category AND i.date BETWEEN :startDate AND :endDate")
    Double sumByUserAndCategoryAndDateBetween(@Param("user") User user, @Param("category") String category, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
