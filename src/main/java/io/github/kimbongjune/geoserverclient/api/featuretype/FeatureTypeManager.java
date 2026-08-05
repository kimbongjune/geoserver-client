package io.github.kimbongjune.geoserverclient.api.featuretype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureType;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.dto.featuretype.UpdateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.exception.FeatureTypeNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * GeoServer FeatureType REST API manager.
 *
 * <p>A FeatureType is the schema definition for a vector layer — a GeoServer catalog entry for
 * a table or file within a DataStore. Each FeatureType belongs to a specific workspace and
 * DataStore, and defines the SRS, attribute list, bounding box, and projection policy.
 * Creating a FeatureType automatically creates an associated Layer.
 * For DB-based stores (H2, PostGIS, etc.), the underlying table is auto-created if it does not exist.</p>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes
 * GET     /rest/workspaces/{workspaceName}/featuretypes
 * POST    /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes
 * POST    /rest/workspaces/{workspaceName}/featuretypes
 * GET     /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes/{featureTypeName}
 * GET     /rest/workspaces/{workspaceName}/featuretypes/{featureTypeName}
 * PUT     /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes/{featureTypeName}
 * DELETE  /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes/{featureTypeName}
 * PUT     /rest/workspaces/{workspaceName}/featuretypes/{featureTypeName}
 * DELETE  /rest/workspaces/{workspaceName}/featuretypes/{featureTypeName}
 * PUT     /rest/workspaces/{workspaceName}/datastores/{storeName}/featuretypes/{featureTypeName}/reset
 * PUT     /rest/workspaces/{workspaceName}/featuretypes/{featureTypeName}/reset
 * </pre>
 *
 * <p>The workspace-level ({@code storeName}-less) variants above operate across all datastores
 * in the workspace: GeoServer resolves the target store automatically on create (there must be
 * an unambiguous default/only store), and reads/updates/deletes locate the featureType by name
 * regardless of which store it lives in. Verified directly against GeoServer 2.28.2.</p>
 */
public class FeatureTypeManager extends AbstractManager {


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class FeatureTypeEnvelope {
        @JsonProperty("featureType")
        public FeatureType featureType;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class AvailableListEnvelope {
        @JsonProperty("list")
        public AvailableList list;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class AvailableList {
        @JsonProperty("string")
        public List<String> string;
    }

    public FeatureTypeManager(GeoServerHttpClient httpClient,
                              SerializerFactory serializerFactory,
                              DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] Public API

    /**
     * List configured featureTypes in a datastore (name + href summary).
     */
    public List<FeatureTypeSummary> list(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = basePath(workspaceName, storeName) + "?list=configured";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseFeatureTypeConfiguredList(response.getBody());
    }

    /**
     * List available (unconfigured) feature names in a datastore.
     */
    public List<String> listAvailable(String workspaceName, String storeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        String path = basePath(workspaceName, storeName) + "?list=available";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAvailableList(response.getBody());
    }

    /**
     * Get featureType details.
     *
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public FeatureType get(String workspaceName, String storeName, String featureTypeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(featureTypeName, "featureTypeName");
        String path = basePath(workspaceName, storeName) + "/" + featureTypeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, storeName, featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseFeatureType(response.getBody());
    }

    /**
     * Returns true if the featureType exists.
     */
    public boolean exists(String workspaceName, String storeName, String featureTypeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(featureTypeName, "featureTypeName");
        GeoServerResponse response = httpClient.get(
                basePath(workspaceName, storeName) + "/" + featureTypeName,
                "application/json");
        return response.isSuccessful();
    }

    /**
     * Create a new featureType. Also auto-creates an associated Layer.
     *
     * @return the created FeatureType (fetched via GET after POST)
     * @throws ResourceAlreadyExistsException if a featureType with the same name already exists
     */
    public FeatureType create(String workspaceName, String storeName,
                              CreateFeatureTypeRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonNull(request, "request");

        String path = basePath(workspaceName, storeName);
        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        // GeoServer returns 500 (not 409) for duplicate feature type names — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("FeatureType",
                    workspaceName + "/" + storeName + "/" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);
        return get(workspaceName, storeName, request.getName());
    }

    /**
     * Update featureType fields (partial update supported).
     * Use {@link UpdateFeatureTypeRequest#getRecalculate()} to trigger bbox recalculation.
     *
     * @return the updated FeatureType (fetched via GET after PUT)
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public FeatureType update(String workspaceName, String storeName,
                              String featureTypeName, UpdateFeatureTypeRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(featureTypeName, "featureTypeName");
        requireNonNull(request, "request");

        StringBuilder sb = new StringBuilder(basePath(workspaceName, storeName))
                .append('/').append(featureTypeName);
        if (request.getRecalculate() != null && !request.getRecalculate().isEmpty()) {
            sb.append("?recalculate=").append(request.getRecalculate());
        }
        String path = sb.toString();

        String body = buildUpdatePayload(request);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, storeName, featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        String resolvedName = request.getName() != null ? request.getName() : featureTypeName;
        return get(workspaceName, storeName, resolvedName);
    }

    /**
     * Delete featureType with recurse=true (also deletes the associated Layer).
     *
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public void delete(String workspaceName, String storeName, String featureTypeName) {
        delete(workspaceName, storeName, featureTypeName, true);
    }

    /**
     * Delete featureType.
     * If recurse=false and a Layer is associated, GeoServer returns 403.
     *
     * @param recurse true to also delete associated Layers
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public void delete(String workspaceName, String storeName,
                       String featureTypeName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(featureTypeName, "featureTypeName");

        String path = basePath(workspaceName, storeName) + "/" + featureTypeName
                + "?recurse=" + recurse;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, storeName, featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Reset the featureType cache so GeoServer re-reads the schema from the store.
     * PUT and POST behave identically; implemented as PUT. Verified against GeoServer 2.28.2.
     */
    public void reset(String workspaceName, String storeName, String featureTypeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(featureTypeName, "featureTypeName");

        String path = basePath(workspaceName, storeName) + "/" + featureTypeName + "/reset";
        GeoServerResponse response = httpClient.put(path, null, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * List configured featureTypes across the whole workspace (all datastores combined).
     */
    public List<FeatureTypeSummary> listByWorkspace(String workspaceName) {
        requireNonEmpty(workspaceName, "workspaceName");
        String path = basePath(workspaceName);
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseFeatureTypeConfiguredList(response.getBody());
    }

    /**
     * Get featureType details without specifying a store (looked up by name within the workspace).
     *
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public FeatureType getByWorkspace(String workspaceName, String featureTypeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(featureTypeName, "featureTypeName");
        String path = basePath(workspaceName) + "/" + featureTypeName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, "-", featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseFeatureType(response.getBody());
    }

    /**
     * Create a new featureType without specifying a store. GeoServer resolves the target
     * datastore automatically, which requires the workspace to have a single unambiguous
     * (default) datastore. Also auto-creates an associated Layer.
     *
     * @return the created FeatureType (fetched via GET after POST)
     * @throws ResourceAlreadyExistsException if a featureType with the same name already exists
     */
    public FeatureType createByWorkspace(String workspaceName, CreateFeatureTypeRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonNull(request, "request");

        String path = basePath(workspaceName);
        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("FeatureType",
                    workspaceName + "/" + request.getName(), null);
        }
        handleErrorResponse(response, "POST", path);
        return getByWorkspace(workspaceName, request.getName());
    }

    /**
     * Update featureType fields without specifying a store (partial update supported).
     *
     * @return the updated FeatureType (fetched via GET after PUT)
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public FeatureType updateByWorkspace(String workspaceName, String featureTypeName,
                                         UpdateFeatureTypeRequest request) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(featureTypeName, "featureTypeName");
        requireNonNull(request, "request");

        StringBuilder sb = new StringBuilder(basePath(workspaceName)).append('/').append(featureTypeName);
        if (request.getRecalculate() != null && !request.getRecalculate().isEmpty()) {
            sb.append("?recalculate=").append(request.getRecalculate());
        }
        String path = sb.toString();

        String body = buildUpdatePayload(request);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, "-", featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        String resolvedName = request.getName() != null ? request.getName() : featureTypeName;
        return getByWorkspace(workspaceName, resolvedName);
    }

    /**
     * Delete a featureType without specifying a store, with recurse=true (also deletes the associated Layer).
     *
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public void deleteByWorkspace(String workspaceName, String featureTypeName) {
        deleteByWorkspace(workspaceName, featureTypeName, true);
    }

    /**
     * Delete a featureType without specifying a store.
     * If recurse=false and a Layer is associated, GeoServer returns 403.
     *
     * @param recurse true to also delete associated Layers
     * @throws FeatureTypeNotFoundException if the featureType does not exist
     */
    public void deleteByWorkspace(String workspaceName, String featureTypeName, boolean recurse) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(featureTypeName, "featureTypeName");

        String path = basePath(workspaceName) + "/" + featureTypeName + "?recurse=" + recurse;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new FeatureTypeNotFoundException(workspaceName, "-", featureTypeName, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    /**
     * Reset the featureType cache without specifying a store, so GeoServer re-reads the schema
     * from the store. GeoServer accepts both PUT and POST for this action with identical effect;
     * this method uses PUT.
     */
    public void resetByWorkspace(String workspaceName, String featureTypeName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(featureTypeName, "featureTypeName");

        String path = basePath(workspaceName) + "/" + featureTypeName + "/reset";
        GeoServerResponse response = httpClient.put(path, null, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [2] Private helpers

    private String basePath(String ws, String ds) {
        return "/rest/workspaces/" + ws + "/datastores/" + ds + "/featuretypes";
    }

    private String basePath(String ws) {
        return "/rest/workspaces/" + ws + "/featuretypes";
    }

    private String buildCreatePayload(CreateFeatureTypeRequest request) {
        try {
            ObjectNode ft = getObjectMapper().createObjectNode();
            ft.put("name", request.getName());
            if (request.getNativeName() != null)       ft.put("nativeName", request.getNativeName());
            if (request.getTitle() != null)            ft.put("title", request.getTitle());
            if (request.getAbstractText() != null)     ft.put("abstract", request.getAbstractText());
            if (request.getSrs() != null)              ft.put("srs", request.getSrs());
            if (request.getProjectionPolicy() != null) ft.put("projectionPolicy", request.getProjectionPolicy().name());
            if (request.getEnabled() != null)          ft.put("enabled", request.getEnabled());

            if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
                ObjectNode attrs = getObjectMapper().createObjectNode();
                ArrayNode attrArray = getObjectMapper().createArrayNode();
                for (CreateFeatureTypeRequest.AttributeDef a : request.getAttributes()) {
                    ObjectNode attr = getObjectMapper().createObjectNode();
                    attr.put("name", a.getName());
                    attr.put("binding", a.getBinding());
                    if (a.getMinOccurs() != null) attr.put("minOccurs", a.getMinOccurs());
                    if (a.getMaxOccurs() != null) attr.put("maxOccurs", a.getMaxOccurs());
                    if (a.getNillable() != null)  attr.put("nillable", a.getNillable());
                    attrArray.add(attr);
                }
                attrs.set("attribute", attrArray);
                ft.set("attributes", attrs);
            }

            ObjectNode root = getObjectMapper().createObjectNode();
            root.set("featureType", ft);
            return getObjectMapper().writeValueAsString(root);
        } catch (Exception e) {
            throw new SerializationException("Failed to build create FeatureType payload", e);
        }
    }

    private String buildUpdatePayload(UpdateFeatureTypeRequest request) {
        try {
            ObjectNode ft = getObjectMapper().createObjectNode();
            if (request.getName() != null)            ft.put("name", request.getName());
            if (request.getTitle() != null)           ft.put("title", request.getTitle());
            if (request.getAbstractText() != null)    ft.put("abstract", request.getAbstractText());
            if (request.getSrs() != null)             ft.put("srs", request.getSrs());
            if (request.getProjectionPolicy() != null) ft.put("projectionPolicy", request.getProjectionPolicy().name());
            if (request.getEnabled() != null)         ft.put("enabled", request.getEnabled());
            if (request.getMaxFeatures() != null)     ft.put("maxFeatures", request.getMaxFeatures());

            ObjectNode root = getObjectMapper().createObjectNode();
            root.set("featureType", ft);
            return getObjectMapper().writeValueAsString(root);
        } catch (Exception e) {
            throw new SerializationException("Failed to build update FeatureType payload", e);
        }
    }

    private FeatureType parseFeatureType(String body) {
        try {
            FeatureTypeEnvelope envelope = getObjectMapper().readValue(body, FeatureTypeEnvelope.class);
            return envelope.featureType;
        } catch (Exception e) {
            throw new SerializationException("Failed to parse FeatureType response", e);
        }
    }

    private List<FeatureTypeSummary> parseFeatureTypeConfiguredList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode items = root.path("featureTypes").path("featureType");
            if (!items.isArray()) {
                // GeoServer returns {"featureTypes":""} when the store has no feature types
                return Collections.emptyList();
            }
            return getObjectMapper().readValue(
                    items.traverse(),
                    getObjectMapper().getTypeFactory().constructCollectionType(List.class, FeatureTypeSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse FeatureType list response", e);
        }
    }

    private List<String> parseAvailableList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            AvailableListEnvelope envelope = getObjectMapper().readValue(body, AvailableListEnvelope.class);
            if (envelope.list == null || envelope.list.string == null) {
                return Collections.emptyList();
            }
            return envelope.list.string;
        } catch (Exception e) {
            throw new SerializationException("Failed to parse available FeatureType list response", e);
        }
    }
}
