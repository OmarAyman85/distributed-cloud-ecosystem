package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.Product;
import com.ayman.distributed.simuclothing.model.WardrobeItem;
import com.ayman.distributed.simuclothing.model.WardrobeStatus;
import com.ayman.distributed.simuclothing.repository.ProductRepository;
import com.ayman.distributed.simuclothing.repository.WardrobeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WardrobeService {

    private final WardrobeItemRepository wardrobeRepository;
    private final ProductRepository productRepository;

    public List<WardrobeItem> getWardrobe(String userId, WardrobeStatus status) {
        if (status != null) {
            return wardrobeRepository.findByUserIdAndStatus(userId, status);
        }
        return wardrobeRepository.findByUserId(userId);
    }

    @Transactional
    public WardrobeItem addToWardrobe(String userId, Long productId, WardrobeStatus status) {
        Optional<WardrobeItem> existing = wardrobeRepository.findByUserIdAndProductId(userId, productId);
        
        if (existing.isPresent()) {
            WardrobeItem item = existing.get();
            // If it was in wishlist and now added as owned (e.g. purchased), update status
            if (item.getStatus() == WardrobeStatus.WISHLIST && status == WardrobeStatus.OWNED) {
                item.setStatus(WardrobeStatus.OWNED);
                return wardrobeRepository.save(item);
            }
            return item;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WardrobeItem newItem = WardrobeItem.builder()
                .userId(userId)
                .product(product)
                .status(status)
                .build();

        return wardrobeRepository.save(newItem);
    }

    @Transactional
    public void removeFromWardrobe(String userId, Long wardrobeItemId) {
        WardrobeItem item = wardrobeRepository.findById(wardrobeItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        wardrobeRepository.delete(item);
    }
}
