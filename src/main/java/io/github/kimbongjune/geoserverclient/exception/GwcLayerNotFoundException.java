package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache tile layer does not exist.
 */
public class GwcLayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code GwcLayerNotFoundException} for the given layer name.
     * @param layerName the GWC layer name
     */
    public GwcLayerNotFoundException(String layerName) {
        super("GwcLayer", layerName, null);
    }

    /**
     * Constructs a {@code GwcLayerNotFoundException} with the raw response body.
     * @param layerName    the GWC layer name
     * @param responseBody the raw HTTP response body, or {@code null}
     */
    public GwcLayerNotFoundException(String layerName, String responseBody) {
        super("GwcLayer", layerName, responseBody);
    }

    /**
     * Returns the GWC layer name that was not found.
     * @return the layer name
     */
    public String getLayerName() {
        return getResourceName();
    }
}
