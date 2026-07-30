package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.exception.GwcGridSetNotFoundException;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcGridSetManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcGridSetManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS   = System.currentTimeMillis();
    private static final String NAME = "INTEG_GS_" + TS;

    private GwcGridSetManager gridSets;

    @BeforeAll
    void setUp() {
        gridSets = client.gwcGridSets();
        try { gridSets.delete(NAME); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { gridSets.delete(NAME); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/gridsets ────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() contains built-in grid set EPSG:4326")
    void list_containsBuiltInGridSet() {
        assertTrue(gridSets.list().contains("EPSG:4326"));
    }

    // ── [2] GET /gwc/rest/gridsets/{name} ────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] get(EPSG:4326) returns a fully-populated built-in grid set")
    void get_epsg4326_returnsDetails() {
        GwcGridSet gs = gridSets.get("EPSG:4326");
        assertEquals("EPSG:4326", gs.getName());
        assertEquals(4326, gs.getSrs().getNumber());
        assertNotNull(gs.getExtent().getCoords());
        assertFalse(gs.getResolutions().isEmpty());
    }

    // ── [3] PUT /gwc/rest/gridsets/{name} — create (201) ─────────────────

    @Test @Order(3)
    @DisplayName("[3] upsert() creates a new custom GridSet")
    void upsert_create_succeeds() {
        GwcGridSet gs = new GwcGridSet(NAME, 4326, -180, -90, 180, 90);
        gs.setResolutions(Arrays.asList(0.703125));
        gs.setTileWidth(256);
        gs.setTileHeight(256);
        gs.setYCoordinateFirst(false);
        gs.setAlignTopLeft(false);
        assertDoesNotThrow(() -> gridSets.upsert(NAME, gs));
    }

    @Test @Order(4)
    @DisplayName("[4] list() contains new GridSet after create")
    void list_afterCreate_containsNewGridSet() {
        assertTrue(gridSets.list().contains(NAME));
    }

    @Test @Order(5)
    @DisplayName("[5] get() returns matching GridSet details after create")
    void get_afterCreate_returnsDetails() {
        GwcGridSet gs = gridSets.get(NAME);
        assertEquals(NAME, gs.getName());
        assertEquals(256, gs.getTileWidth());
    }

    // ── [4] DELETE /gwc/rest/gridsets/{name} ─────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] delete() removes the custom GridSet")
    void delete_thenGet_throwsNotFound() {
        gridSets.delete(NAME);
        assertThrows(GwcGridSetNotFoundException.class, () -> gridSets.get(NAME));
    }

    @Test @Order(7)
    @DisplayName("[7] list() no longer contains the deleted GridSet")
    void list_afterDelete_doesNotContainGridSet() {
        assertFalse(gridSets.list().contains(NAME));
    }
}
