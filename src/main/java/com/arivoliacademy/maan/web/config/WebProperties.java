package com.arivoliacademy.maan.web.config;

import com.arivoliacademy.maan.core.constants.MaanConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Maan web auto-configuration.
 */
@ConfigurationProperties(prefix = "maan.web")
public class WebProperties {

    private final CorrelationId correlationId = new CorrelationId();
    private final RequestLogging requestLogging = new RequestLogging();

    public CorrelationId getCorrelationId() {
        return correlationId;
    }

    public RequestLogging getRequestLogging() {
        return requestLogging;
    }

    public static class CorrelationId {

        private boolean enabled = true;
        private String headerName = MaanConstants.HEADER_CORRELATION_ID;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

    public static class RequestLogging {

        private boolean enabled = true;
        private boolean includeQueryString = true;
        private boolean includeClientInfo = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isIncludeQueryString() {
            return includeQueryString;
        }

        public void setIncludeQueryString(boolean includeQueryString) {
            this.includeQueryString = includeQueryString;
        }

        public boolean isIncludeClientInfo() {
            return includeClientInfo;
        }

        public void setIncludeClientInfo(boolean includeClientInfo) {
            this.includeClientInfo = includeClientInfo;
        }
    }
}
