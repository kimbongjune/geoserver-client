package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when authentication fails (401 Unauthorized) or
 * authorization is denied (403 Forbidden).
 */
public class AuthenticationException extends GeoServerResponseException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(int statusCode, String message, String responseBody) {
        super(statusCode, message, responseBody);
    }
}
