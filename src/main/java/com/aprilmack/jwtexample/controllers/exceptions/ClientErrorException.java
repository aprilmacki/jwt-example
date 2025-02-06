package com.aprilmack.jwtexample.controllers.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ClientErrorException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public ClientErrorException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }
}
