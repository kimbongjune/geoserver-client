package io.github.kimbongjune.geoserverclient.dto.transform;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;

/**
 * Request DTO for creating a WFS output XSLT transform.
 *
 * <p><b>Unverified:</b> see {@link Transform} Javadoc for the plugin-availability caveat.
 */
public class CreateTransformRequest {

    private final String name;
    private String featureType;
    private String outputFormat;
    private String outputMimeType;
    private String xslt;

    private CreateTransformRequest(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "transform name must not be null or empty");
        }
        this.name = name.trim();
    }

    /**
     * @param name the transform name (must not be null or empty)
     * @return a new request
     */
    public static CreateTransformRequest of(String name) {
        return new CreateTransformRequest(name);
    }

    /** @param featureType the qualified feature type this transform applies to
     *  @return this request */
    public CreateTransformRequest featureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    /** @param outputFormat the WFS output format that triggers this transform
     *  @return this request */
    public CreateTransformRequest outputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    /** @param outputMimeType the MIME type of the transformed output
     *  @return this request */
    public CreateTransformRequest outputMimeType(String outputMimeType) {
        this.outputMimeType = outputMimeType;
        return this;
    }

    /** @param xslt the XSLT stylesheet file name in the data directory
     *  @return this request */
    public CreateTransformRequest xslt(String xslt) {
        this.xslt = xslt;
        return this;
    }

    /** @return the transform name */
    public String getName() {
        return name;
    }
    /** @return the feature type */
    public String getFeatureType() {
        return featureType;
    }
    /** @return the output format */
    public String getOutputFormat() {
        return outputFormat;
    }
    /** @return the output MIME type */
    public String getOutputMimeType() {
        return outputMimeType;
    }
    /** @return the XSLT stylesheet file name */
    public String getXslt() {
        return xslt;
    }
}
