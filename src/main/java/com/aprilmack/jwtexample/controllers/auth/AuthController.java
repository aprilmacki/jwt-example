package com.aprilmack.jwtexample.controllers.auth;

import com.aprilmack.jwtexample.auth.UserDetailsModel;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginResponse;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupResponse;
import com.aprilmack.jwtexample.controllers.exceptions.ClientErrorException;
import com.aprilmack.jwtexample.db.entities.UserEntity;
import com.aprilmack.jwtexample.db.repositories.UserRepository;
import com.aprilmack.jwtexample.services.JwtService;
import com.aprilmack.jwtexample.services.model.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(
            final JwtService jwtService,
            final UserRepository userRepository,
            final AuthenticationManager authenticationManager,
            final PasswordEncoder passwordEncoder,
            final Clock clock) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        validateSignupRequest(signupRequest);

        final Optional<UserEntity> maybeExistingUserEntity = userRepository.findByEmail(signupRequest.getEmail());
        if (maybeExistingUserEntity.isPresent()) {
            throw new ClientErrorException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        final UserEntity userEntity = new UserEntity(
                signupRequest.getFullName(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                Instant.now(clock)
        );
        userRepository.saveAndFlush(userEntity);

        // TODO: MapStruct
        return ResponseEntity.ok(
                SignupResponse.builder()
                        .id(userEntity.getId())
                        .email(userEntity.getEmail())
                        .fullName(userEntity.getFullName())
                        .createdAt(userEntity.getCreatedAt())
                        .updatedAt(userEntity.getUpdatedAt())
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        final UserDetailsModel userDetails = (UserDetailsModel) auth.getPrincipal();
        final TokenModel jwtToken = jwtService.generateToken(userDetails);

        final LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .expiresAt(jwtToken.getExpiresAt())
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    private static void validateSignupRequest(final SignupRequest signupRequest) {
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isEmpty()) {
            throw new ClientErrorException(HttpStatus.BAD_REQUEST, "Field email is required");
        }
        if (signupRequest.getPassword() == null || signupRequest.getPassword().isEmpty()) {
            throw new ClientErrorException(HttpStatus.BAD_REQUEST, "Field password is required");
        }
        if (signupRequest.getFullName() == null || signupRequest.getFullName().isEmpty()) {
            throw new ClientErrorException(HttpStatus.BAD_REQUEST, "Field fullName is required");
        }
    }
}
