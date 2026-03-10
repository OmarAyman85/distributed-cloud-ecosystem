package com.ayman.distributed.savvy.services.income;

import com.ayman.distributed.savvy.dto.IncomeDTO;
import com.ayman.distributed.savvy.model.entity.Income;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.repository.IncomeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Income createIncome(IncomeDTO incomeDTO) {
        Income income = new Income();
        income.setUser(getCurrentUser());
        return saveOrUpdateIncome(income, incomeDTO);
    }

    private Income saveOrUpdateIncome(Income income, IncomeDTO incomeDTO) {
        income.setTitle(incomeDTO.getTitle());
        income.setDate(incomeDTO.getDate());
        income.setAmount(incomeDTO.getAmount());
        income.setCategory(incomeDTO.getCategory());
        income.setDescription(incomeDTO.getDescription());
        return incomeRepository.save(income);
    }

    @Override
    public List<Income> getAllIncomes() {
        User user = getCurrentUser();
        return incomeRepository.findAllByUser(user)
                .stream()
                .sorted(Comparator.comparing(Income::getDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Income getIncomeById(Long id) {
        User user = getCurrentUser();
        return incomeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + id + " not found."));
    }

    @Override
    public Income updateIncome(IncomeDTO incomeDTO, Long id) {
        User user = getCurrentUser();
        Income income = incomeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Income with id " + id + " not found."));
        return saveOrUpdateIncome(income, incomeDTO);
    }

    @Override
    public void deleteIncome(Long id) {
        User user = getCurrentUser();
        if (!incomeRepository.existsByIdAndUser(id, user)) {
            throw new EntityNotFoundException("Income with id " + id + " not found.");
        }
        incomeRepository.deleteById(id);
    }
}
