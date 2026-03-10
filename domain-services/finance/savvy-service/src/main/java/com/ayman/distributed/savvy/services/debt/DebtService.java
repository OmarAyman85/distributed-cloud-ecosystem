package com.ayman.distributed.savvy.services.debt;

import com.ayman.distributed.savvy.model.dto.DebtDTO;
import com.ayman.distributed.savvy.model.entity.Debt;

import java.util.List;

public interface DebtService {
    Debt createDebt(DebtDTO debtDTO);
    List<Debt> getAllDebts();
    Debt getDebtById(Long id);
    Debt updateDebt(DebtDTO debtDTO, Long id);
    void deleteDebt(Long id);
}
