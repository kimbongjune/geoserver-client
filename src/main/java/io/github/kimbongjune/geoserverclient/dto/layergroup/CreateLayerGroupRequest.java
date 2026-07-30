package io.github.kimbongjune.geoserverclient.dto.layergroup;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for {@code POST /rest/layergroups} or {@code POST /rest/workspaces/{ws}/layergroups}.
 *
 * <p>Use {@link #builder(String)} to create instances.</p>
 *
 * <p>Note: the abstract field is sent as "abstract" on POST/PUT (not as the "abstractTxt" key
 * returned in GET responses).</p>
 */
public class CreateLayerGroupRequest {

    private final String name;
    private final List<PublishableEntry> publishables;
    private final List<String> styles;
    private final String mode;
    private final String title;
    private final String abstractText;
    private final StringMap internationalTitle;
    private final StringMap internationalAbstract;
    private final Boolean enabled;
    private final Boolean advertised;
    private final BoundsSpec bounds;
    private final List<String> keywords;
    private final StringMap metadata;

    private CreateLayerGroupRequest(Builder b) {
        this.name                 = b.name;
        this.publishables         = b.publishables;
        this.styles               = b.styles;
        this.mode                 = b.mode;
        this.title                = b.title;
        this.abstractText         = b.abstractText;
        this.internationalTitle   = b.internationalTitle;
        this.internationalAbstract = b.internationalAbstract;
        this.enabled              = b.enabled;
        this.advertised           = b.advertised;
        this.bounds               = b.bounds;
        this.keywords             = b.keywords;
        this.metadata             = b.metadata;
    }

    public String getName()                             { return name; }
    public List<PublishableEntry> getPublishables()     { return publishables == null ? null : Collections.unmodifiableList(publishables); }
    public List<String> getStyles()                     { return styles == null ? null : Collections.unmodifiableList(styles); }
    public String getMode()                             { return mode; }
    public String getTitle()                            { return title; }
    public String getAbstractText()                     { return abstractText; }
    public StringMap getInternationalTitle()             { return internationalTitle; }
    public StringMap getInternationalAbstract()           { return internationalAbstract; }
    public Boolean getEnabled()                         { return enabled; }
    public Boolean getAdvertised()                      { return advertised; }
    public BoundsSpec getBounds()                       { return bounds; }
    public List<String> getKeywords()                   { return keywords == null ? null : Collections.unmodifiableList(keywords); }
    public StringMap getMetadata()                       { return metadata; }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private List<PublishableEntry> publishables = new ArrayList<PublishableEntry>();
        private List<String> styles;
        private String mode;
        private String title;
        private String abstractText;
        private StringMap internationalTitle;
        private StringMap internationalAbstract;
        private Boolean enabled;
        private Boolean advertised;
        private BoundsSpec bounds;
        private List<String> keywords;
        private StringMap metadata;

        private Builder(String name) { this.name = name; }

        /** Adds a layer to publishables. Use qualified name "ws:name" for workspace-scoped layers. */
        public Builder layer(String layerName) {
            publishables.add(new PublishableEntry("layer", layerName));
            return this;
        }

        /** Adds a nested layer group to publishables. */
        public Builder layerGroup(String groupName) {
            publishables.add(new PublishableEntry("layerGroup", groupName));
            return this;
        }

        /**
         * Sets styles for publishables in 1:1 order.
         * Use {@code ""} (empty string) to use the layer's default style.
         */
        public Builder styles(List<String> styleNames) {
            this.styles = styleNames;
            return this;
        }

        /** Layer group mode. Valid values: SINGLE | OPAQUE_CONTAINER | NAMED | EO | CONTAINER (defaults to SINGLE). */
        public Builder mode(String mode) { this.mode = mode; return this; }

        public Builder title(String title) { this.title = title; return this; }

        /** Layer group abstract text. Serialized as "abstract" in POST/PUT requests. */
        public Builder abstractText(String abstractText) {
            this.abstractText = abstractText;
            return this;
        }

        public Builder internationalTitle(StringMap internationalTitle) {
            this.internationalTitle = internationalTitle;
            return this;
        }

        public Builder internationalAbstract(StringMap internationalAbstract) {
            this.internationalAbstract = internationalAbstract;
            return this;
        }

        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder advertised(boolean advertised) { this.advertised = advertised; return this; }

        public Builder bounds(double minx, double maxx, double miny, double maxy, String crs) {
            this.bounds = new BoundsSpec(minx, maxx, miny, maxy, crs);
            return this;
        }

        public Builder keywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        /**   key→value . {@code entry}  . */
        public Builder metadata(StringMap metadata) {
            this.metadata = metadata;
            return this;
        }

        public CreateLayerGroupRequest build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("name must not be null or empty");
            }
            if (publishables.isEmpty()) {
                throw new IllegalArgumentException("publishables must not be empty");
            }
            return new CreateLayerGroupRequest(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder that = (Builder) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(publishables, that.publishables)
                    && Objects.equals(styles, that.styles)
                    && Objects.equals(mode, that.mode)
                    && Objects.equals(title, that.title)
                    && Objects.equals(abstractText, that.abstractText)
                    && Objects.equals(internationalTitle, that.internationalTitle)
                    && Objects.equals(internationalAbstract, that.internationalAbstract)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(advertised, that.advertised)
                    && Objects.equals(bounds, that.bounds)
                    && Objects.equals(keywords, that.keywords)
                    && Objects.equals(metadata, that.metadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, publishables, styles, mode, title, abstractText, internationalTitle, internationalAbstract, enabled, advertised, bounds, keywords, metadata);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", publishables=" + publishables +
                    ", styles=" + styles +
                    ", mode=" + mode +
                    ", title=" + title +
                    ", abstractText=" + abstractText +
                    ", internationalTitle=" + internationalTitle +
                    ", internationalAbstract=" + internationalAbstract +
                    ", enabled=" + enabled +
                    ", advertised=" + advertised +
                    ", bounds=" + bounds +
                    ", keywords=" + keywords +
                    ", metadata=" + metadata +
                    '}';
        }
    }

    /** publishables    . */
    public static class PublishableEntry {
        private final String type; // "layer" | "layerGroup"
        private final String name;
        public PublishableEntry(String type, String name) {
            this.type = type;
            this.name = name;
        }
        public String getType() { return type; }
        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PublishableEntry that = (PublishableEntry) o;
            return Objects.equals(type, that.type)
                    && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, name);
        }

        @Override
        public String toString() {
            return "PublishableEntry{" +
                    "type=" + type +
                    ", name=" + name +
                    '}';
        }
    }

    /** bounds . */
    public static class BoundsSpec {
        private final double minx, maxx, miny, maxy;
        private final String crs;
        public BoundsSpec(double minx, double maxx, double miny, double maxy, String crs) {
            this.minx = minx; this.maxx = maxx;
            this.miny = miny; this.maxy = maxy;
            this.crs = crs;
        }
        public double getMinx() { return minx; }
        public double getMaxx() { return maxx; }
        public double getMiny() { return miny; }
        public double getMaxy() { return maxy; }
        public String getCrs()  { return crs; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoundsSpec that = (BoundsSpec) o;
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
            return "BoundsSpec{" +
                    "minx=" + minx +
                    ", maxx=" + maxx +
                    ", miny=" + miny +
                    ", maxy=" + maxy +
                    ", crs=" + crs +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateLayerGroupRequest that = (CreateLayerGroupRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(publishables, that.publishables)
                && Objects.equals(styles, that.styles)
                && Objects.equals(mode, that.mode)
                && Objects.equals(title, that.title)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(internationalTitle, that.internationalTitle)
                && Objects.equals(internationalAbstract, that.internationalAbstract)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(bounds, that.bounds)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, publishables, styles, mode, title, abstractText, internationalTitle, internationalAbstract, enabled, advertised, bounds, keywords, metadata);
    }

    @Override
    public String toString() {
        return "CreateLayerGroupRequest{" +
                "name=" + name +
                ", publishables=" + publishables +
                ", styles=" + styles +
                ", mode=" + mode +
                ", title=" + title +
                ", abstractText=" + abstractText +
                ", internationalTitle=" + internationalTitle +
                ", internationalAbstract=" + internationalAbstract +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", bounds=" + bounds +
                ", keywords=" + keywords +
                ", metadata=" + metadata +
                '}';
    }
}
