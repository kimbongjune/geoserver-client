package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base class for GeoServer REST API integration tests.
 * <p>
 * Requires a GeoServer instance at {@code http://localhost:8100/geoserver}
 * with credentials {@code admin / geoserver}.
 * Use {@code docker-compose up -d} to start the local instance.
 * <p>
 * Tests are skipped automatically (not failed) when GeoServer is unreachable,
 * so CI without Docker does not produce false negatives.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    private static final String GEOSERVER_URL = "http://localhost:8100/geoserver";

    protected GeoServerClient client;

    @BeforeAll
    void setUpClient() {
        Assumptions.assumeTrue(isGeoServerAvailable(),
                "GeoServer not available at " + GEOSERVER_URL + " — skipping integration tests");

        client = GeoServerClient.builder()
                .url(GEOSERVER_URL)
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();
    }

    @AfterAll
    void tearDownClient() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Issues a raw GET request directly against the GeoServer REST API (bypassing the library
     * entirely) and returns the response body as a string. Used by tests that need to assert the
     * library's parsed/serialized value against exactly what GeoServer itself returns over the
     * wire, independent of this library's own deserialization logic.
     *
     * @param path REST path, e.g. {@code "/rest/workspaces/ws/datastores/ds/featuretypes/ft.json"}
     * @return the raw response body
     */
    protected String rawGet(String path) throws IOException {
        URL url = new URL(GEOSERVER_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            String auth = Base64.getEncoder().encodeToString("admin:geoserver".getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + auth);
            try (InputStream is = conn.getInputStream()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int n;
                while ((n = is.read(buf)) != -1) {
                    baos.write(buf, 0, n);
                }
                return new String(baos.toByteArray(), StandardCharsets.UTF_8);
            }
        } finally {
            conn.disconnect();
        }
    }

    private static boolean isGeoServerAvailable() {
        try (Socket s = new Socket()) {
            s.connect(new java.net.InetSocketAddress("localhost", 8100), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
