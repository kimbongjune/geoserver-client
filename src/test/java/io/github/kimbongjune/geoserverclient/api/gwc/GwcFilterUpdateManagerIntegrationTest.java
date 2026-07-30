package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcFilterUpdateManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcFilterUpdateManagerIntegrationTest extends BaseIntegrationTest {

    private GwcFilterUpdateManager filterUpdates;

    @BeforeAll
    void setUp() {
        filterUpdates = client.gwcFilterUpdates();
    }

    // ── [1] POST /gwc/rest/filter/{filterName}/update/xml ────────────────

    @Test @Order(1)
    @DisplayName("[1] updateFilterXml(nonexistent, xml) throws GeoServerException (400)")
    void updateFilterXml_nonExistentFilter_throwsException() {
        io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterContent content =
                io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterContent.of(
                        "<filter><name>nonexistent_filter_integ</name></filter>");
        assertThrows(GeoServerException.class,
                () -> filterUpdates.updateFilterXml("nonexistent_filter_integ", content),
                "Non-existent filter must throw GeoServerException (400)");
    }

    // ── [1] POST /gwc/rest/filter/{filterName}/update/zip ─────────────────

    @Test @Order(2)
    @DisplayName("[2] updateFilterZip(nonexistent, zip) throws GeoServerException (400)")
    void updateFilterZip_nonExistentFilter_throwsException() throws IOException {
        File zip = File.createTempFile("gwc-filter-update-integ", ".zip");
        zip.deleteOnExit();
        Files.write(zip.toPath(), new byte[]{0x50, 0x4B, 0x05, 0x06, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}); // empty ZIP EOCD
        try {
            assertThrows(GeoServerException.class,
                    () -> filterUpdates.updateFilterZip("nonexistent_filter_integ", zip),
                    "Non-existent filter must throw GeoServerException (400)");
        } finally {
            zip.delete();
        }
    }
}
