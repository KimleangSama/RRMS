package com.kkimleang.rrms.exception;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceException extends RuntimeException {
    private final String resourceName;
    private final String reason;

    public ResourceException(String resourceName, String reason) {
        super(String.format("%s is with internal server error with reason: '%s'", resourceName, reason));
        this.resourceName = resourceName;
        this.reason = reason;
    }

}
