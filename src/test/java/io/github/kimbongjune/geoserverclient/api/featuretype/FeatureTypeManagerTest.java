package io.github.kimbongjune.geoserverclient.api.featuretype;

import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureType;
import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureTypeSummary;
import io.github.kimbongjune.geoserverclient.exception.FeatureTypeNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
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
@DisplayName("[UnitTest] FeatureTypeManager")
class FeatureTypeManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private FeatureTypeManager manager;

    @BeforeEach
    void setUp() {
        manager = new FeatureTypeManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── get() parameter validation ────────────────────────────────────────

    @Test
    @DisplayName("get(null, store, name) throws InvalidParameterException")
    void get_nullWorkspace_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.get(null, "store", "ft"));
    }

    @Test
    @DisplayName("get(ws, null, name) throws InvalidParameterException")
    void get_nullStore_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.get("ws", null, "ft"));
    }

    @Test
    @DisplayName("get(ws, store, null) throws InvalidParameterException")
    void get_nullName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.get("ws", "store", null));
    }

    // ── get() 404 → typed exception ──────────────────────────────────────

    @Test
    @DisplayName("get() with 404 throws FeatureTypeNotFoundException")
    void get_notFound_throwsFeatureTypeNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such feature type: missing"));
        assertThrows(FeatureTypeNotFoundException.class,
                () -> manager.get("ws", "store", "missing"));
    }

    // ── get() happy path ─────────────────────────────────────────────────

    @Test
    @DisplayName("get() parses featureType JSON response correctly")
    void get_success_returnsFeatureType() {
        String body = "{\"featureType\":{\"name\":\"myft\",\"nativeName\":\"myft\","
                + "\"namespace\":{\"name\":\"ws\",\"href\":\"http://a\"}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        FeatureType ft = manager.get("ws", "store", "myft");
        assertNotNull(ft);
        assertEquals("myft", ft.getName());
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no feature types")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"featureTypes\":\"\"}"));
        List<FeatureTypeSummary> result = manager.list("ws", "store");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses featureType array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"featureTypes\":{\"featureType\":"
                + "[{\"name\":\"ft1\",\"href\":\"http://a\"},{\"name\":\"ft2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<FeatureTypeSummary> list = manager.list("ws", "store");
        assertEquals(2, list.size());
        assertEquals("ft1", list.get(0).getName());
    }

    // ── create() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() with null request throws InvalidParameterException")
    void create_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.create("ws", "store", null));
    }

    @Test
    @DisplayName("create() when GeoServer returns 500 'already exists' throws ResourceAlreadyExistsException")
    void create_alreadyExists_throwsResourceAlreadyExistsException() {
        // GeoServer returns 500 (not 409) for duplicate feature type names
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred building a new feature type 'myft': already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create("ws", "store", CreateFeatureTypeRequest.of("myft")));
    }
}
