package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.security.UserGroupServiceConfig;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] UserGroupServiceManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserGroupServiceManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS           = System.currentTimeMillis();
    private static final String SERVICE_NAME = "integ_ugsvc_" + TS;

    private UserGroupServiceManager ugServices;

    @BeforeAll
    void setUp() {
        ugServices = client.userGroupServices();
        try { ugServices.delete(SERVICE_NAME); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { ugServices.delete(SERVICE_NAME); } catch (Exception ignored) {}
    }

    // ── [1] list ─────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] list() contains the built-in 'default' service")
    void list_containsDefault() {
        assertTrue(ugServices.list().contains("default"));
    }

    // ── [2] get(default) ─────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] get(default) returns a config with the expected className")
    void get_default_returnsConfig() {
        UserGroupServiceConfig config = ugServices.get("default");
        assertEquals("default", config.getName());
        assertEquals("org.geoserver.security.xml.XMLUserGroupService", config.getClassName());
        assertEquals("users.xml", config.getExtra().get("fileName"));
    }

    // ── [3-4] create ─────────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] create() creates a new user/group service")
    void create_createsService() {
        UserGroupServiceConfig created = ugServices.create(UserGroupServiceConfig.xml(
                SERVICE_NAME, SERVICE_NAME + "_users.xml", 0, true, "digestPasswordEncoder", "default"));
        assertNotNull(created.getId());
        assertEquals(SERVICE_NAME, created.getName());
    }

    @Test @Order(4)
    @DisplayName("[4] list() contains the newly created service")
    void list_containsNewService() {
        assertTrue(ugServices.list().contains(SERVICE_NAME));
    }

    @Test @Order(5)
    @DisplayName("[5] create() with duplicate name throws ResourceAlreadyExistsException")
    void create_duplicate_throwsException() {
        assertThrows(ResourceAlreadyExistsException.class, () -> ugServices.create(
                UserGroupServiceConfig.xml("default", "users.xml", 0, true, "digestPasswordEncoder", "default")));
    }

    // ── [5] update ───────────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] update() changes checkInterval and is reflected on next get()")
    void update_updatesService() {
        ugServices.update(SERVICE_NAME, UserGroupServiceConfig.xml(
                SERVICE_NAME, SERVICE_NAME + "_users.xml", 5000, true, "digestPasswordEncoder", "default"));

        UserGroupServiceConfig updated = ugServices.get(SERVICE_NAME);
        assertEquals(5000, ((Number) updated.getExtra().get("checkInterval")).longValue());
    }

    // ── [6] delete ───────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("[7] delete() deletes the created service; list no longer contains it")
    void delete_deletesService() {
        assertDoesNotThrow(() -> ugServices.delete(SERVICE_NAME));
        assertFalse(ugServices.list().contains(SERVICE_NAME));
    }
}
