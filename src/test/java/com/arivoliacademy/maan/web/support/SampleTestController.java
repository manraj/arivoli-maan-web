package com.arivoliacademy.maan.web.support;

import com.arivoliacademy.maan.core.api.ApiResponse;
import com.arivoliacademy.maan.core.exception.ResourceNotFoundException;
import com.arivoliacademy.maan.web.helper.ApiResponseHelper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
class SampleTestController {

    @GetMapping("/hello")
    ApiResponse<String> hello() {
        return ApiResponse.success("hello");
    }

    @GetMapping("/not-found")
    void notFound() {
        throw new ResourceNotFoundException("Item", 1);
    }

    @PostMapping("/validate")
    ResponseEntity<ApiResponse<String>> validate(@Valid @RequestBody SampleRequest request) {
        return ApiResponseHelper.ok("validated", request.name());
    }

    record SampleRequest(@NotBlank String name) {
    }
}
