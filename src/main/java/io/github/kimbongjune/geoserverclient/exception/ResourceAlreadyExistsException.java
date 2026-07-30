package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 * GeoServer typically responds with 409 Conflict or 500 with a duplicate message.
 */
public class ResourceAlreadyExistsException extends GeoServerResponseException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String resourceName;

    public ResourceAlreadyExistsException(String resourceType, String resourceName, String responseBody) {
        super(409, String.format("%s already exists: '%s'", resourceType, resourceName), responseBody);
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }
}
