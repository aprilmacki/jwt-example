package com.aprilmack.jwtexample.services.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class TokenModel {
    private final String token;
    private final Instant expiresAt;
}
