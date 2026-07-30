package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.*;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcSeedManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcSeedManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS    = System.currentTimeMillis();
    private static final String WS    = "gwc_seed_ws_" + TS;
    private static final String STORE = "gwc_seed_cs_" + TS;

    private static final String LAYER_NAME    = WS + ":" + STORE;
    private static final String LAYER_ENCODED = WS + "%3A" + STORE;

    private GwcSeedManager seed;
    private GwcLayerManager layers;

    @BeforeAll
    void setUp() throws Exception {
        seed = client.gwcSeed();
        layers = client.gwcLayers();

        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif not found in src/test/resources/");
        File byteTif = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        client.coverageStores().uploadFile(WS, STORE, "file", "geotiff", byteTif, "first", null, null);

        GwcLayer layer = new GwcLayer(LAYER_NAME);
        layer.setMimeFormats(Arrays.asList("image/png"));
        layer.gridSetNames("EPSG:4326");
        layer.setMetaWidthHeight(Arrays.asList(4, 4));
        layers.upsert(LAYER_NAME, layer);

        try { seed.killAll(GwcKillType.ALL); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { seed.killAll(GwcKillType.ALL); } catch (Exception ignored) {}
        try { layers.delete(LAYER_NAME); } catch (Exception ignored) {}
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/seed.json ──────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] getAllRunningTasks() returns a status DTO")
    void getAllRunningTasks_returnsStatus() {
        GwcSeedStatus status = seed.getAllRunningTasks();
        assertNotNull(status);
        assertNotNull(status.getTasks());
    }

    // ── [2] GET /gwc/rest/seed/{layer}.json ──────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] getRunningTasks() returns a status DTO")
    void getRunningTasks_returnsStatus() {
        GwcSeedStatus status = seed.getRunningTasks(LAYER_ENCODED);
        assertNotNull(status);
        assertNotNull(status.getTasks());
    }

    // ── [4] POST /gwc/rest/seed/{layer}.xml ──────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] seed(TRUNCATE) starts a task (async, sent as XML)")
    void seed_truncate_succeeds() {
        GwcSeedRequest request = new GwcSeedRequest(LAYER_NAME, 4326, 0, 3, "image/png", GwcSeedType.TRUNCATE)
                .threadCount(1);
        assertDoesNotThrow(() -> seed.seed(LAYER_ENCODED, request));
    }

    // ── [6][7] POST /gwc/rest/seed[/{layer}] — kill_all ──────────────────

    @Test @Order(4)
    @DisplayName("[4] killAll(ALL) terminates all running/pending tasks")
    void killAll_all_succeeds() {
        String result = assertDoesNotThrow(() -> seed.killAll(GwcKillType.ALL));
        assertNotNull(result, "killAll response must not be null");
    }

    @Test @Order(5)
    @DisplayName("[5] killAll(RUNNING) succeeds")
    void killAll_running_succeeds() {
        assertDoesNotThrow(() -> seed.killAll(GwcKillType.RUNNING));
    }

    @Test @Order(6)
    @DisplayName("[6] killAll(PENDING) succeeds")
    void killAll_pending_succeeds() {
        assertDoesNotThrow(() -> seed.killAll(GwcKillType.PENDING));
    }

    @Test @Order(7)
    @DisplayName("[7] getAllRunningTasks() is empty after kill_all")
    void getAllRunningTasks_afterKillAll_isEmpty() {
        GwcSeedStatus status = seed.getAllRunningTasks();
        assertTrue(status.isEmpty(), "running tasks must be empty after kill_all but was: " + status.getTasks().size());
    }
}
