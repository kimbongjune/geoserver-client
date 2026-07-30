package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested layer does not exist in GeoServer.
 */
public class LayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public LayerNotFoundException(String layerName) {
        super("Layer", layerName, null);
    }

    public LayerNotFoundException(String layerName, String responseBody) {
        super("Layer", layerName, responseBody);
    }
}
