package io.github.kimbongjune.geoserverclient.api.wms;

import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayer;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] WmsLayerManager")
class WmsLayerManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private WmsLayerManager manager;

    @BeforeEach
    void setUp() {
        manager = new WmsLayerManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── publishByWorkspace() parameter validation ───────────────────────

    @Test
    @DisplayName("publishByWorkspace(null, store, req) throws InvalidParameterException")
    void publishByWorkspace_nullWorkspace_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.publishByWorkspace(null, "store", PublishWmsLayerRequest.of("l", "ws:l")));
    }

    @Test
    @DisplayName("publishByWorkspace(ws, null, req) throws InvalidParameterException")
    void publishByWorkspace_nullStore_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.publishByWorkspace("ws", null, PublishWmsLayerRequest.of("l", "ws:l")));
    }

    @Test
    @DisplayName("publishByWorkspace() with null request throws InvalidParameterException")
    void publishByWorkspace_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class,
                () -> manager.publishByWorkspace("ws", "store", null));
    }

    // ── publishByWorkspace() already-exists pre-check ───────────────────

    @Test
    @DisplayName("publishByWorkspace() when layer already exists throws ResourceAlreadyExistsException before POST")
    void publishByWorkspace_alreadyExists_throwsResourceAlreadyExistsException() {
        // exists() check -> GET returns 200
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, "{}"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.publishByWorkspace("ws", "store", PublishWmsLayerRequest.of("l", "ws:l")));
    }

    // ── publishByWorkspace() happy path: posts to workspace path with store embedded ────

    @Test
    @DisplayName("publishByWorkspace() posts to /rest/workspaces/{ws}/wmslayers with store embedded in body")
    void publishByWorkspace_postsToWorkspacePath_withStoreInBody() {
        // exists() check -> GET 404 (does not exist yet)
        String getBody = "{\"wmsLayer\":{\"name\":\"l\",\"nativeName\":\"ws:l\","
                + "\"namespace\":{\"name\":\"ws\",\"href\":\"http://a\"}}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "not found"))   // exists() check
                .thenReturn(response(200, getBody));       // get() after POST
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(201, null));

        WmsLayer layer = manager.publishByWorkspace("ws", "store", PublishWmsLayerRequest.of("l", "ws:l"));

        assertNotNull(layer);
        assertEquals("l", layer.getName());

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(pathCaptor.capture(), bodyCaptor.capture(), anyString(), anyString());

        assertEquals("/rest/workspaces/ws/wmslayers", pathCaptor.getValue());
        String sentBody = bodyCaptor.getValue();
        assertTrue(sentBody.contains("\"name\":\"l\""));
        assertTrue(sentBody.contains("\"nativeName\":\"ws:l\""));
        assertTrue(sentBody.contains("\"store\""));
        assertTrue(sentBody.contains("ws:store"), "store name must be workspace-qualified");
    }
}
