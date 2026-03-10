package com.ayman.distributed.savvy.services.expense;

import com.ayman.distributed.savvy.dto.ExpenseDTO;
import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Expense postExpense(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setUser(getCurrentUser());
        return saveOrUpdateExpense(expense, expenseDTO);
    }

    private Expense saveOrUpdateExpense(Expense expense, ExpenseDTO expenseDTO) {
        expense.setTitle(expenseDTO.getTitle());
        expense.setDate(expenseDTO.getDate());
        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(expenseDTO.getCategory());
        expense.setDescription(expenseDTO.getDescription());
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getAllExpenses() {
        User user = getCurrentUser();
        return expenseRepository.findAllByUser(user)
                .stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Expense getExpenseById(Long id) {
        User user = getCurrentUser();
        return expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense with id " + id + " not found."));
    }

    @Override
    public Expense updateExpense(ExpenseDTO expenseDTO, Long id) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense with id " + id + " not found."));
        return saveOrUpdateExpense(expense, expenseDTO);
    }

    @Override
    public void deleteExpense(Long id) {
        User user = getCurrentUser();
        if (!expenseRepository.existsByIdAndUser(id, user)) {
            throw new EntityNotFoundException("Expense with id " + id + " not found.");
        }
        expenseRepository.deleteById(id);
    }
}
