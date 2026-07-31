package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when serialization (DTO → JSON/XML) or deserialization (JSON/XML → DTO) fails.
 */
public class SerializationException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code SerializationException} with a message and cause.
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
