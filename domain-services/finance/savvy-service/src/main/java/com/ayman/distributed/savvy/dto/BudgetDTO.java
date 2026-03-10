package com.ayman.distributed.savvy.dto;

import com.ayman.distributed.savvy.model.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDTO {
    private Long id;
    private String category;
    private Integer budgetLimit;
    private Budget.Period period;
    private Double spent;       // calculated: actual spending in current period
    private Double remaining;   // calculated: limit - spent
    private Double percentage;  // calculated: (spent / limit) * 100
}
