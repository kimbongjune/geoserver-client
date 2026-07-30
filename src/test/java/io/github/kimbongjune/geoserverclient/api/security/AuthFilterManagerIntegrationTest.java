package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.security.AuthFilterConfig;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.AuthFilterNotFoundException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] AuthFilterManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthFilterManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS          = System.currentTimeMillis();
    private static final String FILTER_NAME = "integ_filter_" + TS;

    private AuthFilterManager authFilters;

    @BeforeAll
    void setUp() {
        authFilters = client.authFilters();
        try { authFilters.delete(FILTER_NAME); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { authFilters.delete(FILTER_NAME); } catch (Exception ignored) {}
    }

    // ── [1] list ─────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() contains built-in filters")
    void list_containsBuiltInFilters() {
        assertTrue(authFilters.list().contains("basic"));
        assertTrue(authFilters.list().contains("form"));
    }

    // ── [2] get(basic) ───────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] get(basic) returns a config with className and useRememberMe")
    void get_basic_returnsConfig() {
        AuthFilterConfig basic = authFilters.get("basic");
        assertEquals("basic", basic.getName());
        assertEquals("org.geoserver.security.filter.GeoServerBasicAuthenticationFilter", basic.getClassName());
        assertNotNull(basic.getExtra().get("useRememberMe"));
    }

    // ── [3] get(nonexistent) — GeoServer bug: 200 + {"null":""} → normalized ───

    @Test @Order(3)
    @DisplayName("[3] get(nonexistent) throws AuthFilterNotFoundException (GeoServer bug: 200 + {\"null\":\"\"} normalized)")
    void get_nonexistent_throwsNotFound() {
        assertThrows(AuthFilterNotFoundException.class, () -> authFilters.get("nonexistent_" + TS));
    }

    // ── [4] create ───────────────────────────────────────────────────────

    @Test @Order(4)
    @DisplayName("[4] create() creates a new form filter")
    void create_createsFilter() {
        AuthFilterConfig created = authFilters.create(AuthFilterConfig.form(FILTER_NAME, "username", "password"));
        assertNotNull(created.getId());
        assertEquals(FILTER_NAME, created.getName());
        assertEquals("username", created.getExtra().get("usernameParameterName"));
    }

    // ── [4b] create duplicate ──────────────────────────────────────────────

    @Test @Order(5)
    @DisplayName("[5] create() with duplicate name throws ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> authFilters.create(AuthFilterConfig.basic("basic", true)));
    }

    // ── [5] update ───────────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] update() changes usernameParameterName and is reflected on next get()")
    void update_updatesFilter() {
        authFilters.update(FILTER_NAME, AuthFilterConfig.form(FILTER_NAME, "user", "pass"));
        AuthFilterConfig updated = authFilters.get(FILTER_NAME);
        assertEquals("user", updated.getExtra().get("usernameParameterName"));
    }

    // ── [6] delete ───────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("[7] delete() removes the filter; subsequent get() throws AuthFilterNotFoundException")
    void delete_deletesFilter() {
        assertDoesNotThrow(() -> authFilters.delete(FILTER_NAME));
        assertThrows(AuthFilterNotFoundException.class, () -> authFilters.get(FILTER_NAME));
    }
}
