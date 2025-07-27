package com.youtube.be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum AuthError {

    UNAUTHORIZED(401, "Unauthorized"),
    USER_NOT_FOUND(404, "User not found")

    ;

    private final int errorCode;
    @Setter
    private String errorMessage;
}
