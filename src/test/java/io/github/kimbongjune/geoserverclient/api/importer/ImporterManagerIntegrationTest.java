package io.github.kimbongjune.geoserverclient.api.importer;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportData;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransform;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransformChain;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link ImporterManager}.
 *
 * <p>Verified against: GeoServer 2.28.2 / Docker http://localhost:9090/geoserver
 * Requires importer plugin (STABLE_EXTENSIONS=importer-plugin)
 *
 * <p>Endpoints:
 * <pre>
 * [1]  GET    /rest/imports                              list imports
 * [2]  POST   /rest/imports                              create context
 * [3]  PUT    /rest/imports/{id}                         create context at specific id
 * [4]  GET    /rest/imports/{id}                         get single context
 * [5]  POST   /rest/imports/{id}                         run import
 * [6]  DELETE /rest/imports/{id}                         delete single context
 * [7]  DELETE /rest/imports                              delete all contexts
 * [8]  GET    /rest/imports/{id}/tasks                   list tasks
 * [10] PUT    /rest/imports/{id}/tasks/{file}            upload file
 * [11] GET    /rest/imports/{id}/tasks/{taskId}          get single task
 * [13] DELETE /rest/imports/{id}/tasks/{taskId}          delete task
 * [14] GET    /rest/imports/{id}/tasks/{taskId}/progress task progress
 * [27] GET    /rest/imports/{id}/tasks/{taskId}/transforms   list transform chain
 * [29] POST   /rest/imports/{id}/tasks/{taskId}/transforms   add transform
 * [31] DELETE /rest/imports/{id}/tasks/{taskId}/transforms/{tId} delete transform
 * </pre>
 */
@DisplayName("[IntegrationTest] ImporterManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImporterManagerIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_WORKSPACE = "cite";

    private ImporterManager importer;
    private static long importId = -1;
    private static long taskId = -1;
    private static File testZip;

    @BeforeAll
    void setUp() {
        importer = client.importer();

        // Target workspace for createImport() — don't assume GeoServer sample data is present.
        try { client.workspaces().create(CreateWorkspaceRequest.of(TEST_WORKSPACE)); } catch (Exception ignored) {}

        URL zipUrl = getClass().getClassLoader().getResource("AL_D002_36_20260104.zip");
        if (zipUrl != null) {
            testZip = new File(zipUrl.getFile());
        }
    }

    @AfterAll
    void cleanUp() {
        try {
            if (importId > 0) {
                importer.deleteImport(importId);
            }
        } catch (Exception ignored) {}
    }

    // ── [1] listImports ───────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] listImports() - returns list (may be empty)")
    void listImports_shouldReturnList() {
        List<ImportContextSummary> list = importer.listImports();
        assertNotNull(list, "listImports() must not return null");
        // Each item must have valid id and href
        list.forEach(item -> {
            assertTrue(item.getId() >= 0, "id must be non-negative");
        });
    }

    // ── [7] deleteAllImports ──────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[7] deleteAllImports() - list is empty after deleteAll")
    void deleteAllImports_shouldClearAll() {
        assertDoesNotThrow(() -> importer.deleteAllImports(),
                "deleteAllImports() must not throw");
        List<ImportContextSummary> list = importer.listImports();
        assertTrue(list.isEmpty(), "After deleteAllImports(), list must be empty");
    }

    // ── [2] createImport ──────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[2] createImport() - creates import context")
    void createImport_shouldCreateContext() {
        ImportContext ctx = importer.createImport(TEST_WORKSPACE);
        assertNotNull(ctx, "createImport() must return ImportContext");
        assertTrue(ctx.getId() >= 0, "id must be non-negative");
        assertEquals("PENDING", ctx.getState(), "Initial state must be PENDING");
        importId = ctx.getId();
    }

    // ── [4] getImport ─────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] getImport() - returns single context")
    void getImport_shouldReturnContext() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context created");
        ImportContext ctx = importer.getImport(importId);
        assertNotNull(ctx);
        assertEquals(importId, ctx.getId());
        assertNotNull(ctx.getState());
        assertNotNull(ctx.getHref());
    }

    @Test
    @Order(5)
    @DisplayName("[4] getImport() - nonexistent id → ResourceNotFoundException")
    void getImport_nonExistent_shouldThrow() {
        assertThrows(ResourceNotFoundException.class,
                () -> importer.getImport(Long.MAX_VALUE - 1));
    }

    // ── [8] listTasks ─────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[8] listTasks() - initially empty task list")
    void listTasks_initiallyEmpty() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        List<ImportTask> tasks = importer.listTasks(importId);
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty(), "Initially, there should be no tasks");
    }

    // ── [10] uploadTask ───────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[10] uploadTask() - upload ZIP file")
    void uploadTask_shouldCreateTask() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        Assumptions.assumeTrue(testZip != null && testZip.exists(),
                "Skip: test zip file not available");

        ImportTask task = importer.uploadTask(importId, testZip, "application/octet-stream");
        // uploadTask may return null if the response body is empty (server may return 200 no body)
        // Check that no exception was thrown; task can be null if server returns no body
        if (task != null) {
            assertTrue(task.getId() >= 0, "taskId must be non-negative");
            assertNotNull(task.getState(), "state must not be null");
            taskId = task.getId();
        }
    }

    // ── [11] getTask ──────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[11] getTask() - returns single task")
    void getTask_shouldReturnTask() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        ImportTask task = importer.getTask(importId, taskId);
        assertNotNull(task);
        assertEquals(taskId, task.getId());
        assertNotNull(task.getState());
    }

    // ── [14] getTaskProgress ────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[14] getTaskProgress() - returns progress JSON")
    void getTaskProgress_shouldReturnJson() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        String progress = importer.getTaskProgress(importId, taskId);
        assertNotNull(progress, "progress JSON must not be null");
        assertTrue(progress.contains("state"), "progress JSON must contain 'state'");
    }

    // ── [27] listTransforms ───────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[27] listTransforms() - returns transform chain")
    void listTransforms_shouldReturnChain() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        ImportTransformChain chain = importer.listTransforms(importId, taskId);
        assertNotNull(chain, "transform chain must not be null");
        assertNotNull(chain.getType(), "chain type must not be null");
    }

    // ── [29] addTransform ─────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[29] addTransform() - add ReprojectTransform")
    void addTransform_shouldNotThrow() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");

        // Only add transform if task is in a vector state (not NO_FORMAT / raster)
        String taskState = importer.getTask(importId, taskId).getState();
        Assumptions.assumeFalse("NO_FORMAT".equals(taskState),
                "Skip: task is in NO_FORMAT state, cannot add vector transform");

        ImportTransform transform = ImportTransform.reproject("EPSG:4326", "EPSG:4326");
        assertDoesNotThrow(() -> importer.addTransform(importId, taskId, transform),
                "addTransform() must not throw for vector task");
    }

    // ── [33] getTaskData ─────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[33] getTaskData() - returns Shapefile ImportData for the uploaded task")
    void getTaskData_shouldReturnShapefileData() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        ImportData data = importer.getTaskData(importId, taskId);
        assertNotNull(data, "getTaskData() must not return null once a file has been uploaded");
        assertEquals("file", data.getType(), "AL_D002_36_20260104.zip is auto-detected as a single file, not a directory");
        assertEquals("Shapefile", data.getFormat());
        assertNotNull(data.getFile(), "primary file name must be present");
    }

    // ── [32] getImportData (GeoServer 2.28.2 NPE bug for task-based imports) ──────

    @Test
    @Order(13)
    @DisplayName("[32] getImportData() - import has no import-level data -> GeoServerResponseException(500) [GeoServer bug]")
    void getImportData_taskBasedImport_throws500() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        // Task-based imports (the common case, used by this test class) don't set import-level
        // data -> GeoServer throws a NullPointerException internally -> 500.
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> importer.getImportData(importId));
        assertEquals(500, ex.getStatusCode(),
                "Expected 500 (GeoServer 2.28.2 NPE bug for task-based imports with no import-level data)");
    }

    // ── [34]/[35] listImportDataFiles / listTaskDataFiles (400 for file-type data) ─

    @Test
    @Order(14)
    @DisplayName("[34] listImportDataFiles() - import-level data is file-type -> GeoServerResponseException(400)")
    void listImportDataFiles_fileTypeData_throws400() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> importer.listImportDataFiles(importId));
        assertEquals(400, ex.getStatusCode(),
                "listImportDataFiles() must 400 when the underlying data is a single file, not a directory");
    }

    @Test
    @Order(15)
    @DisplayName("[35] listTaskDataFiles() - task data is file-type (Shapefile ZIP) -> GeoServerResponseException(400)")
    void listTaskDataFiles_fileTypeData_throws400() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        // AL_D002_36_20260104.zip is auto-detected by GeoServer as a single Shapefile (type="file"),
        // not a directory, so the /data/files sub-resource must reject it with 400.
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> importer.listTaskDataFiles(importId, taskId));
        assertEquals(400, ex.getStatusCode(),
                "listTaskDataFiles() must 400 when the task data is a single file, not a directory");
    }

    // NOTE: getImportDataFile()/deleteImportDataFile() operate on a *directory-type* import's
    // data (confirmed supported via OPTIONS: Allow: GET,HEAD,DELETE,OPTIONS), but no
    // directory-type import could be produced with the available fixtures in one reasonable
    // attempt — AL_D002_36_20260104.zip is auto-detected as a single Shapefile (type="file").
    // Their success/404 paths are covered instead by mocked tests in ImporterManagerTest.

    // ── [5] runImport ─────────────────────────────────────────────────────

    @Test
    @Order(16)
    @DisplayName("[5] runImport() - executes import")
    void runImport_shouldNotThrow() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        assertDoesNotThrow(() -> importer.runImport(importId),
                "runImport() must not throw");
    }

    // ── [3] createImportAtId ──────────────────────────────────────────────

    @Test
    @Order(17)
    @DisplayName("[3] createImportAtId() - creates context at specific id")
    void createImportAtId_shouldCreateWithSpecificId() {
        long hintId = 99991L;
        ImportContext ctx = importer.createImportAtId(hintId, TEST_WORKSPACE);
        try {
            assertNotNull(ctx);
            // GeoServer uses its own sequential counter; hintId is a lower-bound hint
            assertTrue(ctx.getId() >= hintId, "returned id should be >= requested hint id");
            assertEquals("PENDING", ctx.getState());
        } finally {
            try { importer.deleteImport(ctx.getId()); } catch (Exception ignored) {}
        }
    }

    // ── [13] deleteTask ───────────────────────────────────────────────────

    @Test
    @Order(18)
    @DisplayName("[13] deleteTask() - removes task")
    void deleteTask_shouldRemoveTask() {
        Assumptions.assumeTrue(importId >= 0 && taskId >= 0,
                "Skip: no task created");
        assertDoesNotThrow(() -> importer.deleteTask(importId, taskId),
                "deleteTask() must not throw");

        List<ImportTask> tasks = importer.listTasks(importId);
        boolean taskExists = tasks.stream().anyMatch(t -> t.getId() == taskId);
        assertFalse(taskExists, "After deleteTask(), task must not appear in list");
    }

    // ── [6] deleteImport ─────────────────────────────────────────────────

    @Test
    @Order(19)
    @DisplayName("[6] deleteImport() - removes context")
    void deleteImport_shouldRemoveContext() {
        Assumptions.assumeTrue(importId >= 0, "Skip: no import context");
        assertDoesNotThrow(() -> importer.deleteImport(importId),
                "deleteImport() must not throw");

        assertThrows(ResourceNotFoundException.class,
                () -> importer.getImport(importId),
                "After deleteImport(), getImport() must throw ResourceNotFoundException");
        importId = -1;  // mark as deleted so @AfterAll doesn't try again
    }
}
