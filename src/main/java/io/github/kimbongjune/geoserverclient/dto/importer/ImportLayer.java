package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for the layer associated with an import task.
 *
 * <p>Used as both the response body for
 * {@code GET /rest/imports/{id}/tasks/{taskId}/layer} and the request body for
 * {@code PUT /rest/imports/{id}/tasks/{taskId}/layer}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportLayer {

    private String name;
    private String href;
    private String title;
    private String originalName;
    private String nativeName;
    private String srs;
    private BBox bbox;

    /** Constructs an empty {@code ImportLayer} for deserialization. */
    public ImportLayer() {}

    /**
     * Constructs an {@code ImportLayer} for PUT requests (update title and/or SRS).
     * @param title the layer title to set
     * @param srs   the SRS (e.g. {@code "EPSG:4326"}) to set
     */
    public ImportLayer(String title, String srs) {
        this.title = title;
        this.srs = srs;
    }

    /** @return the layer name */
    public String getName() {
        return name;
    }
    /** @return the href to this layer resource */
    public String getHref() {
        return href;
    }
    /** @return the layer title */
    public String getTitle() {
        return title;
    }
    /** @return the original name before import */
    public String getOriginalName() {
        return originalName;
    }
    /** @return the native name in the source store */
    public String getNativeName() {
        return nativeName;
    }
    /** @return the SRS (e.g. {@code "EPSG:4326"}) */
    public String getSrs() {
        return srs;
    }
    /** @return the native bounding box */
    public BBox getBbox() {
        return bbox;
    }

    /** Bounding box for an imported layer. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BBox {
        private double minx;
        private double miny;
        private double maxx;
        private double maxy;

        /** Constructs an empty {@code BBox} for deserialization. */
        public BBox() {}

        /**
         * Constructs a {@code BBox} with the given coordinates.
         * @param minx minimum x coordinate
         * @param miny minimum y coordinate
         * @param maxx maximum x coordinate
         * @param maxy maximum y coordinate
         */
        public BBox(double minx, double miny, double maxx, double maxy) {
            this.minx = minx;
            this.miny = miny;
            this.maxx = maxx;
            this.maxy = maxy;
        }

        /** @return the minimum x coordinate */
        public double getMinx() {
            return minx;
        }
        /** @return the minimum y coordinate */
        public double getMiny() {
            return miny;
        }
        /** @return the maximum x coordinate */
        public double getMaxx() {
            return maxx;
        }
        /** @return the maximum y coordinate */
        public double getMaxy() {
            return maxy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BBox that = (BBox) o;
            return Objects.equals(minx, that.minx)
                    && Objects.equals(miny, that.miny)
                    && Objects.equals(maxx, that.maxx)
                    && Objects.equals(maxy, that.maxy);
        }

        @Override
        public int hashCode() {
            return Objects.hash(minx, miny, maxx, maxy);
        }

        @Override
        public String toString() {
            return "BBox{" +
                    "minx=" + minx +
                    ", miny=" + miny +
                    ", maxx=" + maxx +
                    ", maxy=" + maxy +
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
        ImportLayer that = (ImportLayer) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href)
                && Objects.equals(title, that.title)
                && Objects.equals(originalName, that.originalName)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(srs, that.srs)
                && Objects.equals(bbox, that.bbox);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href, title, originalName, nativeName, srs, bbox);
    }

    @Override
    public String toString() {
        return "ImportLayer{" +
                "name=" + name +
                ", href=" + href +
                ", title=" + title +
                ", originalName=" + originalName +
                ", nativeName=" + nativeName +
                ", srs=" + srs +
                ", bbox=" + bbox +
                '}';
    }
}
