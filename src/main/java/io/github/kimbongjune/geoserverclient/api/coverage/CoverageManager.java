package io.github.kimbongjune.geoserverclient.api.coverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.coverage.Coverage;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.coverage.CreateCoverageRequest;
import io.github.kimbongjune.geoserverclient.dto.coverage.UpdateCoverageRequest;
import io.github.kimbongjune.geoserverclient.exception.CoverageNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
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
 * GeoServer Coverage REST API manager.
 *
 * <p>A Coverage is a raster layer entry served from a CoverageStore.
 * A single CoverageStore may contain one or more Coverages; each Coverage stores
 * metadata such as SRS, BoundingBox, Dimensions, and format.
 * When a Coverage is created, a Layer is automatically created at the same time.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/workspaces/{workspaceName}/coveragestores/{storeName}/coverages
 * GET     /rest/workspaces/{workspaceName}/coveragestores/{storeName}/coverages/{coverageName}
 * POST    /rest/workspaces/{workspaceName}/coveragestores/{storeName}/coverages
 * PUT     /rest/workspaces/{workspaceName}/coveragestores/{storeName}/coverages/{coverageName}
 * DELETE  /rest/workspaces/{workspaceName}/coveragestores/{storeName}/coverages/{coverageName}
 * </pre>
 */
public class CoverageManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class CoverageEnvelope {
        @JsonProperty("coverage")
        public Coverage coverage;
    }

    public CoverageManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Public API

    /**
     * List coverages within a specific coverage store.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @return coverage summary list, never null
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public List<CoverageSummary> list(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseCoverageList(response.getBody(), "coverage");
    }

    /**
     * List all coverages in a workspace (across all stores).
     *
     * @param workspaceName workspace name (required)
     * @return coverage summary list, never null
     * @throws InvalidParameterException if workspaceName is null/empty
     */
    public List<CoverageSummary> listByWorkspace(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = "/rest/workspaces/" + workspaceName + "/coverages";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseCoverageList(response.getBody(), "coverage");
    }

    /**
     * List native coverage names directly from the store reader (list=all).
     *
     * <p>May differ from coverages registered in the catalog.
     * Response format: {@code {"list": {"string": ["name1", "name2"]}}}</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @return native coverage name list, never null
     * @throws InvalidParameterException if workspaceName or storeName is null/empty
     */
    public List<String> listNative(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages?list=all";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseNativeList(response.getBody());
    }

    /**
     * Get coverage details (store path).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @return coverage details
     * @throws InvalidParameterException  if any parameter is null/empty
     * @throws CoverageNotFoundException  if the coverage does not exist
     */
    public Coverage get(String workspaceName, String storeName, String coverageName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");
        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages/" + coverageName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new CoverageNotFoundException(workspaceName, storeName, coverageName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseCoverage(response.getBody());
    }

    /**
     * Get coverage details (workspace path — without store name).
     *
     * @param workspaceName workspace name (required)
     * @param coverageName  coverage name (required)
     * @return coverage details
     * @throws InvalidParameterException  if any parameter is null/empty
     * @throws CoverageNotFoundException  if the coverage does not exist
     */
    public Coverage getByWorkspace(String workspaceName, String coverageName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(coverageName, "coverageName");
        String path = "/rest/workspaces/" + workspaceName + "/coverages/" + coverageName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new CoverageNotFoundException(workspaceName, "-", coverageName,
                    response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseCoverage(response.getBody());
    }

    /**
     * Check whether a coverage exists.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @throws InvalidParameterException if any parameter is null/empty
     */
    public boolean exists(String workspaceName, String storeName, String coverageName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");
        GeoServerResponse response = httpClient.get(
                "/rest/workspaces/" + workspaceName
                        + "/coveragestores/" + storeName + "/coverages/" + coverageName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Register a new coverage (add to catalog). On POST success a Layer is also created automatically.
     *
     * <p>isNew mode: specifying only name causes the reader to auto-build the metadata.
     * Used to add a coverage to a store uploaded with configure=none.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param request       creation parameters (required, name is required)
     * @return created coverage (GET after POST)
     * @throws InvalidParameterException      if any parameter is null/empty
     * @throws ResourceAlreadyExistsException if a coverage with the same name already exists
     */
    public Coverage create(String workspaceName, String storeName, CreateCoverageRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages";
        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(
                path, body, "application/json", "application/json");
        // GeoServer returns 500 (not 409) for duplicate coverage names — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("Coverage",
                    workspaceName + "/" + storeName + "/" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);

        return get(workspaceName, storeName, request.getName());
    }

    /**
     * Update coverage attributes. Partial update is supported.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  current name of the coverage to update (required)
     * @param request       update parameters (required, at least one field required)
     * @return updated coverage (GET after PUT)
     * @throws InvalidParameterException  if any parameter is null/empty
     * @throws CoverageNotFoundException  if the coverage does not exist
     */
    public Coverage update(String workspaceName, String storeName, String coverageName,
                           UpdateCoverageRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");
        requireNonNull(request, "request");

        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages/" + coverageName;
        String body = buildUpdatePayload(request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new CoverageNotFoundException(workspaceName, storeName, coverageName,
                    response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        // If the name was changed, look up by the new name
        String resolvedName = request.getName() != null ? request.getName() : coverageName;
        return get(workspaceName, storeName, resolvedName);
    }

    /**
     * Delete a coverage with recurse=false (returns 403 if layers exist).
     *
     * <p>If layers are attached to this coverage, GeoServer returns 403.
     * Use {@link #delete(String, String, String, boolean) delete(..., true)} to also delete layers.</p>
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name to delete (required)
     * @throws InvalidParameterException  if any parameter is null/empty
     * @throws CoverageNotFoundException  if the coverage does not exist (404)
     */
    public void delete(String workspaceName, String storeName, String coverageName) {
        delete(workspaceName, storeName, coverageName, false);
    }

    /**
     * Delete a coverage with an explicit recurse flag.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name to delete (required)
     * @param recurse       when true, also deletes all layers using this coverage;
     *                      when false, returns 403 if any layers exist.
     * @throws InvalidParameterException  if any parameter is null/empty
     * @throws CoverageNotFoundException  if the coverage does not exist (404)
     */
    public void delete(String workspaceName, String storeName, String coverageName,
                       boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");

        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages/" + coverageName
                + "?recurse=" + recurse;

        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new CoverageNotFoundException(workspaceName, storeName, coverageName,
                    response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Reset the coverage resource pool cache.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @throws InvalidParameterException  if any parameter is null/empty
     */
    public void reset(String workspaceName, String storeName, String coverageName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");

        String path = "/rest/workspaces/" + workspaceName
                + "/coveragestores/" + storeName + "/coverages/" + coverageName + "/reset";
        GeoServerResponse response = httpClient.put(path, null, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // Parsing helpers

    private List<CoverageSummary> parseCoverageList(String body, String itemKey) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("coverages").path(itemKey);
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(
                            List.class, CoverageSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse coverage list response", e);
        }
    }

    private List<String> parseNativeList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode strings = root.path("list").path("string");
            if (!strings.isArray()) {
                if (strings.isTextual()) {
                    return Collections.singletonList(strings.asText());
                }
                return Collections.emptyList();
            }
            List<String> result = new ArrayList<>();
            strings.forEach(n -> result.add(n.asText()));
            return result;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse native coverage list response", e);
        }
    }

    private Coverage parseCoverage(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            CoverageEnvelope envelope = om.readValue(body, CoverageEnvelope.class);
            return envelope.coverage;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse coverage response", e);
        }
    }

    // Payload builders

    private String buildCreatePayload(CreateCoverageRequest request) {
        Map<String, Object> cov = new LinkedHashMap<>();
        cov.put("name", request.getName());
        // If nativeCoverageName is not set, default to name — omitting it may cause GeoServer 500
        String nativeCovName = request.getNativeCoverageName() != null
                ? request.getNativeCoverageName() : request.getName();
        cov.put("nativeCoverageName", nativeCovName);
        if (request.getNativeName() != null)         cov.put("nativeName",         request.getNativeName());
        if (request.getTitle() != null)              cov.put("title",              request.getTitle());
        if (request.getDescription() != null)        cov.put("description",        request.getDescription());
        if (request.getSrs() != null)                cov.put("srs",                request.getSrs());
        if (request.getProjectionPolicy() != null)   cov.put("projectionPolicy",   request.getProjectionPolicy());
        if (request.getEnabled() != null)            cov.put("enabled",            request.getEnabled());
        if (request.getNativeFormat() != null)       cov.put("nativeFormat",       request.getNativeFormat());
        return serializeToJson(Collections.singletonMap("coverage", cov));
    }

    private String buildUpdatePayload(UpdateCoverageRequest request) {
        Map<String, Object> cov = new LinkedHashMap<>();
        if (request.getName() != null)                      cov.put("name",                      request.getName());
        if (request.getTitle() != null)                     cov.put("title",                     request.getTitle());
        if (request.getDescription() != null)               cov.put("description",               request.getDescription());
        if (request.getSrs() != null)                       cov.put("srs",                       request.getSrs());
        if (request.getProjectionPolicy() != null)          cov.put("projectionPolicy",          request.getProjectionPolicy());
        if (request.getEnabled() != null)                   cov.put("enabled",                   request.getEnabled());
        if (request.getAdvertised() != null)                cov.put("advertised",                request.getAdvertised());
        if (request.getDefaultInterpolationMethod() != null) cov.put("defaultInterpolationMethod", request.getDefaultInterpolationMethod());
        return serializeToJson(Collections.singletonMap("coverage", cov));
    }


}
