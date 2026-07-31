package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a network-level error prevents communication with GeoServer.
 * Examples: connection refused, timeout, DNS resolution failure.
 */
public class ConnectionException extends GeoServerException {

    private static final long serialVersionUID = 1L;

    private final String url;

    /**
     * Constructs a {@code ConnectionException} for the given URL and root cause.
     *
     * @param url   the GeoServer base URL that could not be reached
     * @param cause the underlying network-level exception
     */
    public ConnectionException(String url, Throwable cause) {
        super("Failed to connect to GeoServer at " + url, cause);
        this.url = url;
    }

    /**
     * Returns the GeoServer URL that could not be reached.
     *
     * @return the target URL
     */
    public String getUrl() {
        return url;
    }
}
