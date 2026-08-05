package io.github.kimbongjune.geoserverclient.dto.featuretype;

import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a feature type. Supports partial updates.
 *
 * <pre>{@code
 * // Change title only
 * UpdateFeatureTypeRequest.builder().title("New Title").build()
 *
 * // Disable
 * UpdateFeatureTypeRequest.builder().enabled(false).build()
 *
 * // Rename (actually accepted by the server)
 * UpdateFeatureTypeRequest.builder().name("newname").build()
 *
 * // Update with bounding box recalculation
 * UpdateFeatureTypeRequest.builder()
 *     .title("Updated")
 *     .recalculate("nativebbox,latlonbbox")
 *     .build()
 * }</pre>
 */
public class UpdateFeatureTypeRequest {

    private final String  name;
    private final String  title;
    private final String  abstractText;
    private final String  srs;
    private final ProjectionPolicy projectionPolicy;
    private final Boolean enabled;
    private final Integer maxFeatures;
    private final String  recalculate; // query parameter (null = no recalculation)

    private UpdateFeatureTypeRequest(Builder b) {
        if (b.name == null && b.title == null && b.abstractText == null
                && b.srs == null && b.projectionPolicy == null
                && b.enabled == null && b.maxFeatures == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.name             = b.name;
        this.title            = b.title;
        this.abstractText     = b.abstractText;
        this.srs              = b.srs;
        this.projectionPolicy = b.projectionPolicy;
        this.enabled          = b.enabled;
        this.maxFeatures      = b.maxFeatures;
        this.recalculate      = b.recalculate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String  getName() {
        return name;
    }
    public String  getTitle() {
        return title;
    }
    public String  getAbstractText() {
        return abstractText;
    }
    public String  getSrs() {
        return srs;
    }
    public ProjectionPolicy getProjectionPolicy() {
        return projectionPolicy;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public Integer getMaxFeatures() {
        return maxFeatures;
    }
    public String  getRecalculate() {
        return recalculate;
    }

    public static class Builder {
        private String  name;
        private String  title;
        private String  abstractText;
        private String  srs;
        private ProjectionPolicy projectionPolicy;
        private Boolean enabled;
        private Integer maxFeatures;
        private String  recalculate;

        public Builder name(String name) {
            this.name = name;                         return this;
        }
        public Builder title(String title) {
            this.title = title;                       return this;
        }
        public Builder abstractText(String text) {
            this.abstractText = text;                 return this;
        }
        public Builder srs(String srs) {
            this.srs = srs;                           return this;
        }
        public Builder projectionPolicy(ProjectionPolicy p) {
            this.projectionPolicy = p;                return this;
        }
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;                   return this;
        }
        public Builder maxFeatures(Integer max) {
            this.maxFeatures = max;                   return this;
        }
        public Builder recalculate(String r) {
            this.recalculate = r;                     return this;
        }

        public UpdateFeatureTypeRequest build() {
            return new UpdateFeatureTypeRequest(this);
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
                    && Objects.equals(title, that.title)
                    && Objects.equals(abstractText, that.abstractText)
                    && Objects.equals(srs, that.srs)
                    && Objects.equals(projectionPolicy, that.projectionPolicy)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(maxFeatures, that.maxFeatures)
                    && Objects.equals(recalculate, that.recalculate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, title, abstractText, srs, projectionPolicy, enabled, maxFeatures, recalculate);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "name=" + name +
                    ", title=" + title +
                    ", abstractText=" + abstractText +
                    ", srs=" + srs +
                    ", projectionPolicy=" + projectionPolicy +
                    ", enabled=" + enabled +
                    ", maxFeatures=" + maxFeatures +
                    ", recalculate=" + recalculate +
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
        UpdateFeatureTypeRequest that = (UpdateFeatureTypeRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(title, that.title)
                && Objects.equals(abstractText, that.abstractText)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(maxFeatures, that.maxFeatures)
                && Objects.equals(recalculate, that.recalculate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, title, abstractText, srs, projectionPolicy, enabled, maxFeatures, recalculate);
    }

    @Override
    public String toString() {
        return "UpdateFeatureTypeRequest{" +
                "name=" + name +
                ", title=" + title +
                ", abstractText=" + abstractText +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", maxFeatures=" + maxFeatures +
                ", recalculate=" + recalculate +
                '}';
    }
}
