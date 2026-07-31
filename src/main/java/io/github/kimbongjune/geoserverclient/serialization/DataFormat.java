package io.github.kimbongjune.geoserverclient.serialization;

/**
 * Data exchange formats used when talking to the GeoServer REST API.
 * <p>
 * GeoServer itself supports both JSON and XML. This library, however, only supports
 * {@link #JSON} as a client-wide default (see
 * {@link io.github.kimbongjune.geoserverclient.GeoServerClient.Builder#defaultFormat(DataFormat)}) —
 * GeoServer's XML responses collapse a single-item list to a bare element (unlike its JSON
 * responses, which are always arrays), which the shared list-parsing logic used across most
 * managers does not yet handle safely. {@link #XML} remains meaningful for APIs that are
 * explicitly XML-shaped regardless of the client default (e.g. SLD/{@code StyleContent} bodies)
 * and for internal use where a specific manager forces XML to work around a GeoServer
 * server-side bug.
 */
public enum DataFormat {

    /** JSON format ({@code application/json}). */
    JSON("application/json", ".json"),
    /** XML format ({@code application/xml}). */
    XML("application/xml", "");

    private final String contentType;
    private final String pathExtension;

    DataFormat(String contentType, String pathExtension) {
        this.contentType = contentType;
        this.pathExtension = pathExtension;
    }

    /**
     * HTTP Content-Type / Accept header value.
     *
     * @return the content type string
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * URL path extension for format negotiation (e.g., ".json").
     * Empty string for XML (GeoServer default).
     *
     * @return the path extension
     */
    public String getPathExtension() {
        return pathExtension;
    }
}
