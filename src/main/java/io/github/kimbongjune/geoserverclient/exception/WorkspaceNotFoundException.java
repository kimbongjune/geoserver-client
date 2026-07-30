package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested workspace does not exist in GeoServer.
 */
public class WorkspaceNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public WorkspaceNotFoundException(String workspaceName) {
        super("Workspace", workspaceName, null);
    }

    public WorkspaceNotFoundException(String workspaceName, String responseBody) {
        super("Workspace", workspaceName, responseBody);
    }

    public String getWorkspaceName() {
        return getResourceName();
    }
}
