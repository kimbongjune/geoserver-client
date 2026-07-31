package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;

/**
 * Request DTO for {@code POST /gwc/rest/seed/{layer}.xml} (GWC seed/truncate).
 * Must be serialized as XML — JSON causes a Jettison-related error (see GwcSeedManager).
 */
@JacksonXmlRootElement(localName = "seedRequest")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcSeedRequest {

    private String name;
    private Integer srs;
    private Integer zoomStart;
    private Integer zoomStop;
    private String format;
    private GwcSeedType type;
    private Integer threadCount;

    /** Constructs an empty {@code GwcSeedRequest} for deserialization. */
    public GwcSeedRequest() {}

    /**
     * Constructs a {@code GwcSeedRequest} with all required fields.
     * @param name      the layer name
     * @param srs       the EPSG code (e.g. {@code 4326})
     * @param zoomStart the starting zoom level
     * @param zoomStop  the stopping zoom level
     * @param format    the MIME format (e.g. {@code "image/png"})
     * @param type      the seed operation type
     */
    public GwcSeedRequest(String name, int srs, int zoomStart, int zoomStop, String format, GwcSeedType type) {
        this.name = name;
        this.srs = srs;
        this.zoomStart = zoomStart;
        this.zoomStop = zoomStop;
        this.format = format;
        this.type = type;
    }

    /** @return the layer name */
    public String getName() {
        return name;
    }
    /** @param name the layer name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the EPSG SRS code */
    public Integer getSrs() {
        return srs;
    }
    /** @param srs the EPSG SRS code */
    public void setSrs(Integer srs) {
        this.srs = srs;
    }

    /** @return the starting zoom level */
    public Integer getZoomStart() {
        return zoomStart;
    }
    /** @param zoomStart the starting zoom level */
    public void setZoomStart(Integer zoomStart) {
        this.zoomStart = zoomStart;
    }

    /** @return the stopping zoom level */
    public Integer getZoomStop() {
        return zoomStop;
    }
    /** @param zoomStop the stopping zoom level */
    public void setZoomStop(Integer zoomStop) {
        this.zoomStop = zoomStop;
    }

    /** @return the tile MIME format */
    public String getFormat() {
        return format;
    }
    /** @param format the tile MIME format */
    public void setFormat(String format) {
        this.format = format;
    }

    /** @return the seed operation type */
    public GwcSeedType getType() {
        return type;
    }
    /** @param type the seed operation type */
    public void setType(GwcSeedType type) {
        this.type = type;
    }

    /**
     * Returns the thread count. Ignored for truncate tasks (always runs on 1 thread).
     * @return the thread count, or {@code null} for the default
     */
    public Integer getThreadCount() {
        return threadCount;
    }
    /**
     * Sets the thread count for this seed operation.
     * @param threadCount the number of threads
     * @return this request for chaining
     */
    public GwcSeedRequest threadCount(int threadCount) {
        this.threadCount = threadCount; return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcSeedRequest that = (GwcSeedRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(srs, that.srs)
                && Objects.equals(zoomStart, that.zoomStart)
                && Objects.equals(zoomStop, that.zoomStop)
                && Objects.equals(format, that.format)
                && Objects.equals(type, that.type)
                && Objects.equals(threadCount, that.threadCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, srs, zoomStart, zoomStop, format, type, threadCount);
    }

    @Override
    public String toString() {
        return "GwcSeedRequest{" +
                "name=" + name +
                ", srs=" + srs +
                ", zoomStart=" + zoomStart +
                ", zoomStop=" + zoomStop +
                ", format=" + format +
                ", type=" + type +
                ", threadCount=" + threadCount +
                '}';
    }
}
