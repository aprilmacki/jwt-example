package com.aprilmack.jwtexample.controllers.auth;

import com.aprilmack.jwtexample.config.TestConfig;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginResponse;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupResponse;
import com.aprilmack.jwtexample.db.entities.UserEntity;
import com.aprilmack.jwtexample.db.repositories.UserRepository;
import com.aprilmack.jwtexample.util.JwtTokenModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {
    private static final String FULL_NAME = "April Mackintosh" ;
    private static final String EMAIL = "aprilmack@posteo.net" ;
    private static final String PASSWORD = "password12345";

    @Value("${security.jwt.expiration-time-ms}")
    private long JWT_EXPIRATION_MS;

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
    public void signUp() {
        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        final SignupResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), SignupResponse.class);

        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getFullName()).isEqualTo(FULL_NAME);
        assertThat(response.getCreatedAt()).isEqualTo(TestConfig.CURRENT_TIME);
        assertThat(response.getUpdatedAt()).isEqualTo(TestConfig.CURRENT_TIME);

        final UserEntity actualUserEntity = userRepo.findById(response.getId()).orElseThrow();
        assertThat(actualUserEntity.getFullName()).isEqualTo(FULL_NAME);
        assertThat(actualUserEntity.getEmail()).isEqualTo(EMAIL);
        assertThat(actualUserEntity.getCreatedAt()).isEqualTo(TestConfig.CURRENT_TIME);
        assertThat(actualUserEntity.getUpdatedAt()).isEqualTo(TestConfig.CURRENT_TIME);
        assertThat(passwordEncoder.matches(PASSWORD, actualUserEntity.getPassword())).isTrue();
    }

    @SneakyThrows
    @Test
    public void signUp_missingEmail() {
        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field email is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_emptyEmail() {
        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .email("")
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field email is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_missingFullName() {
        final SignupRequest body = SignupRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field fullName is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_emptyFullName() {
        final SignupRequest body = SignupRequest.builder()
                .fullName("")
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field fullName is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_missingPassword() {
        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .email(EMAIL)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field password is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_emptyPassword() {
        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password("")
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Bad Request");
        assertThat(problemDetail.getStatus()).isEqualTo(400);
        assertThat(problemDetail.getDetail()).isEqualTo("Field password is required");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void signUp_alreadyExists() {
        final UserEntity existingUserEntity = new UserEntity(
                "Jane Doe",
                EMAIL,
                PASSWORD,
                TestConfig.CURRENT_TIME
        );
        userRepo.saveAndFlush(existingUserEntity);

        final SignupRequest body = SignupRequest.builder()
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Conflict");
        assertThat(problemDetail.getStatus()).isEqualTo(409);
        assertThat(problemDetail.getDetail()).isEqualTo("User with this email already exists");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/signup");
    }

    @SneakyThrows
    @Test
    public void login() {
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
        final LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);

        final Instant expectedExpiration = TestConfig.CURRENT_TIME.plus(JWT_EXPIRATION_MS, ChronoUnit.MILLIS);
        assertThat(response.getExpiresAt()).isEqualTo(expectedExpiration);
        final JwtTokenModel token = parseToken(response.getToken());

        final JwtTokenModel expectedToken = JwtTokenModel.builder()
                .header(Map.of("alg", "HS256"))
                .payload(Map.of(
                        "sub", EMAIL,
                        "iat", (int) TestConfig.CURRENT_TIME.getEpochSecond(),
                        "exp", (int) expectedExpiration.getEpochSecond()
                ))
                .build();
        assertThat(token).isEqualTo(expectedToken);
    }

    @SneakyThrows
    @Test
    public void login_incorrectPassword() {
        createUser();

        final LoginRequest body = LoginRequest.builder()
                .email(EMAIL)
                .password("incorrect_password")
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Unauthorized");
        assertThat(problemDetail.getStatus()).isEqualTo(401);
        assertThat(problemDetail.getDetail()).isEqualTo("Bad credentials");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/login");
    }

    @SneakyThrows
    @Test
    public void login_emailNotFound() {
        createUser();

        final LoginRequest body = LoginRequest.builder()
                .email("fake_email@gmail.com")
                .password(PASSWORD)
                .build();
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
        final ProblemDetail problemDetail = objectMapper.readValue(result.getResponse().getContentAsString(), ProblemDetail.class);

        assertThat(problemDetail.getTitle()).isEqualTo("Unauthorized");
        assertThat(problemDetail.getStatus()).isEqualTo(401);
        assertThat(problemDetail.getDetail()).isEqualTo("Bad credentials");
        assertThat(problemDetail.getInstance().getPath()).isEqualTo("/auth/login");
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

    @SneakyThrows
    private JwtTokenModel parseToken(final String token) {
        final Base64.Decoder decoder = Base64.getDecoder();
        final String[] sections = token.split("\\.");
        final ObjectMapper objectMapper = new ObjectMapper();

        return JwtTokenModel.builder()
                .header(objectMapper.readValue(decoder.decode(sections[0]), Map.class))
                .payload(objectMapper.readValue(decoder.decode(sections[1]), Map.class))
                .build();
    }
}