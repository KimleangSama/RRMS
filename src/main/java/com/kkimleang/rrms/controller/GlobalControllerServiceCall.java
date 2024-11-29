package com.kkimleang.rrms.controller;

import com.kkimleang.rrms.exception.*;
import com.kkimleang.rrms.payload.*;
import lombok.extern.slf4j.*;
import org.hibernate.exception.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
public class GlobalControllerServiceCall {
    public <T> Response<T> executeServiceCall(ServiceOperation<T> operation, String errorMessage) {
        try {
            T result = operation.execute();
            return Response.<T>ok().setPayload(result);
        } catch (ResourceNotFoundException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>notFound().setErrors(e.getMessage());
        } catch (ResourceDuplicationException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>duplicateEntity().setErrors(e.getMessage());
        } catch (ResourceForbiddenException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>accessDenied().setErrors(e.getMessage());
        } catch (ResourceDeletionException | ConstraintViolationException | DataIntegrityViolationException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>badRequest().setErrors(e.getMessage());
        } catch (RoomNotAvailableException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>notAcceptable().setErrors(e.getMessage());
        } catch (Exception e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>exception().setErrors(e.getMessage());
        }
    }

    @FunctionalInterface
    public interface ServiceOperation<T> {
        T execute() throws Exception;
    }
}
