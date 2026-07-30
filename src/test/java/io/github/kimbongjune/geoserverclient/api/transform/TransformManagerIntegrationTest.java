package io.github.kimbongjune.geoserverclient.api.transform;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link TransformManager}.
 *
 * <p>The XSLT Transform plugin is not supported in GeoServer 2.28.x (stable/community).
 * {@code isAvailable()} must always return {@code false}.
 */
@DisplayName("[IntegrationTest] TransformManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransformManagerIntegrationTest extends BaseIntegrationTest {

    private TransformManager transforms;

    @BeforeAll
    void setUp() {
        transforms = client.transforms();
    }

    // ── [1] isAvailable() ─────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] isAvailable() returns false when XSLT plugin not installed")
    void isAvailable_returnsFalseWhenPluginNotInstalled() {
        // XSLT plugin not supported in GeoServer 2.28.x → 404 → false
        assertFalse(transforms.isAvailable(),
                "XSLT Transform plugin is not supported in GeoServer 2.28.x; isAvailable() must return false");
    }

    @Test
    @Order(2)
    @DisplayName("[2] isAvailable() does not throw any exception")
    void isAvailable_doesNotThrow() {
        assertDoesNotThrow(() -> transforms.isAvailable(),
                "isAvailable() must not throw — it should handle 404 gracefully");
    }

    @Test
    @Order(3)
    @DisplayName("[3] isAvailable() returns consistent result on repeated calls")
    void isAvailable_isIdempotent() {
        boolean first  = transforms.isAvailable();
        boolean second = transforms.isAvailable();
        assertEquals(first, second, "isAvailable() must return stable result across calls");
    }
}
