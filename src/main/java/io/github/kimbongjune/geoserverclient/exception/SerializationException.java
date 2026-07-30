package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when serialization (DTO → JSON/XML) or deserialization (JSON/XML → DTO) fails.
 */
public class SerializationException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
