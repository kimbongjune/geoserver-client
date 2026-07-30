package io.github.kimbongjune.geoserverclient.api;

import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.JsonSerializer;
import io.github.kimbongjune.geoserverclient.serialization.Serializer;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import java.io.File;
import java.io.IOException;

/**
 * Base class for all GeoServer REST API managers.
 * <p>
 * Provides common CRUD helpers that:
 * <ul>
 *   <li>Serialize request DTOs → wire format (JSON/XML)</li>
 *   <li>Execute HTTP requests via {@link GeoServerHttpClient}</li>
 *   <li>Deserialize response → DTOs</li>
 *   <li>Map HTTP error codes to typed exceptions</li>
 *   <li>Validate required parameters</li>
 * </ul>
 * <p>
 * Subclasses define business methods (e.g., {@code list()}, {@code create()}) and
 * delegate to these helpers for the actual HTTP communication.
 * <p>
 * <b>Thread safety:</b> subclasses must hold only the constructor-injected fields (no per-call
 * mutable instance state), so that any manager can be safely shared and called concurrently
 * across threads — see {@link io.github.kimbongjune.geoserverclient.GeoServerClient} for the full guarantee.
 */
public abstract class AbstractManager {

    protected final GeoServerHttpClient httpClient;
    protected final SerializerFactory serializerFactory;
    protected final DataFormat defaultFormat;

    protected AbstractManager(GeoServerHttpClient httpClient,
                              SerializerFactory serializerFactory,
                              DataFormat defaultFormat) {
        this.httpClient = httpClient;
        this.serializerFactory = serializerFactory;
        this.defaultFormat = defaultFormat;
    }

    //  GET 

    /**
     * Performs a GET request and deserializes the response using the default format.
     */
    protected <T> T doGet(String path, Class<T> responseType) {
        return doGet(path, responseType, defaultFormat);
    }

    /**
     * Performs a GET request and deserializes the response using the specified format.
     */
    protected <T> T doGet(String path, Class<T> responseType, DataFormat format) {
        Serializer serializer = serializerFactory.getSerializer(format);
        GeoServerResponse response = httpClient.get(path, format.getContentType());
        handleErrorResponse(response, "GET", path);
        if (response.getBody() == null || response.getBody().isEmpty()) {
            return null;
        }
        return serializer.deserialize(response.getBody(), responseType);
    }

    /**
     * Performs a GET request, returning {@code null} instead of throwing on 404.
     * Useful for "exists" checks and optional lookups.
     */
    protected <T> T doGetOrNull(String path, Class<T> responseType) {
        return doGetOrNull(path, responseType, defaultFormat);
    }

    protected <T> T doGetOrNull(String path, Class<T> responseType, DataFormat format) {
        Serializer serializer = serializerFactory.getSerializer(format);
        GeoServerResponse response = httpClient.get(path, format.getContentType());
        if (response.isNotFound()) {
            return null;
        }
        handleErrorResponse(response, "GET", path);
        if (response.getBody() == null || response.getBody().isEmpty()) {
            return null;
        }
        return serializer.deserialize(response.getBody(), responseType);
    }

    /**
     * Performs a GET request and returns the raw response body as a String.
     * Does not deserialize. Useful for non-standard response formats.
     */
    protected String doGetRaw(String path, String accept) {
        GeoServerResponse response = httpClient.get(path, accept);
        handleErrorResponse(response, "GET", path);
        return response.getBody();
    }

    //  POST 

    /**
     * Performs a POST with a serialized body. No response body expected.
     */
    protected void doPost(String path, Object body) {
        doPost(path, body, defaultFormat);
    }

    protected void doPost(String path, Object body, DataFormat format) {
        Serializer serializer = serializerFactory.getSerializer(format);
        String content = serializer.serialize(body);
        GeoServerResponse response = httpClient.post(
                path, content, format.getContentType(), format.getContentType());
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Performs a POST and deserializes the response body.
     */
    protected <T> T doPost(String path, Object body, Class<T> responseType) {
        return doPost(path, body, responseType, defaultFormat);
    }

    protected <T> T doPost(String path, Object body, Class<T> responseType, DataFormat format) {
        Serializer serializer = serializerFactory.getSerializer(format);
        String content = serializer.serialize(body);
        GeoServerResponse response = httpClient.post(
                path, content, format.getContentType(), format.getContentType());
        handleErrorResponse(response, "POST", path);
        if (response.getBody() == null || response.getBody().isEmpty()) {
            return null;
        }
        return serializer.deserialize(response.getBody(), responseType);
    }

    /**
     * Performs a POST with a raw string body and specified content type.
     */
    protected void doPostRaw(String path, String body, String contentType, String accept) {
        GeoServerResponse response = httpClient.post(path, body, contentType, accept);
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Performs a file upload via POST.
     */
    protected void doPostFile(String path, File file, String contentType) {
        GeoServerResponse response = httpClient.postFile(
                path, file, contentType, "application/xml");
        handleErrorResponse(response, "POST (file)", path);
    }

    //  PUT 

    /**
     * Performs a PUT with a serialized body. No response body expected.
     */
    protected void doPut(String path, Object body) {
        doPut(path, body, defaultFormat);
    }

    protected void doPut(String path, Object body, DataFormat format) {
        Serializer serializer = serializerFactory.getSerializer(format);
        String content = serializer.serialize(body);
        GeoServerResponse response = httpClient.put(
                path, content, format.getContentType(), format.getContentType());
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * Performs a PUT with a raw string body.
     */
    protected void doPutRaw(String path, String body, String contentType, String accept) {
        GeoServerResponse response = httpClient.put(path, body, contentType, accept);
        handleErrorResponse(response, "PUT", path);
    }

    /**
     * Performs a file upload via PUT.
     */
    protected void doPutFile(String path, File file, String contentType) {
        GeoServerResponse response = httpClient.putFile(
                path, file, contentType, "application/xml");
        handleErrorResponse(response, "PUT (file)", path);
    }

    //  DELETE 

    /**
     * Performs a DELETE request.
     */
    protected void doDelete(String path) {
        GeoServerResponse response = httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    //  Parameter Validation 

    /**
     * Validates that a required string parameter is not null or empty.
     */
    protected void requireNonEmpty(String value, String paramName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidParameterException(paramName, "must not be null or empty");
        }
    }

    /**
     * Validates that a required parameter is not null.
     */
    protected void requireNonNull(Object value, String paramName) {
        if (value == null) {
            throw new InvalidParameterException(paramName, "must not be null");
        }
    }

    //  JSON Serialization

    /** Returns the Jackson ObjectMapper backed by the JSON serializer. */
    protected ObjectMapper getObjectMapper() {
        Serializer serializer = serializerFactory.getSerializer(DataFormat.JSON);
        if (!(serializer instanceof JsonSerializer)) {
            throw new IllegalStateException(
                    "JSON serializer is not a JsonSerializer — check your SerializerFactory: "
                            + serializer.getClass().getName());
        }
        return ((JsonSerializer) serializer).getObjectMapper();
    }

    /** Serializes an object to JSON, throwing {@link SerializationException} on failure. */
    protected String serializeToJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize request payload", e);
        }
    }

    //  Error Handling

    /**
     * Maps HTTP error status codes to typed exceptions.
     * Called after every HTTP request. Successful responses (2xx) pass through.
     */
    protected void handleErrorResponse(GeoServerResponse response, String method, String path) {
        if (response.isSuccessful()) {
            return;
        }

        int code = response.getStatusCode();
        String body = response.getBody();
        String msg = String.format("%s %s failed", method, path);

        switch (code) {
            case 401:
                throw new AuthenticationException(401, "Unauthorized: " + msg, body);
            case 403:
                throw new AuthenticationException(403, "Forbidden: " + msg, body);
            case 404:
                throw new ResourceNotFoundException("Resource", path, body);
            case 405:
                throw new GeoServerResponseException(405, "Method Not Allowed: " + msg, body);
            default:
                throw new GeoServerResponseException(code, msg, body);
        }
    }
}
