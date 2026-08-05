package io.github.kimbongjune.geoserverclient.dto.wmtslayer;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;
import java.util.Objects;

/**
 * Request DTO for updating a WMTS layer.
 *
 * <p>Builds the request body for
 * {@code PUT /rest/workspaces/{ws}/wmtsstores/{store}/layers/{layer}}.
 *
 * <p>At least one field must be set.
 *
 * <h2>calculate parameter</h2>
 * <ul>
 *   <li>{@code "nativebbox"}: recalculate nativeBoundingBox</li>
 *   <li>{@code "latlonbbox"}: recalculate latLonBoundingBox</li>
 *   <li>{@code "nativebbox,latlonbbox"}: recalculate both</li>
 *   <li>{@code ""} (default): no recalculation</li>
 * </ul>
 */
public class UpdateWmtsLayerRequest {

    private final String title;
    private final String description;
    private final String abstractText;
    private final Boolean enabled;
    private final Boolean advertised;
    private final String srs;
    private final ProjectionPolicy projectionPolicy;
    private final String calculate;

    private UpdateWmtsLayerRequest(Builder builder) {
        if (builder.title == null && builder.description == null && builder.abstractText == null
                && builder.enabled == null && builder.advertised == null
                && builder.srs == null && builder.projectionPolicy == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.title            = builder.title;
        this.description      = builder.description;
        this.abstractText     = builder.abstractText;
        this.enabled          = builder.enabled;
        this.advertised       = builder.advertised;
        this.srs              = builder.srs;
        this.projectionPolicy = builder.projectionPolicy;
        this.calculate        = builder.calculate;
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
    public Boolean getAdvertised() {
        return advertised;
    }
    public String getSrs() {
        return srs;
    }
    public ProjectionPolicy getProjectionPolicy() {
        return projectionPolicy;
    }
    /** The calculate query parameter. Accepted values: "nativebbox", "latlonbbox", or a comma-separated combination. */
    public String getCalculate() {
        return calculate;
    }

    public static class Builder {
        private String title;
        private String description;
        private String abstractText;
        private Boolean enabled;
        private Boolean advertised;
        private String srs;
        private ProjectionPolicy projectionPolicy;
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
        public Builder advertised(boolean advertised) {
            this.advertised = advertised; return this;
        }
        public Builder srs(String srs) {
            this.srs = srs; return this;
        }
        public Builder projectionPolicy(ProjectionPolicy policy) {
            this.projectionPolicy = policy; return this;
        }
        public Builder calculate(String calculate) {
            this.calculate = calculate; return this;
        }
        public UpdateWmtsLayerRequest build() {
            return new UpdateWmtsLayerRequest(this);
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
                    && Objects.equals(advertised, that.advertised)
                    && Objects.equals(srs, that.srs)
                    && Objects.equals(projectionPolicy, that.projectionPolicy)
                    && Objects.equals(calculate, that.calculate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, description, abstractText, enabled, advertised, srs, projectionPolicy, calculate);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "title=" + title +
                    ", description=" + description +
                    ", abstractText=" + abstractText +
                    ", enabled=" + enabled +
                    ", advertised=" + advertised +
                    ", srs=" + srs +
                    ", projectionPolicy=" + projectionPolicy +
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
        UpdateWmtsLayerRequest that = (UpdateWmtsLayerRequest) o;
        return Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(calculate, that.calculate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, abstractText, enabled, advertised, srs, projectionPolicy, calculate);
    }

    @Override
    public String toString() {
        return "UpdateWmtsLayerRequest{" +
                "title=" + title +
                ", description=" + description +
                ", abstractText=" + abstractText +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", calculate=" + calculate +
                '}';
    }
}
