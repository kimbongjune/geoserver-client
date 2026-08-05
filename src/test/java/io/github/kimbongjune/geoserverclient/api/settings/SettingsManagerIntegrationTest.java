package io.github.kimbongjune.geoserverclient.api.settings;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.settings.Contact;
import io.github.kimbongjune.geoserverclient.dto.settings.GlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.WorkspaceSettings;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] SettingsManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SettingsManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS = System.currentTimeMillis();
    private static final String WS = "set_ws_" + TS;

    private SettingsManager settings;

    @BeforeAll
    void setUp() {
        settings = client.settings();
        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
    }

    @AfterAll
    void cleanUp() {
        try {
            WorkspaceSettings ws = settings.getWorkspaceSettings(WS);
            if (ws.hasLocalSettings()) {
                settings.deleteWorkspaceSettings(WS);
            }
        } catch (Exception ignored) { }
        try {
            client.workspaces().delete(WS, false);
        } catch (Exception ignored) { }
    }

    // ── [1] getGlobal() ──────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] getGlobal() returns non-null with settings/jai/coverageAccess")
    void getGlobal_returnsFullStructure() {
        GlobalSettings global = settings.getGlobal();

        assertNotNull(global, "GlobalSettings must not be null");
        assertNotNull(global.getSettings(), "GlobalSettings.settings must not be null");
        assertNotNull(global.getJai(),      "GlobalSettings.jai must not be null");
        assertNotNull(global.getCoverageAccess(), "GlobalSettings.coverageAccess must not be null");
    }

    @Test
    @Order(2)
    @DisplayName("[2] getGlobal() settings has valid charset and numDecimals")
    void getGlobal_settingsHasValidDefaults() {
        GlobalSettings.GeoServerSettings s = settings.getGlobal().getSettings();

        assertNotNull(s.getCharset(),   "charset must not be null");
        assertFalse(s.getCharset().trim().isEmpty(), "charset must not be blank");
        assertTrue(s.getNumDecimals() > 0, "numDecimals must be > 0");
    }

    // ── [3] getContact() ─────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] getContact() returns non-null Contact")
    void getContact_returnsNonNull() {
        Contact contact = settings.getContact();
        assertNotNull(contact, "Contact must not be null");
    }

    // ── [4] updateContact() ──────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] updateContact() MERGE: only changed field updates, others preserved")
    void updateContact_mergeSemantics() {
        Contact before = settings.getContact();
        String originalOrg  = before.getContactOrganization();
        String originalCity = before.getAddressCity();

        Contact patch = new Contact();
        patch.setContactOrganization("IntegTest-Org-" + TS);
        settings.updateContact(patch);

        Contact after = settings.getContact();
        assertEquals("IntegTest-Org-" + TS, after.getContactOrganization(),
                "contactOrganization must be updated");
        assertEquals(originalCity, after.getAddressCity(),
                "addressCity must be preserved (MERGE semantics)");

        // restore original value
        Contact restore = new Contact();
        restore.setContactOrganization(originalOrg);
        settings.updateContact(restore);
    }

    @Test
    @Order(5)
    @DisplayName("[5] updateContact() restored: contactOrganization matches original")
    void updateContact_restored() {
        Contact before = settings.getContact();
        String original = before.getContactOrganization();

        Contact patch = new Contact();
        patch.setContactOrganization("temp-" + TS);
        settings.updateContact(patch);

        Contact restore = new Contact();
        restore.setContactOrganization(original);
        settings.updateContact(restore);

        Contact after = settings.getContact();
        assertEquals(original, after.getContactOrganization(),
                "contactOrganization must be restored to original value");
    }

    // ── [5] getWorkspaceSettings() ───────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] getWorkspaceSettings() before create: 200 but hasLocalSettings=false")
    void getWorkspaceSettings_beforeCreate_noLocalSettings() {
        WorkspaceSettings ws = settings.getWorkspaceSettings(WS);

        assertNotNull(ws, "WorkspaceSettings must not be null");
        assertFalse(ws.hasLocalSettings(),
                "hasLocalSettings must be false before createWorkspaceSettings()");
    }

    // ── [6] createWorkspaceSettings() ────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] createWorkspaceSettings() succeeds without exception")
    void createWorkspaceSettings_succeeds() {
        assertDoesNotThrow(
                () -> settings.createWorkspaceSettings(WS, WorkspaceSettings.of(WS)),
                "createWorkspaceSettings must not throw"
        );
    }

    @Test
    @Order(8)
    @DisplayName("[8] getWorkspaceSettings() after create: hasLocalSettings=true, charset/numDecimals set")
    void getWorkspaceSettings_afterCreate_hasLocalSettings() {
        WorkspaceSettings ws = settings.getWorkspaceSettings(WS);

        assertTrue(ws.hasLocalSettings(),
                "hasLocalSettings must be true after createWorkspaceSettings()");
        assertNotNull(ws.getCharset(), "charset must not be null");
        assertFalse(ws.getCharset().trim().isEmpty(), "charset must not be blank");
        assertTrue(ws.getNumDecimals() > 0, "numDecimals must be > 0");
    }

    // ── [7] updateWorkspaceSettings() ────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] updateWorkspaceSettings() changes numDecimals")
    void updateWorkspaceSettings_changesNumDecimals() {
        WorkspaceSettings patch = WorkspaceSettings.of(WS);
        patch.setNumDecimals(6);
        settings.updateWorkspaceSettings(WS, patch);

        WorkspaceSettings result = settings.getWorkspaceSettings(WS);
        assertEquals(6, result.getNumDecimals(),
                "numDecimals must be updated to 6");
    }

    // ── [2] updateGlobal() ────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[10] updateGlobal() REPLACE: numDecimals survives roundtrip")
    void updateGlobal_replaceSemantics() {
        GlobalSettings original = settings.getGlobal();
        int originalDecimals = original.getSettings().getNumDecimals();

        original.getSettings().setNumDecimals(originalDecimals);
        settings.updateGlobal(original);

        GlobalSettings after = settings.getGlobal();
        assertEquals(originalDecimals, after.getSettings().getNumDecimals(),
                "numDecimals must survive PUT/GET roundtrip");
    }

    // ── [8] deleteWorkspaceSettings() ────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] deleteWorkspaceSettings() succeeds, then hasLocalSettings=false")
    void deleteWorkspaceSettings_succeeds() {
        assertDoesNotThrow(
                () -> settings.deleteWorkspaceSettings(WS),
                "deleteWorkspaceSettings must not throw when local settings exist"
        );

        WorkspaceSettings ws = settings.getWorkspaceSettings(WS);
        assertFalse(ws.hasLocalSettings(),
                "hasLocalSettings must be false after deleteWorkspaceSettings()");
    }

    @Test
    @Order(12)
    @DisplayName("[12] deleteWorkspaceSettings() when no local settings → GeoServerResponseException (500)")
    void deleteWorkspaceSettings_noLocalSettings_throws500() {
        GeoServerResponseException ex = assertThrows(
                GeoServerResponseException.class,
                () -> settings.deleteWorkspaceSettings(WS),
                "deleteWorkspaceSettings with no local settings must throw GeoServerResponseException"
        );
        assertEquals(500, ex.getStatusCode(),
                "Status code must be 500 (GeoServer NPE bug)");
    }
}
