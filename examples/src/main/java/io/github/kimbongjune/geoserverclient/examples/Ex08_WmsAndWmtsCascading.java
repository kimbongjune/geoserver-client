package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.PublishWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * WMS and WMTS <em>cascading</em> — publishing a layer that's actually served by a remote (or, as
 * here, the same) WMS/WMTS server, rather than backed by a local DataStore/CoverageStore.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>To keep this example self-contained, it cascades a layer <b>against GeoServer's own WMS
 *       and WMTS endpoints</b> — that's why the capabilities URL uses {@code localhost:8080}
 *       (the container-internal port) rather than {@code localhost:8100} (the externally-mapped
 *       one): GeoServer is fetching its own capabilities document from inside the container.</li>
 *   <li>WMS and WMTS use different {@code nativeName} conventions: a plain
 *       {@code "workspace:layerName"} for WMS, and the same pattern for WMTS but referring to the
 *       GWC tile-layer name specifically.</li>
 *   <li><b>{@code WmsLayerManager.update()} is {@code @Deprecated}</b> — it always throws on
 *       GeoServer 2.28.x due to a confirmed server-side bug (XStream persister issue), not a
 *       library defect. This example intentionally doesn't call it; see the class Javadoc if
 *       you're curious.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone — it creates its
 * own source layer from the bundled {@code sample.tif} first.
 */
public class Ex08_WmsAndWmtsCascading {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex08: WMS + WMTS Cascading ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_cascade_ws";
        System.out.println("[setup] Creating a workspace and a real layer to cascade (from the bundled sample.tif)...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String covStoreName = "example_source";
        client.coverageStores().uploadFile(ws, covStoreName, "file", "geotiff", extractBundledSample(), "first", null, null);
        List<CoverageSummary> coverages = client.coverages().list(ws, covStoreName);
        String sourceLayer = ws + ":" + coverages.get(0).getName();
        System.out.println("      -> source layer ready: " + sourceLayer);

        System.out.println("[1/2] WMS: creating a store pointed at GeoServer's own WMS capabilities, then cascading a layer...");
        String wmsStoreName = "example_wmsstore";
        String wmsCapsUrl = "http://localhost:8080/geoserver/wms?service=wms&version=1.3.0&request=GetCapabilities";
        WmsStore wmsStore = client.wmsStores().create(ws,
                CreateWmsStoreRequest.builder(wmsStoreName, wmsCapsUrl).enabled(true).build());
        System.out.println("      -> WMS store created: " + wmsStore.getName());
        System.out.println("      -> listAvailable(): remote layers discoverable but not yet published: "
                + client.wmsLayers().listAvailable(ws, wmsStoreName));

        String wmsLayerName = "example_wmslayer";
        WmsLayer wmsLayer = client.wmsLayers().publish(ws, wmsStoreName,
                PublishWmsLayerRequest.builder(wmsLayerName, sourceLayer).title("Cascaded via WMS").build());
        System.out.println("      -> published cascaded WMS layer: " + wmsLayer.getName()
                + " (nativeName=" + wmsLayer.getNativeName() + ")");

        System.out.println("[2/2] WMTS: same idea, against GeoServer's own WMTS (GWC) capabilities...");
        String wmtsStoreName = "example_wmtsstore";
        String wmtsCapsUrl = "http://localhost:8080/geoserver/gwc/service/wmts?REQUEST=GetCapabilities";
        WmtsStore wmtsStore = client.wmtsStores().create(ws,
                CreateWmtsStoreRequest.builder(wmtsStoreName, wmtsCapsUrl).enabled(true).build());
        System.out.println("      -> WMTS store created: " + wmtsStore.getName());
        System.out.println("      -> listAvailable(): remote tile layers discoverable but not yet published: "
                + client.wmtsLayers().listAvailable(ws, wmtsStoreName));

        String wmtsLayerName = "example_wmtslayer";
        WmtsLayer wmtsLayer = client.wmtsLayers().publish(ws, wmtsStoreName,
                PublishWmtsLayerRequest.builder(wmtsLayerName, sourceLayer).title("Cascaded via WMTS").build());
        System.out.println("      -> published cascaded WMTS layer: " + wmtsLayer.getName());

        System.out.println("\nCleaning up (recurse=true tears down everything, including the cascaded layers)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }

    private static File extractBundledSample() throws Exception {
        File tmp = File.createTempFile("geoserver-client-example-sample", ".tif");
        tmp.deleteOnExit();
        try (InputStream in = Ex08_WmsAndWmtsCascading.class.getClassLoader().getResourceAsStream("sample.tif");
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
