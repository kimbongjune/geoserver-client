package io.github.kimbongjune.geoserverclient.api.datastore;

import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.exception.DataStoreNotFoundException;
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

import io.github.kimbongjune.geoserverclient.dto.datastore.DataStore;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] DataStoreManager")
class DataStoreManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private DataStoreManager manager;

    @BeforeEach
    void setUp() {
        manager = new DataStoreManager(httpClient, new SerializerFactory(), DataFormat.JSON);
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

    @Test
    @DisplayName("get(ws, \"\") throws InvalidParameterException")
    void get_emptyStore_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get("ws", ""));
    }

    // ── get() 404 → typed exception ──────────────────────────────────────

    @Test
    @DisplayName("get() with 404 response throws DataStoreNotFoundException with store name")
    void get_notFound_throwsDataStoreNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such datastore: missing"));
        DataStoreNotFoundException ex = assertThrows(DataStoreNotFoundException.class,
                () -> manager.get("myws", "missing"));
        assertEquals("myws/missing", ex.getStoreName());
    }

    // ── get() happy path ─────────────────────────────────────────────────

    @Test
    @DisplayName("get() parses datastore JSON response correctly")
    void get_success_returnsDataStore() {
        String body = "{\"dataStore\":{\"name\":\"myds\",\"type\":\"Shapefile\","
                + "\"enabled\":true,\"workspace\":{\"name\":\"myws\"},"
                + "\"connectionParameters\":{\"entry\":[]}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        DataStore ds = manager.get("myws", "myds");
        assertNotNull(ds);
        assertEquals("myds", ds.getName());
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no datastores")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"dataStores\":\"\"}"));
        List<?> result = manager.list("myws");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses datastore array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"dataStores\":{\"dataStore\":"
                + "[{\"name\":\"ds1\",\"href\":\"http://a\"},{\"name\":\"ds2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<DataStoreSummary> list = manager.list("myws");
        assertEquals(2, list.size());
        assertEquals("ds1", list.get(0).getName());
        assertEquals("ds2", list.get(1).getName());
    }

    // ── create() duplicate detection ─────────────────────────────────────

    @Test
    @DisplayName("create() with 500 + 'already exists' body throws ResourceAlreadyExistsException")
    void create_duplicate_throwsResourceAlreadyExistsException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "DataStore 'myds' already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create("myws", CreateDataStoreRequest.of("myds")));
    }

    @Test
    @DisplayName("create() with 500 + unrelated body re-throws as GeoServerResponseException")
    void create_serverError_notDuplicate_throwsGeoServerResponseException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Connection refused to PostGIS"));
        assertThrows(io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException.class,
                () -> manager.create("myws", CreateDataStoreRequest.of("myds")));
    }
}
