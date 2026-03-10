package com.ayman.distributed.savvy.services.debt;

import com.ayman.distributed.savvy.model.dto.DebtDTO;
import com.ayman.distributed.savvy.model.entity.Debt;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.model.repository.DebtRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Debt createDebt(DebtDTO debtDTO) {
        Debt debt = new Debt();
        debt.setUser(getCurrentUser());
        return saveOrUpdateDebt(debt, debtDTO);
    }

    private Debt saveOrUpdateDebt(Debt debt, DebtDTO debtDTO) {
        debt.setTitle(debtDTO.getTitle());
        debt.setDescription(debtDTO.getDescription());
        debt.setAmount(debtDTO.getAmount());
        debt.setDueDate(debtDTO.getDueDate());
        return debtRepository.save(debt);
    }

    @Override
    public List<Debt> getAllDebts() {
        User user = getCurrentUser();
        return debtRepository.findAllByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(Debt::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Debt getDebtById(Long id) {
        User user = getCurrentUser();
        Debt debt = debtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Debt with id " + id + " not found."));
        if (!debt.getUser().getId().equals(user.getId())) {
             throw new EntityNotFoundException("Debt with id " + id + " not found.");
        }
        return debt;
    }

    @Override
    public Debt updateDebt(DebtDTO debtDTO, Long id) {
        Debt debt = getDebtById(id);
        return saveOrUpdateDebt(debt, debtDTO);
    }

    @Override
    public void deleteDebt(Long id) {
        Debt debt = getDebtById(id);
        debtRepository.delete(debt);
    }
}
