package com.ayman.distributed.savvy.services.stats;

import com.ayman.distributed.savvy.dto.GraphDTO;
import com.ayman.distributed.savvy.dto.StatsDTO;
import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.model.entity.Income;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.repository.ExpenseRepository;
import com.ayman.distributed.savvy.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public GraphDTO getChartData() {
        User user = getCurrentUser();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setExpenseList(expenseRepository.findByDateBetweenAndUser(startDate, endDate, user));
        graphDTO.setIncomeList(incomeRepository.findByDateBetweenAndUser(startDate, endDate, user));
        return graphDTO;
    }

    public StatsDTO getStats() {
        User user = getCurrentUser();

        Double totalIncome = incomeRepository.sumAllIncomesByUser(user);
        Double totalExpense = expenseRepository.sumAllExpensesByUser(user);

        if (totalIncome == null) {
             totalIncome = 0.0;
        }

        if (totalExpense == null) {
             totalExpense = 0.0;
        }

        Optional<Income> income = incomeRepository.findFirstByUserOrderByDateDesc(user);
        Optional<Expense> expense = expenseRepository.findFirstByUserOrderByDateDesc(user);

        StatsDTO statsDTO = new StatsDTO();
        statsDTO.setExpense(totalExpense);
        statsDTO.setIncome(totalIncome);

        income.ifPresent(statsDTO::setLatestIncome);
        expense.ifPresent(statsDTO::setLatestExpense);

        statsDTO.setBalance(totalIncome - totalExpense);

        List<Income> incomeList = incomeRepository.findAllByUser(user);
        List<Expense> expenseList = expenseRepository.findAllByUser(user);

        OptionalDouble minIncome = incomeList.stream().mapToDouble(Income::getAmount).min();
        OptionalDouble maxIncome = incomeList.stream().mapToDouble(Income::getAmount).max();

        OptionalDouble minExpense = expenseList.stream().mapToDouble(Expense::getAmount).min();
        OptionalDouble maxExpense = expenseList.stream().mapToDouble(Expense::getAmount).max();

        statsDTO.setMinIncome(minIncome.orElse(0.0));
        statsDTO.setMaxIncome(maxIncome.orElse(0.0));
        statsDTO.setMinExpense(minExpense.orElse(0.0));
        statsDTO.setMaxExpense(maxExpense.orElse(0.0));

        return statsDTO;
    }
}
