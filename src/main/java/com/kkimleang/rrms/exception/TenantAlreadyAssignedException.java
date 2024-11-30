package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class TenantAlreadyAssignedException extends ResourceDuplicationException {
    private final String message;

    public TenantAlreadyAssignedException(String message) {
        super(message, "Tenant");
        this.message = message;
    }
}
