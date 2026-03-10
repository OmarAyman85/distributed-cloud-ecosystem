package com.ayman.distributed.authy.features.mfa.service;

import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.mfa.model.MfaMethod;
import com.ayman.distributed.authy.features.mfa.model.MfaRecoveryCode;
import com.ayman.distributed.authy.features.mfa.repository.MfaRecoveryCodeRepository;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

/**
 * Unified service for handling Multi-Factor Authentication (MFA) operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MfaService {

    private static final String ISSUER = "Authy";
    private static final int OTP_DIGITS = 6;
    private static final int OTP_PERIOD = 30;
    private static final HashingAlgorithm HASH_ALGORITHM = HashingAlgorithm.SHA1;

    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();

    private final MfaRecoveryCodeRepository recoveryCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* --------------------------- TOTP Logic --------------------------- */

    public String generateNewSecret() {
        return new DefaultSecretGenerator().generate();
    }

    public String generateQRCode(String secret) {
        QrData qrData = new QrData.Builder()
                .label(ISSUER)
                .secret(secret)
                .issuer(ISSUER)
                .algorithm(HASH_ALGORITHM)
                .digits(OTP_DIGITS)
                .period(OTP_PERIOD)
                .build();

        try {
            byte[] imageData = qrGenerator.generate(qrData);
            return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
        } catch (QrGenerationException e) {
            log.error("Error generating QR code: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTotpValid(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }

    /* --------------------------- Recovery Codes --------------------------- */

    /**
     * Generates a new set of recovery codes for a user.
     * Revolkes any existing ones.
     */
    @Transactional
    public List<String> generateRecoveryCodes(User user) {
        recoveryCodeRepository.deleteByUser(user);
        
        List<String> rawCodes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rawCodes.add(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        Set<MfaRecoveryCode> encodedCodes = rawCodes.stream()
                .map(code -> MfaRecoveryCode.builder()
                        .user(user)
                        .hashedCode(passwordEncoder.encode(code))
                        .used(false)
                        .build())
                .collect(Collectors.toSet());

        recoveryCodeRepository.saveAll(encodedCodes);
        return rawCodes;
    }

    /**
     * Verifies if a code is a valid (and unused) recovery code for the user.
     */
    @Transactional
    public boolean verifyRecoveryCode(User user, String code) {
        List<MfaRecoveryCode> unusedCodes = recoveryCodeRepository.findByUserAndUsedFalse(user);
        
        for (MfaRecoveryCode recoveryCode : unusedCodes) {
            if (passwordEncoder.matches(code, recoveryCode.getHashedCode())) {
                recoveryCode.setUsed(true);
                recoveryCode.setUsedAt(Instant.now());
                recoveryCodeRepository.save(recoveryCode);
                return true;
            }
        }
        return false;
    }

    /* --------------------------- Email OTP Logic --------------------------- */

    /**
     * Updates the user's preferred MFA method.
     */
    @Transactional
    public void updatePreferredMethod(User user, MfaMethod method) {
        user.setPreferredMfaMethod(method);
        userRepository.save(user);
    }

    /**
     * Simulates sending an Email OTP.
     */
    public void sendEmailOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        // In real app, store this in cache (Redis) with TTL and send email
        log.info(">>> EMAIL OTP for [{}]: {}", user.getEmail(), otp);
        System.out.println(">>> EMAIL OTP for [" + user.getEmail() + "]: " + otp);
        // For now, we don't have a storage for this simulation, but we could add one if needed.
    }
}
