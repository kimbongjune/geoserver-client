package io.github.kimbongjune.geoserverclient.dto.coverage;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a coverage.
 *
 * <p>Builds the request body for {@code POST /rest/workspaces/{ws}/coveragestores/{cs}/coverages}.
 *
 * <p><b>Two modes:</b>
 * <ul>
 *   <li><b>isNew mode (recommended):</b> specify only the name; GeoServer reads metadata
 *       automatically from the raster reader. Use {@code CreateCoverageRequest.isNew("mycov")}.</li>
 *   <li><b>Partial-definition mode:</b> specify name plus additional fields explicitly.
 *       Use {@code CreateCoverageRequest.of("mycov").srs("EPSG:4326").enabled(true)}.</li>
 * </ul>
 *
 * <pre>{@code
 * // isNew mode — GeoServer reads metadata automatically from the store reader
 * CreateCoverageRequest.isNew("mycov")
 *
 * // Partial-definition mode — explicit field values
 * CreateCoverageRequest.of("mycov")
 *     .title("My Coverage")
 *     .description("Raster layer from GeoTIFF")
 *     .srs("EPSG:4326")
 *     .projectionPolicy("REPROJECT_TO_DECLARED")
 *     .enabled(true)
 * }</pre>
 */
public class CreateCoverageRequest {

    private final String  name;
    private       String  nativeCoverageName;
    private       String  nativeName;
    private       String  title;
    private       String  description;
    private       String  srs;
    private       String  projectionPolicy;
    private       Boolean enabled;
    private       String  nativeFormat;

    private CreateCoverageRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "coverage name must not be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * isNew mode: only the name is required. GeoServer reads all metadata from the store reader automatically.
     * Use this when the store was uploaded with {@code configure=none}.
     *
     * @param name the coverage name (must not be null or empty)
     * @return a new request in isNew mode
     */
    public static CreateCoverageRequest isNew(String name) {
        return new CreateCoverageRequest(name);
    }

    /**
     * Partial-definition mode: specify the name and any additional fields to set explicitly.
     *
     * @param name the coverage name (must not be null or empty)
     * @return a new request in partial-definition mode
     */
    public static CreateCoverageRequest of(String name) {
        return new CreateCoverageRequest(name);
    }

    /**
     * Alias for {@link #of(String)} (partial-definition mode), for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     *
     * @param name the coverage name
     * @return a new request instance
     */
    public static CreateCoverageRequest builder(String name) {
        return of(name);
    }

    /**
     * Sets the native coverage name.
     * @param nativeCoverageName the native coverage name
     * @return this request
     */
    public CreateCoverageRequest nativeCoverageName(String nativeCoverageName) {
        this.nativeCoverageName = nativeCoverageName;
        return this;
    }

    /**
     * Sets the native name.
     * @param nativeName the native name
     * @return this request
     */
    public CreateCoverageRequest nativeName(String nativeName) {
        this.nativeName = nativeName;
        return this;
    }

    /**
     * Sets the coverage title.
     * @param title the title
     * @return this request
     */
    public CreateCoverageRequest title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the coverage description.
     * @param description the description
     * @return this request
     */
    public CreateCoverageRequest description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the declared CRS code.
     * @param srs the SRS code (e.g. {@code "EPSG:4326"})
     * @return this request
     */
    public CreateCoverageRequest srs(String srs) {
        this.srs = srs;
        return this;
    }

    /**
     * Sets the projection policy.
     * @param projectionPolicy the projection policy (e.g. {@code "REPROJECT_TO_DECLARED"})
     * @return this request
     */
    public CreateCoverageRequest projectionPolicy(String projectionPolicy) {
        this.projectionPolicy = projectionPolicy;
        return this;
    }

    /**
     * Sets whether this coverage is enabled.
     * @param enabled {@code true} to enable
     * @return this request
     */
    public CreateCoverageRequest enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets the native format.
     * @param nativeFormat the native format name
     * @return this request
     */
    public CreateCoverageRequest nativeFormat(String nativeFormat) {
        this.nativeFormat = nativeFormat;
        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateCoverageRequest build() {
        return this;
    }

    /** @return the coverage name */
    public String  getName() {
        return name;
    }
    /** @return the native coverage name */
    public String  getNativeCoverageName() {
        return nativeCoverageName;
    }
    /** @return the native name */
    public String  getNativeName() {
        return nativeName;
    }
    /** @return the title */
    public String  getTitle() {
        return title;
    }
    /** @return the description */
    public String  getDescription() {
        return description;
    }
    /** @return the SRS code */
    public String  getSrs() {
        return srs;
    }
    /** @return the projection policy */
    public String  getProjectionPolicy() {
        return projectionPolicy;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return the native format name */
    public String  getNativeFormat() {
        return nativeFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateCoverageRequest that = (CreateCoverageRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeCoverageName, that.nativeCoverageName)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(nativeFormat, that.nativeFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeCoverageName, nativeName, title, description, srs, projectionPolicy, enabled, nativeFormat);
    }

    @Override
    public String toString() {
        return "CreateCoverageRequest{" +
                "name=" + name +
                ", nativeCoverageName=" + nativeCoverageName +
                ", nativeName=" + nativeName +
                ", title=" + title +
                ", description=" + description +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", nativeFormat=" + nativeFormat +
                '}';
    }
}
