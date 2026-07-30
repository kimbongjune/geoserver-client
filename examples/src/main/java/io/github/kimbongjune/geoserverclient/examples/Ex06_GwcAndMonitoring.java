package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * GeoWebCache (tile caching config) and Monitoring (request history) — two of the "extension" API
 * groups that sit alongside the core Workspace/DataStore/Layer world.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>GWC write operations (like the gridset upsert below) always serialize as XML on the wire
 *       internally, regardless of the client's {@code defaultFormat} — a documented workaround for
 *       a GeoServer XStream-persister bug that makes JSON writes to these specific endpoints fail.
 *       This is entirely transparent to you as a caller: you only ever construct/receive typed
 *       DTOs like {@link GwcGridSet}, never raw XML.</li>
 *   <li>Monitoring requires the monitor extension to be active in GeoServer (it is, in this repo's
 *       Docker image) — every call you make against the API, including the ones in this very
 *       example, shows up in its own request history.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone, no arguments or
 * external files needed.
 */
public class Ex06_GwcAndMonitoring {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex06: GeoWebCache + Monitoring ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/3] Creating a custom GWC GridSet (e.g. for a non-standard tiling scheme)...");
        String gridName = "example_grid";
        GwcGridSet grid = new GwcGridSet(gridName, 4326, -180, -90, 180, 90);
        client.gwcGridSets().upsert(gridName, grid);
        System.out.println("      -> created: " + client.gwcGridSets().get(gridName).getName());
        client.gwcGridSets().delete(gridName);
        System.out.println("      -> deleted again");

        System.out.println("[2/3] Reading GWC's global configuration (read-only)...");
        System.out.println("      -> WMTS CITE compliant: " + client.gwcGlobal().get().getWmtsCiteCompliant());

        System.out.println("[3/3] Querying the 5 most recent monitored requests (this example's own calls included)...");
        List<MonitorRequestSummary> recent = client.monitoring().list(
                new MonitoringQuery().count(5).order("startTime;DESC"));
        System.out.println("      -> " + recent.size() + " request(s):");
        for (MonitorRequestSummary r : recent) {
            System.out.println("         - " + r);
        }

        System.out.println("\nDone (nothing to clean up — this example only created the transient gridset above).");
        client.close();
    }
}
