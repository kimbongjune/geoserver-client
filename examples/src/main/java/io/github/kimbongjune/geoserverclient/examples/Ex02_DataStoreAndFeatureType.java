package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * DataStore CRUD (vector data connections — PostGIS here) and uploading a real Shapefile ZIP,
 * which auto-configures both the FeatureType and the underlying Layer in one call.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>Connection parameters (host/port/database/credentials/...) are arbitrary key-value pairs
 *       via {@code .connectionParam(key, value)} — GeoServer's own data store types define what
 *       keys they expect, so this library doesn't hard-code a fixed schema per store type.</li>
 *   <li>Creating the PostGIS store here <b>does not require a real, reachable Postgres</b> — it's
 *       just catalog configuration. GeoServer only actually tries to connect when you ask it to
 *       list feature types or serve data.</li>
 *   <li>{@code uploadFile(..., configure="first")} is the "give me a working layer in one call"
 *       path: upload a real Shapefile ZIP and GeoServer creates the DataStore, the FeatureType,
 *       <em>and</em> a publishable Layer, all from that single POST.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver} ({@code docker-compose up -d} from
 * the repo root). The Shapefile part additionally needs a real zip on disk containing a
 * .shp/.shx/.dbf/(.prj) set — pass its path as the program argument. Without an argument, this
 * example still runs and demonstrates the PostGIS-config path; it just skips the upload part
 * (printing a note instead of failing).
 */
public class Ex02_DataStoreAndFeatureType {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex02: DataStore + FeatureType ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_ds_ws";
        System.out.println("[setup] Creating workspace '" + ws + "' to hold everything...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        System.out.println("[1/2] Creating a PostGIS datastore (config only, no real DB needed for this step)...");
        String pgStoreName = "example_postgis";
        DataStore pgStore = client.datastores().create(ws,
                CreateDataStoreRequest.builder(pgStoreName)
                        .type("PostGIS")
                        .connectionParam("host", "localhost")
                        .connectionParam("port", "5432")
                        .connectionParam("database", "mydb")
                        .connectionParam("schema", "public")
                        .connectionParam("user", "postgres")
                        .connectionParam("passwd", "postgres")
                        .connectionParam("dbtype", "postgis")
                        .build());
        System.out.println("      -> created: " + pgStore.getName() + " (type=" + pgStore.getType() + ")");
        client.datastores().delete(ws, pgStoreName, false);
        System.out.println("      -> deleted again (this example doesn't need it to stick around)");

        System.out.println("[2/2] Uploading a Shapefile ZIP with configure=first...");
        if (args.length > 0) {
            File shpZip = new File(args[0]);
            String shpStoreName = "example_shp";
            client.datastores().uploadFile(ws, shpStoreName, "file", "shp", shpZip, "first", null, null);
            System.out.println("      -> uploaded '" + shpZip.getName() + "' — GeoServer auto-configured the store");

            List<FeatureTypeSummary> types = client.featureTypes().list(ws, shpStoreName);
            System.out.println("      -> feature type(s) auto-created from the upload: " + types.size());
            for (FeatureTypeSummary ft : types) {
                System.out.println("         - " + ft.getName()
                        + "  (also published as a Layer — see Ex03 for working with layers)");
            }
        } else {
            System.out.println("      -> SKIPPED: no shapefile ZIP path given.");
            System.out.println("         Re-run with: java ... Ex02_DataStoreAndFeatureType /path/to/shapefile.zip");
        }

        System.out.println("\nCleaning up (deletes the workspace and everything under it)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }
}
