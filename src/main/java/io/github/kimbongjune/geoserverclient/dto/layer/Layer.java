package io.github.kimbongjune.geoserverclient.dto.layer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * DTO for layer details.
 *
 * <p>Maps the response body of {@code GET /rest/layers/{layerName}} or
 * {@code GET /rest/workspaces/{ws}/layers/{layerName}}.
 *
 * <p>Verified against GeoServer 2.28.2.
 *
 * <ul>
 *   <li>Layers are created automatically when a Coverage or FeatureType is POSTed with
 *       {@code configure=first} — there is no REST POST endpoint for layers directly.</li>
 *   <li>{@code enabled} and {@code advertised} can be changed via PUT but are not included in GET responses.</li>
 *   <li>{@code attribution} is always present (returns only {@code logoWidth=0, logoHeight=0} when empty).</li>
 *   <li>{@code styles}: if omitted on PUT, existing styles are preserved (unlike {@code authorityURLs}/{@code identifiers}).</li>
 *   <li>{@code authorityURLs} and {@code identifiers}: if omitted on PUT, existing values are deleted (full replace).</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Layer {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("path")
    private String path;

    @JsonProperty("defaultStyle")
    private StyleRef defaultStyle;

    @JsonProperty("styles")
    private Styles styles;

    @JsonProperty("resource")
    private ResourceRef resource;

    @JsonProperty("legend")
    private Legend legend;

    @JsonProperty("queryable")
    private Boolean queryable;

    @JsonProperty("opaque")
    private Boolean opaque;

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("attribution")
    private Attribution attribution;

    @JsonProperty("authorityURLs")
    private AuthorityURLs authorityURLs;

    @JsonProperty("identifiers")
    private Identifiers identifiers;

    @JsonProperty("defaultWMSInterpolationMethod")
    private String defaultWMSInterpolationMethod;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    /** Constructs an empty {@code Layer} for deserialization. */
    public Layer() {}

    /** @return the layer name */
    public String getName() {
        return name;
    }
    /** @return the layer type (e.g. {@code "RASTER"} or {@code "VECTOR"}) */
    public String getType() {
        return type;
    }
    /** @return the layer path */
    public String getPath() {
        return path;
    }
    /** @return the default style reference */
    public StyleRef getDefaultStyle() {
        return defaultStyle;
    }
    /** @return the collection of additional styles */
    public Styles getStyles() {
        return styles;
    }
    /** @return the resource reference (coverage, featureType, etc.) */
    public ResourceRef getResource() {
        return resource;
    }
    /** @return the legend graphic definition */
    public Legend getLegend() {
        return legend;
    }
    /** @return the queryable flag as a {@code Boolean} (may be null) */
    public Boolean getQueryable() {
        return queryable;
    }
    /** @return {@code true} if the layer is queryable */
    public boolean isQueryable() {
        return Boolean.TRUE.equals(queryable);
    }
    /** @return the opaque flag as a {@code Boolean} (may be null) */
    public Boolean getOpaque() {
        return opaque;
    }
    /** @return {@code true} if the layer is opaque */
    public boolean isOpaque() {
        return Boolean.TRUE.equals(opaque);
    }
    /** @return the metadata entries */
    public Metadata getMetadata() {
        return metadata;
    }
    /** @return the attribution information */
    public Attribution getAttribution() {
        return attribution;
    }
    /** @return the authority URLs */
    public AuthorityURLs getAuthorityURLs() {
        return authorityURLs;
    }
    /** @return the identifiers */
    public Identifiers getIdentifiers() {
        return identifiers;
    }
    /** @return the default WMS interpolation method */
    public String getDefaultWMSInterpolationMethod() {
        return defaultWMSInterpolationMethod;
    }
    /** @return the date this layer was created */
    public String getDateCreated() {
        return dateCreated;
    }
    /** @return the date this layer was last modified */
    public String getDateModified() {
        return dateModified;
    }

    // Inner DTOs

    /**
     * Reference to a style used by a layer.
     * Used for {@code defaultStyle} and items in {@code styles.style}.
     * <pre>{"name": "raster", "href": "...", ["workspace": "wsname"]}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StyleRef {
        @JsonProperty("name")      private String name;
        @JsonProperty("href")      private String href;
        @JsonProperty("workspace") private String workspace;

        /** Constructs an empty {@code StyleRef} for deserialization. */
        public StyleRef() {}
        /** @return the style name */
        public String getName() {
            return name;
        }
        /** @return the href to the style resource */
        public String getHref() {
            return href;
        }
        /** @return the workspace name, or {@code null} if global */
        public String getWorkspace() {
            return workspace;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StyleRef that = (StyleRef) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(href, that.href)
                    && Objects.equals(workspace, that.workspace);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, href, workspace);
        }

        @Override
        public String toString() {
            return "StyleRef{" +
                    "name=" + name +
                    ", href=" + href +
                    ", workspace=" + workspace +
                    '}';
        }
    }

    /**
     * Container for the additional styles of a layer.
     * <pre>{"@class": "linked-hash-set", "style": [StyleRef...]}</pre>
     * The {@code style} value may be a single object or an array.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Styles {
        @JsonProperty("@class") private String styleClass;
        @JsonProperty("style")  private Object style;  // StyleRef or List<StyleRef>

        /** Constructs an empty {@code Styles} for deserialization. */
        public Styles() {}
        /** @return the style collection class (e.g. {@code "linked-hash-set"}) */
        public String getStyleClass() {
            return styleClass;
        }
        /** @return a single {@code StyleRef} or a {@code List} of {@code StyleRef} */
        public Object getStyle() {
            return style;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Styles that = (Styles) o;
            return Objects.equals(styleClass, that.styleClass)
                    && Objects.equals(style, that.style);
        }

        @Override
        public int hashCode() {
            return Objects.hash(styleClass, style);
        }

        @Override
        public String toString() {
            return "Styles{" +
                    "styleClass=" + styleClass +
                    ", style=" + style +
                    '}';
        }
    }

    /**
     * Reference to the underlying resource of a layer.
     * <pre>{"@class": "coverage", "name": "ws:name", "href": "..."}</pre>
     * The {@code @class} value is one of: "coverage", "featureType", "wmsLayer", "wmtsLayer".
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResourceRef {
        @JsonProperty("@class") private String resourceClass;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;

        /** Constructs an empty {@code ResourceRef} for deserialization. */
        public ResourceRef() {}
        /** @return the resource class (e.g. {@code "coverage"} or {@code "featureType"}) */
        public String getResourceClass() {
            return resourceClass;
        }
        /** @return the resource name (e.g. {@code "ws:layername"}) */
        public String getName() {
            return name;
        }
        /** @return the href to the resource */
        public String getHref() {
            return href;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ResourceRef that = (ResourceRef) o;
            return Objects.equals(resourceClass, that.resourceClass)
                    && Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(resourceClass, name, href);
        }

        @Override
        public String toString() {
            return "ResourceRef{" +
                    "resourceClass=" + resourceClass +
                    ", name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /**
     * Legend graphic definition for a layer.
     * <pre>{"width": 20, "height": 20, "format": "image/png", "onlineResource": "..."}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Legend {
        @JsonProperty("width")          private Integer width;
        @JsonProperty("height")         private Integer height;
        @JsonProperty("format")         private String  format;
        @JsonProperty("onlineResource") private String  onlineResource;

        /** Constructs an empty {@code Legend} for deserialization. */
        public Legend() {}
        /** @return the legend image width in pixels */
        public Integer getWidth() {
            return width;
        }
        /** @return the legend image height in pixels */
        public Integer getHeight() {
            return height;
        }
        /** @return the legend image MIME type (e.g. {@code "image/png"}) */
        public String  getFormat() {
            return format;
        }
        /** @return the URL to the legend graphic */
        public String  getOnlineResource() {
            return onlineResource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Legend that = (Legend) o;
            return Objects.equals(width, that.width)
                    && Objects.equals(height, that.height)
                    && Objects.equals(format, that.format)
                    && Objects.equals(onlineResource, that.onlineResource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height, format, onlineResource);
        }

        @Override
        public String toString() {
            return "Legend{" +
                    "width=" + width +
                    ", height=" + height +
                    ", format=" + format +
                    ", onlineResource=" + onlineResource +
                    '}';
        }
    }

    /**
     * Metadata container for a layer.
     * A single entry is: {@code {"entry": {"@key":"K","$":"V"}}}
     * Multiple entries are: {@code {"entry": [{"@key":"K1","$":"V1"}, ...]}}
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("entry") private Object entry;

        /** Constructs an empty {@code Metadata} for deserialization. */
        public Metadata() {}
        /** @return a single metadata entry map or a list of entry maps */
        public Object getEntry() {
            return entry;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Metadata that = (Metadata) o;
            return Objects.equals(entry, that.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "entry=" + entry +
                    '}';
        }
    }

    /**
     * Attribution information for a layer. Always present in GET responses.
     * <pre>{"title":"...", "href":"...", "logoURL":"...", "logoWidth":0, "logoHeight":0, "logoType":"..."}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attribution {
        @JsonProperty("title")      private String  title;
        @JsonProperty("href")       private String  href;
        @JsonProperty("logoURL")    private String  logoURL;
        @JsonProperty("logoWidth")  private Integer logoWidth;
        @JsonProperty("logoHeight") private Integer logoHeight;
        @JsonProperty("logoType")   private String  logoType;

        /** Constructs an empty {@code Attribution} for deserialization. */
        public Attribution() {}
        /** @return the attribution title */
        public String  getTitle() {
            return title;
        }
        /** @return the attribution URL */
        public String  getHref() {
            return href;
        }
        /** @return the logo image URL */
        public String  getLogoURL() {
            return logoURL;
        }
        /** @return the logo image width in pixels */
        public Integer getLogoWidth() {
            return logoWidth;
        }
        /** @return the logo image height in pixels */
        public Integer getLogoHeight() {
            return logoHeight;
        }
        /** @return the logo image MIME type */
        public String  getLogoType() {
            return logoType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Attribution that = (Attribution) o;
            return Objects.equals(title, that.title)
                    && Objects.equals(href, that.href)
                    && Objects.equals(logoURL, that.logoURL)
                    && Objects.equals(logoWidth, that.logoWidth)
                    && Objects.equals(logoHeight, that.logoHeight)
                    && Objects.equals(logoType, that.logoType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, href, logoURL, logoWidth, logoHeight, logoType);
        }

        @Override
        public String toString() {
            return "Attribution{" +
                    "title=" + title +
                    ", href=" + href +
                    ", logoURL=" + logoURL +
                    ", logoWidth=" + logoWidth +
                    ", logoHeight=" + logoHeight +
                    ", logoType=" + logoType +
                    '}';
        }
    }

    /**
     * A single authority URL entry.
     * <pre>{"name": "EPSG", "href": "http://www.epsg.org/"}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorityURLEntry {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;

        /** Constructs an empty {@code AuthorityURLEntry} for deserialization. */
        public AuthorityURLEntry() {}
        /** @return the authority name (e.g. {@code "EPSG"}) */
        public String getName() {
            return name;
        }
        /** @return the authority URL */
        public String getHref() {
            return href;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AuthorityURLEntry that = (AuthorityURLEntry) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, href);
        }

        @Override
        public String toString() {
            return "AuthorityURLEntry{" +
                    "name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /**
     * Container for authority URL entries.
     * <pre>{"AuthorityURL": [AuthorityURLEntry...]}</pre>
     * The {@code AuthorityURL} value may be a single object or an array.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorityURLs {
        @JsonProperty("AuthorityURL") private Object authorityURL;  // single or List

        /** Constructs an empty {@code AuthorityURLs} for deserialization. */
        public AuthorityURLs() {}
        /** @return a single {@code AuthorityURLEntry} or a {@code List} of entries */
        public Object getAuthorityURL() {
            return authorityURL;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AuthorityURLs that = (AuthorityURLs) o;
            return Objects.equals(authorityURL, that.authorityURL);
        }

        @Override
        public int hashCode() {
            return Objects.hash(authorityURL);
        }

        @Override
        public String toString() {
            return "AuthorityURLs{" +
                    "authorityURL=" + authorityURL +
                    '}';
        }
    }

    /**
     * A single authority/identifier pair.
     * <pre>{"authority": "EPSG", "identifier": "4326"}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IdentifierEntry {
        @JsonProperty("authority")   private String authority;
        @JsonProperty("identifier")  private String identifier;

        /** Constructs an empty {@code IdentifierEntry} for deserialization. */
        public IdentifierEntry() {}
        /** @return the authority name (e.g. {@code "EPSG"}) */
        public String getAuthority() {
            return authority;
        }
        /** @return the identifier value (e.g. {@code "4326"}) */
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IdentifierEntry that = (IdentifierEntry) o;
            return Objects.equals(authority, that.authority)
                    && Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(authority, identifier);
        }

        @Override
        public String toString() {
            return "IdentifierEntry{" +
                    "authority=" + authority +
                    ", identifier=" + identifier +
                    '}';
        }
    }

    /**
     * Container for a list of identifier entries.
     * <pre>{"Identifier": [IdentifierEntry...]}</pre>
     * The {@code Identifier} key may be a single object or an array.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Identifiers {
        @JsonProperty("Identifier") private Object identifier;  // single or List

        /** Constructs an empty {@code Identifiers} for deserialization. */
        public Identifiers() {}
        /** @return a single {@code IdentifierEntry} or a {@code List} of entries */
        public Object getIdentifier() {
            return identifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identifiers that = (Identifiers) o;
            return Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identifier);
        }

        @Override
        public String toString() {
            return "Identifiers{" +
                    "identifier=" + identifier +
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
        Layer that = (Layer) o;
        return Objects.equals(name, that.name)
                && Objects.equals(type, that.type)
                && Objects.equals(path, that.path)
                && Objects.equals(defaultStyle, that.defaultStyle)
                && Objects.equals(styles, that.styles)
                && Objects.equals(resource, that.resource)
                && Objects.equals(legend, that.legend)
                && Objects.equals(queryable, that.queryable)
                && Objects.equals(opaque, that.opaque)
                && Objects.equals(metadata, that.metadata)
                && Objects.equals(attribution, that.attribution)
                && Objects.equals(authorityURLs, that.authorityURLs)
                && Objects.equals(identifiers, that.identifiers)
                && Objects.equals(defaultWMSInterpolationMethod, that.defaultWMSInterpolationMethod)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, path, defaultStyle, styles, resource, legend, queryable, opaque, metadata, attribution, authorityURLs, identifiers, defaultWMSInterpolationMethod, dateCreated, dateModified);
    }

    @Override
    public String toString() {
        return "Layer{" +
                "name=" + name +
                ", type=" + type +
                ", path=" + path +
                ", defaultStyle=" + defaultStyle +
                ", styles=" + styles +
                ", resource=" + resource +
                ", legend=" + legend +
                ", queryable=" + queryable +
                ", opaque=" + opaque +
                ", metadata=" + metadata +
                ", attribution=" + attribution +
                ", authorityURLs=" + authorityURLs +
                ", identifiers=" + identifiers +
                ", defaultWMSInterpolationMethod=" + defaultWMSInterpolationMethod +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }
}
