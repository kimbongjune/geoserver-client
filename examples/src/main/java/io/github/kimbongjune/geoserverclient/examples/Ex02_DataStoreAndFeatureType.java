package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * DataStore CRUD (vector data connections), FeatureType CRUD, and every real way GeoServer
 * ingests vector data: (1) a genuinely live PostGIS connection with real rows in a real table,
 * (2) a Shapefile ZIP upload, and (3) a GeoPackage upload — all auto-configuring FeatureTypes
 * and Layers in one call. Also exercises {@code reset()} and {@code downloadFile()} on both
 * managers, and {@code listAvailable()} for discovering unconfigured tables in a DB store.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>Connection parameters (host/port/database/credentials/...) are arbitrary key-value pairs
 *       via {@code .connectionParam(key, value)} — GeoServer's own data store types define what
 *       keys they expect, so this library doesn't hard-code a fixed schema per store type.</li>
 *   <li>The PostGIS host here is {@code postgres} (the docker-compose service/container name),
 *       <b>not</b> {@code localhost} — GeoServer resolves that hostname from inside the compose
 *       network. This example's own JDBC connection (to create the demo table) uses
 *       {@code localhost:5432} instead, since it runs on the host machine where the port is
 *       published.</li>
 *   <li>{@code featureTypes().listAvailable(ws, store)} lists tables that exist in the database
 *       but aren't yet configured as FeatureTypes — the discovery step before publishing.</li>
 *   <li>{@code featureTypes().create(...)} with no {@code .attribute(...)} calls publishes an
 *       <em>existing</em> table as-is (schema read from the DB); giving it attributes instead
 *       would auto-create a brand-new empty table.</li>
 *   <li>{@code uploadFile(..., configure="first")} is the "give me a working layer in one call"
 *       path: upload a real file and GeoServer creates the DataStore, the FeatureType, <em>and</em>
 *       a publishable Layer, all from that single POST.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer + PostGIS at {@code http://localhost:8100/geoserver} and
 * {@code localhost:5432} ({@code docker-compose up -d} from the repo root — both services are
 * defined there). Uses bundled real sample data:
 * {@code ne_110m_admin_0_countries.zip} (Natural Earth, public domain Shapefile) and
 * {@code sample_vector.gpkg} (a small real GeoPackage container).
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

        // ---------------------------------------------------------------
        // PART 1 — a genuinely live PostGIS connection
        // ---------------------------------------------------------------
        System.out.println("\n[1/3] PostGIS — creating a real table with real rows via plain JDBC...");
        String jdbcUrl = "jdbc:postgresql://localhost:5432/geoserver";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "geoserver", "geoserver");
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS example_cities");
            st.execute("CREATE TABLE example_cities (id SERIAL PRIMARY KEY, name VARCHAR(64), "
                    + "geom GEOMETRY(Point, 4326))");
            st.execute("INSERT INTO example_cities (name, geom) VALUES "
                    + "('Seoul', ST_SetSRID(ST_MakePoint(126.9780, 37.5665), 4326)), "
                    + "('Tokyo', ST_SetSRID(ST_MakePoint(139.6917, 35.6895), 4326)), "
                    + "('Paris', ST_SetSRID(ST_MakePoint(2.3522, 48.8566), 4326)), "
                    + "('London', ST_SetSRID(ST_MakePoint(-0.1278, 51.5074), 4326)), "
                    + "('New York', ST_SetSRID(ST_MakePoint(-74.0060, 40.7128), 4326))");
            System.out.println("      -> created 'example_cities' with 5 real city rows");
        }

        String pgStoreName = "example_postgis";
        System.out.println("      -> creating PostGIS DataStore pointing at that live DB "
                + "(host='postgres', resolved inside the compose network)...");
        DataStore pgStore = client.datastores().create(ws,
                CreateDataStoreRequest.builder(pgStoreName)
                        .type("PostGIS")
                        .connectionParam("host", "postgres")
                        .connectionParam("port", "5432")
                        .connectionParam("database", "geoserver")
                        .connectionParam("schema", "public")
                        .connectionParam("user", "geoserver")
                        .connectionParam("passwd", "geoserver")
                        .connectionParam("dbtype", "postgis")
                        .build());
        System.out.println("      -> created: " + pgStore.getName() + " (type=" + pgStore.getType() + ")");

        System.out.println("      -> listAvailable(): discovering unconfigured tables in the DB...");
        List<String> available = client.featureTypes().listAvailable(ws, pgStoreName);
        System.out.println("      -> available: " + available);

        System.out.println("      -> create(): publishing the existing 'example_cities' table as-is...");
        client.featureTypes().create(ws, pgStoreName,
                CreateFeatureTypeRequest.builder("example_cities").srs("EPSG:4326").build());
        List<FeatureTypeSummary> pgTypes = client.featureTypes().list(ws, pgStoreName);
        System.out.println("      -> now configured: " + pgTypes.size() + " feature type(s) — "
                + "published as a real WMS layer over live PostGIS data");

        System.out.println("      -> reset(): forcing GeoServer to re-read the schema from the DB...");
        client.featureTypes().reset(ws, pgStoreName, "example_cities");
        client.datastores().reset(ws, pgStoreName);
        System.out.println("      -> done, no error means the store/schema cache reset cleanly");

        // ---------------------------------------------------------------
        // PART 2 — Shapefile ZIP upload (real Natural Earth data)
        // ---------------------------------------------------------------
        System.out.println("\n[2/3] Shapefile — uploading a real Natural Earth countries ZIP...");
        File shpZip = resource("ne_110m_admin_0_countries.zip");
        String shpStoreName = "example_shp";
        client.datastores().uploadFile(ws, shpStoreName, "file", "shp", shpZip, "first", null, null);
        List<FeatureTypeSummary> shpTypes = client.featureTypes().list(ws, shpStoreName);
        System.out.println("      -> uploaded '" + shpZip.getName() + "' — auto-configured "
                + shpTypes.size() + " feature type(s): ");
        for (FeatureTypeSummary ft : shpTypes) {
            System.out.println("         - " + ft.getName());
        }

        System.out.println("      -> downloadFile(): fetching the store's source files back as a ZIP...");
        try {
            byte[] downloaded = client.datastores().downloadFile(ws, shpStoreName, "file", "shp");
            System.out.println("      -> downloaded " + downloaded.length + " bytes");
        } catch (GeoServerResponseException e) {
            // [GeoServer 2.28.2 quirk] auto-configured ("configure=first") Shapefile uploads are
            // stored as a *directory* of spatial files (url="file:.../example_shp/", trailing
            // slash) rather than a single .shp file. The download-as-ZIP resource handler throws
            // an uncaught server-side error for directory-backed stores — this returns HTTP 500
            // with an empty body and nothing logged, even though the store is otherwise fully
            // functional. downloadFile() does work for single-file stores (e.g. one uploaded
            // directly at a file: URL pointing at a .shp, not a directory).
            System.out.println("      -> known GeoServer quirk: 500 for directory-backed Shapefile stores"
                    + " (see this catch block's comment) — " + e.getMessage());
        }

        // ---------------------------------------------------------------
        // PART 3 — GeoPackage upload
        // ---------------------------------------------------------------
        System.out.println("\n[3/3] GeoPackage — uploading a real .gpkg container...");
        File gpkgFile = resource("sample_vector.gpkg");
        String gpkgStoreName = "example_gpkg";
        client.datastores().uploadFile(ws, gpkgStoreName, "file", "gpkg", gpkgFile, "first", null, null);
        List<FeatureTypeSummary> gpkgTypes = client.featureTypes().list(ws, gpkgStoreName);
        System.out.println("      -> uploaded '" + gpkgFile.getName() + "' — auto-configured "
                + gpkgTypes.size() + " feature type(s)");

        // ---------------------------------------------------------------
        // Cleanup
        // ---------------------------------------------------------------
        System.out.println("\nCleaning up (workspace delete recurse=true removes every store/type/layer above)...");
        client.workspaces().delete(ws, true);
        try (Connection conn = DriverManager.getConnection(jdbcUrl, "geoserver", "geoserver");
             Statement st = conn.createStatement()) {
            st.execute("DROP TABLE IF EXISTS example_cities");
        }
        System.out.println("Done.");

        client.close();
    }

    private static File resource(String name) {
        return new File("src/main/resources/" + name);
    }
}
