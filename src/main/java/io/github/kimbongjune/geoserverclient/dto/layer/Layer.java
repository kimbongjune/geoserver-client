package io.github.kimbongjune.geoserverclient.dto.layer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * DTO for layer details.
 *
 * <p>Maps the response body of {@code GET /rest/layers/{layerName}} or
 * {@code GET /rest/workspaces/{ws}/layers/{layerName}}.</p>
 *
 * <p>Verified against GeoServer 2.28.2.</p>
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

    public Layer() {}

    public String getName()                           { return name; }
    public String getType()                           { return type; }
    public String getPath()                           { return path; }
    public StyleRef getDefaultStyle()                 { return defaultStyle; }
    public Styles getStyles()                         { return styles; }
    public ResourceRef getResource()                  { return resource; }
    public Legend getLegend()                         { return legend; }
    public Boolean getQueryable()                     { return queryable; }
    public boolean isQueryable()                     { return Boolean.TRUE.equals(queryable); }
    public Boolean getOpaque()                        { return opaque; }
    public boolean isOpaque()                        { return Boolean.TRUE.equals(opaque); }
    public Metadata getMetadata()                     { return metadata; }
    public Attribution getAttribution()               { return attribution; }
    public AuthorityURLs getAuthorityURLs()           { return authorityURLs; }
    public Identifiers getIdentifiers()               { return identifiers; }
    public String getDefaultWMSInterpolationMethod()  { return defaultWMSInterpolationMethod; }
    public String getDateCreated()                    { return dateCreated; }
    public String getDateModified()                   { return dateModified; }

    //  Inner DTOs 

    /**
     *  . defaultStyle  styles.style   .
     * <pre>{"name": "raster", "href": "...", ["workspace": "wsname"]}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StyleRef {
        @JsonProperty("name")      private String name;
        @JsonProperty("href")      private String href;
        @JsonProperty("workspace") private String workspace;

        public StyleRef() {}
        public String getName()      { return name; }
        public String getHref()      { return href; }
        public String getWorkspace() { return workspace; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     *   .
     * <pre>{"@class": "linked-hash-set", "style": [StyleRef...]}</pre>
     * {@code style}       .
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Styles {
        @JsonProperty("@class") private String styleClass;
        @JsonProperty("style")  private Object style;  // StyleRef or List<StyleRef>

        public Styles() {}
        public String getStyleClass() { return styleClass; }
        public Object getStyle()      { return style; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     *    .
     * <pre>{"@class": "coverage", "name": "ws:name", "href": "..."}</pre>
     * {@code @class} : "coverage" | "featureType" | "wmsLayer" | "wmtsLayer"
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResourceRef {
        @JsonProperty("@class") private String resourceClass;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;

        public ResourceRef() {}
        public String getResourceClass() { return resourceClass; }
        public String getName()          { return name; }
        public String getHref()          { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     *  .
     * <pre>{"width": 20, "height": 20, "format": "image/png", "onlineResource": "..."}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Legend {
        @JsonProperty("width")          private Integer width;
        @JsonProperty("height")         private Integer height;
        @JsonProperty("format")         private String  format;
        @JsonProperty("onlineResource") private String  onlineResource;

        public Legend() {}
        public Integer getWidth()         { return width; }
        public Integer getHeight()        { return height; }
        public String  getFormat()        { return format; }
        public String  getOnlineResource(){ return onlineResource; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     * metadata entry .
     * <p> : {@code {"entry": {"@key":"K","$":"V"}}}<br>
     *  : {@code {"entry": [{"@key":"K1","$":"V1"}, ...]}}</p>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("entry") private Object entry;

        public Metadata() {}
        public Object getEntry() { return entry; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     * Attribution . GET   .
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

        public Attribution() {}
        public String  getTitle()      { return title; }
        public String  getHref()       { return href; }
        public String  getLogoURL()    { return logoURL; }
        public Integer getLogoWidth()  { return logoWidth; }
        public Integer getLogoHeight() { return logoHeight; }
        public String  getLogoType()   { return logoType; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     * AuthorityURL .
     * <pre>{"name": "EPSG", "href": "http://www.epsg.org/"}</pre>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorityURLEntry {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;

        public AuthorityURLEntry() {}
        public String getName() { return name; }
        public String getHref() { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
     * AuthorityURLs  .
     * <pre>{"AuthorityURL": [AuthorityURLEntry...]}</pre>
     * {@code AuthorityURL}       .
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorityURLs {
        @JsonProperty("AuthorityURL") private Object authorityURL;  // single or List

        public AuthorityURLs() {}
        public Object getAuthorityURL() { return authorityURL; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

        public IdentifierEntry() {}
        public String getAuthority()  { return authority; }
        public String getIdentifier() { return identifier; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

        public Identifiers() {}
        public Object getIdentifier() { return identifier; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
