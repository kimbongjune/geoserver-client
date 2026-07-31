package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested authentication filter does not exist in GeoServer.
 */
public class AuthFilterNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code AuthFilterNotFoundException} for the given filter name.
     *
     * @param filterName the name of the authentication filter that was not found
     */
    public AuthFilterNotFoundException(String filterName) {
        super("AuthFilter", filterName, null);
    }

    /**
     * Constructs an {@code AuthFilterNotFoundException} with the given filter name and response body.
     *
     * @param filterName   the name of the authentication filter that was not found
     * @param responseBody the raw response body from GeoServer, or {@code null}
     */
    public AuthFilterNotFoundException(String filterName, String responseBody) {
        super("AuthFilter", filterName, responseBody);
    }

    /**
     * Returns the name of the authentication filter that was not found.
     *
     * @return the filter name
     */
    public String getFilterName() {
        return getResourceName();
    }
}
