package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a coverage store.
 *
 * <pre>{@code
 * // Minimal (type and name only)
 * CreateCoverageStoreRequest.builder("mystore")
 *     .type("GeoTIFF")
 *
 * // With URL
 * CreateCoverageStoreRequest.builder("mystore")
 *     .type("GeoTIFF")
 *     .url("file:data/myws/mystore/mystore.tif")
 *     .enabled(true)
 *
 * // Created in disabled state
 * CreateCoverageStoreRequest.builder("mystore")
 *     .type("GeoTIFF")
 *     .enabled(false)
 * }</pre>
 *
 * <p>On POST the {@code workspace.name} field must be present; otherwise GeoServer returns
 * HTTP 500 ("Store must be part of a workspace").
 * {@link io.github.kimbongjune.geoserverclient.api.coveragestore.CoverageStoreManager} injects it automatically.
 */
public class CreateCoverageStoreRequest {

    private final String name;
    private String type;
    private String description;
    private String url;
    private Boolean enabled;
    private Boolean disableOnConnFailure;

    private CreateCoverageStoreRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "coverageStore name must not be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * Creates a new request for a coverage store with the given name.
     * @param name the coverage store name (must not be null or empty)
     * @return a new request instance
     */
    public static CreateCoverageStoreRequest builder(String name) {
        return new CreateCoverageStoreRequest(name);
    }

    /**
     * Sets the store type.
     * @param type the store type (e.g. {@code "GeoTIFF"})
     * @return this request
     */
    public CreateCoverageStoreRequest type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the store description.
     * @param description the description
     * @return this request
     */
    public CreateCoverageStoreRequest description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the store data URL.
     * @param url the data URL
     * @return this request
     */
    public CreateCoverageStoreRequest url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets whether this store is enabled.
     * @param enabled {@code true} to enable
     * @return this request
     */
    public CreateCoverageStoreRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets whether to disable the store on connection failure.
     * @param disableOnConnFailure {@code true} to disable on connection failure
     * @return this request
     */
    public CreateCoverageStoreRequest disableOnConnFailure(Boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateCoverageStoreRequest build() {
        return this;
    }

    /** @return the store name */
    public String getName() {
        return name;
    }
    /** @return the store type */
    public String getType() {
        return type;
    }
    /** @return the store description */
    public String getDescription() {
        return description;
    }
    /** @return the store data URL */
    public String getUrl() {
        return url;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if store is disabled on connection failure */
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateCoverageStoreRequest that = (CreateCoverageStoreRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(type, that.type)
                && Objects.equals(description, that.description)
                && Objects.equals(url, that.url)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, description, url, enabled, disableOnConnFailure);
    }

    @Override
    public String toString() {
        return "CreateCoverageStoreRequest{" +
                "name=" + name +
                ", type=" + type +
                ", description=" + description +
                ", url=" + url +
                ", enabled=" + enabled +
                ", disableOnConnFailure=" + disableOnConnFailure +
                '}';
    }
}
