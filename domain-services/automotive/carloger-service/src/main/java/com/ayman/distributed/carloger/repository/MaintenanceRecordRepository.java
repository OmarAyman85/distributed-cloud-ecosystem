package com.ayman.distributed.carloger.repository;

import com.ayman.distributed.carloger.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByCarId(Long carId);
}
