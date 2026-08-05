package io.github.kimbongjune.geoserverclient.api.wms;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.UpdateWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayer;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayerSummary;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WmsLayerNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] WmsLayerManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WmsLayerManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS       = System.currentTimeMillis();
    private static final String WS       = "wml_ws_" + TS;
    private static final String FEED_CS  = "wml_feed_" + TS;
    // self-cascading: GeoServer container internal address
    private static final String CAPS_URL =
            "http://localhost:8080/geoserver/ows?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetCapabilities";

    // WMS store names
    private static final String STORE       = "wml_store_" + TS;
    private static final String STORE_AVAIL = "wml_st_av_" + TS; // for listAvailable (no layers)
    private static final String STORE_WS    = "wml_st_ws_" + TS; // for publishByWorkspace() test

    // WMS layer names  (all reference same nativeName = WS:FEED_CS)
    private static final String LAYER_DEL_NR = "wml_del_nr_" + TS;  // pre-published, delete no-recurse
    private static final String LAYER_DEL_T  = "wml_del_t_"  + TS;  // pre-published, delete recurse=true
    private static final String LAYER_MAIN   = "wml_main_"   + TS;  // published in test [4]
    private static final String LAYER_VP     = "wml_vp_"     + TS;  // published in test [16] with vendorParameters
    private static final String LAYER_WS     = "wml_ws_pub_" + TS;  // published in test [17] via publishByWorkspace()

    /** native WMS layer name: WS + ":" + FEED_CS */
    private static String NATIVE_LAYER;

    private static File BYTE_TIF;
    private WmsLayerManager wmsLayers;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing from src/test/resources");
        BYTE_TIF = new File(res.toURI());
        NATIVE_LAYER = WS + ":" + FEED_CS;

        // 1. workspace
        client.workspaces().create(CreateWorkspaceRequest.builder(WS));

        // 2. upload byte.tif -> creates WS:FEED_CS coverage + WMS layer
        client.coverageStores().uploadFile(WS, FEED_CS, "file", "geotiff", BYTE_TIF, "first", null, null);

        // 3. create WMS stores
        client.wmsStores().create(WS, CreateWmsStoreRequest.builder(STORE,       CAPS_URL));
        client.wmsStores().create(WS, CreateWmsStoreRequest.builder(STORE_AVAIL, CAPS_URL));
        client.wmsStores().create(WS, CreateWmsStoreRequest.builder(STORE_WS,    CAPS_URL));

        // 4. pre-publish layers into STORE
        wmsLayers = client.wmsLayers();
        wmsLayers.publish(WS, STORE, PublishWmsLayerRequest.builder(LAYER_DEL_NR, NATIVE_LAYER));
        wmsLayers.publish(WS, STORE, PublishWmsLayerRequest.builder(LAYER_DEL_T,  NATIVE_LAYER));
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── 1. list(ws, store) ──────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list(ws, store) returns pre-published layers")
    void list_byStore_returnsPrePublished() {
        List<WmsLayerSummary> list = wmsLayers.list(WS, STORE);
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
        List<WmsLayerSummary> list = wmsLayers.list(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_NR)));
    }

    // ── 3. listAvailable ────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] listAvailable(ws, store) returns available native layer names")
    void listAvailable_returnsNativeLayerNames() {
        // STORE_AVAIL has no layers published -> NATIVE_LAYER should be in available list
        List<String> available = wmsLayers.listAvailable(WS, STORE_AVAIL);
        assertNotNull(available);
        // FEED_CS layer was published to WMS via configure=first during upload
        assertTrue(available.contains(NATIVE_LAYER),
                "Expected '" + NATIVE_LAYER + "' in available list but got: " + available);
    }

    // ── 4-5. publish ────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] publish() full fields -> 201, returns WmsLayer")
    void publish_fullFields() {
        WmsLayer layer = wmsLayers.publish(WS, STORE,
                PublishWmsLayerRequest.builder(LAYER_MAIN, NATIVE_LAYER)
                        .title("Integration Test WMS Layer")
                        .description("wms layer test")
                        .enabled(true)
                        .preferredFormat("image/png")
                        .forcedRemoteStyle("")
                        .metadataBBoxRespected(false)
                        .projectionPolicy(io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy.REPROJECT_TO_DECLARED));

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
        assertEquals(io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy.REPROJECT_TO_DECLARED, layer.getProjectionPolicy());
    }

    @Test
    @Order(19)
    @DisplayName("[19] publish() projectionPolicy - enum round-trips exactly against raw REST API")
    void publish_projectionPolicy_matchesRawRestApi() throws Exception {
        String raw = rawGet("/rest/workspaces/" + WS + "/wmsstores/" + STORE + "/wmslayers/" + LAYER_MAIN + ".json");
        com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(raw);
        assertEquals("REPROJECT_TO_DECLARED", node.path("wmsLayer").path("projectionPolicy").asText(),
                "raw REST API projectionPolicy value must match exactly what the library parsed");
    }

    @Test
    @Order(5)
    @DisplayName("[5] publish() duplicate name -> ResourceAlreadyExistsException")
    void publish_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> wmsLayers.publish(WS, STORE,
                        PublishWmsLayerRequest.builder(LAYER_MAIN, NATIVE_LAYER)));
    }

    // ── 6-8. get ────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] get(ws, store, layer) returns full details")
    void get_byStore_fullDetails() {
        WmsLayer layer = wmsLayers.get(WS, STORE, LAYER_MAIN);

        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
        assertTrue(layer.isEnabled());
        assertNotNull(layer.getSrs());
        assertNotNull(layer.getNativeBoundingBox());
        assertNotNull(layer.getLatLonBoundingBox());
        assertNotNull(layer.getProjectionPolicy());
        assertNotNull(layer.getStore());
        assertEquals("wmsStore", layer.getStore().getStoreClass());
        assertNotNull(layer.getPreferredFormat());
        assertFalse(layer.isEnabled() && layer.getSrs() == null);
    }

    @Test
    @Order(7)
    @DisplayName("[7] get(ws, layer) workspace-level returns same layer")
    void get_byWorkspace_returnsLayer() {
        WmsLayer layer = wmsLayers.get(WS, LAYER_MAIN);

        assertNotNull(layer);
        assertEquals(LAYER_MAIN, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
    }

    @Test
    @Order(8)
    @DisplayName("[8] get() nonexistent -> WmsLayerNotFoundException")
    void get_nonExistent_throwsException() {
        WmsLayerNotFoundException ex = assertThrows(WmsLayerNotFoundException.class,
                () -> wmsLayers.get(WS, STORE, "nonexistent_wml_xyz_12345"));
        assertNotNull(ex.getLayerName());
    }

    // ── 9-10. exists ────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] exists() existing layer -> true")
    void exists_true() {
        assertTrue(wmsLayers.exists(WS, STORE, LAYER_MAIN));
    }

    @Test
    @Order(10)
    @DisplayName("[10] exists() nonexistent -> false")
    void exists_false() {
        assertFalse(wmsLayers.exists(WS, STORE, "nonexistent_wml_xyz_12345"));
    }

    // ── 11. update (GeoServer 2.28.2 BUG: always 500) ───────────────────

    @Test
    @Order(11)
    @DisplayName("[11] update() -> GeoServerResponseException(500) [GeoServer 2.28.2 BUG]")
    void update_alwaysThrows500() {
        // GeoServer 2.28.2: PUT /wmslayers/{layer} is broken (UnsupportedOperationException in
        // XStreamPersister when processing getRemoteStyleInfos())
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> wmsLayers.update(WS, STORE, LAYER_MAIN,
                        UpdateWmsLayerRequest.builder()
                                .title("Updated Title")
                                .build()));
        assertEquals(500, ex.getStatusCode(),
                "Expected 500 (GeoServer 2.28.2 WmsLayer PUT bug)");
    }

    // ── 12-14. delete ────────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] delete(recurse=false) with LayerInfo -> AuthenticationException(403)")
    void delete_noRecurse_withLayerInfo_throws403() {
        // WmsLayer published via POST always has a LayerInfo -> recurse=false -> 403
        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> wmsLayers.delete(WS, STORE, LAYER_DEL_NR, false));
        assertEquals(403, ex.getStatusCode());
        // layer must still exist
        assertTrue(wmsLayers.exists(WS, STORE, LAYER_DEL_NR));
    }

    @Test
    @Order(13)
    @DisplayName("[13] delete(recurse=true) with LayerInfo -> 200 cascade")
    void delete_recurse_withLayerInfo() {
        assertDoesNotThrow(() -> wmsLayers.delete(WS, STORE, LAYER_DEL_T, true));
        assertFalse(wmsLayers.exists(WS, STORE, LAYER_DEL_T));
    }

    @Test
    @Order(14)
    @DisplayName("[14] delete() nonexistent -> WmsLayerNotFoundException")
    void delete_nonExistent_throwsException() {
        assertThrows(WmsLayerNotFoundException.class,
                () -> wmsLayers.delete(WS, STORE, "nonexistent_wml_xyz_12345"));
    }

    // ── 15. list after changes ───────────────────────────────────────────

    // ── 16. vendorParameters ─────────────────────────────────────────────

    @Test
    @Order(16)
    @DisplayName("[16] publish() with vendorParameters(StringMap) -> GET returns vendorParameters")
    void publish_withVendorParameters() {
        WmsLayer layer = wmsLayers.publish(WS, STORE,
                PublishWmsLayerRequest.builder(LAYER_VP, NATIVE_LAYER)
                        .vendorParameters(StringMap.of("FORMAT", "image/png")
                                .put("TRANSPARENT", "TRUE")));

        assertNotNull(layer);
        assertEquals(LAYER_VP, layer.getName());

        WmsLayer fetched = wmsLayers.get(WS, STORE, LAYER_VP);
        assertNotNull(fetched.getVendorParameters(),
                "vendorParameters should be present in GET response after publish");
        assertNotNull(fetched.getVendorParameters().getEntry(),
                "vendorParameters.entry should be non-null");
    }

    // ── 17. publishByWorkspace ────────────────────────────────────────────

    @Test
    @Order(17)
    @DisplayName("[17] publishByWorkspace() — POST /workspaces/{ws}/wmslayers with store named in body")
    void publishByWorkspace_publishesLayer() {
        WmsLayer layer = wmsLayers.publishByWorkspace(WS, STORE_WS,
                PublishWmsLayerRequest.builder(LAYER_WS, NATIVE_LAYER)
                        .title("Integration Test WMS Layer (byWorkspace)")
                        .enabled(true));

        assertNotNull(layer);
        assertEquals(LAYER_WS, layer.getName());
        assertEquals(NATIVE_LAYER, layer.getNativeName());
        assertTrue(layer.isEnabled());
        assertNotNull(layer.getStore());
        assertTrue(layer.getStore().getName().contains(STORE_WS));
        assertTrue(wmsLayers.exists(WS, STORE_WS, LAYER_WS));
    }

    @Test
    @Order(18)
    @DisplayName("[18] publishByWorkspace() duplicate name -> ResourceAlreadyExistsException")
    void publishByWorkspace_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> wmsLayers.publishByWorkspace(WS, STORE_WS,
                        PublishWmsLayerRequest.builder(LAYER_WS, NATIVE_LAYER)));
    }

    @Test
    @Order(15)
    @DisplayName("[15] list() after deletions reflects changes")
    void list_afterChanges() {
        List<WmsLayerSummary> list = wmsLayers.list(WS, STORE);
        assertNotNull(list);
        // LAYER_MAIN and LAYER_DEL_NR (not recursively deleted) still present
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_MAIN)));
        assertTrue(list.stream().anyMatch(s -> s.getName().equals(LAYER_DEL_NR)));
        // LAYER_DEL_T was recursively deleted
        assertTrue(list.stream().noneMatch(s -> s.getName().equals(LAYER_DEL_T)));
    }
}
