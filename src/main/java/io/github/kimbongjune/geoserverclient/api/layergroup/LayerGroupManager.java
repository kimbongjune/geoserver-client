package io.github.kimbongjune.geoserverclient.api.layergroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import io.github.kimbongjune.geoserverclient.dto.layergroup.CreateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroup;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroupSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.UpdateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.exception.LayerGroupNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
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
 * GeoServer LayerGroup REST API manager.
 *
 * <p>A LayerGroup bundles multiple Layers (or other LayerGroups) into a single group
 * that appears as one layer in WMS GetCapabilities. Each publishable entry has a
 * corresponding style entry (parallel arrays), and the group carries metadata such as
 * bounds, mode, keywords, and metadata entries.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/layergroups/{layerGroupName}
 * PUT     /rest/layergroups/{layerGroupName}
 * DELETE  /rest/layergroups/{layerGroupName}
 * </pre>
 */
/**
 * GeoServer LayerGroup REST API manager implementation.
 *
 * <p>Based on Javadoc specification and direct verification against GeoServer 2.28.2.</p>
 *
 * <p>Key behaviors:
 * <ul>
 *   <li>{@link #list()} returns only NO_WORKSPACE groups (workspace-scoped groups not included).</li>
 *   <li>{@link #delete(String)} / {@link #deleteByWorkspace(String, String)} — deleting a non-existent
 *       group returns 500 (GeoServer NPE bug). This throws {@code GeoServerResponseException}.</li>
 *   <li>{@code abstract} key: used in POST/PUT request payloads. GET responses return it as {@code abstractTxt}.</li>
 * </ul>
 */
public class LayerGroupManager extends AbstractManager {

    public LayerGroupManager(GeoServerHttpClient httpClient,
                             SerializerFactory serializerFactory,
                             DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] list

    /**
     * List global LayerGroups (NO_WORKSPACE groups only).
     *
     * <p>Workspace-scoped groups are not included.</p>
     *
     * @return NO_WORKSPACE LayerGroup summary list; empty list if none
     */
    public List<LayerGroupSummary> list() {
        GeoServerResponse response =
                httpClient.get("/rest/layergroups.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/layergroups");
        return parseList(response.getBody());
    }

    /**
     * List LayerGroups in a specific workspace.
     *
     * @param workspaceName workspace name (required); returns 404 if workspace does not exist
     * @return LayerGroup summary list for the workspace; empty list if none
     */
    public List<LayerGroupSummary> listByWorkspace(
            String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/layergroups.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    // [2] get

    /**
     * Get a LayerGroup by name (global path).
     *
     * @param groupName LayerGroup name
     * @return LayerGroup details
     * @throws LayerGroupNotFoundException if the LayerGroup does not exist (404)
     */
    public LayerGroup get(String groupName) {
        requireNonEmpty(groupName, "groupName");
        String path = "/rest/layergroups/" + groupName + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new LayerGroupNotFoundException(
                    groupName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayerGroup(response.getBody());
    }

    /**
     * Get a LayerGroup by workspace and name (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param groupName     LayerGroup name (required)
     * @return LayerGroup details
     * @throws LayerGroupNotFoundException if the LayerGroup does not exist (404)
     */
    public LayerGroup getByWorkspace(
            String workspaceName, String groupName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(groupName, "groupName");
        String path = "/rest/workspaces/" + workspaceName + "/layergroups/" + groupName + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new LayerGroupNotFoundException(
                    workspaceName + ":" + groupName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseLayerGroup(response.getBody());
    }

    // [3] exists

    /**
     * Returns true if the LayerGroup exists (global path).
     *
     * @param groupName LayerGroup name
     */
    public boolean exists(String groupName) {
        requireNonEmpty(groupName, "groupName");
        GeoServerResponse response =
                httpClient.get("/rest/layergroups/" + groupName, "application/json");
        return response.isSuccessful();
    }

    /**
     * Returns true if the LayerGroup exists (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param groupName     LayerGroup name (required)
     */
    public boolean existsByWorkspace(String workspaceName, String groupName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(groupName, "groupName");
        GeoServerResponse response =
                httpClient.get("/rest/workspaces/" + workspaceName
                        + "/layergroups/" + groupName, "application/json");
        return response.isSuccessful();
    }

    // [4] create

    /**
     * Create a global LayerGroup.
     *
     * @param request creation request DTO (name + publishables required)
     * @throws ResourceAlreadyExistsException on name conflict (409)
     */
    public void create(CreateLayerGroupRequest request) {
        requireNonNull(request, "request");
        String body = buildCreatePayload(request);
        GeoServerResponse response =
                httpClient.post("/rest/layergroups", body, "application/json", "application/json");
        if (response.getStatusCode() == 409) {
            throw new ResourceAlreadyExistsException("LayerGroup", request.getName(), null);
        }
        handleErrorResponse(response, "POST", "/rest/layergroups");
    }

    /**
     * Create a workspace-scoped LayerGroup.
     *
     * @param workspaceName workspace name (required)
     * @param request       creation request DTO (name + publishables required)
     * @throws ResourceAlreadyExistsException on name conflict (409)
     */
    public void createByWorkspace(String workspaceName,
            CreateLayerGroupRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");
        String path = "/rest/workspaces/" + workspaceName + "/layergroups";
        String body = buildCreatePayload(request);
        GeoServerResponse response =
                httpClient.post(path, body, "application/json", "application/json");
        if (response.getStatusCode() == 409) {
            throw new ResourceAlreadyExistsException("LayerGroup",
                    workspaceName + ":" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);
    }

    // [5] update

    /**
     * Update a LayerGroup (global path).
     *
     * @param groupName LayerGroup name (required)
     * @param request   update request DTO
     * @return updated LayerGroup details
     * @throws LayerGroupNotFoundException if the LayerGroup does not exist (404)
     */
    public LayerGroup update(
            String groupName,
            UpdateLayerGroupRequest request) {
        requireNonEmpty(groupName, "groupName");
        requireNonNull(request, "request");
        String path = "/rest/layergroups/" + groupName;
        String body = buildUpdatePayload(request);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new LayerGroupNotFoundException(
                    groupName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return get(groupName);
    }

    /**
     * Update a LayerGroup (workspace path).
     *
     * @param workspaceName workspace name (required)
     * @param groupName     LayerGroup name (required)
     * @param request       update request DTO
     * @return updated LayerGroup details
     * @throws LayerGroupNotFoundException if the LayerGroup does not exist (404)
     */
    public LayerGroup updateByWorkspace(
            String workspaceName,
            String groupName,
            UpdateLayerGroupRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(groupName, "groupName");
        requireNonNull(request, "request");
        String path = "/rest/workspaces/" + workspaceName + "/layergroups/" + groupName;
        String body = buildUpdatePayload(request);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new LayerGroupNotFoundException(
                    workspaceName + ":" + groupName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return getByWorkspace(workspaceName, groupName);
    }

    // [6] delete

    /**
     * Delete a LayerGroup (global path).
     *
     * <p>Warning: deleting a non-existent group returns 500 (GeoServer NPE bug) ->
     * throws {@code GeoServerResponseException(500,...)}.</p>
     *
     * @param groupName LayerGroup name (required)
     */
    public void delete(String groupName) {
        requireNonEmpty(groupName, "groupName");
        String path = "/rest/layergroups/" + groupName;
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Delete a LayerGroup (workspace path).
     *
     * <p>Warning: deleting a non-existent group returns 500 (GeoServer NPE bug).</p>
     *
     * @param workspaceName workspace name (required)
     * @param groupName     LayerGroup name (required)
     */
    public void deleteByWorkspace(String workspaceName, String groupName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(groupName, "groupName");
        String path = "/rest/workspaces/" + workspaceName + "/layergroups/" + groupName;
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // [7] Payload builders

    private String buildCreatePayload(
            CreateLayerGroupRequest req) {
        Map<String, Object> lg = new LinkedHashMap<String, Object>();
        lg.put("name", req.getName());
        lg.put("publishables", buildPublishablesMap(req.getPublishables()));
        if (req.getStyles() != null)               lg.put("styles", buildStylesMap(req.getStyles()));
        if (req.getMode() != null)                 lg.put("mode", req.getMode());
        if (req.getTitle() != null)                lg.put("title", req.getTitle());
        if (req.getAbstractText() != null)         lg.put("abstractTxt", req.getAbstractText());
        if (req.getInternationalTitle() != null)   lg.put("internationalTitle", req.getInternationalTitle());
        if (req.getInternationalAbstract() != null)lg.put("internationalAbstract", req.getInternationalAbstract());
        if (req.getEnabled() != null)              lg.put("enabled", req.getEnabled());
        if (req.getAdvertised() != null)           lg.put("advertised", req.getAdvertised());
        if (req.getBounds() != null)               lg.put("bounds", buildBoundsMap(req.getBounds()));
        if (req.getKeywords() != null)             lg.put("keywords", Collections.singletonMap("string", req.getKeywords()));
        if (req.getMetadata() != null)             lg.put("metadata", buildMetadataMap(req.getMetadata()));
        return serializeToJson(Collections.singletonMap("layerGroup", lg));
    }

    private String buildUpdatePayload(
            UpdateLayerGroupRequest req) {
        Map<String, Object> lg = new LinkedHashMap<String, Object>();
        if (req.getPublishables() != null)         lg.put("publishables", buildPublishablesMap(req.getPublishables()));
        if (req.getStyles() != null)               lg.put("styles", buildStylesMap(req.getStyles()));
        if (req.getMode() != null)                 lg.put("mode", req.getMode());
        if (req.getTitle() != null)                lg.put("title", req.getTitle());
        if (req.getAbstractText() != null)         lg.put("abstractTxt", req.getAbstractText());
        if (req.getInternationalTitle() != null)   lg.put("internationalTitle", req.getInternationalTitle());
        if (req.getInternationalAbstract() != null)lg.put("internationalAbstract", req.getInternationalAbstract());
        if (req.getEnabled() != null)              lg.put("enabled", req.getEnabled());
        if (req.getAdvertised() != null)           lg.put("advertised", req.getAdvertised());
        if (req.getBounds() != null)               lg.put("bounds", buildBoundsMap(req.getBounds()));
        if (req.getKeywords() != null)             lg.put("keywords", Collections.singletonMap("string", req.getKeywords()));
        if (req.getMetadata() != null)             lg.put("metadata", buildMetadataMap(req.getMetadata()));
        return serializeToJson(Collections.singletonMap("layerGroup", lg));
    }

    private Map<String, Object> buildPublishablesMap(
            List<CreateLayerGroupRequest.PublishableEntry> entries) {
        List<Map<String, Object>> list =
                new ArrayList<Map<String, Object>>();
        for (CreateLayerGroupRequest.PublishableEntry e : entries) {
            Map<String, Object> p = new LinkedHashMap<String, Object>();
            p.put("@type", e.getType());
            p.put("name", e.getName());
            list.add(p);
        }
        return Collections.singletonMap("published", (Object) list);
    }

    private Map<String, Object> buildStylesMap(List<String> styleNames) {
        List<Object> styleList = new ArrayList<Object>();
        for (String name : styleNames) {
            if (name == null || name.isEmpty()) {
                styleList.add("");
            } else {
                Map<String, Object> styleObj = new LinkedHashMap<String, Object>();
                styleObj.put("name", name);
                styleList.add(styleObj);
            }
        }
        return Collections.singletonMap("style", (Object) styleList);
    }

    private Map<String, Object> buildBoundsMap(
            CreateLayerGroupRequest.BoundsSpec bounds) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("minx", bounds.getMinx());
        m.put("maxx", bounds.getMaxx());
        m.put("miny", bounds.getMiny());
        m.put("maxy", bounds.getMaxy());
        m.put("crs", bounds.getCrs());
        return m;
    }

    private Map<String, Object> buildMetadataMap(StringMap entries) {
        List<Map<String, Object>> entryList =
                new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, String> e : entries.getEntries().entrySet()) {
            Map<String, Object> entry = new LinkedHashMap<String, Object>();
            entry.put("@key", e.getKey());
            entry.put("$", e.getValue());
            entryList.add(entry);
        }
        return Collections.singletonMap("entry", (Object) entryList);
    }

    // [8] Parsing helpers

    private List<LayerGroupSummary> parseList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items =
                    root.path("layerGroups").path("layerGroup");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class,
                            LayerGroupSummary.class));
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse layerGroup list response", e);
        }
    }

    private LayerGroup parseLayerGroup(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            LayerGroupEnvelope env = om.readValue(body, LayerGroupEnvelope.class);
            return env.layerGroup;
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse layerGroup response", e);
        }
    }


    // [9] Envelope

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LayerGroupEnvelope {
        @JsonProperty("layerGroup")
        public LayerGroup layerGroup;
        public LayerGroupEnvelope() {}
    }
}
