package io.github.kimbongjune.geoserverclient.dto.gwc;

/**
 * Marker interface for {@code POST /gwc/rest/masstruncate} request bodies.
 * GWC truncate requests are XML-serialized; each subtype maps to a different XML root element.
 * Implementations: {@link GwcTruncateLayerRequest}, {@link GwcTruncateParametersRequest},
 * {@link GwcTruncateOrphansRequest}, {@link GwcTruncateExtentRequest}.
 */
public interface GwcTruncateRequest {
}
