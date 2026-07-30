package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a network-level error prevents communication with GeoServer.
 * Examples: connection refused, timeout, DNS resolution failure.
 */
public class ConnectionException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final String url;

    public ConnectionException(String url, Throwable cause) {
        super("Failed to connect to GeoServer at " + url, cause);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
