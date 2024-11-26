package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceForbiddenException extends RuntimeException {
    private final String message;
    private final transient Object resource;

    public ResourceForbiddenException(String message, Object resource) {
        super(message);
        this.message = message;
        this.resource = resource;
    }
}
