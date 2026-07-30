package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested cascading WMTS store does not exist in GeoServer.
 */
public class WmtsStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public WmtsStoreNotFoundException(String workspaceName, String storeName) {
        super("WmtsStore", workspaceName + "/" + storeName, null);
    }

    public WmtsStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("WmtsStore", workspaceName + "/" + storeName, responseBody);
    }

    /** /   (: "myws/mystore") */
    public String getStoreName() {
        return getResourceName();
    }
}
