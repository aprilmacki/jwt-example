package com.aprilmack.jwtexample.controllers.auth;

import com.aprilmack.jwtexample.auth.UserDetailsModel;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.LoginResponse;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupRequest;
import com.aprilmack.jwtexample.controllers.auth.dtos.SignupResponse;
import com.aprilmack.jwtexample.db.entities.UserEntity;
import com.aprilmack.jwtexample.db.repositories.UserRepository;
import com.aprilmack.jwtexample.services.JwtService;
import com.aprilmack.jwtexample.services.model.TokenModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            final JwtService jwtService,
            final UserRepository userRepository,
            final AuthenticationManager authenticationManager
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        final Optional<UserEntity> maybeExistingUserEntity = userRepository.findByEmail(signupRequest.getEmail());
        if (maybeExistingUserEntity.isPresent()) {
            // TODO: Use ProblemDetail and exception handlers
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        final UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(signupRequest.getPassword());
        userEntity.setFullName(signupRequest.getFullName());
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
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        final Optional<UserEntity> maybeUserEntity = userRepository.findByEmail(loginRequest.getEmail());
        if (maybeUserEntity.isEmpty()) {
            // TODO: Use ProblemDetail and exception handlers
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        final UserEntity userEntity = maybeUserEntity.get();
        // TODO: MapStruct
        final UserDetails userDetails = UserDetailsModel.builder()
                .id(userEntity.getId())
                .password(userEntity.getPassword())
                .email(userEntity.getEmail())
                .fullName(userEntity.getFullName())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();

        final TokenModel jwtToken = jwtService.generateToken(userDetails);

        final LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .expiresAt(jwtToken.getExpiresAt())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
