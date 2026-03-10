package com.ayman.distributed.authy.features.identity.repository;

import com.ayman.distributed.authy.features.identity.model.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
    Optional<UserProvider> findByProviderNameAndProviderId(String providerName, String providerId);
}
