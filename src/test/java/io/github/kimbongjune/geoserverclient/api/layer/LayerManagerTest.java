package io.github.kimbongjune.geoserverclient.api.layer;

import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.LayerNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] LayerManager")
class LayerManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private LayerManager manager;

    @BeforeEach
    void setUp() {
        manager = new LayerManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    @Test
    @DisplayName("get(null) throws InvalidParameterException")
    void get_nullName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(null));
    }

    @Test
    @DisplayName("get(\"\") throws InvalidParameterException")
    void get_emptyName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(""));
    }

    @Test
    @DisplayName("get() with 404 throws LayerNotFoundException")
    void get_notFound_throwsLayerNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such layer: missing"));
        assertThrows(LayerNotFoundException.class, () -> manager.get("missing"));
    }

    @Test
    @DisplayName("get() parses layer JSON response correctly")
    void get_success_returnsLayer() {
        String body = "{\"layer\":{\"name\":\"mylayer\",\"type\":\"RASTER\"}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        Layer layer = manager.get("mylayer");
        assertNotNull(layer);
        assertEquals("mylayer", layer.getName());
        assertEquals("RASTER", layer.getType());
    }

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no layers")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"layers\":\"\"}"));
        List<LayerSummary> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses layer array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"layers\":{\"layer\":"
                + "[{\"name\":\"l1\",\"href\":\"http://a\"},{\"name\":\"l2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<LayerSummary> list = manager.list();
        assertEquals(2, list.size());
        assertEquals("l1", list.get(0).getName());
    }
}
