package io.github.kimbongjune.geoserverclient.dto.wmslayer;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for publishing a WMS (cascading) layer.
 *
 * <p>Builds the request body for
 * {@code POST /rest/workspaces/{ws}/wmsstores/{store}/wmslayers}.
 *
 * <h2>Required fields</h2>
 * <ul>
 *   <li>{@code name} — local layer name to publish</li>
 *   <li>{@code nativeName} — layer name as it appears in the remote WMS GetCapabilities (e.g. "ws:layerName")</li>
 * </ul>
 *
 * <h2>Known bugs (GeoServer 2.28.2)</h2>
 * <ul>
 *   <li>{@code selectedRemoteFormats}: causes 500 on POST ("Duplicate field") — do not use</li>
 * </ul>
 */
public class PublishWmsLayerRequest {

    private final String name;
    private final String nativeName;
    private String title;
    private String description;
    private String abstractText;
    private Boolean enabled;
    private String forcedRemoteStyle;
    private String preferredFormat;
    private Double minScale;
    private Double maxScale;
    private Boolean metadataBBoxRespected;
    private List<String> selectedRemoteStyles;
    private StringMap vendorParameters;
    private String srs;
    private ProjectionPolicy projectionPolicy;

    private PublishWmsLayerRequest(String name, String nativeName) {
        this.name = name;
        this.nativeName = nativeName;
    }

    /**
     * Creates a request with the minimum required fields.
     *
     * @param name       local layer name (required)
     * @param nativeName layer name in the remote WMS GetCapabilities (e.g. "ws:layerName")
     * @return a new {@code PublishWmsLayerRequest}
     */
    public static PublishWmsLayerRequest builder(String name, String nativeName) {
        return new PublishWmsLayerRequest(name, nativeName);
    }

    /**
     * Sets the layer title.
     * @param title the title
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest title(String title) {
        this.title = title; return this;
    }

    /**
     * Sets the layer description.
     * @param description the description
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest description(String description) {
        this.description = description; return this;
    }

    /**
     * Sets the layer abstract text.
     * @param abstractText the abstract text
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest abstractText(String abstractText) {
        this.abstractText = abstractText; return this;
    }

    /**
     * Sets whether the layer is enabled.
     * @param enabled {@code true} to enable
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest enabled(boolean enabled) {
        this.enabled = enabled; return this;
    }

    /**
     * Sets the forced remote style name.
     * @param style the remote style name
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest forcedRemoteStyle(String style) {
        this.forcedRemoteStyle = style; return this;
    }

    /**
     * Sets the preferred remote image format.
     * @param format the MIME type of the preferred format
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest preferredFormat(String format) {
        this.preferredFormat = format; return this;
    }

    /**
     * Sets the minimum scale denominator.
     * @param minScale the minimum scale
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest minScale(double minScale) {
        this.minScale = minScale; return this;
    }

    /**
     * Sets the maximum scale denominator.
     * @param maxScale the maximum scale
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest maxScale(double maxScale) {
        this.maxScale = maxScale; return this;
    }

    /**
     * Sets whether the remote server's bounding box is respected.
     * @param v {@code true} to respect the remote bounding box
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest metadataBBoxRespected(boolean v) {
        this.metadataBBoxRespected = v; return this;
    }

    /**
     * Sets the list of selected remote styles.
     * @param styles the style names to select
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest selectedRemoteStyles(List<String> styles) {
        this.selectedRemoteStyles = styles; return this;
    }

    /**
     * Sets the vendor parameters map.
     * @param params the vendor parameters
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest vendorParameters(StringMap params) {
        this.vendorParameters = params; return this;
    }

    /**
     * Sets the SRS (spatial reference system).
     * @param srs the SRS (e.g. {@code "EPSG:4326"})
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest srs(String srs) {
        this.srs = srs; return this;
    }

    /**
     * Sets the projection policy.
     * @param policy the projection policy (e.g. {@code "REPROJECT_TO_DECLARED"})
     * @return this instance for chaining
     */
    public PublishWmsLayerRequest projectionPolicy(ProjectionPolicy policy) {
        this.projectionPolicy = policy; return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this instance
     */
    public PublishWmsLayerRequest build() {
        return this;
    }

    /** @return the local layer name */
    public String getName() {
        return name;
    }
    /** @return the native layer name in the remote WMS */
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
    /** @return the forced remote style name */
    public String getForcedRemoteStyle() {
        return forcedRemoteStyle;
    }
    /** @return the preferred remote image format */
    public String getPreferredFormat() {
        return preferredFormat;
    }
    /** @return the minimum scale denominator */
    public Double getMinScale() {
        return minScale;
    }
    /** @return the maximum scale denominator */
    public Double getMaxScale() {
        return maxScale;
    }
    /** @return {@code true} if the remote bounding box is respected */
    public Boolean getMetadataBBoxRespected() {
        return metadataBBoxRespected;
    }
    /** @return the selected remote style names */
    public List<String> getSelectedRemoteStyles() {
        return selectedRemoteStyles == null ? null : Collections.unmodifiableList(selectedRemoteStyles);
    }
    /** @return the vendor parameters map */
    public StringMap getVendorParameters() {
        return vendorParameters;
    }
    /** @return the SRS (e.g. {@code "EPSG:4326"}) */
    public String getSrs() {
        return srs;
    }
    /** @return the projection policy */
    public ProjectionPolicy getProjectionPolicy() {
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
        PublishWmsLayerRequest that = (PublishWmsLayerRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(forcedRemoteStyle, that.forcedRemoteStyle)
                && Objects.equals(preferredFormat, that.preferredFormat)
                && Objects.equals(minScale, that.minScale)
                && Objects.equals(maxScale, that.maxScale)
                && Objects.equals(metadataBBoxRespected, that.metadataBBoxRespected)
                && Objects.equals(selectedRemoteStyles, that.selectedRemoteStyles)
                && Objects.equals(vendorParameters, that.vendorParameters)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, title, description, abstractText, enabled, forcedRemoteStyle, preferredFormat, minScale, maxScale, metadataBBoxRespected, selectedRemoteStyles, vendorParameters, srs, projectionPolicy);
    }

    @Override
    public String toString() {
        return "PublishWmsLayerRequest{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", title=" + title +
                ", description=" + description +
                ", abstractText=" + abstractText +
                ", enabled=" + enabled +
                ", forcedRemoteStyle=" + forcedRemoteStyle +
                ", preferredFormat=" + preferredFormat +
                ", minScale=" + minScale +
                ", maxScale=" + maxScale +
                ", metadataBBoxRespected=" + metadataBBoxRespected +
                ", selectedRemoteStyles=" + selectedRemoteStyles +
                ", vendorParameters=" + vendorParameters +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                '}';
    }
}
