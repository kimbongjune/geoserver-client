package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested data store does not exist in GeoServer.
 */
public class DataStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public DataStoreNotFoundException(String workspaceName, String storeName) {
        super("DataStore", workspaceName + "/" + storeName, null);
    }

    public DataStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("DataStore", workspaceName + "/" + storeName, responseBody);
    }

    /** /   (: "sf/archsites") */
    public String getStoreName() {
        return getResourceName();
    }
}
