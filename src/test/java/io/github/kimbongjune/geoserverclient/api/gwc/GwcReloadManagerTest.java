package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GwcReloadManager#reload()}'s response-body parsing logic, with the HTTP
 * layer mocked out — no live GeoServer/GWC instance required. GWC can return HTTP 200 even when
 * the reload internally failed, so the real signal is text inside the HTML body; these tests
 * pin down that parsing without needing a running server.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] GwcReloadManager")
class GwcReloadManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private GwcReloadManager manager() {
        return new GwcReloadManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    @Test
    @DisplayName("body containing 'reloaded' and a layer count is parsed into both fields")
    void reload_successBody_parsesReloadedAndLayerCount() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new GeoServerResponse(200,
                        "GeoWebCache configuration reloaded. Read 3 layers from configuration resources.",
                        Collections.<String, String>emptyMap()));

        GwcReloadResult result = manager().reload();

        assertTrue(result.isReloaded());
        assertEquals(3, result.getLayerCount());
    }

    @Test
    @DisplayName("body without the 'reloaded' marker is treated as not reloaded, count still parsed if present")
    void reload_bodyWithoutReloadedMarker_reportsNotReloaded() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new GeoServerResponse(200,
                        "Read 5 layers from configuration resources.",
                        Collections.<String, String>emptyMap()));

        GwcReloadResult result = manager().reload();

        assertFalse(result.isReloaded());
        assertEquals(5, result.getLayerCount());
    }

    @Test
    @DisplayName("body without a layer count leaves layerCount null instead of throwing")
    void reload_bodyWithoutLayerCount_leavesCountNull() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new GeoServerResponse(200, "reloaded", Collections.<String, String>emptyMap()));

        GwcReloadResult result = manager().reload();

        assertTrue(result.isReloaded());
        assertNull(result.getLayerCount());
    }

    @Test
    @DisplayName("non-2xx response is mapped to GeoServerResponseException, not silently parsed")
    void reload_errorStatus_throwsGeoServerResponseException() {
        when(httpClient.post(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new GeoServerResponse(400, "Bad request", Collections.<String, String>emptyMap()));

        assertThrows(GeoServerResponseException.class, () -> manager().reload());
    }
}
