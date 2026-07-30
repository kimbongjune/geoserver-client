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
 * CreateDataStoreRequest.of("mystore")
 *     .connectionParam("url", "file:data/sf/archsites.shp")
 *     .connectionParam("namespace", "http://sf.example.com")
 *
 * // PostGIS example
 * CreateDataStoreRequest.of("myPostGIS")
 *     .connectionParam("host", "localhost")
 *     .connectionParam("port", "5432")
 *     .connectionParam("database", "mydb")
 *     .connectionParam("user", "bob")
 *     .connectionParam("passwd", "secret")
 *     .connectionParam("dbtype", "postgis")
 * }</pre>
 *
 * <p>Connection parameters vary by store type. See the DataStoreManager Javadoc
 * for a full list of supported {@code connectionParameters} per type.</p>
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
     * @param name data store name (required)
     */
    public static CreateDataStoreRequest of(String name) {
        return new CreateDataStoreRequest(name);
    }

    /**
     * Alias for {@link #of(String)}, for callers who prefer the {@code builder(...)...build()}
     * spelling used by every {@code UpdateXxxRequest} in this library. {@link #build()} is a
     * no-op terminal call.
     */
    public static CreateDataStoreRequest builder(String name) {
        return of(name);
    }

    public CreateDataStoreRequest description(String description) {
        this.description = description;
        return this;
    }

    public CreateDataStoreRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CreateDataStoreRequest defaultStore(Boolean defaultStore) {
        this.defaultStore = defaultStore;
        return this;
    }

    public CreateDataStoreRequest type(String type) {
        this.type = type;
        return this;
    }

    public CreateDataStoreRequest disableOnConnFailure(Boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
        return this;
    }

    /**
     * Adds a connection parameter.
     *
     * @param key   parameter key (e.g. "url", "host", "dbtype")
     * @param value parameter value
     */
    public CreateDataStoreRequest connectionParam(String key, String value) {
        this.connectionParams.add(new DataStore.Entry(key, value));
        return this;
    }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public CreateDataStoreRequest build() { return this; }

    public String getName()                      { return name; }
    public String getDescription()               { return description; }
    public Boolean getEnabled()                  { return enabled; }
    public Boolean getDefaultStore()             { return defaultStore; }
    public String getType()                      { return type; }
    public Boolean getDisableOnConnFailure()     { return disableOnConnFailure; }
    public List<DataStore.Entry> getConnectionParams() { return connectionParams == null ? null : Collections.unmodifiableList(connectionParams); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
