package io.github.kimbongjune.geoserverclient.exception;

/**
 * Base exception for all GeoServer REST API client errors.
 * <p>
 * Unchecked (RuntimeException) to avoid forcing callers to handle every error,
 * while still providing a clear exception hierarchy for selective catching.
 *
 * <pre>
 * GeoServerException
 *  GeoServerResponseException  (HTTP 4xx/5xx)
 *     ResourceNotFoundException       (404)
 *     ResourceAlreadyExistsException  (409 / duplicate)
 *     AuthenticationException         (401/403)
 *  ConnectionException         (network/timeout)
 *  SerializationException      (JSON/XML parse)
 *  InvalidParameterException   (missing/invalid params)
 * </pre>
 */
public class GeoServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GeoServerException(String message) {
        super(message);
    }

    public GeoServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
