package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.net.Socket;

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

    private static boolean isGeoServerAvailable() {
        try (Socket s = new Socket()) {
            s.connect(new java.net.InetSocketAddress("localhost", 8100), 3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
