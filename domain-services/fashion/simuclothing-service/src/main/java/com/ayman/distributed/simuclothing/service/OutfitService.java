package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.Outfit;
import com.ayman.distributed.simuclothing.model.WardrobeItem;
import com.ayman.distributed.simuclothing.repository.OutfitRepository;
import com.ayman.distributed.simuclothing.repository.WardrobeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final WardrobeItemRepository wardrobeRepository;

    public List<Outfit> getOutfits(String userId) {
        return outfitRepository.findByUserId(userId);
    }

    @Transactional
    public Outfit createOutfit(String userId, Outfit outfitData, List<Long> wardrobeItemIds) {
        Set<WardrobeItem> items = wardrobeItemIds.stream()
                .map(id -> wardrobeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Wardrobe item not found: " + id)))
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toSet());

        Outfit outfit = Outfit.builder()
                .userId(userId)
                .name(outfitData.getName())
                .description(outfitData.getDescription())
                .occasion(outfitData.getOccasion())
                .items(items)
                .build();

        return outfitRepository.save(outfit);
    }

    @Transactional
    public void deleteOutfit(String userId, Long outfitId) {
        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new RuntimeException("Outfit not found"));

        if (!outfit.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        outfitRepository.delete(outfit);
    }
}
