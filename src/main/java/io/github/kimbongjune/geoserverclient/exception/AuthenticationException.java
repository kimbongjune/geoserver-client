package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when authentication fails (401 Unauthorized) or
 * authorization is denied (403 Forbidden).
 */
public class AuthenticationException extends GeoServerResponseException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code AuthenticationException} with the given HTTP status, message, and response body.
     *
     * @param statusCode   the HTTP status code (401 or 403)
     * @param message      human-readable error description
     * @param responseBody the raw response body from GeoServer, or {@code null}
     */
    public AuthenticationException(int statusCode, String message, String responseBody) {
        super(statusCode, message, responseBody);
    }
}
