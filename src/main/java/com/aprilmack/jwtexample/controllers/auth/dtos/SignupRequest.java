package com.aprilmack.jwtexample.controllers.auth.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequest {
    private final String email;
    private final String password;
    private final String fullName;
}
