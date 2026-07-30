package io.github.kimbongjune.geoserverclient.api.coveragestore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStore;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CreateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.UpdateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.CoverageStoreNotFoundException;
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
 * GeoServer CoverageStore REST API manager.
 *
 * <p>A CoverageStore defines a raster spatial data source in the GeoServer catalog.
 * It supports raster formats such as GeoTIFF, ImageMosaic, and WorldImage. Each
 * CoverageStore belongs to a specific workspace and can contain one or more Coverages.
 * The file upload API allows uploading a raster file to the server and simultaneously
 * creating the store and coverage.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/workspaces/{workspaceName}/coveragestores
 * GET     /rest/workspaces/{workspaceName}/coveragestores/{storeName}
 * POST    /rest/workspaces/{workspaceName}/coveragestores
 * PUT     /rest/workspaces/{workspaceName}/coveragestores/{storeName}
 * DELETE  /rest/workspaces/{workspaceName}/coveragestores/{storeName}
 * PUT     /rest/workspaces/{workspaceName}/coveragestores/{storeName}/{method}.{format}
 * POST    /rest/workspaces/{workspaceName}/coveragestores/{storeName}/{method}.{format}
 * </pre>
 */
public class CoverageStoreManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class CoverageStoreEnvelope {
        @JsonProperty("coverageStore")
        public CoverageStore coverageStore;
    }

    public CoverageStoreManager(GeoServerHttpClient httpClient,
                                SerializerFactory serializerFactory,
                                DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Public API

    /**
     * List all coverage stores in a workspace (name + href summary).
     *
     * @param workspaceName workspace name (required)
     * @return coverage store summary list, never null
     * @throws InvalidParameterException if workspaceName is null or empty
     */
    public List<CoverageStoreSummary> list(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/coveragestores";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseCoverageStoreList(response.getBody());
    }

    /**
     * Get coverage store details.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @return coverage store details
     * @throws InvalidParameterException      if workspaceName or storeName is null/empty
     * @throws CoverageStoreNotFoundException if no coverage store with the given name exists
     */
    public CoverageStore get(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new CoverageStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseCoverageStore(response.getBody());
    }

    /**
     * Check whether a coverage store exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public boolean exists(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Create a new coverage store.
     *
     * <p>The POST body automatically includes workspace.name (required — omitting causes GeoServer 500).</p>
     *
     * @param workspaceName workspace name (required)
     * @param request       creation parameters (required, name is required)
     * @return created coverage store (GET after POST)
     * @throws InvalidParameterException      if workspaceName/request is null/empty
     * @throws ResourceAlreadyExistsException if a coverage store with the same name already exists
     */
    public CoverageStore create(String workspaceName, CreateCoverageStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/coveragestores";
        String body = buildCreatePayload(workspaceName, request);
        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");
        // GeoServer returns 500 (not 409) for duplicate store names — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("CoverageStore",
                    workspaceName + "/" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);

        return get(workspaceName, request.getName());
    }

    /**
     * Update a coverage store. Partial update supported.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     current coverage store name to update (required)
     * @param request       update parameters (required, at least one field required)
     * @return updated coverage store (GET after PUT)
     * @throws InvalidParameterException      if workspaceName/storeName/request is null/empty
     * @throws CoverageStoreNotFoundException if no coverage store with the given name exists
     */
    public CoverageStore update(String workspaceName, String storeName, UpdateCoverageStoreRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName;
        String body = buildUpdatePayload(request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new CoverageStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        // If the name was changed, look up by the new name
        String resolvedName = request.getName() != null ? request.getName() : storeName;
        return get(workspaceName, resolvedName);
    }

    /**
     * Delete a coverage store with recurse=false, purge=none.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name to delete (required)
     * @throws InvalidParameterException      if workspaceName or storeName is null/empty
     * @throws CoverageStoreNotFoundException if no coverage store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName) {
        delete(workspaceName, storeName, false, "none");
    }

    /**
     * Delete a coverage store with purge=none.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name to delete (required)
     * @param recurse       when true, also deletes all coverages and layers;
     *                      when false, store must be empty — otherwise returns 403.
     */
    public void delete(String workspaceName, String storeName, boolean recurse) {
        delete(workspaceName, storeName, recurse, "none");
    }

    /**
     * Delete a coverage store with explicit recurse and purge options.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name to delete (required)
     * @param recurse       when true, also deletes all coverages and layers
     * @param purge         file deletion policy: "none" (default, keep files),
     *                      "metadata" (delete metadata only), "all" (delete all files).
     *                      Effective only for ImageMosaic (StructuredGridCoverage2DReader).
     * @throws InvalidParameterException      if workspaceName or storeName is null/empty
     * @throws CoverageStoreNotFoundException if no coverage store with the given name exists (404)
     */
    public void delete(String workspaceName, String storeName, boolean recurse, String purge) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName
                + "?recurse=" + recurse
                + "&purge=" + (purge != null ? purge : "none");

        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new CoverageStoreNotFoundException(workspaceName, storeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Reset the coverage store resource pool cache, forcing GeoServer to reconnect to the source
     * on the next request. PUT and POST behave identically; implemented as PUT.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name to reset (required)
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public void reset(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");

        String path = "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName + "/reset";
        GeoServerResponse response = httpClient.put(path, null, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * Upload a raster file to create or update a coverage store. Runs with configure=none (no auto-coverage).
     *
     * <pre>
     * // Upload a GeoTIFF (store only, no coverage)
     * manager.uploadFile("myws", "mystore", "file", "geotiff", tifFile)
     * </pre>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param method        upload method: "file" (inline), "url" (remote URL), "external" (server path)
     * @param format        file format: "geotiff", "worldimage", "imagemosaic", "arcgrid", etc.
     * @param file          file to upload
     * @throws InvalidParameterException if any required parameter is null/empty
     */
    public void uploadFile(String workspaceName, String storeName,
                           String method, String format, File file) {
        uploadFile(workspaceName, storeName, method, format, file, "none", null, null);
    }

    /**
     * Upload a raster file to create or update a coverage store (with options).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param method        "file", "url", "external"
     * @param format        "geotiff", "worldimage", "imagemosaic", "arcgrid", etc.
     * @param file          file to upload
     * @param configure     auto-coverage mode: "none" (default, store only), "first", "all"
     * @param coverageName  coverage name to create (null uses storeName)
     * @param filename      target filename on server (null uses original filename)
     * @throws InvalidParameterException if any required parameter is null/empty
     */
    public void uploadFile(String workspaceName, String storeName,
                           String method, String format, File file,
                           String configure, String coverageName, String filename) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(method, "method");
        requireNonEmpty(format, "format");
        requireNonNull(file, "file");

        StringBuilder path = new StringBuilder(
                "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName
                + "/" + method + "." + format);

        boolean first = true;
        if (configure != null) {
            path.append(first ? "?" : "&").append("configure=").append(configure); first = false;
        }
        if (coverageName != null) {
            path.append(first ? "?" : "&").append("coverageName=").append(coverageName); first = false;
        }
        if (filename != null) {
            path.append(first ? "?" : "&").append("filename=").append(filename);
        }

        String contentType = determineCoverageContentType(format);
        GeoServerResponse response = httpClient.putFile(path.toString(), file, contentType, "application/json");
        handleErrorResponse(response, "PUT (file)", path.toString());
    }

    /**
     * Harvest a new granule file into an ImageMosaic store.
     * Supported only by StructuredGridCoverage2DReader (ImageMosaic).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     ImageMosaic coverage store name (required)
     * @param method        "file", "url", "external", "remote"
     * @param format        "imagemosaic", etc.
     * @param file          file to harvest
     * @throws InvalidParameterException if any required parameter is null/empty
     */
    public void harvest(String workspaceName, String storeName,
                        String method, String format, File file) {
        harvest(workspaceName, storeName, method, format, file, null, false);
    }

    /**
     * Harvest a new granule file into an ImageMosaic store (with options).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     ImageMosaic coverage store name (required)
     * @param method        "file", "url", "external", "remote"
     * @param format        "imagemosaic", etc.
     * @param file          file to harvest
     * @param filename      target filename on server (null uses original filename)
     * @param updateBBox    when true, recalculates nativeBoundingBox after harvest
     * @throws InvalidParameterException if any required parameter is null/empty
     */
    public void harvest(String workspaceName, String storeName,
                        String method, String format, File file,
                        String filename, boolean updateBBox) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(method, "method");
        requireNonEmpty(format, "format");
        requireNonNull(file, "file");

        StringBuilder path = new StringBuilder(
                "/rest/workspaces/" + workspaceName + "/coveragestores/" + storeName
                + "/" + method + "." + format);

        boolean first = true;
        if (filename != null) {
            path.append(first ? "?" : "&").append("filename=").append(filename); first = false;
        }
        if (updateBBox) {
            path.append(first ? "?" : "&").append("updateBBox=true");
        }

        String contentType = determineCoverageContentType(format);
        GeoServerResponse response = httpClient.postFile(path.toString(), file, contentType, "application/json");
        handleErrorResponse(response, "POST (file)", path.toString());
    }

    // Parsing helpers

    private List<CoverageStoreSummary> parseCoverageStoreList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("coverageStores").path("coverageStore");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, CoverageStoreSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse coverage store list response", e);
        }
    }

    private CoverageStore parseCoverageStore(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            CoverageStoreEnvelope envelope = om.readValue(body, CoverageStoreEnvelope.class);
            return envelope.coverageStore;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse coverage store response", e);
        }
    }

    // Payload builders

    private String buildCreatePayload(String workspaceName, CreateCoverageStoreRequest request) {
        Map<String, Object> cs = new LinkedHashMap<>();
        cs.put("name", request.getName());
        // workspace.name is required — omitting it causes GeoServer 500
        cs.put("workspace", Collections.singletonMap("name", workspaceName));
        if (request.getType() != null)                  cs.put("type",                 request.getType());
        if (request.getDescription() != null)           cs.put("description",          request.getDescription());
        if (request.getEnabled() != null)               cs.put("enabled",              request.getEnabled());
        if (request.getUrl() != null)                   cs.put("url",                  request.getUrl());
        if (request.getDisableOnConnFailure() != null)  cs.put("disableOnConnFailure", request.getDisableOnConnFailure());
        return serializeToJson(Collections.singletonMap("coverageStore", cs));
    }

    private String buildUpdatePayload(UpdateCoverageStoreRequest request) {
        Map<String, Object> cs = new LinkedHashMap<>();
        if (request.getName() != null)                  cs.put("name",                 request.getName());
        if (request.getDescription() != null)           cs.put("description",          request.getDescription());
        if (request.getEnabled() != null)               cs.put("enabled",              request.getEnabled());
        if (request.getUrl() != null)                   cs.put("url",                  request.getUrl());
        if (request.getDisableOnConnFailure() != null)  cs.put("disableOnConnFailure", request.getDisableOnConnFailure());
        return serializeToJson(Collections.singletonMap("coverageStore", cs));
    }



    private String determineCoverageContentType(String format) {
        if (format == null) return "application/octet-stream";
        switch (format.toLowerCase()) {
            case "geotiff":     return "image/tiff";
            case "worldimage":  return "application/zip";
            case "imagemosaic": return "application/zip";
            default:            return "application/octet-stream";
        }
    }
}
