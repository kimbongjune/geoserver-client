package io.github.kimbongjune.geoserverclient.api.gwc;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.exception.GwcLayerNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.Serializer;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.List;



/**
 * GeoWebCache (GWC) Layer REST API client.
 *
 * <p>Source: {@code geowebcache/rest/src/main/java/org/geowebcache/rest/controller/TileLayerController.java}
 * <br>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/layers}
 *
 * <h2>Endpoints (5)</h2>
 * <pre>{@code
 * [1] GET    /gwc/rest/layers              list all tile layers           200
 * [2] GET    /gwc/rest/layers/{name}       get tile layer details         200 / 404
 * [3] POST   /gwc/rest/layers/{name}       add layer (deprecated)         200
 * [4] PUT    /gwc/rest/layers/{name}       create or update (upsert)      200 / 201
 * [5] DELETE /gwc/rest/layers/{name}       delete layer and tile cache    200 / 404
 * }</pre>
 *
 * <p><b>GET [1] observed (2026-07-29):</b> returns a plain string array {@code ["ne:vworld_2d_map"]} —
 * not {@code {name, href}} objects (contrary to some GWC Javadoc).
 *
 * <p><b>GET [2] observed (2026-07-29) response shape:</b>
 * <pre>{@code
 * {"GeoServerLayer":{
 *   "name":"ne:vworld_2d_map","enabled":true,"inMemoryCached":true,"id":"LayerInfoImpl--...",
 *   "mimeFormats":["image/png","image/jpeg"],
 *   "gridSubsets":[{"gridSetName":"EPSG:4326"},{"gridSetName":"EPSG:900913"}],
 *   "parameterFilters":[{"defaultValue":"","key":"STYLES"}],
 *   "metaWidthHeight":[4,4],"expireCache":0,"expireClients":0,"gutter":0,"cacheWarningSkips":[]
 * }}
 * }</pre>
 *
 * <p><b>JSON PUT bug:</b> JSON PUT causes an XStream "Duplicate field" 500.
 * {@link #upsert(String, GwcLayer)} always uses XML.
 *
 * <p><b>DELETE also removes the tile cache.</b>
 *
 * <h2>[4] PUT /gwc/rest/layers/{name} — expireCache / expireClients special values</h2>
 * <pre>{@code
 * -1 = never expire
 * -2 = disable
 * -3 = use HTTP headers from the WMS backend
 * -4 = unset
 * }</pre>
 *
 * <p>In GeoServer integrated mode, the GeoServer layer must already exist before PUT succeeds [server-verified].
 * {@code parameterFilters} are returned in GET responses — see {@link GwcLayer}.
 */
public class GwcLayerManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class GeoServerLayerEnvelope {
        @JsonProperty("GeoServerLayer")
        public GwcLayer layer;
    }

    public GwcLayerManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/layers

    /** Returns all tile layer names. */
    @SuppressWarnings("unchecked")
    public List<String> list() {
        String body = doGetRaw("/gwc/rest/layers", "application/json");
        try {
            return getObjectMapper().readValue(body, List.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC layer list response", e);
        }
    }

    // [2] GET /gwc/rest/layers/{layer}

    /**
     * Returns tile layer details. Layer name format: {@code workspace:name}.
     *
     * <p><b>GeoServer bug (2026-07-29, GeoServer 2.28.2 / GWC 1.28.2):</b> returns
     * <b>500</b> {@code "Unknown layer: {name}"} instead of 404 when the layer is not registered.
     * Both cases are mapped to {@link GwcLayerNotFoundException}.
     *
     * @throws GwcLayerNotFoundException if the layer is not registered in GWC
     */
    public GwcLayer get(String layer) {
        requireNonEmpty(layer, "layer");
        String path = "/gwc/rest/layers/" + layer;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound() || isUnknownLayerError(response)) {
            throw new GwcLayerNotFoundException(layer, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    // [4] PUT /gwc/rest/layers/{layerName}

    /**
     * Creates or updates a tile layer (upsert). Always serialized as XML —
     * JSON PUT causes an XStream 500 on GeoServer 2.28.2. Only works for layers
     * that already exist in GeoServer's catalog.
     */
    public void upsert(String layerName, GwcLayer request) {
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");
        request.setName(layerName);
        doPut("/gwc/rest/layers/" + layerName, request, DataFormat.XML);
    }

    // [3] POST /gwc/rest/layers/{layerName} (Deprecated)

    /** @deprecated Use {@link #upsert(String, GwcLayer)} instead. */
    @Deprecated
    public void updateDeprecated(String layerName, GwcLayer request) {
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");
        request.setName(layerName);
        String content = getXmlSerializer().serialize(request);
        doPostRaw("/gwc/rest/layers/" + layerName, content, "application/xml", "application/json");
    }

    // [5] DELETE /gwc/rest/layers/{layer}

    /** Deletes a tile layer and its tile cache. */
    public void delete(String layer) {
        requireNonEmpty(layer, "layer");
        doDelete("/gwc/rest/layers/" + layer);
    }

    // Parsing helpers

    private GwcLayer parseLayer(String body) {
        try {
            GeoServerLayerEnvelope envelope = getObjectMapper().readValue(body, GeoServerLayerEnvelope.class);
            return envelope.layer;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC layer response", e);
        }
    }

    private boolean isUnknownLayerError(GeoServerResponse response) {
        return response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("Unknown layer");
    }


    private Serializer getXmlSerializer() {
        return serializerFactory.getSerializer(DataFormat.XML);
    }
}
