package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final transient Object fieldValue;

    public ResourceNotFoundException(String resourceName, Object fieldName) {
        super(String.format("%s not found with %s.", resourceName, fieldName));
        this.resourceName = resourceName;
        this.fieldValue = fieldName;
    }

}
