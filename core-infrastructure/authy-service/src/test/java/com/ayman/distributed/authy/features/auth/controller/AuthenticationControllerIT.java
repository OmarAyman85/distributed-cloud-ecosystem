package com.ayman.distributed.authy.features.auth.controller;

import com.ayman.distributed.authy.features.auth.dto.UserLoginRequestDTO;
import com.ayman.distributed.authy.features.identity.model.Application;
import com.ayman.distributed.authy.features.identity.model.Role;
import com.ayman.distributed.authy.features.identity.model.User;
import com.ayman.distributed.authy.features.identity.model.UserRole;
import com.ayman.distributed.authy.features.identity.repository.UserRepository;
import com.ayman.distributed.authy.features.identity.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.ayman.distributed.authy.features.identity.repository.ApplicationRepository applicationRepository;

    @Autowired
    private com.ayman.distributed.authy.features.identity.repository.RoleRepository roleRepository;

    @Autowired
    private com.ayman.distributed.authy.features.identity.repository.UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        applicationRepository.deleteAll();
        roleRepository.deleteAll();
        
        // Setup Application
        Application app = new Application();
        app.setAppKey("AUTHY");
        app.setAppName("Authy Service");
        app.setStatus(com.ayman.distributed.authy.features.identity.model.ApplicationStatus.ACTIVE);
        applicationRepository.save(app);

        // Setup Role
        Role role = new Role();
        role.setRoleKey("ROLE_USER");
        role.setName("User");
        role.assignToApplication(app);
        roleRepository.save(role);

        // Setup User
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender(com.ayman.distributed.authy.features.identity.model.Gender.OTHER);
        user.setStatus(com.ayman.distributed.authy.features.identity.model.UserStatus.ACTIVE);
        user.setMfaEnabled(false);
        user.setEmailVerified(true);
        user.setUserRoles(new HashSet<>()); // Initialize the set
        userRepository.save(user);

        // Assign Role
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setApplication(app);
        userRoleRepository.save(userRole);

        user.getUserRoles().add(userRole); // Sync relationship
        userRepository.save(user);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO("AUTHY", "testuser", "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.user_name").value("testuser"));
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO("AUTHY", "testuser", "wrongpassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        // First login to get a token
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO("AUTHY", "testuser", "password123");
        String response = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();
        
        String token = objectMapper.readTree(response).get("access_token").asText();

        // Then logout
        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
