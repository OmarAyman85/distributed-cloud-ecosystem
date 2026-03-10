package com.ayman.distributed.authy.features.mfa.repository;

import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.mfa.model.MfaRecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MfaRecoveryCodeRepository extends JpaRepository<MfaRecoveryCode, Long> {
    List<MfaRecoveryCode> findByUserAndUsedFalse(User user);
    void deleteByUser(User user);
}
