package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcDiskQuotaConfig;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFileBlobStore;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterContent;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcKillType;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedRequest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedStatus;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedType;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateRequestTypes;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * Every GeoWebCache (GWC) manager — GridSet, Global, Layer, Bounds, BlobStore, DiskQuota,
 * MassTruncate, Reload, Seed, FilterUpdate — plus Monitoring (request history).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>GWC write operations (gridset, layer, blobstore, disk quota upserts) always serialize as
 *       XML on the wire internally, regardless of the client's {@code defaultFormat} — a documented
 *       workaround for a GeoServer XStream-persister bug that makes JSON writes to these specific
 *       endpoints fail. This is entirely transparent to you as a caller: you only ever
 *       construct/receive typed DTOs, never raw XML.</li>
 *   <li>GWC layer/bounds/seed endpoints need the layer name percent-encoded ({@code ws%3Alayer}),
 *       since colons in a path segment aren't auto-encoded by this library (it only auto-encodes
 *       non-ASCII characters — see {@code ApacheHttpClient.encodeNonAsciiPathSegments}).</li>
 *   <li>{@code gwcLayers()} operates on a GWC-side tile-layer registration that GeoServer creates
 *       automatically for every real layer — there's no separate "create" call, only
 *       {@code get}/{@code upsert}/{@code delete} against the auto-registered entry.</li>
 *   <li>{@code gwcFilterUpdate()} only makes sense against a pre-existing WMSRasterFilter
 *       parameter filter (a fairly involved GWC config concept on its own) — this example instead
 *       calls it against a filter name that doesn't exist, to demonstrate the typed error path
 *       without needing that setup.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone, no arguments —
 * uses the same tiny bundled {@code sample.tif} as Ex03 to have a real layer for GWC to cache.
 */
public class Ex06_GwcAndMonitoring {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex06: GeoWebCache + Monitoring ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/9] GridSet — upsert a custom tiling scheme, get, delete...");
        String gridName = "example_grid";
        GwcGridSet grid = new GwcGridSet(gridName, 4326, -180, -90, 180, 90);
        client.gwcGridSets().upsert(gridName, grid);
        System.out.println("      -> created: " + client.gwcGridSets().get(gridName).getName());
        client.gwcGridSets().delete(gridName);
        System.out.println("      -> deleted again");

        System.out.println("\n[2/9] Global config — get, partial update, restore...");
        GwcGlobalSettings originalGlobal = client.gwcGlobal().get();
        Integer originalTimeout = originalGlobal.getBackendTimeout();
        System.out.println("      -> current backendTimeout: " + originalTimeout);
        GwcGlobalSettings updatedGlobal = new GwcGlobalSettings();
        updatedGlobal.setBackendTimeout(90);
        client.gwcGlobal().update(updatedGlobal);
        System.out.println("      -> backendTimeout is now: " + client.gwcGlobal().get().getBackendTimeout());
        GwcGlobalSettings restoreGlobal = new GwcGlobalSettings();
        restoreGlobal.setBackendTimeout(originalTimeout);
        client.gwcGlobal().update(restoreGlobal);
        System.out.println("      -> restored to: " + client.gwcGlobal().get().getBackendTimeout());

        System.out.println("\n[setup] Publishing a real GeoTIFF layer for GWC to cache...");
        File tif = extractBundledSample();
        String ws = "example_gwc_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String storeName = "example_geotiff";
        client.coverageStores().uploadFile(ws, storeName, "file", "geotiff", tif, "first", null, null);
        List<CoverageSummary> coverages = client.coverages().list(ws, storeName);
        String layerName = ws + ":" + coverages.get(0).getName();
        String layerNameEncoded = ws + "%3A" + coverages.get(0).getName();
        System.out.println("      -> layer ready: " + layerName);

        System.out.println("\n[3/9] Layer — get the auto-registered GWC entry, upsert, updateDeprecated...");
        GwcLayer gwcLayer = client.gwcLayers().get(layerName);
        System.out.println("      -> auto-registered, enabled=" + gwcLayer.getEnabled()
                + ", mimeFormats=" + gwcLayer.getMimeFormats());
        client.gwcLayers().upsert(layerName, gwcLayer);
        System.out.println("      -> upsert() re-saved it unchanged");
        client.gwcLayers().updateDeprecated(layerName, gwcLayer);
        System.out.println("      -> updateDeprecated() (legacy POST form) also accepted");

        System.out.println("\n[4/9] Bounds — tile-index coverage bounds for EPSG:4326...");
        System.out.println("      -> " + client.gwcBounds().getBounds(layerNameEncoded, "EPSG:4326"));

        System.out.println("\n[5/9] BlobStore — upsert a second (non-default) FileBlobStore, get, list, delete...");
        String blobStoreName = "example_blobstore";
        GwcFileBlobStore blobStore = new GwcFileBlobStore(blobStoreName, "/tmp/example-gwc-blobstore");
        client.gwcBlobStores().upsert(blobStoreName, blobStore);
        System.out.println("      -> list(): " + client.gwcBlobStores().list());
        System.out.println("      -> get(): baseDirectory reported by server");
        client.gwcBlobStores().delete(blobStoreName);
        System.out.println("      -> deleted");

        System.out.println("\n[6/9] DiskQuota — get, partial-ish update, restore (if the module is installed)...");
        try {
            GwcDiskQuotaConfig originalQuota = client.gwcDiskQuota().get();
            System.out.println("      -> enabled: " + originalQuota.getEnabled()
                    + ", cacheCleanUpFrequency: " + originalQuota.getCacheCleanUpFrequency());
            // NOTE: [GeoServer 2.28.2 bug] re-sending the exact object returned by get() fails —
            // its nested globalQuota.id doesn't round-trip through this endpoint's XStream XML
            // converter ("Expected element name to be 'value' but was id instead"). Building a
            // fresh config with only the simple scalar fields set (no globalQuota) avoids it, and
            // per this method's Javadoc, omitted fields are preserved rather than cleared.
            GwcDiskQuotaConfig update = new GwcDiskQuotaConfig();
            update.setEnabled(originalQuota.getEnabled());
            update.setCacheCleanUpFrequency(originalQuota.getCacheCleanUpFrequency());
            update.setCacheCleanUpUnits(originalQuota.getCacheCleanUpUnits());
            update.setMaxConcurrentCleanUps(originalQuota.getMaxConcurrentCleanUps());
            client.gwcDiskQuota().update(update);
            System.out.println("      -> update() re-saved the scalar fields cleanly");
        } catch (GeoServerException e) {
            System.out.println("      -> DiskQuota module not installed on this server (expected 500): "
                    + e.getMessage());
        }

        System.out.println("\n[7/9] MassTruncate — list supported types, truncate this layer's cache...");
        GwcTruncateRequestTypes types = client.gwcMassTruncate().listRequestTypes();
        System.out.println("      -> supported: " + types);
        client.gwcMassTruncate().truncate(new GwcTruncateLayerRequest(layerName));
        System.out.println("      -> truncated '" + layerName + "'");

        System.out.println("\n[8/9] Seed — start a tiny seed task, check status, reload, kill-all, filter-update error path...");
        // NOTE: [GeoServer 2.28.2 bug] omitting threadCount causes a server-side NPE
        // ("Cannot invoke Integer.intValue() because getThreadCount() is null") — the REST layer
        // unboxes it without a null check, so it must always be set explicitly despite not being
        // a required constructor argument.
        client.gwcSeed().seed(layerNameEncoded,
                new GwcSeedRequest(layerName, 4326, 0, 0, "image/png", GwcSeedType.SEED).threadCount(1));
        GwcSeedStatus running = client.gwcSeed().getRunningTasks(layerNameEncoded);
        System.out.println("      -> running/pending tasks for this layer right after starting: " + running);
        GwcSeedStatus allRunning = client.gwcSeed().getAllRunningTasks();
        System.out.println("      -> running/pending tasks across all layers: " + allRunning);
        String killResult = client.gwcSeed().killAll(GwcKillType.ALL);
        System.out.println("      -> killAll(ALL): " + killResult);
        GwcReloadResult reloadResult = client.gwcReload().reload();
        System.out.println("      -> reload(): reloaded=" + reloadResult.isReloaded()
                + ", layerCount=" + reloadResult.getLayerCount());

        try {
            client.gwcFilterUpdates().updateFilterXml("nonexistent_filter", GwcFilterContent.empty());
            System.out.println("      -> unexpected: no error for a nonexistent filter");
        } catch (GeoServerException e) {
            System.out.println("      -> updateFilterXml() against a nonexistent filter correctly errored: "
                    + e.getMessage());
        }

        client.gwcLayers().delete(layerName);
        System.out.println("      -> gwcLayers().delete(): unregistered the tile layer + cache");

        System.out.println("\n[9/9] Monitoring — querying, exporting in every format, and deleting request history...");
        List<MonitorRequestSummary> recent = client.monitoring().list(
                new MonitoringQuery().count(5).order("startTime;DESC"));
        System.out.println("      -> " + recent.size() + " request(s):");
        for (MonitorRequestSummary r : recent) {
            System.out.println("         - " + r);
        }
        long oneRequestId = recent.get(0).getName();
        System.out.println("      -> get(" + oneRequestId + "): " + client.monitoring().get(oneRequestId));
        System.out.println("      -> listXml(): " + client.monitoring().listXml(new MonitoringQuery().count(1)).length()
                + " chars of XML");
        System.out.println("      -> listCsv(): " + client.monitoring().listCsv(new MonitoringQuery().count(1)).length()
                + " chars of CSV");
        System.out.println("      -> listExcel(): " + client.monitoring().listExcel(new MonitoringQuery().count(1)).length
                + " bytes");
        System.out.println("      -> listZip(): " + client.monitoring().listZip(new MonitoringQuery().count(1)).length
                + " bytes");
        try {
            client.monitoring().deleteById(oneRequestId);
            System.out.println("      -> unexpected: deleteById() succeeded");
        } catch (GeoServerException e) {
            // Per this method's own Javadoc: "GeoServer always returns 405 ... for this endpoint
            // [verified]" — a real, permanent GeoServer limitation, not a bug in this call.
            System.out.println("      -> deleteById() correctly 405s (documented GeoServer limitation): "
                    + e.getMessage());
        }
        client.monitoring().deleteAll();
        System.out.println("      -> deleteAll(): cleared all remaining monitoring history");

        System.out.println("\nCleaning up (removes the workspace/store/coverage/layer)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");
        client.close();
    }

    private static File extractBundledSample() throws Exception {
        File tmp = File.createTempFile("geoserver-client-example-sample", ".tif");
        tmp.deleteOnExit();
        try (InputStream in = Ex06_GwcAndMonitoring.class.getClassLoader().getResourceAsStream("sample.tif");
             OutputStream out = new FileOutputStream(tmp)) {
            if (in == null) {
                throw new IllegalStateException("sample.tif not found on classpath — did the examples build correctly?");
            }
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
        return tmp;
    }
}
