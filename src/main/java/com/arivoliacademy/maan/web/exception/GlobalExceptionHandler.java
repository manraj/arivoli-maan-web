package com.arivoliacademy.maan.web.exception;

import com.arivoliacademy.maan.core.api.ErrorResponse;
import com.arivoliacademy.maan.core.exception.MaanException;
import com.arivoliacademy.maan.web.constants.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps framework and unexpected exceptions to standard error responses.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MaanException.class)
    public ResponseEntity<ErrorResponse> handleMaanException(MaanException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getHttpStatus());
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                exception.getErrorCode()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error", exception);
        ErrorResponse body = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                WebConstants.DEFAULT_INTERNAL_ERROR_MESSAGE,
                WebConstants.ERROR_INTERNAL
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
