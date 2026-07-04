package com.arivoliacademy.maan.web.exception;

import com.arivoliacademy.maan.core.api.ErrorResponse;
import com.arivoliacademy.maan.core.constants.MaanConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationExceptionHandlerTest {

    private ValidationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ValidationExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValidReturnsFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "invalid@email", false, null, null, "must be valid"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(MaanConstants.ERROR_BAD_REQUEST);
        assertThat(response.getBody().fieldErrors()).hasSize(1);
        assertThat(response.getBody().fieldErrors().getFirst().field()).isEqualTo("email");
    }

    @Test
    void handleConstraintViolationReturnsFieldErrors() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("createRequest.email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        when(violation.getInvalidValue()).thenReturn("");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().fieldErrors()).hasSize(1);
        assertThat(response.getBody().fieldErrors().getFirst().field()).isEqualTo("createRequest.email");
    }
}
