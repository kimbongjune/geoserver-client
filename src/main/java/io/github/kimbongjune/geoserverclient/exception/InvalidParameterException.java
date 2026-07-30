package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a required parameter is missing, null, or invalid.
 */
public class InvalidParameterException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final String parameterName;

    public InvalidParameterException(String parameterName, String reason) {
        super(String.format("Invalid parameter '%s': %s", parameterName, reason));
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
