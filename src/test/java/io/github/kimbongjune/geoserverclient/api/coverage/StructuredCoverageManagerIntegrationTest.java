package io.github.kimbongjune.geoserverclient.api.coverage;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.IndexSchema;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.SchemaAttribute;
import io.github.kimbongjune.geoserverclient.exception.GranuleNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for StructuredCoverageManager (GeoServer 2.28.2, localhost:9090).
 *
 * <p>Setup:
 * <ul>
 *   <li>WS: dedicated workspace</li>
 *   <li>STORE: imagemosaic.zip built from byte.tif x2 granules, uploaded via uploadFile(imagemosaic)</li>
 *   <li>COVERAGE: same name as STORE (auto-created via configure=first)</li>
 * </ul>
 *
 * <p>Test order:
 * 1 getSchema,
 * 2 getSchema-attributes,
 * 3 listGranules,
 * 4 listGranules-filter,
 * 5 listGranules-limit,
 * 6 getGranule,
 * 7 getGranule-notfound,
 * 8 deleteGranule,
 * 9 deleteGranule-notfound,
 * 10 deleteGranules-filter,
 * 11 deleteGranules-all
 */
@DisplayName("[IntegrationTest] StructuredCoverageManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StructuredCoverageManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS       = System.currentTimeMillis();
    private static final String WS       = "sc_ws_"    + TS;
    private static final String STORE    = "sc_store_" + TS;
    private static final String COVERAGE = STORE;   // configure=first → coverageName == storeName

    private static String granuleId1;  // populated after test 3
    private static String granuleId2;  // populated after test 3

    private StructuredCoverageManager scm;

    // setup

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        File byteTif = new File(res.toURI());

        File mosaicZip = createMosaicZip(byteTif);

        client.workspaces().create(
                io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest.of(WS));

        // upload imagemosaic zip: configure=first → coverage auto-created
        client.coverageStores().uploadFile(
                WS, STORE, "file", "imagemosaic", mosaicZip, "first", null, null);
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    @BeforeEach
    void initManager() {
        scm = client.structuredCoverages();
    }

    // 1. getSchema

    @Test
    @Order(1)
    @DisplayName("01. getSchema - returns schema")
    void getSchema_returnsSchema() {
        IndexSchema schema = scm.getSchema(WS, STORE, COVERAGE);
        assertNotNull(schema);
        assertNotNull(schema.getAttributes());
        List<SchemaAttribute> attrs = schema.getAttributes().getAttribute();
        assertNotNull(attrs);
        assertFalse(attrs.isEmpty(), "Attribute list must not be empty");
    }

    @Test
    @Order(2)
    @DisplayName("02. getSchema - contains the_geom and location attributes")
    void getSchema_containsRequiredAttributes() {
        IndexSchema schema = scm.getSchema(WS, STORE, COVERAGE);
        List<SchemaAttribute> attrs = schema.getAttributes().getAttribute();
        assertTrue(attrs.stream().anyMatch(a -> "the_geom".equals(a.getName())),
                "Schema must contain 'the_geom' attribute");
        assertTrue(attrs.stream().anyMatch(a -> "location".equals(a.getName())),
                "Schema must contain 'location' attribute");
    }

    // 3. listGranules

    @Test
    @Order(3)
    @DisplayName("03. listGranules - returns FeatureCollection with 2 granules")
    void listGranules_returnsTwoGranules() {
        GranuleCollection col = scm.listGranules(WS, STORE, COVERAGE);
        assertNotNull(col);
        assertEquals("FeatureCollection", col.getType());
        List<GranuleCollection.Granule> features = col.getFeatures();
        assertNotNull(features);
        assertEquals(2, features.size(), "Should have exactly 2 granules");

        // store granule IDs for subsequent tests
        granuleId1 = features.get(0).getId();
        granuleId2 = features.get(1).getId();
        assertNotNull(granuleId1);
        assertNotNull(granuleId2);
    }

    @Test
    @Order(4)
    @DisplayName("04. listGranules(filter) - exact location filter returns 1 granule")
    void listGranules_withFilter_returnsFiltered() {
        // get location from granule1
        GranuleCollection all = scm.listGranules(WS, STORE, COVERAGE);
        GranuleCollection.Granule first = all.getFeatures().get(0);
        String location = (String) first.getProperties().get("location");
        assertNotNull(location, "granule must have 'location' property");

        // filter by location
        GranuleCollection filtered = scm.listGranules(
                WS, STORE, COVERAGE, "location='" + location + "'", null, null);
        assertNotNull(filtered);
        assertEquals(1, filtered.getFeatures().size(),
                "Filter by exact location should return exactly 1 granule");
        assertEquals(location, filtered.getFeatures().get(0).getProperties().get("location"));
    }

    @Test
    @Order(5)
    @DisplayName("05. listGranules(limit=1) - returns exactly 1 granule")
    void listGranules_withLimit_respectsLimit() {
        GranuleCollection col = scm.listGranules(WS, STORE, COVERAGE, null, null, 1);
        assertNotNull(col);
        assertEquals(1, col.getFeatures().size(), "Limit=1 should return exactly 1 granule");
    }

    // 6. getGranule

    @Test
    @Order(6)
    @DisplayName("06. getGranule - returns single-item FeatureCollection")
    void getGranule_returnsFeatureCollection() {
        if (granuleId1 == null) {
            granuleId1 = scm.listGranules(WS, STORE, COVERAGE).getFeatures().get(0).getId();
        }
        GranuleCollection col = scm.getGranule(WS, STORE, COVERAGE, granuleId1);
        assertNotNull(col);
        assertEquals("FeatureCollection", col.getType());
        assertEquals(1, col.getFeatures().size(), "getGranule must return FeatureCollection with 1 feature");
        assertEquals(granuleId1, col.getFeatures().get(0).getId());
    }

    @Test
    @Order(7)
    @DisplayName("07. getGranule(nonexistent) - throws GranuleNotFoundException")
    void getGranule_notFound_throwsException() {
        String fakeId = COVERAGE + ".9999";
        GranuleNotFoundException ex = assertThrows(
                GranuleNotFoundException.class,
                () -> scm.getGranule(WS, STORE, COVERAGE, fakeId));
        assertNotNull(ex.getGranuleId());
        assertTrue(ex.getGranuleId().contains(fakeId),
                "Exception key should contain granuleId");
    }

    // 8. deleteGranule

    @Test
    @Order(8)
    @DisplayName("08. deleteGranule - deleted granule no longer appears in listGranules")
    void deleteGranule_thenNotInList() {
        if (granuleId2 == null) {
            List<GranuleCollection.Granule> features =
                    scm.listGranules(WS, STORE, COVERAGE).getFeatures();
            assertTrue(features.size() >= 2, "Need at least 2 granules for this test");
            granuleId2 = features.get(1).getId();
        }
        assertDoesNotThrow(() -> scm.deleteGranule(WS, STORE, COVERAGE, granuleId2));
        final String id = granuleId2;
        GranuleCollection col = scm.listGranules(WS, STORE, COVERAGE);
        assertTrue(col.getFeatures().stream().noneMatch(f -> id.equals(f.getId())),
                "Deleted granule must not appear in listGranules");
    }

    @Test
    @Order(9)
    @DisplayName("09. deleteGranule(nonexistent) - GeoServer returns 200 OK (silent noop)")
    void deleteGranule_notFound_returnsOk() {
        // GeoServer 2.28.2: DELETE on a nonexistent granule FID returns 200 OK silently.
        String fakeId = COVERAGE + ".9999";
        assertDoesNotThrow(() -> scm.deleteGranule(WS, STORE, COVERAGE, fakeId));
    }

    // 10. deleteGranules

    @Test
    @Order(10)
    @DisplayName("10. deleteGranules(filter) - deletes matching granule by location filter")
    void deleteGranules_withFilter_deletesMatching() {
        if (granuleId1 == null) {
            granuleId1 = scm.listGranules(WS, STORE, COVERAGE).getFeatures().get(0).getId();
        }
        GranuleCollection col = scm.getGranule(WS, STORE, COVERAGE, granuleId1);
        String location = (String) col.getFeatures().get(0).getProperties().get("location");
        assertNotNull(location);

        assertDoesNotThrow(() ->
                scm.deleteGranules(WS, STORE, COVERAGE,
                        "location='" + location + "'", "none", false));

        // after delete, getGranule should throw 404
        final String id = granuleId1;
        assertThrows(GranuleNotFoundException.class,
                () -> scm.getGranule(WS, STORE, COVERAGE, id));
    }

    @Test
    @Order(11)
    @DisplayName("11. deleteGranules(all) - features empty after delete")
    void deleteGranules_all_emptyAfterDelete() {
        assertDoesNotThrow(() -> scm.deleteGranules(WS, STORE, COVERAGE));

        GranuleCollection col = scm.listGranules(WS, STORE, COVERAGE);
        assertNotNull(col);
        assertTrue(col.getFeatures().isEmpty(),
                "After deleteGranules (all), features must be empty");
    }

    // helper

    /**
     * Builds imagemosaic.zip from two copies of byte.tif (granule1.tif, granule2.tif).
     */
    private static File createMosaicZip(File byteTif) throws Exception {
        File zipFile = File.createTempFile("mosaic_test_", ".zip");
        zipFile.deleteOnExit();
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            addEntry(zos, "granule1.tif", byteTif);
            addEntry(zos, "granule2.tif", byteTif);
        }
        return zipFile;
    }

    private static void addEntry(ZipOutputStream zos, String entryName, File src) throws Exception {
        zos.putNextEntry(new ZipEntry(entryName));
        try (FileInputStream fis = new FileInputStream(src)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = fis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
        }
        zos.closeEntry();
    }
}
