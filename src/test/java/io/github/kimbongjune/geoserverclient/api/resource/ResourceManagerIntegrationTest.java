package io.github.kimbongjune.geoserverclient.api.resource;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceHeadInfo;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceMetadata;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] ResourceManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResourceManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS        = System.currentTimeMillis();
    private static final String TEST_DIR  = "temp/copilot-res-" + TS;
    private static final String TEST_FILE = TEST_DIR + "/hello.txt";
    private static final String COPY_FILE = TEST_DIR + "/hello-copy.txt";
    private static final String MOVE_FILE = TEST_DIR + "/hello-moved.txt";

    private ResourceManager resources;

    @BeforeAll
    void setUp() {
        resources = client.resources();
    }

    @AfterAll
    void cleanUp() {
        // Best-effort cleanup: delete the test directory tree
        try { resources.delete(TEST_FILE);  } catch (Exception ignored) {}
        try { resources.delete(COPY_FILE);  } catch (Exception ignored) {}
        try { resources.delete(MOVE_FILE);  } catch (Exception ignored) {}
        try { resources.delete(TEST_DIR);   } catch (Exception ignored) {}
    }

    // ── [1] listRoot() ────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] listRoot() returns non-empty Data Directory listing")
    void listRoot_returnsNonEmpty() {
        List<ResourceChild> children = resources.listRoot();

        assertNotNull(children, "listing must not be null");
        assertFalse(children.isEmpty(), "root must contain files/directories");
        assertTrue(children.stream().anyMatch(c -> "global.xml".equals(c.getName())),
                "root must contain global.xml");
    }

    // ── [2] list(path) ────────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] list(\"styles\") returns non-empty directory listing")
    void list_stylesDir_returnsNonEmpty() {
        List<ResourceChild> children = resources.list("styles");

        assertNotNull(children, "listing must not be null");
        assertFalse(children.isEmpty(), "styles directory must not be empty");
        for (ResourceChild child : children) {
            assertNotNull(child.getName(), "each child must have a name");
        }
    }

    // ── [3] getFileContent() ─────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] getFileContent(\"logging.xml\") returns valid XML with <logging> tag")
    void getFileContent_loggingXml_returnsXml() {
        String content = resources.getFileContent("logging.xml");

        assertNotNull(content, "content must not be null");
        assertTrue(content.contains("<logging>"),
                "logging.xml must contain <logging> tag");
    }

    // ── [4] getMetadata() ────────────────────────────────────────────────

    @Test @Order(4)
    @DisplayName("[4] getMetadata(\"styles\") returns directory metadata")
    void getMetadata_stylesDir_returnsDirectoryType() {
        ResourceMetadata meta = resources.getMetadata("styles");

        assertNotNull(meta, "metadata must not be null");
        assertEquals("directory", meta.getType(), "type must be 'directory'");
        assertEquals("styles", meta.getName(), "name must be 'styles'");
    }

    // ── [5] exists() — existing resource ─────────────────────────────────

    @Test @Order(5)
    @DisplayName("[5] exists(\"global.xml\") returns true")
    void exists_globalXml_returnsTrue() {
        assertTrue(resources.exists("global.xml"), "global.xml must exist");
    }

    // ── [6] exists() — non-existing resource ─────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] exists(\"nonexistent-file-copilot-99999.txt\") returns false")
    void exists_nonExistent_returnsFalse() {
        assertFalse(resources.exists("nonexistent-file-copilot-99999.txt"),
                "non-existent resource must return false");
    }

    // ── [7] createOrUpdate() ─────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("[7] createOrUpdate() creates a new file (201)")
    void createOrUpdate_createsFile() {
        // Should complete without exception (201 is success)
        assertDoesNotThrow(() -> resources.createOrUpdate(TEST_FILE, "hello world", "text/plain"));
    }

    // ── [8] getFileContent() — after create ──────────────────────────────

    @Test @Order(8)
    @DisplayName("[8] getFileContent() returns the created content")
    void getFileContent_afterCreate_returnsContent() {
        String content = resources.getFileContent(TEST_FILE);

        assertEquals("hello world", content, "content must match what was created");
    }

    // ── [9] exists() — after create ──────────────────────────────────────

    @Test @Order(9)
    @DisplayName("[9] exists() returns true after createOrUpdate")
    void exists_afterCreate_returnsTrue() {
        assertTrue(resources.exists(TEST_FILE), "file must exist after createOrUpdate");
    }

    // ── [10] copy() ──────────────────────────────────────────────────────

    @Test @Order(10)
    @DisplayName("[10] copy() copies file to new path (201)")
    void copy_copiesFile() {
        assertDoesNotThrow(() -> resources.copy(COPY_FILE, TEST_FILE));
    }

    // ── [11] getFileContent() — after copy ───────────────────────────────

    @Test @Order(11)
    @DisplayName("[11] getFileContent() of copied file returns same content")
    void getFileContent_afterCopy_returnsSameContent() {
        String content = resources.getFileContent(COPY_FILE);

        assertEquals("hello world", content, "copied content must match original");
    }

    // ── [12] move() ──────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("[12] move() moves file to new path (201)")
    void move_movesFile() {
        assertDoesNotThrow(() -> resources.move(MOVE_FILE, COPY_FILE));
    }

    // ── [13] exists() — source after move ────────────────────────────────

    @Test @Order(13)
    @DisplayName("[13] exists() returns false for source path after move")
    void exists_sourceAfterMove_returnsFalse() {
        assertFalse(resources.exists(COPY_FILE), "source must not exist after move");
    }

    // ── [14] exists() — destination after move ───────────────────────────

    @Test @Order(14)
    @DisplayName("[14] exists() returns true for destination path after move")
    void exists_destAfterMove_returnsTrue() {
        assertTrue(resources.exists(MOVE_FILE), "destination must exist after move");
    }

    // ── [15] delete() — TEST_FILE ─────────────────────────────────────────

    @Test @Order(15)
    @DisplayName("[15] delete() removes the test file (200)")
    void delete_testFile_succeeds() {
        assertDoesNotThrow(() -> resources.delete(TEST_FILE));
        assertFalse(resources.exists(TEST_FILE), "file must not exist after delete");
    }

    // ── [16] delete() — MOVE_FILE ────────────────────────────────────────

    @Test @Order(16)
    @DisplayName("[16] delete() removes the moved file (200)")
    void delete_movedFile_succeeds() {
        assertDoesNotThrow(() -> resources.delete(MOVE_FILE));
        assertFalse(resources.exists(MOVE_FILE), "moved file must not exist after delete");
    }

    // ── [17] delete() — non-existent → 404 ───────────────────────────────

    @Test @Order(17)
    @DisplayName("[17] delete() on non-existent path throws ResourceNotFoundException (404)")
    void delete_nonExistent_throwsNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> resources.delete("nonexistent-file-copilot-99999.txt"),
                "delete of non-existent resource must throw ResourceNotFoundException");
    }

    // ── [18] headMetadata() — existing resource ──────────────────────────

    @Test @Order(18)
    @DisplayName("[18] headMetadata(\"global.xml\") returns resource type, content type, and file name")
    void headMetadata_globalXml_returnsInfo() {
        ResourceHeadInfo info = resources.headMetadata("global.xml");

        assertNotNull(info, "headMetadata() must not be null for an existing resource");
        assertEquals("resource", info.getResourceType(), "global.xml is a file, not a directory");
        assertNotNull(info.getLastModified(), "Last-Modified header must be present");
        assertEquals("application/xml", info.getContentType());
        assertEquals("global.xml", info.getFileName());
    }

    // ── [19] headMetadata() — directory resource ─────────────────────────

    @Test @Order(19)
    @DisplayName("[19] headMetadata(\"styles\") returns directory resource type")
    void headMetadata_stylesDir_returnsDirectoryType() {
        ResourceHeadInfo info = resources.headMetadata("styles");

        assertNotNull(info, "headMetadata() must not be null for an existing directory");
        assertEquals("directory", info.getResourceType());
    }

    // ── [20] headMetadata() — non-existing resource ──────────────────────

    @Test @Order(20)
    @DisplayName("[20] headMetadata(\"nonexistent-file-copilot-99999.txt\") returns null")
    void headMetadata_nonExistent_returnsNull() {
        assertNull(resources.headMetadata("nonexistent-file-copilot-99999.txt"),
                "headMetadata() of a non-existent resource must return null (404)");
    }
}
