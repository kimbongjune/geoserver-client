package io.github.kimbongjune.geoserverclient.api.namespace;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.Namespace;
import io.github.kimbongjune.geoserverclient.dto.namespace.NamespaceSummary;
import io.github.kimbongjune.geoserverclient.dto.namespace.UpdateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.exception.NamespaceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link NamespaceManager}.
 * <p>
 * Tests accumulate state in execution order and are cleaned up in {@code @AfterAll}.
 * Creating a namespace also auto-creates a workspace with the same name.
 * Deleting a namespace also deletes the associated workspace.
 * GeoServer: {@code http://localhost:9090/geoserver}, see docker-compose.yml.
 */
@DisplayName("NamespaceManager Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NamespaceManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS         = System.currentTimeMillis();
    private static final String NS_MAIN    = "it_ns_"    + TS;
    private static final String NS_UPDATE  = "it_ns_upd_" + TS;
    private static final String NS_ISO     = "it_ns_iso_" + TS;
    private static final String NS_DELETE  = "it_ns_del_" + TS;

    private NamespaceManager namespaces;
    private String originalDefault;

    @BeforeAll
    void setUpNamespaces() {
        namespaces      = client.namespaces();
        originalDefault = namespaces.getDefault().getPrefix();
        namespaces.create(CreateNamespaceRequest.of(NS_UPDATE, "http://" + NS_UPDATE));
        namespaces.create(CreateNamespaceRequest.of(NS_DELETE, "http://" + NS_DELETE));
    }

    @AfterAll
    void cleanUp() {
        namespaces.setDefault(originalDefault);
        Arrays.asList(NS_MAIN, NS_UPDATE, NS_ISO, NS_DELETE)
              .stream()
              .filter(namespaces::exists)
              .forEach(namespaces::delete);
    }

    // ── 1. list ──────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("list() - returns namespace list")
    void list_shouldReturnList() {
        List<NamespaceSummary> list = namespaces.list();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "at least one pre-existing namespace must exist");
        list.forEach(ns -> {
            assertNotNull(ns.getName());
            assertNotNull(ns.getHref());
        });
    }

    // ── 2-3. create ──────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("create() - returns namespace detail after creation")
    void create_shouldCreateNamespace() {
        Namespace ns = namespaces.create(
                CreateNamespaceRequest.of(NS_MAIN, "http://" + NS_MAIN));
        assertNotNull(ns);
        assertEquals(NS_MAIN, ns.getPrefix());
        assertEquals("http://" + NS_MAIN, ns.getUri());
        assertFalse(ns.isIsolated());
    }

    @Test
    @Order(3)
    @DisplayName("create() - duplicate prefix → ResourceAlreadyExistsException")
    void create_duplicate_shouldThrowException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> namespaces.create(
                        CreateNamespaceRequest.of(NS_MAIN, "http://duplicate")));
    }

    // ── 4-5. get ─────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("get() - returns namespace detail")
    void get_shouldReturnDetails() {
        Namespace ns = namespaces.get(NS_MAIN);
        assertNotNull(ns);
        assertEquals(NS_MAIN, ns.getPrefix());
        assertNotNull(ns.getUri());
        assertNotNull(ns.getIsolated());
    }

    @Test
    @Order(5)
    @DisplayName("get() - nonexistent namespace → NamespaceNotFoundException")
    void get_nonExistent_shouldThrowException() {
        NamespaceNotFoundException ex = assertThrows(NamespaceNotFoundException.class,
                () -> namespaces.get("nonexistent_xyz_abc_12345"));
        assertEquals("nonexistent_xyz_abc_12345", ex.getPrefix());
    }

    // ── 6. exists ────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("exists() - distinguishes existing from nonexistent")
    void exists_shouldReturnCorrectBoolean() {
        assertTrue(namespaces.exists(NS_MAIN));
        assertFalse(namespaces.exists("nonexistent_xyz_abc_12345"));
    }

    // ── 7. getDefault ────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("getDefault() - returns current default namespace")
    void getDefault_shouldReturnDefault() {
        Namespace def = namespaces.getDefault();
        assertNotNull(def);
        assertNotNull(def.getPrefix());
        assertNotNull(def.getUri());
    }

    // ── 8-9. update ──────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("update() - changes URI")
    void update_uri_shouldUpdateUri() {
        String newUri = "http://" + NS_UPDATE + "-updated";
        Namespace updated = namespaces.update(NS_UPDATE,
                UpdateNamespaceRequest.builder().uri(newUri).build());
        assertEquals(NS_UPDATE, updated.getPrefix());
        assertEquals(newUri, updated.getUri());
    }

    @Test
    @Order(9)
    @DisplayName("update() - nonexistent namespace → NamespaceNotFoundException")
    void update_nonExistent_shouldThrowException() {
        assertThrows(NamespaceNotFoundException.class,
                () -> namespaces.update("nonexistent_xyz_abc_12345",
                        UpdateNamespaceRequest.builder().uri("http://whatever").build()));
    }

    // ── 10-11. setDefault ────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("setDefault() - changes default namespace")
    void setDefault_shouldChangeDefault() {
        namespaces.setDefault(NS_MAIN);
        assertEquals(NS_MAIN, namespaces.getDefault().getPrefix());
    }

    @Test
    @Order(11)
    @DisplayName("setDefault() - nonexistent namespace → NamespaceNotFoundException")
    void setDefault_nonExistent_shouldThrowException() {
        assertThrows(NamespaceNotFoundException.class,
                () -> namespaces.setDefault("nonexistent_xyz_abc_12345"));
    }

    // ── 12. create isolated ──────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("create() - isolated=true creates isolated namespace")
    void create_isolated_shouldCreateIsolatedNamespace() {
        Namespace ns = namespaces.create(
                CreateNamespaceRequest.of(NS_ISO, "http://" + NS_ISO).isolated(true));
        assertTrue(ns.isIsolated());
    }

    // ── 13-14. delete ────────────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("delete() - deletes namespace (associated workspace also removed)")
    void delete_shouldDeleteNamespace() {
        assertTrue(namespaces.exists(NS_DELETE));
        namespaces.delete(NS_DELETE);
        assertFalse(namespaces.exists(NS_DELETE));
    }

    @Test
    @Order(14)
    @DisplayName("delete() - nonexistent namespace → NamespaceNotFoundException")
    void delete_nonExistent_shouldThrowException() {
        assertThrows(NamespaceNotFoundException.class,
                () -> namespaces.delete("nonexistent_xyz_abc_12345"));
    }
}
