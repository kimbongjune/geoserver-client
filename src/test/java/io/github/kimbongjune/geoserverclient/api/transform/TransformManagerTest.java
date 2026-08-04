package io.github.kimbongjune.geoserverclient.api.transform;

import io.github.kimbongjune.geoserverclient.dto.transform.CreateTransformRequest;
import io.github.kimbongjune.geoserverclient.dto.transform.Transform;
import io.github.kimbongjune.geoserverclient.dto.transform.UpdateTransformRequest;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
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

/**
 * Unit tests for {@link TransformManager}, with the HTTP layer mocked out.
 *
 * <p>The XSLT Transform plugin is not installed on any GeoServer 2.28.x instance this library
 * was tested against (confirmed via curl: {@code GET /rest/services/wfs/transforms} returns a
 * genuine 404 — see {@link TransformManagerIntegrationTest}), so the success-path response
 * envelope/behavior below is implemented per GeoServer's general REST list-collection convention
 * (used consistently elsewhere in this library) but could not be verified against a live server.
 * These tests pin down that implemented behavior with mocks.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] TransformManager")
class TransformManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private TransformManager manager;

    @BeforeEach
    void setUp() {
        manager = new TransformManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── isAvailable() ────────────────────────────────────────────────────

    @Test
    @DisplayName("isAvailable() returns true on 200")
    void isAvailable_200_returnsTrue() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, "{}"));
        assertTrue(manager.isAvailable());
    }

    @Test
    @DisplayName("isAvailable() returns false on 404")
    void isAvailable_404_returnsFalse() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(404, "not found"));
        assertFalse(manager.isAvailable());
    }

    @Test
    @DisplayName("isAvailable() returns false when the HTTP client throws")
    void isAvailable_exception_returnsFalse() {
        when(httpClient.get(anyString(), anyString())).thenThrow(new RuntimeException("boom"));
        assertFalse(manager.isAvailable());
    }

    // ── list() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("list() returns empty list on 404 (plugin not installed)")
    void list_404_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(404, "not found"));
        List<String> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses transform name array from the collection envelope")
    void list_success_parsesNames() {
        String body = "{\"transforms\":{\"transform\":"
                + "[{\"name\":\"t1\"},{\"name\":\"t2\"}]}}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, body));
        List<String> result = manager.list();
        assertEquals(2, result.size());
        assertTrue(result.contains("t1"));
        assertTrue(result.contains("t2"));
    }

    // ── get() ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("get(null) throws InvalidParameterException")
    void get_nullName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(null));
    }

    @Test
    @DisplayName("get() with 404 throws ResourceNotFoundException")
    void get_notFound_throwsResourceNotFoundException() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class, () -> manager.get("missing"));
    }

    @Test
    @DisplayName("get() parses transform envelope correctly")
    void get_success_parsesTransform() {
        String body = "{\"transform\":{\"name\":\"t1\",\"featureType\":\"topp:states\","
                + "\"outputFormat\":\"gml3\",\"outputMimeType\":\"application/xml\",\"xslt\":\"t1.xsl\"}}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, body));
        Transform t = manager.get("t1");
        assertNotNull(t);
        assertEquals("t1", t.getName());
        assertEquals("topp:states", t.getFeatureType());
        assertEquals("gml3", t.getOutputFormat());
        assertEquals("application/xml", t.getOutputMimeType());
        assertEquals("t1.xsl", t.getXslt());
    }

    // ── create() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() with null request throws InvalidParameterException")
    void create_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.create(null));
    }

    @Test
    @DisplayName("create() sends the expected JSON body with all fields")
    void create_sendsExpectedBody() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(201, null));
        String getBody = "{\"transform\":{\"name\":\"t1\"}}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, getBody));

        Transform t = manager.create(CreateTransformRequest.of("t1")
                .featureType("topp:states")
                .outputFormat("gml3")
                .outputMimeType("application/xml")
                .xslt("t1.xsl"));

        assertNotNull(t);
        assertEquals("t1", t.getName());

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(pathCaptor.capture(), bodyCaptor.capture(), anyString(), anyString());
        assertEquals("/rest/services/wfs/transforms", pathCaptor.getValue());
        String sentBody = bodyCaptor.getValue();
        assertTrue(sentBody.contains("\"name\":\"t1\""));
        assertTrue(sentBody.contains("\"featureType\":\"topp:states\""));
        assertTrue(sentBody.contains("\"outputFormat\":\"gml3\""));
        assertTrue(sentBody.contains("\"outputMimeType\":\"application/xml\""));
        assertTrue(sentBody.contains("\"xslt\":\"t1.xsl\""));
    }

    @Test
    @DisplayName("create() when GeoServer returns 500 'already exists' throws ResourceAlreadyExistsException")
    void create_alreadyExists_throwsResourceAlreadyExistsException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred: transform 't1' already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create(CreateTransformRequest.of("t1")));
    }

    // ── update() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() with 404 throws ResourceNotFoundException")
    void update_notFound_throwsResourceNotFoundException() {
        when(httpClient.put(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class,
                () -> manager.update("missing", UpdateTransformRequest.builder().outputFormat("gml3")));
    }

    // ── delete() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete(null) throws InvalidParameterException")
    void delete_nullName_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.delete(null));
    }

    @Test
    @DisplayName("delete() with 404 throws ResourceNotFoundException")
    void delete_notFound_throwsResourceNotFoundException() {
        when(httpClient.delete(anyString())).thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class, () -> manager.delete("missing"));
    }

    @Test
    @DisplayName("delete() success does not throw")
    void delete_success_doesNotThrow() {
        when(httpClient.delete(anyString())).thenReturn(response(200, null));
        assertDoesNotThrow(() -> manager.delete("t1"));
    }
}
