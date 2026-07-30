package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for the target data store of an import task.
 *
 * <p>Used as both the response body for
 * {@code GET /rest/imports/{id}/tasks/{taskId}/target} and the request body for
 * {@code PUT /rest/imports/{id}/tasks/{taskId}/target}.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTarget {

    private String href;
    private DataStore dataStore;

    public ImportTarget() {}

    public String getHref() { return href; }
    public DataStore getDataStore() { return dataStore; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataStore {
        private String name;
        private String type;
        private Boolean enabled;
        private Workspace workspace;

        public DataStore() {}

        public DataStore(String name, String workspaceName) {
            this.name = name;
            this.workspace = new Workspace(workspaceName);
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public Boolean getEnabled() { return enabled; }
        public Workspace getWorkspace() { return workspace; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataStore that = (DataStore) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(type, that.type)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(workspace, that.workspace);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, enabled, workspace);
        }

        @Override
        public String toString() {
            return "DataStore{" +
                    "name=" + name +
                    ", type=" + type +
                    ", enabled=" + enabled +
                    ", workspace=" + workspace +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Workspace {
        private String name;

        public Workspace() {}

        public Workspace(String name) { this.name = name; }

        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Workspace that = (Workspace) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Workspace{" +
                    "name=" + name +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportTarget that = (ImportTarget) o;
        return Objects.equals(href, that.href)
                && Objects.equals(dataStore, that.dataStore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, dataStore);
    }

    @Override
    public String toString() {
        return "ImportTarget{" +
                "href=" + href +
                ", dataStore=" + dataStore +
                '}';
    }
}
