package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested namespace does not exist in GeoServer.
 */
public class NamespaceNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code NamespaceNotFoundException} for the given prefix.
     * @param prefix the namespace prefix that was not found
     */
    public NamespaceNotFoundException(String prefix) {
        super("Namespace", prefix, null);
    }

    /**
     * Constructs a {@code NamespaceNotFoundException} with the server response body.
     * @param prefix       the namespace prefix that was not found
     * @param responseBody the raw HTTP response body
     */
    public NamespaceNotFoundException(String prefix, String responseBody) {
        super("Namespace", prefix, responseBody);
    }

    /** @return the namespace prefix that was not found */
    public String getPrefix() {
        return getResourceName();
    }
}
