package com.aprilmack.jwtexample.controllers.users;

import com.aprilmack.jwtexample.auth.UserDetailsModel;
import com.aprilmack.jwtexample.controllers.users.dtos.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UsersController {
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetailsModel userDetails = (UserDetailsModel) authentication.getPrincipal();

        // TODO: MapStruct
        final UserResponse userResponse = UserResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .fullName(userDetails.getFullName())
                .createdAt(userDetails.getCreatedAt())
                .updatedAt(userDetails.getUpdatedAt())
                .build();
        return ResponseEntity.ok(userResponse);
    }
}
