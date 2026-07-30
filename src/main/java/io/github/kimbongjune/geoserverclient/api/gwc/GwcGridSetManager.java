package io.github.kimbongjune.geoserverclient.api.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.exception.GwcGridSetNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.List;

/**
 * GeoWebCache (GWC) GridSet REST API client.
 *
 * <p>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/gridsets}
 *
 * <h2>Endpoints (4)</h2>
 * <pre>{@code
 * [1] GET    /gwc/rest/gridsets              list all grid sets           200
 * [2] GET    /gwc/rest/gridsets/{name}       get grid set details         200 / 404
 * [3] PUT    /gwc/rest/gridsets/{name}       create or update (upsert)    200 / 201
 * [4] DELETE /gwc/rest/gridsets/{name}       delete grid set              200 / 404
 * }</pre>
 * <p><b>Note:</b> POST does not exist; creation is done via PUT.
 *
 * <p><b>GET [1] observed (2026-07-29):</b> returns 100+ built-in plus any custom grid sets.
 *
 * <p><b>JSON PUT bug:</b> JSON PUT causes an XStream "Duplicate field coords" 500.
 * {@link #upsert(String, GwcGridSet)} always sends XML.
 *
 * <p><b>Deleting built-in grid sets:</b> built-in grid sets can be deleted via API,
 * but doing so may break tile layers that reference them.
 */
public class GwcGridSetManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class GridSetEnvelope {
        @JsonProperty("gridSet")
        public GwcGridSet gridSet;
    }

    public GwcGridSetManager(GeoServerHttpClient httpClient,
                             SerializerFactory serializerFactory,
                             DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/gridsets

    /** Returns all grid set names (includes 100+ built-in sets). */
    @SuppressWarnings("unchecked")
    public List<String> list() {
        String body = doGetRaw("/gwc/rest/gridsets", "application/json");
        try {
            return getObjectMapper().readValue(body, List.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC grid set list response", e);
        }
    }

    // [2] GET /gwc/rest/gridsets/{name}

    /**
     * Returns the definition of a specific grid set.
     *
     * <p><b>GeoServer bug (2026-07-29, GeoServer 2.28.2):</b> returns
     * <b>500</b> {@code "...does not exist."} instead of 404 when the grid set is unknown.
     * Both cases are mapped to {@link GwcGridSetNotFoundException}.
     */
    public GwcGridSet get(String name) {
        requireNonEmpty(name, "name");
        String path = "/gwc/rest/gridsets/" + name;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound() || isDoesNotExistError(response)) {
            throw new GwcGridSetNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        try {
            GridSetEnvelope envelope = getObjectMapper().readValue(response.getBody(), GridSetEnvelope.class);
            return envelope.gridSet;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC grid set response", e);
        }
    }

    // [3] PUT /gwc/rest/gridsets/{name}

    /**
     * Creates (201) or updates (200) a grid set. Always sent as XML —
     * JSON PUT causes an XStream 500 on this endpoint.
     */
    public void upsert(String name, GwcGridSet request) {
        requireNonEmpty(name, "name");
        requireNonNull(request, "request");
        request.setName(name);
        doPut("/gwc/rest/gridsets/" + name, request, DataFormat.XML);
    }

    // [4] DELETE /gwc/rest/gridsets/{name}

    /** Deletes a grid set. Built-in grid sets can also be deleted (dangerous). */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        doDelete("/gwc/rest/gridsets/" + name);
    }

    private boolean isDoesNotExistError(GeoServerResponse response) {
        return response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("does not exist");
    }

}
