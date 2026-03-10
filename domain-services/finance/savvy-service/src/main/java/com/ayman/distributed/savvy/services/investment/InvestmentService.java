package com.ayman.distributed.savvy.services.investment;

import com.ayman.distributed.savvy.model.dto.InvestmentDTO;
import com.ayman.distributed.savvy.model.entity.Investment;

import java.util.List;

public interface InvestmentService {
    Investment createInvestment(InvestmentDTO investmentDTO);
    List<Investment> getAllInvestments();
    Investment getInvestmentById(Long id);
    Investment updateInvestment(InvestmentDTO investmentDTO, Long id);
    void deleteInvestment(Long id);
}
