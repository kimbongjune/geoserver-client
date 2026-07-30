package io.github.kimbongjune.geoserverclient.dto.layergroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO for layer group details.
 *
 * <p>Maps the response body of {@code GET /rest/layergroups/{name}} or
 * {@code GET /rest/workspaces/{ws}/layergroups/{name}}.</p>
 *
 * <p>Quirks verified against GeoServer 2.28.2:
 * <ul>
 *   <li>{@code abstractTxt} — the field name in GET responses. POST/PUT uses "abstract" instead.</li>
 *   <li>{@code publishables.published} — may be a single publishable object or an array.</li>
 *   <li>{@code styles.style} — may be an empty string "", a single object, or a mixed array;
 *       deserialisation handles all forms.</li>
 *   <li>{@code enabled} and {@code advertised} — not included in GET responses (unlike the Layer API).</li>
 *   <li>{@code bounds.crs} — may be a plain EPSG string or a {@code {"@class":"...","$":"EPSG:xxx"}} object.</li>
 * </ul>
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LayerGroup {

    @JsonProperty("name")
    private String name;

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("title")
    private String title;

    @JsonProperty("abstractTxt")
    private String abstractText;

    @JsonProperty("internationalTitle")
    private StringMap internationalTitle;

    @JsonProperty("internationalAbstract")
    private StringMap internationalAbstract;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("advertised")
    private Boolean advertised;

    @JsonProperty("workspace")
    private WorkspaceRef workspace;

    @JsonProperty("publishables")
    private Publishables publishables;

    @JsonProperty("styles")
    private Styles styles;

    @JsonProperty("bounds")
    private Bounds bounds;

    @JsonProperty("keywords")
    private Keywords keywords;

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("rootLayer")
    private PublishedRef rootLayer;

    @JsonProperty("rootLayerStyle")
    private StyleRef rootLayerStyle;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    public LayerGroup() {}

    public String getName()                           { return name; }
    public String getMode()                           { return mode; }
    public String getTitle()                          { return title; }
    public String getAbstractText()                   { return abstractText; }
    public StringMap getInternationalTitle()            { return internationalTitle; }
    public StringMap getInternationalAbstract()           { return internationalAbstract; }
    public Boolean getEnabled()                       { return enabled; }
    public boolean isEnabled()                        { return Boolean.TRUE.equals(enabled); }
    public Boolean getAdvertised()                    { return advertised; }
    public boolean isAdvertised()                     { return Boolean.TRUE.equals(advertised); }
    public WorkspaceRef getWorkspace()                { return workspace; }
    public Publishables getPublishables()             { return publishables; }
    public Styles getStyles()                         { return styles; }
    public Bounds getBounds()                         { return bounds; }
    public Keywords getKeywords()                     { return keywords; }
    public Metadata getMetadata()                     { return metadata; }
    public PublishedRef getRootLayer()                { return rootLayer; }
    public StyleRef getRootLayerStyle()               { return rootLayerStyle; }
    public String getDateCreated()                    { return dateCreated; }
    public String getDateModified()                   { return dateModified; }

    // Inner DTOs

    /** Workspace reference in the form {@code {"name":"myws"}}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceRef {
        @JsonProperty("name") private String name;
        public WorkspaceRef() {}
        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkspaceRef that = (WorkspaceRef) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "WorkspaceRef{" +
                    "name=" + name +
                    '}';
        }
    }

    /**
     * Wrapper for the publishables list.
     * The {@code published} field may be a single publishable object or an array.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Publishables {
        @JsonProperty("published")
        @JsonDeserialize(using = PublishedListDeserializer.class)
        private List<PublishedRef> published;

        public Publishables() {}
        public List<PublishedRef> getPublished() {
            return published != null ? published : Collections.<PublishedRef>emptyList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Publishables that = (Publishables) o;
            return Objects.equals(published, that.published);
        }

        @Override
        public int hashCode() {
            return Objects.hash(published);
        }

        @Override
        public String toString() {
            return "Publishables{" +
                    "published=" + published +
                    '}';
        }
    }

    /** A single publishable entry within the publishables list. {@code type} is "layer" or "layerGroup". */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublishedRef {
        @JsonProperty("@type") private String type;
        @JsonProperty("name")  private String name;
        @JsonProperty("href")  private String href;

        public PublishedRef() {}
        public String getType() { return type; }
        public String getName() { return name; }
        public String getHref() { return href; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PublishedRef that = (PublishedRef) o;
            return Objects.equals(type, that.type)
                    && Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, name, href);
        }

        @Override
        public String toString() {
            return "PublishedRef{" +
                    "type=" + type +
                    ", name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /**
     * Wrapper for the styles list.
     * The {@code style} field may be an empty string (""), a single object, or a mixed array;
     * {@link StyleListDeserializer} handles all forms.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Styles {
        @JsonProperty("style")
        @JsonDeserialize(using = StyleListDeserializer.class)
        private List<StyleRef> style;

        public Styles() {}
        public List<StyleRef> getStyle() {
            return style != null ? style : Collections.<StyleRef>emptyList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Styles that = (Styles) o;
            return Objects.equals(style, that.style);
        }

        @Override
        public int hashCode() {
            return Objects.hash(style);
        }

        @Override
        public String toString() {
            return "Styles{" +
                    "style=" + style +
                    '}';
        }
    }

    /**
     * A style reference entry.
     * The default (placeholder) style has a {@code null} or empty {@code name} (deserialised from an empty string).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StyleRef {
        @JsonProperty("name")      private String name;
        @JsonProperty("href")      private String href;
        @JsonProperty("workspace") private String workspace;

        public StyleRef() {}
        public StyleRef(String name) { this.name = name; }

        public String getName()      { return name; }
        public String getHref()      { return href; }
        public String getWorkspace() { return workspace; }
        public boolean isDefault()   { return name == null || name.isEmpty(); }

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
     * Bounding box of the layer group.
     * The {@code crs} field is normalised to a plain EPSG string regardless of whether the server
     * sent a plain string or a {@code {"@class":"...","$":"EPSG:xxx"}} object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bounds {
        @JsonProperty("minx") private Double minx;
        @JsonProperty("maxx") private Double maxx;
        @JsonProperty("miny") private Double miny;
        @JsonProperty("maxy") private Double maxy;
        @JsonProperty("crs")
        @JsonDeserialize(using = CrsDeserializer.class)
        private String crs;

        public Bounds() {}
        public Double getMinx() { return minx; }
        public Double getMaxx() { return maxx; }
        public Double getMiny() { return miny; }
        public Double getMaxy() { return maxy; }
        public String getCrs()  { return crs; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bounds that = (Bounds) o;
            return Objects.equals(minx, that.minx)
                    && Objects.equals(maxx, that.maxx)
                    && Objects.equals(miny, that.miny)
                    && Objects.equals(maxy, that.maxy)
                    && Objects.equals(crs, that.crs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(minx, maxx, miny, maxy, crs);
        }

        @Override
        public String toString() {
            return "Bounds{" +
                    "minx=" + minx +
                    ", maxx=" + maxx +
                    ", miny=" + miny +
                    ", maxy=" + maxy +
                    ", crs=" + crs +
                    '}';
        }
    }

    /**
     * Wrapper for the keywords list.
     * GeoServer may return {@code {"string": ["kw1","kw2"]}} or the single-value form
     * {@code {"string": "kw1"}}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Keywords {
        @JsonProperty("string")
        @JsonDeserialize(using = StringListDeserializer.class)
        private List<String> strings;

        public Keywords() {}
        public List<String> getStrings() {
            return strings != null ? strings : Collections.<String>emptyList();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Keywords that = (Keywords) o;
            return Objects.equals(strings, that.strings);
        }

        @Override
        public int hashCode() {
            return Objects.hash(strings);
        }

        @Override
        public String toString() {
            return "Keywords{" +
                    "strings=" + strings +
                    '}';
        }
    }

    /**
     * Wrapper for metadata entries.
     * Single entry: {@code {"entry": {"@key":"K","$":"V"}}}
     * Multiple entries: {@code {"entry": [{"@key":"K1","$":"V1"},...]}}
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

    // Custom Deserializers

    /**
     * Deserialises {@code publishables.published} — handles both single object and array forms
     * into {@code List<PublishedRef>}.
     */
    public static class PublishedListDeserializer extends JsonDeserializer<List<PublishedRef>> {
        @Override
        public List<PublishedRef> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            List<PublishedRef> result = new ArrayList<PublishedRef>();
            if (p.currentToken() == JsonToken.START_ARRAY) {
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    result.add(mapper.readValue(p, PublishedRef.class));
                }
            } else if (p.currentToken() == JsonToken.START_OBJECT) {
                result.add(mapper.readValue(p, PublishedRef.class));
            }
            return result;
        }
    }

    /**
     * Deserialises {@code styles.style} — handles empty string, single object, and mixed arrays
     * into {@code List<StyleRef>}.
     * <ul>
     *   <li>String "" → StyleRef with name=""</li>
     *   <li>Object {"name":"raster",...} → StyleRef</li>
     *   <li>Array of the above → List</li>
     * </ul>
     */
    public static class StyleListDeserializer extends JsonDeserializer<List<StyleRef>> {
        @Override
        public List<StyleRef> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            List<StyleRef> result = new ArrayList<StyleRef>();
            if (p.currentToken() == JsonToken.START_ARRAY) {
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    result.add(deserializeSingleStyle(p, mapper));
                }
            } else {
                result.add(deserializeSingleStyle(p, mapper));
            }
            return result;
        }

        private StyleRef deserializeSingleStyle(JsonParser p, ObjectMapper mapper) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                return new StyleRef(p.getText());
            } else if (p.currentToken() == JsonToken.START_OBJECT) {
                return mapper.readValue(p, StyleRef.class);
            }
            return new StyleRef("");
        }
    }

    /**
     * Deserialises {@code bounds.crs} — accepts either the plain string "EPSG:4326"
     * or the object form {@code {"@class":"...","$":"EPSG:4326"}}, returning a plain {@code String}.
     */
    public static class CrsDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                return p.getText();
            } else if (p.currentToken() == JsonToken.START_OBJECT) {
                String crsValue = null;
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = p.getCurrentName();
                    p.nextToken();
                    if ("$".equals(fieldName)) {
                        crsValue = p.getText();
                    } else {
                        p.skipChildren();
                    }
                }
                return crsValue;
            }
            return null;
        }
    }

    /**
     * Deserialises {@code keywords.string} — handles both a single string and an array of strings
     * into {@code List<String>}.
     */
    public static class StringListDeserializer extends JsonDeserializer<List<String>> {
        @Override
        public List<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            List<String> result = new ArrayList<String>();
            if (p.currentToken() == JsonToken.START_ARRAY) {
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    result.add(p.getText());
                }
            } else if (p.currentToken() == JsonToken.VALUE_STRING) {
                result.add(p.getText());
            }
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LayerGroup that = (LayerGroup) o;
        return Objects.equals(name, that.name)
                && Objects.equals(mode, that.mode)
                && Objects.equals(title, that.title)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(internationalTitle, that.internationalTitle)
                && Objects.equals(internationalAbstract, that.internationalAbstract)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(publishables, that.publishables)
                && Objects.equals(styles, that.styles)
                && Objects.equals(bounds, that.bounds)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(metadata, that.metadata)
                && Objects.equals(rootLayer, that.rootLayer)
                && Objects.equals(rootLayerStyle, that.rootLayerStyle)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mode, title, abstractText, internationalTitle, internationalAbstract, enabled, advertised, workspace, publishables, styles, bounds, keywords, metadata, rootLayer, rootLayerStyle, dateCreated, dateModified);
    }

    @Override
    public String toString() {
        return "LayerGroup{" +
                "name=" + name +
                ", mode=" + mode +
                ", title=" + title +
                ", abstractText=" + abstractText +
                ", internationalTitle=" + internationalTitle +
                ", internationalAbstract=" + internationalAbstract +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", workspace=" + workspace +
                ", publishables=" + publishables +
                ", styles=" + styles +
                ", bounds=" + bounds +
                ", keywords=" + keywords +
                ", metadata=" + metadata +
                ", rootLayer=" + rootLayer +
                ", rootLayerStyle=" + rootLayerStyle +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }
}
