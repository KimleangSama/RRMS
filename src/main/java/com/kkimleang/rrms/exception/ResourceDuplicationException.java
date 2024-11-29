package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceDuplicationException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;

    public ResourceDuplicationException(String message, String fieldName) {
        super(String.format("%s with %s.", message, fieldName));
        this.resourceName = message;
        this.fieldName = fieldName;
    }

}
