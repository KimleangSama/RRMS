package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NO_CONTENT)
public class ResourceDeletedException extends RuntimeException {
    private final String message;

    public ResourceDeletedException(String resource, String at) {
        this.message = resource + " has been deleted at " + at + ".";
    }

}
