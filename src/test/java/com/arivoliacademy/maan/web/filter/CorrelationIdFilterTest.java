package com.arivoliacademy.maan.web.filter;

import com.arivoliacademy.maan.core.constants.MaanConstants;
import com.arivoliacademy.maan.web.config.WebProperties;
import com.arivoliacademy.maan.web.constants.WebConstants;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CorrelationIdFilterTest {

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void generatesCorrelationIdWhenMissing() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter(new WebProperties());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/items");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(MaanConstants.HEADER_CORRELATION_ID)).isNotBlank();
        assertThat(response.getHeader(MaanConstants.HEADER_REQUEST_ID)).isNotBlank();
        verify(chain).doFilter(request, response);
    }

    @Test
    void reusesIncomingCorrelationId() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter(new WebProperties());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/items");
        request.addHeader(MaanConstants.HEADER_CORRELATION_ID, "corr-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(MaanConstants.HEADER_CORRELATION_ID)).isEqualTo("corr-123");
    }

    @Test
    void setsMdcDuringRequest() throws Exception {
        CorrelationIdFilter filter = new CorrelationIdFilter(new WebProperties());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/items");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) ->
                assertThat(MDC.get(WebConstants.MDC_CORRELATION_ID)).isNotBlank());

        assertThat(MDC.get(WebConstants.MDC_CORRELATION_ID)).isNull();
    }
}
