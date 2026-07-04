package com.arivoliacademy.maan.web.filter;

import com.arivoliacademy.maan.web.config.WebProperties;
import com.arivoliacademy.maan.web.constants.WebConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs inbound HTTP requests and response status with elapsed time.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final WebProperties webProperties;

    public RequestLoggingFilter(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!webProperties.getRequestLogging().isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        String requestLine = buildRequestLine(request);

        log.debug("Incoming request: {}", requestLine);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("Completed request: {} status={} durationMs={} correlationId={}",
                    requestLine,
                    response.getStatus(),
                    duration,
                    safeMdcValue(WebConstants.MDC_CORRELATION_ID));
        }
    }

    private String buildRequestLine(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod()).append(' ').append(request.getRequestURI());

        if (webProperties.getRequestLogging().isIncludeQueryString()) {
            String query = request.getQueryString();
            if (query != null && !query.isBlank()) {
                builder.append('?').append(query);
            }
        }

        if (webProperties.getRequestLogging().isIncludeClientInfo()) {
            builder.append(" client=").append(request.getRemoteAddr());
        }

        return builder.toString();
    }

    private String safeMdcValue(String key) {
        String value = org.slf4j.MDC.get(key);
        return value == null ? "-" : value;
    }
}
