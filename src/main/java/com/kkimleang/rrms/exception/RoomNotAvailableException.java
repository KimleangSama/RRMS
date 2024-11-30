package com.kkimleang.rrms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class RoomNotAvailableException extends RuntimeException {
    private final String reason;

    public RoomNotAvailableException(String reason) {
        super(String.format("Room is not available because %s.", reason));
        this.reason = reason;
    }
}
