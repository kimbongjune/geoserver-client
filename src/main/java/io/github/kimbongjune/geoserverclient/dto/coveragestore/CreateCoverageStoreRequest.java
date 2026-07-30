package io.github.kimbongjune.geoserverclient.dto.coveragestore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a coverage store.
 *
 * <pre>{@code
 * // Minimal (type and name only)
 * CreateCoverageStoreRequest.of("mystore")
 *     .type("GeoTIFF")
 *
 * // With URL
 * CreateCoverageStoreRequest.of("mystore")
 *     .type("GeoTIFF")
 *     .url("file:data/myws/mystore/mystore.tif")
 *     .enabled(true)
 *
 * // Created in disabled state
 * CreateCoverageStoreRequest.of("mystore")
 *     .type("GeoTIFF")
 *     .enabled(false)
 * }</pre>
 *
 * <p>On POST the {@code workspace.name} field must be present; otherwise GeoServer returns
 * HTTP 500 ("Store must be part of a workspace").
 * {@link io.github.kimbongjune.geoserverclient.api.coveragestore.CoverageStoreManager} injects it automatically.</p>
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

    public static CreateCoverageStoreRequest of(String name) {
        return new CreateCoverageStoreRequest(name);
    }

    /**
     * Alias for {@link #of(String)}, for callers who prefer the {@code builder(...)...build()}
     * spelling used by every {@code UpdateXxxRequest} in this library. {@link #build()} is a
     * no-op terminal call.
     */
    public static CreateCoverageStoreRequest builder(String name) {
        return of(name);
    }

    public CreateCoverageStoreRequest type(String type) {
        this.type = type;
        return this;
    }

    public CreateCoverageStoreRequest description(String description) {
        this.description = description;
        return this;
    }

    public CreateCoverageStoreRequest url(String url) {
        this.url = url;
        return this;
    }

    public CreateCoverageStoreRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CreateCoverageStoreRequest disableOnConnFailure(Boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
        return this;
    }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public CreateCoverageStoreRequest build() { return this; }

    public String getName()                   { return name; }
    public String getType()                   { return type; }
    public String getDescription()            { return description; }
    public String getUrl()                    { return url; }
    public Boolean getEnabled()               { return enabled; }
    public Boolean getDisableOnConnFailure()  { return disableOnConnFailure; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
