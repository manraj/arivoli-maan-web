package com.arivoliacademy.maan.web.support;

import com.arivoliacademy.maan.core.constants.MaanConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = WebTestApplication.class)
@AutoConfigureMockMvc
class SampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsStandardApiResponse() throws Exception {
        mockMvc.perform(get("/sample/hello"))
                .andExpect(status().isOk())
                .andExpect(header().exists(MaanConstants.HEADER_CORRELATION_ID))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("hello")));
    }

    @Test
    void notFoundReturnsStandardErrorResponse() throws Exception {
        mockMvc.perform(get("/sample/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.code", is(MaanConstants.ERROR_NOT_FOUND)));
    }

    @Test
    void validationFailureReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/sample/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(MaanConstants.ERROR_BAD_REQUEST)))
                .andExpect(jsonPath("$.fieldErrors[0].field", is("name")));
    }
}
