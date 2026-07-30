package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested authentication filter does not exist in GeoServer.
 */
public class AuthFilterNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public AuthFilterNotFoundException(String filterName) {
        super("AuthFilter", filterName, null);
    }

    public AuthFilterNotFoundException(String filterName, String responseBody) {
        super("AuthFilter", filterName, responseBody);
    }

    public String getFilterName() {
        return getResourceName();
    }
}
