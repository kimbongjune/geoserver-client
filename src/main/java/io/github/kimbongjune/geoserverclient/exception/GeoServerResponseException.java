package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when GeoServer returns an HTTP error response (4xx, 5xx).
 * Carries the status code and response body for debugging.
 */
public class GeoServerResponseException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs a {@code GeoServerResponseException} with the given status code and details.
     * @param statusCode   the HTTP status code
     * @param message      the error message
     * @param responseBody the raw response body, or {@code null}
     */
    public GeoServerResponseException(int statusCode, String message, String responseBody) {
        super(String.format("HTTP %d: %s", statusCode, message));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /** @return the HTTP status code */
    public int getStatusCode() {
        return statusCode;
    }

    /** @return the raw HTTP response body, or {@code null} */
    public String getResponseBody() {
        return responseBody;
    }
}
