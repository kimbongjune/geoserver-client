package io.github.kimbongjune.geoserverclient.api.layer;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.LayerNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] LayerManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LayerManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS           = System.currentTimeMillis();
    private static final String WS           = "lyr_ws_" + TS;

    // CoverageStore names (each holds one uploaded GeoTIFF → one auto-created Layer)
    private static final String CS_MAIN      = "lyr_cs_main_"   + TS;  // for get/exists/update tests
    private static final String CS_DEL_NR    = "lyr_cs_delnr_"  + TS;  // delete recurse=false
    private static final String CS_DEL_R     = "lyr_cs_delr_"   + TS;  // delete recurse=true

    // Layer names equal their CS names (GeoTIFF upload configure=first)
    private static final String LAYER_MAIN   = CS_MAIN;
    private static final String LAYER_DEL_NR = CS_DEL_NR;
    private static final String LAYER_DEL_R  = CS_DEL_R;

    private static File BYTE_TIF;
    private LayerManager layers;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing from src/test/resources");
        BYTE_TIF = new File(res.toURI());

        // Create workspace
        client.workspaces().create(CreateWorkspaceRequest.builder(WS));

        // Upload byte.tif × 3 → 3 CoverageStores + 3 Coverages + 3 Layers (auto-created)
        client.coverageStores().uploadFile(WS, CS_MAIN,   "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, CS_DEL_NR, "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, CS_DEL_R,  "file", "geotiff", BYTE_TIF, "first", null, null);

        layers = client.layers();
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── 1. list() ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() global list contains pre-created layer (ws:name format)")
    void list_global_containsLayer() {
        List<LayerSummary> list = layers.list();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        String fullName = WS + ":" + LAYER_MAIN;
        assertTrue(list.stream().anyMatch(s -> fullName.equals(s.getName())),
                "Expected '" + fullName + "' in global list: " + list);
        list.forEach(s -> {
            assertNotNull(s.getName());
            assertNotNull(s.getHref());
        });
    }

    // ── 2. listByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] listByWorkspace(ws) contains pre-created layer (no prefix)")
    void listByWorkspace_containsLayer() {
        List<LayerSummary> list = layers.listByWorkspace(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> LAYER_MAIN.equals(s.getName())),
                "Expected '" + LAYER_MAIN + "' in workspace list: " + list);
    }

    // ── 3. get() - global path ─────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] get(ws:name) global get returns RASTER layer")
    void get_globalPath_returnsLayer() {
        Layer layer = layers.get(WS + ":" + LAYER_MAIN);
        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals("RASTER", layer.getType());
        assertNotNull(layer.getDefaultStyle(), "defaultStyle should not be null");
        assertNotNull(layer.getResource(), "resource should not be null");
        assertEquals("coverage", layer.getResource().getResourceClass());
        assertNotNull(layer.getAttribution(), "attribution always present");
        assertNotNull(layer.getDateCreated());
        assertNull(layer.getDateModified(), "dateModified absent for fresh layers (not yet PUT)");
    }

    // ── 4. getByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] getByWorkspace(ws, name) returns same layer")
    void getByWorkspace_returnsLayer() {
        Layer layer = layers.getByWorkspace(WS, LAYER_MAIN);
        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals("RASTER", layer.getType());
        // queryable/opaque absent in GET response until explicitly set via PUT (tested in [9]-[12])
        assertNull(layer.getQueryable(), "queryable absent for fresh layer");
        assertNull(layer.getOpaque(), "opaque absent for fresh layer");
    }

    // ── 5. exists() - global path ──────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("[5] exists(ws:name) returns true for existing layer")
    void exists_globalPath_returnsTrue() {
        assertTrue(layers.exists(WS + ":" + LAYER_MAIN));
    }

    // ── 6. existsByWorkspace() ─────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] existsByWorkspace(ws, name) returns true")
    void existsByWorkspace_returnsTrue() {
        assertTrue(layers.existsByWorkspace(WS, LAYER_MAIN));
    }

    // ── 7. get() - not found ───────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] get(nonexistent) throws LayerNotFoundException")
    void get_notFound_throwsException() {
        assertThrows(LayerNotFoundException.class,
                () -> layers.get(WS + ":nonexistent_layer_xyz"));
    }

    // ── 8. existsByWorkspace() - not found ────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] existsByWorkspace(ws, nonexistent) returns false")
    void existsByWorkspace_notFound_returnsFalse() {
        assertFalse(layers.existsByWorkspace(WS, "nonexistent_layer_xyz"));
    }

    // ── 9. update() - global path ─────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] update(ws:name) - queryable=false, opaque=true")
    void update_globalPath_changesFields() {
        Layer updated = layers.update(WS + ":" + LAYER_MAIN,
                UpdateLayerRequest.builder()
                        .queryable(false)
                        .opaque(true)
                        .build());
        assertNotNull(updated);
        assertEquals(Boolean.FALSE, updated.getQueryable());
        assertEquals(Boolean.TRUE,  updated.getOpaque());
    }

    // ── 10. verify update ─────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[10] getByWorkspace after update - changes persisted")
    void getByWorkspace_afterUpdate_changesVisible() {
        Layer layer = layers.getByWorkspace(WS, LAYER_MAIN);
        assertEquals(Boolean.FALSE, layer.getQueryable(), "queryable should be false after update");
        assertEquals(Boolean.TRUE,  layer.getOpaque(),   "opaque should be true after update");
    }

    // ── 11. updateByWorkspace() ────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] updateByWorkspace(ws, name) - restore queryable=true, opaque=false, set attribution")
    void updateByWorkspace_restoresAndSetsAttribution() {
        Layer updated = layers.updateByWorkspace(WS, LAYER_MAIN,
                UpdateLayerRequest.builder()
                        .queryable(true)
                        .opaque(false)
                        .attribution("Test Provider", "http://example.com")
                        .build());
        assertNotNull(updated);
        assertEquals(Boolean.TRUE,  updated.getQueryable());
        assertEquals(Boolean.FALSE, updated.getOpaque());
    }

    // ── 12. verify updateByWorkspace ──────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] get after updateByWorkspace - attribution title persisted")
    void get_afterUpdateByWorkspace_attributionPersisted() {
        Layer layer = layers.getByWorkspace(WS, LAYER_MAIN);
        assertEquals(Boolean.TRUE,  layer.getQueryable(), "queryable should be restored to true");
        assertEquals(Boolean.FALSE, layer.getOpaque(),    "opaque should be restored to false");
        assertNotNull(layer.getAttribution());
        assertEquals("Test Provider", layer.getAttribution().getTitle(),
                "attribution title should be 'Test Provider'");
    }

    // ── 13. deleteByWorkspace() ────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("[13] deleteByWorkspace() - layer deleted, coverage remains")
    void deleteByWorkspace_layerDeletedCoverageRemains() {
        // Pre-condition: layer and coverage exist
        assertTrue(layers.existsByWorkspace(WS, LAYER_DEL_NR), "layer should exist before delete");
        assertDoesNotThrow(() -> client.coverages().get(WS, CS_DEL_NR, LAYER_DEL_NR),
                "coverage should exist before delete");

        // Layer DELETE removes the catalog entry only; resource is unaffected
        layers.deleteByWorkspace(WS, LAYER_DEL_NR);

        // Layer should be gone
        assertFalse(layers.existsByWorkspace(WS, LAYER_DEL_NR),
                "layer should not exist after delete");

        // Coverage should still exist — Layer DELETE does not cascade to the resource
        assertDoesNotThrow(() -> client.coverages().get(WS, CS_DEL_NR, LAYER_DEL_NR),
                "coverage should remain after layer-only delete");
    }

    // ── 14. delete() ────────────────────────────────────────────────────

    @Test
    @Order(14)
    @DisplayName("[14] delete() - layer entry deleted, coverage unaffected")
    void delete_layerEntryDeletedCoverageUnaffected() {
        // Pre-condition: layer exists
        assertTrue(layers.existsByWorkspace(WS, LAYER_DEL_R), "layer should exist before delete");
        assertDoesNotThrow(() -> client.coverages().get(WS, CS_DEL_R, LAYER_DEL_R),
                "coverage should exist before delete");

        // Global-path delete
        layers.delete(WS + ":" + LAYER_DEL_R);

        // Layer should be gone
        assertFalse(layers.existsByWorkspace(WS, LAYER_DEL_R),
                "layer should not exist after delete");

        // Coverage should still exist — Layer DELETE does not cascade to the resource
        assertDoesNotThrow(() -> client.coverages().get(WS, CS_DEL_R, LAYER_DEL_R),
                "coverage should remain after layer-only delete");
    }
}
