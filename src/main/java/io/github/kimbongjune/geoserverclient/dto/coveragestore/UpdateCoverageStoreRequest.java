package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a coverage store. Supports partial updates.
 *
 * <pre>{@code
 * // Change description
 * UpdateCoverageStoreRequest.builder().description("new desc").build()
 *
 * // Disable
 * UpdateCoverageStoreRequest.builder().enabled(false).build()
 *
 * // Update multiple fields at once
 * UpdateCoverageStoreRequest.builder()
 *     .description("updated")
 *     .enabled(true)
 *     .url("file:data/newpath.tif")
 *     .build()
 * }</pre>
 */
public class UpdateCoverageStoreRequest {

    private final String  name;
    private final String  description;
    private final String  url;
    private final Boolean enabled;
    private final Boolean disableOnConnFailure;

    private UpdateCoverageStoreRequest(Builder b) {
        if (b.name == null && b.description == null && b.url == null
                && b.enabled == null && b.disableOnConnFailure == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.name                 = b.name;
        this.description          = b.description;
        this.url                  = b.url;
        this.enabled              = b.enabled;
        this.disableOnConnFailure = b.disableOnConnFailure;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String  getName() {
        return name;
    }
    public String  getDescription() {
        return description;
    }
    public String  getUrl() {
        return url;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }

    public static class Builder {
        private String  name;
        private String  description;
        private String  url;
        private Boolean enabled;
        private Boolean disableOnConnFailure;

        public Builder name(String name) {
            this.name = name;                         return this;
        }
        public Builder description(String description) {
            this.description = description;           return this;
        }
        public Builder url(String url) {
            this.url = url;                           return this;
        }
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;                   return this;
        }
        public Builder disableOnConnFailure(Boolean disable) {
            this.disableOnConnFailure = disable;      return this;
        }
        public UpdateCoverageStoreRequest build() {
            return new UpdateCoverageStoreRequest(this);
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
                    && Objects.equals(description, that.description)
                    && Objects.equals(url, that.url)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(disableOnConnFailure, that.disableOnConnFailure);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, url, enabled, disableOnConnFailure);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", description=" + description +
                    ", url=" + url +
                    ", enabled=" + enabled +
                    ", disableOnConnFailure=" + disableOnConnFailure +
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
        UpdateCoverageStoreRequest that = (UpdateCoverageStoreRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(url, that.url)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, url, enabled, disableOnConnFailure);
    }

    @Override
    public String toString() {
        return "UpdateCoverageStoreRequest{" +
                "name=" + name +
                ", description=" + description +
                ", url=" + url +
                ", enabled=" + enabled +
                ", disableOnConnFailure=" + disableOnConnFailure +
                '}';
    }
}
