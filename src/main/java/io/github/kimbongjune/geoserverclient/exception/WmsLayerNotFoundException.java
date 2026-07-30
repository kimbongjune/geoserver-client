package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested cascading WMS layer does not exist in GeoServer.
 */
public class WmsLayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public WmsLayerNotFoundException(String workspaceName, String storeName, String layerName) {
        super("WmsLayer", buildKey(workspaceName, storeName, layerName), null);
    }

    public WmsLayerNotFoundException(String workspaceName, String storeName, String layerName, String responseBody) {
        super("WmsLayer", buildKey(workspaceName, storeName, layerName), responseBody);
    }

    private static String buildKey(String ws, String store, String layer) {
        if (store != null) {
            return ws + "/" + store + "/" + layer;
        }
        return ws + "/" + layer;
    }

    /** "workspace/store/layer"  "workspace/layer" . */
    public String getLayerName() {
        return getResourceName();
    }
}
