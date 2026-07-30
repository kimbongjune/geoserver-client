package io.github.kimbongjune.geoserverclient.dto.gwc;

import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for {@link GwcTileBounds#parse(String)} — no GeoServer instance required.
 * Covers the Java double-brace-array text format GWC returns from
 * {@code GET /gwc/rest/bounds/{layer}/{srs}/java}.
 */
@DisplayName("[UnitTest] GwcTileBounds")
class GwcTileBoundsTest {

    @Test
    @DisplayName("parses multiple zoom-level entries in order")
    void parse_multipleEntries_returnsAllInOrder() {
        GwcTileBounds bounds = GwcTileBounds.parse("{{1, 0, 1, 0, 0}, {3, 1, 3, 1, 1}}");

        List<long[]> coverages = bounds.getCoverages();
        assertEquals(2, coverages.size());
        assertArrayEquals(new long[]{1, 0, 1, 0, 0}, coverages.get(0));
        assertArrayEquals(new long[]{3, 1, 3, 1, 1}, coverages.get(1));
    }

    @Test
    @DisplayName("forZoomLevel finds the matching entry by the 5th field")
    void forZoomLevel_matchingZoom_returnsEntry() {
        GwcTileBounds bounds = GwcTileBounds.parse("{{1, 0, 1, 0, 0}, {3, 1, 3, 1, 1}}");

        assertArrayEquals(new long[]{3, 1, 3, 1, 1}, bounds.forZoomLevel(1));
        assertNull(bounds.forZoomLevel(99));
    }

    @Test
    @DisplayName("negative tile indices parse correctly")
    void parse_negativeValues_parsesCorrectly() {
        GwcTileBounds bounds = GwcTileBounds.parse("{{-1, -2, -3, -4, 0}}");

        assertArrayEquals(new long[]{-1, -2, -3, -4, 0}, bounds.getCoverages().get(0));
    }

    @Test
    @DisplayName("empty coverage text yields an empty (not null) list")
    void parse_noEntries_returnsEmptyList() {
        GwcTileBounds bounds = GwcTileBounds.parse("{}");

        assertTrue(bounds.getCoverages().isEmpty());
    }

    @Test
    @DisplayName("null input throws SerializationException rather than NPE")
    void parse_nullInput_throwsSerializationException() {
        assertThrows(SerializationException.class, () -> GwcTileBounds.parse(null));
    }

    @Test
    @DisplayName("equal content parsed separately is equal (Arrays-aware equals/hashCode)")
    void equals_sameContentDifferentInstances_areEqual() {
        GwcTileBounds a = GwcTileBounds.parse("{{1, 0, 1, 0, 0}, {3, 1, 3, 1, 1}}");
        GwcTileBounds b = GwcTileBounds.parse("{{1, 0, 1, 0, 0}, {3, 1, 3, 1, 1}}");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.toString().contains("1, 0, 1, 0, 0"), "toString must print element values, not array identities");
    }
}
