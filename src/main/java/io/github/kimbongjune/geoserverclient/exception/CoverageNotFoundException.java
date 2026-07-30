package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested coverage does not exist in GeoServer.
 */
public class CoverageNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public CoverageNotFoundException(String workspaceName, String storeName, String coverageName) {
        super("Coverage", workspaceName + "/" + storeName + "/" + coverageName, null);
    }

    public CoverageNotFoundException(String workspaceName, String storeName, String coverageName,
                                     String responseBody) {
        super("Coverage", workspaceName + "/" + storeName + "/" + coverageName, responseBody);
    }

    /** //   (: "myws/mystore/mycov") */
    public String getCoverageName() {
        return getResourceName();
    }
}
