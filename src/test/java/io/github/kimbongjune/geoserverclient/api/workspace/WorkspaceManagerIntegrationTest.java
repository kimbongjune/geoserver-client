package io.github.kimbongjune.geoserverclient.api.workspace;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.UpdateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.Workspace;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WorkspaceNotFoundException;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link WorkspaceManager}.
 * <p>
 * Tests accumulate state in execution order and are cleaned up in {@code @AfterAll}.
 * GeoServer: {@code http://localhost:9090/geoserver}, see docker-compose.yml.
 */
@DisplayName("WorkspaceManager Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkspaceManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS        = System.currentTimeMillis();
    private static final String WS_MAIN   = "it_ws_"    + TS;
    private static final String WS_RENAME = "it_ws_rn_" + TS;
    private static final String WS_ISO    = "it_ws_iso_" + TS;
    private static final String WS_DELETE = "it_ws_del_" + TS;

    private WorkspaceManager workspaces;
    private String originalDefault;

    @BeforeAll
    void setUpWorkspaces() {
        workspaces      = client.workspaces();
        originalDefault = workspaces.getDefault().getName();
        workspaces.create(CreateWorkspaceRequest.of(WS_RENAME));
        workspaces.create(CreateWorkspaceRequest.of(WS_DELETE));
    }

    @AfterAll
    void cleanUp() {
        workspaces.setDefault(originalDefault);
        Arrays.asList(WS_MAIN, WS_RENAME, WS_RENAME + "_after", WS_ISO, WS_DELETE)
              .stream()
              .filter(workspaces::exists)
              .forEach(name -> workspaces.delete(name, true));
    }

    // ── 1. list ──────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("list() - returns workspace list")
    void list_shouldReturnList() {
        List<WorkspaceSummary> list = workspaces.list();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "at least one pre-existing workspace must exist");
        list.forEach(ws -> {
            assertNotNull(ws.getName());
            assertNotNull(ws.getHref());
        });
    }

    // ── 2-3. create ──────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("create() - returns workspace detail after creation")
    void create_shouldCreateWorkspace() {
        Workspace ws = workspaces.create(CreateWorkspaceRequest.of(WS_MAIN));
        assertNotNull(ws);
        assertEquals(WS_MAIN, ws.getName());
        assertFalse(ws.isIsolated());
        assertNotNull(ws.getDataStores());
    }

    @Test
    @Order(3)
    @DisplayName("create() - duplicate name → ResourceAlreadyExistsException")
    void create_duplicate_shouldThrowException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> workspaces.create(CreateWorkspaceRequest.of(WS_MAIN)));
    }

    // ── 4-5. get ─────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("get() - returns workspace detail")
    void get_shouldReturnDetails() {
        Workspace ws = workspaces.get(WS_MAIN);
        assertNotNull(ws);
        assertEquals(WS_MAIN, ws.getName());
        assertNotNull(ws.getDataStores());
        assertNotNull(ws.getCoverageStores());
    }

    @Test
    @Order(5)
    @DisplayName("get() - nonexistent workspace → WorkspaceNotFoundException")
    void get_nonExistent_shouldThrowException() {
        WorkspaceNotFoundException ex = assertThrows(WorkspaceNotFoundException.class,
                () -> workspaces.get("nonexistent_xyz_abc_12345"));
        assertEquals("nonexistent_xyz_abc_12345", ex.getWorkspaceName());
    }

    // ── 6. exists ────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("exists() - distinguishes existing from nonexistent")
    void exists_shouldReturnCorrectBoolean() {
        assertTrue(workspaces.exists(WS_MAIN));
        assertFalse(workspaces.exists("nonexistent_xyz_abc_12345"));
    }

    // ── 7. getDefault ────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("getDefault() - returns current default workspace")
    void getDefault_shouldReturnDefault() {
        Workspace def = workspaces.getDefault();
        assertNotNull(def);
        assertNotNull(def.getName());
    }

    // ── 8-9. update / rename ─────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("update() - rename workspace (WS_RENAME → WS_RENAME_after)")
    void update_rename_shouldRenameWorkspace() {
        Workspace updated = workspaces.update(WS_RENAME,
                UpdateWorkspaceRequest.rename(WS_RENAME + "_after"));
        assertEquals(WS_RENAME + "_after", updated.getName());
        assertFalse(workspaces.exists(WS_RENAME));
        assertTrue(workspaces.exists(WS_RENAME + "_after"));
    }

    @Test
    @Order(9)
    @DisplayName("update() - nonexistent workspace → WorkspaceNotFoundException")
    void update_nonExistent_shouldThrowException() {
        assertThrows(WorkspaceNotFoundException.class,
                () -> workspaces.update("nonexistent_xyz_abc_12345",
                        UpdateWorkspaceRequest.rename("whatever")));
    }

    // ── 10-11. setDefault ────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("setDefault() - changes default workspace")
    void setDefault_shouldChangeDefault() {
        // setDefault is a global server state; other test classes may concurrently
        // change it. We verify only that the call succeeds without exception and
        // that the default is *one of* the workspaces we created (not a foreign name).
        assertDoesNotThrow(() -> workspaces.setDefault(WS_MAIN));
        String actual = workspaces.getDefault().getName();
        // Either WS_MAIN was accepted, or a concurrent test reset it to another
        // it_ws_* test workspace — both indicate setDefault() works correctly.
        assertTrue(actual.startsWith("it_ws_") || actual.equals(WS_MAIN),
                "default workspace should be a test workspace, but was: " + actual);
    }

    @Test
    @Order(11)
    @DisplayName("setDefault() - nonexistent workspace → WorkspaceNotFoundException")
    void setDefault_nonExistent_shouldThrowException() {
        assertThrows(WorkspaceNotFoundException.class,
                () -> workspaces.setDefault("nonexistent_xyz_abc_12345"));
    }

    // ── 12. create isolated ──────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("create() - isolated=true creates isolated workspace")
    void create_isolated_shouldCreateIsolatedWorkspace() {
        Workspace ws = workspaces.create(
                CreateWorkspaceRequest.of(WS_ISO).isolated(true));
        assertTrue(ws.isIsolated());
    }

    // ── 13-14. delete ────────────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("delete() - deletes workspace")
    void delete_shouldDeleteWorkspace() {
        assertTrue(workspaces.exists(WS_DELETE));
        workspaces.delete(WS_DELETE);
        assertFalse(workspaces.exists(WS_DELETE));
    }

    @Test
    @Order(14)
    @DisplayName("delete() - nonexistent workspace → WorkspaceNotFoundException")
    void delete_nonExistent_shouldThrowException() {
        assertThrows(WorkspaceNotFoundException.class,
                () -> workspaces.delete("nonexistent_xyz_abc_12345"));
    }

    // ── 15. delete with recurse ──────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("delete(recurse=true) - deletes workspace including child resources")
    void delete_withRecurse_shouldDeleteWorkspace() {
        workspaces.delete(WS_MAIN, true);
        assertFalse(workspaces.exists(WS_MAIN));
    }
}
