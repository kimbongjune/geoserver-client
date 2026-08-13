package io.github.kimbongjune.geoserverclient.http;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * No live GeoServer required — uses the JDK's built-in {@link HttpServer} to observe
 * exactly what {@link ApacheHttpClient} puts on the wire.
 */
@DisplayName("[UnitTest] ApacheHttpClient URL encoding and header handling")
class ApacheHttpClientTest {

    private static HttpServer server;
    private static ApacheHttpClient client;
    private static final AtomicReference<String> lastRawPath = new AtomicReference<>();

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            lastRawPath.set(exchange.getRequestURI().getRawPath()
                    + (exchange.getRequestURI().getRawQuery() != null
                       ? "?" + exchange.getRequestURI().getRawQuery() : ""));
            byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("X-Custom-Header", "custom-value");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        client = new ApacheHttpClient(
                "http://127.0.0.1:" + server.getAddress().getPort(),
                "admin", "geoserver", 5_000, 5_000);
    }

    @AfterAll
    static void stopServer() throws IOException {
        client.close();
        server.stop(0);
    }

    //  Pure encoding (no HTTP)

    @Test
    @DisplayName("space, '%', '#' in a segment are percent-encoded (RFC 3986)")
    void asciiSpecials_encoded() {
        assertEquals("/rest/workspaces/my%20ws/styles/50%25%20off%231",
                ApacheHttpClient.encodePathSegments("/rest/workspaces/my ws/styles/50% off#1"));
    }

    @Test
    @DisplayName("non-ASCII segments are still percent-encoded (existing behavior preserved)")
    void nonAscii_encoded() {
        assertEquals("/rest/workspaces/%ED%95%9C%EA%B8%80",
                ApacheHttpClient.encodePathSegments("/rest/workspaces/한글"));
    }

    @Test
    @DisplayName("':' in qualified layer names and '@' stay raw (legal path-segment chars)")
    void colonAndAt_notEncoded() {
        assertEquals("/rest/layers/topp:tasmania_roads",
                ApacheHttpClient.encodePathSegments("/rest/layers/topp:tasmania_roads"));
    }

    @Test
    @DisplayName("query string after the first '?' passes through untouched")
    void queryString_untouched() {
        assertEquals("/rest/workspaces/my%20ws?recurse=true&quietOnNotFound=true",
                ApacheHttpClient.encodePathSegments("/rest/workspaces/my ws?recurse=true&quietOnNotFound=true"));
    }

    @Test
    @DisplayName("pre-encoded %XX triplets pass through untouched (GWC layer name contract)")
    void preEncodedTriplet_notDoubleEncoded() {
        assertEquals("/gwc/rest/seed/sf%3Aarchsites.xml",
                ApacheHttpClient.encodePathSegments("/gwc/rest/seed/sf%3Aarchsites.xml"));
        // A bare '%' not followed by two hex digits is still a literal and gets encoded.
        assertEquals("/rest/styles/50%25_off",
                ApacheHttpClient.encodePathSegments("/rest/styles/50%_off"));
    }

    @Test
    @DisplayName("plain ASCII path is returned unchanged (fast path)")
    void plainPath_unchanged() {
        assertEquals("/rest/workspaces/topp.roads_v2",
                ApacheHttpClient.encodePathSegments("/rest/workspaces/topp.roads_v2"));
    }

    //  Wire-level behavior (real HTTP round trip)

    @Test
    @DisplayName("encoded path actually reaches the server; request does not blow up on a space")
    void spaceInName_reachesServerEncoded() {
        GeoServerResponse response = client.get("/rest/workspaces/my ws", "application/json");
        assertEquals(200, response.getStatusCode());
        assertEquals("/rest/workspaces/my%20ws", lastRawPath.get());
    }

    @Test
    @DisplayName("response header lookup is case-insensitive")
    void headerLookup_caseInsensitive() {
        GeoServerResponse response = client.get("/rest/about/version", "application/json");
        assertNotNull(response.getHeader("X-Custom-Header"));
        assertNotNull(response.getHeader("x-custom-header"));
        assertNotNull(response.getHeader("X-CUSTOM-HEADER"));
        assertEquals("custom-value", response.getHeader("x-CUSTOM-header"));
        assertEquals(response.getHeader("Content-Type"), response.getHeader("content-type"));
    }
}
