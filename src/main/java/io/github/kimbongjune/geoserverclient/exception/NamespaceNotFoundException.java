package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested namespace does not exist in GeoServer.
 */
public class NamespaceNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public NamespaceNotFoundException(String prefix) {
        super("Namespace", prefix, null);
    }

    public NamespaceNotFoundException(String prefix, String responseBody) {
        super("Namespace", prefix, responseBody);
    }

    public String getPrefix() {
        return getResourceName();
    }
}
