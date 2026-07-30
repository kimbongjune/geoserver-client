package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache grid set does not exist.
 */
public class GwcGridSetNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public GwcGridSetNotFoundException(String gridSetName) {
        super("GwcGridSet", gridSetName, null);
    }

    public GwcGridSetNotFoundException(String gridSetName, String responseBody) {
        super("GwcGridSet", gridSetName, responseBody);
    }

    public String getGridSetName() {
        return getResourceName();
    }
}
