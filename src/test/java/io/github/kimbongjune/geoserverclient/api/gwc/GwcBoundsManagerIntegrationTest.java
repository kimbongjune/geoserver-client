package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTileBounds;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcBoundsManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcBoundsManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS    = System.currentTimeMillis();
    private static final String WS    = "gwc_bounds_ws_" + TS;
    private static final String STORE = "gwc_bounds_cs_" + TS;

    private static final String LAYER_NAME    = WS + ":" + STORE;
    private static final String LAYER_ENCODED = WS + "%3A" + STORE;

    private GwcBoundsManager bounds;
    private GwcLayerManager layers;

    @BeforeAll
    void setUp() throws Exception {
        bounds = client.gwcBounds();
        layers = client.gwcLayers();

        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        File byteTif = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
        client.coverageStores().uploadFile(WS, STORE, "file", "geotiff", byteTif, "first", null, null);

        GwcLayer layer = new GwcLayer(LAYER_NAME);
        layer.setMimeFormats(Arrays.asList("image/png"));
        layer.gridSetNames("EPSG:4326", "EPSG:900913");
        layer.setMetaWidthHeight(Arrays.asList(4, 4));
        layers.upsert(LAYER_NAME, layer);
    }

    @AfterAll
    void cleanUp() {
        try { layers.delete(LAYER_NAME); } catch (Exception ignored) {}
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/bounds/{layer}/{srs}/java ───────────────────────

    @Test @Order(1)
    @DisplayName("[1] getBounds(EPSG:4326) returns non-empty tile coverages")
    void getBounds_epsg4326_returnsCoverages() {
        GwcTileBounds result = bounds.getBounds(LAYER_ENCODED, "EPSG:4326");
        assertNotNull(result);
        assertFalse(result.getCoverages().isEmpty());
        assertEquals(5, result.getCoverages().get(0).length, "each coverage entry must have 5 elements");
    }

    @Test @Order(2)
    @DisplayName("[2] getBounds(EPSG:900913) also returns coverages")
    void getBounds_epsg900913_returnsCoverages() {
        GwcTileBounds result = bounds.getBounds(LAYER_ENCODED, "EPSG:900913");
        assertNotNull(result);
        assertFalse(result.getCoverages().isEmpty());
    }

    @Test @Order(3)
    @DisplayName("[3] getBounds(nonexistent, EPSG:4326) throws GeoServerException (404)")
    void getBounds_nonexistentLayer_throwsException() {
        assertThrows(GeoServerException.class,
                () -> bounds.getBounds("nonexistent%3Alayer", "EPSG:4326"),
                "Non-existent layer must throw GeoServerException (404)");
    }
}
