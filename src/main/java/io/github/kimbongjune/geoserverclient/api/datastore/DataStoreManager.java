package io.github.kimbongjune.geoserverclient.api.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.datastore.UpdateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.DataStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GeoServer DataStore REST API manager.
 *
 * <p>A DataStore is a container for vector spatial data. Supported sources include files
 * (Shapefile), databases (PostGIS), and remote servers (WFS). Each DataStore belongs to
 * a specific workspace and can hold one or more FeatureTypes.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/workspaces/{workspaceName}/datastores
 * POST    /rest/workspaces/{workspaceName}/datastores
 * GET     /rest/workspaces/{workspaceName}/datastores/{storeName}
 * PUT     /rest/workspaces/{workspaceName}/datastores/{storeName}
 * DELETE  /rest/workspaces/{workspaceName}/datastores/{storeName}
 * PUT     /rest/workspaces/{workspaceName}/datastores/{storeName}/reset
 * POST    /rest/workspaces/{workspaceName}/datastores/{storeName}/reset
 * GET     /rest/workspaces/{workspaceName}/datastores/{storeName}/{method}.{format}
 * PUT     /rest/workspaces/{workspaceName}/datastores/{storeName}/{method}.{format}
 * POST    /rest/workspaces/{workspaceName}/datastores/{storeName}/mosaic/{method}.{format}
 * POST    /workspaces/{ws}/appschemastores/{store}/cleanSchemas
 * POST    /workspaces/{ws}/appschemastores/{store}/datastores/{id}/cleanSchemas
 * POST    /workspaces/{ws}/appschemastores/{store}/rebuildMongoSchemas?ids=...&amp;max=N
 * POST    /workspaces/{ws}/appschemastores/{store}/datastores/{id}/rebuildMongoSchemas?ids=...&amp;max=N&amp;schema=...
 * </pre>
 */
public class DataStoreManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class DataStoreEnvelope {
        @JsonProperty("dataStore")
        public DataStore dataStore;
    }

    public DataStoreManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] Public API

    /**
     * List all DataStores in a workspace (name + href summary).
     *
     * @param workspaceName workspace name (required)
     * @return DataStore summary list, never null
     * @throws InvalidParameterException if workspaceName is null or empty
     */
    public List<DataStoreSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/datastores";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseDataStoreList(response.getBody());
    }

    /**
     * Get details of a specific DataStore.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name (required)
     * @return DataStore details
     * @throws InvalidParameterException  if workspaceName or storeName is null/empty
     * @throws DataStoreNotFoundException if no DataStore with the given name exists
     */
    public DataStore get(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName + "/datastores/" + storeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new DataStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseDataStore(response.getBody());
    }

    /**
     * Returns true if the DataStore exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public boolean exists(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/datastores/" + storeName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Create a new DataStore.
     *
     * @param workspaceName workspace name (required)
     * @param request       creation parameters (required; name + connectionParams required)
     * @return the created DataStore (fetched via GET after POST), or null if GET fails
     *         (GeoServer may return 404 for stores with non-ASCII names)
     * @throws InvalidParameterException      if workspaceName or request is null/empty
     * @throws ResourceAlreadyExistsException if a DataStore with the same name already exists
     */
    public DataStore create(String workspaceName, CreateDataStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/datastores";
        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");

        // GeoServer returns 500 (not 409) for duplicate store names — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("DataStore",
                    workspaceName + "/" + request.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);

        // GeoServer returns 404 on GET for stores with non-ASCII names; return null if so.
        try {
            return get(workspaceName, request.getName());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Update a DataStore. Partial update supported.
     * <p>Note: including connectionParameters replaces all existing parameters entirely.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     current DataStore name to update (required)
     * @param request       update parameters (required)
     * @return the updated DataStore (fetched via GET after PUT)
     * @throws InvalidParameterException  if workspaceName/storeName/request is null/empty
     * @throws DataStoreNotFoundException if no DataStore with the given name exists
     */
    public DataStore update(String workspaceName, String storeName, UpdateDataStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/datastores/" + storeName;
        String body = buildUpdatePayload(request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new DataStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        // If the name was changed, look up by the new name
        String resolvedName = request.getName() != null ? request.getName() : storeName;
        return get(workspaceName, resolvedName);
    }

    /**
     * Delete a DataStore with recurse=false and purge=none.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name to delete (required)
     * @throws DataStoreNotFoundException if no DataStore with the given name exists (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *                                   if recurse=false and the store has FeatureTypes (403)
     */
    public void delete(String workspaceName, String storeName) {
        delete(workspaceName, storeName, false, "none");
    }

    /**
     * Delete a DataStore with purge=none.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name to delete (required)
     * @param recurse       when true, also deletes all FeatureTypes and Layers in the store;
     *                      when false, the store must be empty or 403 is returned
     */
    public void delete(String workspaceName, String storeName, boolean recurse) {
        delete(workspaceName, storeName, recurse, "none");
    }

    /**
     * Delete a DataStore with explicit recurse and purge options.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name to delete (required)
     * @param recurse       when true, also deletes child FeatureTypes/Layers
     * @param purge         source file deletion policy:
     *                      "none" (default, keep files), "metadata" (delete metadata only), "all" (delete all files)
     * @throws InvalidParameterException  if workspaceName or storeName is null/empty
     * @throws DataStoreNotFoundException if no DataStore with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName, boolean recurse, String purge) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/datastores/" + storeName
                + "?recurse=" + recurse
                + "&purge=" + (purge != null ? purge : "none");

        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new DataStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Reset the DataStore cache. Forces GeoServer to reconnect to the source on the next request.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name to reset (required)
     * @throws InvalidParameterException  if workspaceName or storeName is null/empty
     */
    public void reset(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/datastores/" + storeName + "/reset";
        GeoServerResponse response = httpClient.put(path, null, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * Create or update a DataStore by uploading a file.
     *
     * <pre>
     * // Shapefile ZIP upload example
     * manager.uploadFile("sf", "mystore", "file", "shp", zipFile)
     *
     * // GeoPackage upload example
     * manager.uploadFile("sf", "mygpkg", "file", "gpkg", gpkgFile)
     * </pre>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name (required)
     * @param method        upload method: "file" (local upload), "url" (remote URL), "external" (existing server file)
     * @param format        file format: "shp", "properties", "h2", "gpkg", "mbtiles", "geoparquet"
     * @param file          file to upload (ZIP must have flat structure for Shapefiles)
     * @throws InvalidParameterException if any required parameter is null/empty
     */
    public void uploadFile(String workspaceName, String storeName,
                           String method, String format, File file) {
        uploadFile(workspaceName, storeName, method, format, file, null, null, null);
    }

    /**
     * Create or update a DataStore by uploading a file (with optional parameters).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name (required)
     * @param method        "file", "url", "external"
     * @param format        "shp", "properties", "h2", "gpkg", "mbtiles", "geoparquet"
     * @param file          file to upload
     * @param configure     FeatureType auto-configuration: "first" (default), "none", "all"
     * @param update        data update policy: "append" (default), "overwrite"
     * @param charset       file encoding (e.g. "UTF-8", "EUC-KR")
     */
    public void uploadFile(String workspaceName, String storeName,
                           String method, String format, File file,
                           String configure, String update, String charset) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(method, "method");
        requireNonEmpty(format, "format");
        requireNonNull(file, "file");

        StringBuilder path = new StringBuilder(
                "/rest/workspaces/" + workspaceName + "/datastores/" + storeName
                + "/" + method + "." + format);

        boolean first = true;
        if (configure != null) {
            path.append(first ? "?" : "&").append("configure=").append(configure); first = false;
        }
        if (update != null) {
            path.append(first ? "?" : "&").append("update=").append(update); first = false;
        }
        if (charset != null) {
            path.append(first ? "?" : "&").append("charset=").append(charset);
        }

        String contentType = format.equals("shp") ? "application/zip" : "application/octet-stream";
        GeoServerResponse response = httpClient.putFile(path.toString(), file, contentType, "application/json");
        handleErrorResponse(response, "PUT", path.toString());
    }

    /**
     * Download the DataStore source files as a ZIP archive.
     *
     * <p>Only works for file-based stores (Shapefile, GeoPackage, etc.) when the actual files
     * are accessible in the GeoServer data directory. DB-based stores (H2, PostGIS) return 404
     * as they have no file path.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     DataStore name (required)
     * @param method        access method: "file", "url", "external"
     * @param format        file format: "shp", "gpkg", etc.
     * @return ZIP file bytes
     * @throws DataStoreNotFoundException if the store does not exist or has no server-side files (404)
     */
    public byte[] downloadFile(String workspaceName, String storeName,
                               String method, String format) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(method, "method");
        requireNonEmpty(format, "format");

        String path = String.format("/rest/workspaces/%s/datastores/%s/%s.%s",
                workspaceName, storeName, method, format);
        GeoServerResponse response = httpClient.getBinary(path, "application/zip");
        if (response.isNotFound()) {
            throw new DataStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "GET (binary)", path);
        return response.getBinaryBody();
    }

    // [2] Parsing helpers

    private List<DataStoreSummary> parseDataStoreList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("dataStores").path("dataStore");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, DataStoreSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse datastore list response", e);
        }
    }

    private DataStore parseDataStore(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            DataStoreEnvelope envelope = om.readValue(body, DataStoreEnvelope.class);
            return envelope.dataStore;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse datastore response", e);
        }
    }

    // [3] Payload builders

    private String buildCreatePayload(CreateDataStoreRequest request) {
        Map<String, Object> ds = new LinkedHashMap<>();
        ds.put("name", request.getName());
        if (request.getDescription() != null)           ds.put("description",          request.getDescription());
        if (request.getEnabled() != null)               ds.put("enabled",              request.getEnabled());
        if (request.getDefaultStore() != null)          ds.put("_default",             request.getDefaultStore());
        if (request.getType() != null)                  ds.put("type",                 request.getType());
        if (request.getDisableOnConnFailure() != null)  ds.put("disableOnConnFailure", request.getDisableOnConnFailure());
        if (!request.getConnectionParams().isEmpty()) {
            ds.put("connectionParameters",
                    Collections.singletonMap("entry", request.getConnectionParams()));
        }
        return serializeToJson(Collections.singletonMap("dataStore", ds));
    }

    private String buildUpdatePayload(UpdateDataStoreRequest request) {
        Map<String, Object> ds = new LinkedHashMap<>();
        if (request.getName() != null)                  ds.put("name",                 request.getName());
        if (request.getDescription() != null)           ds.put("description",          request.getDescription());
        if (request.getEnabled() != null)               ds.put("enabled",              request.getEnabled());
        if (request.getDefaultStore() != null)          ds.put("_default",             request.getDefaultStore());
        if (request.getDisableOnConnFailure() != null)  ds.put("disableOnConnFailure", request.getDisableOnConnFailure());
        if (!request.getConnectionParams().isEmpty()) {
            ds.put("connectionParameters",
                    Collections.singletonMap("entry", request.getConnectionParams()));
        }
        return serializeToJson(Collections.singletonMap("dataStore", ds));
    }


}
