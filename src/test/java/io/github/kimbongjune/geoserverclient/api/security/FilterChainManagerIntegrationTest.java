package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.security.FilterChainEntry;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] FilterChainManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilterChainManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS          = System.currentTimeMillis();
    private static final String CHAIN_NAME  = "integ_chain_" + TS;
    private static final String CHAIN_CLASS = "org.geoserver.security.ServiceLoginFilterChain";

    private FilterChainManager filterChains;

    @BeforeAll
    void setUp() {
        filterChains = client.filterChains();
        try { filterChains.delete(CHAIN_NAME); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { filterChains.delete(CHAIN_NAME); } catch (Exception ignored) {}
    }

    // ── [1] list ─────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() contains built-in 'web' and 'rest' chains")
    void list_containsBuiltInChains() {
        java.util.List<FilterChainEntry> chains = filterChains.list();
        assertTrue(chains.stream().anyMatch(c -> "web".equals(c.getName())));
        assertTrue(chains.stream().anyMatch(c -> "rest".equals(c.getName())));
    }

    // ── [2] get(rest) ─────────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] get(rest) returns a chain with filters")
    void get_rest_returnsConfig() {
        FilterChainEntry rest = filterChains.get("rest");
        assertEquals("rest", rest.getName());
        assertNotNull(rest.getFilters());
        assertFalse(rest.getFilters().isEmpty());
    }

    // ── [3-4] create ─────────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] create() creates a new filter chain")
    void create_createsChain() {
        FilterChainEntry entry = new FilterChainEntry(CHAIN_NAME, CHAIN_CLASS,
                "/integtest_" + TS + "/**", Arrays.asList("basic", "anonymous"));
        FilterChainEntry created = filterChains.create(entry);
        assertEquals(CHAIN_NAME, created.getName());
        assertEquals(CHAIN_CLASS, created.getClazz());
    }

    @Test @Order(4)
    @DisplayName("[4] get(CHAIN_NAME) returns the created chain")
    void get_createdChain_returnsConfig() {
        FilterChainEntry chain = filterChains.get(CHAIN_NAME);
        assertEquals(CHAIN_NAME, chain.getName());
        assertEquals(Arrays.asList("basic", "anonymous"), chain.getFilters());
    }

    @Test @Order(5)
    @DisplayName("[5] create() with duplicate name throws ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        FilterChainEntry dup = new FilterChainEntry("rest", CHAIN_CLASS, "/x/**", Arrays.asList("basic"));
        assertThrows(ResourceAlreadyExistsException.class, () -> filterChains.create(dup));
    }

    // ── [5] update ───────────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] update() changes the path and is reflected on next get()")
    void update_updatesChain() {
        FilterChainEntry entry = new FilterChainEntry(CHAIN_NAME, CHAIN_CLASS,
                "/integtest_v2_" + TS + "/**", Arrays.asList("basic"));
        filterChains.update(CHAIN_NAME, entry);

        FilterChainEntry updated = filterChains.get(CHAIN_NAME);
        assertEquals("/integtest_v2_" + TS + "/**", updated.getPath());
    }

    // ── [6] delete ───────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("[7] delete() deletes the created filter chain")
    void delete_deletesChain() {
        assertDoesNotThrow(() -> filterChains.delete(CHAIN_NAME));
    }

    // ── [7] list after delete ─────────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("[8] list() no longer contains deleted chain after deletion")
    void list_notContainsDeletedChain() {
        assertFalse(filterChains.list().stream().anyMatch(c -> CHAIN_NAME.equals(c.getName())));
    }
}
