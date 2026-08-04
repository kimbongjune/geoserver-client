package io.github.kimbongjune.geoserverclient.api.coverage;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.coverage.Coverage;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.coverage.CreateCoverageRequest;
import io.github.kimbongjune.geoserverclient.dto.coverage.UpdateCoverageRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.CoverageNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CoverageManager (GeoServer 2.28.2, localhost:9090).
 *
 * <p>Setup:
 * <ul>
 *   <li>CS_MAIN: byte.tif uploaded (configure=first) → coverage COV_MAIN auto-created</li>
 *   <li>CS_NONE: byte.tif uploaded (configure=none) → no coverage (used for create() test)</li>
 *   <li>CS_DEL_T: byte.tif uploaded (configure=first) → coverage COV_DEL auto-created (used for delete test)</li>
 * </ul>
 *
 * <p>Test order:
 * 1 list, 2 create, 3 create-duplicate, 4 get, 5 get-notfound,
 * 6 getByWorkspace, 7 exists-true, 8 exists-false, 9 listByWorkspace,
 * 10 listNative, 11 update-title, 12 update-rename, 13 update-notfound,
 * 14 reset, 15 delete-recurse-false-has-layer, 16 delete-recurse-true, 17 delete-notfound,
 * 18 list-after-changes
 */
@DisplayName("[IntegrationTest] CoverageManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoverageManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS          = System.currentTimeMillis();
    private static final String WS          = "cov_ws_"      + TS;
    private static final String CS_MAIN     = "cov_cs_main_" + TS;   // configure=first → coverage auto-created
    private static final String CS_NONE     = "cov_cs_none_" + TS;   // configure=none → no coverage
    private static final String CS_DEL_T    = "cov_cs_dt_"   + TS;   // configure=first → for delete test
    private static final String CS_WS_NEW   = "cov_cs_wsnew_" + TS;  // configure=none → for createByWorkspace() test
    private static final String COV_MAIN    = CS_MAIN;                // coverage name = store name (configure=first, coverageName=null)
    private static final String COV_DEL     = CS_DEL_T;              // coverage name = store name
    // isNew mode: GeoServer saves the file as {storeName}.geotiff → native coverage name = storeName
    // → POST name must also equal storeName so the reader can locate it
    private static final String COV_CREATED = CS_NONE;               // manually created via POST (name = native name = storeName)
    private static final String COV_RENAMED = "cov_cv_ren_"  + TS;   // renamed from COV_MAIN

    private static File BYTE_TIF;

    private CoverageManager coverages;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        BYTE_TIF = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));

        // CS_MAIN: configure=first → COV_MAIN auto-created
        client.coverageStores().uploadFile(WS, CS_MAIN, "file", "geotiff", BYTE_TIF, "first", null, null);
        // CS_NONE: configure=none → no coverage
        client.coverageStores().uploadFile(WS, CS_NONE, "file", "geotiff", BYTE_TIF, "none", null, null);
        // CS_DEL_T: configure=first → COV_DEL auto-created (for delete recurse=true test)
        client.coverageStores().uploadFile(WS, CS_DEL_T, "file", "geotiff", BYTE_TIF, "first", null, null);
        // CS_WS_NEW: configure=none → for createByWorkspace() test
        client.coverageStores().uploadFile(WS, CS_WS_NEW, "file", "geotiff", BYTE_TIF, "none", null, null);

        coverages = client.coverages();
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // 1. list

    @Test
    @Order(1)
    @DisplayName("[1] list() — CS_MAIN has 1 coverage (COV_MAIN)")
    void list_returnsOneCoverage() {
        List<CoverageSummary> list = coverages.list(WS, CS_MAIN);
        assertNotNull(list);
        assertEquals(1, list.size(), "CS_MAIN must have exactly 1 coverage");
        assertEquals(COV_MAIN, list.get(0).getName(), "coverage name must equal store name");
        assertNotNull(list.get(0).getHref(), "href must be present");
    }

    // 2. create

    @Test
    @Order(2)
    @DisplayName("[2] create() — register via isNew mode into CS_NONE")
    void create_isNewMode() {
        Coverage cov = coverages.create(WS, CS_NONE,
                CreateCoverageRequest.isNew(COV_CREATED));

        assertNotNull(cov, "created coverage must not be null");
        assertEquals(COV_CREATED, cov.getName(), "name");
        assertTrue(cov.isEnabled(),              "enabled (default true)");
        assertNotNull(cov.getSrs(),              "srs must be set (reader auto-detects in isNew mode)");
        assertNotNull(cov.getNativeFormat(),     "nativeFormat must be present");
    }

    // 3. create duplicate

    @Test
    @Order(3)
    @DisplayName("[3] create() duplicate name → ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> coverages.create(WS, CS_NONE,
                        CreateCoverageRequest.isNew(COV_CREATED)));
    }

    // 4. get

    @Test
    @Order(4)
    @DisplayName("[4] get() — full field verification via store path")
    void get_fullFields() {
        Coverage cov = coverages.get(WS, CS_MAIN, COV_MAIN);

        assertNotNull(cov,                              "coverage not null");
        assertEquals(COV_MAIN, cov.getName(),          "name");
        assertNotNull(cov.getNativeName(),              "nativeName");
        assertNotNull(cov.getSrs(),                     "srs");
        assertNotNull(cov.getProjectionPolicy(),        "projectionPolicy");
        assertTrue(cov.isEnabled(),                     "enabled");
        assertNotNull(cov.getNativeFormat(),            "nativeFormat");
        assertNotNull(cov.getNamespace(),               "namespace");
        assertEquals(WS, cov.getNamespace().getName(), "namespace.name");
        assertNotNull(cov.getStore(),                   "store");
        assertTrue(cov.getStore().getName().contains(CS_MAIN), "store.name contains cs name");
        assertNotNull(cov.getDefaultInterpolationMethod(), "defaultInterpolationMethod");
    }

    // 5. get not found

    @Test
    @Order(5)
    @DisplayName("[5] get() nonexistent coverage → CoverageNotFoundException")
    void get_notFound_throwsException() {
        assertThrows(CoverageNotFoundException.class,
                () -> coverages.get(WS, CS_MAIN, "nonexistent_cov_xyz_12345"));
    }

    // 6. getByWorkspace

    @Test
    @Order(6)
    @DisplayName("[6] getByWorkspace() — same result as get() via store path")
    void getByWorkspace_sameResult() {
        Coverage byStore     = coverages.get(WS, CS_MAIN, COV_MAIN);
        Coverage byWorkspace = coverages.getByWorkspace(WS, COV_MAIN);

        assertNotNull(byWorkspace);
        assertEquals(byStore.getName(),   byWorkspace.getName(),   "name matches");
        assertEquals(byStore.getSrs(),    byWorkspace.getSrs(),    "srs matches");
        assertEquals(byStore.isEnabled(), byWorkspace.isEnabled(), "enabled matches");
    }

    // 7. exists true

    @Test
    @Order(7)
    @DisplayName("[7] exists() existing coverage → true")
    void exists_true() {
        assertTrue(coverages.exists(WS, CS_MAIN, COV_MAIN), "COV_MAIN must exist");
    }

    // 8. exists false

    @Test
    @Order(8)
    @DisplayName("[8] exists() nonexistent coverage → false")
    void exists_false() {
        assertFalse(coverages.exists(WS, CS_MAIN, "nonexistent_cov_xyz_12345"),
                "nonexistent coverage must return false");
    }

    // 9. listByWorkspace

    @Test
    @Order(9)
    @DisplayName("[9] listByWorkspace() — returns all coverages in WS")
    void listByWorkspace_returnsAll() {
        List<CoverageSummary> list = coverages.listByWorkspace(WS);
        assertNotNull(list, "list must not be null");
        // CS_MAIN(1) + CS_NONE/COV_CREATED(1) + CS_DEL_T(1) = at least 3
        assertTrue(list.size() >= 3,
                "expected at least 3 coverages in workspace, got: " + list.size());
    }

    // 10. listNative

    @Test
    @Order(10)
    @DisplayName("[10] listNative() — retrieves native names directly from reader")
    void listNative_returnsNativeNames() {
        List<String> natives = coverages.listNative(WS, CS_MAIN);
        assertNotNull(natives, "native list must not be null");
        assertFalse(natives.isEmpty(), "at least one native coverage name expected");
    }

    // 11. update title

    @Test
    @Order(11)
    @DisplayName("[11] update() — changes title")
    void update_title() {
        Coverage updated = coverages.update(WS, CS_MAIN, COV_MAIN,
                UpdateCoverageRequest.builder()
                        .title("Updated Title by Test")
                        .description("Updated description by test")
                        .build());

        assertNotNull(updated, "updated coverage must not be null");
        assertEquals("Updated Title by Test",        updated.getTitle(),       "title updated");
        assertEquals("Updated description by test",  updated.getDescription(), "description updated");
    }

    // 12. update rename

    @Test
    @Order(12)
    @DisplayName("[12] update() — rename coverage")
    void update_rename() {
        Coverage renamed = coverages.update(WS, CS_MAIN, COV_MAIN,
                UpdateCoverageRequest.builder()
                        .name(COV_RENAMED)
                        .build());

        assertNotNull(renamed, "renamed coverage must not be null");
        assertEquals(COV_RENAMED, renamed.getName(), "new name applied");
    }

    // 13. update not found

    @Test
    @Order(13)
    @DisplayName("[13] update() nonexistent coverage → CoverageNotFoundException")
    void update_notFound_throwsException() {
        assertThrows(CoverageNotFoundException.class,
                () -> coverages.update(WS, CS_MAIN, "nonexistent_cov_xyz_12345",
                        UpdateCoverageRequest.builder().title("X").build()));
    }

    // 14. reset

    @Test
    @Order(14)
    @DisplayName("[14] reset() — completes without exception")
    void reset_noException() {
        // Order 12 renamed COV_MAIN to COV_RENAMED
        assertDoesNotThrow(() -> coverages.reset(WS, CS_MAIN, COV_RENAMED));
    }

    // 15. delete recurse=false (has layer → 403)

    @Test
    @Order(15)
    @DisplayName("[15] delete(recurse=false) with layer → AuthenticationException(403)")
    void delete_recursefalse_withLayer_throws403() {
        // COV_CREATED was created via create() → layer also auto-created → recurse=false returns 403
        assertThrows(AuthenticationException.class,
                () -> coverages.delete(WS, CS_NONE, COV_CREATED, false));

        // coverage must still exist after rejected delete
        assertTrue(coverages.exists(WS, CS_NONE, COV_CREATED),
                "coverage rejected by 403 must still exist");
    }

    // 16. delete recurse=true

    @Test
    @Order(16)
    @DisplayName("[16] delete(recurse=true) — deletes coverage and its layer")
    void delete_recursetrue_success() {
        assertDoesNotThrow(() -> coverages.delete(WS, CS_DEL_T, COV_DEL, true));
        assertFalse(coverages.exists(WS, CS_DEL_T, COV_DEL), "coverage must not exist after delete");
    }

    // 17. delete not found

    @Test
    @Order(17)
    @DisplayName("[17] delete() nonexistent coverage → CoverageNotFoundException")
    void delete_notFound_throwsException() {
        assertThrows(CoverageNotFoundException.class,
                () -> coverages.delete(WS, CS_MAIN, "nonexistent_cov_xyz_12345"));
    }

    // 18. list after changes

    @Test
    @Order(18)
    @DisplayName("[18] list() — reflects rename (COV_MAIN → COV_RENAMED)")
    void list_afterChanges_reflectsRename() {
        List<CoverageSummary> list = coverages.list(WS, CS_MAIN);
        assertNotNull(list);
        assertEquals(1, list.size(), "CS_MAIN must still have 1 coverage");
        assertEquals(COV_RENAMED, list.get(0).getName(),
                "coverage name must reflect rename to COV_RENAMED");
    }

    // 19. createByWorkspace

    @Test
    @Order(19)
    @DisplayName("[19] createByWorkspace() — registers coverage with store named in body (no store in URL)")
    void createByWorkspace_registersCoverage() {
        // The store must already exist and the native coverage name must match a name from
        // listNative(): for a single-file GeoTIFF store, that's the store name itself.
        List<String> natives = coverages.listNative(WS, CS_WS_NEW);
        assertFalse(natives.isEmpty(), "CS_WS_NEW must expose at least one native coverage name");
        String nativeName = natives.get(0);

        Coverage cov = coverages.createByWorkspace(WS, CS_WS_NEW,
                CreateCoverageRequest.of(CS_WS_NEW).nativeCoverageName(nativeName));

        assertNotNull(cov, "created coverage must not be null");
        assertEquals(CS_WS_NEW, cov.getName(), "name");
        assertNotNull(cov.getStore(), "store must be present");
        assertTrue(cov.getStore().getName().contains(CS_WS_NEW), "store.name must reference CS_WS_NEW");
        assertTrue(coverages.exists(WS, CS_WS_NEW, CS_WS_NEW), "coverage must exist after createByWorkspace()");
    }

    @Test
    @Order(20)
    @DisplayName("[20] createByWorkspace() duplicate name → ResourceAlreadyExistsException")
    void createByWorkspace_duplicate_throwsException() {
        List<String> natives = coverages.listNative(WS, CS_WS_NEW);
        String nativeName = natives.get(0);
        assertThrows(ResourceAlreadyExistsException.class,
                () -> coverages.createByWorkspace(WS, CS_WS_NEW,
                        CreateCoverageRequest.of(CS_WS_NEW).nativeCoverageName(nativeName)));
    }
}
