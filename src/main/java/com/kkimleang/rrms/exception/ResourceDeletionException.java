package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NO_CONTENT)
public class ResourceDeletionException extends RuntimeException {
    private final String message;

    public ResourceDeletionException(String resource, String at) {
        this.message = resource + " has been deleted at " + at;
    }

}
