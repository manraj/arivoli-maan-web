package com.arivoliacademy.maan.web.exception;

import com.arivoliacademy.maan.core.api.ErrorResponse;
import com.arivoliacademy.maan.core.constants.MaanConstants;
import com.arivoliacademy.maan.core.exception.ResourceNotFoundException;
import com.arivoliacademy.maan.web.constants.WebConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleMaanExceptionMapsToErrorResponse() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Course", 42);

        ResponseEntity<ErrorResponse> response = handler.handleMaanException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().code()).isEqualTo(MaanConstants.ERROR_NOT_FOUND);
        assertThat(response.getBody().message()).contains("Course");
    }

    @Test
    void handleUnexpectedExceptionReturnsInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(WebConstants.ERROR_INTERNAL);
        assertThat(response.getBody().message()).isEqualTo(WebConstants.DEFAULT_INTERNAL_ERROR_MESSAGE);
    }
}
