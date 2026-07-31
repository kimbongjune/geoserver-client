package io.github.kimbongjune.geoserverclient.api.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.settings.Contact;
import io.github.kimbongjune.geoserverclient.dto.settings.GlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.WorkspaceSettings;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * GeoServer Settings REST API client.
 *
 * <p>Covers global server settings, contact info, and per-workspace local settings.
 * Two Spring controllers back this API:
 * <ul>
 *   <li>Global/contact: {@code src/restconfig/.../org/geoserver/rest/SettingsController.java}
 *       ({@code @RequestMapping} path = "/settings")</li>
 *   <li>Workspace-local: {@code src/restconfig/.../org/geoserver/rest/LocalSettingsController.java}
 *       ({@code @RequestMapping} path = "/workspaces/{workspaceName}/settings")</li>
 * </ul>
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET    /rest/settings
 * PUT    /rest/settings
 * GET    /rest/settings/contact
 * PUT    /rest/settings/contact
 * GET    /rest/workspaces/{ws}/settings
 * POST   /rest/workspaces/{ws}/settings
 * PUT    /rest/workspaces/{ws}/settings
 * DELETE /rest/workspaces/{ws}/settings
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * SettingsManager mgr = client.settings();
 * GlobalSettings gs = mgr.getGlobal();
 * gs.getSettings().setNumDecimals(6);
 * mgr.updateGlobal(gs);
 * }</pre>
 *
 * @since 1.0.0
 */
public class SettingsManager extends AbstractManager {

    /**
     * Constructs a new SettingsManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public SettingsManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/settings

    /**
     * Returns the full global server settings.
     *
     * @return global settings (settings, jai, coverageAccess sections)
     */
    public GlobalSettings getGlobal() {
        GeoServerResponse response =
                httpClient.get("/rest/settings.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/settings");
        return parseGlobalSettings(response.getBody());
    }

    // [2] PUT /rest/settings

    /**
     * Updates the global server settings.
     *
     * <p><b>Behavior: REPLACE</b> — primitive fields (numDecimals, verbose, etc.) reset to 0/false
     * when omitted from the request body.
     * Recommendation: call {@link #getGlobal()} first, modify the desired fields, then PUT the full body.
     *
     * @param settings updated global settings
     */
    public void updateGlobal(GlobalSettings settings) {
        requireNonNull(settings, "settings");
        String body = serializeGlobal(settings);
        GeoServerResponse response =
                httpClient.put("/rest/settings", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/settings");
    }

    // [3] GET /rest/settings/contact

    /**
     * Returns the contact settings.
     *
     * @return contact settings
     */
    public Contact getContact() {
        GeoServerResponse response =
                httpClient.get("/rest/settings/contact.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/settings/contact");
        return parseContact(response.getBody());
    }

    // [4] PUT /rest/settings/contact

    /**
     * Updates the contact settings.
     *
     * <p><b>Behavior: MERGE</b> — null fields (String type) are skipped, preserving existing values.
     * Safe to send a partial body with only the fields to change.
     *
     * @param contact updated contact info
     */
    public void updateContact(Contact contact) {
        requireNonNull(contact, "contact");
        String body = serializeContact(contact);
        GeoServerResponse response =
                httpClient.put("/rest/settings/contact", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/settings/contact");
    }

    // [5] GET /rest/workspaces/{ws}/settings

    /**
     * Returns workspace-local settings.
     *
     * <p>Returns 200 even when no local settings exist — use
     * {@link WorkspaceSettings#hasLocalSettings()} to check.
     * A non-existent workspace name causes a 500 NPE on the server.
     *
     * @param workspaceName workspace name (required)
     * @return workspace local settings
     */
    public WorkspaceSettings getWorkspaceSettings(
            String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/settings.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseWorkspaceSettings(response.getBody());
    }

    // [6] POST /rest/workspaces/{ws}/settings

    /**
     * Creates workspace-local settings.
     *
     * <p>Uses {@code Accept: text/plain} internally because the endpoint's {@code @PostMapping}
     * declares {@code produces = "text/plain"} — appending {@code .json} causes 406.
     *
     * @param workspaceName workspace name (required)
     * @param settings      local settings (workspace.name field required)
     */
    public void createWorkspaceSettings(String workspaceName,
            WorkspaceSettings settings) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(settings, "settings");
        String path = "/rest/workspaces/" + workspaceName + "/settings";
        String body = serializeWorkspaceSettings(settings);
        // Accept: text/plain — using a .json suffix causes 406 (produces=text/plain in source)
        GeoServerResponse response =
                httpClient.post(path, body, "application/json", "text/plain");
        handleErrorResponse(response, "POST", path);
    }

    // [7] PUT /rest/workspaces/{ws}/settings

    /**
     * Upserts workspace-local settings (creates if absent, updates if present).
     *
     * <p><b>Behavior: REPLACE</b> — primitive fields reset to 0/false when omitted.
     * A non-existent workspace name causes a 500 NPE on the server.
     *
     * @param workspaceName workspace name (required)
     * @param settings      local settings (workspace.name field required)
     */
    public void updateWorkspaceSettings(String workspaceName,
            WorkspaceSettings settings) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(settings, "settings");
        String path = "/rest/workspaces/" + workspaceName + "/settings";
        String body = serializeWorkspaceSettings(settings);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [8] DELETE /rest/workspaces/{ws}/settings

    /**
     * Deletes workspace-local settings. The workspace falls back to global settings after deletion.
     *
     * <p>Deleting when no local settings exist causes a 500 NPE (GeoServer bug).
     * Call {@link #getWorkspaceSettings(String)} first and check
     * {@code hasLocalSettings()} before calling this method.
     *
     * @param workspaceName workspace name (required)
     */
    public void deleteWorkspaceSettings(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/settings";
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // Serialization helpers

    private String serializeGlobal(GlobalSettings gs) {
        try {
            Map<String, Object> root = new LinkedHashMap<String, Object>();
            root.put("global", gs);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize GlobalSettings", e);
        }
    }

    private String serializeContact(Contact contact) {
        try {
            Map<String, Object> root = new LinkedHashMap<String, Object>();
            root.put("contact", contact);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize Contact", e);
        }
    }

    private String serializeWorkspaceSettings(
            WorkspaceSettings ws) {
        try {
            Map<String, Object> root = new LinkedHashMap<String, Object>();
            root.put("settings", ws);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize WorkspaceSettings", e);
        }
    }

    // Parsing helpers

    private GlobalSettings parseGlobalSettings(
            String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode globalNode = root.path("global");
            return om.treeToValue(globalNode,
                    GlobalSettings.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse GlobalSettings response", e);
        }
    }

    private Contact parseContact(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode contactNode = root.path("contact");
            return om.treeToValue(contactNode,
                    Contact.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse Contact response", e);
        }
    }

    private WorkspaceSettings parseWorkspaceSettings(
            String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode settingsNode = root.path("settings");
            return om.treeToValue(settingsNode,
                    WorkspaceSettings.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse WorkspaceSettings response", e);
        }
    }

}
