package io.github.kimbongjune.geoserverclient.dto.layer;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;
import java.util.List;

/**
 * Request DTO for updating a layer.
 *
 * <p>Builds the request body for {@code PUT /rest/layers/{layerName}} or
 * {@code PUT /rest/workspaces/{ws}/layers/{layerName}}.
 *
 * <p>Supports partial updates — only set the fields you want to change.
 *
 * <h2>Field notes</h2>
 * <ul>
 *   <li>{@code defaultStyleName}: sets the default style. Pair with {@code defaultStyleWorkspace}
 *       when the style belongs to a specific workspace.</li>
 *   <li>{@code styleNames}: sets additional styles. {@code null} leaves existing styles unchanged;
 *       an empty list clears them.</li>
 *   <li>{@code queryable}: whether GetFeatureInfo is enabled (default {@code true}).</li>
 *   <li>{@code opaque}: whether the layer is opaque (default {@code false}).</li>
 *   <li>{@code enabled} and {@code advertised}: can be changed via PUT but are not returned in GET responses.</li>
 *   <li>{@code attribution}: sending an attribution on PUT is reflected in subsequent GETs.</li>
 *   <li>{@code defaultWMSInterpolationMethod}: one of Nearest, Bilinear, or Bicubic.</li>
 * </ul>
 */
public class UpdateLayerRequest {

    private final String defaultStyleName;
    private final String defaultStyleWorkspace;
    private final List<String> styleNames;  // null = don't change; empty list = clear
    private final Boolean queryable;
    private final Boolean opaque;
    private final Boolean enabled;
    private final Boolean advertised;
    private final String path;
    private final String attributionTitle;
    private final String attributionHref;
    private final String defaultWMSInterpolationMethod;

    private UpdateLayerRequest(Builder builder) {
        if (builder.defaultStyleName == null && builder.styleNames == null
                && builder.queryable == null && builder.opaque == null
                && builder.enabled == null && builder.advertised == null
                && builder.path == null && builder.attributionTitle == null
                && builder.attributionHref == null
                && builder.defaultWMSInterpolationMethod == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.defaultStyleName            = builder.defaultStyleName;
        this.defaultStyleWorkspace       = builder.defaultStyleWorkspace;
        this.styleNames                  = builder.styleNames;
        this.queryable                   = builder.queryable;
        this.opaque                      = builder.opaque;
        this.enabled                     = builder.enabled;
        this.advertised                  = builder.advertised;
        this.path                        = builder.path;
        this.attributionTitle            = builder.attributionTitle;
        this.attributionHref             = builder.attributionHref;
        this.defaultWMSInterpolationMethod = builder.defaultWMSInterpolationMethod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDefaultStyleName() {
        return defaultStyleName;
    }
    public String getDefaultStyleWorkspace() {
        return defaultStyleWorkspace;
    }
    public List<String> getStyleNames() {
        return styleNames;
    }
    public Boolean getQueryable() {
        return queryable;
    }
    public Boolean getOpaque() {
        return opaque;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public Boolean getAdvertised() {
        return advertised;
    }
    public String getPath() {
        return path;
    }
    public String getAttributionTitle() {
        return attributionTitle;
    }
    public String getAttributionHref() {
        return attributionHref;
    }
    public String getDefaultWMSInterpolationMethod() {
        return defaultWMSInterpolationMethod;
    }

    public static class Builder {
        private String defaultStyleName;
        private String defaultStyleWorkspace;
        private List<String> styleNames;
        private Boolean queryable;
        private Boolean opaque;
        private Boolean enabled;
        private Boolean advertised;
        private String path;
        private String attributionTitle;
        private String attributionHref;
        private String defaultWMSInterpolationMethod;

        /**    ( ). */
        public Builder defaultStyleName(String name) {
            this.defaultStyleName = name;
            return this;
        }

        /**    +  ( ). */
        public Builder defaultStyle(String name, String workspace) {
            this.defaultStyleName      = name;
            this.defaultStyleWorkspace = workspace;
            return this;
        }

        /**   . null  ;     . */
        public Builder styleNames(List<String> styleNames) {
            this.styleNames = styleNames;
            return this;
        }

        public Builder queryable(boolean queryable) {
            this.queryable = queryable;
            return this;
        }

        public Builder opaque(boolean opaque) {
            this.opaque = opaque;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder advertised(boolean advertised) {
            this.advertised = advertised;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /** attribution  + URL . */
        public Builder attribution(String title, String href) {
            this.attributionTitle = title;
            this.attributionHref  = href;
            return this;
        }

        /** WMS  : Nearest | Bilinear | Bicubic. */
        public Builder defaultWMSInterpolationMethod(String method) {
            this.defaultWMSInterpolationMethod = method;
            return this;
        }

        public UpdateLayerRequest build() {
            return new UpdateLayerRequest(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Builder that = (Builder) o;
            return Objects.equals(defaultStyleName, that.defaultStyleName)
                    && Objects.equals(defaultStyleWorkspace, that.defaultStyleWorkspace)
                    && Objects.equals(styleNames, that.styleNames)
                    && Objects.equals(queryable, that.queryable)
                    && Objects.equals(opaque, that.opaque)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(advertised, that.advertised)
                    && Objects.equals(path, that.path)
                    && Objects.equals(attributionTitle, that.attributionTitle)
                    && Objects.equals(attributionHref, that.attributionHref)
                    && Objects.equals(defaultWMSInterpolationMethod, that.defaultWMSInterpolationMethod);
        }

        @Override
        public int hashCode() {
            return Objects.hash(defaultStyleName, defaultStyleWorkspace, styleNames, queryable, opaque, enabled, advertised, path, attributionTitle, attributionHref, defaultWMSInterpolationMethod);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "defaultStyleName=" + defaultStyleName +
                    ", defaultStyleWorkspace=" + defaultStyleWorkspace +
                    ", styleNames=" + styleNames +
                    ", queryable=" + queryable +
                    ", opaque=" + opaque +
                    ", enabled=" + enabled +
                    ", advertised=" + advertised +
                    ", path=" + path +
                    ", attributionTitle=" + attributionTitle +
                    ", attributionHref=" + attributionHref +
                    ", defaultWMSInterpolationMethod=" + defaultWMSInterpolationMethod +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateLayerRequest that = (UpdateLayerRequest) o;
        return Objects.equals(defaultStyleName, that.defaultStyleName)
                && Objects.equals(defaultStyleWorkspace, that.defaultStyleWorkspace)
                && Objects.equals(styleNames, that.styleNames)
                && Objects.equals(queryable, that.queryable)
                && Objects.equals(opaque, that.opaque)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(path, that.path)
                && Objects.equals(attributionTitle, that.attributionTitle)
                && Objects.equals(attributionHref, that.attributionHref)
                && Objects.equals(defaultWMSInterpolationMethod, that.defaultWMSInterpolationMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultStyleName, defaultStyleWorkspace, styleNames, queryable, opaque, enabled, advertised, path, attributionTitle, attributionHref, defaultWMSInterpolationMethod);
    }

    @Override
    public String toString() {
        return "UpdateLayerRequest{" +
                "defaultStyleName=" + defaultStyleName +
                ", defaultStyleWorkspace=" + defaultStyleWorkspace +
                ", styleNames=" + styleNames +
                ", queryable=" + queryable +
                ", opaque=" + opaque +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", path=" + path +
                ", attributionTitle=" + attributionTitle +
                ", attributionHref=" + attributionHref +
                ", defaultWMSInterpolationMethod=" + defaultWMSInterpolationMethod +
                '}';
    }
}
