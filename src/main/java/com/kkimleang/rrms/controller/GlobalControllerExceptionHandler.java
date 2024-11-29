package com.kkimleang.rrms.controller;


import com.kkimleang.rrms.payload.*;
import java.util.*;
import lombok.extern.slf4j.*;
import org.hibernate.exception.*;
import org.springframework.dao.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;
import org.springframework.web.multipart.*;

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

    @ExceptionHandler(ConstraintViolationException.class)
    public Response<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        return Response.duplicateEntity().setErrors(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return Response.badRequest().setErrors(ex.getMessage());
    }
}
