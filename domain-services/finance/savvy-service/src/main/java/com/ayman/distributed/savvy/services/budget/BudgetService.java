package com.ayman.distributed.savvy.services.budget;

import com.ayman.distributed.savvy.dto.BudgetDTO;
import com.ayman.distributed.savvy.model.entity.Budget;

import java.util.List;

public interface BudgetService {
    Budget createBudget(BudgetDTO budgetDTO);
    List<BudgetDTO> getAllBudgetsWithStatus();
    Budget updateBudget(Long id, BudgetDTO budgetDTO);
    void deleteBudget(Long id);
}
