package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * Uploading a raster file (GeoTIFF) with {@code configure=first}, which auto-creates the
 * CoverageStore, the Coverage, <em>and</em> a publishable Layer in a single call — then updating
 * that auto-created layer.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>There is <b>no separate "create layer" REST endpoint</b> in GeoServer at all — a Layer is
 *       always created as a side effect of publishing a Coverage or FeatureType. That's why
 *       {@code LayerManager} has no {@code create()} method, only {@code get}/{@code update}/
 *       {@code delete}.</li>
 *   <li>The auto-created layer's full name follows the {@code "workspace:resourceName"} pattern —
 *       you'll see this convention throughout the library (WMS/WMTS layer nativeName, layer group
 *       members, etc).</li>
 *   <li>Layer updates are partial, same as workspace updates in Ex01: only the field you set
 *       ({@code queryable} here) is changed.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs out of the box with no
 * arguments — it uses a tiny bundled 20x20 GeoTIFF ({@code sample.tif}, ~700 bytes). Pass your own
 * GeoTIFF path as the program argument if you'd rather use real imagery.
 */
public class Ex03_CoverageUploadAndLayer {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex03: Coverage upload + auto-created Layer ===\n");

        File tif = args.length > 0 ? new File(args[0]) : extractBundledSample();
        System.out.println("Using GeoTIFF: " + tif.getAbsolutePath()
                + (args.length > 0 ? "" : " (bundled sample — pass your own path as an argument to use real imagery)"));

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_cov_ws";
        System.out.println("[setup] Creating workspace '" + ws + "'...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        System.out.println("[1/3] Uploading the GeoTIFF with configure=first...");
        String storeName = "example_geotiff";
        client.coverageStores().uploadFile(ws, storeName, "file", "geotiff", tif, "first", null, null);

        List<CoverageSummary> coverages = client.coverages().list(ws, storeName);
        String coverageName = coverages.get(0).getName();
        System.out.println("      -> GeoServer auto-created CoverageStore '" + storeName
                + "' and Coverage '" + coverageName + "'");

        System.out.println("[2/3] Looking up the Layer that was auto-created alongside it...");
        String layerFullName = ws + ":" + coverageName;
        Layer layer = client.layers().get(layerFullName);
        System.out.println("      -> layer '" + layer.getName() + "', defaultStyle="
                + layer.getDefaultStyle().getName() + " (GeoServer picks a default raster style automatically)");

        System.out.println("[3/3] Partially updating the layer (queryable=true only)...");
        Layer updated = client.layers().update(layerFullName,
                UpdateLayerRequest.builder().queryable(true).build());
        System.out.println("      -> queryable is now: " + updated.getQueryable());

        System.out.println("\nCleaning up (recurse=true also removes the store and coverage)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }

    /** Copies the tiny bundled sample.tif out of the classpath to a real file uploadFile() can read. */
    private static File extractBundledSample() throws Exception {
        File tmp = File.createTempFile("geoserver-client-example-sample", ".tif");
        tmp.deleteOnExit();
        try (InputStream in = Ex03_CoverageUploadAndLayer.class.getClassLoader().getResourceAsStream("sample.tif");
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
