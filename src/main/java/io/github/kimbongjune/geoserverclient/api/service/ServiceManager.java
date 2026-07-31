package io.github.kimbongjune.geoserverclient.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.service.ServiceSettings;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * GeoServer OWS Services REST API client.
 *
 * <p>Covers global and workspace-scoped service settings for WMS, WFS, WCS, and WMTS.
 * The underlying Spring controllers are:
 * <ul>
 *   <li>Abstract base: {@code src/restconfig/.../org/geoserver/rest/service/ServiceSettingsController.java}
 *       (defines {@code @GetMapping}/{@code @PutMapping}/{@code @DeleteMapping})</li>
 *   <li>WMS:  {@code src/restconfig-wms/.../rest/service/WMSSettingsController.java}
 *       ({@code @RequestMapping} path = "/services/wms")</li>
 *   <li>WFS:  {@code src/restconfig-wfs/.../rest/service/WFSSettingsController.java}
 *       ({@code @RequestMapping} path = "/services/wfs")</li>
 *   <li>WCS:  {@code src/restconfig-wcs/.../rest/service/WCSSettingsController.java}
 *       ({@code @RequestMapping} path = "/services/wcs")</li>
 *   <li>WMTS: {@code src/restconfig-wmts/.../rest/service/WMTSSettingsController.java}
 *       ({@code @RequestMapping} path = "/services/wmts")</li>
 * </ul>
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET    /rest/services/wms/settings
 * PUT    /rest/services/wms/settings
 * GET    /rest/services/wfs/settings
 * PUT    /rest/services/wfs/settings
 * GET    /rest/services/wcs/settings
 * PUT    /rest/services/wcs/settings
 * GET    /rest/services/wmts/settings
 * PUT    /rest/services/wmts/settings
 * GET    /rest/services/{svc}/workspaces/{ws}/settings
 * PUT    /rest/services/{svc}/workspaces/{ws}/settings
 * DELETE /rest/services/{svc}/workspaces/{ws}/settings
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * ServiceManager mgr = client.service();
 * ServiceSettings wms = mgr.getWms();
 * wms.setEnabled(false);
 * mgr.updateWms(wms);
 * }</pre>
 *
 * @since 1.0.0
 */
public class ServiceManager extends AbstractManager {

    /**
     * Constructs a new ServiceManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public ServiceManager(GeoServerHttpClient httpClient,
                          SerializerFactory serializerFactory,
                          DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/services/wms/settings

    /**
     * Returns the global WMS settings.
     *
     * @return WMS settings
     */
    public ServiceSettings getWms() {
        GeoServerResponse response =
                httpClient.get("/rest/services/wms/settings.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/services/wms/settings");
        return parseService(response.getBody(), "wms");
    }

    // [2] PUT /rest/services/wms/settings

    /**
     * Updates the global WMS settings.
     *
     * <p><b>Behavior: MERGE</b> — existing values are preserved for fields not included in the body.
     * Send a complete body when you need to set fields to 0 or false.
     *
     * @param settings updated WMS settings
     */
    public void updateWms(ServiceSettings settings) {
        requireNonNull(settings, "settings");
        String body = serializeService(settings, "wms");
        GeoServerResponse response =
                httpClient.put("/rest/services/wms/settings", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/services/wms/settings");
    }

    // [3] GET /rest/services/wfs/settings

    /**
     * Returns the global WFS settings.
     *
     * @return WFS settings
     */
    public ServiceSettings getWfs() {
        GeoServerResponse response =
                httpClient.get("/rest/services/wfs/settings.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/services/wfs/settings");
        return parseService(response.getBody(), "wfs");
    }

    // [4] PUT /rest/services/wfs/settings

    /**
     * Updates the global WFS settings.
     *
     * <p><b>Behavior: MERGE</b> — existing values are preserved for fields not included in the body.
     *
     * @param settings updated WFS settings
     */
    public void updateWfs(ServiceSettings settings) {
        requireNonNull(settings, "settings");
        String body = serializeService(settings, "wfs");
        GeoServerResponse response =
                httpClient.put("/rest/services/wfs/settings", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/services/wfs/settings");
    }

    // [5] GET /rest/services/wcs/settings

    /**
     * Returns the global WCS settings.
     *
     * @return WCS settings
     */
    public ServiceSettings getWcs() {
        GeoServerResponse response =
                httpClient.get("/rest/services/wcs/settings.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/services/wcs/settings");
        return parseService(response.getBody(), "wcs");
    }

    // [6] PUT /rest/services/wcs/settings

    /**
     * Updates the global WCS settings.
     *
     * <p><b>Behavior: MERGE</b> — existing values are preserved for fields not included in the body.
     *
     * @param settings updated WCS settings
     */
    public void updateWcs(ServiceSettings settings) {
        requireNonNull(settings, "settings");
        String body = serializeService(settings, "wcs");
        GeoServerResponse response =
                httpClient.put("/rest/services/wcs/settings", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/services/wcs/settings");
    }

    // [7] GET /rest/services/wmts/settings

    /**
     * Returns the global WMTS settings.
     *
     * @return WMTS settings
     */
    public ServiceSettings getWmts() {
        GeoServerResponse response =
                httpClient.get("/rest/services/wmts/settings.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/services/wmts/settings");
        return parseService(response.getBody(), "wmts");
    }

    // [8] PUT /rest/services/wmts/settings

    /**
     * Updates the global WMTS settings.
     *
     * <p><b>Behavior: MERGE</b> — existing values are preserved for fields not included in the body.
     *
     * @param settings updated WMTS settings
     */
    public void updateWmts(ServiceSettings settings) {
        requireNonNull(settings, "settings");
        String body = serializeService(settings, "wmts");
        GeoServerResponse response =
                httpClient.put("/rest/services/wmts/settings", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/services/wmts/settings");
    }

    // [9] GET /rest/services/{svc}/workspaces/{ws}/settings

    /**
     * Returns workspace-scoped service settings.
     *
     * <p>Returns 404 if the workspace does not exist or no workspace-scoped settings are configured
     * for the given service ->
     * {@link ResourceNotFoundException}.
     *
     * @param svc service type ({@code wms} | {@code wfs} | {@code wcs} | {@code wmts})
     * @param ws  workspace name
     * @return workspace-scoped service settings
     */
    public ServiceSettings getWorkspaceSettings(
            String svc, String ws) {
        requireNonEmpty(svc, "svc");
        requireNonEmpty(ws, "ws");
        String path = "/rest/services/" + svc + "/workspaces/" + ws + "/settings.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseService(response.getBody(), svc);
    }

    // [10] PUT /rest/services/{svc}/workspaces/{ws}/settings

    /**
     * Upserts workspace-scoped service settings (creates if absent, updates if present).
     *
     * <p><b>Prerequisite:</b> workspace-scoped settings must be initialized via the GeoServer Admin UI first.
     * PUT on an uninitialized workspace service causes 500
     * ({@link GeoServerResponseException}).
     * Verified on GeoServer 2.28.2: a non-null {@code gml} field on a workspace-scoped WFS PUT does
     * <em>not</em> cause 500 (contrary to earlier notes in this codebase) — the field is simply
     * ignored by GeoServer at this scope. No client-side workaround is needed.
     *
     * @param svc      service type ({@code wms} | {@code wfs} | {@code wcs} | {@code wmts})
     * @param ws       workspace name
     * @param settings updated service settings
     */
    public void updateWorkspaceSettings(String svc, String ws,
            ServiceSettings settings) {
        requireNonEmpty(svc, "svc");
        requireNonEmpty(ws, "ws");
        requireNonNull(settings, "settings");
        String path = "/rest/services/" + svc + "/workspaces/" + ws + "/settings";
        String body = serializeService(settings, svc);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [11] DELETE /rest/services/{svc}/workspaces/{ws}/settings

    /**
     * Deletes workspace-scoped service settings. The workspace falls back to global settings.
     *
     * <p>Returns 404 if the workspace does not exist or no workspace-scoped settings are configured
     * for the given service ->
     * {@link ResourceNotFoundException}.
     *
     * @param svc service type ({@code wms} | {@code wfs} | {@code wcs} | {@code wmts})
     * @param ws  workspace name
     */
    public void deleteWorkspaceSettings(String svc, String ws) {
        requireNonEmpty(svc, "svc");
        requireNonEmpty(ws, "ws");
        String path = "/rest/services/" + svc + "/workspaces/" + ws + "/settings";
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // Serialization helpers

    private String serializeService(
            ServiceSettings settings, String key) {
        try {
            Map<String, Object> root = new LinkedHashMap<String, Object>();
            root.put(key, settings);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize ServiceSettings", e);
        }
    }

    private ServiceSettings parseService(
            String body, String key) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode serviceNode = root.path(key);
            return om.treeToValue(serviceNode,
                    ServiceSettings.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse ServiceSettings response", e);
        }
    }

}
