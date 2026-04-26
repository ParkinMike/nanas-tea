package com.parkin.mike.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentReturnsBadRequest() {
        var response = handler.handleIllegalArgument(new IllegalArgumentException("bad input"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("bad input");
    }

    @Test
    void handleValidationReturnsFieldMessage() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "date", "date is required"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter(), bindingResult);

        var response = handler.handleValidation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("date is required");
    }

    @Test
    void handleValidationFallsBackWhenNoFieldError() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter(), bindingResult);

        var response = handler.handleValidation(exception);

        assertThat(response.getBody()).isEqualTo("Invalid request");
    }

    @Test
    void handleConstraintViolationReturnsBadRequest() {
        var response = handler.handleConstraintViolation(new ConstraintViolationException("bad", null));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid request");
    }

    @Test
    void handleIllegalStateReturnsInternalServerError() {
        var response = handler.handleIllegalState(new IllegalStateException("nope"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Application is not configured securely");
    }

    private MethodParameter methodParameter() throws Exception {
        Method method = TestRequest.class.getDeclaredMethod("submit", String.class);
        return new MethodParameter(method, 0);
    }

    @SuppressWarnings("unused")
    private static class TestRequest {
        void submit(String date) {
        }
    }
}
