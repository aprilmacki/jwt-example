package com.aprilmack.jwtexample.db.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Table(name = "USERS")
@Entity
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private long id;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "EMAIL", unique = true, length = 100, nullable = false)
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "CREATED_AT", updatable = false)
    private Instant createdAt;

    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

    public UserEntity(
            final String fullName,
            final String email,
            final String password,
            final Instant now
    ) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.createdAt = now;
        this.updatedAt = now;
    }
}
