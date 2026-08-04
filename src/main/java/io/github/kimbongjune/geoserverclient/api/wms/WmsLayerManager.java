package io.github.kimbongjune.geoserverclient.api.wms;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.UpdateWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayerSummary;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.WmsLayerNotFoundException;
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
 * GeoServer Cascading WMS Layer REST API client.
 *
 * <p>Verified against: GeoServer 2.28.2 / 2026-04-02 (Kartoza Docker kartoza/geoserver)
 *
 * <p>Source: {@code gs-rest/src/main/java/org/geoserver/rest/catalog/WMSLayerController.java}
 * <br>{@code @RequestMapping(path = RestBaseController.ROOT_PATH)}
 * <br>GeoServer REST base path: {@code /geoserver/rest}
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET  /rest/workspaces/{ws}/wmsstores/{store}/wmslayers
 * GET  /rest/workspaces/{ws}/wmslayers
 * POST /rest/workspaces/{ws}/wmsstores/{store}/wmslayers
 * POST /rest/workspaces/{ws}/wmslayers
 * GET  /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}
 * GET  /rest/workspaces/{ws}/wmslayers/{layer}
 * PUT  /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}
 * DELETE /rest/workspaces/{ws}/wmsstores/{store}/wmslayers/{layer}
 * DELETE /rest/workspaces/{ws}/wmslayers/{layer}
 * </pre>
 *
 * <p>{@code POST /workspaces/{ws}/wmslayers} (no store in the URL) requires the target store
 * to be named explicitly in the request body — see {@link #publishByWorkspace}. Verified against
 * GeoServer 2.28.2.</p>
 */
public class WmsLayerManager extends AbstractManager {

    public WmsLayerManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // list

    /**
     * Lists published (configured) WMS layers in a WMS store.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @return list of published WMS layer summaries (empty list if none)
     */
    public List<WmsLayerSummary> list(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                + "/wmslayers.json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Lists all WMS layers in a workspace (regardless of store).
     *
     * @param workspaceName workspace name (required)
     * @return list of WMS layer summaries (empty list if none)
     */
    public List<WmsLayerSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");

        String path = "/rest/workspaces/" + workspaceName + "/wmslayers.json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Lists layer names available from the remote WMS server that have not yet been published.
     *
     * <p>Issues a real GetCapabilities HTTP request to the remote WMS.
     * If the capabilitiesURL is unreachable, GeoServer returns 500.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @return list of remote WMS layer names not yet published (empty list if none)
     */
    public List<String> listAvailable(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                + "/wmslayers?list=available";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAvailableList(response.getBody());
    }

    // publish

    /**
     * Publishes a remote WMS layer from a WMS store.
     *
     * <p>On publish, GeoServer calls CatalogBuilder.initWMSLayer() automatically to set
     * bbox and CRS. A GeoServer LayerInfo is also created automatically.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param request       publish parameters (name + nativeName required)
     * @return the published WMS layer details
     * @throws ResourceAlreadyExistsException
     *         if a WMS layer with the same name already exists
     */
    public WmsLayer publish(String workspaceName, String storeName, PublishWmsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        if (exists(workspaceName, storeName, request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "WmsLayer",
                    workspaceName + "/" + storeName + "/" + request.getName(),
                    null);
        }

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName + "/wmslayers";
        String body = buildPublishPayload(request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
        return get(workspaceName, storeName, request.getName());
    }

    /**
     * Publishes a remote WMS layer without a store segment in the URL. The target store must be
     * named explicitly in the request body (via {@code storeName}), since GeoServer does not
     * auto-resolve it the way it does for FeatureTypes. Verified against GeoServer 2.28.2.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store the new layer belongs to (required)
     * @param request       publish parameters (name + nativeName required)
     * @return the published WMS layer details
     * @throws ResourceAlreadyExistsException if a WMS layer with the same name already exists
     */
    public WmsLayer publishByWorkspace(String workspaceName, String storeName, PublishWmsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        if (exists(workspaceName, storeName, request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "WmsLayer",
                    workspaceName + "/" + storeName + "/" + request.getName(),
                    null);
        }

        String path = "/rest/workspaces/" + workspaceName + "/wmslayers";
        String body = buildPublishByWorkspacePayload(workspaceName, storeName, request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
        return get(workspaceName, storeName, request.getName());
    }

    // get

    /**
     * Gets a WMS layer by store and layer name.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param layerName     WMS layer name (required)
     * @return WMS layer details
     * @throws WmsLayerNotFoundException if the layer does not exist
     */
    public WmsLayer get(String workspaceName, String storeName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                + "/wmslayers/" + layerName + ".json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    /**
     * Gets a WMS layer by workspace and layer name (no store required).
     *
     * @param workspaceName workspace name (required)
     * @param layerName     WMS layer name (required)
     * @return WMS layer details
     * @throws WmsLayerNotFoundException if the layer does not exist
     */
    public WmsLayer get(String workspaceName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmslayers/" + layerName + ".json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new WmsLayerNotFoundException(workspaceName, null, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    // exists

    /**
     * Returns whether a WMS layer exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param layerName     WMS layer name (required)
     * @return true if the layer exists
     */
    public boolean exists(String workspaceName, String storeName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                        + "/wmslayers/" + layerName,
                "application/json");
        return response.isSuccessful();
    }

    // update

    /**
     * Updates a WMS layer.
     *
     * <p><b>GeoServer 2.28.2 BUG: PUT always returns 500.</b>
     * Every PUT call to GeoServer 2.28.2 returns 500 due to an internal bug.
     * Calling this method will throw
     * {@link io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException}(500).</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param layerName     name of the WMS layer to update (required)
     * @param request       update parameters (required; specify at least one field)
     * @return the updated WMS layer (unreachable due to GeoServer bug)
     * @throws WmsLayerNotFoundException if the layer does not exist
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *         always thrown with 500 on GeoServer 2.28.2
     * @deprecated always throws on GeoServer 2.28.2 (server-side XStream persister bug, not a
     *             client defect — see class Javadoc and {@code docs/GEOSERVER_BUG_WMS_LAYER_PUT.md}).
     *             Kept for API completeness and to document the failure via
     *             {@code WmsLayerManagerIntegrationTest#update_alwaysThrows500()}; do not call this
     *             in new code expecting it to succeed. No workaround exists client-side.
     */
    @Deprecated
    public WmsLayer update(String workspaceName, String storeName, String layerName,
                           UpdateWmsLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");

        StringBuilder sb = new StringBuilder("/rest/workspaces/").append(workspaceName)
                .append("/wmsstores/").append(storeName)
                .append("/wmslayers/").append(layerName);
        if (request.getCalculate() != null && !request.getCalculate().isEmpty()) {
            sb.append("?calculate=").append(request.getCalculate());
        }
        String path = sb.toString();

        String body = buildUpdatePayload(request);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new WmsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return get(workspaceName, storeName, layerName);
    }

    // delete

    /**
     * Deletes a WMS layer. Uses recurse=false.
     *
     * <p>Returns 403 Forbidden if a GeoServer LayerInfo still references the WMS layer
     * ({@link io.github.kimbongjune.geoserverclient.exception.AuthenticationException}).
     * Layers published via POST always have an associated LayerInfo, so use
     * {@code delete(ws, store, layer, true)} or delete the LayerInfo separately first.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param layerName     WMS layer name to delete (required)
     * @throws WmsLayerNotFoundException if the layer does not exist (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.AuthenticationException if a LayerInfo reference exists (403)
     */
    public void delete(String workspaceName, String storeName, String layerName) {
        delete(workspaceName, storeName, layerName, false);
    }

    /**
     * Deletes a WMS layer.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     WMS store name (required)
     * @param layerName     WMS layer name to delete (required)
     * @param recurse       when true, cascade-deletes associated LayerInfo;
     *                      when false, returns 403 if a LayerInfo reference exists
     * @throws WmsLayerNotFoundException if the layer does not exist (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.AuthenticationException
     *         when recurse=false and a LayerInfo reference exists (403)
     */
    public void delete(String workspaceName, String storeName, String layerName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/wmsstores/" + storeName
                + "/wmslayers/" + layerName + "?recurse=" + recurse;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new WmsLayerNotFoundException(workspaceName, storeName, layerName,
                    response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<WmsLayerSummary> parseList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("wmsLayers").path("wmsLayer");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, WmsLayerSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMS layer list response", e);
        }
    }

    private List<String> parseAvailableList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("list").path("string");
            if (items.isMissingNode() || items.isNull() || (!items.isContainerNode() && items.asText().isEmpty())) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<String> result = new ArrayList<>();
                items.forEach(n -> result.add(n.asText()));
                return result;
            }
            // single string node
            return Collections.singletonList(items.asText());
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMS layer available list response", e);
        }
    }

    private WmsLayer parseLayer(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            WmsLayerEnvelope envelope = om.readValue(body, WmsLayerEnvelope.class);
            return envelope.wmsLayer;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse WMS layer response", e);
        }
    }

    // Payload builders

    private String buildPublishPayload(PublishWmsLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();
        layer.put("name", req.getName());
        layer.put("nativeName", req.getNativeName());
        if (req.getTitle() != null)                   layer.put("title",                req.getTitle());
        if (req.getDescription() != null)             layer.put("description",          req.getDescription());
        if (req.getAbstractText() != null)            layer.put("abstract",             req.getAbstractText());
        if (req.getEnabled() != null)                 layer.put("enabled",              req.getEnabled());
        if (req.getForcedRemoteStyle() != null)       layer.put("forcedRemoteStyle",    req.getForcedRemoteStyle());
        if (req.getPreferredFormat() != null)         layer.put("preferredFormat",      req.getPreferredFormat());
        if (req.getMinScale() != null)                layer.put("minScale",             req.getMinScale());
        if (req.getMaxScale() != null)                layer.put("maxScale",             req.getMaxScale());
        if (req.getMetadataBBoxRespected() != null)   layer.put("metadataBBoxRespected", req.getMetadataBBoxRespected());
        if (req.getSelectedRemoteStyles() != null)    layer.put("selectedRemoteStyles", req.getSelectedRemoteStyles());
        if (req.getVendorParameters() != null)        layer.put("vendorParameters",     req.getVendorParameters());
        if (req.getSrs() != null)                     layer.put("srs",                  req.getSrs());
        if (req.getProjectionPolicy() != null)        layer.put("projectionPolicy",     req.getProjectionPolicy());
        return serializeToJson(Collections.singletonMap("wmsLayer", layer));
    }

    private String buildPublishByWorkspacePayload(String workspaceName, String storeName, PublishWmsLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();
        layer.put("name", req.getName());
        layer.put("nativeName", req.getNativeName());
        if (req.getTitle() != null)                   layer.put("title",                req.getTitle());
        if (req.getDescription() != null)             layer.put("description",          req.getDescription());
        if (req.getAbstractText() != null)            layer.put("abstract",             req.getAbstractText());
        if (req.getEnabled() != null)                 layer.put("enabled",              req.getEnabled());
        if (req.getForcedRemoteStyle() != null)       layer.put("forcedRemoteStyle",    req.getForcedRemoteStyle());
        if (req.getPreferredFormat() != null)         layer.put("preferredFormat",      req.getPreferredFormat());
        if (req.getMinScale() != null)                layer.put("minScale",             req.getMinScale());
        if (req.getMaxScale() != null)                layer.put("maxScale",             req.getMaxScale());
        if (req.getMetadataBBoxRespected() != null)   layer.put("metadataBBoxRespected", req.getMetadataBBoxRespected());
        if (req.getSelectedRemoteStyles() != null)    layer.put("selectedRemoteStyles", req.getSelectedRemoteStyles());
        if (req.getVendorParameters() != null)        layer.put("vendorParameters",     req.getVendorParameters());
        if (req.getSrs() != null)                     layer.put("srs",                  req.getSrs());
        if (req.getProjectionPolicy() != null)        layer.put("projectionPolicy",     req.getProjectionPolicy());

        Map<String, Object> store = new LinkedHashMap<>();
        store.put("@class", "wmsStore");
        store.put("name", workspaceName + ":" + storeName);
        layer.put("store", store);

        return serializeToJson(Collections.singletonMap("wmsLayer", layer));
    }

    private String buildUpdatePayload(UpdateWmsLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();
        if (req.getTitle() != null)                   layer.put("title",                req.getTitle());
        if (req.getDescription() != null)             layer.put("description",          req.getDescription());
        if (req.getAbstractText() != null)            layer.put("abstract",             req.getAbstractText());
        if (req.getEnabled() != null)                 layer.put("enabled",              req.getEnabled());
        if (req.getForcedRemoteStyle() != null)       layer.put("forcedRemoteStyle",    req.getForcedRemoteStyle());
        if (req.getPreferredFormat() != null)         layer.put("preferredFormat",      req.getPreferredFormat());
        if (req.getMinScale() != null)                layer.put("minScale",             req.getMinScale());
        if (req.getMaxScale() != null)                layer.put("maxScale",             req.getMaxScale());
        if (req.getMetadataBBoxRespected() != null)   layer.put("metadataBBoxRespected", req.getMetadataBBoxRespected());
        if (req.getVendorParameters() != null)        layer.put("vendorParameters",     req.getVendorParameters());
        return serializeToJson(Collections.singletonMap("wmsLayer", layer));
    }



    // Envelope (JSON unwrapping)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WmsLayerEnvelope {
        @JsonProperty("wmsLayer")
        WmsLayer wmsLayer;
        public WmsLayerEnvelope() {}
    }
}
