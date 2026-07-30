package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested user/group service does not exist in GeoServer.
 */
public class UserGroupServiceNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public UserGroupServiceNotFoundException(String serviceName) {
        super("UserGroupService", serviceName, null);
    }

    public UserGroupServiceNotFoundException(String serviceName, String responseBody) {
        super("UserGroupService", serviceName, responseBody);
    }

    public String getServiceName() {
        return getResourceName();
    }
}
