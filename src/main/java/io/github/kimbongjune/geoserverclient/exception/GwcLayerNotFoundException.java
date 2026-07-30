package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache tile layer does not exist.
 */
public class GwcLayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public GwcLayerNotFoundException(String layerName) {
        super("GwcLayer", layerName, null);
    }

    public GwcLayerNotFoundException(String layerName, String responseBody) {
        super("GwcLayer", layerName, responseBody);
    }

    public String getLayerName() {
        return getResourceName();
    }
}
