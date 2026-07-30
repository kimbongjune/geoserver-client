package io.github.kimbongjune.geoserverclient.api.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFileBlobStore;
import io.github.kimbongjune.geoserverclient.exception.GwcBlobStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.List;

/**
 * GeoWebCache (GWC) BlobStore REST API client.
 *
 * <p>Source: {@code geowebcache/rest/src/main/java/org/geowebcache/rest/controller/BlobStoreController.java}
 * <br>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/blobstores}
 *
 * <h2>Endpoints (4)</h2>
 * <pre>{@code
 * [1] GET    /gwc/rest/blobstores              list all blob stores          200
 * [2] GET    /gwc/rest/blobstores/{name}       get blob store details        200 / 404
 * [3] PUT    /gwc/rest/blobstores/{name}       create or update (upsert)     200 / 201
 * [4] DELETE /gwc/rest/blobstores/{name}       delete blob store             200 / 404 / 500
 * }</pre>
 * <p><b>Note:</b> POST does not exist; creation is also done via PUT.
 *
 * <p><b>GET [1] observed behavior (2026-07-29):</b> returns a plain string array
 * {@code ["probe1"]} — not {@code {name, href}} objects.
 *
 * <p><b>Deleting the default BlobStore:</b> DELETE on the default BlobStore
 * causes 500 {@code ConfigurationException} [server-verified].
 *
 * <p>Only {@code FileBlobStore} is supported here — see {@link GwcFileBlobStore}.
 * PUT always uses XML (JSON PUT triggers an XStream 500).
 */
public class GwcBlobStoreManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class FileBlobStoreEnvelope {
        @JsonProperty("FileBlobStore")
        public GwcFileBlobStore store;
    }

    public GwcBlobStoreManager(GeoServerHttpClient httpClient,
                               SerializerFactory serializerFactory,
                               DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/blobstores

    /** Returns all blob store names (empty list if none). */
    @SuppressWarnings("unchecked")
    public List<String> list() {
        String body = doGetRaw("/gwc/rest/blobstores", "application/json");
        try {
            return getObjectMapper().readValue(body, List.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC blob store list response", e);
        }
    }

    // [2] GET /gwc/rest/blobstores/{blobStoreName}

    /**
     * Returns the configuration of a specific FileBlobStore.
     *
     * <p><b>GeoServer bug (2026-07-29, GeoServer 2.28.2):</b> returns
     * <b>500</b> {@code "...does not exist."} instead of 404 when the store is unknown.
     * Both cases are mapped to {@link GwcBlobStoreNotFoundException}.
     */
    public GwcFileBlobStore get(String name) {
        requireNonEmpty(name, "name");
        String path = "/gwc/rest/blobstores/" + name;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound() || isDoesNotExistError(response)) {
            throw new GwcBlobStoreNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        try {
            FileBlobStoreEnvelope envelope = getObjectMapper().readValue(response.getBody(), FileBlobStoreEnvelope.class);
            return envelope.store;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GWC blob store response", e);
        }
    }

    // [3] PUT /gwc/rest/blobstores/{blobStoreName}

    /**
     * Creates (201) or updates (200) a FileBlobStore. Always sent as XML —
     * JSON PUT causes an XStream 500. URL {@code name} must match the store's {@code id}.
     */
    public void upsert(String name, GwcFileBlobStore request) {
        requireNonEmpty(name, "name");
        requireNonNull(request, "request");
        request.setId(name);
        doPut("/gwc/rest/blobstores/" + name, request, DataFormat.XML);
    }

    // [4] DELETE /gwc/rest/blobstores/{blobStoreName}

    /** Deletes a blob store. Deleting the default store causes 500. */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        doDelete("/gwc/rest/blobstores/" + name);
    }

    private boolean isDoesNotExistError(GeoServerResponse response) {
        return response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("does not exist");
    }

}
