package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.dto.gwc.GwcIndexResult;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GwcIndexManager#getIndex()}'s HTML link-parsing logic, with the HTTP
 * layer mocked out — no live GeoServer/GWC instance required.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] GwcIndexManager")
class GwcIndexManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private GwcIndexManager manager() {
        return new GwcIndexManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    @Test
    @DisplayName("getIndex() parses href links and filters out embedded images")
    void getIndex_parsesLinks_filtersImages() {
        String html = "<html><body>"
                + "<a href=\"layers/\">layers</a><br>"
                + "<a href=\"gridsets/\">gridsets</a><br>"
                + "<a href=\"web/geowebcache_logo.png\"><img src=\"web/geowebcache_logo.png\"/></a>"
                + "</body></html>";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, html));

        GwcIndexResult result = manager().getIndex();

        assertNotNull(result);
        assertEquals(html, result.getRawHtml());
        List<String> links = result.getResourceLinks();
        assertTrue(links.contains("layers/"));
        assertTrue(links.contains("gridsets/"));
        assertFalse(links.stream().anyMatch(l -> l.endsWith(".png")),
                ".png links must be filtered out");
    }

    @Test
    @DisplayName("getIndex() filters .jpg and .gif links too")
    void getIndex_filtersJpgAndGif() {
        String html = "<a href=\"foo.jpg\">x</a><a href=\"bar.gif\">y</a><a href=\"layers/\">z</a>";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, html));

        GwcIndexResult result = manager().getIndex();

        assertEquals(1, result.getResourceLinks().size());
        assertEquals("layers/", result.getResourceLinks().get(0));
    }

    @Test
    @DisplayName("getIndex() with no href links returns empty resourceLinks but preserves rawHtml")
    void getIndex_noLinks_returnsEmptyList() {
        String html = "<html><body>no links here</body></html>";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, html));

        GwcIndexResult result = manager().getIndex();

        assertNotNull(result.getResourceLinks());
        assertTrue(result.getResourceLinks().isEmpty());
        assertEquals(html, result.getRawHtml());
    }

    @Test
    @DisplayName("getIndex() with null body returns empty resourceLinks, does not throw")
    void getIndex_nullBody_returnsEmptyList() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, null));

        GwcIndexResult result = manager().getIndex();

        assertNotNull(result.getResourceLinks());
        assertTrue(result.getResourceLinks().isEmpty());
        assertNull(result.getRawHtml());
    }

    @Test
    @DisplayName("getIndex() on non-2xx throws GeoServerResponseException")
    void getIndex_errorStatus_throwsGeoServerResponseException() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(406, "Not Acceptable"));
        assertThrows(GeoServerResponseException.class, () -> manager().getIndex());
    }
}
