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
 * are returned by GET but ignored on PUT.</p>
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

    public GwcGridSet() {}

    public GwcGridSet(String name, int epsgNumber, double minx, double miny, double maxx, double maxy) {
        this.name = name;
        this.srs = new Srs(epsgNumber);
        this.extent = new Extent(minx, miny, maxx, maxy);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Srs getSrs() { return srs; }
    public void setSrs(Srs srs) { this.srs = srs; }

    public Extent getExtent() { return extent; }
    public void setExtent(Extent extent) { this.extent = extent; }

    public Boolean getAlignTopLeft() { return alignTopLeft; }
    public void setAlignTopLeft(Boolean alignTopLeft) { this.alignTopLeft = alignTopLeft; }

    public List<Double> getResolutions() { return resolutions == null ? null : Collections.unmodifiableList(resolutions); }
    public void setResolutions(List<Double> resolutions) { this.resolutions = resolutions; }

    public Double getMetersPerUnit() { return metersPerUnit; }
    public void setMetersPerUnit(Double metersPerUnit) { this.metersPerUnit = metersPerUnit; }

    public Double getPixelSize() { return pixelSize; }
    public void setPixelSize(Double pixelSize) { this.pixelSize = pixelSize; }

    public Integer getTileWidth() { return tileWidth; }
    public void setTileWidth(Integer tileWidth) { this.tileWidth = tileWidth; }

    public Integer getTileHeight() { return tileHeight; }
    public void setTileHeight(Integer tileHeight) { this.tileHeight = tileHeight; }

    public Boolean getYCoordinateFirst() { return yCoordinateFirst; }
    public void setYCoordinateFirst(Boolean yCoordinateFirst) { this.yCoordinateFirst = yCoordinateFirst; }

    public String getDescription() { return description; }
    public List<String> getScaleNames() { return scaleNames == null ? null : Collections.unmodifiableList(scaleNames); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Srs {
        private Integer number;
        public Srs() {}
        public Srs(Integer number) { this.number = number; }
        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Extent {
        @JacksonXmlElementWrapper(localName = "coords")
        @JacksonXmlProperty(localName = "double")
        private List<Double> coords;

        public Extent() {}
        public Extent(double minx, double miny, double maxx, double maxy) {
            this.coords = Arrays.asList(minx, miny, maxx, maxy);
        }
        public List<Double> getCoords() { return coords == null ? null : Collections.unmodifiableList(coords); }
        public void setCoords(List<Double> coords) { this.coords = coords; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
