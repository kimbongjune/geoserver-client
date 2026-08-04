package io.github.kimbongjune.geoserverclient.api.featuretype;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureType;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.dto.featuretype.UpdateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.FeatureTypeNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link FeatureTypeManager}.
 * <p>
 * Requires GeoServer at {@code http://localhost:9090/geoserver} (admin/geoserver).
 * Uses an H2 datastore to auto-create tables.
 */
@DisplayName("FeatureTypeManager Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeatureTypeManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS       = System.currentTimeMillis();
    private static final String WS       = "it_ft_ws_"   + TS;
    private static final String DS       = "it_ft_ds_"   + TS;
    private static final String FT_MAIN  = "it_ft_main_" + TS;
    private static final String FT_UPD   = "it_ft_upd_"  + TS;
    private static final String FT_RST   = "it_ft_rst_"  + TS;
    private static final String FT_DEL   = "it_ft_del_"  + TS;
    private static final String FT_NREC  = "it_ft_nrc_"  + TS; // recurse=false test

    // workspace-level (no-store) variants — single datastore in WS, so createByWorkspace can
    // resolve the target store unambiguously
    private static final String FT_WS_UPD  = "it_ft_wsupd_"  + TS;
    private static final String FT_WS_RST  = "it_ft_wsrst_"  + TS;
    private static final String FT_WS_DEL  = "it_ft_wsdel_"  + TS;
    private static final String FT_WS_NREC = "it_ft_wsnrc_"  + TS;
    private static final String FT_WS_MAIN = "it_ft_wsmain_" + TS;

    private FeatureTypeManager featureTypes;

    @BeforeAll
    void setUpResources() {
        // workspace + H2 datastore
        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        client.datastores().create(WS, h2Request(DS));
        featureTypes = client.featureTypes();

        // pre-create FTs for update / reset / delete / recurse=false scenarios
        featureTypes.create(WS, DS, ftRequest(FT_UPD));
        featureTypes.create(WS, DS, ftRequest(FT_RST));
        featureTypes.create(WS, DS, ftRequest(FT_DEL));
        featureTypes.create(WS, DS, ftRequest(FT_NREC));

        // pre-create FTs for the workspace-level (no-store) variants — WS has exactly one
        // datastore (DS), so createByWorkspace can auto-resolve the target store.
        featureTypes.createByWorkspace(WS, ftRequest(FT_WS_UPD));
        featureTypes.createByWorkspace(WS, ftRequest(FT_WS_RST));
        featureTypes.createByWorkspace(WS, ftRequest(FT_WS_DEL));
        featureTypes.createByWorkspace(WS, ftRequest(FT_WS_NREC));
    }

    @AfterAll
    void cleanUp() {
        // delete workspace with recurse=true → removes all datastores, FTs, and layers
        client.workspaces().delete(WS, true);
    }

    // ── 1. list ────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("list() - returns pre-created featureType list")
    void list_shouldReturnPreCreated() {
        List<FeatureTypeSummary> list = featureTypes.list(WS, DS);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "FTs created in BeforeAll must be present");
        list.forEach(ft -> {
            assertNotNull(ft.getName());
            assertNotNull(ft.getHref());
        });
    }

    // ── 2. create ──────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("create() - creates FeatureType and returns detail")
    void create_shouldCreateFeatureType() {
        FeatureType ft = featureTypes.create(WS, DS, ftRequest(FT_MAIN));
        assertNotNull(ft);
        assertEquals(FT_MAIN, ft.getName());
        assertNotNull(ft.getSrs());
        assertNotNull(ft.getStore());
    }

    @Test
    @Order(3)
    @DisplayName("create() - duplicate name → ResourceAlreadyExistsException")
    void create_duplicate_shouldThrow() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> featureTypes.create(WS, DS, ftRequest(FT_MAIN)));
    }

    // ── 3. get ─────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("get() - returns FeatureType detail")
    void get_shouldReturnFeatureType() {
        FeatureType ft = featureTypes.get(WS, DS, FT_MAIN);
        assertNotNull(ft);
        assertEquals(FT_MAIN, ft.getName());
        assertNotNull(ft.getSrs());
        assertNotNull(ft.getStore());
        assertNotNull(ft.getAttributes());
        assertFalse(ft.getAttributes().getAttribute().isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("get() - nonexistent FT → FeatureTypeNotFoundException")
    void get_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.get(WS, DS, "nonexistent_ft_xyz"));
    }

    // ── 4. exists ──────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("exists() - existing FT → true")
    void exists_shouldReturnTrue() {
        assertTrue(featureTypes.exists(WS, DS, FT_MAIN));
    }

    @Test
    @Order(7)
    @DisplayName("exists() - nonexistent FT → false")
    void exists_notFound_shouldReturnFalse() {
        assertFalse(featureTypes.exists(WS, DS, "nonexistent_ft_xyz"));
    }

    // ── 5. list (after create) ─────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("list() - FT_MAIN appears in list")
    void list_afterCreate_shouldContainMain() {
        List<FeatureTypeSummary> list = featureTypes.list(WS, DS);
        assertTrue(list.stream().anyMatch(s -> FT_MAIN.equals(s.getName())),
                "list() must contain FT_MAIN");
    }

    // ── 6. listAvailable ───────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("listAvailable() - all configured → empty list")
    void listAvailable_allConfigured_shouldBeEmpty() {
        List<String> available = featureTypes.listAvailable(WS, DS);
        assertNotNull(available);
        assertTrue(available.isEmpty(),
                "H2: all tables already configured, so available list must be empty");
    }

    // ── 7. update ──────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("update() - changes title")
    void update_shouldChangeTitle() {
        String newTitle = "Updated Title " + TS;
        FeatureType updated = featureTypes.update(WS, DS, FT_UPD,
                UpdateFeatureTypeRequest.builder().title(newTitle).build());
        assertNotNull(updated);
        assertEquals(newTitle, updated.getTitle());
    }

    @Test
    @Order(11)
    @DisplayName("update() - nonexistent FT → FeatureTypeNotFoundException")
    void update_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.update(WS, DS, "nonexistent_ft_xyz",
                        UpdateFeatureTypeRequest.builder().title("x").build()));
    }

    // ── 8. reset ───────────────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("reset() - cache reset succeeds (200)")
    void reset_shouldSucceed() {
        assertDoesNotThrow(() -> featureTypes.reset(WS, DS, FT_RST));
    }

    // ── 9. delete ──────────────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("delete(recurse=false) - has layer → 403")
    void delete_recursefalse_withLayer_shouldThrow403() {
        // create() auto-creates a Layer, so recurse=false should return 403
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> featureTypes.delete(WS, DS, FT_NREC, false));
        assertEquals(403, ex.getStatusCode());
    }

    @Test
    @Order(14)
    @DisplayName("delete() - recurse=true (default) → succeeds, layer also deleted")
    void delete_shouldDeleteWithRecurse() {
        assertDoesNotThrow(() -> featureTypes.delete(WS, DS, FT_DEL));
        assertFalse(featureTypes.exists(WS, DS, FT_DEL));
    }

    @Test
    @Order(15)
    @DisplayName("delete() - nonexistent FT → FeatureTypeNotFoundException")
    void delete_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.delete(WS, DS, "nonexistent_ft_xyz"));
    }

    // ── 10. workspace-level (no-store) variants ───────────────────────────

    @Test
    @Order(16)
    @DisplayName("createByWorkspace() - creates FeatureType via auto-resolved store")
    void createByWorkspace_shouldCreateFeatureType() {
        FeatureType ft = featureTypes.createByWorkspace(WS, ftRequest(FT_WS_MAIN));
        assertNotNull(ft);
        assertEquals(FT_WS_MAIN, ft.getName());
        assertNotNull(ft.getSrs());
        assertNotNull(ft.getStore());
        // GeoServer auto-picked WS's single datastore (DS)
        assertTrue(ft.getStore().getName().contains(DS));
    }

    @Test
    @Order(17)
    @DisplayName("createByWorkspace() - duplicate name -> ResourceAlreadyExistsException")
    void createByWorkspace_duplicate_shouldThrow() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> featureTypes.createByWorkspace(WS, ftRequest(FT_WS_MAIN)));
    }

    @Test
    @Order(18)
    @DisplayName("getByWorkspace() - returns FeatureType detail without a store")
    void getByWorkspace_shouldReturnFeatureType() {
        FeatureType ft = featureTypes.getByWorkspace(WS, FT_WS_MAIN);
        assertNotNull(ft);
        assertEquals(FT_WS_MAIN, ft.getName());
        assertNotNull(ft.getSrs());
        assertNotNull(ft.getStore());
    }

    @Test
    @Order(19)
    @DisplayName("getByWorkspace() - nonexistent FT -> FeatureTypeNotFoundException")
    void getByWorkspace_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.getByWorkspace(WS, "nonexistent_ft_xyz"));
    }

    @Test
    @Order(20)
    @DisplayName("listByWorkspace() - returns featureTypes across the whole workspace")
    void listByWorkspace_shouldReturnAll() {
        List<FeatureTypeSummary> list = featureTypes.listByWorkspace(WS);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(s -> FT_WS_MAIN.equals(s.getName())),
                "listByWorkspace() must contain FT_WS_MAIN");
        // also contains store-scoped FTs created earlier, since they live in the same workspace
        assertTrue(list.stream().anyMatch(s -> FT_MAIN.equals(s.getName())),
                "listByWorkspace() must also contain store-scoped FTs in the same workspace");
    }

    @Test
    @Order(21)
    @DisplayName("updateByWorkspace() - changes title without specifying a store")
    void updateByWorkspace_shouldChangeTitle() {
        String newTitle = "Updated WS Title " + TS;
        FeatureType updated = featureTypes.updateByWorkspace(WS, FT_WS_UPD,
                UpdateFeatureTypeRequest.builder().title(newTitle).build());
        assertNotNull(updated);
        assertEquals(newTitle, updated.getTitle());
    }

    @Test
    @Order(22)
    @DisplayName("updateByWorkspace() - nonexistent FT -> FeatureTypeNotFoundException")
    void updateByWorkspace_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.updateByWorkspace(WS, "nonexistent_ft_xyz",
                        UpdateFeatureTypeRequest.builder().title("x").build()));
    }

    @Test
    @Order(23)
    @DisplayName("resetByWorkspace() - cache reset succeeds (200)")
    void resetByWorkspace_shouldSucceed() {
        assertDoesNotThrow(() -> featureTypes.resetByWorkspace(WS, FT_WS_RST));
    }

    @Test
    @Order(24)
    @DisplayName("deleteByWorkspace(recurse=false) - has layer -> 403")
    void deleteByWorkspace_recursefalse_withLayer_shouldThrow403() {
        // createByWorkspace() auto-creates a Layer, so recurse=false should return 403
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> featureTypes.deleteByWorkspace(WS, FT_WS_NREC, false));
        assertEquals(403, ex.getStatusCode());
    }

    @Test
    @Order(25)
    @DisplayName("deleteByWorkspace() - recurse=true (default) -> succeeds, layer also deleted")
    void deleteByWorkspace_shouldDeleteWithRecurse() {
        assertDoesNotThrow(() -> featureTypes.deleteByWorkspace(WS, FT_WS_DEL));
        assertFalse(featureTypes.exists(WS, DS, FT_WS_DEL));
    }

    @Test
    @Order(26)
    @DisplayName("deleteByWorkspace() - nonexistent FT -> FeatureTypeNotFoundException")
    void deleteByWorkspace_notFound_shouldThrow() {
        assertThrows(FeatureTypeNotFoundException.class,
                () -> featureTypes.deleteByWorkspace(WS, "nonexistent_ft_xyz"));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private static CreateFeatureTypeRequest ftRequest(String name) {
        return CreateFeatureTypeRequest.of(name)
                .srs("EPSG:4326")
                .title(name)
                .attribute("the_geom", "org.locationtech.jts.geom.Point", 0, 1, true)
                .attribute("name", "java.lang.String", 0, 1, true);
    }

    private static CreateDataStoreRequest h2Request(String storeName) {
        return CreateDataStoreRequest.of(storeName)
                .connectionParam("database", "it_test_" + storeName)
                .connectionParam("dbtype",   "h2");
    }
}
