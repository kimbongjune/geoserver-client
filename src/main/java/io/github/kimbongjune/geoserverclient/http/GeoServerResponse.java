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

    public GeoServerResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.bodyBytes = null;
        this.headers = headers != null
                ? Collections.unmodifiableMap(headers)
                : Collections.<String, String>emptyMap();
    }

    /** Binary-body constructor — used by {@code getBinary()} for file download responses. */
    public GeoServerResponse(int statusCode, byte[] bodyBytes, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = null;
        this.bodyBytes = bodyBytes;
        this.headers = headers != null
                ? Collections.unmodifiableMap(headers)
                : Collections.<String, String>emptyMap();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    /** Returns the raw binary body, or {@code null} if this is a text response. */
    public byte[] getBinaryBody() {
        return bodyBytes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isNotFound() {
        return statusCode == 404;
    }

    public boolean isUnauthorized() {
        return statusCode == 401;
    }

    public boolean isForbidden() {
        return statusCode == 403;
    }

    @Override
    public String toString() {
        return String.format("GeoServerResponse{statusCode=%d, bodyLength=%d}",
                statusCode, body != null ? body.length() : 0);
    }
}
