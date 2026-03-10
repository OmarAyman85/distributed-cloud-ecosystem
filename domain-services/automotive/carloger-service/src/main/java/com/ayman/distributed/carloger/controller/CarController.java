package com.ayman.distributed.carloger.controller;

import com.ayman.distributed.carloger.model.Car;
import com.ayman.distributed.carloger.model.MaintenanceRecord;
import com.ayman.distributed.carloger.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<Car> addCar(@Valid @RequestBody Car car) {
        return ResponseEntity.ok(carService.addCar(car));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Car>> getCarsByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(carService.getCarsByOwner(ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping("/{carId}/maintenance")
    public ResponseEntity<MaintenanceRecord> addMaintenanceRecord(
            @PathVariable Long carId,
            @Valid @RequestBody MaintenanceRecord record) {
        return ResponseEntity.ok(carService.addMaintenanceRecord(carId, record));
    }

    @GetMapping("/{carId}/maintenance")
    public ResponseEntity<List<MaintenanceRecord>> getMaintenanceHistory(@PathVariable Long carId) {
        return ResponseEntity.ok(carService.getMaintenanceHistory(carId));
    }
}
