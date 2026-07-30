package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when GeoServer returns an HTTP error response (4xx, 5xx).
 * Carries the status code and response body for debugging.
 */
public class GeoServerResponseException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String responseBody;

    public GeoServerResponseException(int statusCode, String message, String responseBody) {
        super(String.format("HTTP %d: %s", statusCode, message));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
