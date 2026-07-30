package io.github.kimbongjune.geoserverclient.api.wms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.UpdateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.WmsStoreNotFoundException;
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
 * GeoServer Cascading WMS Store REST API manager.
 *
 * <p>A WMS store lets GeoServer proxy layers from an external WMS server (Cascading WMS).
 * Create a WMS store by registering the external server's GetCapabilities URL;
 * then publish individual layers via the wmslayers API (WmsLayerManager).</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * </pre>
 */
public class WmsStoreManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class WmsStoreEnvelope {
        @JsonProperty("wmsStore")
        public WmsStore wmsStore;
    }

    public WmsStoreManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Public API

    /**
     * Returns the summary list of WMS stores in a workspace (name + href only).
     *
     * @param workspaceName workspace name (required)
     * @return list of WMS store summaries, never null
     * @throws InvalidParameterException if workspaceName is null or empty
     */
    public List<WmsStoreSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/wmsstores";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Gets a single WMS store by name.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @return WMS store details
     * @throws InvalidParameterException   if workspaceName or storeName is null/empty
     * @throws WmsStoreNotFoundException   if no WMS store with the given name exists
     */
    public WmsStore get(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseStore(response.getBody());
    }

    /**
     * Returns whether a WMS store exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public boolean exists(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Creates a new WMS store.
     *
     * @param workspaceName workspace name (required)
     * @param request       create request (required; name + capabilitiesURL are mandatory)
     * @return the created WMS store (fetched via GET after POST)
     * @throws InvalidParameterException      if workspaceName/request is null/empty
     * @throws ResourceAlreadyExistsException if a WMS store with the same name already exists
     */
    public WmsStore create(String workspaceName, CreateWmsStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores";
        String body = buildCreatePayload(workspaceName, request);
        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");
        // GeoServer returns 500 (not 409) for duplicate WMS store names — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("WmsStore",
                    workspaceName + "/" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);

        return get(workspaceName, request.getName());
    }

    /**
     * Updates a WMS store. Only the specified fields are changed (partial update).
     *
     * <p>Attempting to rename the store (setting a different name in the request) returns 403 Forbidden.
     * Use storeName to identify which store to update.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     name of the WMS store to update (required)
     * @param request       update request (required; only non-null fields are sent)
     * @return the updated WMS store (fetched via GET after PUT)
     * @throws InvalidParameterException   if workspaceName/storeName/request is null/empty
     * @throws WmsStoreNotFoundException   if no WMS store with the given name exists
     */
    public WmsStore update(String workspaceName, String storeName, UpdateWmsStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName;
        String body = buildUpdatePayload(storeName, request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new WmsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        return get(workspaceName, storeName);
    }

    /**
     * Deletes a WMS store. Uses recurse=false; returns 401 if the store has WMS layers.
     * Use {@link #delete(String, String, boolean)} with recurse=true to cascade-delete layers.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name to delete (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     * @throws WmsStoreNotFoundException if no WMS store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName) {
        delete(workspaceName, storeName, false);
    }

    /**
     * Deletes a WMS store.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name to delete (required)
     * @param recurse       when true, cascade-deletes all associated WMS layers;
     *                      when false, returns 401 if layers exist
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     * @throws WmsStoreNotFoundException if no WMS store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                + "?recurse=" + recurse;

        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new WmsStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<WmsStoreSummary> parseList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("wmsStores").path("wmsStore");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, WmsStoreSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMS store list response", e);
        }
    }

    private WmsStore parseStore(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            WmsStoreEnvelope envelope = om.readValue(body, WmsStoreEnvelope.class);
            return envelope.wmsStore;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMS store response", e);
        }
    }

    // Payload builders

    private String buildCreatePayload(String workspaceName, CreateWmsStoreRequest req) {
        Map<String, Object> store = new LinkedHashMap<>();
        store.put("name", req.getName());
        store.put("capabilitiesURL", req.getCapabilitiesURL());
        store.put("workspace", Collections.singletonMap("name", workspaceName));
        if (req.getDescription() != null)           store.put("description",          req.getDescription());
        if (req.getEnabled() != null)               store.put("enabled",              req.getEnabled());
        if (req.getDefaultStore() != null)          store.put("_default",             req.getDefaultStore());
        if (req.getUser() != null)                  store.put("user",                 req.getUser());
        if (req.getPassword() != null)              store.put("password",             req.getPassword());
        if (req.getAuthKey() != null)               store.put("authKey",              req.getAuthKey());
        if (req.getHeaderName() != null)            store.put("headerName",           req.getHeaderName());
        if (req.getHeaderValue() != null)           store.put("headerValue",          req.getHeaderValue());
        if (req.getMaxConnections() != null)        store.put("maxConnections",       req.getMaxConnections());
        if (req.getReadTimeout() != null)           store.put("readTimeout",          req.getReadTimeout());
        if (req.getConnectTimeout() != null)        store.put("connectTimeout",       req.getConnectTimeout());
        if (req.getDisableOnConnFailure() != null)  store.put("disableOnConnFailure", req.getDisableOnConnFailure());
        return serializeToJson(Collections.singletonMap("wmsStore", store));
    }

    private String buildUpdatePayload(String storeName, UpdateWmsStoreRequest req) {
        Map<String, Object> store = new LinkedHashMap<>();
        store.put("name", storeName);
        if (req.getDescription() != null)           store.put("description",          req.getDescription());
        if (req.getEnabled() != null)               store.put("enabled",              req.getEnabled());
        if (req.getCapabilitiesURL() != null)       store.put("capabilitiesURL",      req.getCapabilitiesURL());
        if (req.getUser() != null)                  store.put("user",                 req.getUser());
        if (req.getPassword() != null)              store.put("password",             req.getPassword());
        if (req.getAuthKey() != null)               store.put("authKey",              req.getAuthKey());
        if (req.getHeaderName() != null)            store.put("headerName",           req.getHeaderName());
        if (req.getHeaderValue() != null)           store.put("headerValue",          req.getHeaderValue());
        if (req.getMaxConnections() != null)        store.put("maxConnections",       req.getMaxConnections());
        if (req.getReadTimeout() != null)           store.put("readTimeout",          req.getReadTimeout());
        if (req.getConnectTimeout() != null)        store.put("connectTimeout",       req.getConnectTimeout());
        if (req.getDisableOnConnFailure() != null)  store.put("disableOnConnFailure", req.getDisableOnConnFailure());
        return serializeToJson(Collections.singletonMap("wmsStore", store));
    }


}
