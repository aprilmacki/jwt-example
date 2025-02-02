package com.aprilmack.jwtexample.controllers.users.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    final long id;
    final String email;
    final String fullName;
    final Instant createdAt;
    final Instant updatedAt;
}
