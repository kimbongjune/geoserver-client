package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested style does not exist in GeoServer.
 */
public class StyleNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public StyleNotFoundException(String styleName) {
        super("Style", styleName, null);
    }

    public StyleNotFoundException(String styleName, String responseBody) {
        super("Style", styleName, responseBody);
    }
}
