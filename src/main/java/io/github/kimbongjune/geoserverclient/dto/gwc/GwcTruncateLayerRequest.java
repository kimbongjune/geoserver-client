package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;

/**
 * Request DTO for truncating all cached tiles for a layer.
 * Verified (2026-07-29): returns 200 OK and clears tiles correctly.
 *
 * <pre>{@code <truncateLayer><layerName>sf:archsites</layerName></truncateLayer>}</pre>
 */
@JacksonXmlRootElement(localName = "truncateLayer")
public class GwcTruncateLayerRequest implements GwcTruncateRequest {

    private String layerName;

    /** Constructs an empty {@code GwcTruncateLayerRequest} for deserialization. */
    public GwcTruncateLayerRequest() {}

    /**
     * Constructs a {@code GwcTruncateLayerRequest} for the given layer.
     * @param layerName the GWC layer name (e.g. {@code "sf:archsites"})
     */
    public GwcTruncateLayerRequest(String layerName) {
        this.layerName = layerName;
    }

    /** @return the GWC layer name */
    public String getLayerName() {
        return layerName;
    }

    /**
     * Sets the GWC layer name.
     * @param layerName the layer name to set
     */
    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcTruncateLayerRequest that = (GwcTruncateLayerRequest) o;
        return Objects.equals(layerName, that.layerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerName);
    }

    @Override
    public String toString() {
        return "GwcTruncateLayerRequest{" +
                "layerName=" + layerName +
                '}';
    }
}
