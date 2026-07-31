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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h2>What this covers</h2>
 * Uploading a raster file with {@code configure=first}, which auto-creates the CoverageStore, the
 * Coverage, <em>and</em> a publishable Layer in a single call — then updating that auto-created
 * layer. Repeats the upload across four real raster formats: GeoTIFF, ArcGrid (ASCII), WorldImage
 * (PNG + world file), and GeoPackage (raster/mosaic reader). Also exercises
 * {@code coverageStores.reset()} and {@code coverages.listNative()}/{@code reset()}.
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
 *   <li>Each raster format expects a different upload shape: GeoTIFF/ArcGrid are single raw files;
 *       WorldImage <b>must</b> be a ZIP containing the image plus its {@code .pgw}/{@code .wld}
 *       world file (a raw PNG alone is rejected); GeoPackage's raster format id is literally
 *       {@code "geopackage (mosaic)"} — the space needs pre-encoding as {@code %20} by the caller,
 *       since this library only auto-encodes non-ASCII path segments (see the format string used
 *       below).</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs out of the box with no
 * arguments — it uses a tiny bundled 20x20 GeoTIFF ({@code sample.tif}, ~700 bytes) for the main
 * flow, plus bundled real files for the other formats ({@code sample.asc},
 * {@code small_world.png}/{@code .pgw}, {@code sample_raster.gpkg}). Pass your own GeoTIFF path as
 * the program argument if you'd rather use real imagery for the main flow.
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

        System.out.println("\n[extra] coverageStores.reset() / coverages.listNative()+reset() on the GeoTIFF store...");
        client.coverageStores().reset(ws, storeName);
        List<String> nativeNames = client.coverages().listNative(ws, storeName);
        System.out.println("      -> native coverage names available in the store: " + nativeNames);
        client.coverages().reset(ws, storeName, coverageName);
        System.out.println("      -> reset both store and coverage cache cleanly");

        System.out.println("\n=== Additional raster formats (same configure=first pattern, different format token) ===");

        System.out.println("[ArcGrid] Uploading a real ArcGrid ASCII (.asc) file...");
        client.coverageStores().uploadFile(ws, "example_arcgrid", "file", "arcgrid",
                new File("src/main/resources/sample.asc"), "first", null, null);
        System.out.println("      -> coverage(s): " + names(client.coverages().list(ws, "example_arcgrid")));

        System.out.println("[WorldImage] Uploading a real basemap PNG + hand-authored world file...");
        System.out.println("      -> WorldImage uploads must be a ZIP containing the image + its .pgw/.wld world file");
        File worldImageZip = zipWorldImage();
        client.coverageStores().uploadFile(ws, "example_worldimage", "file", "worldimage",
                worldImageZip, "first", null, null);
        System.out.println("      -> coverage(s): " + names(client.coverages().list(ws, "example_worldimage")));

        System.out.println("[GeoPackage raster] Uploading a real raster .gpkg (GeoPackage \"mosaic\" reader)...");
        System.out.println("      -> NOTE: this format id is literally \"geopackage (mosaic)\" (see GeoServer's"
                + " own error message when you pass an unsupported one) — the space must be pre-encoded as"
                + " %20 by the caller, since this library only auto-encodes non-ASCII path segments.");
        client.coverageStores().uploadFile(ws, "example_gpkg_raster", "file", "geopackage%20(mosaic)",
                new File("src/main/resources/sample_raster.gpkg"), "first", null, null);
        System.out.println("      -> coverage(s): " + names(client.coverages().list(ws, "example_gpkg_raster")));

        System.out.println("\nCleaning up (recurse=true also removes every store/coverage/layer above)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }

    private static String names(List<CoverageSummary> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i).getName());
        }
        return sb.append("]").toString();
    }

    /** Zips the bundled small_world.png with its hand-authored small_world.pgw world file. */
    private static File zipWorldImage() throws Exception {
        File zip = File.createTempFile("geoserver-client-example-worldimage", ".zip");
        zip.deleteOnExit();
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
            addToZip(out, "src/main/resources/small_world.png", "small_world.png");
            addToZip(out, "src/main/resources/small_world.pgw", "small_world.pgw");
        }
        return zip;
    }

    private static void addToZip(ZipOutputStream out, String sourcePath, String entryName) throws Exception {
        out.putNextEntry(new ZipEntry(entryName));
        try (InputStream in = new java.io.FileInputStream(sourcePath)) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
        out.closeEntry();
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
