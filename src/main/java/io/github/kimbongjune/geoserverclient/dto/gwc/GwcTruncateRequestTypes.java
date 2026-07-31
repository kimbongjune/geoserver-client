package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * Response DTO for {@code GET /gwc/rest/masstruncate} — lists the supported truncate request types.
 *
 * <p>Note (2026-07-29): the response only returns XML regardless of the Accept header:
 * <pre>{@code
 * <massTruncateRequests href="http://.../gwc/rest/masstruncate">
 *   <requestType>truncateLayer</requestType>
 *   <requestType>truncateParameters</requestType>
 *   <requestType>truncateOrphans</requestType>
 *   <requestType>truncateExtent</requestType>
 * </massTruncateRequests>
 * }</pre>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JacksonXmlRootElement(localName = "massTruncateRequests")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcTruncateRequestTypes {

    @JacksonXmlProperty(isAttribute = true)
    private String href;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "requestType")
    private List<String> requestTypes;

    /** @return the WMTS layer href */
    public String getHref() {
        return href;
    }

    /** @return the supported truncation request types */
    public List<String> getRequestTypes() {
        return requestTypes == null ? null : Collections.unmodifiableList(requestTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcTruncateRequestTypes that = (GwcTruncateRequestTypes) o;
        return Objects.equals(href, that.href)
                && Objects.equals(requestTypes, that.requestTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, requestTypes);
    }

    @Override
    public String toString() {
        return "GwcTruncateRequestTypes{" +
                "href=" + href +
                ", requestTypes=" + requestTypes +
                '}';
    }
}
