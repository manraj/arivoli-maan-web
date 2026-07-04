package com.arivoliacademy.maan.web.constants;

/**
 * Web layer constants for Maan framework consumers.
 */
public final class WebConstants {

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_REQUEST_ID = "requestId";

    public static final String ERROR_INTERNAL = "MAAN-500";
    public static final String ERROR_VALIDATION = "MAAN-422";

    public static final String DEFAULT_INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";

    private WebConstants() {
    }
}
