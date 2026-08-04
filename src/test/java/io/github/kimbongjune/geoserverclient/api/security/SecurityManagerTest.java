package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SecurityManager}'s bulk ACL-delete methods
 * ({@code deleteAllLayerAcl()}, {@code deleteAllServiceAcl()}, {@code deleteAllRestAcl()}).
 *
 * <p>These are confirmed to always throw {@link GeoServerResponseException}(500) on GeoServer
 * 2.28.2 due to a server-side {@code StringIndexOutOfBoundsException} bug (see the
 * {@code @Deprecated} Javadoc on each method) — these tests pin down that the client maps the
 * mocked 500 response to the expected exception, not that the call ever succeeds.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] SecurityManager")
class SecurityManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private SecurityManager manager;

    @BeforeEach
    void setUp() {
        manager = new SecurityManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    @Test
    @DisplayName("deleteAllLayerAcl() maps 500 response to GeoServerResponseException")
    void deleteAllLayerAcl_500_throwsGeoServerResponseException() {
        when(httpClient.delete(anyString()))
                .thenReturn(response(500, "begin 5, end 4, length 4"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.deleteAllLayerAcl());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    @DisplayName("deleteAllServiceAcl() maps 500 response to GeoServerResponseException")
    void deleteAllServiceAcl_500_throwsGeoServerResponseException() {
        when(httpClient.delete(anyString()))
                .thenReturn(response(500, "begin 5, end 4, length 4"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.deleteAllServiceAcl());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    @DisplayName("deleteAllRestAcl() maps 500 response to GeoServerResponseException")
    void deleteAllRestAcl_500_throwsGeoServerResponseException() {
        when(httpClient.delete(anyString()))
                .thenReturn(response(500, "begin 5, end 4, length 4"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.deleteAllRestAcl());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    @DisplayName("deleteAllLayerAcl() calls DELETE /rest/security/acl/layers (no trailing rule segment)")
    void deleteAllLayerAcl_callsBulkPath() {
        when(httpClient.delete("/rest/security/acl/layers"))
                .thenReturn(response(500, "err"));
        assertThrows(GeoServerResponseException.class, () -> manager.deleteAllLayerAcl());
    }
}
