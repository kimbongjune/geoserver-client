package io.github.kimbongjune.geoserverclient.api.resource;

import io.github.kimbongjune.geoserverclient.dto.resource.ResourceHeadInfo;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] ResourceManager")
class ResourceManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private ResourceManager manager;

    @BeforeEach
    void setUp() {
        manager = new ResourceManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, Map<String, String> headers) {
        return new GeoServerResponse(status, (String) null, headers);
    }

    // ── parameter validation ────────────────────────────────────────────

    @Test
    @DisplayName("headMetadata(null) throws InvalidParameterException")
    void headMetadata_nullPath_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.headMetadata(null));
    }

    @Test
    @DisplayName("headMetadata(\"\") throws InvalidParameterException")
    void headMetadata_emptyPath_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.headMetadata(""));
    }

    // ── 404 → null ───────────────────────────────────────────────────────

    @Test
    @DisplayName("headMetadata() returns null on 404")
    void headMetadata_notFound_returnsNull() {
        when(httpClient.head(anyString()))
                .thenReturn(response(404, Collections.<String, String>emptyMap()));
        assertNull(manager.headMetadata("nonexistent.txt"));
    }

    // ── happy path: file resource ───────────────────────────────────────

    @Test
    @DisplayName("headMetadata() parses headers for a file resource")
    void headMetadata_file_parsesHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Resource-Type", "resource");
        headers.put("Last-Modified", "Tue, 04 Aug 2026 00:00:00 GMT");
        headers.put("Content-Type", "application/xml");
        headers.put("Content-Disposition", "attachment; filename=\"global.xml\"");
        when(httpClient.head(anyString())).thenReturn(response(200, headers));

        ResourceHeadInfo info = manager.headMetadata("global.xml");

        assertNotNull(info);
        assertEquals("resource", info.getResourceType());
        assertEquals("Tue, 04 Aug 2026 00:00:00 GMT", info.getLastModified());
        assertEquals("application/xml", info.getContentType());
        assertEquals("global.xml", info.getFileName());
    }

    // ── happy path: directory resource (no Content-Disposition) ────────

    @Test
    @DisplayName("headMetadata() for a directory has null fileName when Content-Disposition absent")
    void headMetadata_directory_nullFileName() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Resource-Type", "directory");
        headers.put("Last-Modified", "Tue, 04 Aug 2026 00:00:00 GMT");
        when(httpClient.head(anyString())).thenReturn(response(200, headers));

        ResourceHeadInfo info = manager.headMetadata("styles");

        assertNotNull(info);
        assertEquals("directory", info.getResourceType());
        assertNull(info.getFileName());
        assertNull(info.getContentType());
    }

    // ── error status ─────────────────────────────────────────────────────

    @Test
    @DisplayName("headMetadata() on 500 throws GeoServerResponseException")
    void headMetadata_serverError_throwsGeoServerResponseException() {
        when(httpClient.head(anyString()))
                .thenReturn(response(500, Collections.<String, String>emptyMap()));
        assertThrows(GeoServerResponseException.class, () -> manager.headMetadata("global.xml"));
    }
}
