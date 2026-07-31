package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.about.SystemMetric;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceMetadata;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * Every read-only "introspection" manager — {@code AboutManager} (version/manifest/module
 * status/system status), {@code ResourceManager} (full read/write access to GeoServer's own data
 * directory over REST), {@code ResetManager} (cache reset / catalog reload),
 * {@code OutputManager} (font + root template listing convenience wrapper), and
 * {@code TransformManager} (plugin availability check).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code getVersion()} always returns exactly 3 entries — GeoServer itself, GeoTools, and
 *       GeoWebCache — each with its own version/build info.</li>
 *   <li>{@code getManifest()} returns one entry <em>per JAR</em> on the classpath (hundreds of
 *       them) — useful for diagnosing "which exact version of library X is actually deployed"
 *       questions, but noisy for casual browsing.</li>
 *   <li>{@code ResourceManager} lets you read/write/copy/move files inside GeoServer's data
 *       directory the same way the Admin UI's "Resource Browser" does — this example creates a
 *       real scratch file, copies it, moves it, reads it back both ways, and cleans up.</li>
 *   <li>{@code ResetManager.reset()} only clears caches; {@code reload()} also re-reads the whole
 *       catalog from disk — reaching for {@code reload()} when you only changed on-disk data (not
 *       configuration) is unnecessarily heavy.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}.
 */
public class Ex09_AboutAndResource {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex09: About + Resource + Reset + Output + Transform ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/7] Version info (always GeoServer + GeoTools + GeoWebCache)...");
        List<AboutResource> version = client.about().getVersion();
        for (AboutResource r : version) {
            System.out.println("      - " + r.getName() + ": " + r.getProperties().get("Version"));
        }

        System.out.println("[2/7] Manifest — one entry per loaded JAR (showing count + first 3, filtered by name)...");
        List<AboutResource> manifest = client.about().getManifest();
        System.out.println("      -> " + manifest.size() + " JAR(s) total");
        manifest.stream().limit(3).forEach(r -> System.out.println("      - " + r.getName()));
        List<AboutResource> filteredManifest = client.about().getManifest("gs-.*", null, null);
        System.out.println("      -> filtered to 'gs-.*': " + filteredManifest.size() + " entr(y/ies)");

        System.out.println("[3/7] Module status — all, and one specific module (gs-main)...");
        List<ModuleStatusSummary> status = client.about().getStatus();
        System.out.println("      -> " + status.size() + " module(s) total, showing the first 5:");
        status.stream().limit(5).forEach(m -> System.out.println("      - " + m.getName()));
        List<ModuleStatusSummary> oneModule = client.about().getModuleStatus("gs-main");
        System.out.println("      -> getModuleStatus(\"gs-main\"): " + oneModule);

        System.out.println("[4/7] System status — 30+ live resource metrics (CPU/memory/filesystem/...)...");
        List<SystemMetric> metrics = client.about().getSystemStatus();
        System.out.println("      -> " + metrics.size() + " metric(s), showing the first 3:");
        metrics.stream().limit(3).forEach(m -> System.out.println("      - " + m.getName() + " = " + m.getValue()));

        System.out.println("\n[5/7] Resource API — create, copy, move, read (text + bytes), metadata, delete...");
        String dir = "example_resource_test";
        String original = dir + "/hello.txt";
        String copied = dir + "/hello-copy.txt";
        String moved = dir + "/hello-moved.txt";
        client.resources().createOrUpdate(original, "Hello from geoserver-client examples!", "text/plain");
        System.out.println("      -> createOrUpdate(): wrote '" + original + "'");
        System.out.println("      -> exists(): " + client.resources().exists(original));
        client.resources().copy(copied, original);
        System.out.println("      -> copy(): '" + copied + "' now exists too: " + client.resources().exists(copied));
        client.resources().move(moved, copied);
        System.out.println("      -> move(): '" + copied + "' gone (" + !client.resources().exists(copied)
                + "), '" + moved + "' now exists (" + client.resources().exists(moved) + ")");
        System.out.println("      -> getFileContent(): \"" + client.resources().getFileContent(original) + "\"");
        System.out.println("      -> getFileBytes(): " + client.resources().getFileBytes(original).length + " bytes");
        ResourceMetadata dirMeta = client.resources().getMetadata(dir);
        System.out.println("      -> getMetadata() on the directory: type=" + dirMeta.getType());
        List<ResourceChild> listing = client.resources().list(dir);
        System.out.println("      -> list(dir): " + listing.size() + " entries");
        List<ResourceChild> root = client.resources().listRoot();
        System.out.println("      -> listRoot(): " + root.size() + " entries at the top level");
        client.resources().delete(dir);
        System.out.println("      -> delete(dir): removed the whole scratch directory");

        System.out.println("\n[6/7] Reset / Reload — clear caches, then reload the full catalog from disk...");
        client.reset().reset();
        System.out.println("      -> reset(): caches cleared");
        client.reset().reload();
        System.out.println("      -> reload(): catalog + config reloaded from the data directory");

        System.out.println("\n[7/7] Output convenience wrapper + Transform availability...");
        System.out.println("      -> getFonts(): " + client.output().getFonts().size() + " font(s) registered");
        System.out.println("      -> getTemplates(): " + client.output().getTemplates().size()
                + " root-level template(s)");
        System.out.println("      -> transforms().isAvailable(): " + client.transforms().isAvailable()
                + " (app-schema transform plugin)");

        System.out.println("\nDone (nothing left behind — the Resource scratch directory was deleted above).");
        client.close();
    }
}
