package com.ayman.distributed.savvy.services.saving;

import com.ayman.distributed.savvy.model.dto.SavingDTO;
import com.ayman.distributed.savvy.model.entity.Saving;

import java.util.List;

public interface SavingService {
    Saving createSaving(SavingDTO savingDTO);
    List<Saving> getAllSavings();
    Saving getSavingById(Long id);
    Saving updateSaving(SavingDTO savingDTO, Long id);
    void deleteSaving(Long id);
}
