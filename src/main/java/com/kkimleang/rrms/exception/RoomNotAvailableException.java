package com.kkimleang.rrms.exception;

import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Getter
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class RoomNotAvailableException extends RuntimeException {
    private final String reason;

    public RoomNotAvailableException(String reason) {
        super(String.format("Room is not available because %s.", reason));
        this.reason = reason;
    }
}
