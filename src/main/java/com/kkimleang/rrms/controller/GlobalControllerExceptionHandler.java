package com.kkimleang.rrms.controller;


import com.kkimleang.rrms.payload.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<Object> handleValidationExceptions(MethodArgumentTypeMismatchException ex) {
        return Response.badRequest()
                .setErrors(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return Response.badRequest()
                .setErrors(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Response<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return Response.wrongCredentials().setErrors(ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Response<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return Response.badRequest().setErrors("File size is too large!");
    }
}
