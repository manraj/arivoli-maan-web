package com.arivoliacademy.maan.web.exception;

import com.arivoliacademy.maan.core.api.ErrorResponse;
import com.arivoliacademy.maan.core.api.FieldErrorDetail;
import com.arivoliacademy.maan.core.constants.MaanConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Maps validation failures to standard field-level error responses.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<FieldErrorDetail> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorDetail)
                .toList();

        return buildValidationResponse(fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<FieldErrorDetail> fieldErrors = exception.getConstraintViolations().stream()
                .map(this::toFieldErrorDetail)
                .toList();

        return buildValidationResponse(fieldErrors);
    }

    private ResponseEntity<ErrorResponse> buildValidationResponse(List<FieldErrorDetail> fieldErrors) {
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                MaanConstants.ERROR_BAD_REQUEST,
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()
        );
    }

    private FieldErrorDetail toFieldErrorDetail(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath() == null ? null : violation.getPropertyPath().toString();
        return new FieldErrorDetail(field, violation.getMessage(), violation.getInvalidValue());
    }
}
