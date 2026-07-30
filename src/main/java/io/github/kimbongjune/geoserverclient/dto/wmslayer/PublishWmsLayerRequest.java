package io.github.kimbongjune.geoserverclient.dto.wmslayer;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for publishing a WMS (cascading) layer.
 *
 * <p>Builds the request body for
 * {@code POST /rest/workspaces/{ws}/wmsstores/{store}/wmslayers}.</p>
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
    private String projectionPolicy;

    private PublishWmsLayerRequest(String name, String nativeName) {
        this.name = name;
        this.nativeName = nativeName;
    }

    /**
     * Creates a request with the minimum required fields.
     *
     * @param name       local layer name (required)
     * @param nativeName layer name in the remote WMS GetCapabilities (e.g. "ws:layerName")
     */
    public static PublishWmsLayerRequest of(String name, String nativeName) {
        return new PublishWmsLayerRequest(name, nativeName);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     */
    public static PublishWmsLayerRequest builder(String name, String nativeName) {
        return of(name, nativeName);
    }

    public PublishWmsLayerRequest title(String title)                           { this.title = title; return this; }
    public PublishWmsLayerRequest description(String description)               { this.description = description; return this; }
    public PublishWmsLayerRequest abstractText(String abstractText)             { this.abstractText = abstractText; return this; }
    public PublishWmsLayerRequest enabled(boolean enabled)                      { this.enabled = enabled; return this; }
    public PublishWmsLayerRequest forcedRemoteStyle(String style)               { this.forcedRemoteStyle = style; return this; }
    public PublishWmsLayerRequest preferredFormat(String format)                { this.preferredFormat = format; return this; }
    public PublishWmsLayerRequest minScale(double minScale)                     { this.minScale = minScale; return this; }
    public PublishWmsLayerRequest maxScale(double maxScale)                     { this.maxScale = maxScale; return this; }
    public PublishWmsLayerRequest metadataBBoxRespected(boolean v)              { this.metadataBBoxRespected = v; return this; }
    public PublishWmsLayerRequest selectedRemoteStyles(List<String> styles)     { this.selectedRemoteStyles = styles; return this; }
    public PublishWmsLayerRequest vendorParameters(StringMap params)            { this.vendorParameters = params; return this; }
    public PublishWmsLayerRequest srs(String srs)                               { this.srs = srs; return this; }
    public PublishWmsLayerRequest projectionPolicy(String policy)               { this.projectionPolicy = policy; return this; }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public PublishWmsLayerRequest build() { return this; }

    public String getName()                         { return name; }
    public String getNativeName()                   { return nativeName; }
    public String getTitle()                        { return title; }
    public String getDescription()                  { return description; }
    public String getAbstractText()                 { return abstractText; }
    public Boolean getEnabled()                     { return enabled; }
    public String getForcedRemoteStyle()            { return forcedRemoteStyle; }
    public String getPreferredFormat()              { return preferredFormat; }
    public Double getMinScale()                     { return minScale; }
    public Double getMaxScale()                     { return maxScale; }
    public Boolean getMetadataBBoxRespected()       { return metadataBBoxRespected; }
    public List<String> getSelectedRemoteStyles()   { return selectedRemoteStyles == null ? null : Collections.unmodifiableList(selectedRemoteStyles); }
    public StringMap getVendorParameters()             { return vendorParameters; }
    public String getSrs()                          { return srs; }
    public String getProjectionPolicy()             { return projectionPolicy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
