package io.github.kimbongjune.geoserverclient.dto.gwc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Result DTO for {@code GET /gwc/rest} — GeoWebCache's REST resource index.
 *
 * <p>GeoServer returns this as an HTML directory-listing page (only 200/text/html is supported;
 * {@code .json}/{@code .xml} suffixes return 406) rather than a structured resource, so this DTO
 * parses the {@code href} links out of the page for convenience alongside the raw HTML.
 */
public class GwcIndexResult {

    private final List<String> resourceLinks;
    private final String rawHtml;

    /**
     * @param resourceLinks relative resource links parsed from the page (e.g. {@code "layers/"})
     * @param rawHtml       the raw HTML response body
     */
    public GwcIndexResult(List<String> resourceLinks, String rawHtml) {
        this.resourceLinks = resourceLinks != null ? resourceLinks : Collections.emptyList();
        this.rawHtml = rawHtml;
    }

    /** @return relative resource links parsed from the page (e.g. {@code "layers/"}), never null */
    public List<String> getResourceLinks() {
        return resourceLinks;
    }

    /** @return the raw HTML response body */
    public String getRawHtml() {
        return rawHtml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcIndexResult that = (GwcIndexResult) o;
        return Objects.equals(resourceLinks, that.resourceLinks)
                && Objects.equals(rawHtml, that.rawHtml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceLinks, rawHtml);
    }

    @Override
    public String toString() {
        return "GwcIndexResult{" +
                "resourceLinks=" + resourceLinks +
                '}';
    }
}
