package com.ayman.distributed.authy.e2e;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.identity.model.User;

import com.ayman.distributed.authy.features.auth.dto.AuthenticationResponse;
import com.ayman.distributed.authy.features.identity.dto.StandardUserRegistrationRequestDTO;
import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.identity.model.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    public void testUserRegistrationAndLoginFlow() {
        String email = "testuser" + System.currentTimeMillis() + "@example.com";
        String password = "Password123!";

        // 1. Register a new user
        StandardUserRegistrationRequestDTO registrationRequest = new StandardUserRegistrationRequestDTO(
                "AUTHY",
                email,
                "testuser",
                password,
                "Test",
                "User",
                "Test User",
                null,
                "en-US",
                "UTC",
                null,
                Gender.MALE,
                null,
                false
        );

        ResponseEntity<AuthenticationResponse> registrationResponse = restTemplate.postForEntity(
                getBaseUrl() + "/register",
                registrationRequest,
                AuthenticationResponse.class
        );

        assertThat(registrationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registrationResponse.getBody()).isNotNull();
        assertThat(registrationResponse.getBody().getAccessToken()).isNotNull();
        
        // 2. Login with the new user
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO("AUTHY", email, password);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(
                getBaseUrl() + "/login",
                loginRequest,
                AuthenticationResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getAccessToken()).isNotNull();
    }
}
