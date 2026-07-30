package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested authentication provider does not exist in GeoServer.
 */
public class AuthProviderNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public AuthProviderNotFoundException(String providerName) {
        super("AuthProvider", providerName, null);
    }

    public AuthProviderNotFoundException(String providerName, String responseBody) {
        super("AuthProvider", providerName, responseBody);
    }

    public String getProviderName() {
        return getResourceName();
    }
}
