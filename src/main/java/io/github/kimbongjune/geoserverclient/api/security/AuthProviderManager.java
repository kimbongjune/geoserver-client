package io.github.kimbongjune.geoserverclient.api.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.AuthProviderConfig;
import io.github.kimbongjune.geoserverclient.exception.AuthProviderNotFoundException;
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
 * GeoServer Security Authentication Provider REST API client.
 *
 * <h2>Endpoints (6 effective)</h2>
 * <pre>{@code
 * [1] GET    /rest/security/authproviders               List active providers        200
 * [2] GET    /rest/security/authproviders/{name}        Get a specific provider      200 / 404
 * [3] POST   /rest/security/authproviders               Create a provider            201
 * [4] PUT    /rest/security/authproviders/{name}        Update a provider            200
 * [5] DELETE /rest/security/authproviders/{name}        Delete a provider            200
 * [6] PUT    /rest/security/authproviders/order         Replace the active order     200
 * }</pre>
 * {@code order} is a reserved name and cannot be used as a provider name.
 *
 * <p><b>XStream serialization quirk:</b> 1 provider returns an object; 2+ of the same type
 * return a JSON array — {@link #list()} normalizes both shapes to a {@code List}.
 *
 * <p><b>GeoServer bug:</b> deleting a non-existent or disabled provider throws
 * {@code NothingToDelete} internally, but it is caught by a broad {@code catch(Exception)}
 * block and wrapped as {@code CannotSaveConfig}, causing a 500 instead of the expected 410.
 */
public class AuthProviderManager extends AbstractManager {

    public AuthProviderManager(GeoServerHttpClient httpClient,
                               SerializerFactory serializerFactory,
                               DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/authproviders

    /** Returns all active auth providers, normalized to a list regardless of the 1-vs-many XStream shape. */
    @SuppressWarnings("unchecked")
    public List<AuthProviderConfig> list() {
        String body = doGetRaw("/rest/security/authproviders", "application/json");
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map<String, Object> wrapper = (Map<String, Object>) root.get("authproviders");
            List<AuthProviderConfig> result = new ArrayList<AuthProviderConfig>();
            if (wrapper != null) {
                for (Map.Entry<String, Object> entry : wrapper.entrySet()) {
                    if (entry.getValue() instanceof List) {
                        for (Object item : (List<Object>) entry.getValue()) {
                            result.add(toConfig(entry.getKey(), item));
                        }
                    } else {
                        result.add(toConfig(entry.getKey(), entry.getValue()));
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse auth provider list response", e);
        }
    }

    // [2] GET /rest/security/authproviders/{providerName}

    /**
     * Returns a specific auth provider's config.
     *
     * @throws AuthProviderNotFoundException if no provider with the given name exists
     */
    public AuthProviderConfig get(String providerName) {
        requireNonEmpty(providerName, "providerName");
        String path = "/rest/security/authproviders/" + providerName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new AuthProviderNotFoundException(providerName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseSingle(response.getBody());
    }

    // [3] POST /rest/security/authproviders

    /** Creates a new auth provider, appended to the end of the active order. */
    public AuthProviderConfig create(AuthProviderConfig config) {
        return create(config, null);
    }

    /**
     * Creates a new auth provider, inserted at the given 0-based position in the active order.
     *
     * <p>Duplicate name returns 400 {@code "Provider '{name}' already exists"}.
     *
     * @throws ResourceAlreadyExistsException if the name already exists
     */
    public AuthProviderConfig create(AuthProviderConfig config, Integer position) {
        requireNonNull(config, "config");
        String path = "/rest/security/authproviders" + (position != null ? "?position=" + position : "");
        GeoServerResponse response = httpClient.post(path, buildEnvelope(config), "application/json", "application/json");
        if (isDuplicateNameError(response)) {
            throw new ResourceAlreadyExistsException(
                    "AuthProvider", config.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);
        return parseSingle(response.getBody());
    }

    // [4] PUT /rest/security/authproviders/{providerName}

    /** Updates an existing auth provider. URL {@code providerName} must match {@code config.getName()}. */
    public AuthProviderConfig update(String providerName, AuthProviderConfig config) {
        return update(providerName, config, null);
    }

    /** Updates an existing auth provider and moves it to the given 0-based position. */
    public AuthProviderConfig update(String providerName, AuthProviderConfig config, Integer position) {
        requireNonEmpty(providerName, "providerName");
        requireNonNull(config, "config");
        String path = "/rest/security/authproviders/" + providerName
                + (position != null ? "?position=" + position : "");
        GeoServerResponse response = httpClient.put(path, buildEnvelope(config), "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
        return parseSingle(response.getBody());
    }

    // [5] DELETE /rest/security/authproviders/{providerName}

    /** Deletes an auth provider. Deleting a non-existent/disabled provider returns 500 (GeoServer bug). */
    public void delete(String providerName) {
        requireNonEmpty(providerName, "providerName");
        doDelete("/rest/security/authproviders/" + providerName);
    }

    // [6] PUT /rest/security/authproviders/order

    /** Replaces the entire active provider order. Providers omitted from the list become disabled. */
    public void updateOrder(List<String> order) {
        requireNonNull(order, "order");
        String body = "{\"order\":" + toJsonArray(order) + "}";
        doPutRaw("/rest/security/authproviders/order", body, "application/json", "application/json");
    }

    // Envelope helpers

    private AuthProviderConfig toConfig(String configClass, Object rawValue) {
        AuthProviderConfig config = getObjectMapper().convertValue(rawValue, AuthProviderConfig.class);
        config.setConfigClass(configClass);
        return config;
    }

    @SuppressWarnings("unchecked")
    private AuthProviderConfig parseSingle(String body) {
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map.Entry<String, Object> entry = root.entrySet().iterator().next();
            return toConfig(entry.getKey(), entry.getValue());
        } catch (IOException e) {
            throw new SerializationException("Failed to parse auth provider response", e);
        }
    }

    private String buildEnvelope(AuthProviderConfig config) {
        requireNonEmpty(config.getConfigClass(), "config.configClass");
        Map<String, Object> fields = getObjectMapper().convertValue(config, LinkedHashMap.class);
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put(config.getConfigClass(), fields);
        try {
            return getObjectMapper().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize auth provider request", e);
        }
    }

    private String toJsonArray(List<String> values) {
        try {
            return getObjectMapper().writeValueAsString(values);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize order list", e);
        }
    }

    private boolean isDuplicateNameError(GeoServerResponse response) {
        return response.getStatusCode() == 400
                && response.getBody() != null
                && response.getBody().contains("already exists");
    }

}
