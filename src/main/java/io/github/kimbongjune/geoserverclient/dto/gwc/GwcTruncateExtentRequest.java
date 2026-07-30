package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Arrays;

/**
 * Request DTO for a bounding-box extent truncation (truncate by spatial extent).
 * The endpoint behavior is similar to a seed operation —
 * see GwcMassTruncateManager for the POST endpoint and known limitations.
 * GeoWebCache documentation for this operation is sparse; behavior was verified via curl.
 */
@JacksonXmlRootElement(localName = "truncateExtent")
public class GwcTruncateExtentRequest implements GwcTruncateRequest {

    private String layerName;
    private String gridSetId;
    private String format;
    private Bounds bounds;

    public GwcTruncateExtentRequest() {}

    public GwcTruncateExtentRequest(String layerName, String gridSetId, String format,
                                     double minx, double miny, double maxx, double maxy) {
        this.layerName = layerName;
        this.gridSetId = gridSetId;
        this.format = format;
        this.bounds = new Bounds(minx, miny, maxx, maxy);
    }

    public String getLayerName() { return layerName; }
    public void setLayerName(String layerName) { this.layerName = layerName; }

    public String getGridSetId() { return gridSetId; }
    public void setGridSetId(String gridSetId) { this.gridSetId = gridSetId; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Bounds getBounds() { return bounds; }
    public void setBounds(Bounds bounds) { this.bounds = bounds; }

    /** {@code <bounds><coords><double>minx</double>...</coords></bounds>} —  (2026-07-29). */
    public static class Bounds {
        @JacksonXmlElementWrapper(localName = "coords")
        @JacksonXmlProperty(localName = "double")
        private List<Double> coords;

        public Bounds() {}
        public Bounds(double minx, double miny, double maxx, double maxy) {
            this.coords = Arrays.asList(minx, miny, maxx, maxy);
        }

        public List<Double> getCoords() { return coords == null ? null : Collections.unmodifiableList(coords); }
        public void setCoords(List<Double> coords) { this.coords = coords; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bounds that = (Bounds) o;
            return Objects.equals(coords, that.coords);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coords);
        }

        @Override
        public String toString() {
            return "Bounds{" +
                    "coords=" + coords +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcTruncateExtentRequest that = (GwcTruncateExtentRequest) o;
        return Objects.equals(layerName, that.layerName)
                && Objects.equals(gridSetId, that.gridSetId)
                && Objects.equals(format, that.format)
                && Objects.equals(bounds, that.bounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerName, gridSetId, format, bounds);
    }

    @Override
    public String toString() {
        return "GwcTruncateExtentRequest{" +
                "layerName=" + layerName +
                ", gridSetId=" + gridSetId +
                ", format=" + format +
                ", bounds=" + bounds +
                '}';
    }
}
