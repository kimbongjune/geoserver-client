package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested feature type does not exist in GeoServer.
 */
public class FeatureTypeNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code FeatureTypeNotFoundException} for the given workspace, store, and feature type.
     * @param workspaceName   the workspace name
     * @param storeName       the data store name
     * @param featureTypeName the feature type name
     */
    public FeatureTypeNotFoundException(String workspaceName, String storeName, String featureTypeName) {
        super("FeatureType", workspaceName + "/" + storeName + "/" + featureTypeName, null);
    }

    /**
     * Constructs a {@code FeatureTypeNotFoundException} with the raw response body.
     * @param workspaceName   the workspace name
     * @param storeName       the data store name
     * @param featureTypeName the feature type name
     * @param responseBody    the raw HTTP response body, or {@code null}
     */
    public FeatureTypeNotFoundException(String workspaceName, String storeName, String featureTypeName, String responseBody) {
        super("FeatureType", workspaceName + "/" + storeName + "/" + featureTypeName, responseBody);
    }
}
