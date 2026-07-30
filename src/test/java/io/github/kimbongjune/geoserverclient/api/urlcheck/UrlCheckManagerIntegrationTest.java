package io.github.kimbongjune.geoserverclient.api.urlcheck;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheck;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckRequest;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckSummary;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.UrlCheckNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link UrlCheckManager}.
 *
 * <p>Verified against: GeoServer 2.28.2 / Docker http://localhost:9090/geoserver
 *
 * <p>Endpoints:
 * <pre>
 * [1] GET    /rest/urlchecks                   list
 * [2] POST   /rest/urlchecks                   create
 * [3] GET    /rest/urlchecks/{name}            get single
 * [4] PUT    /rest/urlchecks/{name}            update
 * [5] DELETE /rest/urlchecks/{name}            delete
 *
 * Error cases:
 *   GET/PUT/DELETE nonexistent name → 404 → UrlCheckNotFoundException
 *   POST duplicate name             → 409 → ResourceAlreadyExistsException
 * </pre>
 */
@DisplayName("[IntegrationTest] UrlCheckManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UrlCheckManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS          = System.currentTimeMillis();
    private static final String CHECK_NAME  = "it_check_" + TS;
    private static final String CHECK_REGEX = "^https?://[a-z]+\\.example\\.com/.*$";
    private static final String CHECK_DESC  = "Integration test URL check";

    private UrlCheckManager urlChecks;

    @BeforeAll
    void setUp() {
        urlChecks = client.urlChecks();
    }

    @AfterAll
    void cleanUp() {
        try {
            if (urlChecks.exists(CHECK_NAME)) {
                urlChecks.delete(CHECK_NAME);
            }
        } catch (Exception ignored) {}
    }

    // [1] list

    @Test
    @Order(1)
    @DisplayName("[1] list() - returns list (empty or with items)")
    void list_shouldReturnList() {
        List<UrlCheckSummary> list = urlChecks.list();
        assertNotNull(list, "list() must not return null");
        list.forEach(item -> {
            assertNotNull(item.getName(), "name must not be null");
            assertNotNull(item.getHref(), "href must not be null");
        });
    }

    // [2] create

    @Test
    @Order(2)
    @DisplayName("[2] create() - creates and returns detail")
    void create_shouldCreateAndReturn() {
        UrlCheck check = urlChecks.create(
                new UrlCheckRequest(CHECK_NAME, CHECK_DESC, CHECK_REGEX, true));
        assertNotNull(check, "create() must return the created UrlCheck");
        assertEquals(CHECK_NAME,  check.getName());
        assertEquals(CHECK_REGEX, check.getRegex());
        assertTrue(check.isEnabled());
    }

    @Test
    @Order(3)
    @DisplayName("[2] create() - duplicate name → ResourceAlreadyExistsException (409)")
    void create_duplicate_shouldThrow409() {
        assertThrows(ResourceAlreadyExistsException.class,
                () -> urlChecks.create(
                        new UrlCheckRequest(CHECK_NAME, "dup", CHECK_REGEX, true)));
    }

    // [3] get

    @Test
    @Order(4)
    @DisplayName("[3] get() - verifies detail fields")
    void get_shouldReturnDetails() {
        UrlCheck check = urlChecks.get(CHECK_NAME);
        assertNotNull(check);
        assertEquals(CHECK_NAME,  check.getName());
        assertEquals(CHECK_REGEX, check.getRegex());
        assertEquals(CHECK_DESC,  check.getDescription());
        assertTrue(check.isEnabled());
    }

    @Test
    @Order(5)
    @DisplayName("[3] get() - nonexistent name → UrlCheckNotFoundException (404)")
    void get_nonExistent_shouldThrow404() {
        UrlCheckNotFoundException ex = assertThrows(UrlCheckNotFoundException.class,
                () -> urlChecks.get("nonexistent_xyz_12345"));
        assertEquals("nonexistent_xyz_12345", ex.getCheckName());
    }

    // exists

    @Test
    @Order(6)
    @DisplayName("exists() - distinguishes existing from nonexistent")
    void exists_shouldReturnCorrectBoolean() {
        assertTrue(urlChecks.exists(CHECK_NAME));
        assertFalse(urlChecks.exists("nonexistent_xyz_12345"));
    }

    // [1] list after create

    @Test
    @Order(7)
    @DisplayName("[1] list() - created check appears in list")
    void list_afterCreate_shouldContainCheck() {
        List<UrlCheckSummary> list = urlChecks.list();
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> CHECK_NAME.equals(s.getName())),
                "Created check must appear in list");
    }

    // [4] update

    @Test
    @Order(8)
    @DisplayName("[4] update() - modifies regex and enabled flag")
    void update_shouldModifyCheck() {
        String newRegex = "^https?://.*\\.updated\\.com/.*$";
        urlChecks.update(CHECK_NAME,
                new UrlCheckRequest(CHECK_NAME, "updated description", newRegex, false));

        UrlCheck updated = urlChecks.get(CHECK_NAME);
        assertEquals(newRegex, updated.getRegex());
        assertFalse(updated.isEnabled());
        assertEquals("updated description", updated.getDescription());
    }

    @Test
    @Order(9)
    @DisplayName("[4] update() - nonexistent name → UrlCheckNotFoundException (404)")
    void update_nonExistent_shouldThrow404() {
        assertThrows(UrlCheckNotFoundException.class,
                () -> urlChecks.update("nonexistent_xyz_12345",
                        new UrlCheckRequest("nonexistent_xyz_12345", "desc", CHECK_REGEX, true)));
    }

    // [5] delete

    @Test
    @Order(10)
    @DisplayName("[5] delete() - after delete: exists()=false, get() → 404")
    void delete_shouldRemoveCheck() {
        urlChecks.delete(CHECK_NAME);
        assertFalse(urlChecks.exists(CHECK_NAME));
        assertThrows(UrlCheckNotFoundException.class, () -> urlChecks.get(CHECK_NAME));
    }

    @Test
    @Order(11)
    @DisplayName("[5] delete() - nonexistent name → UrlCheckNotFoundException (404)")
    void delete_nonExistent_shouldThrow404() {
        assertThrows(UrlCheckNotFoundException.class,
                () -> urlChecks.delete("nonexistent_xyz_12345"));
    }
}
