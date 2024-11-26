package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceEditionException extends RuntimeException {
    private final String message;

    public ResourceEditionException(String message) {
        this.message = message;
    }

}
