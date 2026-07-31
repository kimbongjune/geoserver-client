package io.github.kimbongjune.geoserverclient.dto.layergroup;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Request DTO for {@code PUT /rest/layergroups/{name}} or
 * {@code PUT /rest/workspaces/{ws}/layergroups/{name}}.
 *
 * <p>Supports partial updates — null fields are excluded from the request payload.
 * Renaming is not supported (server returns 403); workspace changes are also not supported (403).
 * Bounds are not recalculated automatically when publishables change — specify them explicitly.
 *
 * <p>Use {@link #builder()} to create instances.
 */
public class UpdateLayerGroupRequest {

    private final List<CreateLayerGroupRequest.PublishableEntry> publishables;
    private final List<String> styles;
    private final String mode;
    private final String title;
    private final String abstractText;
    private final StringMap internationalTitle;
    private final StringMap internationalAbstract;
    private final Boolean enabled;
    private final Boolean advertised;
    private final CreateLayerGroupRequest.BoundsSpec bounds;
    private final List<String> keywords;
    private final StringMap metadata;

    private UpdateLayerGroupRequest(Builder b) {
        this.publishables          = b.publishables;
        this.styles                = b.styles;
        this.mode                  = b.mode;
        this.title                 = b.title;
        this.abstractText          = b.abstractText;
        this.internationalTitle    = b.internationalTitle;
        this.internationalAbstract = b.internationalAbstract;
        this.enabled               = b.enabled;
        this.advertised            = b.advertised;
        this.bounds                = b.bounds;
        this.keywords              = b.keywords;
        this.metadata              = b.metadata;
    }

    public List<CreateLayerGroupRequest.PublishableEntry> getPublishables() {
        return publishables == null ? null : Collections.unmodifiableList(publishables);
    }
    public List<String> getStyles() {
        return styles == null ? null : Collections.unmodifiableList(styles);
    }
    public String getMode() {
        return mode;
    }
    public String getTitle() {
        return title;
    }
    public String getAbstractText() {
        return abstractText;
    }
    public StringMap getInternationalTitle() {
        return internationalTitle;
    }
    public StringMap getInternationalAbstract() {
        return internationalAbstract;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public Boolean getAdvertised() {
        return advertised;
    }
    public CreateLayerGroupRequest.BoundsSpec getBounds() {
        return bounds;
    }
    public List<String> getKeywords() {
        return keywords == null ? null : Collections.unmodifiableList(keywords);
    }
    public StringMap getMetadata() {
        return metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<CreateLayerGroupRequest.PublishableEntry> publishables;
        private List<String> styles;
        private String mode;
        private String title;
        private String abstractText;
        private StringMap internationalTitle;
        private StringMap internationalAbstract;
        private Boolean enabled;
        private Boolean advertised;
        private CreateLayerGroupRequest.BoundsSpec bounds;
        private List<String> keywords;
        private StringMap metadata;

        /** publishables  . */
        public Builder layers(List<String> layerNames) {
            this.publishables = new ArrayList<CreateLayerGroupRequest.PublishableEntry>();
            for (String n : layerNames) {
                this.publishables.add(new CreateLayerGroupRequest.PublishableEntry("layer", n));
            }
            return this;
        }

        /** publishables   . */
        public Builder layer(String layerName) {
            this.publishables = new ArrayList<CreateLayerGroupRequest.PublishableEntry>();
            this.publishables.add(new CreateLayerGroupRequest.PublishableEntry("layer", layerName));
            return this;
        }

        public Builder styles(List<String> styleNames) {
            this.styles = styleNames;
            return this;
        }

        public Builder mode(String mode) {
            this.mode = mode; return this;
        }
        public Builder title(String title) {
            this.title = title; return this;
        }
        public Builder abstractText(String abstractText) {
            this.abstractText = abstractText; return this;
        }

        public Builder internationalTitle(StringMap v) {
            this.internationalTitle = v; return this;
        }
        public Builder internationalAbstract(StringMap v) {
            this.internationalAbstract = v; return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled; return this;
        }
        public Builder advertised(boolean advertised) {
            this.advertised = advertised; return this;
        }

        public Builder bounds(double minx, double maxx, double miny, double maxy, String crs) {
            this.bounds = new CreateLayerGroupRequest.BoundsSpec(minx, maxx, miny, maxy, crs);
            return this;
        }

        public Builder keywords(List<String> keywords) {
            this.keywords = keywords; return this;
        }
        public Builder metadata(StringMap meta) {
            this.metadata = meta; return this;
        }

        public UpdateLayerGroupRequest build() {
            return new UpdateLayerGroupRequest(this);
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
            return Objects.equals(publishables, that.publishables)
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
            return Objects.hash(publishables, styles, mode, title, abstractText, internationalTitle, internationalAbstract, enabled, advertised, bounds, keywords, metadata);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "publishables=" + publishables +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateLayerGroupRequest that = (UpdateLayerGroupRequest) o;
        return Objects.equals(publishables, that.publishables)
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
        return Objects.hash(publishables, styles, mode, title, abstractText, internationalTitle, internationalAbstract, enabled, advertised, bounds, keywords, metadata);
    }

    @Override
    public String toString() {
        return "UpdateLayerGroupRequest{" +
                "publishables=" + publishables +
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
