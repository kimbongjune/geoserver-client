package io.github.kimbongjune.geoserverclient.dto.layergroup;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for {@code POST /rest/layergroups} or {@code POST /rest/workspaces/{ws}/layergroups}.
 *
 * <p>Use {@link #builder(String)} to create instances.
 *
 * <p>Note: the abstract field is sent as "abstract" on POST/PUT (not as the "abstractTxt" key
 * returned in GET responses).
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

    /** @return the layer group name */
    public String getName() {
        return name;
    }
    /** @return the publishable entries */
    public List<PublishableEntry> getPublishables() {
        return publishables == null ? null : Collections.unmodifiableList(publishables);
    }
    /** @return the styles list */
    public List<String> getStyles() {
        return styles == null ? null : Collections.unmodifiableList(styles);
    }
    /** @return the layer group mode */
    public String getMode() {
        return mode;
    }
    /** @return the title */
    public String getTitle() {
        return title;
    }
    /** @return the abstract text */
    public String getAbstractText() {
        return abstractText;
    }
    /** @return the international title */
    public StringMap getInternationalTitle() {
        return internationalTitle;
    }
    /** @return the international abstract */
    public StringMap getInternationalAbstract() {
        return internationalAbstract;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if advertised */
    public Boolean getAdvertised() {
        return advertised;
    }
    /** @return the bounding box specification */
    public BoundsSpec getBounds() {
        return bounds;
    }
    /** @return the keywords */
    public List<String> getKeywords() {
        return keywords == null ? null : Collections.unmodifiableList(keywords);
    }
    /** @return the metadata map */
    public StringMap getMetadata() {
        return metadata;
    }

    /**
     * Returns a new builder for a layer group with the given name.
     * @param name the layer group name (must not be null or empty)
     * @return a new {@code Builder}
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /** Builder for {@link CreateLayerGroupRequest}. */
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

        private Builder(String name) {
            this.name = name;
        }

        /**
         * Adds a layer to publishables. Use qualified name "ws:name" for workspace-scoped layers.
         * @param layerName the layer name, optionally qualified as "workspace:name"
         * @return this builder
         */
        public Builder layer(String layerName) {
            publishables.add(new PublishableEntry("layer", layerName));
            return this;
        }

        /**
         * Adds a nested layer group to publishables.
         * @param groupName the nested layer group name
         * @return this builder
         */
        public Builder layerGroup(String groupName) {
            publishables.add(new PublishableEntry("layerGroup", groupName));
            return this;
        }

        /**
         * Sets styles for publishables in 1:1 order.
         * Use {@code ""} (empty string) to use the layer's default style.
         * @param styleNames list of style names aligned with publishables
         * @return this builder
         */
        public Builder styles(List<String> styleNames) {
            this.styles = styleNames;
            return this;
        }

        /**
         * Layer group mode. Valid values: SINGLE | OPAQUE_CONTAINER | NAMED | EO | CONTAINER (defaults to SINGLE).
         * @param mode the layer group mode
         * @return this builder
         */
        public Builder mode(String mode) {
            this.mode = mode; return this;
        }

        /**
         * Sets the title.
         * @param title the title
         * @return this builder
         */
        public Builder title(String title) {
            this.title = title; return this;
        }

        /**
         * Layer group abstract text. Serialized as "abstract" in POST/PUT requests.
         * @param abstractText the abstract text
         * @return this builder
         */
        public Builder abstractText(String abstractText) {
            this.abstractText = abstractText;
            return this;
        }

        /**
         * Sets the international title.
         * @param internationalTitle map of locale to title
         * @return this builder
         */
        public Builder internationalTitle(StringMap internationalTitle) {
            this.internationalTitle = internationalTitle;
            return this;
        }

        /**
         * Sets the international abstract.
         * @param internationalAbstract map of locale to abstract
         * @return this builder
         */
        public Builder internationalAbstract(StringMap internationalAbstract) {
            this.internationalAbstract = internationalAbstract;
            return this;
        }

        /**
         * Sets whether this layer group is enabled.
         * @param enabled {@code true} to enable
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled; return this;
        }

        /**
         * Sets whether this layer group is advertised.
         * @param advertised {@code true} to advertise
         * @return this builder
         */
        public Builder advertised(boolean advertised) {
            this.advertised = advertised; return this;
        }

        /**
         * Sets the bounding box.
         * @param minx minimum x coordinate
         * @param maxx maximum x coordinate
         * @param miny minimum y coordinate
         * @param maxy maximum y coordinate
         * @param crs  CRS code
         * @return this builder
         */
        public Builder bounds(double minx, double maxx, double miny, double maxy, String crs) {
            this.bounds = new BoundsSpec(minx, maxx, miny, maxy, crs);
            return this;
        }

        /**
         * Sets the keywords list.
         * @param keywords list of keywords
         * @return this builder
         */
        public Builder keywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        /**
         * Sets the metadata map (key-to-value entries).
         * @param metadata the metadata map
         * @return this builder
         */
        public Builder metadata(StringMap metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Builds the {@code CreateLayerGroupRequest}.
         * @return a new {@code CreateLayerGroupRequest}
         * @throws IllegalArgumentException if name is blank or publishables is empty
         */
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
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** An entry in the publishables list, representing either a layer or a nested layer group. */
    public static class PublishableEntry {
        private final String type; // "layer" | "layerGroup"
        private final String name;

        /**
         * Constructs a {@code PublishableEntry}.
         * @param type either {@code "layer"} or {@code "layerGroup"}
         * @param name the layer or layer group name
         */
        public PublishableEntry(String type, String name) {
            this.type = type;
            this.name = name;
        }

        /** @return the entry type ({@code "layer"} or {@code "layerGroup"}) */
        public String getType() {
            return type;
        }
        /** @return the layer or layer group name */
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** Bounding box specification for a layer group. */
    public static class BoundsSpec {
        private final double minx, maxx, miny, maxy;
        private final String crs;

        /**
         * Constructs a {@code BoundsSpec}.
         * @param minx minimum x coordinate
         * @param maxx maximum x coordinate
         * @param miny minimum y coordinate
         * @param maxy maximum y coordinate
         * @param crs  CRS code (e.g. {@code "EPSG:4326"})
         */
        public BoundsSpec(double minx, double maxx, double miny, double maxy, String crs) {
            this.minx = minx; this.maxx = maxx;
            this.miny = miny; this.maxy = maxy;
            this.crs = crs;
        }

        /** @return the minimum x coordinate */
        public double getMinx() {
            return minx;
        }
        /** @return the maximum x coordinate */
        public double getMaxx() {
            return maxx;
        }
        /** @return the minimum y coordinate */
        public double getMiny() {
            return miny;
        }
        /** @return the maximum y coordinate */
        public double getMaxy() {
            return maxy;
        }
        /** @return the CRS code */
        public String getCrs() {
            return crs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
