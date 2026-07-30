package io.github.kimbongjune.geoserverclient.api.layer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.exception.LayerNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
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
 * GeoServer Layer REST API manager.
 *
 * <p>A Layer is the logical serving unit exposed to clients by GeoServer. Each Layer references
 * exactly one resource (FeatureType, Coverage, WMSLayer, etc.) and carries rendering metadata such
 * as defaultStyle, additional styles, attribution, and legend. Layers are created automatically
 * when a Coverage or FeatureType is POSTed, so there is no POST endpoint to create a Layer directly.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/layers/{layerName}
 * PUT     /rest/layers/{layerName}
 * DELETE  /rest/layers/{layerName}
 * </pre>
 */
public class LayerManager extends AbstractManager {

    public LayerManager(GeoServerHttpClient httpClient,
                        SerializerFactory serializerFactory,
                        DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] list

    /**
     * List all layers globally (entire server).
     *
     * @return all layer summaries (name in "ws:layerName" format); empty list if none
     */
    public List<LayerSummary> list() {
        GeoServerResponse response =
                httpClient.get("/rest/layers.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/layers");
        return parseList(response.getBody());
    }

    /**
     * List layers in a specific workspace.
     *
     * @param workspaceName workspace name (required)
     * @return layer summaries for the workspace (name without workspace prefix); empty list if none
     */
    public List<LayerSummary> listByWorkspace(
            String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");

        String path = "/rest/workspaces/" + workspaceName + "/layers.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    // [2] get

    /**
     * Get a layer by name (global path).
     *
     * @param layerName layer name; "ws:name" format or simple name both accepted
     * @return layer details
     * @throws LayerNotFoundException if the layer does not exist
     */
    public Layer get(String layerName) {
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/layers/" + layerName + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new LayerNotFoundException(
                    layerName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    /**
     * Get a layer by workspace and name (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param layerName     layer name (required)
     * @return layer details
     * @throws LayerNotFoundException if the layer does not exist
     */
    public Layer getByWorkspace(
            String workspaceName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/layers/" + layerName + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new LayerNotFoundException(
                    workspaceName + ":" + layerName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    // [3] exists

    /**
     * Returns true if the layer exists (global path).
     *
     * @param layerName layer name ("ws:name" or simple name)
     */
    public boolean exists(String layerName) {
        requireNonEmpty(layerName, "layerName");

        GeoServerResponse response =
                httpClient.get("/rest/layers/" + layerName, "application/json");
        return response.isSuccessful();
    }

    /**
     * Returns true if the layer exists (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param layerName     layer name (required)
     */
    public boolean existsByWorkspace(String workspaceName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");

        GeoServerResponse response =
                httpClient.get("/rest/workspaces/" + workspaceName + "/layers/" + layerName,
                        "application/json");
        return response.isSuccessful();
    }

    // [4] update

    /**
     * Update layer attributes (global path). Partial update supported.
     * Note: authorityURLs and identifiers are full-replace — omitting them removes existing values.
     *
     * @param layerName layer name ("ws:name" or simple name)
     * @param request   update request DTO
     * @return the updated layer
     * @throws LayerNotFoundException if the layer does not exist
     */
    public Layer update(
            String layerName,
            UpdateLayerRequest request) {
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");

        String path = "/rest/layers/" + layerName;
        String body = buildUpdatePayload(request);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new LayerNotFoundException(
                    layerName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return get(layerName);
    }

    /**
     * Update layer attributes (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param layerName     layer name (required)
     * @param request       update request DTO
     * @return the updated layer
     * @throws LayerNotFoundException if the layer does not exist
     */
    public Layer updateByWorkspace(
            String workspaceName,
            String layerName,
            UpdateLayerRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/layers/" + layerName;
        String body = buildUpdatePayload(request);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new LayerNotFoundException(
                    workspaceName + ":" + layerName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return getByWorkspace(workspaceName, layerName);
    }

    // [5] delete

    /**
     * Delete a layer entry (global path). Only the Layer catalog entry is removed;
     * the referenced Coverage/FeatureType is retained. To delete the resource too,
     * use FeatureTypeManager or CoverageStoreManager with recurse=true.
     *
     * @param layerName layer name ("ws:name" or simple name)
     * @throws LayerNotFoundException if the layer does not exist
     */
    public void delete(String layerName) {
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/layers/" + layerName;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new LayerNotFoundException(layerName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Delete a layer entry (workspace path). Only the Layer catalog entry is removed;
     * the referenced resource is retained.
     *
     * @param workspaceName workspace name (required)
     * @param layerName     layer name (required)
     * @throws LayerNotFoundException if the layer does not exist
     */
    public void deleteByWorkspace(String workspaceName, String layerName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(layerName, "layerName");

        String path = "/rest/workspaces/" + workspaceName + "/layers/" + layerName;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new LayerNotFoundException(workspaceName + ":" + layerName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // [6] Payload builders

    private String buildUpdatePayload(
            UpdateLayerRequest req) {
        Map<String, Object> layer = new LinkedHashMap<>();

        // defaultStyle
        if (req.getDefaultStyleName() != null) {
            Map<String, Object> styleRef = new LinkedHashMap<>();
            styleRef.put("name", req.getDefaultStyleName());
            if (req.getDefaultStyleWorkspace() != null) {
                styleRef.put("workspace", req.getDefaultStyleWorkspace());
            }
            layer.put("defaultStyle", styleRef);
        }

        // styles (additional style list)
        if (req.getStyleNames() != null) {
            List<Map<String, Object>> styleList = new ArrayList<>();
            for (String styleName : req.getStyleNames()) {
                Map<String, Object> s = new LinkedHashMap<>();
                s.put("name", styleName);
                styleList.add(s);
            }
            Map<String, Object> styles = new LinkedHashMap<>();
            styles.put("@class", "linked-hash-set");
            styles.put("style", styleList);
            layer.put("styles", styles);
        }

        if (req.getQueryable() != null)  layer.put("queryable", req.getQueryable());
        if (req.getOpaque() != null)     layer.put("opaque",    req.getOpaque());
        if (req.getEnabled() != null)    layer.put("enabled",   req.getEnabled());
        if (req.getAdvertised() != null) layer.put("advertised", req.getAdvertised());
        if (req.getPath() != null)       layer.put("path",      req.getPath());

        // attribution
        if (req.getAttributionTitle() != null || req.getAttributionHref() != null) {
            Map<String, Object> attr = new LinkedHashMap<>();
            if (req.getAttributionTitle() != null) attr.put("title", req.getAttributionTitle());
            if (req.getAttributionHref()  != null) attr.put("href",  req.getAttributionHref());
            attr.put("logoWidth",  0);
            attr.put("logoHeight", 0);
            layer.put("attribution", attr);
        }

        if (req.getDefaultWMSInterpolationMethod() != null) {
            layer.put("defaultWMSInterpolationMethod", req.getDefaultWMSInterpolationMethod());
        }

        return serializeToJson(Collections.singletonMap("layer", layer));
    }

    // [7] Parsing helpers

    private List<LayerSummary> parseList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items =
                    root.path("layers").path("layer");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class,
                            LayerSummary.class));
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse layer list response", e);
        }
    }

    private Layer parseLayer(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            LayerEnvelope envelope = om.readValue(body, LayerEnvelope.class);
            return envelope.layer;
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse layer response", e);
        }
    }


    // [8] Envelope (JSON unwrapping)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LayerEnvelope {
        @JsonProperty("layer")
        public Layer layer;

        public LayerEnvelope() {}
    }
}
