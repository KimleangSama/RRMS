package com.kkimleang.rrms.controller;

import com.kkimleang.rrms.exception.ResourceDeletionException;
import com.kkimleang.rrms.exception.ResourceDuplicationException;
import com.kkimleang.rrms.exception.ResourceForbiddenException;
import com.kkimleang.rrms.exception.ResourceNotFoundException;
import com.kkimleang.rrms.payload.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        } catch (ResourceDeletionException e) {
            log.error("{}: {}", errorMessage, e.getMessage(), e);
            return Response.<T>badRequest().setErrors(e.getMessage());
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
