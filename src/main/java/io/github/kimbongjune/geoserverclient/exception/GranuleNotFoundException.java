package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested granule does not exist in a GeoServer StructuredCoverage index.
 */
public class GranuleNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public GranuleNotFoundException(String workspaceName, String storeName,
                                    String coverageName, String granuleId) {
        super("Granule", buildKey(workspaceName, storeName, coverageName, granuleId), null);
    }

    public GranuleNotFoundException(String workspaceName, String storeName,
                                    String coverageName, String granuleId, String responseBody) {
        super("Granule", buildKey(workspaceName, storeName, coverageName, granuleId), responseBody);
    }

    private static String buildKey(String ws, String store, String coverage, String id) {
        return ws + "/" + store + "/" + coverage + "/" + id;
    }

    /** "workspace/store/coverage/granuleId" . */
    public String getGranuleId() {
        return getResourceName();
    }
}
