package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.IndexSchema;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h2>What this covers</h2>
 * ImageMosaic — the raster format that assembles multiple files ("granules") into one virtual
 * coverage — via {@code coverageStores().uploadFile(..., "imagemosaic", ...)} to create it,
 * {@code coverageStores().harvest(...)} to add a second granule afterwards, and every
 * {@code StructuredCoverageManager} method to inspect and manage its internal granule index.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>An ImageMosaic upload is a ZIP (like Shapefile), even for a single starter tile — GeoServer
 *       builds the mosaic index from whatever raster file(s) it finds inside.</li>
 *   <li>{@code harvest()} is how you add <em>more</em> granules to an already-existing mosaic store
 *       without re-uploading everything — a single raw file, not a ZIP. <b>Pass the raw file's own
 *       format</b> (e.g. {@code "geotiff"}), not {@code "imagemosaic"}, as {@code harvest()}'s format
 *       argument — confirmed directly against the REST API: this library derives the request's
 *       Content-Type from that format string, and {@code "imagemosaic"} always maps to
 *       {@code application/zip}, which makes GeoServer reject a raw (non-zipped) file with
 *       {@code 500 "Error occured unzipping file"}. The URL's {@code .../file.{format}} suffix is
 *       just a REST routing detail — it still harvests into the same mosaic store either way.</li>
 *   <li>{@code StructuredCoverageManager} only works against ImageMosaic-backed coverages —
 *       calling it against a plain GeoTIFF store (like Ex03's) causes a 500 (GeoServer
 *       {@code ClassCastException}, not a library bug).</li>
 *   <li>A granule's FID (used by {@code getGranule}/{@code deleteGranule}) follows the
 *       {@code "coverageName.N"} pattern GeoServer assigns internally, visible in
 *       {@code listGranules()}'s output.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Uses the bundled
 * {@code sample.tif} (first granule) and {@code small_world.tif} (second, harvested granule).
 */
public class Ex14_ImageMosaicAndStructuredCoverage {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex14: ImageMosaic + StructuredCoverage ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_mosaic_ws";
        System.out.println("[setup] Creating workspace '" + ws + "'...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        System.out.println("[1/4] Uploading the first granule as a ZIP to create the ImageMosaic store...");
        String storeName = "example_mosaic";
        File firstGranuleZip = zipSingleFile("src/main/resources/sample.tif", "granule_a.tif");
        client.coverageStores().uploadFile(ws, storeName, "file", "imagemosaic", firstGranuleZip, "first", null, null);
        String coverageName = client.coverages().list(ws, storeName).get(0).getName();
        System.out.println("      -> mosaic store '" + storeName + "' created with coverage '" + coverageName + "'");

        System.out.println("[2/4] harvest(): adding a second real GeoTIFF as another granule...");
        // format="geotiff" (the raw file's own format), not "imagemosaic" — see class Javadoc.
        client.coverageStores().harvest(ws, storeName, "file", "geotiff",
                new File("src/main/resources/small_world.tif"));
        System.out.println("      -> harvested small_world.tif into the mosaic");

        System.out.println("[3/4] StructuredCoverage — schema, list/get granules...");
        IndexSchema schema = client.structuredCoverages().getSchema(ws, storeName, coverageName);
        System.out.println("      -> getSchema(): " + schema);
        GranuleCollection all = client.structuredCoverages().listGranules(ws, storeName, coverageName);
        System.out.println("      -> listGranules(): " + all.getFeatures().size() + " granule(s)");
        String secondGranuleId = coverageName + ".2";
        GranuleCollection one = client.structuredCoverages().getGranule(ws, storeName, coverageName, secondGranuleId);
        System.out.println("      -> getGranule(\"" + secondGranuleId + "\"): " + one.getFeatures().size() + " feature");
        System.out.println("      -> listGranules() with paging (limit=1): "
                + client.structuredCoverages().listGranules(ws, storeName, coverageName, null, 0, 1)
                        .getFeatures().size() + " granule(s)");

        System.out.println("[4/4] Deleting one granule by id, then bulk-deleting the rest...");
        client.structuredCoverages().deleteGranule(ws, storeName, coverageName, secondGranuleId);
        System.out.println("      -> deleteGranule(\"" + secondGranuleId + "\"): removed");
        System.out.println("      -> remaining: "
                + client.structuredCoverages().listGranules(ws, storeName, coverageName).getFeatures().size());
        client.structuredCoverages().deleteGranules(ws, storeName, coverageName);
        System.out.println("      -> deleteGranules(): bulk-removed everything else");

        System.out.println("\nCleaning up...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }

    /** Zips a single file under a chosen entry name, the shape ImageMosaic/Shapefile uploads expect. */
    private static File zipSingleFile(String sourcePath, String entryName) throws Exception {
        File zip = File.createTempFile("geoserver-client-example-mosaic", ".zip");
        zip.deleteOnExit();
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
            out.putNextEntry(new ZipEntry(entryName));
            try (InputStream in = new FileInputStream(sourcePath)) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }
            }
            out.closeEntry();
        }
        return zip;
    }
}
