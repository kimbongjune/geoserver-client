package io.github.kimbongjune.geoserverclient.dto.workspace;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating an existing workspace.
 * Supports partial updates — only the fields you set will be sent.
 *
 * <pre>{@code
 * // Rename only
 * UpdateWorkspaceRequest.rename("newname")
 *
 * // Change isolated flag only
 * UpdateWorkspaceRequest.builder().isolated(true).build()
 *
 * // Rename + change flag
 * UpdateWorkspaceRequest.builder().name("newname").isolated(false).build()
 * }</pre>
 */
public class UpdateWorkspaceRequest {

    private final String name;
    private final Boolean isolated;

    private UpdateWorkspaceRequest(String name, Boolean isolated) {
        if (name != null && name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "workspace name must not be empty");
        }
        if (name == null && isolated == null) {
            throw new InvalidParameterException("request",
                    "at least one of 'name' or 'isolated' must be provided");
        }
        this.name = name != null ? name.trim() : null;
        this.isolated = isolated;
    }

    /**
     * Creates a rename-only update request.
     */
    public static UpdateWorkspaceRequest rename(String newName) {
        return new Builder().name(newName).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() { return name; }
    public Boolean getIsolated() { return isolated; }

    public static class Builder {
        private String name;
        private Boolean isolated;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder isolated(Boolean isolated) {
            this.isolated = isolated;
            return this;
        }

        public UpdateWorkspaceRequest build() {
            return new UpdateWorkspaceRequest(name, isolated);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder that = (Builder) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(isolated, that.isolated);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, isolated);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", isolated=" + isolated +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateWorkspaceRequest that = (UpdateWorkspaceRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(isolated, that.isolated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isolated);
    }

    @Override
    public String toString() {
        return "UpdateWorkspaceRequest{" +
                "name=" + name +
                ", isolated=" + isolated +
                '}';
    }
}
