package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Arrays;

/**
 * DTO for a GWC grid set. Maps {@code GET/PUT /gwc/rest/gridsets/{name}}.
 *
 * <p>PUT requires XML — JSON PUT causes an XStream "Duplicate field coords" 500 error
 * (see GwcGridSetManager). The {@link #description} and {@link #scaleNames} fields
 * are returned by GET but ignored on PUT.
 */
@JacksonXmlRootElement(localName = "gridSet")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcGridSet {

    private String name;
    private Srs srs;
    private Extent extent;
    private Boolean alignTopLeft;

    @JacksonXmlElementWrapper(localName = "resolutions")
    @JacksonXmlProperty(localName = "double")
    private List<Double> resolutions;

    private Double metersPerUnit;
    private Double pixelSize;
    private Integer tileWidth;
    private Integer tileHeight;
    private Boolean yCoordinateFirst;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> scaleNames;

    /** Constructs an empty {@code GwcGridSet} for deserialization. */
    public GwcGridSet() {}

    /**
     * Constructs a {@code GwcGridSet} with name, SRS, and bounding extent.
     * @param name       the grid set name
     * @param epsgNumber the EPSG code (e.g. {@code 4326})
     * @param minx       the minimum x coordinate
     * @param miny       the minimum y coordinate
     * @param maxx       the maximum x coordinate
     * @param maxy       the maximum y coordinate
     */
    public GwcGridSet(String name, int epsgNumber, double minx, double miny, double maxx, double maxy) {
        this.name = name;
        this.srs = new Srs(epsgNumber);
        this.extent = new Extent(minx, miny, maxx, maxy);
    }

    /** @return the grid set name */
    public String getName() {
        return name;
    }
    /** @param name the grid set name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the SRS definition */
    public Srs getSrs() {
        return srs;
    }
    /** @param srs the SRS definition */
    public void setSrs(Srs srs) {
        this.srs = srs;
    }

    /** @return the bounding extent */
    public Extent getExtent() {
        return extent;
    }
    /** @param extent the bounding extent */
    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    /** @return {@code true} if tiles are aligned from the top-left */
    public Boolean getAlignTopLeft() {
        return alignTopLeft;
    }
    /** @param alignTopLeft {@code true} to align from top-left */
    public void setAlignTopLeft(Boolean alignTopLeft) {
        this.alignTopLeft = alignTopLeft;
    }

    /** @return the tile resolutions list */
    public List<Double> getResolutions() {
        return resolutions == null ? null : Collections.unmodifiableList(resolutions);
    }
    /** @param resolutions the tile resolutions */
    public void setResolutions(List<Double> resolutions) {
        this.resolutions = resolutions;
    }

    /** @return the meters-per-unit conversion factor */
    public Double getMetersPerUnit() {
        return metersPerUnit;
    }
    /** @param metersPerUnit the meters-per-unit factor */
    public void setMetersPerUnit(Double metersPerUnit) {
        this.metersPerUnit = metersPerUnit;
    }

    /** @return the pixel size in meters */
    public Double getPixelSize() {
        return pixelSize;
    }
    /** @param pixelSize the pixel size in meters */
    public void setPixelSize(Double pixelSize) {
        this.pixelSize = pixelSize;
    }

    /** @return the tile width in pixels */
    public Integer getTileWidth() {
        return tileWidth;
    }
    /** @param tileWidth the tile width in pixels */
    public void setTileWidth(Integer tileWidth) {
        this.tileWidth = tileWidth;
    }

    /** @return the tile height in pixels */
    public Integer getTileHeight() {
        return tileHeight;
    }
    /** @param tileHeight the tile height in pixels */
    public void setTileHeight(Integer tileHeight) {
        this.tileHeight = tileHeight;
    }

    /** @return {@code true} if the Y coordinate comes first */
    public Boolean getYCoordinateFirst() {
        return yCoordinateFirst;
    }
    /** @param yCoordinateFirst {@code true} if Y coordinate is first */
    public void setYCoordinateFirst(Boolean yCoordinateFirst) {
        this.yCoordinateFirst = yCoordinateFirst;
    }

    /** @return the grid set description (read-only, not sent on PUT) */
    public String getDescription() {
        return description;
    }
    /** @return the scale names list (read-only, not sent on PUT) */
    public List<String> getScaleNames() {
        return scaleNames == null ? null : Collections.unmodifiableList(scaleNames);
    }

    /** SRS definition containing the EPSG number. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Srs {
        private Integer number;
        /** Constructs an empty {@code Srs} for deserialization. */
        public Srs() {}
        /**
         * Constructs an {@code Srs} with the given EPSG number.
         * @param number the EPSG code
         */
        public Srs(Integer number) {
            this.number = number;
        }
        /** @return the EPSG number */
        public Integer getNumber() {
            return number;
        }
        /** @param number the EPSG number */
        public void setNumber(Integer number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Srs that = (Srs) o;
            return Objects.equals(number, that.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }

        @Override
        public String toString() {
            return "Srs{" +
                    "number=" + number +
                    '}';
        }
    }

    /** Bounding extent defined as a coordinate list {@code [minx, miny, maxx, maxy]}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Extent {
        @JacksonXmlElementWrapper(localName = "coords")
        @JacksonXmlProperty(localName = "double")
        private List<Double> coords;

        /** Constructs an empty {@code Extent} for deserialization. */
        public Extent() {}
        /**
         * Constructs an {@code Extent} from bounding coordinates.
         * @param minx the minimum x
         * @param miny the minimum y
         * @param maxx the maximum x
         * @param maxy the maximum y
         */
        public Extent(double minx, double miny, double maxx, double maxy) {
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
            Extent that = (Extent) o;
            return Objects.equals(coords, that.coords);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coords);
        }

        @Override
        public String toString() {
            return "Extent{" +
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
        GwcGridSet that = (GwcGridSet) o;
        return Objects.equals(name, that.name)
                && Objects.equals(srs, that.srs)
                && Objects.equals(extent, that.extent)
                && Objects.equals(alignTopLeft, that.alignTopLeft)
                && Objects.equals(resolutions, that.resolutions)
                && Objects.equals(metersPerUnit, that.metersPerUnit)
                && Objects.equals(pixelSize, that.pixelSize)
                && Objects.equals(tileWidth, that.tileWidth)
                && Objects.equals(tileHeight, that.tileHeight)
                && Objects.equals(yCoordinateFirst, that.yCoordinateFirst)
                && Objects.equals(description, that.description)
                && Objects.equals(scaleNames, that.scaleNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, srs, extent, alignTopLeft, resolutions, metersPerUnit, pixelSize, tileWidth, tileHeight, yCoordinateFirst, description, scaleNames);
    }

    @Override
    public String toString() {
        return "GwcGridSet{" +
                "name=" + name +
                ", srs=" + srs +
                ", extent=" + extent +
                ", alignTopLeft=" + alignTopLeft +
                ", resolutions=" + resolutions +
                ", metersPerUnit=" + metersPerUnit +
                ", pixelSize=" + pixelSize +
                ", tileWidth=" + tileWidth +
                ", tileHeight=" + tileHeight +
                ", yCoordinateFirst=" + yCoordinateFirst +
                ", description=" + description +
                ", scaleNames=" + scaleNames +
                '}';
    }
}
