package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested coverage does not exist in GeoServer.
 */
public class CoverageNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code CoverageNotFoundException} for the given workspace, store, and coverage.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name
     * @param coverageName  the coverage name that was not found
     */
    public CoverageNotFoundException(String workspaceName, String storeName, String coverageName) {
        super("Coverage", workspaceName + "/" + storeName + "/" + coverageName, null);
    }

    /**
     * Constructs a {@code CoverageNotFoundException} with response body.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name
     * @param coverageName  the coverage name that was not found
     * @param responseBody  the raw response body from GeoServer, or {@code null}
     */
    public CoverageNotFoundException(String workspaceName, String storeName, String coverageName,
                                     String responseBody) {
        super("Coverage", workspaceName + "/" + storeName + "/" + coverageName, responseBody);
    }

    /**
     * Returns the composite resource name in the form {@code "workspace/store/coverage"}.
     * @return the resource name
     */
    public String getCoverageName() {
        return getResourceName();
    }
}
