package io.github.kimbongjune.geoserverclient.api.style;

import io.github.kimbongjune.geoserverclient.dto.style.Style;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.StyleNotFoundException;
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
@DisplayName("[UnitTest] StyleManager")
class StyleManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private StyleManager manager;

    @BeforeEach
    void setUp() {
        manager = new StyleManager(httpClient, new SerializerFactory(), DataFormat.JSON);
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
    @DisplayName("get() with 404 throws StyleNotFoundException")
    void get_notFound_throwsStyleNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such style: missing"));
        assertThrows(StyleNotFoundException.class, () -> manager.get("missing"));
    }

    @Test
    @DisplayName("get() parses style JSON response correctly")
    void get_success_returnsStyle() {
        String body = "{\"style\":{\"name\":\"mystyle\",\"filename\":\"mystyle.sld\"}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        Style style = manager.get("mystyle");
        assertNotNull(style);
        assertEquals("mystyle", style.getName());
        assertEquals("mystyle.sld", style.getFilename());
    }

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no styles")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"styles\":\"\"}"));
        List<StyleSummary> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses style array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"styles\":{\"style\":"
                + "[{\"name\":\"point\",\"href\":\"http://a\"},{\"name\":\"line\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<StyleSummary> list = manager.list();
        assertEquals(2, list.size());
        assertEquals("point", list.get(0).getName());
    }
}
