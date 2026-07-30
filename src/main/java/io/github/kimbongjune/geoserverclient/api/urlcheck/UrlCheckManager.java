package io.github.kimbongjune.geoserverclient.api.urlcheck;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheck;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckRequest;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckSummary;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.exception.UrlCheckNotFoundException;
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
 * GeoServer URL Checks REST API client.
 *
 * <p>Source classes:
 * <ul>
 *   <li>{@code gs-restconfig-2.28.2.jar -> org/geoserver/rest/UrlCheckController.class}</li>
 *   <li>Model: {@code gs-main-2.28.2.jar -> org/geoserver/security/urlchecks/AbstractURLCheck.class}</li>
 *   <li>Model: {@code gs-main-2.28.2.jar -> org/geoserver/security/urlchecks/RegexURLCheck.class}</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * </pre>
 */
public class UrlCheckManager extends AbstractManager {


    public UrlCheckManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/urlchecks

    /**
     * Returns all registered URL checks.
     *
     * @return list of URL check summaries (never null; empty when none exist)
     */
    public List<UrlCheckSummary> list() {
        GeoServerResponse response = httpClient.get("/rest/urlchecks", "application/json");
        handleErrorResponse(response, "GET", "/rest/urlchecks");
        return parseList(response.getBody());
    }

    // [2] POST /rest/urlchecks

    /**
     * Creates a new URL check (RegexUrlCheck).
     *
     * @param request DTO with name, description, regex, enabled
     * @return the created URL check detail
     * @throws ResourceAlreadyExistsException if a check with the same name already exists (409)
     */
    public UrlCheck create(UrlCheckRequest request) {
        requireNonNull(request, "request");
        requireNonEmpty(request.getName(), "request.name");
        requireNonEmpty(request.getRegex(), "request.regex");

        String body = buildRequestBody(request);
        GeoServerResponse response =
                httpClient.post("/rest/urlchecks", body, "application/json", "application/json");

        if (response.getStatusCode() == 409) {
            throw new ResourceAlreadyExistsException("UrlCheck", request.getName(), response.getBody());
        }
        handleErrorResponse(response, "POST", "/rest/urlchecks");
        return get(request.getName());
    }

    // [3] GET /rest/urlchecks/{name}

    /**
     * Returns the detail of a single URL check.
     *
     * @param name URL check name (required)
     * @return URL check detail
     * @throws UrlCheckNotFoundException if no URL check with the given name exists (404)
     */
    public UrlCheck get(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/urlchecks/" + name;
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new UrlCheckNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseUrlCheck(response.getBody());
    }

    /**
     * Returns true if a URL check with the given name exists.
     *
     * @param name URL check name (required)
     * @return true if found
     */
    public boolean exists(String name) {
        requireNonEmpty(name, "name");
        GeoServerResponse response = httpClient.get("/rest/urlchecks/" + name, "application/json");
        return response.isSuccessful();
    }

    // [4] PUT /rest/urlchecks/{name}

    /**
     * Updates a URL check.
     *
     * @param name    current URL check name (used in path; required)
     * @param request DTO with name, description, regex, enabled
     * @throws UrlCheckNotFoundException if no URL check with the given name exists (404)
     */
    public void update(String name, UrlCheckRequest request) {
        requireNonEmpty(name, "name");
        requireNonNull(request, "request");
        requireNonEmpty(request.getRegex(), "request.regex");

        String path = "/rest/urlchecks/" + name;
        // use the path name in the body to keep them consistent
        UrlCheckRequest bodyRequest = new UrlCheckRequest(
                name, request.getDescription(), request.getRegex(), request.isEnabled());
        String body = buildRequestBody(bodyRequest);
        GeoServerResponse response = httpClient.put(path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new UrlCheckNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);
    }

    // [5] DELETE /rest/urlchecks/{name}

    /**
     * Deletes a URL check.
     *
     * @param name URL check name (required)
     * @throws UrlCheckNotFoundException if no URL check with the given name exists (404)
     */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/urlchecks/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new UrlCheckNotFoundException(name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<UrlCheckSummary> parseList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root  = getObjectMapper().readTree(body);
            JsonNode items = root.path("urlChecks").path("urlCheck");
            if (items.isMissingNode() || items.isNull() || items.isTextual()) {
                // {"urlChecks":""} when empty
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<UrlCheckSummary> result = new ArrayList<>();
                for (JsonNode item : items) {
                    result.add(getObjectMapper().treeToValue(item, UrlCheckSummary.class));
                }
                return result;
            }
            // single item — returned as an object instead of a one-element array
            List<UrlCheckSummary> single = new ArrayList<>();
            single.add(getObjectMapper().treeToValue(items, UrlCheckSummary.class));
            return single;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse urlcheck list", e);
        }
    }

    private UrlCheck parseUrlCheck(String body) {
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path("regexUrlCheck");
            if (node.isMissingNode()) {
                throw new SerializationException("Missing 'regexUrlCheck' key in response", null);
            }
            return getObjectMapper().treeToValue(node, UrlCheck.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse urlcheck response", e);
        }
    }

    // Body builder

    private String buildRequestBody(UrlCheckRequest request) {
        try {
            Map<String, Object> check = new LinkedHashMap<>();
            check.put("name", request.getName());
            if (request.getDescription() != null) {
                check.put("description", request.getDescription());
            }
            check.put("regex", request.getRegex());
            check.put("enabled", request.isEnabled());
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("regexUrlCheck", check);
            return getObjectMapper().writeValueAsString(wrapper);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize urlcheck request", e);
        }
    }
}
