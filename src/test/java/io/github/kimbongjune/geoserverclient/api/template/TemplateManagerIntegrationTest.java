package io.github.kimbongjune.geoserverclient.api.template;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateContent;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] TemplateManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TemplateManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS        = System.currentTimeMillis();
    private static final String TMP_WS    = "tmpl_ws_" + TS;
    private static final String TMP_DS    = "tmpl_ds_" + TS;
    private static final String TMP_FT    = "tmpl_ft_" + TS;
    private static final String TMP_CS    = "tmpl_cs_" + TS;
    private static final String TMP_COV   = "tmpl_cov_" + TS;

    private static final String ROOT_TMPL  = "_copilot_root_"  + TS + ".ftl";
    private static final String WS_TMPL    = "_copilot_ws_"    + TS + ".ftl";
    private static final String DS_TMPL    = "_copilot_ds_"    + TS + ".ftl";
    private static final String FT_TMPL    = "_copilot_ft_"    + TS + ".ftl";
    private static final String CS_TMPL    = "_copilot_cs_"    + TS + ".ftl";
    private static final String COV_TMPL   = "_copilot_cov_"   + TS + ".ftl";

    private static final String FTL_V1 = "<#-- test template v1 -->\nhello ${name}";
    private static final String FTL_V2 = "<#-- test template v2 -->\ngoodbye ${name}";

    private TemplateManager templates;

    @BeforeAll
    void setUp() {
        templates = client.templates();
        // Create a temporary workspace for workspace/ds/ft/cs/cov level tests
        try {
            client.workspaces().create(CreateWorkspaceRequest.builder(TMP_WS));
        } catch (Exception ignored) {}

        // Pre-cleanup in case leftover templates from a failed previous run
        try { templates.delete(ROOT_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByWorkspace(TMP_WS, WS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByDatastore(TMP_WS, TMP_DS, DS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByFeatureType(TMP_WS, TMP_DS, TMP_FT, FT_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByCoverageStore(TMP_WS, TMP_CS, CS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByCoverage(TMP_WS, TMP_CS, TMP_COV, COV_TMPL); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { templates.delete(ROOT_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByWorkspace(TMP_WS, WS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByDatastore(TMP_WS, TMP_DS, DS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByFeatureType(TMP_WS, TMP_DS, TMP_FT, FT_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByCoverageStore(TMP_WS, TMP_CS, CS_TMPL); } catch (Exception ignored) {}
        try { templates.deleteByCoverage(TMP_WS, TMP_CS, TMP_COV, COV_TMPL); } catch (Exception ignored) {}
        try { client.workspaces().delete(TMP_WS, true); } catch (Exception ignored) {}
    }

    // ── Root level ────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() does not throw and returns a List (possibly empty)")
    void listRoot_doesNotThrow() {
        List<TemplateInfo> result = templates.list();

        assertNotNull(result, "list() must return a non-null List");
        // We cannot assume empty, other templates may exist; just verify type
    }

    @Test @Order(2)
    @DisplayName("[2] put() creates a root-level template (201)")
    void putRoot_createsTemplate() {
        assertDoesNotThrow(() -> templates.put(ROOT_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(3)
    @DisplayName("[3] list() after put() contains the new template")
    void listRoot_afterPut_containsTemplate() {
        List<TemplateInfo> result = templates.list();

        assertNotNull(result);
        assertTrue(
                result.stream().anyMatch(t -> ROOT_TMPL.equals(t.getName())),
                "list must contain " + ROOT_TMPL + " after put");
    }

    @Test @Order(4)
    @DisplayName("[4] get() returns the FTL content that was put")
    void getRoot_returnsContent() {
        String content = templates.get(ROOT_TMPL).getBody();

        assertNotNull(content, "content must not be null");
        assertEquals(FTL_V1, content.trim(), "content must match what was put");
    }

    @Test @Order(5)
    @DisplayName("[5] put() updates existing template (201)")
    void putRoot_updatesTemplate() {
        assertDoesNotThrow(() -> templates.put(ROOT_TMPL, TemplateContent.of(FTL_V2)));
    }

    @Test @Order(6)
    @DisplayName("[6] get() returns the updated FTL content")
    void getRoot_afterUpdate_returnsNewContent() {
        String content = templates.get(ROOT_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V2, content.trim(), "content must reflect the update");
    }

    @Test @Order(7)
    @DisplayName("[7] delete() removes the root-level template (200)")
    void deleteRoot_removesTemplate() {
        assertDoesNotThrow(() -> templates.delete(ROOT_TMPL));
    }

    @Test @Order(8)
    @DisplayName("[8] get() on non-existent template throws ResourceNotFoundException (404)")
    void getRoot_nonExistent_throwsNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> templates.get("nonexistent_copilot_tmpl_99999.ftl"),
                "get() on non-existent template must throw ResourceNotFoundException");
    }

    // ── Workspace level ───────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("[9] listByWorkspace() returns a List for a new workspace (possibly empty)")
    void listByWorkspace_returnsEmptyOrList() {
        List<TemplateInfo> result = templates.listByWorkspace(TMP_WS);

        assertNotNull(result, "listByWorkspace() must return a non-null List");
    }

    @Test @Order(10)
    @DisplayName("[10] putByWorkspace() creates a workspace-level template (201)")
    void putByWorkspace_createsTemplate() {
        assertDoesNotThrow(() -> templates.putByWorkspace(TMP_WS, WS_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(11)
    @DisplayName("[11] listByWorkspace() after put() contains the new template")
    void listByWorkspace_afterPut_containsTemplate() {
        List<TemplateInfo> result = templates.listByWorkspace(TMP_WS);

        assertTrue(
                result.stream().anyMatch(t -> WS_TMPL.equals(t.getName())),
                "listByWorkspace must contain " + WS_TMPL + " after put");
    }

    @Test @Order(12)
    @DisplayName("[12] getByWorkspace() returns the FTL content")
    void getByWorkspace_returnsContent() {
        String content = templates.getByWorkspace(TMP_WS, WS_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V1, content.trim(), "workspace template content must match");
    }

    @Test @Order(13)
    @DisplayName("[13] deleteByWorkspace() removes the workspace-level template (200)")
    void deleteByWorkspace_removesTemplate() {
        assertDoesNotThrow(() -> templates.deleteByWorkspace(TMP_WS, WS_TMPL));
    }

    // ── DataStore level ───────────────────────────────────────────────────

    @Test @Order(14)
    @DisplayName("[14] putByDatastore() creates a datastore-level template (201)")
    void putByDatastore_createsTemplate() {
        assertDoesNotThrow(() -> templates.putByDatastore(TMP_WS, TMP_DS, DS_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(15)
    @DisplayName("[15] getByDatastore() returns the FTL content")
    void getByDatastore_returnsContent() {
        String content = templates.getByDatastore(TMP_WS, TMP_DS, DS_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V1, content.trim(), "datastore template content must match");
    }

    @Test @Order(16)
    @DisplayName("[16] deleteByDatastore() removes the datastore-level template (200)")
    void deleteByDatastore_removesTemplate() {
        assertDoesNotThrow(() -> templates.deleteByDatastore(TMP_WS, TMP_DS, DS_TMPL));
    }

    // ── FeatureType level ─────────────────────────────────────────────────

    @Test @Order(17)
    @DisplayName("[17] putByFeatureType() creates a featuretype-level template (201)")
    void putByFeatureType_createsTemplate() {
        assertDoesNotThrow(() ->
                templates.putByFeatureType(TMP_WS, TMP_DS, TMP_FT, FT_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(18)
    @DisplayName("[18] getByFeatureType() returns the FTL content")
    void getByFeatureType_returnsContent() {
        String content = templates.getByFeatureType(TMP_WS, TMP_DS, TMP_FT, FT_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V1, content.trim(), "featuretype template content must match");
    }

    @Test @Order(19)
    @DisplayName("[19] deleteByFeatureType() removes the featuretype-level template (200)")
    void deleteByFeatureType_removesTemplate() {
        assertDoesNotThrow(() ->
                templates.deleteByFeatureType(TMP_WS, TMP_DS, TMP_FT, FT_TMPL));
    }

    // ── CoverageStore level ───────────────────────────────────────────────

    @Test @Order(20)
    @DisplayName("[20] putByCoverageStore() creates a coveragestore-level template (201)")
    void putByCoverageStore_createsTemplate() {
        assertDoesNotThrow(() ->
                templates.putByCoverageStore(TMP_WS, TMP_CS, CS_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(21)
    @DisplayName("[21] getByCoverageStore() returns the FTL content")
    void getByCoverageStore_returnsContent() {
        String content = templates.getByCoverageStore(TMP_WS, TMP_CS, CS_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V1, content.trim(), "coveragestore template content must match");
    }

    @Test @Order(22)
    @DisplayName("[22] deleteByCoverageStore() removes the coveragestore-level template (200)")
    void deleteByCoverageStore_removesTemplate() {
        assertDoesNotThrow(() ->
                templates.deleteByCoverageStore(TMP_WS, TMP_CS, CS_TMPL));
    }

    // ── Coverage level ────────────────────────────────────────────────────

    @Test @Order(23)
    @DisplayName("[23] putByCoverage() creates a coverage-level template (201)")
    void putByCoverage_createsTemplate() {
        assertDoesNotThrow(() ->
                templates.putByCoverage(TMP_WS, TMP_CS, TMP_COV, COV_TMPL, TemplateContent.of(FTL_V1)));
    }

    @Test @Order(24)
    @DisplayName("[24] getByCoverage() returns the FTL content")
    void getByCoverage_returnsContent() {
        String content = templates.getByCoverage(TMP_WS, TMP_CS, TMP_COV, COV_TMPL).getBody();

        assertNotNull(content);
        assertEquals(FTL_V1, content.trim(), "coverage template content must match");
    }

    @Test @Order(25)
    @DisplayName("[25] deleteByCoverage() removes the coverage-level template (200)")
    void deleteByCoverage_removesTemplate() {
        assertDoesNotThrow(() ->
                templates.deleteByCoverage(TMP_WS, TMP_CS, TMP_COV, COV_TMPL));
    }
}
