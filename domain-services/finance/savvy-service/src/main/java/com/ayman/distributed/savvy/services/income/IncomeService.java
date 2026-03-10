package com.ayman.distributed.savvy.services.income;

import com.ayman.distributed.savvy.dto.IncomeDTO;
import com.ayman.distributed.savvy.model.entity.Income;

import java.util.List;

public interface IncomeService {

    Income createIncome(IncomeDTO incomeDTO);

    List<Income> getAllIncomes();

    Income getIncomeById(Long id);

    Income updateIncome(IncomeDTO incomeDTO, Long id);

    void deleteIncome(Long id);
}
