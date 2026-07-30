package io.github.kimbongjune.geoserverclient.api.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.UserGroupServiceConfig;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.UserGroupServiceNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
 * GeoServer Security User/Group Service REST API client.
 *
 * <h2>Endpoints (5)</h2>
 * <pre>{@code
 * [1] GET    /rest/security/usergroupservices                    List services            200
 * [2] GET    /rest/security/usergroupservices/{serviceName}      Get service config       200 / 404
 * [3] POST   /rest/security/usergroupservices                    Create a service         200
 * [4] PUT    /rest/security/usergroupservices/{serviceName}      Update a service         200
 * [5] DELETE /rest/security/usergroupservices/{serviceName}      Delete a service         200
 * }</pre>
 *
 * <p><b>Protected service:</b> {@code default} cannot be deleted (400).
 * <p><b>GeoServer note:</b> PUT for a non-existent service returns 400 rather than 404
 * (documented in source as intentional to match existing tests/behavior).
 */
public class UserGroupServiceManager extends AbstractManager {

    public UserGroupServiceManager(GeoServerHttpClient httpClient,
                                   SerializerFactory serializerFactory,
                                   DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/usergroupservices

    /** Returns the names of all registered user/group services. */
    @SuppressWarnings("unchecked")
    public List<String> list() {
        String body = doGetRaw("/rest/security/usergroupservices", "application/json");
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map<String, Object> wrapper = (Map<String, Object>) root.get("userGroupServices");
            List<Map<String, Object>> items = wrapper == null
                    ? null : (List<Map<String, Object>>) wrapper.get("userGroupService");
            List<String> names = new ArrayList<String>();
            if (items != null) {
                for (Map<String, Object> item : items) {
                    names.add((String) item.get("name"));
                }
            }
            return names;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse user/group service list response", e);
        }
    }

    // [2] GET /rest/security/usergroupservices/{serviceName}

    /**
     * Returns a specific user/group service's config.
     *
     * @throws UserGroupServiceNotFoundException if no service with the given name exists
     */
    public UserGroupServiceConfig get(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        String path = "/rest/security/usergroupservices/" + serviceName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new UserGroupServiceNotFoundException(serviceName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseSingle(response.getBody());
    }

    // [3] POST /rest/security/usergroupservices

    /**
     * Creates a new user/group service and returns the created config.
     *
     * <p>Duplicate name returns 400 {@code "...because the name is already in use"}.
     *
     * @throws ResourceAlreadyExistsException if the name is already in use
     */
    public UserGroupServiceConfig create(UserGroupServiceConfig config) {
        requireNonNull(config, "config");
        String path = "/rest/security/usergroupservices";
        GeoServerResponse response = httpClient.post(path, buildEnvelope(config), "application/json", "application/json");
        if (isDuplicateNameError(response)) {
            throw new ResourceAlreadyExistsException(
                    "UserGroupService", config.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);
        return parseSingle(response.getBody());
    }

    private boolean isDuplicateNameError(GeoServerResponse response) {
        return response.getStatusCode() == 400
                && response.getBody() != null
                && response.getBody().contains("already in use");
    }

    // [4] PUT /rest/security/usergroupservices/{serviceName}

    /** Updates an existing user/group service. URL {@code serviceName} must match {@code config.getName()}. */
    public void update(String serviceName, UserGroupServiceConfig config) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonNull(config, "config");
        doPutRaw("/rest/security/usergroupservices/" + serviceName, buildEnvelope(config),
                "application/json", "application/json");
    }

    // [5] DELETE /rest/security/usergroupservices/{serviceName}

    /** Deletes a user/group service. {@code default} cannot be deleted (400). */
    public void delete(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        doDelete("/rest/security/usergroupservices/" + serviceName);
    }

    // Envelope helpers

    @SuppressWarnings("unchecked")
    private UserGroupServiceConfig parseSingle(String body) {
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map.Entry<String, Object> entry = root.entrySet().iterator().next();
            UserGroupServiceConfig config = getObjectMapper().convertValue(entry.getValue(), UserGroupServiceConfig.class);
            config.setConfigClass(entry.getKey());
            return config;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse user/group service response", e);
        }
    }

    private String buildEnvelope(UserGroupServiceConfig config) {
        requireNonEmpty(config.getConfigClass(), "config.configClass");
        Map<String, Object> fields = getObjectMapper().convertValue(config, LinkedHashMap.class);
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put(config.getConfigClass(), fields);
        try {
            return getObjectMapper().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize user/group service request", e);
        }
    }

}
