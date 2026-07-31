package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache grid set does not exist.
 */
public class GwcGridSetNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code GwcGridSetNotFoundException} for the given grid set name.
     * @param gridSetName the GWC grid set name
     */
    public GwcGridSetNotFoundException(String gridSetName) {
        super("GwcGridSet", gridSetName, null);
    }

    /**
     * Constructs a {@code GwcGridSetNotFoundException} with the raw response body.
     * @param gridSetName  the GWC grid set name
     * @param responseBody the raw HTTP response body, or {@code null}
     */
    public GwcGridSetNotFoundException(String gridSetName, String responseBody) {
        super("GwcGridSet", gridSetName, responseBody);
    }

    /**
     * Returns the GWC grid set name that was not found.
     * @return the grid set name
     */
    public String getGridSetName() {
        return getResourceName();
    }
}
