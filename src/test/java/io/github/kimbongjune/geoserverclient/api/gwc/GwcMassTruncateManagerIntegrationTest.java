package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.*;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcMassTruncateManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcMassTruncateManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS    = System.currentTimeMillis();
    private static final String WS    = "gwc_mt_ws_" + TS;
    private static final String STORE = "gwc_mt_cs_" + TS;

    private static final String TEST_LAYER = WS + ":" + STORE;

    private GwcMassTruncateManager massTruncate;
    private GwcLayerManager layers;

    @BeforeAll
    void setUp() throws Exception {
        massTruncate = client.gwcMassTruncate();
        layers = client.gwcLayers();

        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        File byteTif = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
        client.coverageStores().uploadFile(WS, STORE, "file", "geotiff", byteTif, "first", null, null);

        GwcLayer layer = new GwcLayer(TEST_LAYER);
        layer.setMimeFormats(Arrays.asList("image/png"));
        layer.gridSetNames("EPSG:4326");
        layer.setMetaWidthHeight(Arrays.asList(4, 4));
        layers.upsert(TEST_LAYER, layer);
    }

    @AfterAll
    void cleanUp() {
        try { layers.delete(TEST_LAYER); } catch (Exception ignored) {}
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/masstruncate ────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] listRequestTypes() returns all 4 documented request types")
    void listRequestTypes_returnsAllTypes() {
        GwcTruncateRequestTypes types = massTruncate.listRequestTypes();
        assertNotNull(types.getRequestTypes());
        assertTrue(types.getRequestTypes().contains("truncateLayer"));
        assertTrue(types.getRequestTypes().contains("truncateParameters"));
        assertTrue(types.getRequestTypes().contains("truncateOrphans"));
        assertTrue(types.getRequestTypes().contains("truncateExtent"));
    }

    // ── [2] POST /gwc/rest/masstruncate — truncateLayer ──────────────────

    @Test @Order(2)
    @DisplayName("[2] truncate(GwcTruncateLayerRequest) succeeds synchronously")
    void truncate_truncateLayer_succeeds() {
        assertDoesNotThrow(() -> massTruncate.truncate(new GwcTruncateLayerRequest(TEST_LAYER)));
    }

    // ── [2] POST /gwc/rest/masstruncate — truncateParameters ─────────────

    @Test @Order(3)
    @DisplayName("[3] truncate(GwcTruncateParametersRequest) -> GeoServerResponseException(500) [GeoServer/GWC 2.28.2 BUG]")
    void truncate_truncateParameters_alwaysThrows500() {
        // Re-verified against live server (2026-07-30): sending truncateParameters to a GWC layer
        // with a STYLES parameter filter always returns 500 (empty error body) from GeoWebCache.
        // GwcLayerManager/GwcTruncateLayerRequest/GwcTruncateExtentRequest paths all work correctly,
        // so this is a server-side bug specific to truncateParameters, not a serialization issue.
        io.github.kimbongjune.geoserverclient.dto.common.StringMap params =
                io.github.kimbongjune.geoserverclient.dto.common.StringMap.of("STYLES", "");
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> massTruncate.truncate(new GwcTruncateParametersRequest(TEST_LAYER, params)));
        assertEquals(500, ex.getStatusCode(),
                "Expected 500 (GeoWebCache truncateParameters server-side bug)");
    }

    // ── [2] POST /gwc/rest/masstruncate — truncateExtent ─────────────────

    @Test @Order(4)
    @DisplayName("[4] truncate(GwcTruncateExtentRequest) with EPSG:4326 succeeds")
    void truncate_truncateExtent_succeeds() {
        GwcTruncateExtentRequest request = new GwcTruncateExtentRequest(
                TEST_LAYER, "EPSG:4326", "image/png", -180.0, -90.0, 180.0, 90.0);
        assertDoesNotThrow(() -> massTruncate.truncate(request));
    }
}
