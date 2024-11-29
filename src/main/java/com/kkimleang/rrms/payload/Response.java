package com.kkimleang.rrms.payload;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> implements Serializable {
    private Status status;
    private int statusCode;
    private transient T payload;
    private transient Object errors;
    private boolean success = false;
    private Instant timestamp = Instant.now();
    private transient Object metadata;

    public static <T> Response<T> badRequest() {
        Response<T> response = new Response<>();
        response.setStatus(Status.BAD_REQUEST);
        response.setStatusCode(400);
        return response;
    }

    public static <T> Response<T> notAcceptable() {
        Response<T> response = new Response<>();
        response.setStatus(Status.NOT_ACCEPTABLE);
        response.setStatusCode(406);
        return response;
    }

    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.setStatus(Status.OK);
        response.setStatusCode(200);
        response.setSuccess(true);
        return response;
    }

    public static <T> Response<T> created() {
        Response<T> response = new Response<>();
        response.setStatus(Status.CREATED);
        response.setSuccess(true);
        response.setStatusCode(201);
        return response;
    }

    public static <T> Response<T> unauthorized() {
        Response<T> response = new Response<>();
        response.setStatus(Status.UNAUTHORIZED);
        response.setStatusCode(401);
        return response;
    }

    public static <T> Response<T> wrongCredentials() {
        Response<T> response = new Response<>();
        response.setStatus(Status.WRONG_CREDENTIALS);
        response.setStatusCode(401);
        return response;
    }

    public static <T> Response<T> accessDenied() {
        Response<T> response = new Response<>();
        response.setStatus(Status.ACCESS_DENIED);
        response.setStatusCode(403);
        return response;
    }

    public static <T> Response<T> exception() {
        Response<T> response = new Response<>();
        response.setStatus(Status.EXCEPTION);
        response.setStatusCode(500);
        return response;
    }

    public static <T> Response<T> invalidToken() {
        Response<T> response = new Response<>();
        response.setStatus(Status.VALIDATION_EXCEPTION);
        response.setStatusCode(400);
        return response;
    }

    public static <T> Response<T> notFound() {
        Response<T> response = new Response<>();
        response.setStatus(Status.NOT_FOUND);
        response.setStatusCode(404);
        return response;
    }

    public static <T> Response<T> generationNotAvailable() {
        Response<T> response = new Response<>();
        response.setStatus(Status.GENERATION_NOT_AVAILABLE);
        response.setStatusCode(503);
        return response;
    }

    public static <T> Response<T> duplicateEntity() {
        Response<T> response = new Response<>();
        response.setStatus(Status.DUPLICATE_ENTITY);
        response.setStatusCode(409);
        return response;
    }

    public enum Status {
        GENERATION_NOT_AVAILABLE, NOT_ACCEPTABLE, OK, BAD_REQUEST, UNAUTHORIZED, VALIDATION_EXCEPTION, EXCEPTION, WRONG_CREDENTIALS, ACCESS_DENIED, NOT_FOUND, CREATED, DUPLICATE_ENTITY
    }

    @Getter
    @Accessors(chain = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageMetadata {
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final int number;

        public PageMetadata(int size, long totalElements, int totalPages, int number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }
    }

}