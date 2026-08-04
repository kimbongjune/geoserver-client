package io.github.kimbongjune.geoserverclient.dto.transform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * DTO for a WFS output XSLT transform, as returned by
 * {@code GET /rest/services/wfs/transforms/{name}}.
 *
 * <p><b>Unverified:</b> the XSLT Transform plugin is not available on GeoServer 2.28.x
 * (stable or community), so these field names could not be confirmed against a live server —
 * see {@link io.github.kimbongjune.geoserverclient.api.transform.TransformManager} Javadoc.
 * Field names follow GeoServer's published WFS Output Transformation extension documentation.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transform {

    private String name;
    private String featureType;
    private String outputFormat;
    private String outputMimeType;
    private String xslt;

    /** @return the transform name */
    public String getName() {
        return name;
    }
    /** @return the qualified feature type this transform applies to (e.g. {@code "topp:states"}) */
    public String getFeatureType() {
        return featureType;
    }
    /** @return the WFS output format that triggers this transform */
    public String getOutputFormat() {
        return outputFormat;
    }
    /** @return the MIME type of the transformed output */
    public String getOutputMimeType() {
        return outputMimeType;
    }
    /** @return the XSLT stylesheet file name in the data directory */
    public String getXslt() {
        return xslt;
    }

    @Override
    public String toString() {
        return "Transform{" +
                "name=" + name +
                ", featureType=" + featureType +
                ", outputFormat=" + outputFormat +
                ", outputMimeType=" + outputMimeType +
                ", xslt=" + xslt +
                '}';
    }
}
