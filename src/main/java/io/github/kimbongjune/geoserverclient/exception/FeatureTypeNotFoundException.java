package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested feature type does not exist in GeoServer.
 */
public class FeatureTypeNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public FeatureTypeNotFoundException(String workspaceName, String storeName, String featureTypeName) {
        super("FeatureType", workspaceName + "/" + storeName + "/" + featureTypeName, null);
    }

    public FeatureTypeNotFoundException(String workspaceName, String storeName, String featureTypeName, String responseBody) {
        super("FeatureType", workspaceName + "/" + storeName + "/" + featureTypeName, responseBody);
    }
}
