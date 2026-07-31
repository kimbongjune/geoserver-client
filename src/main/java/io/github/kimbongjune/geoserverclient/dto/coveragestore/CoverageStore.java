package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for coverage store details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/coveragestores/{name}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverageStore {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("_default")
    private Boolean defaultStore;

    @JsonProperty("workspace")
    private WorkspaceLink workspace;

    @JsonProperty("url")
    private String url;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    @JsonProperty("disableOnConnFailure")
    private Boolean disableOnConnFailure;

    @JsonProperty("coverages")
    private String coverages;

    /** Constructs an empty {@code CoverageStore} for deserialization. */
    public CoverageStore() {}

    /** @return the store name */
    public String getName() {
        return name;
    }
    /** @return the store description */
    public String getDescription() {
        return description;
    }
    /** @return the store type (e.g. {@code "GeoTIFF"}) */
    public String getType() {
        return type;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if this store is enabled */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    /** @return {@code true} if this is the default store */
    public Boolean getDefaultStore() {
        return defaultStore;
    }
    /** @return the workspace link */
    public WorkspaceLink getWorkspace() {
        return workspace;
    }
    /** @return the store data URL */
    public String getUrl() {
        return url;
    }
    /** @return the date the store was created */
    public String getDateCreated() {
        return dateCreated;
    }
    /** @return the date the store was last modified */
    public String getDateModified() {
        return dateModified;
    }
    /** @return {@code true} if the store is disabled on connection failure */
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }
    /** @return the coverages href */
    public String getCoverages() {
        return coverages;
    }

    // Inner DTOs

    /** Workspace link (name + href) embedded in coverage store responses. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
        /** Constructs an empty {@code WorkspaceLink} for deserialization. */
        public WorkspaceLink() {}
        /** @return the workspace name */
        public String getName() {
            return name;
        }
        /** @return the workspace href */
        public String getHref() {
            return href;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WorkspaceLink that = (WorkspaceLink) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, href);
        }

        @Override
        public String toString() {
            return "WorkspaceLink{" +
                    "name=" + name +
                    ", href=" + href +
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
        CoverageStore that = (CoverageStore) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(type, that.type)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(url, that.url)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(coverages, that.coverages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, type, enabled, defaultStore, workspace, url, dateCreated, dateModified, disableOnConnFailure, coverages);
    }

    @Override
    public String toString() {
        return "CoverageStore{" +
                "name=" + name +
                ", description=" + description +
                ", type=" + type +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", workspace=" + workspace +
                ", url=" + url +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", coverages=" + coverages +
                '}';
    }
}
