package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * GeoWebCache (tile caching) and Monitoring (request history) — two of the "extension" API
 * groups. GWC write operations always use XML on the wire internally (a documented GeoServer
 * XStream-persister workaround baked into these managers), which is transparent to callers: you
 * only ever see typed DTOs.
 */
public class Ex06_GwcAndMonitoring {

    public static void main(String[] args) throws Exception {
        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        // Custom GridSet (e.g. for a non-standard tiling scheme)
        String gridName = "example_grid";
        GwcGridSet grid = new GwcGridSet(gridName, 4326, -180, -90, 180, 90);
        client.gwcGridSets().upsert(gridName, grid);
        System.out.println("Created gridset: " + client.gwcGridSets().get(gridName).getName());
        client.gwcGridSets().delete(gridName);

        // GWC global config (read-only demo)
        System.out.println("GWC WMTS CITE compliant: " + client.gwcGlobal().get().getWmtsCiteCompliant());

        // Monitoring: query the most recent requests (requires the monitor extension to be active)
        List<MonitorRequestSummary> recent = client.monitoring().list(
                new MonitoringQuery().count(5).order("startTime;DESC"));
        System.out.println("Most recent " + recent.size() + " request(s):");
        for (MonitorRequestSummary r : recent) {
            System.out.println("  - " + r);
        }

        client.close();
    }
}
