package com.ayman.distributed.savvy.services.budget;

import com.ayman.distributed.savvy.dto.BudgetDTO;
import com.ayman.distributed.savvy.model.entity.Budget;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.repository.BudgetRepository;
import com.ayman.distributed.savvy.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Budget createBudget(BudgetDTO budgetDTO) {
        User user = getCurrentUser();
        Budget budget = new Budget();
        budget.setCategory(budgetDTO.getCategory());
        budget.setBudgetLimit(budgetDTO.getBudgetLimit());
        budget.setPeriod(budgetDTO.getPeriod());
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    @Override
    public List<BudgetDTO> getAllBudgetsWithStatus() {
        User user = getCurrentUser();
        List<Budget> budgets = budgetRepository.findAllByUser(user);
        return budgets.stream().map(this::toBudgetDTOWithStatus).collect(Collectors.toList());
    }

    @Override
    public Budget updateBudget(Long id, BudgetDTO budgetDTO) {
        User user = getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        budget.setCategory(budgetDTO.getCategory());
        budget.setBudgetLimit(budgetDTO.getBudgetLimit());
        budget.setPeriod(budgetDTO.getPeriod());
        return budgetRepository.save(budget);
    }

    @Override
    public void deleteBudget(Long id) {
        User user = getCurrentUser();
        if (!budgetRepository.existsByIdAndUser(id, user)) {
            throw new EntityNotFoundException("Budget not found");
        }
        budgetRepository.deleteById(id);
    }

    private BudgetDTO toBudgetDTOWithStatus(Budget budget) {
        LocalDate[] dateRange = getPeriodDateRange(budget.getPeriod());
        Double spent = expenseRepository.sumByUserAndCategoryAndDateBetween(
                budget.getUser(), budget.getCategory(), dateRange[0], dateRange[1]);
        if (spent == null) spent = 0.0;

        double limit = budget.getBudgetLimit();
        double remaining = limit - spent;
        double percentage = limit > 0 ? (spent / limit) * 100 : 0;

        return BudgetDTO.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .budgetLimit(budget.getBudgetLimit())
                .period(budget.getPeriod())
                .spent(spent)
                .remaining(remaining)
                .percentage(Math.min(percentage, 100))
                .build();
    }

    private LocalDate[] getPeriodDateRange(Budget.Period period) {
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        switch (period) {
            case WEEKLY:
                start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                break;
            case MONTHLY:
                start = now.withDayOfMonth(1);
                break;
            case YEARLY:
                start = now.withDayOfYear(1);
                break;
            default:
                start = now.withDayOfMonth(1);
        }
        return new LocalDate[]{start, end};
    }
}
