package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 * GeoServer typically responds with 409 Conflict or 500 with a duplicate message.
 */
public class ResourceAlreadyExistsException extends GeoServerResponseException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceName;

    /**
     * Constructs a {@code ResourceAlreadyExistsException}.
     * @param resourceType the type of the conflicting resource (e.g. {@code "Layer"})
     * @param resourceName the name of the conflicting resource
     * @param responseBody the raw HTTP response body
     */
    public ResourceAlreadyExistsException(String resourceType, String resourceName, String responseBody) {
        super(409, String.format("%s already exists: '%s'", resourceType, resourceName), responseBody);
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }

    /** @return the type of the conflicting resource (e.g. {@code "Layer"}) */
    public String getResourceType() {
        return resourceType;
    }

    /** @return the name of the conflicting resource */
    public String getResourceName() {
        return resourceName;
    }
}
