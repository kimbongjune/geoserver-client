package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcIndexResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcIndexManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcIndexManagerIntegrationTest extends BaseIntegrationTest {

    private GwcIndexManager gwcIndex;

    @BeforeAll
    void setUp() {
        gwcIndex = client.gwcIndex();
    }

    // ── [1] GET /gwc/rest ───────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] getIndex() returns non-empty resourceLinks including a 'layers' link")
    void getIndex_returnsResourceLinks() {
        GwcIndexResult result = gwcIndex.getIndex();

        assertNotNull(result);
        assertNotNull(result.getResourceLinks(), "resourceLinks must not be null");
        assertFalse(result.getResourceLinks().isEmpty(), "resourceLinks must not be empty");
        assertTrue(result.getResourceLinks().stream().anyMatch(l -> l.contains("layers")),
                "resourceLinks must contain a link referencing 'layers' but got: " + result.getResourceLinks());
    }

    @Test @Order(2)
    @DisplayName("[2] getIndex() returns non-null non-empty rawHtml")
    void getIndex_returnsRawHtml() {
        GwcIndexResult result = gwcIndex.getIndex();

        assertNotNull(result.getRawHtml(), "rawHtml must not be null");
        assertFalse(result.getRawHtml().isEmpty(), "rawHtml must not be empty");
    }

    @Test @Order(3)
    @DisplayName("[3] getIndex() resourceLinks do not contain filtered image extensions")
    void getIndex_filtersImageLinks() {
        GwcIndexResult result = gwcIndex.getIndex();

        assertTrue(result.getResourceLinks().stream()
                        .noneMatch(l -> l.contains(".png") || l.contains(".jpg") || l.contains(".gif")),
                "resourceLinks must not contain image links but got: " + result.getResourceLinks());
    }

    @Test @Order(4)
    @DisplayName("[4] getIndex() can be called multiple times without error")
    void getIndex_idempotent_succeeds() {
        assertDoesNotThrow(() -> gwcIndex.getIndex());
        assertDoesNotThrow(() -> gwcIndex.getIndex());
    }
}
