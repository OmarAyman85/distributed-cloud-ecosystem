package com.ayman.distributed.simuclothing.service;

import com.ayman.distributed.simuclothing.model.FashionProfile;
import com.ayman.distributed.simuclothing.repository.FashionProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FashionProfileService {

    private final FashionProfileRepository profileRepository;

    public FashionProfile getProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElse(FashionProfile.builder().userId(userId).build());
    }

    @Transactional
    public FashionProfile updateProfile(String userId, FashionProfile profileData) {
        FashionProfile profile = profileRepository.findByUserId(userId)
                .orElse(FashionProfile.builder().userId(userId).build());
        
        profile.setHeightCm(profileData.getHeightCm());
        profile.setWeightKg(profileData.getWeightKg());
        profile.setBodyType(profileData.getBodyType());
        
        return profileRepository.save(profile);
    }
}
