package io.github.kimbongjune.geoserverclient.api.wmts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.UpdateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.WmtsStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GeoServer Cascading WMTS Store REST API manager.
 *
 * <p>A WMTS store lets GeoServer proxy tile layers from an external WMTS server (Cascading WMTS).
 * Create a WMTS store by registering the external server's GetCapabilities URL;
 * then publish individual tile layers via the layers API ({@link WmtsLayerManager}).
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET    /rest/workspaces/{ws}/wmtsstores
 * POST   /rest/workspaces/{ws}/wmtsstores
 * GET    /rest/workspaces/{ws}/wmtsstores/{storeName}
 * PUT    /rest/workspaces/{ws}/wmtsstores/{storeName}
 * DELETE /rest/workspaces/{ws}/wmtsstores/{storeName}
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * WmtsStoreManager mgr = client.wmtsStore();
 * CreateWmtsStoreRequest req = new CreateWmtsStoreRequest();
 * req.setName("my-wmts");
 * req.setCapabilitiesURL("https://example.com/wmts?SERVICE=WMTS&REQUEST=GetCapabilities");
 * WmtsStore store = mgr.create("myWorkspace", req);
 * }</pre>
 *
 * @since 1.0.0
 */
public class WmtsStoreManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class WmtsStoreEnvelope {
        @JsonProperty("wmtsStore")
        public WmtsStore wmtsStore;
    }

    /**
     * Constructs a new WmtsStoreManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public WmtsStoreManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Public API

    /**
     * Returns the summary list of WMTS stores in a workspace (name + href only).
     *
     * @param workspaceName workspace name (required)
     * @return list of WMTS store summaries, never null
     * @throws InvalidParameterException if workspaceName is null or empty
     */
    public List<WmtsStoreSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Gets a single WMTS store by name.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @return WMTS store details
     * @throws InvalidParameterException    if workspaceName or storeName is null/empty
     * @throws WmtsStoreNotFoundException   if no WMTS store with the given name exists
     */
    public WmtsStore get(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmtsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseStore(response.getBody());
    }

    /**
     * Returns whether a WMTS store exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public boolean exists(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Creates a new WMTS store.
     *
     * @param workspaceName workspace name (required)
     * @param request       create request (required; name + capabilitiesURL are mandatory)
     * @return the created WMTS store (fetched via GET after POST)
     * @throws InvalidParameterException      if workspaceName/request is null/empty
     * @throws ResourceAlreadyExistsException if a WMTS store with the same name already exists
     */
    public WmtsStore create(String workspaceName, CreateWmtsStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");

        if (exists(workspaceName, request.getName())) {
            throw new ResourceAlreadyExistsException("WmtsStore",
                    workspaceName + "/" + request.getName(), null);
        }

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores";
        String body = buildCreatePayload(workspaceName, request);
        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);

        return get(workspaceName, request.getName());
    }

    /**
     * Updates a WMTS store. Only the specified fields are changed (partial update).
     *
     * <p>Attempting to rename the store (setting a different name) returns 403 Forbidden.
     * Use storeName to identify which store to update.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     name of the WMTS store to update (required)
     * @param request       update request (required; only non-null fields are sent)
     * @return the updated WMTS store (fetched via GET after PUT)
     * @throws InvalidParameterException    if workspaceName/storeName/request is null/empty
     * @throws WmtsStoreNotFoundException   if no WMTS store with the given name exists
     */
    public WmtsStore update(String workspaceName, String storeName, UpdateWmtsStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName;
        String body = buildUpdatePayload(storeName, request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new WmtsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        return get(workspaceName, storeName);
    }

    /**
     * Deletes a WMTS store. Uses recurse=false; returns 401 if the store has WMTS layers.
     * Use {@link #delete(String, String, boolean)} with recurse=true to cascade-delete layers.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name to delete (required)
     * @throws InvalidParameterException  if workspaceName or storeName is null/empty
     * @throws WmtsStoreNotFoundException if no WMTS store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName) {
        delete(workspaceName, storeName, false);
    }

    /**
     * Deletes a WMTS store.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name to delete (required)
     * @param recurse       when true, cascade-deletes all associated WMTS layers and GeoServer layers;
     *                      when false, returns 401 if layers exist
     * @throws InvalidParameterException  if workspaceName or storeName is null/empty
     * @throws WmtsStoreNotFoundException if no WMTS store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                + "?recurse=" + recurse;

        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new WmtsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<WmtsStoreSummary> parseList(String body) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("wmtsStores").path("wmtsStore");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, WmtsStoreSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMTS store list response", e);
        }
    }

    private WmtsStore parseStore(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            WmtsStoreEnvelope envelope = om.readValue(body, WmtsStoreEnvelope.class);
            return envelope.wmtsStore;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMTS store response", e);
        }
    }

    // Payload builders

    /**
     * Builds the metadata entry map for useConnectionPooling.
     * WmtsStore requires this format; the flat "useConnectionPooling" field is silently ignored [verified].
     */
    private Map<String, Object> buildMetadataEntry(boolean pooling) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("@key", "useConnectionPooling");
        entry.put("$", pooling ? "true" : "false");
        return Collections.singletonMap("entry", entry);
    }

    private String buildCreatePayload(String workspaceName, CreateWmtsStoreRequest req) {
        Map<String, Object> store = new LinkedHashMap<>();
        store.put("name", req.getName());
        store.put("capabilitiesURL", req.getCapabilitiesURL());
        store.put("workspace", Collections.singletonMap("name", workspaceName));
        if (req.getDescription() != null) {
            store.put("description",          req.getDescription());
        }
        if (req.getEnabled() != null) {
            store.put("enabled",              req.getEnabled());
        }
        if (req.getDefaultStore() != null) {
            store.put("_default",             req.getDefaultStore());
        }
        if (req.getUser() != null) {
            store.put("user",                 req.getUser());
        }
        if (req.getPassword() != null) {
            store.put("password",             req.getPassword());
        }
        if (req.getAuthKey() != null) {
            store.put("authKey",              req.getAuthKey());
        }
        if (req.getHeaderName() != null) {
            store.put("headerName",           req.getHeaderName());
        }
        if (req.getHeaderValue() != null) {
            store.put("headerValue",          req.getHeaderValue());
        }
        if (req.getMaxConnections() != null) {
            store.put("maxConnections",       req.getMaxConnections());
        }
        if (req.getReadTimeout() != null) {
            store.put("readTimeout",          req.getReadTimeout());
        }
        if (req.getConnectTimeout() != null) {
            store.put("connectTimeout",       req.getConnectTimeout());
        }
        if (req.getDisableOnConnFailure() != null) {
            store.put("disableOnConnFailure", req.getDisableOnConnFailure());
        }
        if (req.getUseConnectionPooling() != null) {
            store.put("metadata",             buildMetadataEntry(req.getUseConnectionPooling()));
        }
        return serializeToJson(Collections.singletonMap("wmtsStore", store));
    }

    private String buildUpdatePayload(String storeName, UpdateWmtsStoreRequest req) {
        Map<String, Object> store = new LinkedHashMap<>();
        store.put("name", storeName);
        if (req.getDescription() != null) {
            store.put("description",          req.getDescription());
        }
        if (req.getEnabled() != null) {
            store.put("enabled",              req.getEnabled());
        }
        if (req.getCapabilitiesURL() != null) {
            store.put("capabilitiesURL",      req.getCapabilitiesURL());
        }
        if (req.getUser() != null) {
            store.put("user",                 req.getUser());
        }
        if (req.getPassword() != null) {
            store.put("password",             req.getPassword());
        }
        if (req.getAuthKey() != null) {
            store.put("authKey",              req.getAuthKey());
        }
        if (req.getHeaderName() != null) {
            store.put("headerName",           req.getHeaderName());
        }
        if (req.getHeaderValue() != null) {
            store.put("headerValue",          req.getHeaderValue());
        }
        if (req.getMaxConnections() != null) {
            store.put("maxConnections",       req.getMaxConnections());
        }
        if (req.getReadTimeout() != null) {
            store.put("readTimeout",          req.getReadTimeout());
        }
        if (req.getConnectTimeout() != null) {
            store.put("connectTimeout",       req.getConnectTimeout());
        }
        if (req.getDisableOnConnFailure() != null) {
            store.put("disableOnConnFailure", req.getDisableOnConnFailure());
        }
        if (req.getUseConnectionPooling() != null) {
            store.put("metadata",             buildMetadataEntry(req.getUseConnectionPooling()));
        }
        return serializeToJson(Collections.singletonMap("wmtsStore", store));
    }


}
