package com.ayman.distributed.savvy.services.investment;

import com.ayman.distributed.savvy.model.dto.InvestmentDTO;
import com.ayman.distributed.savvy.model.entity.Investment;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.model.repository.InvestmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Investment createInvestment(InvestmentDTO investmentDTO) {
        Investment investment = new Investment();
        investment.setUser(getCurrentUser());
        return saveOrUpdateInvestment(investment, investmentDTO);
    }

    private Investment saveOrUpdateInvestment(Investment investment, InvestmentDTO investmentDTO) {
        investment.setTitle(investmentDTO.getTitle());
        investment.setDescription(investmentDTO.getDescription());
        investment.setSymbol(investmentDTO.getSymbol());
        investment.setAmountInvested(investmentDTO.getAmountInvested());
        investment.setCurrentValue(investmentDTO.getCurrentValue());
        investment.setDate(investmentDTO.getDate());
        return investmentRepository.save(investment);
    }

    @Override
    public List<Investment> getAllInvestments() {
        User user = getCurrentUser();
        return investmentRepository.findAllByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(Investment::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Investment getInvestmentById(Long id) {
        User user = getCurrentUser();
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Investment with id " + id + " not found."));
        if (!investment.getUser().getId().equals(user.getId())) {
             throw new EntityNotFoundException("Investment with id " + id + " not found.");
        }
        return investment;
    }

    @Override
    public Investment updateInvestment(InvestmentDTO investmentDTO, Long id) {
        Investment investment = getInvestmentById(id);
        return saveOrUpdateInvestment(investment, investmentDTO);
    }

    @Override
    public void deleteInvestment(Long id) {
        Investment investment = getInvestmentById(id);
        investmentRepository.delete(investment);
    }
}
