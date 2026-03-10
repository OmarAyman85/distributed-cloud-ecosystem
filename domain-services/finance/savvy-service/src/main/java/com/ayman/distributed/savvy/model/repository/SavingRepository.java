package com.ayman.distributed.savvy.model.repository;

import com.ayman.distributed.savvy.model.entity.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Long> {
    List<Saving> findAllByUserId(Long userId);
}
