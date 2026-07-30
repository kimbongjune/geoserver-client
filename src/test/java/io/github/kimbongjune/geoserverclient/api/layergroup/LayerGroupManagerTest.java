package io.github.kimbongjune.geoserverclient.api.layergroup;

import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroup;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroupSummary;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.LayerGroupNotFoundException;
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
@DisplayName("[UnitTest] LayerGroupManager")
class LayerGroupManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private LayerGroupManager manager;

    @BeforeEach
    void setUp() {
        manager = new LayerGroupManager(httpClient, new SerializerFactory(), DataFormat.JSON);
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
    @DisplayName("get() with 404 throws LayerGroupNotFoundException")
    void get_notFound_throwsLayerGroupNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such layer group: missing"));
        assertThrows(LayerGroupNotFoundException.class, () -> manager.get("missing"));
    }

    @Test
    @DisplayName("get() parses layerGroup JSON response correctly")
    void get_success_returnsLayerGroup() {
        String body = "{\"layerGroup\":{\"name\":\"mygroup\",\"mode\":\"SINGLE\"}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        LayerGroup lg = manager.get("mygroup");
        assertNotNull(lg);
        assertEquals("mygroup", lg.getName());
    }

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no layer groups")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"layerGroups\":\"\"}"));
        List<LayerGroupSummary> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses layerGroup array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"layerGroups\":{\"layerGroup\":"
                + "[{\"name\":\"g1\",\"href\":\"http://a\"},{\"name\":\"g2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<LayerGroupSummary> list = manager.list();
        assertEquals(2, list.size());
        assertEquals("g1", list.get(0).getName());
    }
}
