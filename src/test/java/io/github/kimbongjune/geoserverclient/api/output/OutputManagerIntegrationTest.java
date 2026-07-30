package io.github.kimbongjune.geoserverclient.api.output;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link OutputManager}.
 *
 * <p>Verified against GeoServer 2.28.2:
 * <ul>
 *   <li>GET /rest/fonts     → HTTP 200, JSON with font list</li>
 *   <li>GET /rest/templates → HTTP 200, JSON (empty template set)</li>
 * </ul>
 */
@DisplayName("[IntegrationTest] OutputManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OutputManagerIntegrationTest extends BaseIntegrationTest {

    private OutputManager output;

    @BeforeAll
    void setUp() {
        output = client.output();
    }

    // ── [1] getFonts() ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] getFonts() returns a non-empty list")
    void getFonts_returnsNonEmptyList() {
        List<String> result = output.getFonts();
        assertNotNull(result, "getFonts() must not return null");
        assertFalse(result.isEmpty(), "getFonts() must not return an empty list");
    }

    @Test
    @Order(2)
    @DisplayName("[2] getFonts() matches FontManager.list()")
    void getFonts_matchesFontManager() {
        assertEquals(client.fonts().list(), output.getFonts());
    }

    @Test
    @Order(3)
    @DisplayName("[3] getFonts() returns consistent results")
    void getFonts_isIdempotent() {
        List<String> first  = output.getFonts();
        List<String> second = output.getFonts();
        assertEquals(first, second, "getFonts() must return stable response across calls");
    }

    // ── [2] getTemplates() ─────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] getTemplates() does not throw and returns a non-null list")
    void getTemplates_returnsNonNullList() {
        List<TemplateInfo> result = output.getTemplates();
        assertNotNull(result, "getTemplates() must not return null");
    }

    @Test
    @Order(5)
    @DisplayName("[5] getTemplates() matches TemplateManager.list()")
    void getTemplates_matchesTemplateManager() {
        List<TemplateInfo> fromOutput = output.getTemplates();
        List<TemplateInfo> fromTemplateManager = client.templates().list();
        assertEquals(fromTemplateManager.size(), fromOutput.size());
    }

    @Test
    @Order(6)
    @DisplayName("[6] getTemplates() returns consistent results")
    void getTemplates_isIdempotent() {
        List<TemplateInfo> first  = output.getTemplates();
        List<TemplateInfo> second = output.getTemplates();
        assertEquals(first.size(), second.size(), "getTemplates() must return stable response across calls");
    }
}
