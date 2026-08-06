package io.github.kimbongjune.geoserverclient.dto.gwc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for fields added in 1.1.2:
 * GwcSeedRequest.gridSetId and GwcLayer.GridSubset.zoomStart/zoomStop.
 */
@DisplayName("[UnitTest] GWC DTO 1.1.2 fields")
class GwcDtoFieldsTest {

    @Test
    @DisplayName("GwcSeedRequest: gridSetId getter and setter")
    void seedRequest_gridSetId_roundTrip() {
        GwcSeedRequest req = new GwcSeedRequest("ws:layer", 4326, 0, 5, "image/png", GwcSeedType.SEED);
        assertNull(req.getGridSetId());

        req.setGridSetId("EPSG:4326");
        assertEquals("EPSG:4326", req.getGridSetId());
    }

    @Test
    @DisplayName("GwcSeedRequest: equals/hashCode includes gridSetId")
    void seedRequest_equals_withGridSetId() {
        GwcSeedRequest a = new GwcSeedRequest("l", 4326, 0, 5, "image/png", GwcSeedType.SEED);
        GwcSeedRequest b = new GwcSeedRequest("l", 4326, 0, 5, "image/png", GwcSeedType.SEED);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setGridSetId("EPSG:4326");
        assertNotEquals(a, b);

        b.setGridSetId("EPSG:4326");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("GwcSeedRequest: toString includes gridSetId")
    void seedRequest_toString_includesGridSetId() {
        GwcSeedRequest req = new GwcSeedRequest("l", 4326, 0, 5, "image/png", GwcSeedType.SEED);
        req.setGridSetId("EPSG:4326");
        assertTrue(req.toString().contains("gridSetId=EPSG:4326"));
    }

    @Test
    @DisplayName("GwcLayer.GridSubset: zoomStart/zoomStop getter and setter")
    void gridSubset_zoomRange_roundTrip() {
        GwcLayer.GridSubset subset = new GwcLayer.GridSubset("EPSG:4326");
        assertNull(subset.getZoomStart());
        assertNull(subset.getZoomStop());

        subset.setZoomStart(2);
        subset.setZoomStop(12);
        assertEquals(2, subset.getZoomStart());
        assertEquals(12, subset.getZoomStop());
    }

    @Test
    @DisplayName("GwcLayer.GridSubset: equals/hashCode includes zoom range")
    void gridSubset_equals_withZoomRange() {
        GwcLayer.GridSubset a = new GwcLayer.GridSubset("EPSG:4326");
        GwcLayer.GridSubset b = new GwcLayer.GridSubset("EPSG:4326");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        a.setZoomStart(0);
        a.setZoomStop(10);
        assertNotEquals(a, b);

        b.setZoomStart(0);
        b.setZoomStop(10);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("GwcLayer.GridSubset: toString includes zoom range")
    void gridSubset_toString_includesZoomRange() {
        GwcLayer.GridSubset subset = new GwcLayer.GridSubset("EPSG:4326");
        subset.setZoomStart(3);
        subset.setZoomStop(15);
        String s = subset.toString();
        assertTrue(s.contains("zoomStart=3"));
        assertTrue(s.contains("zoomStop=15"));
    }
}
