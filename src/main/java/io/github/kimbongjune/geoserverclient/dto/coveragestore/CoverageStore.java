package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for coverage store details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/coveragestores/{name}}.</p>
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

    public CoverageStore() {}

    public String getName()                   { return name; }
    public String getDescription()            { return description; }
    public String getType()                   { return type; }
    public Boolean getEnabled()               { return enabled; }
    public boolean isEnabled()                { return Boolean.TRUE.equals(enabled); }
    public Boolean getDefaultStore()          { return defaultStore; }
    public WorkspaceLink getWorkspace()       { return workspace; }
    public String getUrl()                    { return url; }
    public String getDateCreated()            { return dateCreated; }
    public String getDateModified()           { return dateModified; }
    public Boolean getDisableOnConnFailure()  { return disableOnConnFailure; }
    public String getCoverages()              { return coverages; }

    // Inner DTOs

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
        public WorkspaceLink() {}
        public String getName() { return name; }
        public String getHref() { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
