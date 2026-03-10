package com.ayman.distributed.authy.features.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordBreachService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.pwnedpasswords.com/range")
            .build();

    /**
     * Checks if the password exists in the HaveIBeenPwned database using k-Anonymity.
     * @param password The raw password to check.
     * @return true if the password is found in a breach, false otherwise.
     */
    public boolean isPasswordBreached(String password) {
        try {
            // 1. Hash password with SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(password.getBytes());
            String sha1 = HexFormat.of().formatHex(hashBytes).toUpperCase();

            // 2. Extract prefix (first 5 chars) and suffix
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5);

            // 3. Call API with prefix
            String response = restClient.get()
                    .uri("/{prefix}", prefix)
                    .retrieve()
                    .body(String.class);

            if (response == null) {
                return false;
            }

            // 4. Check if suffix exists in response
            // Response format: SUFFIX:COUNT\nSUFFIX:COUNT...
            return response.lines().anyMatch(line -> line.startsWith(suffix));

        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 algorithm not found", e);
            return false; // Fail open (allow password) if hashing fails
        } catch (Exception e) {
            log.warn("Failed to check password breach status via API", e);
            return false; // Fail open on API error to avoid blocking registration
        }
    }
}
