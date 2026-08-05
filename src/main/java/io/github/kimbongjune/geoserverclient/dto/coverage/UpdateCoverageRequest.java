package io.github.kimbongjune.geoserverclient.dto.coverage;

import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a coverage. Supports partial updates.
 *
 * <pre>{@code
 * // Change only the title
 * UpdateCoverageRequest.builder().title("New Title").build()
 *
 * // Rename
 * UpdateCoverageRequest.builder().name("new_name").build()
 *
 * // Update multiple fields at once
 * UpdateCoverageRequest.builder()
 *     .title("Updated")
 *     .description("Updated description")
 *     .enabled(false)
 *     .srs("EPSG:4326")
 *     .build()
 * }</pre>
 */
public class UpdateCoverageRequest {

    private final String  name;
    private final String  title;
    private final String  description;
    private final String  srs;
    private final ProjectionPolicy projectionPolicy;
    private final Boolean enabled;
    private final Boolean advertised;
    private final String  defaultInterpolationMethod;

    private UpdateCoverageRequest(Builder b) {
        if (b.name == null && b.title == null && b.description == null
                && b.srs == null && b.projectionPolicy == null
                && b.enabled == null && b.advertised == null
                && b.defaultInterpolationMethod == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.name                      = b.name;
        this.title                     = b.title;
        this.description               = b.description;
        this.srs                       = b.srs;
        this.projectionPolicy          = b.projectionPolicy;
        this.enabled                   = b.enabled;
        this.advertised                = b.advertised;
        this.defaultInterpolationMethod = b.defaultInterpolationMethod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String  getName() {
        return name;
    }
    public String  getTitle() {
        return title;
    }
    public String  getDescription() {
        return description;
    }
    public String  getSrs() {
        return srs;
    }
    public ProjectionPolicy getProjectionPolicy() {
        return projectionPolicy;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public Boolean getAdvertised() {
        return advertised;
    }
    public String  getDefaultInterpolationMethod() {
        return defaultInterpolationMethod;
    }

    public static class Builder {
        private String  name;
        private String  title;
        private String  description;
        private String  srs;
        private ProjectionPolicy projectionPolicy;
        private Boolean enabled;
        private Boolean advertised;
        private String  defaultInterpolationMethod;

        public Builder name(String name) {
            this.name = name;                           return this;
        }
        public Builder title(String title) {
            this.title = title;                         return this;
        }
        public Builder description(String description) {
            this.description = description;             return this;
        }
        public Builder srs(String srs) {
            this.srs = srs;                             return this;
        }
        public Builder projectionPolicy(ProjectionPolicy projectionPolicy) {
            this.projectionPolicy = projectionPolicy;   return this;
        }
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;                     return this;
        }
        public Builder advertised(Boolean advertised) {
            this.advertised = advertised;               return this;
        }
        public Builder defaultInterpolationMethod(String method) {
            this.defaultInterpolationMethod = method;   return this;
        }
        public UpdateCoverageRequest build() {
            return new UpdateCoverageRequest(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Builder that = (Builder) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(title, that.title)
                    && Objects.equals(description, that.description)
                    && Objects.equals(srs, that.srs)
                    && Objects.equals(projectionPolicy, that.projectionPolicy)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(advertised, that.advertised)
                    && Objects.equals(defaultInterpolationMethod, that.defaultInterpolationMethod);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, title, description, srs, projectionPolicy, enabled, advertised, defaultInterpolationMethod);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", title=" + title +
                    ", description=" + description +
                    ", srs=" + srs +
                    ", projectionPolicy=" + projectionPolicy +
                    ", enabled=" + enabled +
                    ", advertised=" + advertised +
                    ", defaultInterpolationMethod=" + defaultInterpolationMethod +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateCoverageRequest that = (UpdateCoverageRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(defaultInterpolationMethod, that.defaultInterpolationMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title, description, srs, projectionPolicy, enabled, advertised, defaultInterpolationMethod);
    }

    @Override
    public String toString() {
        return "UpdateCoverageRequest{" +
                "name=" + name +
                ", title=" + title +
                ", description=" + description +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", defaultInterpolationMethod=" + defaultInterpolationMethod +
                '}';
    }
}
