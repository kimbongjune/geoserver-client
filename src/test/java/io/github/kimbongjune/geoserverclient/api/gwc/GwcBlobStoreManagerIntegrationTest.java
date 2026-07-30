package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFileBlobStore;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcBlobStoreManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcBlobStoreManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS   = System.currentTimeMillis();
    private static final String NAME = "integ_blob_" + TS;

    private GwcBlobStoreManager blobStores;

    @BeforeAll
    void setUp() {
        blobStores = client.gwcBlobStores();
        try { blobStores.delete(NAME); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { blobStores.delete(NAME); } catch (Exception ignored) {}
    }

    // ── [3] PUT /gwc/rest/blobstores/{name} — create (201) ───────────────

    @Test @Order(1)
    @DisplayName("[1] upsert() creates a new FileBlobStore")
    void upsert_create_succeeds() {
        GwcFileBlobStore store = new GwcFileBlobStore(NAME, "/opt/geoserver/data_dir/" + NAME);
        store.setFileSystemBlockSize(4096);
        store.setDefault(false);
        assertDoesNotThrow(() -> blobStores.upsert(NAME, store));
    }

    // ── [1] GET /gwc/rest/blobstores ──────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] list() contains the created store")
    void list_containsCreatedStore() {
        assertTrue(blobStores.list().contains(NAME));
    }

    // ── [2] GET /gwc/rest/blobstores/{name} ──────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] get() returns matching FileBlobStore after create")
    void get_afterCreate_returnsMatchingStore() {
        GwcFileBlobStore fetched = blobStores.get(NAME);
        assertEquals(NAME, fetched.getId());
        assertEquals("/opt/geoserver/data_dir/" + NAME, fetched.getBaseDirectory());
        assertFalse(fetched.isDefault());
    }

    // ── [3] PUT /gwc/rest/blobstores/{name} — update (200) ───────────────

    @Test @Order(4)
    @DisplayName("[4] upsert() updates enabled=false and is reflected on next get()")
    void upsert_update_succeeds() {
        GwcFileBlobStore store = new GwcFileBlobStore(NAME, "/opt/geoserver/data_dir/" + NAME);
        store.setEnabled(false);
        store.setFileSystemBlockSize(4096);
        store.setDefault(false);
        assertDoesNotThrow(() -> blobStores.upsert(NAME, store));

        GwcFileBlobStore fetched = blobStores.get(NAME);
        assertEquals(Boolean.FALSE, fetched.getEnabled());
    }

    // ── [4] DELETE /gwc/rest/blobstores/{name} ───────────────────────────

    @Test @Order(5)
    @DisplayName("[5] delete() removes the FileBlobStore")
    void delete_removesStore() {
        assertDoesNotThrow(() -> blobStores.delete(NAME));
    }

    @Test @Order(6)
    @DisplayName("[6] list() no longer contains the deleted store")
    void list_afterDelete_doesNotContainStore() {
        assertFalse(blobStores.list().contains(NAME));
    }
}
