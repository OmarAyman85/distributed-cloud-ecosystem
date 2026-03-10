package com.ayman.distributed.carloger.repository;

import com.ayman.distributed.carloger.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwnerId(String ownerId);
}
