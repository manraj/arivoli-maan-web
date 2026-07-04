package com.arivoliacademy.maan.web.filter;

import com.arivoliacademy.maan.core.constants.MaanConstants;
import com.arivoliacademy.maan.core.util.IdUtils;
import com.arivoliacademy.maan.web.config.WebProperties;
import com.arivoliacademy.maan.web.constants.WebConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ensures every request has a correlation identifier for tracing and logging.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private final WebProperties webProperties;

    public CorrelationIdFilter(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!webProperties.getCorrelationId().isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerName = webProperties.getCorrelationId().getHeaderName();
        String correlationId = resolveCorrelationId(request, headerName);
        String requestId = resolveRequestId(request);

        MDC.put(WebConstants.MDC_CORRELATION_ID, correlationId);
        MDC.put(WebConstants.MDC_REQUEST_ID, requestId);
        response.setHeader(headerName, correlationId);
        response.setHeader(MaanConstants.HEADER_REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(WebConstants.MDC_CORRELATION_ID);
            MDC.remove(WebConstants.MDC_REQUEST_ID);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request, String headerName) {
        String existing = request.getHeader(headerName);
        if (existing != null && !existing.isBlank()) {
            return existing.trim();
        }
        return IdUtils.generateUuid();
    }

    private String resolveRequestId(HttpServletRequest request) {
        String existing = request.getHeader(MaanConstants.HEADER_REQUEST_ID);
        if (existing != null && !existing.isBlank()) {
            return existing.trim();
        }
        return IdUtils.generateUuid();
    }
}
