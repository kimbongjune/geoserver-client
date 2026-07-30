package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] RoleManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS         = System.currentTimeMillis();
    private static final String ROLE_NAME  = "INTEG_ROLE_" + TS;
    private static final String ROLE_SVC   = "INTEG_SVC_ROLE_" + TS;
    private static final String ROLE_GROUP = "INTEG_RG_" + TS;
    private static final String ROLE_SVC2  = "INTEG_SVC2_" + TS;

    private RoleManager roles;

    @BeforeAll
    void setUp() {
        roles = client.roles();
        // Pre-cleanup leftover test data from prior run
        try { roles.unassignRoleFromGroup(ROLE_NAME, ROLE_GROUP); } catch (Exception ignored) {}
        try { roles.unassignRoleFromGroupByService("default", ROLE_SVC2, ROLE_GROUP); } catch (Exception ignored) {}
        try { roles.deleteRoleByService("default", ROLE_SVC2); } catch (Exception ignored) {}
        try { client.userGroups().deleteGroup(ROLE_GROUP); } catch (Exception ignored) {}
        try { roles.deleteRole(ROLE_NAME); } catch (Exception ignored) {}
        try { roles.deleteRoleByService("default", ROLE_SVC); } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { roles.unassignRoleFromGroupByService("default", ROLE_SVC2, ROLE_GROUP); } catch (Exception ignored) {}
        try { roles.deleteRoleByService("default", ROLE_SVC2); } catch (Exception ignored) {}
        try { client.userGroups().deleteGroup(ROLE_GROUP); } catch (Exception ignored) {}
        try { roles.deleteRole(ROLE_NAME); } catch (Exception ignored) {}
        try { roles.deleteRoleByService("default", ROLE_SVC); } catch (Exception ignored) {}
    }

    // ── [1] getRoles ──────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] getRoles() returns non-empty list containing ADMIN role")
    void getRoles_returnsNonEmpty() {
        List<String> list = roles.getRoles();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "roles list must not be empty");
        assertTrue(list.contains("ADMIN"), "roles list must contain 'ADMIN'");
    }

    // ── [2-3] createRole / getRoles ───────────────────────────────────────

    @Test @Order(2)
    @DisplayName("[2] createRole() creates a new role")
    void createRole_creates() {
        assertDoesNotThrow(() -> roles.createRole(ROLE_NAME));
    }

    @Test @Order(3)
    @DisplayName("[3] getRoles() contains the newly created role")
    void getRoles_containsNewRole() {
        List<String> list = roles.getRoles();
        assertTrue(list.contains(ROLE_NAME), "roles list must contain newly created role");
    }

    // ── [4-5] getUserRoles / getGroupRoles ────────────────────────────────

    @Test @Order(4)
    @DisplayName("[4] getUserRoles(admin) returns non-empty list")
    void getUserRoles_admin_returnsNonEmpty() {
        List<String> list = roles.getUserRoles("admin");
        assertNotNull(list);
        assertFalse(list.isEmpty(), "admin must have at least one role");
    }

    @Test @Order(5)
    @DisplayName("[5] getGroupRoles(nonexistent group) returns empty list without throwing")
    void getGroupRoles_nonexistent_returnsEmpty() {
        List<String> list = roles.getGroupRoles("nonexistent_group_" + TS);
        assertNotNull(list);
        assertTrue(list.isEmpty(), "nonexistent group must have empty roles list");
    }

    // ── [6-9] assignRoleToUser / unassignRoleFromUser ─────────────────────

    @Test @Order(6)
    @DisplayName("[6] assignRoleToUser() assigns role to admin")
    void assignRoleToUser_assignsRole() {
        assertDoesNotThrow(() -> roles.assignRoleToUser(ROLE_NAME, "admin"));
    }

    @Test @Order(7)
    @DisplayName("[7] getUserRoles(admin) contains the assigned role")
    void getUserRoles_admin_containsAssignedRole() {
        List<String> list = roles.getUserRoles("admin");
        assertTrue(list.contains(ROLE_NAME), "admin roles must contain the assigned test role");
    }

    @Test @Order(8)
    @DisplayName("[8] unassignRoleFromUser() removes role from admin")
    void unassignRoleFromUser_removesRole() {
        assertDoesNotThrow(() -> roles.unassignRoleFromUser(ROLE_NAME, "admin"));
    }

    @Test @Order(9)
    @DisplayName("[9] getUserRoles(admin) no longer contains the unassigned role")
    void getUserRoles_admin_notContainsUnassignedRole() {
        List<String> list = roles.getUserRoles("admin");
        assertFalse(list.contains(ROLE_NAME), "admin roles must not contain the unassigned role");
    }

    // ── [10-13] Service-specific Role API ────────────────────────────────

    @Test @Order(10)
    @DisplayName("[10] getRolesByService(default) returns non-null list")
    void getRolesByService_default_returnsNonNull() {
        List<String> list = roles.getRolesByService("default");
        assertNotNull(list);
    }

    @Test @Order(11)
    @DisplayName("[11] createRoleByService(default) creates role in service")
    void createRoleByService_creates() {
        assertDoesNotThrow(() -> roles.createRoleByService("default", ROLE_SVC));
    }

    @Test @Order(12)
    @DisplayName("[12] getRolesByService(default) contains new service role")
    void getRolesByService_containsNewRole() {
        List<String> list = roles.getRolesByService("default");
        assertTrue(list.contains(ROLE_SVC), "service roles must contain newly created service role");
    }

    @Test @Order(13)
    @DisplayName("[13] deleteRoleByService(default) deletes role from service")
    void deleteRoleByService_deletes() {
        assertDoesNotThrow(() -> roles.deleteRoleByService("default", ROLE_SVC));
        List<String> list = roles.getRolesByService("default");
        assertFalse(list.contains(ROLE_SVC), "service roles must not contain deleted role");
    }

    // ── [14] deleteRole ───────────────────────────────────────────────────

    @Test @Order(14)
    @DisplayName("[14] deleteRole() deletes the test role")
    void deleteRole_deletes() {
        assertDoesNotThrow(() -> roles.deleteRole(ROLE_NAME));
        List<String> list = roles.getRoles();
        assertFalse(list.contains(ROLE_NAME), "roles list must not contain deleted role");
    }

    // ── [15] Re-create ROLE_NAME + create ROLE_GROUP for group tests ──────

    @Test @Order(15)
    @DisplayName("[15] setup: re-create ROLE_NAME and create ROLE_GROUP for group tests")
    void setup_roleAndGroup() {
        assertDoesNotThrow(() -> roles.createRole(ROLE_NAME));
        assertDoesNotThrow(() -> client.userGroups().createGroup(ROLE_GROUP));
    }

    // ── [16-17] assignRoleToGroup / unassignRoleFromGroup ─────────────────

    @Test @Order(16)
    @DisplayName("[16] assignRoleToGroup() assigns ROLE_NAME to ROLE_GROUP")
    void assignRoleToGroup_assigns() {
        assertDoesNotThrow(() -> roles.assignRoleToGroup(ROLE_NAME, ROLE_GROUP));
        List<String> groupRoles = roles.getGroupRoles(ROLE_GROUP);
        assertTrue(groupRoles.contains(ROLE_NAME),
                "ROLE_GROUP must contain ROLE_NAME after assignment");
    }

    @Test @Order(17)
    @DisplayName("[17] unassignRoleFromGroup() removes ROLE_NAME from ROLE_GROUP")
    void unassignRoleFromGroup_removes() {
        assertDoesNotThrow(() -> roles.unassignRoleFromGroup(ROLE_NAME, ROLE_GROUP));
        List<String> groupRoles = roles.getGroupRoles(ROLE_GROUP);
        assertFalse(groupRoles.contains(ROLE_NAME),
                "ROLE_GROUP must not contain ROLE_NAME after unassignment");
    }

    // ── [18-19] getUserRolesByService / getGroupRolesByService ────────────

    @Test @Order(18)
    @DisplayName("[18] getUserRolesByService(default, admin) returns non-null list")
    void getUserRolesByService_admin_returnsNonNull() {
        List<String> list = roles.getUserRolesByService("default", "admin");
        assertNotNull(list, "getUserRolesByService must not return null");
    }

    @Test @Order(19)
    @DisplayName("[19] getGroupRolesByService(default, ROLE_GROUP) returns non-null list")
    void getGroupRolesByService_returnsNonNull() {
        List<String> list = roles.getGroupRolesByService("default", ROLE_GROUP);
        assertNotNull(list, "getGroupRolesByService must not return null");
    }

    // ── [20] Create ROLE_SVC2 for service-assign tests ────────────────────

    @Test @Order(20)
    @DisplayName("[20] setup: create ROLE_SVC2 in default service for service-assign tests")
    void setup_svc2Role() {
        assertDoesNotThrow(() -> roles.createRoleByService("default", ROLE_SVC2));
        List<String> list = roles.getRolesByService("default");
        assertTrue(list.contains(ROLE_SVC2), "service roles must contain newly created ROLE_SVC2");
    }

    // ── [21-22] assignRoleToUserByService / unassignRoleFromUserByService ──

    @Test @Order(21)
    @DisplayName("[21] assignRoleToUserByService(default, ROLE_SVC2, admin) succeeds")
    void assignRoleToUserByService_assigns() {
        assertDoesNotThrow(() -> roles.assignRoleToUserByService("default", ROLE_SVC2, "admin"));
        List<String> userRoles = roles.getUserRolesByService("default", "admin");
        assertTrue(userRoles.contains(ROLE_SVC2),
                "admin must contain ROLE_SVC2 after service assignment");
    }

    @Test @Order(22)
    @DisplayName("[22] unassignRoleFromUserByService(default, ROLE_SVC2, admin) removes role")
    void unassignRoleFromUserByService_removes() {
        assertDoesNotThrow(() -> roles.unassignRoleFromUserByService("default", ROLE_SVC2, "admin"));
        List<String> userRoles = roles.getUserRolesByService("default", "admin");
        assertFalse(userRoles.contains(ROLE_SVC2),
                "admin must not contain ROLE_SVC2 after service unassignment");
    }

    // ── [23-24] assignRoleToGroupByService / unassignRoleFromGroupByService ─

    @Test @Order(23)
    @DisplayName("[23] assignRoleToGroupByService(default, ROLE_SVC2, ROLE_GROUP) succeeds")
    void assignRoleToGroupByService_assigns() {
        assertDoesNotThrow(() -> roles.assignRoleToGroupByService("default", ROLE_SVC2, ROLE_GROUP));
        List<String> groupRoles = roles.getGroupRolesByService("default", ROLE_GROUP);
        assertTrue(groupRoles.contains(ROLE_SVC2),
                "ROLE_GROUP must contain ROLE_SVC2 after service group assignment");
    }

    @Test @Order(24)
    @DisplayName("[24] unassignRoleFromGroupByService(default, ROLE_SVC2, ROLE_GROUP) removes role")
    void unassignRoleFromGroupByService_removes() {
        assertDoesNotThrow(() -> roles.unassignRoleFromGroupByService("default", ROLE_SVC2, ROLE_GROUP));
        List<String> groupRoles = roles.getGroupRolesByService("default", ROLE_GROUP);
        assertFalse(groupRoles.contains(ROLE_SVC2),
                "ROLE_GROUP must not contain ROLE_SVC2 after service group unassignment");
    }

    // ── [25] cleanup: delete ROLE_SVC2, ROLE_GROUP, ROLE_NAME ─────────────

    @Test @Order(25)
    @DisplayName("[25] cleanup: remove ROLE_SVC2 from service, delete ROLE_GROUP and ROLE_NAME")
    void cleanup_svc2AndGroup() {
        assertDoesNotThrow(() -> roles.deleteRoleByService("default", ROLE_SVC2));
        assertDoesNotThrow(() -> client.userGroups().deleteGroup(ROLE_GROUP));
        assertDoesNotThrow(() -> roles.deleteRole(ROLE_NAME));
        assertFalse(roles.getRolesByService("default").contains(ROLE_SVC2));
        assertFalse(roles.getRoles().contains(ROLE_NAME));
    }
}
