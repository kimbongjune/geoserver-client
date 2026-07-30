package io.github.kimbongjune.geoserverclient.api.wms;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.UpdateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.exception.WmsStoreNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] WmsStoreManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WmsStoreManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS           = System.currentTimeMillis();
    private static final String WS           = "wms_ws_"    + TS;
    private static final String CAPS_URL     =
            "http://localhost:8080/geoserver/ows?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities";
    private static final String CAPS_URL_ALT =
            "http://localhost:8080/geoserver/cite/ows?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities";
    private static final String FEED_CS      = "wms_feed_"  + TS;
    private static final String FEED_CS2     = "wms_feed2_" + TS;

    private static final String STORE_MAIN   = "wms_main_"  + TS;
    private static final String STORE_UPD    = "wms_upd_"   + TS;
    private static final String STORE_DEL    = "wms_del_"   + TS;
    private static final String STORE_DEL_L  = "wms_del_l_" + TS;
    private static final String STORE_DEL_T  = "wms_del_t_" + TS;

    private static File BYTE_TIF;
    private WmsStoreManager wmsStores;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing (src/test/resources/byte.tif)");
        BYTE_TIF = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        wmsStores = client.wmsStores();

        // Upload byte.tif twice to create two coverage layers for WmsLayer publishing
        client.coverageStores().uploadFile(WS, FEED_CS,  "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, FEED_CS2, "file", "geotiff", BYTE_TIF, "first", null, null);

        wmsStores.create(WS, CreateWmsStoreRequest.of(STORE_UPD,   CAPS_URL));
        wmsStores.create(WS, CreateWmsStoreRequest.of(STORE_DEL,   CAPS_URL));
        wmsStores.create(WS, CreateWmsStoreRequest.of(STORE_DEL_L, CAPS_URL));
        wmsStores.create(WS, CreateWmsStoreRequest.of(STORE_DEL_T, CAPS_URL));

        publishWmsLayer(WS, STORE_DEL_L, WS + ":" + FEED_CS);
        publishWmsLayer(WS, STORE_DEL_T, WS + ":" + FEED_CS2);
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    private void publishWmsLayer(String ws, String store, String layerName) {
        client.wmsLayers().publish(ws, store, PublishWmsLayerRequest.of(layerName, layerName));
    }

    // ── 1. list ──────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() returns pre-created stores")
    void list_returnsPreCreatedStores() {
        List<WmsStoreSummary> list = wmsStores.list(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        list.forEach(s -> {
            assertNotNull(s.getName());
            assertNotNull(s.getHref());
        });
    }

    // ── 2-3. create ──────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] create() full fields")
    void create_fullFields() {
        WmsStore ws = wmsStores.create(WS,
                CreateWmsStoreRequest.of(STORE_MAIN, CAPS_URL)
                        .description("integration test WMS store")
                        .enabled(true)
                        .maxConnections(8)
                        .readTimeout(45)
                        .connectTimeout(20)
                        .disableOnConnFailure(false));

        assertNotNull(ws);
        assertEquals(STORE_MAIN, ws.getName());
        assertTrue(ws.isEnabled());
        assertEquals(CAPS_URL, ws.getCapabilitiesURL());
        assertEquals("integration test WMS store", ws.getDescription());
        assertNotNull(ws.getDateCreated());
        assertNotNull(ws.getWmslayers());
        assertNotNull(ws.getWorkspace());
        assertEquals(WS, ws.getWorkspace().getName());
    }

    @Test
    @Order(3)
    @DisplayName("[3] create() duplicate -> ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> wmsStores.create(WS, CreateWmsStoreRequest.of(STORE_MAIN, CAPS_URL)));
    }

    // ── 4-5. get ─────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] get() returns full details")
    void get_fullDetails() {
        WmsStore ws = wmsStores.get(WS, STORE_MAIN);

        assertNotNull(ws);
        assertEquals(STORE_MAIN, ws.getName());
        // type: GeoServer 2.28.2 does not include 'type' in WmsStore GET response
        assertTrue(ws.isEnabled());
        assertEquals(CAPS_URL, ws.getCapabilitiesURL());
        assertEquals(WS, ws.getWorkspace().getName());
        assertNotNull(ws.getWmslayers());
        assertNotNull(ws.getMaxConnections());
        assertNotNull(ws.getReadTimeout());
        assertNotNull(ws.getConnectTimeout());
        assertNotNull(ws.getDisableOnConnFailure());
        assertNotNull(ws.getDateCreated());
    }

    @Test
    @Order(5)
    @DisplayName("[5] get() nonexistent -> WmsStoreNotFoundException")
    void get_nonExistent_throwsException() {
        WmsStoreNotFoundException ex = assertThrows(WmsStoreNotFoundException.class,
                () -> wmsStores.get(WS, "nonexistent_wms_xyz_12345"));
        assertNotNull(ex.getStoreName());
    }

    // ── 6-7. exists ──────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] exists() existing -> true")
    void exists_true() {
        assertTrue(wmsStores.exists(WS, STORE_MAIN));
    }

    @Test
    @Order(7)
    @DisplayName("[7] exists() nonexistent -> false")
    void exists_false() {
        assertFalse(wmsStores.exists(WS, "nonexistent_wms_xyz_12345"));
    }

    // ── 8-10. update ─────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] update() description + enabled")
    void update_descriptionAndEnabled() {
        WmsStore updated = wmsStores.update(WS, STORE_UPD,
                UpdateWmsStoreRequest.builder()
                        .description("updated description")
                        .enabled(false)
                        .build());

        assertEquals(STORE_UPD, updated.getName());
        assertEquals("updated description", updated.getDescription());
        assertFalse(updated.isEnabled());
        assertNotNull(updated.getDateModified());
    }

    @Test
    @Order(9)
    @DisplayName("[9] update() capabilitiesURL + maxConnections")
    void update_urlAndConnections() {
        WmsStore updated = wmsStores.update(WS, STORE_MAIN,
                UpdateWmsStoreRequest.builder()
                        .capabilitiesURL(CAPS_URL_ALT)
                        .maxConnections(12)
                        .readTimeout(90)
                        .build());

        assertEquals(STORE_MAIN, updated.getName());
        assertEquals(CAPS_URL_ALT, updated.getCapabilitiesURL());
        assertEquals(Integer.valueOf(12), updated.getMaxConnections());
        assertEquals(Integer.valueOf(90), updated.getReadTimeout());
    }

    @Test
    @Order(10)
    @DisplayName("[10] update() nonexistent -> WmsStoreNotFoundException")
    void update_nonExistent_throwsException() {
        assertThrows(WmsStoreNotFoundException.class,
                () -> wmsStores.update(WS, "nonexistent_wms_xyz_12345",
                        UpdateWmsStoreRequest.builder().description("x").build()));
    }

    // ── 11-14. delete ─────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] delete(recurse=false) empty store -> 200")
    void delete_noRecurse_emptyStore() {
        assertDoesNotThrow(() -> wmsStores.delete(WS, STORE_DEL, false));
        assertFalse(wmsStores.exists(WS, STORE_DEL));
    }

    @Test
    @Order(12)
    @DisplayName("[12] delete(recurse=false) with WmsLayer -> AuthenticationException(401)")
    void delete_noRecurse_withLayer_throws401() {
        assertThrows(AuthenticationException.class,
                () -> wmsStores.delete(WS, STORE_DEL_L, false));
        assertTrue(wmsStores.exists(WS, STORE_DEL_L));
    }

    @Test
    @Order(13)
    @DisplayName("[13] delete(recurse=true) with WmsLayer -> 200 cascade")
    void delete_recurse_withLayer() {
        assertDoesNotThrow(() -> wmsStores.delete(WS, STORE_DEL_T, true));
        assertFalse(wmsStores.exists(WS, STORE_DEL_T));
    }

    @Test
    @Order(14)
    @DisplayName("[14] delete() nonexistent -> WmsStoreNotFoundException")
    void delete_nonExistent_throwsException() {
        assertThrows(WmsStoreNotFoundException.class,
                () -> wmsStores.delete(WS, "nonexistent_wms_xyz_12345"));
    }

    // ── 15. list after changes ────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("[15] list() after deletions reflects changes")
    void list_afterChanges() {
        List<WmsStoreSummary> list = wmsStores.list(WS);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(STORE_MAIN)));
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(STORE_DEL)));
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(STORE_DEL_T)));
    }
}
