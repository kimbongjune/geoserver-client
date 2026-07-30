package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a GWC tile layer ({@code GeoServerLayer}). Used for both GET and PUT.
 *
 * <p>Some fields ({@link #id}, {@link #inMemoryCached}, {@link #cacheWarningSkips})
 * are returned by GET but ignored on PUT ({@code WRITE_ONLY} annotation prevents serialization).</p>
 *
 * <p>{@link #parameterFilters} is populated on GET. GWC returns these as
 * polymorphic XML elements ({@code styleParameterFilter}, {@code regexParameterFilter}, etc.)
 * that are difficult to round-trip on PUT — see
 * {@link io.github.kimbongjune.geoserverclient.api.gwc.GwcLayerManager} upsert for the recommended approach.</p>
 *
 * <p>PUT must use {@code application/xml} — JSON PUT on GeoServer 2.28.2 causes an
 * XStream "Duplicate field" 500 error (see GwcLayerManager).</p>
 */
@JacksonXmlRootElement(localName = "GeoServerLayer")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcLayer {

    private String name;
    private Boolean enabled;

    @JacksonXmlElementWrapper(localName = "mimeFormats")
    @JacksonXmlProperty(localName = "string")
    private List<String> mimeFormats;

    @JacksonXmlElementWrapper(localName = "gridSubsets")
    @JacksonXmlProperty(localName = "gridSubset")
    private List<GridSubset> gridSubsets;

    private Long expireCache;
    private Long expireClients;

    @JacksonXmlElementWrapper(localName = "metaWidthHeight")
    @JacksonXmlProperty(localName = "int")
    private List<Integer> metaWidthHeight;

    private Integer gutter;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean inMemoryCached;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlElementWrapper(localName = "cacheWarningSkips")
    @JacksonXmlProperty(localName = "string")
    private List<String> cacheWarningSkips;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JacksonXmlElementWrapper(localName = "parameterFilters")
    @JacksonXmlProperty(localName = "parameterFilter")
    private List<ParameterFilter> parameterFilters;

    public GwcLayer() {}

    public GwcLayer(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public List<String> getMimeFormats() { return mimeFormats == null ? null : Collections.unmodifiableList(mimeFormats); }
    public void setMimeFormats(List<String> mimeFormats) { this.mimeFormats = mimeFormats; }

    public List<GridSubset> getGridSubsets() { return gridSubsets == null ? null : Collections.unmodifiableList(gridSubsets); }
    public void setGridSubsets(List<GridSubset> gridSubsets) { this.gridSubsets = gridSubsets; }

    /** Fluent helper: sets gridSubsets from bare gridSetName strings. */
    public GwcLayer gridSetNames(String... gridSetNames) {
        List<GridSubset> subsets = new ArrayList<GridSubset>();
        for (String name : gridSetNames) {
            subsets.add(new GridSubset(name));
        }
        this.gridSubsets = subsets;
        return this;
    }

    public Long getExpireCache() { return expireCache; }
    public void setExpireCache(Long expireCache) { this.expireCache = expireCache; }

    public Long getExpireClients() { return expireClients; }
    public void setExpireClients(Long expireClients) { this.expireClients = expireClients; }

    public List<Integer> getMetaWidthHeight() { return metaWidthHeight == null ? null : Collections.unmodifiableList(metaWidthHeight); }
    public void setMetaWidthHeight(List<Integer> metaWidthHeight) { this.metaWidthHeight = metaWidthHeight; }

    public Integer getGutter() { return gutter; }
    public void setGutter(Integer gutter) { this.gutter = gutter; }

    public String getId() { return id; }
    public Boolean getInMemoryCached() { return inMemoryCached; }
    public List<String> getCacheWarningSkips() { return cacheWarningSkips == null ? null : Collections.unmodifiableList(cacheWarningSkips); }
    public List<ParameterFilter> getParameterFilters() { return parameterFilters == null ? null : Collections.unmodifiableList(parameterFilters); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GridSubset {
        @JacksonXmlProperty(localName = "gridSetName")
        private String gridSetName;

        public GridSubset() {}
        public GridSubset(String gridSetName) { this.gridSetName = gridSetName; }

        public String getGridSetName() { return gridSetName; }
        public void setGridSetName(String gridSetName) { this.gridSetName = gridSetName; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridSubset that = (GridSubset) o;
            return Objects.equals(gridSetName, that.gridSetName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gridSetName);
        }

        @Override
        public String toString() {
            return "GridSubset{" +
                    "gridSetName=" + gridSetName +
                    '}';
        }
    }

    /**  .  (2026-04-02): {@code {"key": "STYLES", "defaultValue": ""}}  . */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParameterFilter {
        private String key;
        private String defaultValue;

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ParameterFilter that = (ParameterFilter) o;
            return Objects.equals(key, that.key)
                    && Objects.equals(defaultValue, that.defaultValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, defaultValue);
        }

        @Override
        public String toString() {
            return "ParameterFilter{" +
                    "key=" + key +
                    ", defaultValue=" + defaultValue +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcLayer that = (GwcLayer) o;
        return Objects.equals(name, that.name)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(mimeFormats, that.mimeFormats)
                && Objects.equals(gridSubsets, that.gridSubsets)
                && Objects.equals(expireCache, that.expireCache)
                && Objects.equals(expireClients, that.expireClients)
                && Objects.equals(metaWidthHeight, that.metaWidthHeight)
                && Objects.equals(gutter, that.gutter)
                && Objects.equals(id, that.id)
                && Objects.equals(inMemoryCached, that.inMemoryCached)
                && Objects.equals(cacheWarningSkips, that.cacheWarningSkips)
                && Objects.equals(parameterFilters, that.parameterFilters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, enabled, mimeFormats, gridSubsets, expireCache, expireClients, metaWidthHeight, gutter, id, inMemoryCached, cacheWarningSkips, parameterFilters);
    }

    @Override
    public String toString() {
        return "GwcLayer{" +
                "name=" + name +
                ", enabled=" + enabled +
                ", mimeFormats=" + mimeFormats +
                ", gridSubsets=" + gridSubsets +
                ", expireCache=" + expireCache +
                ", expireClients=" + expireClients +
                ", metaWidthHeight=" + metaWidthHeight +
                ", gutter=" + gutter +
                ", id=" + id +
                ", inMemoryCached=" + inMemoryCached +
                ", cacheWarningSkips=" + cacheWarningSkips +
                ", parameterFilters=" + parameterFilters +
                '}';
    }
}
