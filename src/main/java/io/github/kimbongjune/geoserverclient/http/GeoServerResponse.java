package io.github.kimbongjune.geoserverclient.http;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable representation of an HTTP response from GeoServer.
 */
public class GeoServerResponse {

    private final int statusCode;
    private final String body;
    private final byte[] bodyBytes;
    private final Map<String, String> headers;

    /**
     * Text-body constructor.
     * @param statusCode HTTP status code
     * @param body       text response body, or {@code null}
     * @param headers    response headers, or {@code null} for an empty map
     */
    public GeoServerResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.bodyBytes = null;
        this.headers = headers != null
                ? Collections.unmodifiableMap(headers)
                : Collections.<String, String>emptyMap();
    }

    /**
     * Binary-body constructor — used by {@code getBinary()} for file download responses.
     *
     * @param statusCode HTTP status code
     * @param bodyBytes  raw binary response body
     * @param headers    response headers, or {@code null} for an empty map
     */
    public GeoServerResponse(int statusCode, byte[] bodyBytes, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = null;
        this.bodyBytes = bodyBytes;
        this.headers = headers != null
                ? Collections.unmodifiableMap(headers)
                : Collections.<String, String>emptyMap();
    }

    /** @return the HTTP status code */
    public int getStatusCode() {
        return statusCode;
    }

    /** @return the text response body, or {@code null} for binary responses */
    public String getBody() {
        return body;
    }

    /**
     * Returns the raw binary body, or {@code null} if this is a text response.
     *
     * @return the binary body bytes, or {@code null}
     */
    public byte[] getBinaryBody() {
        return bodyBytes;
    }

    /** @return the response headers */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns the value of the named response header.
     * @param name the header name
     * @return the header value, or {@code null} if absent
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /** @return {@code true} if the status code is 2xx */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /** @return {@code true} if the status code is 404 */
    public boolean isNotFound() {
        return statusCode == 404;
    }

    /** @return {@code true} if the status code is 401 */
    public boolean isUnauthorized() {
        return statusCode == 401;
    }

    /** @return {@code true} if the status code is 403 */
    public boolean isForbidden() {
        return statusCode == 403;
    }

    @Override
    public String toString() {
        return String.format("GeoServerResponse{statusCode=%d, bodyLength=%d}",
                statusCode, body != null ? body.length() : 0);
    }
}
