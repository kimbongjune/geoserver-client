package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested URL check entry does not exist in GeoServer (HTTP 404).
 */
public class UrlCheckNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public UrlCheckNotFoundException(String name, String responseBody) {
        super("UrlCheck", name, responseBody);
    }

    public String getCheckName() {
        return getResourceName();
    }
}
