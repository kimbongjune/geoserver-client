package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested layer group does not exist in GeoServer.
 */
public class LayerGroupNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code LayerGroupNotFoundException} for the given layer group.
     * @param groupName the layer group name that was not found
     */
    public LayerGroupNotFoundException(String groupName) {
        super("LayerGroup", groupName, null);
    }

    /**
     * Constructs a {@code LayerGroupNotFoundException} with the server response body.
     * @param groupName    the layer group name that was not found
     * @param responseBody the raw HTTP response body
     */
    public LayerGroupNotFoundException(String groupName, String responseBody) {
        super("LayerGroup", groupName, responseBody);
    }
}
