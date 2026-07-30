package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcReloadManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcReloadManagerIntegrationTest extends BaseIntegrationTest {

    private GwcReloadManager reload;

    @BeforeAll
    void setUp() {
        reload = client.gwcReload();
    }

    // ── [1] POST /gwc/rest/reload ─────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] reload() reports success and a parsed layer count")
    void reload_reportsSuccess() {
        GwcReloadResult result = reload.reload();
        assertNotNull(result);
        assertTrue(result.isReloaded(), "reload response must indicate success but was: " + result.getRawMessage());
        assertNotNull(result.getLayerCount(), "layer count must be parsed from the response");
    }

    @Test @Order(2)
    @DisplayName("[2] reload() can be called multiple times without error")
    void reload_idempotent_succeeds() {
        assertDoesNotThrow(() -> reload.reload());
        assertDoesNotThrow(() -> reload.reload());
    }
}
