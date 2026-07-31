package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateRequest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateRequestTypes;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;

/**
 * GeoWebCache (GWC) Mass Truncate REST API client.
 *
 * <p>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/masstruncate}
 *
 * <h2>Endpoints (2)</h2>
 * <pre>{@code
 * [1] GET  /gwc/rest/masstruncate    list supported truncate types     200
 * [2] POST /gwc/rest/masstruncate    execute mass truncate (sync)      200
 * }</pre>
 *
 * <p><b>GET [1] (2026-07-29):</b> always returns XML regardless of the Accept header.
 *
 * <p><b>POST [2] Content-Type requirement:</b> must be {@code text/xml}.
 * {@code application/xml} and {@code application/json} both cause 400 Bad Request [server-verified].
 *
 * <p>Supported request types:
 * {@link io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateLayerRequest} (most common),
 * {@link io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateParametersRequest},
 * {@link io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateOrphansRequest} (causes 500 with file-based BlobStore),
 * {@link io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateExtentRequest} (creates async seed tasks internally).
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * GwcMassTruncateManager mgr = client.gwcMassTruncate();
 * GwcTruncateLayerRequest req = new GwcTruncateLayerRequest("ne:vworld_2d_map");
 * mgr.truncate(req);
 * }</pre>
 *
 * @since 1.0.0
 */
public class GwcMassTruncateManager extends AbstractManager {

    /**
     * Constructs a new GwcMassTruncateManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public GwcMassTruncateManager(GeoServerHttpClient httpClient,
                                  SerializerFactory serializerFactory,
                                  DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/masstruncate

    /**
     * Returns the list of supported mass-truncate request types.
     * Response is always XML regardless of the Accept header.
     *
     * @return supported truncate request type names
     */
    public GwcTruncateRequestTypes listRequestTypes() {
        String body = doGetRaw("/gwc/rest/masstruncate", "application/xml");
        try {
            return serializerFactory.getSerializer(DataFormat.XML)
                    .deserialize(body, GwcTruncateRequestTypes.class);
        } catch (Exception e) {
            throw new SerializationException("Failed to parse GWC mass-truncate type list", e);
        }
    }

    // [2] POST /gwc/rest/masstruncate

    /**
     * Executes a mass-truncate request synchronously.
     * Content-Type is always {@code text/xml} (per server requirement) regardless of the client's default format.
     *
     * @param request the truncate request (one of the supported subtypes: layer, parameters, orphans, or extent)
     */
    public void truncate(GwcTruncateRequest request) {
        requireNonNull(request, "request");
        String content = serializerFactory.getSerializer(DataFormat.XML).serialize(request);
        doPostRaw("/gwc/rest/masstruncate", content, "text/xml", "text/plain");
    }
}
