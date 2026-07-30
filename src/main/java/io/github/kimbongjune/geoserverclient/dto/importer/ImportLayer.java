package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for the layer associated with an import task.
 *
 * <p>Used as both the response body for
 * {@code GET /rest/imports/{id}/tasks/{taskId}/layer} and the request body for
 * {@code PUT /rest/imports/{id}/tasks/{taskId}/layer}.</p>
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

    public ImportLayer() {}

    /** Constructor for PUT requests (update title and/or SRS). */
    public ImportLayer(String title, String srs) {
        this.title = title;
        this.srs = srs;
    }

    public String getName() { return name; }
    public String getHref() { return href; }
    public String getTitle() { return title; }
    public String getOriginalName() { return originalName; }
    public String getNativeName() { return nativeName; }
    public String getSrs() { return srs; }
    public BBox getBbox() { return bbox; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BBox {
        private double minx;
        private double miny;
        private double maxx;
        private double maxy;

        public BBox() {}

        public BBox(double minx, double miny, double maxx, double maxy) {
            this.minx = minx;
            this.miny = miny;
            this.maxx = maxx;
            this.maxy = maxy;
        }

        public double getMinx() { return minx; }
        public double getMiny() { return miny; }
        public double getMaxx() { return maxx; }
        public double getMaxy() { return maxy; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
