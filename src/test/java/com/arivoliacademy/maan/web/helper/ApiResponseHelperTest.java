package com.arivoliacademy.maan.web.helper;

import com.arivoliacademy.maan.core.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseHelperTest {

    @Test
    void okReturnsSuccessResponse() {
        ResponseEntity<ApiResponse<String>> response = ApiResponseHelper.ok("hello");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data()).isEqualTo("hello");
    }

    @Test
    void createdReturns201Response() {
        ResponseEntity<ApiResponse<Integer>> response = ApiResponseHelper.created(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Created");
    }
}
