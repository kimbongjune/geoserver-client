package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested coverage store does not exist in GeoServer.
 */
public class CoverageStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code CoverageStoreNotFoundException} for the given workspace and store.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name that was not found
     */
    public CoverageStoreNotFoundException(String workspaceName, String storeName) {
        super("CoverageStore", workspaceName + "/" + storeName, null);
    }

    /**
     * Constructs a {@code CoverageStoreNotFoundException} with response body.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name that was not found
     * @param responseBody  the raw response body from GeoServer, or {@code null}
     */
    public CoverageStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("CoverageStore", workspaceName + "/" + storeName, responseBody);
    }

    /**
     * Returns the composite resource name in the form {@code "workspace/store"}.
     * @return the resource name
     */
    public String getStoreName() {
        return getResourceName();
    }
}
