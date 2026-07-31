package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoServer resource does not exist (HTTP 404).
 * Examples: workspace not found, layer not found, store not found.
 */
public class ResourceNotFoundException extends GeoServerResponseException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceName;

    /**
     * Constructs a {@code ResourceNotFoundException}.
     * @param resourceType the type of the missing resource (e.g. {@code "Layer"})
     * @param resourceName the name of the missing resource
     * @param responseBody the raw HTTP response body
     */
    public ResourceNotFoundException(String resourceType, String resourceName, String responseBody) {
        super(404, String.format("%s not found: '%s'", resourceType, resourceName), responseBody);
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }

    /** @return the type of the missing resource (e.g. {@code "Layer"}) */
    public String getResourceType() {
        return resourceType;
    }

    /** @return the name of the missing resource */
    public String getResourceName() {
        return resourceName;
    }
}
