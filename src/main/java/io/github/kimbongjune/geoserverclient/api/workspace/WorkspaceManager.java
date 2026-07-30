
package io.github.kimbongjune.geoserverclient.api.workspace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.UpdateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.Workspace;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.WorkspaceNotFoundException;
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
 * GeoServer Workspace REST API client.
 *
 * <p>A workspace is the top-level logical container in GeoServer that groups layers, stores, and
 * styles. Each workspace has a unique name; creating a workspace also automatically creates a
 * namespace with the same name.
 *
 * <h2>Endpoints</h2>
 * <pre>
 * POST    /rest/workspaces/{name}
 * </pre>
 */
public class WorkspaceManager extends AbstractManager {

    // Internal response envelope for single-workspace GET
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class WorkspaceEnvelope {
        @JsonProperty("workspace")
        public Workspace workspace;
    }

    public WorkspaceManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    /**
     * Returns all workspaces (summary: name + href only).
     * <p>GeoServer returns {@code {"workspaces":""}} when there are no workspaces;
     * this method normalizes that to an empty list.
     *
     * @return list of workspace summaries, never null
     */
    public List<WorkspaceSummary> list() {
        GeoServerResponse response = httpClient.get("/rest/workspaces", "application/json");
        handleErrorResponse(response, "GET", "/rest/workspaces");
        return parseWorkspaceList(response.getBody());
    }

    /**
     * Returns the full details of a workspace.
     *
     * @param name workspace name (required)
     * @return workspace detail
     * @throws InvalidParameterException   if name is null or empty
     * @throws WorkspaceNotFoundException  if no workspace with the given name exists
     */
    public Workspace get(String name) {
        requireNonEmpty(name, "workspaceName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + name, "application/json");
        if (response.isNotFound()) {
            throw new WorkspaceNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "GET", "/rest/workspaces/" + name);
        return parseWorkspace(response.getBody());
    }

    /**
     * Returns the current default workspace.
     *
     * @return default workspace detail
     */
    public Workspace getDefault() {
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/default", "application/json");
        handleErrorResponse(response, "GET", "/rest/workspaces/default");
        return parseWorkspace(response.getBody());
    }

    /**
     * Returns true if a workspace with the given name exists.
     *
     * @param name workspace name
     * @throws InvalidParameterException if name is null or empty
     */
    public boolean exists(String name) {
        requireNonEmpty(name, "workspaceName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + name, "application/json");
        return response.isSuccessful();
    }

    /**
     * Creates a new workspace.
     * <p>A namespace with the same name is automatically created by GeoServer.
     *
     * @param request creation parameters (required)
     * @return the created workspace (via GET after POST)
     * @throws InvalidParameterException        if request or name is null/empty
     * @throws ResourceAlreadyExistsException   if a workspace with the same name already exists (409)
     */
    public Workspace create(CreateWorkspaceRequest request) {
        requireNonNull(request, "request");

        String path = "/rest/workspaces" + (request.isSetAsDefault() ? "?default=true" : "");
        String body = buildCreatePayload(request);

        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");

        if (response.getStatusCode() == 409) {
            throw new ResourceAlreadyExistsException("Workspace", request.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);

        return get(request.getName());
    }

    /**
     * Updates an existing workspace. Supports partial updates (only set fields are sent).
     *
     * @param name    current workspace name (required)
     * @param request update parameters — at least one of name/isolated must be set
     * @return the updated workspace (via GET after PUT)
     * @throws InvalidParameterException  if name is null/empty or request is null
     * @throws WorkspaceNotFoundException if no workspace with the given name exists
     */
    public Workspace update(String name, UpdateWorkspaceRequest request) {
        requireNonEmpty(name, "workspaceName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + name;
        String body = buildUpdatePayload(request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new WorkspaceNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        // Name may have changed — use new name if provided
        String resolvedName = request.getName() != null ? request.getName() : name;
        return get(resolvedName);
    }

    /**
     * Deletes a workspace.
     * Convenience overload with {@code recurse=false}.
     *
     * @param name workspace name
     * @throws WorkspaceNotFoundException if no workspace with the given name exists
     */
    public void delete(String name) {
        delete(name, false);
    }

    /**
     * Deletes a workspace.
     *
     * @param name    workspace name (required)
     * @param recurse if true, all contained stores/layers/styles are also deleted.
     *                If false and the workspace is not empty, GeoServer returns 403.
     * @throws InvalidParameterException  if name is null or empty
     * @throws WorkspaceNotFoundException if no workspace with the given name exists (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *                                   if recurse=false and workspace has contents (403)
     */
    public void delete(String name, boolean recurse) {
        requireNonEmpty(name, "workspaceName");

        String path = "/rest/workspaces/" + name + "?recurse=" + recurse;
        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new WorkspaceNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Sets the default workspace.
     *
     * @param name workspace name to set as default (required)
     * @throws InvalidParameterException  if name is null or empty
     * @throws WorkspaceNotFoundException if no workspace with the given name exists
     */
    public void setDefault(String name) {
        requireNonEmpty(name, "workspaceName");
        // Validate existence upfront — gives a clear error instead of a cryptic GeoServer 500
        get(name);

        String body = buildNameOnlyPayload(name);
        GeoServerResponse response = httpClient.put(
                "/rest/workspaces/default", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/workspaces/default");
    }

    // Parsing helpers

    private List<WorkspaceSummary> parseWorkspaceList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("workspaces").path("workspace");
            if (!items.isArray()) {
                // GeoServer returns {"workspaces":""} when empty
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(List.class, WorkspaceSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse workspace list response", e);
        }
    }

    private Workspace parseWorkspace(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            WorkspaceEnvelope envelope = om.readValue(body, WorkspaceEnvelope.class);
            return envelope.workspace;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse workspace response", e);
        }
    }

    // Payload builders

    private String buildCreatePayload(CreateWorkspaceRequest request) {
        Map<String, Object> ws = new LinkedHashMap<>();
        ws.put("name", request.getName());
        if (request.getIsolated() != null) {
            ws.put("isolated", request.getIsolated());
        }
        return serializeToJson(Collections.singletonMap("workspace", ws));
    }

    private String buildUpdatePayload(UpdateWorkspaceRequest request) {
        Map<String, Object> ws = new LinkedHashMap<>();
        if (request.getName() != null) {
            ws.put("name", request.getName());
        }
        if (request.getIsolated() != null) {
            ws.put("isolated", request.getIsolated());
        }
        return serializeToJson(Collections.singletonMap("workspace", ws));
    }

    private String buildNameOnlyPayload(String name) {
        return serializeToJson(Collections.singletonMap("workspace",
                Collections.singletonMap("name", name)));
    }


}

