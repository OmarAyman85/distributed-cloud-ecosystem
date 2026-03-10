package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.ClothingItem;
import com.ayman.distributed.simuclothing.repository.ClothingItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClothingService {

    private final ClothingItemRepository repository;

    public ClothingItem addItem(ClothingItem item) {
        log.info("Adding new clothing item: {}", item.getName());
        return repository.save(item);
    }

    public List<ClothingItem> getAllItems() {
        log.info("Fetching all clothing items");
        return repository.findAll();
    }

    public List<ClothingItem> getItemsByType(String type) {
        log.info("Fetching clothing items of type: {}", type);
        return repository.findByType(type);
    }

    public ClothingItem getItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clothing item not found with id: " + id));
    }
}
