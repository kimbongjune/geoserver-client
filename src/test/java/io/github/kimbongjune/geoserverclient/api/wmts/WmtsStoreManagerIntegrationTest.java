package io.github.kimbongjune.geoserverclient.api.wmts;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.UpdateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WmtsStoreNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] WmtsStoreManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WmtsStoreManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS           = System.currentTimeMillis();
    private static final String WS           = "wmts_ws_"    + TS;
    // GeoServer GWC WMTS capabilities (self-cascading via internal port)
    private static final String CAPS_URL     =
            "http://localhost:8080/geoserver/gwc/service/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities";
    private static final String CAPS_URL_ALT =
            "http://localhost:8080/geoserver/gwc/service/wmts?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities&accept=application/json";
    private static final String FEED_CS      = "wmts_feed_"  + TS;
    private static final String FEED_CS2     = "wmts_feed2_" + TS;

    private static final String STORE_MAIN   = "wmts_main_"  + TS;
    private static final String STORE_UPD    = "wmts_upd_"   + TS;
    private static final String STORE_DEL    = "wmts_del_"   + TS;
    private static final String STORE_DEL_L  = "wmts_del_l_" + TS;
    private static final String STORE_DEL_T  = "wmts_del_t_" + TS;

    private static File BYTE_TIF;
    private WmtsStoreManager wmtsStores;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing (src/test/resources/byte.tif)");
        BYTE_TIF = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
        wmtsStores = client.wmtsStores();

        // Upload byte.tif to create two coverage layers published to GWC (for WMTS layer cascading)
        client.coverageStores().uploadFile(WS, FEED_CS,  "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, FEED_CS2, "file", "geotiff", BYTE_TIF, "first", null, null);

        wmtsStores.create(WS, CreateWmtsStoreRequest.builder(STORE_UPD,   CAPS_URL));
        wmtsStores.create(WS, CreateWmtsStoreRequest.builder(STORE_DEL,   CAPS_URL));
        wmtsStores.create(WS, CreateWmtsStoreRequest.builder(STORE_DEL_L, CAPS_URL));
        wmtsStores.create(WS, CreateWmtsStoreRequest.builder(STORE_DEL_T, CAPS_URL));

        publishWmtsLayer(WS, STORE_DEL_L, WS + ":" + FEED_CS);
        publishWmtsLayer(WS, STORE_DEL_T, WS + ":" + FEED_CS2);
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    private void publishWmtsLayer(String ws, String store, String layerName) {
        String path = "/rest/workspaces/" + ws + "/wmtsstores/" + store + "/layers";
        String body = "{\"wmtsLayer\":{\"name\":\"" + layerName + "\"}}";
        GeoServerResponse response = client.getHttpClient()
                .post(path, body, "application/json", "application/json");
        assertEquals(201, response.getStatusCode(),
                "WmtsLayer publish failed (" + store + "/" + layerName + "): "
                        + response.getStatusCode() + " " + response.getBody());
    }

    // ── 1. list ──────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() returns pre-created stores")
    void list_returnsPreCreatedStores() {
        List<WmtsStoreSummary> list = wmtsStores.list(WS);
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
        WmtsStore ws = wmtsStores.create(WS,
                CreateWmtsStoreRequest.builder(STORE_MAIN, CAPS_URL)
                        .description("integration test WMTS store")
                        .enabled(true)
                        .maxConnections(8)
                        .readTimeout(45)
                        .connectTimeout(20)
                        .disableOnConnFailure(false));

        assertNotNull(ws);
        assertEquals(STORE_MAIN, ws.getName());
        assertTrue(ws.isEnabled());
        assertEquals(CAPS_URL, ws.getCapabilitiesURL());
        assertEquals("integration test WMTS store", ws.getDescription());
        assertNotNull(ws.getDateCreated());
        assertNotNull(ws.getLayers());
        assertNotNull(ws.getWorkspace());
        assertEquals(WS, ws.getWorkspace().getName());
    }

    @Test
    @Order(3)
    @DisplayName("[3] create() duplicate -> ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> wmtsStores.create(WS, CreateWmtsStoreRequest.builder(STORE_MAIN, CAPS_URL)));
    }

    // ── 4-5. get ─────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] get() returns full details")
    void get_fullDetails() {
        WmtsStore ws = wmtsStores.get(WS, STORE_MAIN);

        assertNotNull(ws);
        assertEquals(STORE_MAIN, ws.getName());
        assertTrue(ws.isEnabled());
        assertEquals(CAPS_URL, ws.getCapabilitiesURL());
        assertEquals(WS, ws.getWorkspace().getName());
        assertNotNull(ws.getLayers());
        assertNotNull(ws.getMaxConnections());
        assertNotNull(ws.getReadTimeout());
        assertNotNull(ws.getConnectTimeout());
        assertNotNull(ws.getDisableOnConnFailure());
        assertNotNull(ws.getDateCreated());
    }

    @Test
    @Order(5)
    @DisplayName("[5] get() nonexistent -> WmtsStoreNotFoundException")
    void get_nonExistent_throwsException() {
        WmtsStoreNotFoundException ex = assertThrows(WmtsStoreNotFoundException.class,
                () -> wmtsStores.get(WS, "nonexistent_wmts_xyz_12345"));
        assertNotNull(ex.getStoreName());
    }

    // ── 6-7. exists ──────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] exists() existing -> true")
    void exists_true() {
        assertTrue(wmtsStores.exists(WS, STORE_MAIN));
    }

    @Test
    @Order(7)
    @DisplayName("[7] exists() nonexistent -> false")
    void exists_false() {
        assertFalse(wmtsStores.exists(WS, "nonexistent_wmts_xyz_12345"));
    }

    // ── 8-10. update ─────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] update() description + enabled")
    void update_descriptionAndEnabled() {
        WmtsStore updated = wmtsStores.update(WS, STORE_UPD,
                UpdateWmtsStoreRequest.builder()
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
        WmtsStore updated = wmtsStores.update(WS, STORE_MAIN,
                UpdateWmtsStoreRequest.builder()
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
    @DisplayName("[10] update() nonexistent -> WmtsStoreNotFoundException")
    void update_nonExistent_throwsException() {
        assertThrows(WmtsStoreNotFoundException.class,
                () -> wmtsStores.update(WS, "nonexistent_wmts_xyz_12345",
                        UpdateWmtsStoreRequest.builder().description("x").build()));
    }

    // ── 11-14. delete ─────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] delete(recurse=false) empty store -> 200")
    void delete_noRecurse_emptyStore() {
        assertDoesNotThrow(() -> wmtsStores.delete(WS, STORE_DEL, false));
        assertFalse(wmtsStores.exists(WS, STORE_DEL));
    }

    @Test
    @Order(12)
    @DisplayName("[12] delete(recurse=false) with WmtsLayer -> AuthenticationException(401)")
    void delete_noRecurse_withLayer_throws401() {
        assertThrows(AuthenticationException.class,
                () -> wmtsStores.delete(WS, STORE_DEL_L, false));
        assertTrue(wmtsStores.exists(WS, STORE_DEL_L));
    }

    @Test
    @Order(13)
    @DisplayName("[13] delete(recurse=true) with WmtsLayer -> 200 cascade")
    void delete_recurse_withLayer() {
        assertDoesNotThrow(() -> wmtsStores.delete(WS, STORE_DEL_T, true));
        assertFalse(wmtsStores.exists(WS, STORE_DEL_T));
    }

    @Test
    @Order(14)
    @DisplayName("[14] delete() nonexistent -> WmtsStoreNotFoundException")
    void delete_nonExistent_throwsException() {
        assertThrows(WmtsStoreNotFoundException.class,
                () -> wmtsStores.delete(WS, "nonexistent_wmts_xyz_12345"));
    }

    // ── 15. list after changes ────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("[15] list() after deletions reflects changes")
    void list_afterChanges() {
        List<WmtsStoreSummary> list = wmtsStores.list(WS);
        assertNotNull(list);
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(STORE_MAIN)));
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(STORE_DEL)));
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(STORE_DEL_T)));
    }
}
