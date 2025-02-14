package com.aprilmack.jwtexample.controllers.users;

import com.aprilmack.jwtexample.config.TestConfig;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginResponse;
import com.aprilmack.jwtexample.controllers.users.dtos.UserResponse;
import com.aprilmack.jwtexample.db.entities.UserEntity;
import com.aprilmack.jwtexample.db.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UsersControllerTest {
    private static final String FULL_NAME = "April Mackintosh" ;
    private static final String EMAIL = "aprilmack@posteo.net" ;
    private static final String PASSWORD = "password12345";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        userRepo.deleteAll();
    }

    @SneakyThrows
    @Test
    public void me_notLoggedIn() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @SneakyThrows
    @Test
    public void me() {
        final LoginResponse loginResponse = login();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization", "Bearer " + loginResponse.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        final UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getFullName()).isEqualTo(FULL_NAME);
        assertThat(response.getCreatedAt()).isEqualTo(TestConfig.CURRENT_TIME);
        assertThat(response.getUpdatedAt()).isEqualTo(TestConfig.CURRENT_TIME);
    }

    @SneakyThrows
    private LoginResponse login() {
        createUser();

        final LoginRequest body = LoginRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
    }

    private void createUser() {
        final UserEntity existingUserEntity = new UserEntity(
                FULL_NAME,
                EMAIL,
                passwordEncoder.encode(PASSWORD),
                TestConfig.CURRENT_TIME
        );
        userRepo.saveAndFlush(existingUserEntity);
    }
}