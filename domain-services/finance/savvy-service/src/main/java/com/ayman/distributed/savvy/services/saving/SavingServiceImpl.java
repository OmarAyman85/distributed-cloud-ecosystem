package com.ayman.distributed.savvy.services.saving;

import com.ayman.distributed.savvy.model.dto.SavingDTO;
import com.ayman.distributed.savvy.model.entity.Saving;
import com.ayman.distributed.savvy.model.entity.User;
import com.ayman.distributed.savvy.model.repository.SavingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {

    private final SavingRepository savingRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public Saving createSaving(SavingDTO savingDTO) {
        Saving saving = new Saving();
        saving.setUser(getCurrentUser());
        return saveOrUpdateSaving(saving, savingDTO);
    }

    private Saving saveOrUpdateSaving(Saving saving, SavingDTO savingDTO) {
        saving.setTitle(savingDTO.getTitle());
        saving.setDescription(savingDTO.getDescription());
        saving.setTargetAmount(savingDTO.getTargetAmount());
        saving.setCurrentAmount(savingDTO.getCurrentAmount());
        saving.setTargetDate(savingDTO.getTargetDate());
        return savingRepository.save(saving);
    }

    @Override
    public List<Saving> getAllSavings() {
        User user = getCurrentUser();
        return savingRepository.findAllByUserId(user.getId())
                .stream()
                .sorted(Comparator.comparing(Saving::getTargetDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Saving getSavingById(Long id) {
        User user = getCurrentUser();
        Saving saving = savingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Saving with id " + id + " not found."));
        if (!saving.getUser().getId().equals(user.getId())) {
             throw new EntityNotFoundException("Saving with id " + id + " not found.");
        }
        return saving;
    }

    @Override
    public Saving updateSaving(SavingDTO savingDTO, Long id) {
        Saving saving = getSavingById(id);
        return saveOrUpdateSaving(saving, savingDTO);
    }

    @Override
    public void deleteSaving(Long id) {
        Saving saving = getSavingById(id);
        savingRepository.delete(saving);
    }
}
