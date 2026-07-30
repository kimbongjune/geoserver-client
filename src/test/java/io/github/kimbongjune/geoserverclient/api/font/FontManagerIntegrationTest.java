package io.github.kimbongjune.geoserverclient.api.font;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] FontManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FontManagerIntegrationTest extends BaseIntegrationTest {

    private FontManager fonts;

    @BeforeAll
    void setUp() {
        fonts = client.fonts();
    }

    // ── [1] list() ────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() returns non-empty font list")
    void list_returnsNonEmptyList() {
        List<String> list = fonts.list();
        assertNotNull(list);
        assertFalse(list.isEmpty(),
                "Font list must not be empty (JVM system fonts always present)");
    }

    @Test
    @Order(2)
    @DisplayName("[2] list() all entries are non-null non-blank strings")
    void list_allEntriesNonBlank() {
        List<String> list = fonts.list();
        list.forEach(name -> {
            assertNotNull(name, "Font name must not be null");
            assertFalse(name.trim().isEmpty(), "Font name must not be blank");
        });
    }

    @Test
    @Order(3)
    @DisplayName("[3] list() is sorted in ascending alphabetical order (TreeSet)")
    void list_isSortedAscending() {
        List<String> list = fonts.list();
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).compareTo(list.get(i)) <= 0,
                    "Font list must be sorted: '" + list.get(i - 1)
                            + "' must come before '" + list.get(i) + "'");
        }
    }

    @Test
    @Order(4)
    @DisplayName("[4] list() contains common JVM fonts (SansSerif, Serif, Dialog)")
    void list_containsCommonJvmFonts() {
        List<String> list = fonts.list();
        // These logical fonts are always available in any JVM
        assertTrue(list.contains("SansSerif") || list.contains("Dialog"),
                "Expected at least one JVM logical font in list: " + list.subList(0, Math.min(10, list.size())));
    }

    @Test
    @Order(5)
    @DisplayName("[5] list() returns consistent results on repeated calls")
    void list_isIdempotent() {
        List<String> first  = fonts.list();
        List<String> second = fonts.list();
        assertEquals(first.size(), second.size(), "Font list size must be stable across calls");
        assertEquals(first, second, "Font list must be identical across calls");
    }
}
