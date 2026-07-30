package io.github.kimbongjune.geoserverclient.api.style;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.style.Style;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.StyleNotFoundException;
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
 * GeoServer REST API - Style management (SLD 1.0.0 / SLD 1.1.0(SE) style CRUD)
 *
 * <h2>Endpoints</h2>
 * <pre>
 * POST    /rest/styles/{style}
 * PUT     /rest/layers/{layer}/styles
 * DELETE  /rest/layers/{layer}/styles
 * GET     /rest/layers/{layer}/styles/{style}
 * DELETE  /rest/layers/{layer}/styles/{style}
 * GET     /rest/workspaces/{ws}/layers/{layer}/styles
 * POST    /rest/workspaces/{ws}/layers/{layer}/styles
 * DELETE  /rest/styles/{style}
 * DELETE  /rest/styles/{style}?purge=true
 * DELETE  /rest/styles/{style}?recurse=true
 * DELETE  /rest/styles/{style}?recurse=true&amp;purge=true
 * GET     /layers/{layer}.json
 * </pre>
 */
public class StyleManager extends AbstractManager {

    private static final String SLD_10_CONTENT_TYPE = "application/vnd.ogc.sld+xml";
    private static final String SLD_11_CONTENT_TYPE = "application/vnd.ogc.se+xml";

    public StyleManager(GeoServerHttpClient httpClient,
                        SerializerFactory serializerFactory,
                        DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // list

    /**
     * Returns the list of global styles.
     *
     * @return list of style summaries; empty list if none exist
     */
    public List<StyleSummary> list() {
        GeoServerResponse response =
                httpClient.get("/rest/styles.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/styles");
        return parseList(response.getBody());
    }

    /**
     * Returns the list of workspace-scoped styles.
     *
     * <p>Returns 200 with an empty list when the workspace exists but has no styles (including LayerGroup styles).</p>
     *
     * @param workspaceName workspace name (required)
     * @return list of style summaries; empty list if none exist
     */
    public List<StyleSummary> listByWorkspace(
            String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/styles.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    /**
     * Returns the available styles list for a layer.
     *
     * @param layerName layer name (also accepts workspaceName:layerName format)
     * @return list of style summaries; empty list if none exist
     */
    public List<StyleSummary> listByLayer(
            String layerName) {
        requireNonEmpty(layerName, "layerName");
        String path = "/rest/layers/" + layerName + "/styles.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    // get

    /**
     * Returns a global style's metadata.
     *
     * @param name style name (required)
     * @return style metadata
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public Style get(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/styles/" + name + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseStyle(response.getBody());
    }

    /**
     * Returns a workspace-scoped style's metadata.
     *
     * @param workspaceName workspace name (required)
     * @param name          style name (required)
     * @return style metadata
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public Style getByWorkspace(
            String workspaceName, String name) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspaceName + "/styles/" + name + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    workspaceName + ":" + name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseStyle(response.getBody());
    }

    /**
     * Returns the SLD body of a global style (as SLD 1.0.0).
     *
     * @param name style name (required)
     * @return SLD content (raw GET always returns re-serialized form; raw=true only affects POST/PUT)
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public StyleContent getSld(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/styles/" + name + ".sld";
        GeoServerResponse response =
                httpClient.get(path, SLD_10_CONTENT_TYPE);
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return StyleContent.of(response.getBody());
    }

    /**
     * Returns the SLD body of a workspace-scoped style (as SLD 1.0.0).
     *
     * @param workspaceName workspace name (required)
     * @param name          style name (required)
     * @return SLD content
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public StyleContent getSldByWorkspace(String workspaceName, String name) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspaceName + "/styles/" + name + ".sld";
        GeoServerResponse response =
                httpClient.get(path, SLD_10_CONTENT_TYPE);
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    workspaceName + ":" + name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return StyleContent.of(response.getBody());
    }

    // exists

    /**
     * Returns true if a global style with the given name exists.
     *
     * @param name style name (required)
     * @return true if the style exists
     */
    public boolean exists(String name) {
        requireNonEmpty(name, "name");
        GeoServerResponse response =
                httpClient.get("/rest/styles/" + name, "application/json");
        return response.isSuccessful();
    }

    /**
     * Returns true if a workspace-scoped style with the given name exists.
     *
     * @param workspaceName workspace name (required)
     * @param name          style name (required)
     * @return true if the style exists
     */
    public boolean existsByWorkspace(String workspaceName, String name) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(name, "name");
        GeoServerResponse response =
                httpClient.get("/rest/workspaces/" + workspaceName + "/styles/" + name,
                        "application/json");
        return response.isSuccessful();
    }

    // create

    /**
     * Creates a global style (name taken from the SLD UserStyle/Name element).
     *
     * @param content SLD content (required)
     * @throws AuthenticationException on duplicate name — returns 403
     *         [GeoServer 2.28.2 BUG] should return 409 but returns 403
     */
    public void create(StyleContent content) {
        requireNonNull(content, "content");
        requireNonEmpty(content.getSldBody(), "content.sldBody");
        String path = "/rest/styles" + (content.isRaw() ? "?raw=true" : "");
        GeoServerResponse response =
                httpClient.post(path, content.getSldBody(), sldContentType(content), "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Creates a global style with an explicit name (?name= overrides the SLD Name element).
     *
     * @param content SLD content (required)
     * @param name    explicit style name (required)
     * @throws AuthenticationException on duplicate name — returns 403
     *         [GeoServer 2.28.2 BUG] should return 409 but returns 403
     */
    public void create(StyleContent content, String name) {
        requireNonNull(content, "content");
        requireNonEmpty(content.getSldBody(), "content.sldBody");
        requireNonEmpty(name, "name");
        String path = "/rest/styles?name=" + name + (content.isRaw() ? "&raw=true" : "");
        GeoServerResponse response =
                httpClient.post(path, content.getSldBody(), sldContentType(content), "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Creates a workspace-scoped style.
     *
     * @param workspaceName workspace name (required)
     * @param content       SLD content (required)
     * @param name          style name (required)
     * @throws ResourceAlreadyExistsException if the style already exists (409)
     */
    public void createByWorkspace(String workspaceName,
            StyleContent content, String name) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(content, "content");
        requireNonEmpty(content.getSldBody(), "content.sldBody");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspaceName + "/styles?name=" + name
                + (content.isRaw() ? "&raw=true" : "");
        GeoServerResponse response =
                httpClient.post(path, content.getSldBody(), sldContentType(content), "application/json");
        handleErrorResponse(response, "POST", path);
    }

    // update

    /**
     * Updates a global style's SLD body.
     *
     * <p>Note: adding ?name= to a PUT returns 400 NPE from GeoServer; name cannot be changed via SLD PUT.</p>
     *
     * @param name    style name to update (required)
     * @param content new SLD content (required)
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public void update(String name, StyleContent content) {
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        requireNonEmpty(content.getSldBody(), "content.sldBody");
        String path = "/rest/styles/" + name + (content.isRaw() ? "?raw=true" : "");
        GeoServerResponse response =
                httpClient.put(path, content.getSldBody(), sldContentType(content), "application/json");
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    name, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * Updates a workspace-scoped style's SLD body.
     *
     * @param workspaceName workspace name (required)
     * @param name          style name to update (required)
     * @param content       new SLD content (required)
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public void updateByWorkspace(String workspaceName, String name,
            StyleContent content) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        requireNonEmpty(content.getSldBody(), "content.sldBody");
        String path = "/rest/workspaces/" + workspaceName + "/styles/" + name
                + (content.isRaw() ? "?raw=true" : "");
        GeoServerResponse response =
                httpClient.put(path, content.getSldBody(), sldContentType(content), "application/json");
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    workspaceName + ":" + name, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
    }

    // delete

    /**
     * Deletes a global style.
     *
     * <p>Returns 403 if any layer references the style.</p>
     *
     * @param name style name (required)
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/styles/" + name;
        GeoServerResponse response =
                httpClient.delete(path);
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Deletes a global style with purge and recurse options.
     *
     * <p>purge=true: also deletes the SLD file from disk.<br>
     * recurse=true: removes all layer references before deleting.<br>
     * recurse=false (default) with existing references: returns 403.</p>
     *
     * @param name    style name (required)
     * @param purge   if true, also delete the SLD file from disk
     * @param recurse if true, automatically remove all layer references
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public void delete(String name, boolean purge, boolean recurse) {
        requireNonEmpty(name, "name");
        String path = "/rest/styles/" + name + buildDeleteQuery(purge, recurse);
        GeoServerResponse response =
                httpClient.delete(path);
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Deletes a workspace-scoped style.
     *
     * @param workspaceName workspace name (required)
     * @param name          style name (required)
     * @param purge         if true, also delete the SLD file from disk
     * @param recurse       if true, automatically remove all layer references
     * @throws StyleNotFoundException if the style does not exist (404)
     */
    public void deleteByWorkspace(String workspaceName, String name, boolean purge,
            boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspaceName + "/styles/" + name
                + buildDeleteQuery(purge, recurse);
        GeoServerResponse response =
                httpClient.delete(path);
        if (response.isNotFound()) {
            throw new StyleNotFoundException(
                    workspaceName + ":" + name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // layer style association

    /**
     * Adds a style to a layer's available styles list.
     *
     * @param layerName  layer name (also accepts workspaceName:layerName format)
     * @param styleName  style name to add (required)
     */
    public void addStyleToLayer(String layerName, String styleName) {
        requireNonEmpty(layerName, "layerName");
        requireNonEmpty(styleName, "styleName");
        String path = "/rest/layers/" + layerName + "/styles";
        String body = buildStyleRefPayload(styleName);
        GeoServerResponse response =
                httpClient.post(path, body, "application/json", "*/*");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Adds a style to a layer's available styles, optionally setting it as the default.
     *
     * @param layerName  layer name (required)
     * @param styleName  style name to add (required)
     * @param setDefault if true, also sets the style as the layer's defaultStyle (?default=true)
     */
    public void addStyleToLayer(String layerName, String styleName, boolean setDefault) {
        requireNonEmpty(layerName, "layerName");
        requireNonEmpty(styleName, "styleName");
        String path = "/rest/layers/" + layerName + "/styles"
                + (setDefault ? "?default=true" : "");
        String body = buildStyleRefPayload(styleName);
        GeoServerResponse response =
                httpClient.post(path, body, "application/json", "*/*");
        handleErrorResponse(response, "POST", path);
    }

    // Payload / Query builders

    private String sldContentType(StyleContent content) {
        return content.isSld11() ? SLD_11_CONTENT_TYPE : SLD_10_CONTENT_TYPE;
    }

    private String buildDeleteQuery(boolean purge, boolean recurse) {
        if (!purge && !recurse) return "";
        StringBuilder sb = new StringBuilder("?");
        if (purge)   sb.append("purge=true&");
        if (recurse) sb.append("recurse=true");
        String q = sb.toString();
        return q.endsWith("&") ? q.substring(0, q.length() - 1) : q;
    }

    private String buildStyleRefPayload(String styleName) {
        try {
            Map<String, Object> style = new LinkedHashMap<String, Object>();
            style.put("name", styleName);
            Map<String, Object> root = new LinkedHashMap<String, Object>();
            root.put("style", style);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize style ref payload", e);
        }
    }

    // Parsing helpers

    private List<StyleSummary> parseList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode stylesNode = root.path("styles");
            // {"styles": ""} — empty
            if (stylesNode.isTextual()) {
                return Collections.emptyList();
            }
            JsonNode styleNode = stylesNode.path("style");
            if (styleNode.isMissingNode() || styleNode.isNull()) {
                return Collections.emptyList();
            }
            if (styleNode.isArray()) {
                return om.readValue(styleNode.traverse(),
                        om.getTypeFactory().constructCollectionType(
                                List.class,
                                StyleSummary.class));
            }
            // Single object (not wrapped in array)
            StyleSummary single =
                    om.treeToValue(styleNode, StyleSummary.class);
            return Collections.singletonList(single);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse style list response", e);
        }
    }

    private Style parseStyle(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode styleNode = root.path("style");
            return om.treeToValue(styleNode, Style.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse style response", e);
        }
    }

}
