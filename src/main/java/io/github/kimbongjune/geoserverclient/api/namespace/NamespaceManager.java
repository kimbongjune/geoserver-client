package io.github.kimbongjune.geoserverclient.api.namespace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.Namespace;
import io.github.kimbongjune.geoserverclient.dto.namespace.NamespaceSummary;
import io.github.kimbongjune.geoserverclient.dto.namespace.UpdateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.NamespaceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
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
 * GeoServer Namespace REST API client.
 *
 * <p>A namespace identifies the XML/URI namespace associated with a workspace. Each namespace
 * has a short prefix (matching the workspace name) and a URI. Namespaces and workspaces share a
 * strict 1:1 relationship: creating a namespace also creates the paired workspace, and deleting
 * one removes the other.
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/namespaces/{prefix}
 * PUT     /rest/namespaces/{prefix}
 * DELETE  /rest/namespaces/{prefix}
 * </pre>
 */
public class NamespaceManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class NamespaceEnvelope {
        @JsonProperty("namespace")
        public Namespace namespace;
    }

    public NamespaceManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Public API

    /**
     * Returns all namespaces (summary: name + href only).
     * <p>GeoServer returns {@code {"namespaces":""}} when there are no namespaces;
     * this method normalizes that to an empty list.
     *
     * @return list of namespace summaries, never null
     */
    public List<NamespaceSummary> list() {
        GeoServerResponse response = httpClient.get("/rest/namespaces", "application/json");
        handleErrorResponse(response, "GET", "/rest/namespaces");
        return parseNamespaceList(response.getBody());
    }

    /**
     * Returns the full detail of a namespace.
     *
     * @param prefix namespace prefix (required)
     * @return namespace detail
     * @throws InvalidParameterException  if prefix is null or empty
     * @throws NamespaceNotFoundException if no namespace with the given prefix exists
     */
    public Namespace get(String prefix) {
        requireNonEmpty(prefix, "prefix");
        GeoServerResponse response = httpClient.get(
                "/rest/namespaces/" + prefix, "application/json");
        if (response.isNotFound()) {
            throw new NamespaceNotFoundException(prefix, response.getBody());
        }
        handleErrorResponse(response, "GET", "/rest/namespaces/" + prefix);
        return parseNamespace(response.getBody());
    }

    /**
     * Returns the current default namespace.
     *
     * @return default namespace detail
     */
    public Namespace getDefault() {
        GeoServerResponse response = httpClient.get(
                "/rest/namespaces/default", "application/json");
        handleErrorResponse(response, "GET", "/rest/namespaces/default");
        return parseNamespace(response.getBody());
    }

    /**
     * Returns true if a namespace with the given prefix exists.
     *
     * @param prefix namespace prefix
     * @throws InvalidParameterException if prefix is null or empty
     */
    public boolean exists(String prefix) {
        requireNonEmpty(prefix, "prefix");
        GeoServerResponse response = httpClient.get(
                "/rest/namespaces/" + prefix, "application/json");
        return response.isSuccessful();
    }

    /**
     * Creates a namespace. A workspace with the same prefix is automatically created.
     *
     * <p><b>Note:</b> GeoServer 2.28.2 returns 500 (not 409) for duplicate prefixes;
     * the response body is inspected to detect this case.
     *
     * @param request creation parameters (required)
     * @return the created namespace (via GET after POST)
     * @throws InvalidParameterException      if request or prefix/uri is null/empty
     * @throws ResourceAlreadyExistsException if a namespace with the same prefix already exists
     */
    public Namespace create(CreateNamespaceRequest request) {
        requireNonNull(request, "request");

        String body = buildCreatePayload(request);
        GeoServerResponse response = httpClient.post(
                "/rest/namespaces", body, "application/json", "application/json");
        // GeoServer returns 500 (not 409) for duplicate namespace prefixes — detect from body.
        if (response.getStatusCode() == 500
                && response.getBody() != null
                && response.getBody().contains("already exists")) {
            throw new ResourceAlreadyExistsException("Namespace", request.getPrefix(), null);
        }
        handleErrorResponse(response, "POST", "/rest/namespaces");

        return get(request.getPrefix());
    }

    /**
     * Updates a namespace. Only {@code uri} and {@code isolated} can be changed.
     * <p>The prefix is immutable — do not include it in {@link UpdateNamespaceRequest}.
     *
     * @param prefix  current namespace prefix (required)
     * @param request update parameters (uri/isolated fields)
     * @return the updated namespace (via GET after PUT)
     * @throws InvalidParameterException  if prefix is null/empty or request is null
     * @throws NamespaceNotFoundException if no namespace with the given prefix exists
     */
    public Namespace update(String prefix, UpdateNamespaceRequest request) {
        requireNonEmpty(prefix, "prefix");
        requireNonNull(request, "request");

        String path = "/rest/namespaces/" + prefix;
        String body = buildUpdatePayload(request);

        GeoServerResponse response = httpClient.put(
                path, body, "application/json", "application/json");

        if (response.isNotFound()) {
            throw new NamespaceNotFoundException(prefix, response.getBody());
        }
        handleErrorResponse(response, "PUT", path);

        return get(prefix);
    }

    /**
     * Sets the default namespace.
     *
     * @param prefix namespace prefix to set as default (required)
     * @throws InvalidParameterException  if prefix is null or empty
     * @throws NamespaceNotFoundException if no namespace with the given prefix exists
     */
    public void setDefault(String prefix) {
        requireNonEmpty(prefix, "prefix");
        // Validate existence upfront — gives a clear error instead of a cryptic GeoServer response
        get(prefix);

        String body = buildPrefixOnlyPayload(prefix);
        GeoServerResponse response = httpClient.put(
                "/rest/namespaces/default", body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", "/rest/namespaces/default");
    }

    /**
     * Deletes a namespace. The paired workspace is also deleted.
     *
     * @param prefix  namespace prefix (required)
     * @throws InvalidParameterException  if prefix is null or empty
     * @throws NamespaceNotFoundException if no namespace with the given prefix exists (404)
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *                                   if the namespace has associated resources (403)
     */
    public void delete(String prefix) {
        requireNonEmpty(prefix, "prefix");

        String path = "/rest/namespaces/" + prefix;
        GeoServerResponse response = httpClient.delete(path);

        if (response.isNotFound()) {
            throw new NamespaceNotFoundException(prefix, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<NamespaceSummary> parseNamespaceList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode items = root.path("namespaces").path("namespace");
            if (!items.isArray()) {
                // GeoServer returns {"namespaces":""} when empty
                return Collections.emptyList();
            }
            return om.readValue(items.traverse(),
                    om.getTypeFactory().constructCollectionType(List.class, NamespaceSummary.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse namespace list response", e);
        }
    }

    private Namespace parseNamespace(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            NamespaceEnvelope envelope = om.readValue(body, NamespaceEnvelope.class);
            return envelope.namespace;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse namespace response", e);
        }
    }

    // Payload builders

    private String buildCreatePayload(CreateNamespaceRequest request) {
        Map<String, Object> ns = new LinkedHashMap<>();
        ns.put("prefix", request.getPrefix());
        ns.put("uri",    request.getUri());
        if (request.getIsolated() != null) {
            ns.put("isolated", request.getIsolated());
        }
        return serializeToJson(Collections.singletonMap("namespace", ns));
    }

    private String buildUpdatePayload(UpdateNamespaceRequest request) {
        Map<String, Object> ns = new LinkedHashMap<>();
        if (request.getUri() != null) {
            ns.put("uri", request.getUri());
        }
        if (request.getIsolated() != null) {
            ns.put("isolated", request.getIsolated());
        }
        return serializeToJson(Collections.singletonMap("namespace", ns));
    }

    private String buildPrefixOnlyPayload(String prefix) {
        return serializeToJson(Collections.singletonMap("namespace",
                Collections.singletonMap("prefix", prefix)));
    }


}
