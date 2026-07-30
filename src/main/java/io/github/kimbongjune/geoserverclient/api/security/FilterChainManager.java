package io.github.kimbongjune.geoserverclient.api.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.FilterChainEntry;
import io.github.kimbongjune.geoserverclient.exception.FilterChainNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;



/**
 * GeoServer Security Filter Chain REST API client.
 *
 * <h2>Endpoints</h2>
 * <pre>{@code
 * [1] GET    /rest/security/filterchain                 List all filter chains               200
 * [2] GET    /rest/security/filterchain/{chainName}     Get a specific chain                 200 / 404
 * [3] POST   /rest/security/filterchain                 Create a chain                       201
 * [4] PUT    /rest/security/filterchain/{chainName}     Update a chain (position optional)   200
 * [5] DELETE /rest/security/filterchain/{chainName}     Delete a chain                       200
 * [7] PUT    /rest/security/filterchain/order           Replace the entire chain order       200
 * }</pre>
 *
 * <p><b>Known bug:</b> {@code PUT /rest/security/filterchain} (replace-all endpoint) always
 * returns 500 in GeoServer 2.28.2 due to a Jackson deserialization failure
 * (suspected {@code @JsonAlias} field name conflict). This library does not expose that endpoint.
 *
 * <p>{@code order} is a reserved name and cannot be used as a chain name.
 * See {@link FilterChainEntry} for the full field reference.
 */
public class FilterChainManager extends AbstractManager {

    public FilterChainManager(GeoServerHttpClient httpClient,
                              SerializerFactory serializerFactory,
                              DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/filterchain

    /** Returns all filter chains in matching order. */
    @SuppressWarnings("unchecked")
    public List<FilterChainEntry> list() {
        String body = doGetRaw("/rest/security/filterchain", "application/json");
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            Map<String, Object> wrapper = (Map<String, Object>) root.get("filterchain");
            List<Object> items = wrapper == null ? null : (List<Object>) wrapper.get("filters");
            List<FilterChainEntry> result = new ArrayList<FilterChainEntry>();
            if (items != null) {
                for (Object item : items) {
                    result.add(getObjectMapper().convertValue(item, FilterChainEntry.class));
                }
            }
            return result;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse filter chain list response", e);
        }
    }

    // [2] GET /rest/security/filterchain/{chainName}

    /**
     * Returns a specific filter chain.
     *
     * @throws FilterChainNotFoundException if no chain with the given name exists
     */
    public FilterChainEntry get(String chainName) {
        requireNonEmpty(chainName, "chainName");
        String path = "/rest/security/filterchain/" + chainName;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new FilterChainNotFoundException(chainName, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseSingle(response.getBody());
    }

    // [3] POST /rest/security/filterchain

    /** Creates a new filter chain, appended to the end. */
    public FilterChainEntry create(FilterChainEntry entry) {
        return create(entry, null);
    }

    /**
     * Creates a new filter chain, inserted at the given 0-based position.
     *
     * <p>Duplicate name returns 400 {@code "...because one with that name already exists."}.
     *
     * @throws ResourceAlreadyExistsException if the name already exists
     */
    public FilterChainEntry create(FilterChainEntry entry, Integer position) {
        requireNonNull(entry, "entry");
        String path = "/rest/security/filterchain" + (position != null ? "?position=" + position : "");
        GeoServerResponse response = httpClient.post(path, buildEnvelope(entry), "application/json", "application/json");
        if (isDuplicateNameError(response)) {
            throw new ResourceAlreadyExistsException(
                    "FilterChain", entry.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", path);
        return parseSingle(response.getBody());
    }

    private boolean isDuplicateNameError(GeoServerResponse response) {
        return response.getStatusCode() == 400
                && response.getBody() != null
                && response.getBody().contains("already exists");
    }

    // [4] PUT /rest/security/filterchain/{chainName}

    /** Updates an existing filter chain. URL {@code chainName} must match {@code entry.getName()}. */
    public FilterChainEntry update(String chainName, FilterChainEntry entry) {
        return update(chainName, entry, null);
    }

    /** Updates an existing filter chain and moves it to the given 0-based position. */
    public FilterChainEntry update(String chainName, FilterChainEntry entry, Integer position) {
        requireNonEmpty(chainName, "chainName");
        requireNonNull(entry, "entry");
        String path = "/rest/security/filterchain/" + chainName
                + (position != null ? "?position=" + position : "");
        GeoServerResponse response = httpClient.put(path, buildEnvelope(entry), "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
        return parseSingle(response.getBody());
    }

    // [5] DELETE /rest/security/filterchain/{chainName}

    /** Deletes a filter chain. Non-existent chain returns 410 Gone. */
    public void delete(String chainName) {
        requireNonEmpty(chainName, "chainName");
        doDelete("/rest/security/filterchain/" + chainName);
    }

    // [7] PUT /rest/security/filterchain/order

    /** Replaces the entire chain order. The array must match the current set of chains exactly. */
    public void updateOrder(List<String> order) {
        requireNonNull(order, "order");
        try {
            String body = "{\"order\":" + getObjectMapper().writeValueAsString(order) + "}";
            doPutRaw("/rest/security/filterchain/order", body, "application/json", "application/json");
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize order list", e);
        }
    }

    // Envelope helpers

    @SuppressWarnings("unchecked")
    private FilterChainEntry parseSingle(String body) {
        try {
            Map<String, Object> root = getObjectMapper().readValue(body, Map.class);
            return getObjectMapper().convertValue(root.get("filters"), FilterChainEntry.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse filter chain response", e);
        }
    }

    private String buildEnvelope(FilterChainEntry entry) {
        Map<String, Object> envelope = new LinkedHashMap<String, Object>();
        envelope.put("filters", entry);
        try {
            return getObjectMapper().writeValueAsString(envelope);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize filter chain request", e);
        }
    }

}
