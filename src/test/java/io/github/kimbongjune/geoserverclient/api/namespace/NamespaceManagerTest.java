package io.github.kimbongjune.geoserverclient.api.namespace;

import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.Namespace;
import io.github.kimbongjune.geoserverclient.dto.namespace.NamespaceSummary;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.NamespaceNotFoundException;
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
@DisplayName("[UnitTest] NamespaceManager")
class NamespaceManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private NamespaceManager manager;

    @BeforeEach
    void setUp() {
        manager = new NamespaceManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    @Test
    @DisplayName("get(null) throws InvalidParameterException")
    void get_nullPrefix_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(null));
    }

    @Test
    @DisplayName("get(\"\") throws InvalidParameterException")
    void get_emptyPrefix_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.get(""));
    }

    @Test
    @DisplayName("get() with 404 throws NamespaceNotFoundException")
    void get_notFound_throwsNamespaceNotFoundException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(404, "No such namespace: missing"));
        NamespaceNotFoundException ex = assertThrows(NamespaceNotFoundException.class,
                () -> manager.get("missing"));
        assertEquals("missing", ex.getPrefix());
    }

    @Test
    @DisplayName("get() parses namespace JSON response correctly")
    void get_success_returnsNamespace() {
        String body = "{\"namespace\":{\"prefix\":\"myns\",\"uri\":\"http://myns.example.com\"}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        Namespace ns = manager.get("myns");
        assertNotNull(ns);
        assertEquals("myns", ns.getPrefix());
    }

    @Test
    @DisplayName("list() returns empty list when GeoServer signals no namespaces")
    void list_geoServerEmptySignal_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, "{\"namespaces\":\"\"}"));
        List<NamespaceSummary> result = manager.list();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() parses namespace array correctly")
    void list_withResults_returnsParsedList() {
        String body = "{\"namespaces\":{\"namespace\":"
                + "[{\"prefix\":\"ns1\",\"uri\":\"http://ns1\"},{\"prefix\":\"ns2\",\"uri\":\"http://ns2\"}]}}";
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(200, body));
        List<NamespaceSummary> list = manager.list();
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("create() with null request throws InvalidParameterException")
    void create_nullRequest_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.create(null));
    }

    @Test
    @DisplayName("create() when GeoServer returns 500 'already exists' throws ResourceAlreadyExistsException")
    void create_alreadyExists_throwsResourceAlreadyExistsException() {
        // GeoServer 2.28.2 returns 500 (not 409) for duplicate namespace prefixes
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(response(500, "Error occurred creating namespace 'existing': already exists"));
        assertThrows(ResourceAlreadyExistsException.class,
                () -> manager.create(CreateNamespaceRequest.builder("existing", "http://existing.example.com")));
    }
}
