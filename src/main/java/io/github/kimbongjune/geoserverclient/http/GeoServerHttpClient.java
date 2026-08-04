package io.github.kimbongjune.geoserverclient.http;

import java.io.Closeable;
import java.io.File;

/**
 * Abstraction for HTTP communication with GeoServer REST API.
 * <p>
 * Implementations are responsible for:
 * <ul>
 *   <li>Authentication (Basic Auth)</li>
 *   <li>Connection/timeout management</li>
 *   <li>Request/response logging</li>
 * </ul>
 * <p>
 * All methods return {@link GeoServerResponse} regardless of HTTP status code.
 * Error handling (status code → exception mapping) is delegated to the caller
 * (typically {@link io.github.kimbongjune.geoserverclient.api.AbstractManager}).
 */
public interface GeoServerHttpClient extends Closeable {

    /**
     * Performs a GET request.
     *
     * @param path   relative path (e.g., "/rest/workspaces")
     * @param accept Accept header value (e.g., "application/json")
     * @return raw HTTP response
     */
    GeoServerResponse get(String path, String accept);

    /**
     * Performs a POST request with a string body.
     *
     * @param path        relative path
     * @param body        serialized request body (JSON or XML string), may be null
     * @param contentType Content-Type header
     * @param accept      Accept header
     * @return raw HTTP response
     */
    GeoServerResponse post(String path, String body, String contentType, String accept);

    /**
     * Performs a PUT request with a string body.
     *
     * @param path        relative path
     * @param body        serialized request body, may be null
     * @param contentType Content-Type header
     * @param accept      Accept header
     * @return raw HTTP response
     */
    GeoServerResponse put(String path, String body, String contentType, String accept);

    /**
     * Performs a DELETE request.
     *
     * @param path relative path
     * @return raw HTTP response
     */
    GeoServerResponse delete(String path);

    /**
     * Performs a HEAD request. The response body is always empty (per HTTP semantics); callers
     * should inspect {@link GeoServerResponse#getHeaders()} instead.
     *
     * @param path relative path
     * @return raw HTTP response (empty body, headers populated)
     */
    GeoServerResponse head(String path);

    /**
     * Performs a POST with a file body (for file uploads like SHP ZIP, GeoTIFF, SLD).
     *
     * @param path        relative path
     * @param file        file to upload
     * @param contentType Content-Type for the file (e.g., "application/zip")
     * @param accept      Accept header
     * @return raw HTTP response
     */
    GeoServerResponse postFile(String path, File file, String contentType, String accept);

    /**
     * Performs a PUT with a file body (for file uploads).
     *
     * @param path        relative path
     * @param file        file to upload
     * @param contentType Content-Type for the file
     * @param accept      Accept header
     * @return raw HTTP response
     */
    GeoServerResponse putFile(String path, File file, String contentType, String accept);

    /**
     * Performs a GET request and returns the raw binary response body.
     * <p>
     * For 2xx responses the returned {@link GeoServerResponse} carries binary data via
     * {@link GeoServerResponse#getBinaryBody()}. For non-2xx responses the text body is
     * preserved in {@link GeoServerResponse#getBody()} for error-message extraction.
     *
     * @param path   relative path (e.g., "/rest/workspaces/sf/datastores/mystore/file.shp")
     * @param accept Accept header value (e.g., "application/zip")
     * @return raw HTTP response
     */
    GeoServerResponse getBinary(String path, String accept);

    /**
     * Returns the base URL of the GeoServer instance.
     *
     * @return the base URL (e.g. {@code "http://localhost:8080/geoserver"})
     */
    String getBaseUrl();
}
