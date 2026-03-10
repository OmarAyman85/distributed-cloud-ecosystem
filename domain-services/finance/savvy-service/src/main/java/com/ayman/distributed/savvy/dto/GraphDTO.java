package com.ayman.distributed.savvy.dto;

import com.ayman.distributed.savvy.model.entity.Expense;
import com.ayman.distributed.savvy.model.entity.Income;
import lombok.Data;

import java.util.List;

@Data
public class GraphDTO {
    private List<Expense> expenseList;

    private List<Income> incomeList;
}
