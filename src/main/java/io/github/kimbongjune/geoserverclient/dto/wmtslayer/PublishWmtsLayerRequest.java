package io.github.kimbongjune.geoserverclient.dto.wmtslayer;
import java.util.Objects;

/**
 * Request DTO for publishing a WMTS (cascading) layer.
 *
 * <p>Builds the request body for
 * {@code POST /rest/workspaces/{ws}/wmtsstores/{store}/layers}.</p>
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
     */
    public static PublishWmtsLayerRequest of(String name, String nativeName) {
        return new PublishWmtsLayerRequest(name, nativeName);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     */
    public static PublishWmtsLayerRequest builder(String name, String nativeName) {
        return of(name, nativeName);
    }

    public PublishWmtsLayerRequest title(String title)               { this.title = title; return this; }
    public PublishWmtsLayerRequest description(String description)   { this.description = description; return this; }
    public PublishWmtsLayerRequest abstractText(String abstractText) { this.abstractText = abstractText; return this; }
    public PublishWmtsLayerRequest enabled(boolean enabled)          { this.enabled = enabled; return this; }
    public PublishWmtsLayerRequest advertised(boolean advertised)    { this.advertised = advertised; return this; }
    public PublishWmtsLayerRequest srs(String srs)                   { this.srs = srs; return this; }
    public PublishWmtsLayerRequest projectionPolicy(String policy)   { this.projectionPolicy = policy; return this; }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public PublishWmtsLayerRequest build() { return this; }

    public String getName()              { return name; }
    public String getNativeName()        { return nativeName; }
    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public String getAbstractText()      { return abstractText; }
    public Boolean getEnabled()          { return enabled; }
    public Boolean getAdvertised()       { return advertised; }
    public String getSrs()               { return srs; }
    public String getProjectionPolicy()  { return projectionPolicy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
