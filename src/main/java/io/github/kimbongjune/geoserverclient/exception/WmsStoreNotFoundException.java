package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested cascading WMS store does not exist in GeoServer.
 */
public class WmsStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public WmsStoreNotFoundException(String workspaceName, String storeName) {
        super("WmsStore", workspaceName + "/" + storeName, null);
    }

    public WmsStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("WmsStore", workspaceName + "/" + storeName, responseBody);
    }

    /** /   (: "myws/mystore") */
    public String getStoreName() {
        return getResourceName();
    }
}
