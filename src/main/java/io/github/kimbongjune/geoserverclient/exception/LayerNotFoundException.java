package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested layer does not exist in GeoServer.
 */
public class LayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code LayerNotFoundException} for the given layer.
     * @param layerName the layer name that was not found
     */
    public LayerNotFoundException(String layerName) {
        super("Layer", layerName, null);
    }

    /**
     * Constructs a {@code LayerNotFoundException} with the server response body.
     * @param layerName    the layer name that was not found
     * @param responseBody the raw HTTP response body
     */
    public LayerNotFoundException(String layerName, String responseBody) {
        super("Layer", layerName, responseBody);
    }
}
