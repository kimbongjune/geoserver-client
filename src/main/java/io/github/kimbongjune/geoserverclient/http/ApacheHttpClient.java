package io.github.kimbongjune.geoserverclient.http;

import io.github.kimbongjune.geoserverclient.exception.ConnectionException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * Apache HttpClient 5 implementation of {@link GeoServerHttpClient}.
 * <p>
 * Uses Basic Authentication with pre-emptive header injection (no 401 challenge round-trip).
 * Timeouts and connection pool size are configurable via constructor parameters.
 * <p>
 * <b>Thread safety:</b> a single instance wraps one pooled {@link CloseableHttpClient} and holds
 * no other mutable state, so it is safe to share across threads. Create one instance per
 * GeoServer endpoint (typically via {@link io.github.kimbongjune.geoserverclient.GeoServerClient#builder()})
 * and reuse it — do not construct a new client per request.
 */
public class ApacheHttpClient implements GeoServerHttpClient {

    private static final Logger log = LoggerFactory.getLogger(ApacheHttpClient.class);

    /** Default max concurrent connections. Apache HttpClient's own default (2 per route) is too
     *  low for a client that talks to a single GeoServer host under any real concurrency. */
    public static final int DEFAULT_MAX_CONNECTIONS = 50;

    private final String baseUrl;
    private final String authHeader;
    private final CloseableHttpClient httpClient;

    /**
     * Creates a new Apache HTTP client for GeoServer communication with the default
     * connection pool size ({@value #DEFAULT_MAX_CONNECTIONS}).
     *
     * @param baseUrl           GeoServer base URL (e.g., "http://localhost:8080/geoserver")
     * @param username          GeoServer username
     * @param password          GeoServer password
     * @param connectTimeoutMs  connection timeout in milliseconds
     * @param responseTimeoutMs response timeout in milliseconds
     */
    public ApacheHttpClient(String baseUrl, String username, String password,
                            int connectTimeoutMs, int responseTimeoutMs) {
        this(baseUrl, username, password, connectTimeoutMs, responseTimeoutMs, DEFAULT_MAX_CONNECTIONS);
    }

    /**
     * Creates a new Apache HTTP client for GeoServer communication.
     *
     * @param baseUrl           GeoServer base URL (e.g., "http://localhost:8080/geoserver")
     * @param username          GeoServer username
     * @param password          GeoServer password
     * @param connectTimeoutMs  connection timeout in milliseconds
     * @param responseTimeoutMs response timeout in milliseconds
     * @param maxConnections    max concurrent connections to the GeoServer host (this client only
     *                          ever talks to one host, so total and per-route limits are the same)
     */
    public ApacheHttpClient(String baseUrl, String username, String password,
                            int connectTimeoutMs, int responseTimeoutMs, int maxConnections) {
        this(baseUrl, username, password, connectTimeoutMs, responseTimeoutMs, maxConnections, null);
    }

    /**
     * Creates a new Apache HTTP client for GeoServer communication with a custom TLS context.
     *
     * @param baseUrl           GeoServer base URL (e.g., "https://geoserver.internal/geoserver")
     * @param username          GeoServer username
     * @param password          GeoServer password
     * @param connectTimeoutMs  connection timeout in milliseconds
     * @param responseTimeoutMs response timeout in milliseconds
     * @param maxConnections    max concurrent connections to the GeoServer host
     * @param sslContext        TLS context for HTTPS connections (e.g. one trusting a
     *                          self-signed certificate), or {@code null} for the JVM default
     */
    public ApacheHttpClient(String baseUrl, String username, String password,
                            int connectTimeoutMs, int responseTimeoutMs, int maxConnections,
                            SSLContext sslContext) {
        this.baseUrl = normalizeUrl(baseUrl);
        this.authHeader = createBasicAuthHeader(username, password);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeoutMs))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .build();

        PoolingHttpClientConnectionManagerBuilder connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(maxConnections)
                        .setMaxConnPerRoute(maxConnections);
        if (sslContext != null) {
            connectionManager.setTlsSocketStrategy(new DefaultClientTlsStrategy(sslContext));
        }

        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager.build())
                .setDefaultRequestConfig(config)
                .build();
    }

    //  HTTP Methods 

    @Override
    public GeoServerResponse get(String path, String accept) {
        String url = buildUrl(path);
        log.debug("GET {}", url);
        HttpGet request = applyHeaders(new HttpGet(url), null, accept);
        return execute(request);
    }

    @Override
    public GeoServerResponse post(String path, String body, String contentType, String accept) {
        String url = buildUrl(path);
        log.debug("POST {}", url);
        HttpPost request = applyHeaders(new HttpPost(url), contentType, accept);
        if (body != null) {
            request.setEntity(new StringEntity(body,
                    ContentType.create(contentType, StandardCharsets.UTF_8)));
        }
        return execute(request);
    }

    @Override
    public GeoServerResponse put(String path, String body, String contentType, String accept) {
        String url = buildUrl(path);
        log.debug("PUT {}", url);
        HttpPut request = applyHeaders(new HttpPut(url), contentType, accept);
        if (body != null) {
            request.setEntity(new StringEntity(body,
                    ContentType.create(contentType, StandardCharsets.UTF_8)));
        }
        return execute(request);
    }

    @Override
    public GeoServerResponse delete(String path) {
        String url = buildUrl(path);
        log.debug("DELETE {}", url);
        HttpDelete request = applyHeaders(new HttpDelete(url), null, null);
        return execute(request);
    }

    @Override
    public GeoServerResponse head(String path) {
        String url = buildUrl(path);
        log.debug("HEAD {}", url);
        HttpHead request = applyHeaders(new HttpHead(url), null, null);
        return execute(request);
    }

    @Override
    public GeoServerResponse postFile(String path, File file, String contentType, String accept) {
        String url = buildUrl(path);
        log.debug("POST (file) {} [{}]", url, file.getName());
        HttpPost request = applyHeaders(new HttpPost(url), contentType, accept);
        request.setEntity(new FileEntity(file, ContentType.create(contentType)));
        return execute(request);
    }

    @Override
    public GeoServerResponse putFile(String path, File file, String contentType, String accept) {
        String url = buildUrl(path);
        log.debug("PUT (file) {} [{}]", url, file.getName());
        HttpPut request = applyHeaders(new HttpPut(url), contentType, accept);
        request.setEntity(new FileEntity(file, ContentType.create(contentType)));
        return execute(request);
    }

    @Override
    public GeoServerResponse getBinary(String path, String accept) {
        String url = buildUrl(path);
        log.debug("GET (binary) {}", url);
        HttpGet request = applyHeaders(new HttpGet(url), null, accept);
        return executeBinary(request);
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    //  Internal Helpers 

    private GeoServerResponse execute(HttpUriRequestBase request) {
        try {
            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                String body = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                        : null;

                // Case-insensitive: HTTP header names are case-insensitive and HTTP/2
                // (or a fronting proxy) may lowercase them.
                Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                for (Header header : response.getHeaders()) {
                    headers.put(header.getName(), header.getValue());
                }

                log.debug("Response: {} (body: {} bytes)",
                        statusCode, body != null ? body.length() : 0);
                return new GeoServerResponse(statusCode, body, headers);
            });
        } catch (IOException e) {
            throw new ConnectionException(request.getRequestUri(), e);
        }
    }

    /**
     * Executes an HTTP request and reads the response body as raw bytes.
     * For 2xx responses the binary body is stored. For non-2xx responses the
     * text body is preserved so error-handling code can extract the message.
     */
    private GeoServerResponse executeBinary(HttpUriRequestBase request) {
        try {
            return httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                // Case-insensitive: HTTP header names are case-insensitive and HTTP/2
                // (or a fronting proxy) may lowercase them.
                Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                for (Header header : response.getHeaders()) {
                    headers.put(header.getName(), header.getValue());
                }
                if (statusCode >= 200 && statusCode < 300) {
                    byte[] bodyBytes = response.getEntity() != null
                            ? EntityUtils.toByteArray(response.getEntity())
                            : null;
                    log.debug("Response (binary): {} ({} bytes)", statusCode,
                            bodyBytes != null ? bodyBytes.length : 0);
                    return new GeoServerResponse(statusCode, bodyBytes, headers);
                } else {
                    String body = response.getEntity() != null
                            ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                            : null;
                    log.debug("Response (binary error): {} body={}", statusCode, body);
                    return new GeoServerResponse(statusCode, body, headers);
                }
            });
        } catch (IOException e) {
            throw new ConnectionException(request.getRequestUri(), e);
        }
    }

    private <T extends HttpUriRequestBase> T applyHeaders(T request, String contentType, String accept) {
        request.setHeader("Authorization", authHeader);
        if (contentType != null) {
            request.setHeader("Content-Type", contentType);
        }
        if (accept != null) {
            request.setHeader("Accept", accept);
        }
        return request;
    }

    private String buildUrl(String path) {
        // Percent-encode path segments so GeoServer's REST API handles special characters correctly.
        String encodedPath = encodePathSegments(path);
        return encodedPath.startsWith("/") ? baseUrl + encodedPath : baseUrl + "/" + encodedPath;
    }

    /**
     * Percent-encodes each path segment per RFC 3986: every character outside the
     * unreserved set (plus ':' and '@', which are legal raw in a path segment and
     * load-bearing in GeoServer qualified names like {@code topp:roads}) is encoded —
     * covering non-ASCII as well as ASCII specials such as space, '%', and '#'.
     * An existing valid percent-triplet ({@code %XX}) is passed through untouched, so
     * callers that pre-encode (the documented contract for GWC layer names, e.g.
     * {@code sf%3Aarchsites}) are not double-encoded.
     * The query string (everything after the first '?') is passed through untouched,
     * so a literal '?' inside a resource name cannot be distinguished from the start
     * of a query and must be avoided by callers.
     *
     * <p>"/rest/workspaces".split("/", -1) produces ["", "rest", "workspaces"];
     * empty segments from leading slashes are skipped to prevent double slashes.
     */
    static String encodePathSegments(String path) { // package-private for tests
        // Separate path from query string
        int qIdx = path.indexOf('?');
        String pathPart = qIdx >= 0 ? path.substring(0, qIdx) : path;
        String queryPart = qIdx >= 0 ? path.substring(qIdx) : "";

        StringBuilder sb = new StringBuilder();
        for (String segment : pathPart.split("/", -1)) {
            // Skip empty segments to prevent double slashes.
            if (segment.isEmpty()) {
                continue;
            }
            sb.append('/');
            sb.append(encodeSegment(segment));
        }
        // Re-attach a leading '/' if the original path started with one.
        String result = pathPart.startsWith("/") ? sb.toString() : (sb.length() > 0 ? sb.substring(1) : "");
        return result + queryPart;
    }

    private static String encodeSegment(String segment) {
        // Fast path: return as-is when every character is segment-safe.
        boolean safe = true;
        for (int i = 0; i < segment.length(); i++) {
            if (!isSegmentSafe(segment.charAt(i))) {
                safe = false;
                break;
            }
        }
        if (safe) {
            return segment;
        }
        StringBuilder sb = new StringBuilder(segment.length() + 8);
        int i = 0;
        while (i < segment.length()) {
            char c = segment.charAt(i);
            if (isSegmentSafe(c)) {
                sb.append(c);
                i++;
            } else if (c == '%' && i + 2 < segment.length()
                    && isHexDigit(segment.charAt(i + 1)) && isHexDigit(segment.charAt(i + 2))) {
                // Valid percent-triplet already present (pre-encoded caller input) — pass through.
                sb.append(segment, i, i + 3);
                i += 3;
            } else {
                // Percent-encode the UTF-8 bytes of this code point (handles surrogate pairs).
                int cp = segment.codePointAt(i);
                for (byte b : new String(Character.toChars(cp)).getBytes(StandardCharsets.UTF_8)) {
                    sb.append('%')
                      .append(Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16)))
                      .append(Character.toUpperCase(Character.forDigit(b & 0xF, 16)));
                }
                i += Character.charCount(cp);
            }
        }
        return sb.toString();
    }

    private static boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    /** RFC 3986 unreserved characters, plus ':' and '@' (legal raw within a path segment). */
    private static boolean isSegmentSafe(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || c == '-' || c == '.' || c == '_' || c == '~' || c == ':' || c == '@';
    }

    private static String normalizeUrl(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8));
    }
}
