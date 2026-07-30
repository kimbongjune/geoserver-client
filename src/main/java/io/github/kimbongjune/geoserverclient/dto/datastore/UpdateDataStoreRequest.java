package io.github.kimbongjune.geoserverclient.dto.datastore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for updating a data store. Supports partial updates.
 *
 * <p><b>Important:</b> including {@code connectionParameters} replaces all existing connection
 * parameters in full — partial parameter updates are not supported. Omit
 * {@code connectionParam()} calls if you do not intend to change the connection parameters.</p>
 *
 * <pre>{@code
 * // Change description only
 * UpdateDataStoreRequest.builder().description("new description").build()
 *
 * // Rename (actually accepted by the server despite 403 in official docs)
 * UpdateDataStoreRequest.builder().name("newname").build()
 *
 * // Replace all connection parameters
 * UpdateDataStoreRequest.builder()
 *     .connectionParam("host", "newhost")
 *     .connectionParam("port", "5432")
 *     .connectionParam("dbtype", "postgis")
 *     .build()
 * }</pre>
 */
public class UpdateDataStoreRequest {

    private final String name;
    private final String description;
    private final Boolean enabled;
    private final Boolean defaultStore;
    private final Boolean disableOnConnFailure;
    private final List<DataStore.Entry> connectionParams;

    private UpdateDataStoreRequest(Builder b) {
        if (b.name == null && b.description == null && b.enabled == null
                && b.defaultStore == null && b.disableOnConnFailure == null
                && b.connectionParams.isEmpty()) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.name                  = b.name;
        this.description           = b.description;
        this.enabled               = b.enabled;
        this.defaultStore          = b.defaultStore;
        this.disableOnConnFailure  = b.disableOnConnFailure;
        this.connectionParams      = b.connectionParams;
    }

    public static Builder builder() { return new Builder(); }

    public String getName()                      { return name; }
    public String getDescription()               { return description; }
    public Boolean getEnabled()                  { return enabled; }
    public Boolean getDefaultStore()             { return defaultStore; }
    public Boolean getDisableOnConnFailure()     { return disableOnConnFailure; }
    public List<DataStore.Entry> getConnectionParams() { return connectionParams == null ? null : Collections.unmodifiableList(connectionParams); }

    public static class Builder {
        private String name;
        private String description;
        private Boolean enabled;
        private Boolean defaultStore;
        private Boolean disableOnConnFailure;
        private final List<DataStore.Entry> connectionParams = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        public Builder defaultStore(Boolean defaultStore) {
            this.defaultStore = defaultStore;
            return this;
        }
        public Builder disableOnConnFailure(Boolean disableOnConnFailure) {
            this.disableOnConnFailure = disableOnConnFailure;
            return this;
        }
        public Builder connectionParam(String key, String value) {
            this.connectionParams.add(new DataStore.Entry(key, value));
            return this;
        }
        public UpdateDataStoreRequest build() {
            return new UpdateDataStoreRequest(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder that = (Builder) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(description, that.description)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(defaultStore, that.defaultStore)
                    && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                    && Objects.equals(connectionParams, that.connectionParams);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, description, enabled, defaultStore, disableOnConnFailure, connectionParams);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", description=" + description +
                    ", enabled=" + enabled +
                    ", defaultStore=" + defaultStore +
                    ", disableOnConnFailure=" + disableOnConnFailure +
                    ", connectionParams=" + connectionParams +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateDataStoreRequest that = (UpdateDataStoreRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(connectionParams, that.connectionParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, enabled, defaultStore, disableOnConnFailure, connectionParams);
    }

    @Override
    public String toString() {
        return "UpdateDataStoreRequest{" +
                "name=" + name +
                ", description=" + description +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", connectionParams=" + connectionParams +
                '}';
    }
}
