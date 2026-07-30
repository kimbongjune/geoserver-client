package io.github.kimbongjune.geoserverclient.api.coveragestore;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStore;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CreateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.UpdateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.CoverageStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CoverageStoreManager}.
 * <p>
 * Covers catalog CRUD and file upload (uploadFile) across the full API.
 * Upload tests use GDAL byte.tif (20x20, NAD27/UTM zone 11N, 736 bytes).
 * File location: src/test/resources/byte.tif
 * GeoServer: {@code http://localhost:9090/geoserver}, see docker-compose.yml.
 */
@DisplayName("CoverageStoreManager Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoverageStoreManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS             = System.currentTimeMillis();
    private static final String WS             = "it_cs_ws_"  + TS;
    private static final String CS_MAIN        = "it_cs_"     + TS;
    private static final String CS_UPD         = "it_cs_upd_" + TS;
    private static final String CS_DEL         = "it_cs_del_" + TS;
    private static final String CS_RESET       = "it_cs_rst_" + TS;
    private static final String CS_UP_NONE     = "it_cs_upn_" + TS;   // uploadFile configure=none
    private static final String CS_UP_FIRST    = "it_cs_upf_" + TS;   // uploadFile configure=first

    /** src/test/resources/byte.tif — GDAL 20x20 GeoTIFF (NAD27/UTM 11N, 736 bytes) */
    private static File BYTE_TIF;

    private CoverageStoreManager coverageStores;

    @BeforeAll
    void setUpCoverageStores() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif test resource not found (src/test/resources/byte.tif)");
        BYTE_TIF = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        coverageStores = client.coverageStores();
        // pre-create stores for update/delete/reset tests
        coverageStores.create(WS, CreateCoverageStoreRequest.of(CS_UPD).type("GeoTIFF").enabled(false));
        coverageStores.create(WS, CreateCoverageStoreRequest.of(CS_DEL).type("GeoTIFF").enabled(false));
        coverageStores.create(WS, CreateCoverageStoreRequest.of(CS_RESET).type("GeoTIFF").enabled(false));
    }

    @AfterAll
    void cleanUp() {
        client.workspaces().delete(WS, true);
    }

    // 1. list (initial state)

    @Test
    @Order(1)
    @DisplayName("list() - returns pre-created store list")
    void list_shouldReturnList() {
        List<CoverageStoreSummary> list = coverageStores.list(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "at least one pre-created store must exist");
        list.forEach(cs -> {
            assertNotNull(cs.getName());
            assertNotNull(cs.getHref());
        });
    }

    // 2-3. create

    @Test
    @Order(2)
    @DisplayName("create() - creates GeoTIFF store and returns detail")
    void create_shouldCreateCoverageStore() {
        CoverageStore cs = coverageStores.create(WS,
                CreateCoverageStoreRequest.of(CS_MAIN)
                        .type("GeoTIFF")
                        .enabled(false)
                        .description("Integration test GeoTIFF store"));

        assertNotNull(cs);
        assertEquals(CS_MAIN, cs.getName());
        assertFalse(cs.isEnabled());
        assertEquals("GeoTIFF", cs.getType());
        assertEquals("Integration test GeoTIFF store", cs.getDescription());
        assertNotNull(cs.getDateCreated());
    }

    @Test
    @Order(3)
    @DisplayName("create() - duplicate name → ResourceAlreadyExistsException")
    void create_duplicate_shouldThrowException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> coverageStores.create(WS,
                        CreateCoverageStoreRequest.of(CS_MAIN).type("GeoTIFF")));
    }

    // 4-5. get

    @Test
    @Order(4)
    @DisplayName("get() - returns store detail")
    void get_shouldReturnDetails() {
        CoverageStore cs = coverageStores.get(WS, CS_MAIN);
        assertNotNull(cs);
        assertEquals(CS_MAIN, cs.getName());
        assertNotNull(cs.getWorkspace());
        assertEquals(WS, cs.getWorkspace().getName());
        assertEquals("GeoTIFF", cs.getType());
        assertFalse(cs.isEnabled());
    }

    @Test
    @Order(5)
    @DisplayName("get() - nonexistent store → CoverageStoreNotFoundException")
    void get_nonExistent_shouldThrowException() {
        CoverageStoreNotFoundException ex = assertThrows(CoverageStoreNotFoundException.class,
                () -> coverageStores.get(WS, "nonexistent_xyz_abc_12345"));
        assertNotNull(ex.getStoreName());
    }

    // 6. exists

    @Test
    @Order(6)
    @DisplayName("exists() - distinguishes existing from nonexistent")
    void exists_shouldReturnCorrectBoolean() {
        assertTrue(coverageStores.exists(WS, CS_MAIN));
        assertFalse(coverageStores.exists(WS, "nonexistent_xyz_abc_12345"));
    }

    // 7-9. update

    @Test
    @Order(7)
    @DisplayName("update() - changes description")
    void update_description_shouldUpdateDescription() {
        CoverageStore updated = coverageStores.update(WS, CS_UPD,
                UpdateCoverageStoreRequest.builder()
                        .description("integration test update")
                        .build());
        assertEquals(CS_UPD, updated.getName());
        assertEquals("integration test update", updated.getDescription());
    }

    @Test
    @Order(8)
    @DisplayName("update() - rename store")
    void update_rename_shouldRenameCoverageStore() {
        String newName = CS_MAIN + "_renamed";
        CoverageStore updated = coverageStores.update(WS, CS_MAIN,
                UpdateCoverageStoreRequest.builder().name(newName).build());
        assertEquals(newName, updated.getName());
        assertFalse(coverageStores.exists(WS, CS_MAIN));
        assertTrue(coverageStores.exists(WS, newName));
    }

    @Test
    @Order(9)
    @DisplayName("update() - nonexistent store → CoverageStoreNotFoundException")
    void update_nonExistent_shouldThrowException() {
        assertThrows(CoverageStoreNotFoundException.class,
                () -> coverageStores.update(WS, "nonexistent_xyz_abc_12345",
                        UpdateCoverageStoreRequest.builder().description("x").build()));
    }

    // 10. reset

    @Test
    @Order(10)
    @DisplayName("reset() - cache reset succeeds (200)")
    void reset_shouldSucceed() {
        assertDoesNotThrow(() -> coverageStores.reset(WS, CS_RESET));
    }

    // 11-13. delete

    @Test
    @Order(11)
    @DisplayName("delete() - recurse=false (no coverages) → 200")
    void delete_noRecurse_empty_shouldSucceed() {
        assertDoesNotThrow(() -> coverageStores.delete(WS, CS_DEL, false));
        assertFalse(coverageStores.exists(WS, CS_DEL));
    }

    @Test
    @Order(12)
    @DisplayName("delete() - recurse=true → 200")
    void delete_recurse_shouldSucceed() {
        String storeName = CS_RESET;
        assertDoesNotThrow(() -> coverageStores.delete(WS, storeName, true));
        assertFalse(coverageStores.exists(WS, storeName));
    }

    @Test
    @Order(13)
    @DisplayName("delete() - nonexistent store → CoverageStoreNotFoundException")
    void delete_nonExistent_shouldThrowException() {
        assertThrows(CoverageStoreNotFoundException.class,
                () -> coverageStores.delete(WS, "nonexistent_xyz_abc_12345"));
    }

    // 14. list after changes

    @Test
    @Order(14)
    @DisplayName("list() - reflects renamed store after changes")
    void list_afterChanges_shouldReflectRename() {
        List<CoverageStoreSummary> list = coverageStores.list(WS);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(cs -> cs.getName().equals(CS_MAIN + "_renamed")),
                "renamed store must appear in list");
        assertTrue(list.stream().noneMatch(cs -> cs.getName().equals(CS_MAIN)),
                "original name must not appear in list after rename");
    }

    // 15-17. uploadFile
    // byte.tif: GDAL official test GeoTIFF (20x20 px, NAD27/UTM zone 11N, 736 bytes)
    // https://raw.githubusercontent.com/OSGeo/gdal/master/autotest/gcore/data/byte.tif

    @Test
    @Order(15)
    @DisplayName("uploadFile() - configure=none (store auto-created, no coverage) → 201")
    void uploadFile_configureNone_shouldCreateStore() {
        assertDoesNotThrow(() -> coverageStores.uploadFile(WS, CS_UP_NONE, "file", "geotiff", BYTE_TIF));

        assertTrue(coverageStores.exists(WS, CS_UP_NONE), "store must be created");

        CoverageStore cs = coverageStores.get(WS, CS_UP_NONE);
        assertEquals(CS_UP_NONE, cs.getName());
        assertEquals("GeoTIFF", cs.getType());
        assertTrue(cs.isEnabled(), "upload-created store defaults to enabled=true");
        assertNotNull(cs.getUrl(), "url field must be set after upload");
    }

    @Test
    @Order(16)
    @DisplayName("uploadFile() - configure=first (store+coverage auto-created) → 201")
    void uploadFile_configureFirst_shouldCreateStoreAndCoverage() {
        assertDoesNotThrow(() ->
                coverageStores.uploadFile(WS, CS_UP_FIRST, "file", "geotiff", BYTE_TIF,
                        "first", null, null));

        assertTrue(coverageStores.exists(WS, CS_UP_FIRST), "store must be created");

        CoverageStore cs = coverageStores.get(WS, CS_UP_FIRST);
        assertEquals(CS_UP_FIRST, cs.getName());
        assertEquals("GeoTIFF", cs.getType());
        assertTrue(cs.isEnabled());
        assertNotNull(cs.getUrl());
        assertNotNull(cs.getCoverages(), "coverages link must be present after configure=first");
    }

    @Test
    @Order(17)
    @DisplayName("uploadFile() - re-upload to existing store replaces file → 200")
    void uploadFile_toExistingStore_shouldUpdateFile() {
        assertDoesNotThrow(() ->
                coverageStores.uploadFile(WS, CS_UP_FIRST, "file", "geotiff", BYTE_TIF,
                        "first", null, null));

        assertTrue(coverageStores.exists(WS, CS_UP_FIRST));
    }
}
