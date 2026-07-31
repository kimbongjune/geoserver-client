package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested authentication provider does not exist in GeoServer.
 */
public class AuthProviderNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code AuthProviderNotFoundException} for the given provider name.
     *
     * @param providerName the name of the authentication provider that was not found
     */
    public AuthProviderNotFoundException(String providerName) {
        super("AuthProvider", providerName, null);
    }

    /**
     * Constructs an {@code AuthProviderNotFoundException} with the given provider name and response body.
     *
     * @param providerName the name of the authentication provider that was not found
     * @param responseBody the raw response body from GeoServer, or {@code null}
     */
    public AuthProviderNotFoundException(String providerName, String responseBody) {
        super("AuthProvider", providerName, responseBody);
    }

    /**
     * Returns the name of the authentication provider that was not found.
     *
     * @return the provider name
     */
    public String getProviderName() {
        return getResourceName();
    }
}
