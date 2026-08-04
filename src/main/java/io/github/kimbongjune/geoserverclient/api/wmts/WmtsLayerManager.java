package io.github.kimbongjune.geoserverclient.api.wmts;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.PublishWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.UpdateWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayerSummary;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.WmtsLayerNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * GeoServer Cascading WMTS Layer REST API manager.
 *
 * <p>A WMTS layer proxies a tile layer from an external WMTS server (or GeoWebCache) registered
 * in a WMTS store and publishes it as a GeoServer layer.
 * On publish, GeoServer calls CatalogBuilder.initWMTSLayer() automatically to set bbox and CRS.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET  /rest/workspaces/{ws}/wmtsstores/{store}/layers
 * GET  /rest/workspaces/{ws}/wmtslayers
 * POST /rest/workspaces/{ws}/wmtsstores/{store}/layers
 * POST /rest/workspaces/{ws}/wmtslayers
 * GET  /rest/workspaces/{ws}/wmtsstores/{store}/layers/{layer}
 * GET  /rest/workspaces/{ws}/wmtslayers/{layer}
 * PUT  /rest/workspaces/{ws}/wmtsstores/{store}/layers/{layer}
 * DELETE /rest/workspaces/{ws}/wmtsstores/{store}/layers/{layer}
 * DELETE /rest/workspaces/{ws}/wmtslayers/{layer}
 * </pre>
 *
 * <p>{@code POST /workspaces/{ws}/wmtslayers} (no store in the URL) requires the target store
 * to be named explicitly in the request body — see {@link #publishByWorkspace}. Verified against
 * GeoServer 2.28.2.</p>
 */
public class WmtsLayerManager extends AbstractManager {

    public WmtsLayerManager(GeoServerHttpClient httpClient,
                             SerializerFactory serializerFactory,
                             DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // list

    /**
     * Lists published (configured) WMTS layers in a WMTS store.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @return list of published WMTS layer summaries (empty list if none)
     */
    public List<WmtsLayerSummary> list(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                + "/layers.json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Lists all WMTS layers in a workspace (regardless of store).
     *
     * @param workspaceName workspace name (required)
     * @return list of WMTS layer summaries (empty list if none)
     */
    public List<WmtsLayerSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtslayers.json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Lists layer names available from the remote WMTS server that have not yet been published.
     *
     * <p>Issues a real GetCapabilities HTTP request to the remote WMTS.
     * If the capabilitiesURL is unreachable, GeoServer returns 500.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @return list of remote WMTS layer names not yet published (empty list if none)
     */
    public List<String> listAvailable(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                + "/layers?list=available";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAvailableList(response.getBody());
    }

    // publish

    /**
     * Publishes a remote WMTS layer from a WMTS store.
     *
     * <p>On publish, GeoServer calls CatalogBuilder.initWMTSLayer() automatically to set
     * bbox and CRS. A GeoServer LayerInfo is also created automatically.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param request       publish parameters (name + nativeName required)
     * @return the published WMTS layer details
     * @throws ResourceAlreadyExistsException
     *         if a WMTS layer with the same name already exists
     */
    public WmtsLayer publish(String workspaceName, String storeName,
                             PublishWmtsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        if (exists(workspaceName, storeName, request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "WmtsLayer",
                    workspaceName + "/" + storeName + "/" + request.getName(),
                    null);
        }

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName + "/layers";
        String body = buildPublishPayload(request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
        return get(workspaceName, storeName, request.getName());
    }

    /**
     * Publishes a remote WMTS layer without a store segment in the URL. The target store must be
     * named explicitly in the request body (via {@code storeName}), since GeoServer does not
     * auto-resolve it the way it does for FeatureTypes. Verified against GeoServer 2.28.2.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store the new layer belongs to (required)
     * @param request       publish parameters (name + nativeName required)
     * @return the published WMTS layer details
     * @throws ResourceAlreadyExistsException if a WMTS layer with the same name already exists
     */
    public WmtsLayer publishByWorkspace(String workspaceName, String storeName,
                                        PublishWmtsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        if (exists(workspaceName, storeName, request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "WmtsLayer",
                    workspaceName + "/" + storeName + "/" + request.getName(),
                    null);
        }

        String path = "/rest/workspaces/" + workspaceName + "/wmtslayers";
        String body = buildPublishByWorkspacePayload(workspaceName, storeName, request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
        return get(workspaceName, storeName, request.getName());
    }

    // get

    /**
     * Gets a WMTS layer by store and layer name.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param layerName     WMTS layer name (required)
     * @return WMTS layer details
     * @throws WmtsLayerNotFoundException if the layer does not exist
     */
    public WmtsLayer get(String workspaceName, String storeName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                + "/layers/" + layerName + ".json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmtsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    /**
     * Gets a WMTS layer by workspace and layer name (no store required).
     *
     * @param workspaceName workspace name (required)
     * @param layerName     WMTS layer name (required)
     * @return WMTS layer details
     * @throws WmtsLayerNotFoundException if the layer does not exist
     */
    public WmtsLayer get(String workspaceName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtslayers/" + layerName + ".json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmtsLayerNotFoundException(workspaceName, null, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    // exists

    /**
     * Returns whether a WMTS layer exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param layerName     WMTS layer name (required)
     * @return true if the layer exists
     */
    public boolean exists(String workspaceName, String storeName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                        + "/layers/" + layerName,
                "application/json");
        return response.isSuccessful();
    }

    // update

    /**
     * Updates a WMTS layer. Unlike WmsLayerManager, PUT works correctly here (no 500 bug).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param layerName     name of the WMTS layer to update (required)
     * @param request       update parameters (required; specify at least one field)
     * @return the updated WMTS layer details
     * @throws WmtsLayerNotFoundException if the layer does not exist
     */
    public WmtsLayer update(String workspaceName, String storeName, String layerName,
                            UpdateWmtsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");

        StringBuilder sb = new StringBuilder("/rest/workspaces/").append(workspaceName)
                .append("/wmtsstores/").append(storeName)
                .append("/layers/").append(layerName);
        if (request.getCalculate() != null && !request.getCalculate().isEmpty()) {
            sb.append("?calculate=").append(request.getCalculate());
        }
        String path = sb.toString();

        String body = buildUpdatePayload(request);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new WmtsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return get(workspaceName, storeName, layerName);
    }

    // delete

    /**
     * Deletes a WMTS layer. Uses recurse=false.
     *
     * <p>Layers published via POST always have an associated LayerInfo.
     * With recurse=false, returns 403 if a LayerInfo exists.
     * Use {@code delete(ws, store, layer, true)} to cascade-delete the LayerInfo as well.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param layerName     WMTS layer name to delete (required)
     * @throws WmtsLayerNotFoundException if the layer does not exist (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.AuthenticationException if a LayerInfo reference exists (403)
     */
    public void delete(String workspaceName, String storeName, String layerName) {
        delete(workspaceName, storeName, layerName, false);
    }

    /**
     * Deletes a WMTS layer.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMTS store name (required)
     * @param layerName     WMTS layer name to delete (required)
     * @param recurse       when true, cascade-deletes associated LayerInfo;
     *                      when false, returns 403 if a LayerInfo reference exists
     * @throws WmtsLayerNotFoundException if the layer does not exist (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.AuthenticationException
     *         when recurse=false and a LayerInfo reference exists (403)
     */
    public void delete(String workspaceName, String storeName, String layerName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmtsstores/" + storeName
                + "/layers/" + layerName + "?recurse=" + recurse;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new WmtsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<WmtsLayerSummary> parseList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("wmtsLayers").path("wmtsLayer");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, WmtsLayerSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMTS layer list response", e);
        }
    }

    private List<String> parseAvailableList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("list").path("string");
            if (items.isMissingNode() || items.isNull()
                    || (!items.isContainerNode() && items.asText().isEmpty())) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<String> result = new ArrayList<>();
                items.forEach(n -> result.add(n.asText()));
                return result;
            }
            return Collections.singletonList(items.asText());
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMTS layer available list response", e);
        }
    }

    private WmtsLayer parseLayer(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            WmtsLayerEnvelope envelope = om.readValue(body, WmtsLayerEnvelope.class);
            return envelope.wmtsLayer;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMTS layer response", e);
        }
    }

    // Payload builders

    private Map<String, Object> buildPublishFields(PublishWmtsLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();
        layer.put("name", req.getName());
        layer.put("nativeName", req.getNativeName());
        if (req.getTitle() != null)            layer.put("title",            req.getTitle());
        if (req.getDescription() != null)      layer.put("description",      req.getDescription());
        if (req.getAbstractText() != null)     layer.put("abstract",         req.getAbstractText());
        if (req.getEnabled() != null)          layer.put("enabled",          req.getEnabled());
        if (req.getAdvertised() != null)       layer.put("advertised",       req.getAdvertised());
        if (req.getSrs() != null)              layer.put("srs",              req.getSrs());
        if (req.getProjectionPolicy() != null) layer.put("projectionPolicy", req.getProjectionPolicy());
        return layer;
    }

    private String buildPublishPayload(PublishWmtsLayerRequest req) {
        return serializeToJson(Collections.singletonMap("wmtsLayer", buildPublishFields(req)));
    }

    private String buildPublishByWorkspacePayload(String workspaceName, String storeName, PublishWmtsLayerRequest req) {
        Map<String, Object> layer = buildPublishFields(req);

        Map<String, Object> store = new LinkedHashMap<>();
        store.put("@class", "wmtsStore");
        store.put("name", workspaceName + ":" + storeName);
        layer.put("store", store);

        return serializeToJson(Collections.singletonMap("wmtsLayer", layer));
    }

    private String buildUpdatePayload(UpdateWmtsLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();
        if (req.getTitle() != null)            layer.put("title",            req.getTitle());
        if (req.getDescription() != null)      layer.put("description",      req.getDescription());
        if (req.getAbstractText() != null)     layer.put("abstract",         req.getAbstractText());
        if (req.getEnabled() != null)          layer.put("enabled",          req.getEnabled());
        if (req.getAdvertised() != null)       layer.put("advertised",       req.getAdvertised());
        if (req.getSrs() != null)              layer.put("srs",              req.getSrs());
        if (req.getProjectionPolicy() != null) layer.put("projectionPolicy", req.getProjectionPolicy());
        return serializeToJson(Collections.singletonMap("wmtsLayer", layer));
    }



    // Envelope (JSON unwrapping)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WmtsLayerEnvelope {
        @JsonProperty("wmtsLayer")
        WmtsLayer wmtsLayer;
        public WmtsLayerEnvelope() {}
    }
}
