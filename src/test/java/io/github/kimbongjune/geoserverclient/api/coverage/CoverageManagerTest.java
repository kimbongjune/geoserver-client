package io.github.kimbongjune.geoserverclient.api.coverage;

import io.github.kimbongjune.geoserverclient.dto.coverage.Coverage;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.coverage.CreateCoverageRequest;
import io.github.kimbongjune.geoserverclient.exception.CoverageNotFoundException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] CoverageManager")
class CoverageManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private CoverageManager manager;

    @BeforeEach
    void setUp() {
        manager = new CoverageManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── get() parameter validation ────────────────────────────────────────

    @Test
    @DisplayName("get(null, store, cov) throws InvalidParameterException")
    void get_nullWorkspace_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.get(null, "store", "cov"));
    }

    @Test
    @DisplayName("get() with 404 throws CoverageNotFoundException")
    void get_notFound_throwsCoverageNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such coverage: missing"));
        assertThrows(CoverageNotFoundException.class,
                () -> manager.get("ws", "store", "missing"));
    }

    @Test
    @DisplayName("get() parses coverage JSON response correctly")
    void get_success_returnsCoverage() {
        String body = "{\"coverage\":{\"name\":\"mycov\",\"nativeName\":\"mycov\","
                + "\"namespace\":{\"name\":\"ws\",\"href\":\"http://a\"}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        Coverage cov = manager.get("ws", "store", "mycov");
        assertNotNull(cov);
        assertEquals("mycov", cov.getName());
    }

    // ── list() / listByWorkspace() ──────────────────────────────────────

    @Test
    @DisplayName("list() parses coverage array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"coverages\":{\"coverage\":"
                + "[{\"name\":\"c1\",\"href\":\"http://a\"},{\"name\":\"c2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<CoverageSummary> list = manager.list("ws", "store");
        assertEquals(2, list.size());
        assertEquals("c1", list.get(0).getName());
    }

    @Test
    @DisplayName("listByWorkspace() parses coverage array correctly")
    void listByWorkspace_withResults_returnsParsedList() {
        String body = "{\"coverages\":{\"coverage\":"
                + "[{\"name\":\"c1\",\"href\":\"http://a\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<CoverageSummary> list = manager.listByWorkspace("ws");
        assertEquals(1, list.size());
        assertEquals("c1", list.get(0).getName());
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
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred building a new coverage 'mycov': already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create("ws", "store", CreateCoverageRequest.isNew("mycov")));
    }

    // ── createByWorkspace() ──────────────────────────────────────────────

    @Test
    @DisplayName("createByWorkspace(null, store, req) throws InvalidParameterException")
    void createByWorkspace_nullWorkspace_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.createByWorkspace(null, "store", CreateCoverageRequest.isNew("mycov")));
    }

    @Test
    @DisplayName("createByWorkspace(ws, null, req) throws InvalidParameterException")
    void createByWorkspace_nullStore_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.createByWorkspace("ws", null, CreateCoverageRequest.isNew("mycov")));
    }

    @Test
    @DisplayName("createByWorkspace() with null request throws InvalidParameterException")
    void createByWorkspace_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.createByWorkspace("ws", "store", null));
    }

    @Test
    @DisplayName("createByWorkspace() when GeoServer returns 500 'already exists' throws ResourceAlreadyExistsException")
    void createByWorkspace_alreadyExists_throwsResourceAlreadyExistsException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred building a new coverage 'mycov': already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.createByWorkspace("ws", "store", CreateCoverageRequest.isNew("mycov")));
    }

    @Test
    @DisplayName("createByWorkspace() posts to /rest/workspaces/{ws}/coverages with store embedded in body")
    void createByWorkspace_postsToWorkspacePath_withStoreInBody() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(201, null));
        String getBody = "{\"coverage\":{\"name\":\"mycov\",\"nativeName\":\"mycov\","
                + "\"namespace\":{\"name\":\"ws\",\"href\":\"http://a\"}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, getBody));

        Coverage cov = manager.createByWorkspace("ws", "store",
                CreateCoverageRequest.isNew("mycov").nativeCoverageName("mycov"));

        assertNotNull(cov);
        assertEquals("mycov", cov.getName());

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(pathCaptor.capture(), bodyCaptor.capture(), anyString(), anyString());

        assertEquals("/rest/workspaces/ws/coverages", pathCaptor.getValue());
        String sentBody = bodyCaptor.getValue();
        assertTrue(sentBody.contains("\"name\":\"mycov\""));
        assertTrue(sentBody.contains("\"nativeCoverageName\":\"mycov\""));
        assertTrue(sentBody.contains("\"store\""));
        assertTrue(sentBody.contains("ws:store"), "store name must be workspace-qualified");
    }

    // ── update() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() with 404 throws CoverageNotFoundException")
    void update_notFound_throwsCoverageNotFoundException() {
        when(httpClient.put(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(404, "No such coverage: missing"));
        assertThrows(CoverageNotFoundException.class,
                () -> manager.update("ws", "store", "missing",
                        io.github.kimbongjune.geoserverclient.dto.coverage.UpdateCoverageRequest.builder()
                                .title("x").build()));
    }
}
