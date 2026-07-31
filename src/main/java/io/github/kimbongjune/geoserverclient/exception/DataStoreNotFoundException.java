package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested data store does not exist in GeoServer.
 */
public class DataStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code DataStoreNotFoundException} for the given workspace and store name.
     * @param workspaceName the workspace name
     * @param storeName     the data store name
     */
    public DataStoreNotFoundException(String workspaceName, String storeName) {
        super("DataStore", workspaceName + "/" + storeName, null);
    }

    /**
     * Constructs a {@code DataStoreNotFoundException} with the raw response body.
     * @param workspaceName the workspace name
     * @param storeName     the data store name
     * @param responseBody  the raw HTTP response body, or {@code null}
     */
    public DataStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("DataStore", workspaceName + "/" + storeName, responseBody);
    }

    /**
     * Returns the store name in {@code workspace/store} format (e.g. {@code "sf/archsites"}).
     * @return the store name
     */
    public String getStoreName() {
        return getResourceName();
    }
}
