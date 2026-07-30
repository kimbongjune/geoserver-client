package io.github.kimbongjune.geoserverclient.api.wmts;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.PublishWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.UpdateWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayerSummary;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WmtsLayerNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] WmtsLayerManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WmtsLayerManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS       = System.currentTimeMillis();
    private static final String WS       = "wtml_ws_" + TS;
    private static final String FEED_CS  = "wtml_feed_"  + TS;
    private static final String FEED_CS2 = "wtml_feed2_" + TS;

    // GeoServer GWC WMTS capabilities (self-cascading via internal port)
    private static final String CAPS_URL =
            "http://localhost:8080/geoserver/gwc/service/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities";

    // WMTS store names
    private static final String STORE       = "wtml_store_"  + TS;
    private static final String STORE_AVAIL = "wtml_st_av_"  + TS;  // for listAvailable (empty)

    // WMTS layer names (all reference GWC-published coverage layers)
    private static final String LAYER_DEL_NR = "wtml_del_nr_" + TS;  // pre-published, delete no-recurse
    private static final String LAYER_DEL_T  = "wtml_del_t_"  + TS;  // pre-published, delete recurse=true
    private static final String LAYER_MAIN   = "wtml_main_"   + TS;  // published in test [4]

    /** native WMTS layer name: WS:FEED_CS (GWC uses this format) */
    private static String NATIVE_LAYER;
    private static String NATIVE_LAYER2;

    private static File BYTE_TIF;
    private WmtsLayerManager wmtsLayers;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing from src/test/resources");
        BYTE_TIF = new File(res.toURI());
        NATIVE_LAYER  = WS + ":" + FEED_CS;
        NATIVE_LAYER2 = WS + ":" + FEED_CS2;

        // 1. workspace
        client.workspaces().create(CreateWorkspaceRequest.of(WS));

        // 2. upload two GeoTIFFs -> GWC auto-registers them as WMTS tile layers
        client.coverageStores().uploadFile(WS, FEED_CS,  "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, FEED_CS2, "file", "geotiff", BYTE_TIF, "first", null, null);

        // 3. create WMTS stores
        client.wmtsStores().create(WS, CreateWmtsStoreRequest.of(STORE,       CAPS_URL));
        client.wmtsStores().create(WS, CreateWmtsStoreRequest.of(STORE_AVAIL, CAPS_URL));

        // 4. pre-publish two layers in STORE for delete tests
        wmtsLayers = client.wmtsLayers();
        publishWmtsLayer(WS, STORE, LAYER_DEL_NR, NATIVE_LAYER);
        publishWmtsLayer(WS, STORE, LAYER_DEL_T,  NATIVE_LAYER2);
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    private void publishWmtsLayer(String ws, String store, String layerName, String nativeName) {
        wmtsLayers.publish(ws, store, PublishWmtsLayerRequest.of(layerName, nativeName));
    }

    // ── 1. list(ws, store) ──────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list(ws, store) returns pre-published layers")
    void list_byStore_returnsPrePublished() {
        List<WmtsLayerSummary> list = wmtsLayers.list(WS, STORE);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        list.forEach(s -> {
            assertNotNull(s.getName());
            assertNotNull(s.getHref());
        });
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_NR)));
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_T)));
    }

    // ── 2. list(ws) ─────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] list(ws) workspace-level list includes pre-published layers")
    void list_byWorkspace_includesPrePublished() {
        List<WmtsLayerSummary> list = wmtsLayers.list(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_NR)));
    }

    // ── 3. listAvailable ────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] listAvailable(ws, store) returns available WMTS tile layer names")
    void listAvailable_returnsAvailableLayerNames() {
        // STORE_AVAIL has no layers published -> both native layers should be available
        List<String> available = wmtsLayers.listAvailable(WS, STORE_AVAIL);
        assertNotNull(available);
        assertFalse(available.isEmpty(), "listAvailable should return at least one layer");
        assertTrue(available.contains(NATIVE_LAYER),
                "Expected '" + NATIVE_LAYER + "' in available list but got: " + available);
    }

    // ── 4-5. publish ────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] publish() full fields -> 201, returns WmtsLayer")
    void publish_fullFields() {
        WmtsLayer layer = wmtsLayers.publish(WS, STORE,
                PublishWmtsLayerRequest.of(LAYER_MAIN, NATIVE_LAYER)
                        .title("Integration Test WMTS Layer")
                        .description("wmts layer test")
                        .enabled(true)
                        .advertised(true));

        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
        assertTrue(layer.isEnabled());
        assertNotNull(layer.getSrs());
        assertNotNull(layer.getNativeBoundingBox());
        assertNotNull(layer.getLatLonBoundingBox());
        assertNotNull(layer.getStore());
        assertNotNull(layer.getNamespace());
        assertEquals(WS, layer.getNamespace().getName());
    }

    @Test
    @Order(5)
    @DisplayName("[5] publish() duplicate name -> ResourceAlreadyExistsException")
    void publish_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> wmtsLayers.publish(WS, STORE,
                        PublishWmtsLayerRequest.of(LAYER_MAIN, NATIVE_LAYER)));
    }

    // ── 6-8. get ────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] get(ws, store, layer) returns full details")
    void get_byStore_fullDetails() {
        WmtsLayer layer = wmtsLayers.get(WS, STORE, LAYER_MAIN);

        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
        assertTrue(layer.isEnabled());
        assertNotNull(layer.getSrs());
        assertNotNull(layer.getNativeBoundingBox());
        assertNotNull(layer.getLatLonBoundingBox());
        assertNotNull(layer.getProjectionPolicy());
        assertNotNull(layer.getStore());
        assertEquals("wmtsStore", layer.getStore().getStoreClass());
        assertNotNull(layer.getNamespace());
    }

    @Test
    @Order(7)
    @DisplayName("[7] get(ws, layer) workspace-level returns same layer")
    void get_byWorkspace_returnsLayer() {
        WmtsLayer layer = wmtsLayers.get(WS, LAYER_MAIN);

        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
    }

    @Test
    @Order(8)
    @DisplayName("[8] get() nonexistent -> WmtsLayerNotFoundException")
    void get_nonExistent_throwsException() {
        WmtsLayerNotFoundException ex = assertThrows(WmtsLayerNotFoundException.class,
                () -> wmtsLayers.get(WS, STORE, "nonexistent_wtml_xyz_12345"));
        assertNotNull(ex.getLayerName());
    }

    // ── 9-10. exists ────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] exists() existing layer -> true")
    void exists_true() {
        assertTrue(wmtsLayers.exists(WS, STORE, LAYER_MAIN));
    }

    @Test
    @Order(10)
    @DisplayName("[10] exists() nonexistent -> false")
    void exists_false() {
        assertFalse(wmtsLayers.exists(WS, STORE, "nonexistent_wtml_xyz_12345"));
    }

    // ── 11. update (WmtsLayer PUT works normally, unlike WmsLayer) ───────

    @Test
    @Order(11)
    @DisplayName("[11] update() title change -> 200, returns updated WmtsLayer")
    void update_titleChange() {
        WmtsLayer updated = wmtsLayers.update(WS, STORE, LAYER_MAIN,
                UpdateWmtsLayerRequest.builder()
                        .title("Updated WMTS Title")
                        .build());

        assertNotNull(updated);
        assertEquals(LAYER_MAIN, updated.getName());
        assertEquals("Updated WMTS Title", updated.getTitle());
    }

    // ── 12-14. delete ────────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] delete(recurse=false) with LayerInfo -> AuthenticationException(403)")
    void delete_noRecurse_withLayerInfo_throws403() {
        // WmtsLayer published via POST always has a LayerInfo -> recurse=false -> 403
        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> wmtsLayers.delete(WS, STORE, LAYER_DEL_NR, false));
        assertEquals(403, ex.getStatusCode());
        // layer must still exist after failed delete
        assertTrue(wmtsLayers.exists(WS, STORE, LAYER_DEL_NR));
    }

    @Test
    @Order(13)
    @DisplayName("[13] delete(recurse=true) with LayerInfo -> 200 cascade")
    void delete_recurse_withLayerInfo() {
        assertDoesNotThrow(() -> wmtsLayers.delete(WS, STORE, LAYER_DEL_T, true));
        assertFalse(wmtsLayers.exists(WS, STORE, LAYER_DEL_T));
    }

    @Test
    @Order(14)
    @DisplayName("[14] delete() nonexistent -> WmtsLayerNotFoundException")
    void delete_nonExistent_throwsException() {
        assertThrows(WmtsLayerNotFoundException.class,
                () -> wmtsLayers.delete(WS, STORE, "nonexistent_wtml_xyz_12345"));
    }

    // ── 15. list after changes ───────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("[15] list() after deletions reflects changes")
    void list_afterChanges() {
        List<WmtsLayerSummary> list = wmtsLayers.list(WS, STORE);
        assertNotNull(list);
        // LAYER_MAIN and LAYER_DEL_NR (not recursively deleted) still present
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_MAIN)));
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_NR)));
        // LAYER_DEL_T was recursively deleted
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(LAYER_DEL_T)));
    }
}
