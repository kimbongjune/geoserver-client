package io.github.kimbongjune.geoserverclient.dto.wmtslayer;
import java.util.Objects;

/**
 * Request DTO for publishing a WMTS (cascading) layer.
 *
 * <p>Builds the request body for
 * {@code POST /rest/workspaces/{ws}/wmtsstores/{store}/layers}.
 *
 * <h2>Required fields</h2>
 * <ul>
 *   <li>{@code name} — local layer name to publish</li>
 *   <li>{@code nativeName} — layer name as it appears in the remote WMTS GetCapabilities (e.g. "ws:layerName")</li>
 * </ul>
 */
public class PublishWmtsLayerRequest {

    private final String name;
    private final String nativeName;
    private String title;
    private String description;
    private String abstractText;
    private Boolean enabled;
    private Boolean advertised;
    private String srs;
    private String projectionPolicy;

    private PublishWmtsLayerRequest(String name, String nativeName) {
        this.name = name;
        this.nativeName = nativeName;
    }

    /**
     * Creates a request with the minimum required fields.
     *
     * @param name       local layer name (required)
     * @param nativeName  layer name as it appears in the remote WMTS GetCapabilities (e.g. "ws:layerName")
     * @return a new {@code PublishWmtsLayerRequest}
     */
    public static PublishWmtsLayerRequest of(String name, String nativeName) {
        return new PublishWmtsLayerRequest(name, nativeName);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     *
     * @param name       local layer name (required)
     * @param nativeName layer name in the remote WMTS GetCapabilities
     * @return a new {@code PublishWmtsLayerRequest}
     */
    public static PublishWmtsLayerRequest builder(String name, String nativeName) {
        return of(name, nativeName);
    }

    /**
     * Sets the layer title.
     * @param title the title
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest title(String title) {
        this.title = title; return this;
    }

    /**
     * Sets the layer description.
     * @param description the description
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest description(String description) {
        this.description = description; return this;
    }

    /**
     * Sets the layer abstract text.
     * @param abstractText the abstract text
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest abstractText(String abstractText) {
        this.abstractText = abstractText; return this;
    }

    /**
     * Sets whether the layer is enabled.
     * @param enabled {@code true} to enable
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest enabled(boolean enabled) {
        this.enabled = enabled; return this;
    }

    /**
     * Sets whether the layer is advertised in capabilities.
     * @param advertised {@code true} to advertise
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest advertised(boolean advertised) {
        this.advertised = advertised; return this;
    }

    /**
     * Sets the SRS (spatial reference system).
     * @param srs the SRS (e.g. {@code "EPSG:4326"})
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest srs(String srs) {
        this.srs = srs; return this;
    }

    /**
     * Sets the projection policy.
     * @param policy the projection policy (e.g. {@code "REPROJECT_TO_DECLARED"})
     * @return this instance for chaining
     */
    public PublishWmtsLayerRequest projectionPolicy(String policy) {
        this.projectionPolicy = policy; return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this instance
     */
    public PublishWmtsLayerRequest build() {
        return this;
    }

    /** @return the local layer name */
    public String getName() {
        return name;
    }
    /** @return the native layer name in the remote WMTS */
    public String getNativeName() {
        return nativeName;
    }
    /** @return the layer title */
    public String getTitle() {
        return title;
    }
    /** @return the layer description */
    public String getDescription() {
        return description;
    }
    /** @return the layer abstract text */
    public String getAbstractText() {
        return abstractText;
    }
    /** @return {@code true} if the layer is enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if the layer is advertised in capabilities */
    public Boolean getAdvertised() {
        return advertised;
    }
    /** @return the SRS (e.g. {@code "EPSG:4326"}) */
    public String getSrs() {
        return srs;
    }
    /** @return the projection policy */
    public String getProjectionPolicy() {
        return projectionPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublishWmtsLayerRequest that = (PublishWmtsLayerRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, title, description, abstractText, enabled, advertised, srs, projectionPolicy);
    }

    @Override
    public String toString() {
        return "PublishWmtsLayerRequest{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", title=" + title +
                ", description=" + description +
                ", abstractText=" + abstractText +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                '}';
    }
}
