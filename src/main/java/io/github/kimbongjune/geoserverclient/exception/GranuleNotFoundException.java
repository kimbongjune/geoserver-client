package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested granule does not exist in a GeoServer StructuredCoverage index.
 */
public class GranuleNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code GranuleNotFoundException} for the given granule.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name
     * @param coverageName  the coverage name
     * @param granuleId     the granule ID
     */
    public GranuleNotFoundException(String workspaceName, String storeName,
                                    String coverageName, String granuleId) {
        super("Granule", buildKey(workspaceName, storeName, coverageName, granuleId), null);
    }

    /**
     * Constructs a {@code GranuleNotFoundException} with the raw response body.
     * @param workspaceName the workspace name
     * @param storeName     the coverage store name
     * @param coverageName  the coverage name
     * @param granuleId     the granule ID
     * @param responseBody  the raw HTTP response body, or {@code null}
     */
    public GranuleNotFoundException(String workspaceName, String storeName,
                                    String coverageName, String granuleId, String responseBody) {
        super("Granule", buildKey(workspaceName, storeName, coverageName, granuleId), responseBody);
    }

    private static String buildKey(String ws, String store, String coverage, String id) {
        return ws + "/" + store + "/" + coverage + "/" + id;
    }

    /**
     * Returns the granule key in {@code workspace/store/coverage/granuleId} format.
     * @return the granule key
     */
    public String getGranuleId() {
        return getResourceName();
    }
}
