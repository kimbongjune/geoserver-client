package io.github.kimbongjune.geoserverclient.dto.gwc;

import java.util.Objects;

/**
 * DTO for a GWC WMSRasterFilter content update.
 * Used as the request body for {@code POST /gwc/rest/filter/{filterName}/update/xml}.
 *
 * <p>WMSRasterFilter XML is passed through raw (GeoWebCache XStream format),
 * analogous to {@link io.github.kimbongjune.geoserverclient.dto.style.StyleContent}.
 * Use {@link #empty()} to trigger a filter reload without providing new content.
 */
public class GwcFilterContent {

    private final String xmlBody;

    private GwcFilterContent(String xmlBody) {
        this.xmlBody = xmlBody;
    }

    /**
     * Creates a filter content holder from a raw WMSRasterFilter XML string.
     * @param xmlBody the raw WMSRasterFilter XML, or {@code null} for empty
     * @return a new {@code GwcFilterContent} instance
     */
    public static GwcFilterContent of(String xmlBody) {
        return new GwcFilterContent(xmlBody != null ? xmlBody : "");
    }

    /**
     * Creates an empty update (triggers a filter reload without supplying new XML).
     * @return a new empty {@code GwcFilterContent} instance
     */
    public static GwcFilterContent empty() {
        return new GwcFilterContent("");
    }

    /** @return the raw WMSRasterFilter XML body */
    public String getXmlBody() {
        return xmlBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcFilterContent that = (GwcFilterContent) o;
        return Objects.equals(xmlBody, that.xmlBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlBody);
    }

    @Override
    public String toString() {
        return "GwcFilterContent{" +
                "xmlBody=" + xmlBody +
                '}';
    }
}
