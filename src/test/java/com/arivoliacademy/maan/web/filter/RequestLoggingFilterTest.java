package com.arivoliacademy.maan.web.filter;

import com.arivoliacademy.maan.web.config.WebProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RequestLoggingFilterTest {

    @Test
    void logsAndContinuesFilterChain() throws Exception {
        WebProperties properties = new WebProperties();
        RequestLoggingFilter filter = new RequestLoggingFilter(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        request.setQueryString("verbose=true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertThatCode(() -> filter.doFilter(request, response, chain)).doesNotThrowAnyException();

        verify(chain).doFilter(request, response);
    }

    @Test
    void skipsLoggingWhenDisabled() throws Exception {
        WebProperties properties = new WebProperties();
        properties.getRequestLogging().setEnabled(false);
        RequestLoggingFilter filter = new RequestLoggingFilter(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
