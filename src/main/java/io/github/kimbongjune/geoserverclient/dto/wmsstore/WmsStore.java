package io.github.kimbongjune.geoserverclient.dto.wmsstore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for WMS store details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/wmsstores/{name}}.
 *
 * <p>Verified against GeoServer 2.28.2. Includes fields not documented in Swagger.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WmsStore {

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

    @JsonProperty("capabilitiesURL")
    private String capabilitiesURL;

    @JsonProperty("user")
    private String user;

    @JsonProperty("password")
    private String password;

    @JsonProperty("authKey")
    private String authKey;

    @JsonProperty("headerName")
    private String headerName;

    @JsonProperty("headerValue")
    private String headerValue;

    @JsonProperty("maxConnections")
    private Integer maxConnections;

    @JsonProperty("readTimeout")
    private Integer readTimeout;

    @JsonProperty("connectTimeout")
    private Integer connectTimeout;

    @JsonProperty("disableOnConnFailure")
    private Boolean disableOnConnFailure;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    @JsonProperty("wmslayers")
    private String wmslayers;

    @JsonProperty("metadata")
    private Metadata metadata;

    public WmsStore() {}

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getType() {
        return type;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    public Boolean getDefaultStore() {
        return defaultStore;
    }
    public WorkspaceLink getWorkspace() {
        return workspace;
    }
    public String getCapabilitiesURL() {
        return capabilitiesURL;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public String getAuthKey() {
        return authKey;
    }
    public String getHeaderName() {
        return headerName;
    }
    public String getHeaderValue() {
        return headerValue;
    }
    public Integer getMaxConnections() {
        return maxConnections;
    }
    public Integer getReadTimeout() {
        return readTimeout;
    }
    public Integer getConnectTimeout() {
        return connectTimeout;
    }
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public String getDateModified() {
        return dateModified;
    }
    public String getWmslayers() {
        return wmslayers;
    }
    public Metadata getMetadata() {
        return metadata;
    }

    //  Inner DTOs 

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
        public WorkspaceLink() {}
        public String getName() {
            return name;
        }
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("entry") private MetadataEntry entry;
        public Metadata() {}
        public MetadataEntry getEntry() {
            return entry;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Metadata that = (Metadata) o;
            return Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "entry=" + entry +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetadataEntry {
        @JsonProperty("@key") private String key;
        @JsonProperty("$")    private String value;
        public MetadataEntry() {}
        public String getKey() {
            return key;
        }
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MetadataEntry that = (MetadataEntry) o;
            return Objects.equals(key, that.key)
                    && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public String toString() {
            return "MetadataEntry{" +
                    "key=" + key +
                    ", value=" + value +
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
        WmsStore that = (WmsStore) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(type, that.type)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(capabilitiesURL, that.capabilitiesURL)
                && Objects.equals(user, that.user)
                && Objects.equals(password, that.password)
                && Objects.equals(authKey, that.authKey)
                && Objects.equals(headerName, that.headerName)
                && Objects.equals(headerValue, that.headerValue)
                && Objects.equals(maxConnections, that.maxConnections)
                && Objects.equals(readTimeout, that.readTimeout)
                && Objects.equals(connectTimeout, that.connectTimeout)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified)
                && Objects.equals(wmslayers, that.wmslayers)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, type, enabled, defaultStore, workspace, capabilitiesURL, user, password, authKey, headerName, headerValue, maxConnections, readTimeout, connectTimeout, disableOnConnFailure, dateCreated, dateModified, wmslayers, metadata);
    }

    @Override
    public String toString() {
        return "WmsStore{" +
                "name=" + name +
                ", description=" + description +
                ", type=" + type +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", workspace=" + workspace +
                ", capabilitiesURL=" + capabilitiesURL +
                ", user=" + user +
                ", password=" + password +
                ", authKey=" + authKey +
                ", headerName=" + headerName +
                ", headerValue=" + headerValue +
                ", maxConnections=" + maxConnections +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", wmslayers=" + wmslayers +
                ", metadata=" + metadata +
                '}';
    }
}
