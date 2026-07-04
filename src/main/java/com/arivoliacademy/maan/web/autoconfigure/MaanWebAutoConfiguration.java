package com.arivoliacademy.maan.web.autoconfigure;

import com.arivoliacademy.maan.web.config.WebProperties;
import com.arivoliacademy.maan.web.exception.GlobalExceptionHandler;
import com.arivoliacademy.maan.web.exception.ValidationExceptionHandler;
import com.arivoliacademy.maan.web.filter.CorrelationIdFilter;
import com.arivoliacademy.maan.web.filter.RequestLoggingFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Auto-configuration for Maan web/API standards.
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(WebProperties.class)
public class MaanWebAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "maan.web.correlation-id", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter(WebProperties webProperties) {
        FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorrelationIdFilter(webProperties));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    @ConditionalOnProperty(prefix = "maan.web.request-logging", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter(WebProperties webProperties) {
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestLoggingFilter(webProperties));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public ValidationExceptionHandler validationExceptionHandler() {
        return new ValidationExceptionHandler();
    }
}
