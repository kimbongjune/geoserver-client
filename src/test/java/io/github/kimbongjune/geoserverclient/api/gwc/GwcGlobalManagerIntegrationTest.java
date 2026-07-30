package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGlobalSettings;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcGlobalManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcGlobalManagerIntegrationTest extends BaseIntegrationTest {

    private GwcGlobalManager global;
    private GwcGlobalSettings original;

    @BeforeAll
    void setUp() {
        global = client.gwcGlobal();
        original = global.get();
    }

    @AfterAll
    void cleanUp() {
        GwcGlobalSettings restore = new GwcGlobalSettings();
        restore.setRuntimeStatsEnabled(original.getRuntimeStatsEnabled());
        try { global.update(restore); } catch (Exception ignored) {}
    }

    // ── [1] GET /gwc/rest/global ─────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] get() returns a non-null GwcGlobalSettings with a version")
    void get_returnsSettings() {
        assertNotNull(original);
        assertNotNull(original.getVersion(), "version must be populated (read-only field)");
    }

    @Test @Order(2)
    @DisplayName("[2] get() populates backendTimeout")
    void get_containsBackendTimeout() {
        assertNotNull(original.getBackendTimeout());
    }

    // ── [2] PUT /gwc/rest/global ─────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] update() with runtimeStatsEnabled=false succeeds and is reflected on next get()")
    void update_disableRuntimeStats_succeeds() {
        GwcGlobalSettings update = new GwcGlobalSettings();
        update.setRuntimeStatsEnabled(false);
        assertDoesNotThrow(() -> global.update(update));

        GwcGlobalSettings after = global.get();
        assertEquals(Boolean.FALSE, after.getRuntimeStatsEnabled());
    }

    @Test @Order(4)
    @DisplayName("[4] update() restores runtimeStatsEnabled=true and leaves unrelated fields untouched")
    void update_restoreRuntimeStats_succeeds() {
        GwcGlobalSettings update = new GwcGlobalSettings();
        update.setRuntimeStatsEnabled(true);
        assertDoesNotThrow(() -> global.update(update));

        GwcGlobalSettings after = global.get();
        assertEquals(Boolean.TRUE, after.getRuntimeStatsEnabled());
        assertEquals(original.getBackendTimeout(), after.getBackendTimeout(),
                "partial update must not clobber fields it didn't set");
    }
}
