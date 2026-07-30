package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * Two read-only "introspection" API groups: {@code AboutManager} (version/manifest/module status)
 * and {@code ResourceManager} (browsing GeoServer's own data directory over REST).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code getVersion()} always returns exactly 3 entries — GeoServer itself, GeoTools, and
 *       GeoWebCache — each with its own version/build info.</li>
 *   <li>{@code getManifest()} returns one entry <em>per JAR</em> on the classpath (hundreds of
 *       them) — useful for diagnosing "which exact version of library X is actually deployed"
 *       questions, but noisy for casual browsing.</li>
 *   <li>{@code ResourceManager} lets you browse/read/write files inside GeoServer's data directory
 *       the same way the Admin UI's "Resource Browser" does — {@code listRoot()} here just lists
 *       what's at the top level.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Read-only — nothing to clean up.
 */
public class Ex09_AboutAndResource {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex09: About + Resource (read-only) ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/3] Version info (always GeoServer + GeoTools + GeoWebCache)...");
        List<AboutResource> version = client.about().getVersion();
        for (AboutResource r : version) {
            System.out.println("      - " + r.getName() + ": " + r.getProperties().get("Version"));
        }

        System.out.println("[2/3] Module status (a sample of the first 5, out of everything registered)...");
        List<ModuleStatusSummary> status = client.about().getStatus();
        System.out.println("      -> " + status.size() + " module(s) total, showing the first 5:");
        status.stream().limit(5).forEach(m -> System.out.println("      - " + m.getName()));

        System.out.println("[3/3] Browsing the root of GeoServer's data directory via the Resource API...");
        List<ResourceChild> root = client.resources().listRoot();
        System.out.println("      -> " + root.size() + " entries at the top level, showing the first 10:");
        root.stream().limit(10).forEach(c -> System.out.println("      - " + c.getName()));

        System.out.println("\nDone (nothing to clean up — everything here was read-only).");
        client.close();
    }
}
