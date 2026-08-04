package io.github.kimbongjune.geoserverclient.api.transform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.transform.CreateTransformRequest;
import io.github.kimbongjune.geoserverclient.dto.transform.Transform;
import io.github.kimbongjune.geoserverclient.dto.transform.UpdateTransformRequest;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
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
 * GeoServer XSLT Transform REST API client.
 *
 * <p>Source controller (plugin-only):
 * {@code src/wfs/.../org/geoserver/wfs/xslt/rest/TransformController.java}
 * — activated only when the XSLT plugin is installed
 * ({@code @RequestMapping} path = "/services/wfs/transforms")
 *
 *
 * <h2>Plugin availability</h2>
 * <ul>
 *   <li>Stable plugins (SourceForge): {@code geoserver-2.28.2-xslt-plugin.zip} not found -> 404</li>
 *   <li>Community plugins (build.geoserver.org/geoserver/2.28.x/community-latest/): {@code xslt-plugin.zip} not listed</li>
 *   <li>Conclusion: XSLT Transform plugin is not supported on GeoServer 2.28.x</li>
 * </ul>
 *
 * <h2>Endpoint summary</h2>
 * <pre>{@code
 * [1] GET    /rest/services/wfs/transforms            200 (plugin installed) / 404 (not installed)
 * [2] GET    /rest/services/wfs/transforms/{name}     200 / 404
 * [3] POST   /rest/services/wfs/transforms            201
 * [4] PUT    /rest/services/wfs/transforms/{name}     200 / 404
 * [5] DELETE /rest/services/wfs/transforms/{name}     200 / 404
 *
 * Verified (2026-04-02):
 *   (XSLT plugin not installed -- isAvailable() always returns false)
 * }</pre>
 *
 * @author nocdev
 * @since 1.0.0
 */
public class TransformManager extends AbstractManager {

    /**
     * Constructs a new TransformManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public TransformManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    /**
     * Checks whether the XSLT Transform plugin is installed on the server.
     *
     * <p>Returns {@code true} if {@code GET /rest/services/wfs/transforms} responds with 200.
     * Returns {@code false} on 404 (plugin not installed) or any other error.
     *
     * <p>On GeoServer 2.28.x (stable and community), the XSLT plugin is not available,
     * so this method always returns {@code false}.
     *
     * @return {@code true} if XSLT Transform extension is available on the server
     */
    public boolean isAvailable() {
        try {
            GeoServerResponse response = httpClient.get(
                    "/rest/services/wfs/transforms", "application/json");
            return response != null && response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lists configured XSLT transforms.
     *
     * <p><b>Unverified:</b> see class Javadoc — the XSLT plugin is unavailable on every
     * GeoServer 2.28.x instance this library was tested against, so the exact response envelope
     * could not be confirmed. Implemented per GeoServer's general REST list-collection convention
     * (used consistently elsewhere in this library, e.g. styles/templates/urlchecks).
     *
     * @return transform names, or an empty list if none / plugin not installed
     */
    public List<String> list() {
        GeoServerResponse response = httpClient.get("/rest/services/wfs/transforms", "application/json");
        if (response.isNotFound()) {
            return Collections.emptyList();
        }
        handleErrorResponse(response, "GET", "/rest/services/wfs/transforms");
        return parseTransformList(response.getBody());
    }

    /**
     * Gets a transform by name.
     *
     * <p><b>Unverified:</b> see class Javadoc.
     *
     * @throws ResourceNotFoundException if the transform does not exist (or plugin not installed)
     */
    public Transform get(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/services/wfs/transforms/" + name;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("Transform", name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseTransform(response.getBody());
    }

    /**
     * Creates a new XSLT transform.
     *
     * <p><b>Unverified:</b> see class Javadoc.
     *
     * @throws ResourceAlreadyExistsException if a transform with the same name already exists
     */
    public Transform create(CreateTransformRequest request) {
        requireNonNull(request, "request");
        String path = "/rest/services/wfs/transforms";
        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("Transform", request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);
        return get(request.getName());
    }

    /**
     * Updates an existing XSLT transform. Partial update supported.
     *
     * <p><b>Unverified:</b> see class Javadoc.
     *
     * @throws ResourceNotFoundException if the transform does not exist
     */
    public Transform update(String name, UpdateTransformRequest request) {
        requireNonEmpty(name, "name");
        requireNonNull(request, "request");
        String path = "/rest/services/wfs/transforms/" + name;
        String body = buildUpdatePayload(request);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("Transform", name, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
        return get(name);
    }

    /**
     * Deletes a transform by name.
     *
     * <p><b>Unverified:</b> see class Javadoc.
     *
     * @throws ResourceNotFoundException if the transform does not exist
     */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/services/wfs/transforms/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("Transform", name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing / payload helpers

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class TransformEnvelope {
        @JsonProperty("transform")
        public Transform transform;
    }

    private Transform parseTransform(String body) {
        try {
            return getObjectMapper().readValue(body, TransformEnvelope.class).transform;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse transform response", e);
        }
    }

    private List<String> parseTransformList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("transforms").path("transform");
            if (!items.isArray()) {
                return Collections.emptyList();
            }
            List<String> names = new java.util.ArrayList<>();
            for (JsonNode item : items) {
                JsonNode nameNode = item.path("name");
                if (nameNode.isTextual()) {
                    names.add(nameNode.asText());
                }
            }
            return names;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse transform list response", e);
        }
    }

    private String buildCreatePayload(CreateTransformRequest request) {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("name", request.getName());
        if (request.getFeatureType() != null)     t.put("featureType",     request.getFeatureType());
        if (request.getOutputFormat() != null)     t.put("outputFormat",    request.getOutputFormat());
        if (request.getOutputMimeType() != null)   t.put("outputMimeType",  request.getOutputMimeType());
        if (request.getXslt() != null)             t.put("xslt",            request.getXslt());
        return serializeToJson(Collections.singletonMap("transform", t));
    }

    private String buildUpdatePayload(UpdateTransformRequest request) {
        Map<String, Object> t = new LinkedHashMap<>();
        if (request.getFeatureType() != null)     t.put("featureType",     request.getFeatureType());
        if (request.getOutputFormat() != null)     t.put("outputFormat",    request.getOutputFormat());
        if (request.getOutputMimeType() != null)   t.put("outputMimeType",  request.getOutputMimeType());
        if (request.getXslt() != null)             t.put("xslt",            request.getXslt());
        return serializeToJson(Collections.singletonMap("transform", t));
    }
}
