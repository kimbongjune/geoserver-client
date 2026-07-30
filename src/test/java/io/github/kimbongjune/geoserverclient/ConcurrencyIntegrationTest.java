package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the thread-safety guarantee documented on {@link GeoServerClient}: a single client
 * (built with a deliberately small connection pool) can be shared and called concurrently from
 * many threads across several different managers without errors, deadlock, or starvation.
 */
@DisplayName("[IntegrationTest] Concurrency")
class ConcurrencyIntegrationTest {

    private GeoServerClient client;

    @AfterEach
    void tearDown() throws IOException {
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

    @Test
    @DisplayName("many threads sharing one client with a small pool complete without errors")
    void concurrentCalls_sharedClientSmallPool_succeed() throws InterruptedException {
        Assumptions.assumeTrue(isGeoServerAvailable(),
                "GeoServer not available at http://localhost:8100/geoserver — skipping integration tests");

        client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .maxConnections(5)
                .build();

        int threads = 40;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger errors = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    List<String> workspaceNames = client.workspaces().list().stream()
                            .map(w -> w.getName())
                            .collect(Collectors.toList());
                    client.about().getVersion();
                    client.fonts().list();
                    if (workspaceNames.isEmpty()) {
                        throw new AssertionError("workspaces list unexpectedly empty");
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(30, TimeUnit.SECONDS);
        pool.shutdown();

        assertTrue(finished, "all threads should complete within 30s (possible deadlock/starvation)");
        assertEquals(0, errors.get(), "no thread should throw while sharing one client concurrently");
    }
}
