package io.github.kimbongjune.geoserverclient.api.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.AuthFilterConfig;
import io.github.kimbongjune.geoserverclient.exception.AuthFilterNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
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
 * GeoServer Security Authentication Filter REST API client.
 *
 * <h2>Endpoints (5)</h2>
 * <pre>{@code
 * [1] GET    /rest/security/authfilters                 List all auth filters     200
 * [2] GET    /rest/security/authfilters/{filterName}    Get a specific filter     200
 * [3] POST   /rest/security/authfilters                 Create a filter           200
 * [4] PUT    /rest/security/authfilters/{filterName}    Update a filter           200
 * [5] DELETE /rest/security/authfilters/{filterName}    Delete a filter           200
 * }</pre>
 *
 * <p><b>Delete blacklist:</b> {@code anonymous}, {@code basic}, {@code form}, {@code rememberme} —
 * these built-in filters cannot be deleted; attempting to do so returns 400.
 *
 * <p><b>GeoServer bug:</b> querying an unknown filter name returns 200 with {@code {"null":""}}
 * instead of 404. This library normalizes that response to
 * {@link AuthFilterNotFoundException} so callers do not see the raw 200/null quirk.
 *
 * <p>Response JSON top-level key is the filter config class FQCN — see {@link AuthFilterConfig}.
 */
public class AuthFilterManager extends AbstractManager {

    public AuthFilterManager(GeoServerHttpClient httpClient,
                             SerializerFactory serializerFactory,
                             DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/authfilters

    /** Returns the names of all registered authentication filters. */
    @SuppressWarnings("unchecked")
    public List<String> list() {
        String body = doGetRaw("/rest/security/authfilters", "application/json");
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map<String, Object> wrapper = (Map<String, Object>) root.get("authfilters");
            List<Map<String, Object>> items = wrapper == null
                    ? null : (List<Map<String, Object>>) wrapper.get("authfilter");
            List<String> names = new ArrayList<String>();
            if (items != null) {
                for (Map<String, Object> item : items) {
                    names.add((String) item.get("name"));
                }
            }
            return names;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse auth filter list response", e);
        }
    }

    // [2] GET /rest/security/authfilters/{filterName}

    /**
     * Returns a specific auth filter's full config.
     *
     * <p>GeoServer returns 200 with {@code {"null":""}} for unknown names instead of 404 —
     * this method detects that and throws {@link AuthFilterNotFoundException} like every other
     * manager's {@code get()}, instead of leaking the raw 200/null quirk to the caller.
     *
     * @throws AuthFilterNotFoundException if no filter with the given name exists
     */
    public AuthFilterConfig get(String filterName) {
        requireNonEmpty(filterName, "filterName");
        String body = doGetRaw("/rest/security/authfilters/" + filterName, "application/json");
        AuthFilterConfig config = parseSingle(body);
        if (config == null) {
            throw new AuthFilterNotFoundException(filterName, body);
        }
        return config;
    }

    // [3] POST /rest/security/authfilters

    /**
     * Creates a new auth filter and returns the created config.
     *
     * <p>Duplicate name returns 400 {@code "...because the name is already in use"}.
     *
     * @throws ResourceAlreadyExistsException if the name is already in use
     */
    public AuthFilterConfig create(AuthFilterConfig config) {
        requireNonNull(config, "config");
        String path = "/rest/security/authfilters";
        GeoServerResponse response = httpClient.post(path, buildEnvelope(config), "application/json", "application/json");
        if (isDuplicateNameError(response)) {
            throw new ResourceAlreadyExistsException(
                    "AuthFilter", config.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);
        return parseSingle(response.getBody());
    }

    private boolean isDuplicateNameError(GeoServerResponse response) {
        return response.getStatusCode() == 400
                && response.getBody() != null
                && response.getBody().contains("already in use");
    }

    // [4] PUT /rest/security/authfilters/{filterName}

    /** Updates an existing auth filter. URL {@code filterName} must match {@code config.getName()}. */
    public void update(String filterName, AuthFilterConfig config) {
        requireNonEmpty(filterName, "filterName");
        requireNonNull(config, "config");
        doPutRaw("/rest/security/authfilters/" + filterName, buildEnvelope(config),
                "application/json", "application/json");
    }

    // [5] DELETE /rest/security/authfilters/{filterName}

    /**
     * Deletes an auth filter. Built-in filters (anonymous, basic, form, rememberme)
     * cannot be deleted (400).
     */
    public void delete(String filterName) {
        requireNonEmpty(filterName, "filterName");
        doDelete("/rest/security/authfilters/" + filterName);
    }

    // Envelope helpers

    @SuppressWarnings("unchecked")
    private AuthFilterConfig parseSingle(String body) {
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map.Entry<String, Object> entry = root.entrySet().iterator().next();
            if (entry.getValue() == null || "".equals(entry.getValue())) {
                return null; // {"null":""} — unknown filter name (GeoServer bug)
            }
            AuthFilterConfig config = getObjectMapper().convertValue(entry.getValue(), AuthFilterConfig.class);
            config.setConfigClass(entry.getKey());
            return config;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse auth filter response", e);
        }
    }

    private String buildEnvelope(AuthFilterConfig config) {
        requireNonEmpty(config.getConfigClass(), "config.configClass");
        Map<String, Object> fields = getObjectMapper().convertValue(config, LinkedHashMap.class);
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put(config.getConfigClass(), fields);
        try {
            return getObjectMapper().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize auth filter request", e);
        }
    }

}
