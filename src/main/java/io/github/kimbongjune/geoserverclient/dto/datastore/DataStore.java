package io.github.kimbongjune.geoserverclient.dto.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for data store details.
 *
 * <p>The {@code connectionParameters.entry} array uses the format
 * {@code [{"@key":"url","$":"file:..."}]}. A GET response only includes parameters that were
 * explicitly set; defaults are omitted. Sending {@code connectionParameters} on PUT replaces
 * all existing parameters in full — partial updates are not supported.</p>
 *
 * <p>{@code dateCreated}, {@code dateModified}, and {@code disableOnConnFailure} are not in the
 * official Swagger spec but are returned by GeoServer 2.28.x.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataStore {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("_default")
    private Boolean defaultStore;

    @JsonProperty("type")
    private String type;

    @JsonProperty("workspace")
    private WorkspaceLink workspace;

    @JsonProperty("connectionParameters")
    private ConnectionParameters connectionParameters;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    @JsonProperty("disableOnConnFailure")
    private Boolean disableOnConnFailure;

    @JsonProperty("featureTypes")
    private String featureTypes;

    public DataStore() {}

    public String getName()                           { return name; }
    public String getDescription()                    { return description; }
    public Boolean getEnabled()                       { return enabled; }
    public boolean isEnabled()                        { return Boolean.TRUE.equals(enabled); }
    public Boolean getDefaultStore()                  { return defaultStore; }
    public boolean isDefaultStore()                   { return Boolean.TRUE.equals(defaultStore); }
    public String getType()                           { return type; }
    public WorkspaceLink getWorkspace()               { return workspace; }
    public ConnectionParameters getConnectionParameters() { return connectionParameters; }
    public String getDateCreated()                    { return dateCreated; }
    public String getDateModified()                   { return dateModified; }
    public Boolean getDisableOnConnFailure()          { return disableOnConnFailure; }
    public boolean isDisableOnConnFailure()           { return Boolean.TRUE.equals(disableOnConnFailure); }
    public String getFeatureTypes()                   { return featureTypes; }

    /** Workspace link embedded in the data store response. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceLink {
        @JsonProperty("name")
        private String name;
        @JsonProperty("href")
        private String href;

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

    /** Container for the connectionParameters entry list. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectionParameters {
        @JsonProperty("entry")
        private List<Entry> entry;

        public ConnectionParameters() {}
        public List<Entry> getEntry() { return entry == null ? null : Collections.unmodifiableList(entry); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectionParameters that = (ConnectionParameters) o;
            return Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }

        @Override
        public String toString() {
            return "ConnectionParameters{" +
                    "entry=" + entry +
                    '}';
        }
    }

    /** A single connection parameter entry in the format {@code {"@key": "...", "$": "..."}}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        @JsonProperty("@key")
        private String key;
        @JsonProperty("$")
        private String value;

        public Entry() {}
        public Entry(String key, String value) {
            this.key   = key;
            this.value = value;
        }
        public String getKey()   { return key; }
        public String getValue() { return value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry that = (Entry) o;
            return Objects.equals(key, that.key)
                    && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataStore that = (DataStore) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(type, that.type)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(connectionParameters, that.connectionParameters)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(featureTypes, that.featureTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, enabled, defaultStore, type, workspace, connectionParameters, dateCreated, dateModified, disableOnConnFailure, featureTypes);
    }

    @Override
    public String toString() {
        return "DataStore{" +
                "name=" + name +
                ", description=" + description +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", type=" + type +
                ", workspace=" + workspace +
                ", connectionParameters=" + connectionParameters +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", featureTypes=" + featureTypes +
                '}';
    }
}
