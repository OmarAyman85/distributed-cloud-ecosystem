package com.ayman.distributed.carloger.service;

import com.ayman.distributed.carloger.model.Car;
import com.ayman.distributed.carloger.model.MaintenanceRecord;
import com.ayman.distributed.carloger.repository.CarRepository;
import com.ayman.distributed.carloger.repository.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository maintenanceRepository;

    public Car addCar(Car car) {
        log.info("Adding new car for owner: {}", car.getOwnerId());
        return carRepository.save(car);
    }

    public List<Car> getCarsByOwner(String ownerId) {
        log.info("Fetching cars for owner: {}", ownerId);
        return carRepository.findByOwnerId(ownerId);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }

    @Transactional
    public MaintenanceRecord addMaintenanceRecord(Long carId, MaintenanceRecord record) {
        log.info("Adding maintenance record for car: {}", carId);
        // Verify car exists
        getCarById(carId);
        record.setCarId(carId);
        return maintenanceRepository.save(record);
    }

    public List<MaintenanceRecord> getMaintenanceHistory(Long carId) {
        log.info("Fetching maintenance history for car: {}", carId);
        return maintenanceRepository.findByCarId(carId);
    }
}
