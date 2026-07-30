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

    public GwcSeedRequest() {}

    public GwcSeedRequest(String name, int srs, int zoomStart, int zoomStop, String format, GwcSeedType type) {
        this.name = name;
        this.srs = srs;
        this.zoomStart = zoomStart;
        this.zoomStop = zoomStop;
        this.format = format;
        this.type = type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSrs() { return srs; }
    public void setSrs(Integer srs) { this.srs = srs; }

    public Integer getZoomStart() { return zoomStart; }
    public void setZoomStart(Integer zoomStart) { this.zoomStart = zoomStart; }

    public Integer getZoomStop() { return zoomStop; }
    public void setZoomStop(Integer zoomStop) { this.zoomStop = zoomStop; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public GwcSeedType getType() { return type; }
    public void setType(GwcSeedType type) { this.type = type; }

    /** truncate   ( 1  ) [ ]. */
    public Integer getThreadCount() { return threadCount; }
    public GwcSeedRequest threadCount(int threadCount) { this.threadCount = threadCount; return this; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
