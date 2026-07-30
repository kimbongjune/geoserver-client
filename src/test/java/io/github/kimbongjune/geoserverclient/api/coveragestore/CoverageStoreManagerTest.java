package io.github.kimbongjune.geoserverclient.api.coveragestore;

import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStore;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CreateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.CoverageStoreNotFoundException;
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
@DisplayName("[UnitTest] CoverageStoreManager")
class CoverageStoreManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private CoverageStoreManager manager;

    @BeforeEach
    void setUp() {
        manager = new CoverageStoreManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── get() parameter validation ────────────────────────────────────────

    @Test
    @DisplayName("get(null, store) throws InvalidParameterException")
    void get_nullWorkspace_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(null, "store"));
    }

    @Test
    @DisplayName("get(ws, null) throws InvalidParameterException")
    void get_nullStore_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get("ws", null));
    }

    // ── get() 404 → typed exception ──────────────────────────────────────

    @Test
    @DisplayName("get() with 404 throws CoverageStoreNotFoundException")
    void get_notFound_throwsCoverageStoreNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such coverage store: missing"));
        CoverageStoreNotFoundException ex = assertThrows(CoverageStoreNotFoundException.class,
                () -> manager.get("myws", "missing"));
        assertEquals("myws/missing", ex.getStoreName());
    }

    // ── get() happy path ─────────────────────────────────────────────────

    @Test
    @DisplayName("get() parses coverageStore JSON response correctly")
    void get_success_returnsCoverageStore() {
        String body = "{\"coverageStore\":{\"name\":\"mycs\",\"type\":\"GeoTIFF\","
                + "\"enabled\":true,\"workspace\":{\"name\":\"myws\",\"href\":\"http://a\"}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        CoverageStore cs = manager.get("myws", "mycs");
        assertNotNull(cs);
        assertEquals("mycs", cs.getName());
        assertEquals("GeoTIFF", cs.getType());
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no coverage stores")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"coverageStores\":\"\"}"));
        List<CoverageStoreSummary> result = manager.list("myws");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses coverageStore array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"coverageStores\":{\"coverageStore\":"
                + "[{\"name\":\"cs1\",\"href\":\"http://a\"},{\"name\":\"cs2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<CoverageStoreSummary> list = manager.list("myws");
        assertEquals(2, list.size());
        assertEquals("cs1", list.get(0).getName());
    }

    // ── create() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() with null request throws InvalidParameterException")
    void create_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.create("ws", null));
    }

    @Test
    @DisplayName("create() when GeoServer returns 500 'already exists' throws ResourceAlreadyExistsException")
    void create_alreadyExists_throwsResourceAlreadyExistsException() {
        // GeoServer returns 500 (not 409) for duplicate store names
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred building a new data store 'existing': already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create("myws", CreateCoverageStoreRequest.of("existing")));
    }
}
