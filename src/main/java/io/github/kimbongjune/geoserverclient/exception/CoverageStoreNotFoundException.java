package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested coverage store does not exist in GeoServer.
 */
public class CoverageStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public CoverageStoreNotFoundException(String workspaceName, String storeName) {
        super("CoverageStore", workspaceName + "/" + storeName, null);
    }

    public CoverageStoreNotFoundException(String workspaceName, String storeName, String responseBody) {
        super("CoverageStore", workspaceName + "/" + storeName, responseBody);
    }

    /** /   (: "myws/mystore") */
    public String getStoreName() {
        return getResourceName();
    }
}
