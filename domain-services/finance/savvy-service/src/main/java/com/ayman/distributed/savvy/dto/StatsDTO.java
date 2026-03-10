package com.ayman.distributed.savvy.dto;

import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.model.entity.Income;
import lombok.Data;

@Data
public class StatsDTO {
    private Double balance;

    private Double income;
    private Double expense;

    private Double minIncome;
    private Double maxIncome;

    private Double minExpense;
    private Double maxExpense;

    private Income latestIncome;
    private Expense latestExpense;
}
