package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a required parameter is missing, null, or invalid.
 */
public class InvalidParameterException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final String parameterName;

    /**
     * Constructs an {@code InvalidParameterException} for the given parameter.
     * @param parameterName the name of the invalid parameter
     * @param reason        the reason the parameter is invalid
     */
    public InvalidParameterException(String parameterName, String reason) {
        super(String.format("Invalid parameter '%s': %s", parameterName, reason));
        this.parameterName = parameterName;
    }

    /**
     * Returns the name of the invalid parameter.
     * @return the parameter name
     */
    public String getParameterName() {
        return parameterName;
    }
}
