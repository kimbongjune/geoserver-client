package io.github.kimbongjune.geoserverclient.dto.wmslayer;

import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a WMS layer.
 *
 * <p>Builds the request body for
 * {@code PUT /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}}.
 *
 * <p><b>GeoServer 2.28.2 BUG:</b> the PUT endpoint always returns 500.
 * This class is implemented for future compatibility.
 *
 * <p>At least one field must be set.
 */
public class UpdateWmsLayerRequest {

    private final String title;
    private final String description;
    private final String abstractText;
    private final Boolean enabled;
    private final String forcedRemoteStyle;
    private final String preferredFormat;
    private final Double minScale;
    private final Double maxScale;
    private final Boolean metadataBBoxRespected;
    private final StringMap vendorParameters;
    private final String calculate;

    private UpdateWmsLayerRequest(Builder builder) {
        this.title                = builder.title;
        this.description          = builder.description;
        this.abstractText         = builder.abstractText;
        this.enabled              = builder.enabled;
        this.forcedRemoteStyle    = builder.forcedRemoteStyle;
        this.preferredFormat      = builder.preferredFormat;
        this.minScale             = builder.minScale;
        this.maxScale             = builder.maxScale;
        this.metadataBBoxRespected = builder.metadataBBoxRespected;
        this.vendorParameters     = builder.vendorParameters;
        this.calculate            = builder.calculate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getAbstractText() {
        return abstractText;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public String getForcedRemoteStyle() {
        return forcedRemoteStyle;
    }
    public String getPreferredFormat() {
        return preferredFormat;
    }
    public Double getMinScale() {
        return minScale;
    }
    public Double getMaxScale() {
        return maxScale;
    }
    public Boolean getMetadataBBoxRespected() {
        return metadataBBoxRespected;
    }
    public StringMap getVendorParameters() {
        return vendorParameters;
    }
    /** The {@code calculate} query parameter. Accepted values: "nativebbox", "latlonbbox", or a comma-separated combination. */
    public String getCalculate() {
        return calculate;
    }

    public static class Builder {
        private String title;
        private String description;
        private String abstractText;
        private Boolean enabled;
        private String forcedRemoteStyle;
        private String preferredFormat;
        private Double minScale;
        private Double maxScale;
        private Boolean metadataBBoxRespected;
        private StringMap vendorParameters;
        private String calculate;

        public Builder title(String title) {
            this.title = title; return this;
        }
        public Builder description(String description) {
            this.description = description; return this;
        }
        public Builder abstractText(String abstractText) {
            this.abstractText = abstractText; return this;
        }
        public Builder enabled(boolean enabled) {
            this.enabled = enabled; return this;
        }
        public Builder forcedRemoteStyle(String style) {
            this.forcedRemoteStyle = style; return this;
        }
        public Builder preferredFormat(String format) {
            this.preferredFormat = format; return this;
        }
        public Builder minScale(double minScale) {
            this.minScale = minScale; return this;
        }
        public Builder maxScale(double maxScale) {
            this.maxScale = maxScale; return this;
        }
        public Builder metadataBBoxRespected(boolean v) {
            this.metadataBBoxRespected = v; return this;
        }
        public Builder vendorParameters(StringMap params) {
            this.vendorParameters = params; return this;
        }
        public Builder calculate(String calculate) {
            this.calculate = calculate; return this;
        }

        public UpdateWmsLayerRequest build() {
            boolean anySet = title != null || description != null || abstractText != null
                    || enabled != null || forcedRemoteStyle != null || preferredFormat != null
                    || minScale != null || maxScale != null || metadataBBoxRespected != null
                    || vendorParameters != null;
            if (!anySet) {
                throw new InvalidParameterException("UpdateWmsLayerRequest",
                        "at least one field must be set");
            }
            return new UpdateWmsLayerRequest(this);
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
            return Objects.equals(title, that.title)
                    && Objects.equals(description, that.description)
                    && Objects.equals(abstractText, that.abstractText)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(forcedRemoteStyle, that.forcedRemoteStyle)
                    && Objects.equals(preferredFormat, that.preferredFormat)
                    && Objects.equals(minScale, that.minScale)
                    && Objects.equals(maxScale, that.maxScale)
                    && Objects.equals(metadataBBoxRespected, that.metadataBBoxRespected)
                    && Objects.equals(vendorParameters, that.vendorParameters)
                    && Objects.equals(calculate, that.calculate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, abstractText, enabled, forcedRemoteStyle, preferredFormat, minScale, maxScale, metadataBBoxRespected, vendorParameters, calculate);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "title=" + title +
                    ", description=" + description +
                    ", abstractText=" + abstractText +
                    ", enabled=" + enabled +
                    ", forcedRemoteStyle=" + forcedRemoteStyle +
                    ", preferredFormat=" + preferredFormat +
                    ", minScale=" + minScale +
                    ", maxScale=" + maxScale +
                    ", metadataBBoxRespected=" + metadataBBoxRespected +
                    ", vendorParameters=" + vendorParameters +
                    ", calculate=" + calculate +
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
        UpdateWmsLayerRequest that = (UpdateWmsLayerRequest) o;
        return Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(forcedRemoteStyle, that.forcedRemoteStyle)
                && Objects.equals(preferredFormat, that.preferredFormat)
                && Objects.equals(minScale, that.minScale)
                && Objects.equals(maxScale, that.maxScale)
                && Objects.equals(metadataBBoxRespected, that.metadataBBoxRespected)
                && Objects.equals(vendorParameters, that.vendorParameters)
                && Objects.equals(calculate, that.calculate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, abstractText, enabled, forcedRemoteStyle, preferredFormat, minScale, maxScale, metadataBBoxRespected, vendorParameters, calculate);
    }

    @Override
    public String toString() {
        return "UpdateWmsLayerRequest{" +
                "title=" + title +
                ", description=" + description +
                ", abstractText=" + abstractText +
                ", enabled=" + enabled +
                ", forcedRemoteStyle=" + forcedRemoteStyle +
                ", preferredFormat=" + preferredFormat +
                ", minScale=" + minScale +
                ", maxScale=" + maxScale +
                ", metadataBBoxRespected=" + metadataBBoxRespected +
                ", vendorParameters=" + vendorParameters +
                ", calculate=" + calculate +
                '}';
    }
}
