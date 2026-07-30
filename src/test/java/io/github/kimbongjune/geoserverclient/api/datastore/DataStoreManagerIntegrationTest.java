package io.github.kimbongjune.geoserverclient.api.datastore;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.datastore.UpdateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.DataStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link DataStoreManager}.
 * <p>
 * Creates a dedicated test workspace in {@code @BeforeAll}.
 * Tests both H2 embedded stores and PostGIS stores.
 * PostGIS: local PostgreSQL 18 (geoserver_it/geoserver_it@localhost:5432/geoserver_it)
 * accessed from the GeoServer container via host.docker.internal.
 * GeoServer: {@code http://localhost:9090/geoserver}, see docker-compose.yml.
 */
@DisplayName("DataStoreManager Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataStoreManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS             = System.currentTimeMillis();
    private static final String WS             = "it_ds_ws_"  + TS;
    private static final String DS_MAIN        = "it_ds_"     + TS;
    private static final String DS_UPD         = "it_ds_upd_" + TS;
    private static final String DS_DEL         = "it_ds_del_" + TS;
    private static final String DS_RESET       = "it_ds_rst_" + TS;
    private static final String DS_PG          = "it_ds_pg_"   + TS;
    private static final String DS_SHP         = "it_ds_shp_"  + TS;
    private static final String DS_SHP_URL     = "it_ds_shpu_" + TS;
    private static final String DS_DIR         = "it_ds_dir_"  + TS;
    private static final String DS_CSV         = "it_ds_csv_"  + TS;
    private static final String DS_PROPS       = "it_ds_prp_"  + TS;
    private static final String DS_GPKG        = "it_ds_gpkg_" + TS;
    private static final String DS_WFSNG       = "it_ds_wfs_"  + TS;

    /** Relative paths based on GeoServer's internal data_dir (requires GEOSERVER_FILESYSTEM_SANDBOX=/opt/geoserver/data_dir) */
    private static final String SHP_FILE_PATH  = "file:data/testshp/testpoint.shp";
    private static final String DIR_PATH       = "file:data/testshp";
    private static final String CSV_FILE_PATH  = "file:data/testcsv/points.csv";
    private static final String PROPS_DIR_PATH = "file:data/testprops";
    private static final String GPKG_FILE_PATH = "file:data/testgpkg/test.gpkg";
    /** GeoServer's own WFS used as WFS-NG source — container-internal URL (localhost:8080) */
    private static final String WFS_CAP_URL    = "http://localhost:8080/geoserver/ows?service=WFS&request=GetCapabilities";

    /** Address for the GeoServer container to reach the host PC's PostgreSQL */
    private static final String PG_HOST        = "host.docker.internal";
    private static final String PG_PORT        = "5432";
    private static final String PG_DB          = "geoserver_it";
    private static final String PG_USER        = "geoserver_it";
    private static final String PG_PASS        = "geoserver_it";

    private DataStoreManager datastores;
 
    @BeforeAll
    void setUpDataStores() {
        // create dedicated workspace
        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        datastores = client.datastores();
        // pre-create stores for update/delete/reset tests
        datastores.create(WS, h2Request(DS_UPD));
        datastores.create(WS, h2Request(DS_DEL));
        datastores.create(WS, h2Request(DS_RESET));
    }

    @AfterAll
    void cleanUp() {
        // deleting workspace with recurse=true also removes all stores inside
        client.workspaces().delete(WS, true);
    }

    // ── 1. list ───────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("list() - returns datastore list")
    void list_shouldReturnList() {
        List<DataStoreSummary> list = datastores.list(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "at least one pre-created store must exist");
        list.forEach(ds -> {
            assertNotNull(ds.getName());
            assertNotNull(ds.getHref());
        });
    }

    // ── 2-3. create ──────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("create() - creates H2 store and returns detail")
    void create_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, h2Request(DS_MAIN));
        assertNotNull(ds);
        assertEquals(DS_MAIN, ds.getName());
        assertTrue(ds.isEnabled());
        assertNotNull(ds.getConnectionParameters());
        assertNotNull(ds.getDateCreated());
    }

    @Test
    @Order(3)
    @DisplayName("create() - duplicate name → ResourceAlreadyExistsException")
    void create_duplicate_shouldThrowException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> datastores.create(WS, h2Request(DS_MAIN)));
    }

    // ── 4-5. get ──────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("get() - returns datastore detail")
    void get_shouldReturnDetails() {
        DataStore ds = datastores.get(WS, DS_MAIN);
        assertNotNull(ds);
        assertEquals(DS_MAIN, ds.getName());
        assertNotNull(ds.getWorkspace());
        assertEquals(WS, ds.getWorkspace().getName());
        assertNotNull(ds.getConnectionParameters());
        assertNotNull(ds.getConnectionParameters().getEntry());
    }

    @Test
    @Order(5)
    @DisplayName("get() - nonexistent store → DataStoreNotFoundException")
    void get_nonExistent_shouldThrowException() {
        DataStoreNotFoundException ex = assertThrows(DataStoreNotFoundException.class,
                () -> datastores.get(WS, "nonexistent_xyz_abc_12345"));
        assertNotNull(ex.getStoreName());
    }

    // ── 6. exists ─────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("exists() - distinguishes existing from nonexistent")
    void exists_shouldReturnCorrectBoolean() {
        assertTrue(datastores.exists(WS, DS_MAIN));
        assertFalse(datastores.exists(WS, "nonexistent_xyz_abc_12345"));
    }

    // ── 7-9. update ───────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("update() - changes description")
    void update_description_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_UPD,
                UpdateDataStoreRequest.builder().description("integration test update").build());
        assertEquals(DS_UPD, updated.getName());
        assertEquals("integration test update", updated.getDescription());
        assertNotNull(updated.getDateModified());
    }

    @Test
    @Order(8)
    @DisplayName("update() - rename datastore")
    void update_rename_shouldRenameDataStore() {
        String newName = DS_MAIN + "_renamed";
        DataStore updated = datastores.update(WS, DS_MAIN,
                UpdateDataStoreRequest.builder().name(newName).build());
        assertEquals(newName, updated.getName());
        assertFalse(datastores.exists(WS, DS_MAIN));
        assertTrue(datastores.exists(WS, newName));
    }

    @Test
    @Order(9)
    @DisplayName("update() - nonexistent store → DataStoreNotFoundException")
    void update_nonExistent_shouldThrowException() {
        assertThrows(DataStoreNotFoundException.class,
                () -> datastores.update(WS, "nonexistent_xyz_abc_12345",
                        UpdateDataStoreRequest.builder().description("x").build()));
    }

    // ── 10. reset ─────────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("reset() - cache reset succeeds (200)")
    void reset_shouldSucceed() {
        assertDoesNotThrow(() -> datastores.reset(WS, DS_RESET));
    }

    // ── 11-12. delete ─────────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("delete() - deletes datastore")
    void delete_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_DEL));
        datastores.delete(WS, DS_DEL);
        assertFalse(datastores.exists(WS, DS_DEL));
    }

    @Test
    @Order(12)
    @DisplayName("delete() - nonexistent store → DataStoreNotFoundException")
    void delete_nonExistent_shouldThrowException() {
        assertThrows(DataStoreNotFoundException.class,
                () -> datastores.delete(WS, "nonexistent_xyz_abc_12345"));
    }

    // ── 13-14. delete with recurse/purge ──────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("delete(recurse=false) - empty store → 200")
    void delete_recurse_false_emptyStore_shouldSucceed() {
        assertTrue(datastores.exists(WS, DS_RESET));
        datastores.delete(WS, DS_RESET, false);
        assertFalse(datastores.exists(WS, DS_RESET));
    }

    @Test
    @Order(14)
    @DisplayName("delete(recurse=true, purge=none) - 200")
    void delete_recurse_true_purge_none_shouldSucceed() {
        assertTrue(datastores.exists(WS, DS_UPD));
        datastores.delete(WS, DS_UPD, true, "none");
        assertFalse(datastores.exists(WS, DS_UPD));
    }

    // ── 15-18. PostGIS CRUD ──────────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("PostGIS create() - creates store")
    void create_postgis_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, pgRequest(DS_PG));
        assertNotNull(ds);
        assertEquals(DS_PG, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(16)
    @DisplayName("PostGIS get() - connParams contains dbtype=postgis and host")
    void get_postgis_shouldContainConnectionParams() {
        DataStore ds = datastores.get(WS, DS_PG);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertFalse(entries.isEmpty());
        assertTrue(entries.stream().anyMatch(e -> "dbtype".equals(e.getKey()) && "postgis".equals(e.getValue())),
                "dbtype=postgis must be present");
        assertTrue(entries.stream().anyMatch(e -> "host".equals(e.getKey())),
                "host must be present");
    }

    @Test
    @Order(17)
    @DisplayName("PostGIS update() - changes description")
    void update_postgis_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_PG,
                UpdateDataStoreRequest.builder().description("PostGIS integration test").build());
        assertEquals("PostGIS integration test", updated.getDescription());
    }

    @Test
    @Order(18)
    @DisplayName("PostGIS delete() - deletes store")
    void delete_postgis_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_PG));
        datastores.delete(WS, DS_PG);
        assertFalse(datastores.exists(WS, DS_PG));
    }

    // ── 19-24. Shapefile (upload) CRUD + downloadFile ────────────────────────

    @Test
    @Order(19)
    @DisplayName("uploadFile() - Shapefile ZIP upload → store created")
    void uploadFile_shouldCreateDataStore() {
        URL zipUrl = getClass().getClassLoader().getResource("AL_D002_36_20260104.zip");
        assertNotNull(zipUrl, "test resource AL_D002_36_20260104.zip not found");
        File zipFile = new File(URI.create(zipUrl.toString()));
        datastores.uploadFile(WS, DS_SHP, "file", "shp", zipFile, "first", null, null);
        assertTrue(datastores.exists(WS, DS_SHP), "store must be created after upload");
    }

    @Test
    @Order(20)
    @DisplayName("SHP(upload) get() - verifies store name and enabled flag")
    void get_shp_upload_shouldReturnStore() {
        DataStore ds = datastores.get(WS, DS_SHP);
        assertNotNull(ds);
        assertEquals(DS_SHP, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(21)
    @DisplayName("SHP(upload) update() - changes description")
    void update_shp_upload_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_SHP,
                UpdateDataStoreRequest.builder().description("SHP upload test").build());
        assertEquals("SHP upload test", updated.getDescription());
    }

    @Test
    @Order(22)
    @DisplayName("downloadFile() - 404 → DataStoreNotFoundException")
    void downloadFile_nonExistent_shouldThrowException() {
        assertThrows(DataStoreNotFoundException.class,
                () -> datastores.downloadFile(WS, "nonexistent_xyz_abc_12345", "file", "shp"));
    }

    @Test
    @Order(23)
    @DisplayName("downloadFile() - server error → GeoServerResponseException")
    void downloadFile_serverError_shouldThrowException() {
        assertThrows(GeoServerResponseException.class,
                () -> datastores.downloadFile(WS, DS_SHP, "file", "shp"));
    }

    @Test
    @Order(24)
    @DisplayName("SHP(upload) delete() - deletes store including auto-created layer (recurse)")
    void delete_shp_upload_shouldDeleteDataStore() {
        // GeoServer auto-creates FeatureType/Layer on upload → recurse=true required
        assertTrue(datastores.exists(WS, DS_SHP));
        datastores.delete(WS, DS_SHP, true, "none");
        assertFalse(datastores.exists(WS, DS_SHP));
    }

    // ── 25-28. Shapefile (url) CRUD ──────────────────────────────────────────

    @Test
    @Order(25)
    @DisplayName("SHP(url) create() - creates store")
    void create_shapefile_url_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_SHP_URL)
                .connectionParam("url", SHP_FILE_PATH)
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_SHP_URL, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(26)
    @DisplayName("SHP(url) get() - connParams contains url parameter")
    void get_shapefile_url_shouldContainUrlParam() {
        DataStore ds = datastores.get(WS, DS_SHP_URL);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> "url".equals(e.getKey())),
                "url parameter must be present");
    }

    @Test
    @Order(27)
    @DisplayName("SHP(url) update() - changes description")
    void update_shapefile_url_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_SHP_URL,
                UpdateDataStoreRequest.builder().description("SHP url test").build());
        assertEquals("SHP url test", updated.getDescription());
    }

    @Test
    @Order(28)
    @DisplayName("SHP(url) delete() - deletes store")
    void delete_shapefile_url_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_SHP_URL));
        datastores.delete(WS, DS_SHP_URL);
        assertFalse(datastores.exists(WS, DS_SHP_URL));
    }

    // ── 29-32. Directory CRUD ────────────────────────────────────────────────

    @Test
    @Order(29)
    @DisplayName("Directory create() - creates store")
    void create_directory_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_DIR)
                .connectionParam("url", DIR_PATH)
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_DIR, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(30)
    @DisplayName("Directory get() - connParams contains url parameter")
    void get_directory_shouldContainUrlParam() {
        DataStore ds = datastores.get(WS, DS_DIR);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> "url".equals(e.getKey())),
                "url parameter must be present");
    }

    @Test
    @Order(31)
    @DisplayName("Directory update() - changes description")
    void update_directory_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_DIR,
                UpdateDataStoreRequest.builder().description("Directory test").build());
        assertEquals("Directory test", updated.getDescription());
    }

    @Test
    @Order(32)
    @DisplayName("Directory delete() - deletes store")
    void delete_directory_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_DIR));
        datastores.delete(WS, DS_DIR);
        assertFalse(datastores.exists(WS, DS_DIR));
    }

    // ── 33-36. CSV CRUD ──────────────────────────────────────────────────────

    @Test
    @Order(33)
    @DisplayName("CSV create() - creates store")
    void create_csv_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_CSV)
                .connectionParam("file", CSV_FILE_PATH)
                .connectionParam("strategy", "specify")
                .connectionParam("latField", "latitude")
                .connectionParam("lngField", "longitude")
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_CSV, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(34)
    @DisplayName("CSV get() - connParams contains file parameter")
    void get_csv_shouldContainFileParam() {
        DataStore ds = datastores.get(WS, DS_CSV);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> "file".equals(e.getKey())),
                "file parameter must be present");
    }

    @Test
    @Order(35)
    @DisplayName("CSV update() - changes description")
    void update_csv_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_CSV,
                UpdateDataStoreRequest.builder().description("CSV test").build());
        assertEquals("CSV test", updated.getDescription());
    }

    @Test
    @Order(36)
    @DisplayName("CSV delete() - deletes store")
    void delete_csv_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_CSV));
        datastores.delete(WS, DS_CSV);
        assertFalse(datastores.exists(WS, DS_CSV));
    }

    // ── 37-40. Properties CRUD ───────────────────────────────────────────────

    @Test
    @Order(37)
    @DisplayName("Properties create() - creates store")
    void create_properties_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_PROPS)
                .connectionParam("directory", PROPS_DIR_PATH)
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_PROPS, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(38)
    @DisplayName("Properties get() - connParams contains directory parameter")
    void get_properties_shouldContainDirectoryParam() {
        DataStore ds = datastores.get(WS, DS_PROPS);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> "directory".equals(e.getKey())),
                "directory parameter must be present");
    }

    @Test
    @Order(39)
    @DisplayName("Properties update() - changes description")
    void update_properties_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_PROPS,
                UpdateDataStoreRequest.builder().description("Properties test").build());
        assertEquals("Properties test", updated.getDescription());
    }

    @Test
    @Order(40)
    @DisplayName("Properties delete() - deletes store")
    void delete_properties_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_PROPS));
        datastores.delete(WS, DS_PROPS);
        assertFalse(datastores.exists(WS, DS_PROPS));
    }

    // ── 41-44. GeoPackage CRUD ───────────────────────────────────────────────

    @Test
    @Order(41)
    @DisplayName("GeoPackage create() - creates store")
    void create_geopackage_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_GPKG)
                .connectionParam("database", GPKG_FILE_PATH)
                .connectionParam("dbtype", "geopkg")
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_GPKG, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(42)
    @DisplayName("GeoPackage get() - connParams contains dbtype=geopkg")
    void get_geopackage_shouldContainGeopkgDbtype() {
        DataStore ds = datastores.get(WS, DS_GPKG);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> "dbtype".equals(e.getKey()) && "geopkg".equals(e.getValue())),
                "dbtype=geopkg must be present");
    }

    @Test
    @Order(43)
    @DisplayName("GeoPackage update() - changes description")
    void update_geopackage_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_GPKG,
                UpdateDataStoreRequest.builder().description("GeoPackage test").build());
        assertEquals("GeoPackage test", updated.getDescription());
    }

    @Test
    @Order(44)
    @DisplayName("GeoPackage delete() - deletes store")
    void delete_geopackage_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_GPKG));
        datastores.delete(WS, DS_GPKG);
        assertFalse(datastores.exists(WS, DS_GPKG));
    }

    // ── 45-48. WFS-NG CRUD ───────────────────────────────────────────────────

    @Test
    @Order(45)
    @DisplayName("WFS-NG create() - creates store (using GeoServer's own WFS)")
    void create_wfsng_shouldCreateDataStore() {
        DataStore ds = datastores.create(WS, CreateDataStoreRequest.of(DS_WFSNG)
                .connectionParam("WFSDataStoreFactory:GET_CAPABILITIES_URL", WFS_CAP_URL)
                .connectionParam("WFSDataStoreFactory:PROTOCOL", "false")
                .connectionParam("WFSDataStoreFactory:USERNAME", "admin")
                .connectionParam("WFSDataStoreFactory:PASSWORD", "geoserver")
                .connectionParam("namespace", "http://" + WS + ".com"));
        assertNotNull(ds);
        assertEquals(DS_WFSNG, ds.getName());
        assertTrue(ds.isEnabled());
    }

    @Test
    @Order(46)
    @DisplayName("WFS-NG get() - connParams contains GET_CAPABILITIES_URL")
    void get_wfsng_shouldContainCapabilitiesUrl() {
        DataStore ds = datastores.get(WS, DS_WFSNG);
        List<DataStore.Entry> entries = ds.getConnectionParameters().getEntry();
        assertTrue(entries.stream().anyMatch(e -> e.getKey() != null && e.getKey().contains("CAPABILITIES_URL")),
                "GET_CAPABILITIES_URL must be present");
    }

    @Test
    @Order(47)
    @DisplayName("WFS-NG update() - changes description")
    void update_wfsng_shouldUpdateDescription() {
        DataStore updated = datastores.update(WS, DS_WFSNG,
                UpdateDataStoreRequest.builder().description("WFS-NG test").build());
        assertEquals("WFS-NG test", updated.getDescription());
    }

    @Test
    @Order(48)
    @DisplayName("WFS-NG delete() - deletes store")
    void delete_wfsng_shouldDeleteDataStore() {
        assertTrue(datastores.exists(WS, DS_WFSNG));
        datastores.delete(WS, DS_WFSNG);
        assertFalse(datastores.exists(WS, DS_WFSNG));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static CreateDataStoreRequest h2Request(String name) {
        return CreateDataStoreRequest.of(name)
                .connectionParam("database", "it_test_" + name)
                .connectionParam("dbtype",   "h2");
    }

    private static CreateDataStoreRequest pgRequest(String name) {
        return CreateDataStoreRequest.of(name)
                .connectionParam("host",     PG_HOST)
                .connectionParam("port",     PG_PORT)
                .connectionParam("database", PG_DB)
                .connectionParam("user",     PG_USER)
                .connectionParam("passwd",   PG_PASS)
                .connectionParam("dbtype",   "postgis");
    }
}
