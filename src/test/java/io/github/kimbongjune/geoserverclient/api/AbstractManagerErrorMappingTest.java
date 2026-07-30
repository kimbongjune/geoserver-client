package io.github.kimbongjune.geoserverclient.api;

import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
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

/**
 * Unit tests for {@link AbstractManager#handleErrorResponse}'s HTTP status → exception mapping —
 * exercised directly through a minimal subclass, with no HTTP calls or live GeoServer involved.
 * This is the one piece of logic shared by all 44 managers, so it deserves fast, deterministic
 * coverage independent of the Docker-based integration suite.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] AbstractManager error mapping")
class AbstractManagerErrorMappingTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private static class ProbeManager extends AbstractManager {
        ProbeManager(GeoServerHttpClient httpClient) {
            super(httpClient, new SerializerFactory(), DataFormat.JSON);
        }

        void handle(GeoServerResponse response) {
            handleErrorResponse(response, "GET", "/test/path");
        }
    }

    private ProbeManager probe() {
        return new ProbeManager(httpClient);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    @Test
    @DisplayName("2xx responses pass through without throwing")
    void successfulResponse_doesNotThrow() {
        assertDoesNotThrow(() -> probe().handle(response(200, "ok")));
        assertDoesNotThrow(() -> probe().handle(response(201, "created")));
        assertDoesNotThrow(() -> probe().handle(response(204, null)));
    }

    @Test
    @DisplayName("401 maps to AuthenticationException")
    void unauthorized_mapsToAuthenticationException() {
        AuthenticationException e = assertThrows(AuthenticationException.class,
                () -> probe().handle(response(401, "Unauthorized")));
        assertEquals(401, e.getStatusCode());
    }

    @Test
    @DisplayName("403 maps to AuthenticationException")
    void forbidden_mapsToAuthenticationException() {
        AuthenticationException e = assertThrows(AuthenticationException.class,
                () -> probe().handle(response(403, "Forbidden")));
        assertEquals(403, e.getStatusCode());
    }

    @Test
    @DisplayName("404 maps to ResourceNotFoundException")
    void notFound_mapsToResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> probe().handle(response(404, "Not found")));
    }

    @Test
    @DisplayName("405 maps to GeoServerResponseException")
    void methodNotAllowed_mapsToGeoServerResponseException() {
        GeoServerResponseException e = assertThrows(GeoServerResponseException.class,
                () -> probe().handle(response(405, "Method Not Allowed")));
        assertEquals(405, e.getStatusCode());
    }

    @Test
    @DisplayName("unmapped 5xx status falls back to generic GeoServerResponseException with the real code preserved")
    void unmappedServerError_mapsToGenericGeoServerResponseException() {
        GeoServerResponseException e = assertThrows(GeoServerResponseException.class,
                () -> probe().handle(response(500, "Internal Server Error")));
        assertEquals(500, e.getStatusCode());
    }
}
