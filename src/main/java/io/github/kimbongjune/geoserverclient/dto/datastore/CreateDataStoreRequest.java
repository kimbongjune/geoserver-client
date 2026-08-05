package io.github.kimbongjune.geoserverclient.dto.datastore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for creating a data store.
 *
 * <pre>{@code
 * // Shapefile example
 * CreateDataStoreRequest.builder("mystore")
 *     .connectionParam("url", "file:data/sf/archsites.shp")
 *     .connectionParam("namespace", "http://sf.example.com")
 *
 * // PostGIS example
 * CreateDataStoreRequest.builder("myPostGIS")
 *     .connectionParam("host", "localhost")
 *     .connectionParam("port", "5432")
 *     .connectionParam("database", "mydb")
 *     .connectionParam("user", "bob")
 *     .connectionParam("passwd", "secret")
 *     .connectionParam("dbtype", "postgis")
 * }</pre>
 *
 * <p>Connection parameters vary by store type. See the DataStoreManager Javadoc
 * for a full list of supported {@code connectionParameters} per type.
 */
public class CreateDataStoreRequest {

    private final String name;
    private String description;
    private Boolean enabled;
    private Boolean defaultStore;
    private String type;
    private Boolean disableOnConnFailure;
    private final List<DataStore.Entry> connectionParams = new ArrayList<>();

    private CreateDataStoreRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "datastore name must not be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * Creates a data store request with the given name.
     *
     * @param name data store name (required)
     * @return a new request instance
     */
    public static CreateDataStoreRequest builder(String name) {
        return new CreateDataStoreRequest(name);
    }

    /**
     * Sets the store description.
     * @param description the description
     * @return this request
     */
    public CreateDataStoreRequest description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets whether this store is enabled.
     * @param enabled {@code true} to enable
     * @return this request
     */
    public CreateDataStoreRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets whether this is the default store for the workspace.
     * @param defaultStore {@code true} to make this the default store
     * @return this request
     */
    public CreateDataStoreRequest defaultStore(Boolean defaultStore) {
        this.defaultStore = defaultStore;
        return this;
    }

    /**
     * Sets the store type.
     * @param type the store type (e.g. {@code "PostGIS"}, {@code "Shapefile"})
     * @return this request
     */
    public CreateDataStoreRequest type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Sets whether to disable the store on connection failure.
     * @param disableOnConnFailure {@code true} to disable on connection failure
     * @return this request
     */
    public CreateDataStoreRequest disableOnConnFailure(Boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
        return this;
    }

    /**
     * Adds a connection parameter.
     *
     * @param key   parameter key (e.g. "url", "host", "dbtype")
     * @param value parameter value
     * @return this request
     */
    public CreateDataStoreRequest connectionParam(String key, String value) {
        this.connectionParams.add(new DataStore.Entry(key, value));
        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateDataStoreRequest build() {
        return this;
    }

    /** @return the store name */
    public String getName() {
        return name;
    }
    /** @return the store description */
    public String getDescription() {
        return description;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if this is the default store */
    public Boolean getDefaultStore() {
        return defaultStore;
    }
    /** @return the store type */
    public String getType() {
        return type;
    }
    /** @return {@code true} if disabled on connection failure */
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }
    /** @return the connection parameters */
    public List<DataStore.Entry> getConnectionParams() {
        return connectionParams == null ? null : Collections.unmodifiableList(connectionParams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateDataStoreRequest that = (CreateDataStoreRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(type, that.type)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(connectionParams, that.connectionParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, enabled, defaultStore, type, disableOnConnFailure, connectionParams);
    }

    @Override
    public String toString() {
        return "CreateDataStoreRequest{" +
                "name=" + name +
                ", description=" + description +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", type=" + type +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", connectionParams=" + connectionParams +
                '}';
    }
}
