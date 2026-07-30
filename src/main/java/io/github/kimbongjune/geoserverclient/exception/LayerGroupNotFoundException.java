package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested layer group does not exist in GeoServer.
 */
public class LayerGroupNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public LayerGroupNotFoundException(String groupName) {
        super("LayerGroup", groupName, null);
    }

    public LayerGroupNotFoundException(String groupName, String responseBody) {
        super("LayerGroup", groupName, responseBody);
    }
}
