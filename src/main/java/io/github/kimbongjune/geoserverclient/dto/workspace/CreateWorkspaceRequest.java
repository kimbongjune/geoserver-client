package io.github.kimbongjune.geoserverclient.dto.workspace;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a new workspace.
 *
 * <pre>{@code
 * // Simple creation
 * CreateWorkspaceRequest.of("myws")
 *
 * // With options
 * CreateWorkspaceRequest.of("myws")
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
     */
    public static CreateWorkspaceRequest of(String name) {
        return new CreateWorkspaceRequest(name);
    }

    /**
     * Alias for {@link #of(String)}, for callers who prefer the {@code builder(...)...build()}
     * spelling used by every {@code UpdateXxxRequest} in this library. Returns the same fluent,
     * chainable object as {@link #of(String)} — {@link #build()} is a no-op terminal call.
     */
    public static CreateWorkspaceRequest builder(String name) {
        return of(name);
    }

    /**
     * Sets whether this should be an isolated workspace.
     * Isolated workspaces are accessible only through virtual services.
     *
     * @param isolated true for an isolated workspace (default: false)
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
     */
    public CreateWorkspaceRequest setAsDefault(boolean setAsDefault) {
        this.setAsDefault = setAsDefault;
        return this;
    }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public CreateWorkspaceRequest build() { return this; }

    public String getName() { return name; }
    public Boolean getIsolated() { return isolated; }
    public boolean isSetAsDefault() { return setAsDefault; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
