package io.github.kimbongjune.geoserverclient.api.reset;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] ResetManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResetManagerIntegrationTest extends BaseIntegrationTest {

    private ResetManager reset;

    @BeforeAll
    void setUp() {
        reset = client.reset();
    }

    // ── [1] reset() ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] reset() completes without exception (200)")
    void reset_succeeds() {
        assertDoesNotThrow(() -> reset.reset(),
                "POST /rest/reset must return 200 without exception");
    }

    // ── [2] reload() ──────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] reload() completes without exception (200)")
    void reload_succeeds() {
        assertDoesNotThrow(() -> reset.reload(),
                "POST /rest/reload must return 200 without exception");
    }
}
