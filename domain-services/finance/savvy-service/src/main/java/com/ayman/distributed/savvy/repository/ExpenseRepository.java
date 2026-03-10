package com.ayman.distributed.savvy.repository;

import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // User-scoped queries
    List<Expense> findAllByUser(User user);

    List<Expense> findByDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user")
    Double sumAllExpensesByUser(@Param("user") User user);

    Optional<Expense> findFirstByUserOrderByDateDesc(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);

    // Global queries (kept for backward compatibility)
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double sumAllExpenses();

    Optional<Expense> findFirstByOrderByDateDesc();

    // Budget support: sum expenses by category and date range for a user
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.category = :category AND e.date BETWEEN :startDate AND :endDate")
    Double sumByUserAndCategoryAndDateBetween(@Param("user") User user, @Param("category") String category, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
