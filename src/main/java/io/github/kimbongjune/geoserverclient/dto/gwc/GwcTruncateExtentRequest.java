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

    /** Constructs an empty {@code GwcTruncateExtentRequest} for deserialization. */
    public GwcTruncateExtentRequest() {}

    /**
     * Constructs a {@code GwcTruncateExtentRequest} with all required fields.
     * @param layerName the layer name
     * @param gridSetId the grid set ID
     * @param format    the tile MIME format (e.g. {@code "image/png"})
     * @param minx      the minimum x coordinate
     * @param miny      the minimum y coordinate
     * @param maxx      the maximum x coordinate
     * @param maxy      the maximum y coordinate
     */
    public GwcTruncateExtentRequest(String layerName, String gridSetId, String format,
                                     double minx, double miny, double maxx, double maxy) {
        this.layerName = layerName;
        this.gridSetId = gridSetId;
        this.format = format;
        this.bounds = new Bounds(minx, miny, maxx, maxy);
    }

    /** @return the layer name */
    public String getLayerName() {
        return layerName;
    }
    /** @param layerName the layer name */
    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    /** @return the grid set ID */
    public String getGridSetId() {
        return gridSetId;
    }
    /** @param gridSetId the grid set ID */
    public void setGridSetId(String gridSetId) {
        this.gridSetId = gridSetId;
    }

    /** @return the tile MIME format */
    public String getFormat() {
        return format;
    }
    /** @param format the tile MIME format */
    public void setFormat(String format) {
        this.format = format;
    }

    /** @return the bounding box */
    public Bounds getBounds() {
        return bounds;
    }
    /** @param bounds the bounding box */
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    /**
     * Bounding box in the format {@code <bounds><coords><double>minx</double>...</coords></bounds>}.
     * Server-verified 2026-07-29.
     */
    public static class Bounds {
        @JacksonXmlElementWrapper(localName = "coords")
        @JacksonXmlProperty(localName = "double")
        private List<Double> coords;

        /** Constructs an empty {@code Bounds} for deserialization. */
        public Bounds() {}
        /**
         * Constructs a {@code Bounds} from bounding coordinates.
         * @param minx the minimum x
         * @param miny the minimum y
         * @param maxx the maximum x
         * @param maxy the maximum y
         */
        public Bounds(double minx, double miny, double maxx, double maxy) {
            this.coords = Arrays.asList(minx, miny, maxx, maxy);
        }

        /** @return the coordinate list {@code [minx, miny, maxx, maxy]} */
        public List<Double> getCoords() {
            return coords == null ? null : Collections.unmodifiableList(coords);
        }
        /** @param coords the coordinate list {@code [minx, miny, maxx, maxy]} */
        public void setCoords(List<Double> coords) {
            this.coords = coords;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
