package io.github.kimbongjune.geoserverclient.dto.transform;

/**
 * Request DTO for updating a WFS output XSLT transform. Partial update — only non-null fields
 * are sent.
 *
 * <p><b>Unverified:</b> see {@link Transform} Javadoc for the plugin-availability caveat.
 */
public class UpdateTransformRequest {

    private String featureType;
    private String outputFormat;
    private String outputMimeType;
    private String xslt;

    private UpdateTransformRequest() {
    }

    /** @return a new, empty request */
    public static UpdateTransformRequest builder() {
        return new UpdateTransformRequest();
    }

    /** @param featureType the qualified feature type this transform applies to
     *  @return this request */
    public UpdateTransformRequest featureType(String featureType) {
        this.featureType = featureType;
        return this;
    }

    /** @param outputFormat the WFS output format that triggers this transform
     *  @return this request */
    public UpdateTransformRequest outputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    /** @param outputMimeType the MIME type of the transformed output
     *  @return this request */
    public UpdateTransformRequest outputMimeType(String outputMimeType) {
        this.outputMimeType = outputMimeType;
        return this;
    }

    /** @param xslt the XSLT stylesheet file name in the data directory
     *  @return this request */
    public UpdateTransformRequest xslt(String xslt) {
        this.xslt = xslt;
        return this;
    }

    /** Terminal no-op for {@code builder()...build()} chains. @return this request */
    public UpdateTransformRequest build() {
        return this;
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
