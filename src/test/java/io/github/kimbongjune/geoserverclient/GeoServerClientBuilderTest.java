package io.github.kimbongjune.geoserverclient;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * No live GeoServer required — {@link GeoServerClient.Builder#build()} validation is pure
 * parameter checking. Covers {@code DataFormat.XML} being rejected as a client-wide default: most
 * managers hardcode JSON internally regardless of this setting, and GeoServer's XML responses
 * collapse a single-item list to a bare element (unlike JSON, always an array), so the shared
 * list-parsing logic used across ~40 managers cannot honor an XML default safely today. See
 * {@code CHANGELOG.md} ("Fixed (breaking — pre-release quality pass)").
 */
@DisplayName("[UnitTest] GeoServerClient.Builder validation")
class GeoServerClientBuilderTest {

    private GeoServerClient.Builder validBuilder() {
        return GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver");
    }

    @Test
    @DisplayName("defaultFormat(XML) fails fast at build() instead of silently behaving as JSON")
    void defaultFormatXml_rejectedAtBuild() {
        GeoServerClient.Builder builder = validBuilder().defaultFormat(DataFormat.XML);

        InvalidParameterException e = assertThrows(InvalidParameterException.class, builder::build);
        assertTrue(e.getMessage().contains("defaultFormat"), "message should name the offending param: " + e.getMessage());
    }

    @Test
    @DisplayName("defaultFormat(JSON) — the only supported value — builds successfully")
    void defaultFormatJson_buildsSuccessfully() throws Exception {
        try (GeoServerClient client = validBuilder().defaultFormat(DataFormat.JSON).build()) {
            assertEquals(DataFormat.JSON, client.getDefaultFormat());
        }
    }

    @Test
    @DisplayName("omitting defaultFormat() defaults to JSON and builds successfully")
    void defaultFormatOmitted_defaultsToJsonAndBuilds() throws Exception {
        try (GeoServerClient client = validBuilder().build()) {
            assertEquals(DataFormat.JSON, client.getDefaultFormat());
        }
    }

    @Test
    @DisplayName("missing url still fails with InvalidParameterException (pre-existing validation unaffected)")
    void missingUrl_stillRejected() {
        GeoServerClient.Builder builder = GeoServerClient.builder().credentials("admin", "geoserver");
        assertThrows(InvalidParameterException.class, builder::build);
    }
}
