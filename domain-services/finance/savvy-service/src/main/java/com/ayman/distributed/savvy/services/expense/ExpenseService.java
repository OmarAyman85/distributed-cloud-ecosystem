package com.ayman.distributed.savvy.services.expense;

import com.ayman.distributed.savvy.dto.ExpenseDTO;
import com.ayman.distributed.savvy.model.entity.Expense;

import java.util.List;

/**
 * Interface defining business logic for managing expenses.
 * It declares methods for CRUD operations, which will be implemented in a service class.
 */
public interface ExpenseService {

    /**
     * Creates a new expense in the system.
     *
     * @param expenseDTO Data Transfer Object (DTO) containing expense details.
     * @return The created Expense entity.
     */
    Expense postExpense(ExpenseDTO expenseDTO);

    /**
     * Retrieves all expenses stored in the database.
     *
     * @return A list of Expense entities.
     */
    List<Expense> getAllExpenses();

    /**
     * Fetches a single expense by its unique identifier.
     *
     * @param id The ID of the expense.
     * @return The Expense entity if found, or null if not found.
     */
    Expense getExpenseById(Long id);

    /**
     * Updates an existing expense.
     *
     * @param expenseDTO Data Transfer Object containing updated expense details.
     * @param id The ID of the expense to be updated.
     * @return The updated Expense entity.
     */
    Expense updateExpense(ExpenseDTO expenseDTO, Long id);

    /**
     * Deletes an expense from the database.
     *
     * @param id The ID of the expense to be deleted.
     */
    void deleteExpense(Long id);
}
