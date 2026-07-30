package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import java.util.Objects;

/**
 * Request DTO for truncating tiles with specific parameters (e.g. a given STYLES value).
 * GeoWebCache documentation for this operation is sparse; behavior was verified via curl
 * (see GwcMassTruncateManager for known issues with the GWC REST response format).
 */
@JacksonXmlRootElement(localName = "truncateParameters")
public class GwcTruncateParametersRequest implements GwcTruncateRequest {

    private String layerName;
    private StringMap parameters;

    public GwcTruncateParametersRequest() {}

    public GwcTruncateParametersRequest(String layerName, StringMap parameters) {
        this.layerName = layerName;
        this.parameters = parameters;
    }

    public String getLayerName() { return layerName; }
    public void setLayerName(String layerName) { this.layerName = layerName; }

    public StringMap getParameters() { return parameters; }
    public void setParameters(StringMap parameters) { this.parameters = parameters; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcTruncateParametersRequest that = (GwcTruncateParametersRequest) o;
        return Objects.equals(layerName, that.layerName)
                && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layerName, parameters);
    }

    @Override
    public String toString() {
        return "GwcTruncateParametersRequest{" +
                "layerName=" + layerName +
                ", parameters=" + parameters +
                '}';
    }
}
