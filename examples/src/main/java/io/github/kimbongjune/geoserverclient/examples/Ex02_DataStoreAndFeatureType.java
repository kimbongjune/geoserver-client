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
 * DataStore CRUD (PostGIS connection config) + uploading a Shapefile ZIP with
 * {@code configure=first}, which auto-creates the FeatureType (and, in turn, a Layer) in one call.
 *
 * Requires a real shapefile ZIP on disk — pass its path as the program argument, e.g. a zip
 * containing a .shp/.shx/.dbf/.prj set. Skips the upload step (but still demonstrates the
 * PostGIS-config path) if no argument is given.
 */
public class Ex02_DataStoreAndFeatureType {

    public static void main(String[] args) throws Exception {
        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_ds_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        // --- PostGIS connection-only datastore (no actual DB connectivity required to create it) ---
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
        System.out.println("Created PostGIS datastore: " + pgStore.getName() + " (type=" + pgStore.getType() + ")");
        client.datastores().delete(ws, pgStoreName, false);

        // --- Shapefile ZIP upload (real file, configure=first auto-creates FeatureType + Layer) ---
        if (args.length > 0) {
            File shpZip = new File(args[0]);
            String shpStoreName = "example_shp";
            client.datastores().uploadFile(ws, shpStoreName, "file", "shp", shpZip, "first", null, null);

            List<FeatureTypeSummary> types = client.featureTypes().list(ws, shpStoreName);
            System.out.println("Feature types auto-configured from upload: " + types.size());
            for (FeatureTypeSummary ft : types) {
                System.out.println("  - " + ft.getName());
            }
        } else {
            System.out.println("No shapefile ZIP argument given — skipping upload demo. "
                    + "Run with: java ... Ex02_DataStoreAndFeatureType /path/to/shapefile.zip");
        }

        client.workspaces().delete(ws, true);
        client.close();
    }
}
