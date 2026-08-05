package io.github.kimbongjune.geoserverclient.api.workspace;

import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WorkspaceNotFoundException;
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

import io.github.kimbongjune.geoserverclient.dto.workspace.Workspace;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] WorkspaceManager")
class WorkspaceManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private WorkspaceManager manager;

    @BeforeEach
    void setUp() {
        manager = new WorkspaceManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── get() parameter validation ────────────────────────────────────────

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

    // ── get() 404 → typed exception ──────────────────────────────────────

    @Test
    @DisplayName("get() with 404 response throws WorkspaceNotFoundException")
    void get_notFound_throwsWorkspaceNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such workspace: missing"));
        WorkspaceNotFoundException ex = assertThrows(WorkspaceNotFoundException.class,
                () -> manager.get("missing"));
        assertEquals("missing", ex.getWorkspaceName());
    }

    // ── get() happy path ─────────────────────────────────────────────────

    @Test
    @DisplayName("get() parses workspace JSON response correctly")
    void get_success_returnsWorkspace() {
        String body = "{\"workspace\":{\"name\":\"testws\",\"isolated\":false}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        Workspace ws = manager.get("testws");
        assertNotNull(ws);
        assertEquals("testws", ws.getName());
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no workspaces")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"workspaces\":\"\"}"));
        List<?> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses workspace array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"workspaces\":{\"workspace\":"
                + "[{\"name\":\"ws1\",\"href\":\"http://a\"},{\"name\":\"ws2\",\"href\":\"http://b\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<WorkspaceSummary> list = manager.list();
        assertEquals(2, list.size());
        assertEquals("ws1", list.get(0).getName());
        assertEquals("ws2", list.get(1).getName());
    }

    // ── create() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() with null request throws InvalidParameterException")
    void create_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.create(null));
    }

    @Test
    @DisplayName("create() with 409 response throws ResourceAlreadyExistsException")
    void create_conflict_throwsResourceAlreadyExistsException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(409, "Workspace 'existing' already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create(CreateWorkspaceRequest.builder("existing")));
    }

    // ── delete() parameter validation ────────────────────────────────────

    @Test
    @DisplayName("delete(null) throws InvalidParameterException")
    void delete_nullName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.delete(null));
    }

    @Test
    @DisplayName("delete() with 404 response throws WorkspaceNotFoundException")
    void delete_notFound_throwsWorkspaceNotFoundException() {
        when(httpClient.delete(anyString()))
                .thenReturn(response(404, "No such workspace: gone"));
        assertThrows(WorkspaceNotFoundException.class, () -> manager.delete("gone"));
    }
}
