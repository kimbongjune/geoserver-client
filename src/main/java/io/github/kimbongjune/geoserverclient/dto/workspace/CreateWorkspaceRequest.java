package io.github.kimbongjune.geoserverclient.dto.workspace;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a new workspace.
 *
 * <pre>{@code
 * // Simple creation
 * CreateWorkspaceRequest.builder("myws")
 *
 * // With options
 * CreateWorkspaceRequest.builder("myws")
 *     .isolated(true)
 *     .setAsDefault(true)
 * }</pre>
 */
public class CreateWorkspaceRequest {

    private final String name;
    private Boolean isolated;
    private boolean setAsDefault;

    private CreateWorkspaceRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "workspace name must not be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * Creates a request with the given workspace name.
     *
     * @param name workspace name (required)
     * @return a new request instance
     */
    public static CreateWorkspaceRequest builder(String name) {
        return new CreateWorkspaceRequest(name);
    }

    /**
     * Sets whether this should be an isolated workspace.
     * Isolated workspaces are accessible only through virtual services.
     *
     * @param isolated true for an isolated workspace (default: false)
     * @return this request
     */
    public CreateWorkspaceRequest isolated(Boolean isolated) {
        this.isolated = isolated;
        return this;
    }

    /**
     * Sets whether this workspace should become the default workspace upon creation.
     * Corresponds to the {@code ?default=true} query parameter.
     *
     * @param setAsDefault true to make this the default workspace
     * @return this request
     */
    public CreateWorkspaceRequest setAsDefault(boolean setAsDefault) {
        this.setAsDefault = setAsDefault;
        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateWorkspaceRequest build() {
        return this;
    }

    /** @return the workspace name */
    public String getName() {
        return name;
    }
    /** @return {@code true} if this is an isolated workspace */
    public Boolean getIsolated() {
        return isolated;
    }
    /** @return {@code true} if this workspace should be set as default */
    public boolean isSetAsDefault() {
        return setAsDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateWorkspaceRequest that = (CreateWorkspaceRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(isolated, that.isolated)
                && Objects.equals(setAsDefault, that.setAsDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isolated, setAsDefault);
    }

    @Override
    public String toString() {
        return "CreateWorkspaceRequest{" +
                "name=" + name +
                ", isolated=" + isolated +
                ", setAsDefault=" + setAsDefault +
                '}';
    }
}
