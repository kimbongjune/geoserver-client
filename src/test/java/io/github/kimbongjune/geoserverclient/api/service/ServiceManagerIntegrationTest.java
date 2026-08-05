package io.github.kimbongjune.geoserverclient.api.service;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.service.ServiceSettings;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("[IntegrationTest] ServiceManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServiceManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS = System.currentTimeMillis();
    private static final String WS = "svc_ws_" + TS;

    private ServiceManager services;

    @BeforeAll
    void setUp() {
        services = client.services();
        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, false); } catch (Exception ignored) { }
    }

    // ── [1] getWms() ─────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] getWms() returns non-null with name=WMS and enabled=true")
    void getWms_returnsValidSettings() {
        ServiceSettings wms = services.getWms();

        assertNotNull(wms, "WMS settings must not be null");
        assertEquals("WMS", wms.getName(), "WMS name must be WMS");
        assertTrue(Boolean.TRUE.equals(wms.getEnabled()), "WMS must be enabled");
    }

    // ── [2] updateWms() MERGE ─────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] updateWms() MERGE: changed field updated, other fields preserved")
    void updateWms_mergeSemantics() {
        ServiceSettings before    = services.getWms();
        Boolean originalVerbose   = before.getVerbose();
        Integer originalMaxBuffer = before.getMaxBuffer();
        boolean newVerbose        = !Boolean.TRUE.equals(originalVerbose);

        ServiceSettings patch = new ServiceSettings();
        patch.setVerbose(newVerbose);
        services.updateWms(patch);

        ServiceSettings after = services.getWms();
        assertEquals(newVerbose, after.getVerbose(), "verbose must be updated");
        assertEquals(originalMaxBuffer, after.getMaxBuffer(),
                "maxBuffer must be preserved (MERGE semantics)");

        // restore original value
        ServiceSettings restore = new ServiceSettings();
        restore.setVerbose(originalVerbose);
        services.updateWms(restore);
    }

    // ── [3] getWfs() ─────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] getWfs() returns non-null with name=WFS and enabled=true")
    void getWfs_returnsValidSettings() {
        ServiceSettings wfs = services.getWfs();

        assertNotNull(wfs, "WFS settings must not be null");
        assertEquals("WFS", wfs.getName(), "WFS name must be WFS");
        assertTrue(Boolean.TRUE.equals(wfs.getEnabled()), "WFS must be enabled");
    }

    // ── [4] updateWfs() MERGE ─────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] updateWfs() MERGE: maxFeatures updated, serviceLevel preserved")
    void updateWfs_mergeSemantics() {
        ServiceSettings before        = services.getWfs();
        Integer originalMaxFeatures   = before.getMaxFeatures();
        String  originalServiceLevel  = before.getServiceLevel();
        int     newMaxFeatures        = (originalMaxFeatures != null ? originalMaxFeatures : 1000000) + 1;

        ServiceSettings patch = new ServiceSettings();
        patch.setMaxFeatures(newMaxFeatures);
        services.updateWfs(patch);

        ServiceSettings after = services.getWfs();
        assertEquals(newMaxFeatures, after.getMaxFeatures(), "maxFeatures must be updated");
        assertEquals(originalServiceLevel, after.getServiceLevel(),
                "serviceLevel must be preserved (MERGE semantics)");

        // restore original value
        ServiceSettings restore = new ServiceSettings();
        restore.setMaxFeatures(originalMaxFeatures);
        services.updateWfs(restore);
    }

    // ── [5] getWcs() ─────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("[5] getWcs() returns non-null with name=WCS and enabled=true")
    void getWcs_returnsValidSettings() {
        ServiceSettings wcs = services.getWcs();

        assertNotNull(wcs, "WCS settings must not be null");
        assertEquals("WCS", wcs.getName(), "WCS name must be WCS");
        assertTrue(Boolean.TRUE.equals(wcs.getEnabled()), "WCS must be enabled");
    }

    // ── [6] updateWcs() MERGE ─────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] updateWcs() MERGE: changed field updated, other fields preserved")
    void updateWcs_mergeSemantics() {
        ServiceSettings before       = services.getWcs();
        Boolean originalVerbose      = before.getVerbose();
        Boolean originalGmlPrefixing = before.getGmlPrefixing();
        boolean newVerbose           = !Boolean.TRUE.equals(originalVerbose);

        ServiceSettings patch = new ServiceSettings();
        patch.setVerbose(newVerbose);
        services.updateWcs(patch);

        ServiceSettings after = services.getWcs();
        assertEquals(newVerbose, after.getVerbose(), "verbose must be updated");
        assertEquals(originalGmlPrefixing, after.getGmlPrefixing(),
                "gmlPrefixing must be preserved (MERGE semantics)");

        // restore original value
        ServiceSettings restore = new ServiceSettings();
        restore.setVerbose(originalVerbose);
        services.updateWcs(restore);
    }

    // ── [7] getWmts() ────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] getWmts() returns non-null with name=WMTS and enabled=true")
    void getWmts_returnsValidSettings() {
        ServiceSettings wmts = services.getWmts();

        assertNotNull(wmts, "WMTS settings must not be null");
        assertEquals("WMTS", wmts.getName(), "WMTS name must be WMTS");
        assertTrue(Boolean.TRUE.equals(wmts.getEnabled()), "WMTS must be enabled");
    }

    // ── [8] updateWmts() MERGE ───────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] updateWmts() MERGE: title updated, verbose preserved")
    void updateWmts_mergeSemantics() {
        ServiceSettings before   = services.getWmts();
        String originalTitle    = before.getTitle();
        Boolean originalVerbose = before.getVerbose();
        String newTitle         = "IntegTest WMTS " + TS;

        ServiceSettings patch = new ServiceSettings();
        patch.setTitle(newTitle);
        services.updateWmts(patch);

        ServiceSettings after = services.getWmts();
        assertEquals(newTitle, after.getTitle(), "title must be updated");
        assertEquals(originalVerbose, after.getVerbose(),
                "verbose must be preserved (MERGE semantics)");

        // restore original value
        ServiceSettings restore = new ServiceSettings();
        restore.setTitle(originalTitle);
        services.updateWmts(restore);
    }

    // ── [9] getWorkspaceSettings() → 404 ─────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] getWorkspaceSettings(wms, WS) → ResourceNotFoundException (ws settings not created)")
    void getWorkspaceSettings_notCreated_throws404() {
        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wms", WS),
                "Expected ResourceNotFoundException for missing ws settings");
    }

    // ── [10] updateWorkspaceSettings() creates ws settings ─────────────────

    @Test
    @Order(10)
    @DisplayName("[10] updateWorkspaceSettings(wms, WS, ...) creates ws settings and getWorkspaceSettings returns them")
    void updateWorkspaceSettings_createsSettings() {
        ServiceSettings patch = new ServiceSettings();
        patch.setEnabled(true);
        patch.setVerbose(false);

        assertDoesNotThrow(() -> services.updateWorkspaceSettings("wms", WS, patch),
                "updateWorkspaceSettings must not throw");

        ServiceSettings created = services.getWorkspaceSettings("wms", WS);
        assertNotNull(created, "ws settings must be retrievable after PUT");
        assertTrue(Boolean.TRUE.equals(created.getEnabled()), "enabled must be true after PUT");
    }

    // ── [11] deleteWorkspaceSettings() removes ws settings ───────────────

    @Test
    @Order(11)
    @DisplayName("[11] deleteWorkspaceSettings(wms, WS) removes settings → getWorkspaceSettings throws 404")
    void deleteWorkspaceSettings_removesSettings() {
        assertDoesNotThrow(() -> services.deleteWorkspaceSettings("wms", WS),
                "deleteWorkspaceSettings must not throw");

        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wms", WS),
                "Expected ResourceNotFoundException after delete");
    }

    // ── [12] WFS per-workspace GET → 404 ──────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] getWorkspaceSettings(wfs, WS) → ResourceNotFoundException (not created)")
    void getWfsWorkspaceSettings_notCreated_throws404() {
        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wfs", WS));
    }

    // ── [13] WFS per-workspace PUT → creates settings, verify GET (no gml field) ──

    @Test
    @Order(13)
    @DisplayName("[13] updateWorkspaceSettings(wfs, WS) creates wfs settings (no gml field)")
    void updateWfsWorkspaceSettings_createsSettings() {
        ServiceSettings patch = new ServiceSettings();
        patch.setEnabled(true);
        patch.setServiceLevel("COMPLETE");

        assertDoesNotThrow(() -> services.updateWorkspaceSettings("wfs", WS, patch));

        ServiceSettings created = services.getWorkspaceSettings("wfs", WS);
        assertNotNull(created);
        assertTrue(Boolean.TRUE.equals(created.getEnabled()));
    }

    // ── [14] WFS per-workspace PUT with gml field → verify actual server behavior ─

    @Test
    @Order(14)
    @DisplayName("[14] updateWorkspaceSettings(wfs, WS) with gml field → succeeds (GeoServer 2.28.2: no 500)")
    void updateWfsWorkspaceSettings_withGml_succeeds() {
        // Previously documented as 500 when gml field is included,
        // but GeoServer 2.28.2 actual behavior: succeeds without exception (gml field is ignored)
        ServiceSettings withGml = new ServiceSettings();
        withGml.setEnabled(true);
        withGml.setGml(new java.util.LinkedHashMap<String, Object>());

        assertDoesNotThrow(() -> services.updateWorkspaceSettings("wfs", WS, withGml),
                "GeoServer 2.28.2: gml field in ws-level WFS PUT does not cause 500");
    }

    // ── [15] WFS per-workspace DELETE ────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("[15] deleteWorkspaceSettings(wfs, WS) removes settings → GET throws 404")
    void deleteWfsWorkspaceSettings_removesSettings() {
        assertDoesNotThrow(() -> services.deleteWorkspaceSettings("wfs", WS));

        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wfs", WS));
    }

    // ── [16] WCS per-workspace GET → 404 ──────────────────────────────────

    @Test
    @Order(16)
    @DisplayName("[16] getWorkspaceSettings(wcs, WS) → ResourceNotFoundException (not created)")
    void getWcsWorkspaceSettings_notCreated_throws404() {
        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wcs", WS));
    }

    // ── [17] WCS per-workspace PUT → creates settings, verify GET ─────────

    @Test
    @Order(17)
    @DisplayName("[17] updateWorkspaceSettings(wcs, WS) creates wcs settings")
    void updateWcsWorkspaceSettings_createsSettings() {
        ServiceSettings patch = new ServiceSettings();
        patch.setEnabled(true);
        patch.setVerbose(false);

        assertDoesNotThrow(() -> services.updateWorkspaceSettings("wcs", WS, patch));

        ServiceSettings created = services.getWorkspaceSettings("wcs", WS);
        assertNotNull(created);
        assertTrue(Boolean.TRUE.equals(created.getEnabled()));
    }

    // ── [18] WCS per-workspace DELETE ────────────────────────────────────

    @Test
    @Order(18)
    @DisplayName("[18] deleteWorkspaceSettings(wcs, WS) removes settings → GET throws 404")
    void deleteWcsWorkspaceSettings_removesSettings() {
        assertDoesNotThrow(() -> services.deleteWorkspaceSettings("wcs", WS));

        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wcs", WS));
    }

    // ── [19] WMTS per-workspace GET → 404 ─────────────────────────────────

    @Test
    @Order(19)
    @DisplayName("[19] getWorkspaceSettings(wmts, WS) → ResourceNotFoundException (not created)")
    void getWmtsWorkspaceSettings_notCreated_throws404() {
        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wmts", WS));
    }

    // ── [20] WMTS per-workspace PUT → creates settings, verify GET ─────────

    @Test
    @Order(20)
    @DisplayName("[20] updateWorkspaceSettings(wmts, WS) creates wmts settings")
    void updateWmtsWorkspaceSettings_createsSettings() {
        ServiceSettings patch = new ServiceSettings();
        patch.setEnabled(true);

        assertDoesNotThrow(() -> services.updateWorkspaceSettings("wmts", WS, patch));

        ServiceSettings created = services.getWorkspaceSettings("wmts", WS);
        assertNotNull(created);
        assertTrue(Boolean.TRUE.equals(created.getEnabled()));
    }

    // ── [21] WMTS per-workspace DELETE ───────────────────────────────────

    @Test
    @Order(21)
    @DisplayName("[21] deleteWorkspaceSettings(wmts, WS) removes settings → GET throws 404")
    void deleteWmtsWorkspaceSettings_removesSettings() {
        assertDoesNotThrow(() -> services.deleteWorkspaceSettings("wmts", WS));

        assertThrows(ResourceNotFoundException.class,
                () -> services.getWorkspaceSettings("wmts", WS));
    }
}
