package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested cascading WMTS layer does not exist in GeoServer.
 */
public class WmtsLayerNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public WmtsLayerNotFoundException(String workspaceName, String storeName, String layerName) {
        super("WmtsLayer", buildKey(workspaceName, storeName, layerName), null);
    }

    public WmtsLayerNotFoundException(String workspaceName, String storeName, String layerName, String responseBody) {
        super("WmtsLayer", buildKey(workspaceName, storeName, layerName), responseBody);
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
