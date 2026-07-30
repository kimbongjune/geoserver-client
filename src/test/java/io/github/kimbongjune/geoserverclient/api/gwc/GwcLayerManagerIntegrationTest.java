package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GwcLayerNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcLayerManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcLayerManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS    = System.currentTimeMillis();
    private static final String WS    = "gwc_layer_ws_" + TS;
    private static final String STORE = "gwc_layer_cs_" + TS;

    /** Self-published GeoTIFF layer — avoids depending on GeoServer sample data being present. */
    private static final String TEST_LAYER = WS + ":" + STORE;

    private GwcLayerManager gwcLayers;

    @BeforeAll
    void setUp() throws Exception {
        gwcLayers = client.gwcLayers();

        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        File byteTif = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        // configure=first uploads the GeoTIFF and auto-publishes a Coverage + Layer named WS:STORE
        client.coverageStores().uploadFile(WS, STORE, "file", "geotiff", byteTif, "first", null, null);

        GwcLayer layer = new GwcLayer(TEST_LAYER);
        layer.setMimeFormats(Arrays.asList("image/png"));
        layer.gridSetNames("EPSG:4326");
        layer.setMetaWidthHeight(Arrays.asList(4, 4));
        layer.setGutter(0);
        layer.setEnabled(true);
        gwcLayers.upsert(TEST_LAYER, layer);
    }

    @AfterAll
    void cleanUp() {
        try { gwcLayers.delete(TEST_LAYER); } catch (Exception ignored) {}
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/layers ─────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() contains the registered test layer")
    void list_containsTestLayer() {
        assertTrue(gwcLayers.list().contains(TEST_LAYER));
    }

    // ── [2] GET /gwc/rest/layers/{layer} ─────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] get() returns a GwcLayer with matching name and mimeFormats")
    void get_returnsMatchingLayer() {
        GwcLayer layer = gwcLayers.get(TEST_LAYER);
        assertEquals(TEST_LAYER, layer.getName());
        assertTrue(layer.getMimeFormats().contains("image/png"));
        assertNotNull(layer.getGridSubsets());
        assertFalse(layer.getGridSubsets().isEmpty());
    }

    @Test @Order(3)
    @DisplayName("[3] get() populates server-managed read-only fields (id)")
    void get_populatesReadOnlyId() {
        GwcLayer layer = gwcLayers.get(TEST_LAYER);
        assertNotNull(layer.getId(), "server-assigned id must be populated on GET");
    }

    // ── [4] PUT /gwc/rest/layers/{layerName} ─────────────────────────────

    @Test @Order(4)
    @DisplayName("[4] upsert() updates gutter and is reflected on next get()")
    void upsert_updatesGutter() {
        GwcLayer layer = gwcLayers.get(TEST_LAYER);
        layer.setGutter(10);
        assertDoesNotThrow(() -> gwcLayers.upsert(TEST_LAYER, layer));

        GwcLayer updated = gwcLayers.get(TEST_LAYER);
        assertEquals(10, updated.getGutter());
    }

    // ── [5] DELETE /gwc/rest/layers/{layer} ──────────────────────────────

    @Test @Order(5)
    @DisplayName("[5] delete() removes the layer and subsequent get() throws GwcLayerNotFoundException")
    void delete_thenGet_throwsNotFound() {
        gwcLayers.delete(TEST_LAYER);
        assertThrows(GwcLayerNotFoundException.class, () -> gwcLayers.get(TEST_LAYER));

        // re-register so @AfterAll cleanup and any later runs stay consistent
        GwcLayer layer = new GwcLayer(TEST_LAYER);
        layer.setMimeFormats(Arrays.asList("image/png"));
        layer.gridSetNames("EPSG:4326");
        layer.setMetaWidthHeight(Arrays.asList(4, 4));
        layer.setGutter(0);
        gwcLayers.upsert(TEST_LAYER, layer);
    }
}
