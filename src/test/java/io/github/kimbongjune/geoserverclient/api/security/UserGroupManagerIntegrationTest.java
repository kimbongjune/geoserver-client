package io.github.kimbongjune.geoserverclient.api.security;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.security.SecurityUserInfo;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] UserGroupManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserGroupManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS         = System.currentTimeMillis();
    private static final String USER_NAME  = "integ_user_" + TS;
    private static final String GROUP_NAME = "INTEG_GROUP_" + TS;
    private static final String PASSWORD   = "TestPass1!";
    private static final String SVC_USER   = "svc_user_" + TS;
    private static final String SVC_GROUP  = "SVC_GROUP_" + TS;

    private UserGroupManager userGroups;

    @BeforeAll
    void setUp() {
        userGroups = client.userGroups();
        // Pre-cleanup leftover test data from prior run
        try { userGroups.unassignUserFromGroupByService("default", SVC_USER, SVC_GROUP); } catch (Exception ignored) {}
        try { userGroups.deleteGroupByService("default", SVC_GROUP); } catch (Exception ignored) {}
        try { userGroups.deleteUserByService("default", SVC_USER); } catch (Exception ignored) {}
        try { userGroups.unassignUserFromGroup(USER_NAME, GROUP_NAME); } catch (Exception ignored) {}
        try { userGroups.deleteGroup(GROUP_NAME); } catch (Exception ignored) {}
        try { userGroups.deleteUser(USER_NAME);  } catch (Exception ignored) {}
    }

    @AfterAll
    void cleanUp() {
        try { userGroups.unassignUserFromGroupByService("default", SVC_USER, SVC_GROUP); } catch (Exception ignored) {}
        try { userGroups.deleteGroupByService("default", SVC_GROUP); } catch (Exception ignored) {}
        try { userGroups.deleteUserByService("default", SVC_USER); } catch (Exception ignored) {}
        try { userGroups.unassignUserFromGroup(USER_NAME, GROUP_NAME); } catch (Exception ignored) {}
        try { userGroups.deleteGroup(GROUP_NAME); } catch (Exception ignored) {}
        try { userGroups.deleteUser(USER_NAME);  } catch (Exception ignored) {}
    }

    // ── [1-2] getUsers / getGroups ────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] getUsers() returns non-empty list containing 'admin'")
    void getUsers_returnsNonEmpty() {
        List<SecurityUserInfo> users = userGroups.getUsers();
        assertNotNull(users);
        assertFalse(users.isEmpty(), "users list must not be empty");
        assertTrue(users.stream().anyMatch(u -> "admin".equals(u.getUserName())),
                "users list must contain 'admin'");
    }

    @Test @Order(2)
    @DisplayName("[2] getGroups() returns non-null list (may be empty in default GeoServer)")
    void getGroups_returnsNonNull() {
        List<String> groups = userGroups.getGroups();
        assertNotNull(groups);
    }

    // ── [3-4] createUser ─────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("[3] createUser() creates a new user")
    void createUser_creates() {
        assertDoesNotThrow(() -> userGroups.createUser(USER_NAME, PASSWORD, true));
    }

    @Test @Order(4)
    @DisplayName("[4] getUsers() contains the newly created user")
    void getUsers_containsNewUser() {
        List<SecurityUserInfo> users = userGroups.getUsers();
        assertTrue(users.stream().anyMatch(u -> USER_NAME.equals(u.getUserName())),
                "users list must contain newly created user");
    }

    // ── [5] updateUser ────────────────────────────────────────────────────

    @Test @Order(5)
    @DisplayName("[5] updateUser() updates user password without throwing")
    void updateUser_updates() {
        assertDoesNotThrow(() -> userGroups.updateUser(USER_NAME, "NewPass1!", null));
    }

    // ── [6-7] createGroup ─────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("[6] createGroup() creates a new group")
    void createGroup_creates() {
        assertDoesNotThrow(() -> userGroups.createGroup(GROUP_NAME));
    }

    @Test @Order(7)
    @DisplayName("[7] getGroups() contains the newly created group")
    void getGroups_containsNewGroup() {
        List<String> groups = userGroups.getGroups();
        assertTrue(groups.contains(GROUP_NAME), "groups list must contain newly created group");
    }

    // ── [8-12] assignUserToGroup / unassignUserFromGroup ──────────────────

    @Test @Order(8)
    @DisplayName("[8] assignUserToGroup() assigns user to group")
    void assignUserToGroup_assigns() {
        assertDoesNotThrow(() -> userGroups.assignUserToGroup(USER_NAME, GROUP_NAME));
    }

    @Test @Order(9)
    @DisplayName("[9] getGroupUsers() contains the assigned user")
    void getGroupUsers_containsAssignedUser() {
        List<SecurityUserInfo> users = userGroups.getGroupUsers(GROUP_NAME);
        assertTrue(users.stream().anyMatch(u -> USER_NAME.equals(u.getUserName())),
                "group users must contain the assigned user");
    }

    @Test @Order(10)
    @DisplayName("[10] getUserGroups() contains the group for the assigned user")
    void getUserGroups_containsGroup() {
        List<String> groups = userGroups.getUserGroups(USER_NAME);
        assertTrue(groups.contains(GROUP_NAME), "user groups must contain the assigned group");
    }

    @Test @Order(11)
    @DisplayName("[11] unassignUserFromGroup() removes user from group")
    void unassignUserFromGroup_removes() {
        assertDoesNotThrow(() -> userGroups.unassignUserFromGroup(USER_NAME, GROUP_NAME));
    }

    @Test @Order(12)
    @DisplayName("[12] getGroupUsers() no longer contains the unassigned user")
    void getGroupUsers_notContainsUnassignedUser() {
        List<SecurityUserInfo> users = userGroups.getGroupUsers(GROUP_NAME);
        assertFalse(users.stream().anyMatch(u -> USER_NAME.equals(u.getUserName())),
                "group users must not contain the unassigned user");
    }

    // ── [13-14] deleteGroup / deleteUser ──────────────────────────────────

    @Test @Order(13)
    @DisplayName("[13] deleteGroup() deletes the test group")
    void deleteGroup_deletes() {
        assertDoesNotThrow(() -> userGroups.deleteGroup(GROUP_NAME));
        List<String> groups = userGroups.getGroups();
        assertFalse(groups.contains(GROUP_NAME), "groups list must not contain deleted group");
    }

    @Test @Order(14)
    @DisplayName("[14] deleteUser() deletes the test user")
    void deleteUser_deletes() {
        assertDoesNotThrow(() -> userGroups.deleteUser(USER_NAME));
        List<SecurityUserInfo> users = userGroups.getUsers();
        assertFalse(users.stream().anyMatch(u -> USER_NAME.equals(u.getUserName())),
                "users list must not contain deleted user");
    }
    // ── [15-16] getUsersByService / getGroupsByService ──────────────────

    @Test @Order(15)
    @DisplayName("[15] getUsersByService(default) returns non-null list containing 'admin'")
    void getUsersByService_returnsAdmin() {
        List<SecurityUserInfo> users = userGroups.getUsersByService("default");
        assertNotNull(users);
        assertTrue(users.stream().anyMatch(u -> "admin".equals(u.getUserName())),
                "getUsersByService(default) must contain 'admin'");
    }

    @Test @Order(16)
    @DisplayName("[16] getGroupsByService(default) returns non-null list")
    void getGroupsByService_returnsNonNull() {
        List<String> groups = userGroups.getGroupsByService("default");
        assertNotNull(groups);
    }

    // ── [17-18] createGroupByService ──────────────────────────────────

    @Test @Order(17)
    @DisplayName("[17] createGroupByService(default, SVC_GROUP) creates group in service")
    void createGroupByService_creates() {
        assertDoesNotThrow(() -> userGroups.createGroupByService("default", SVC_GROUP));
        List<String> groups = userGroups.getGroupsByService("default");
        assertTrue(groups.contains(SVC_GROUP), "service groups must contain SVC_GROUP after create");
    }

    // ── [19-20] createUserByService ──────────────────────────────────

    @Test @Order(18)
    @DisplayName("[18] createUserByService(default, SVC_USER) creates user in service")
    void createUserByService_creates() {
        assertDoesNotThrow(() -> userGroups.createUserByService("default", SVC_USER, PASSWORD, true));
        List<SecurityUserInfo> users = userGroups.getUsersByService("default");
        assertTrue(users.stream().anyMatch(u -> SVC_USER.equals(u.getUserName())),
                "service users must contain SVC_USER after create");
    }

    // ── [19] updateUserByService ─────────────────────────────────────

    @Test @Order(19)
    @DisplayName("[19] updateUserByService(default, SVC_USER) updates password without throwing")
    void updateUserByService_updates() {
        assertDoesNotThrow(() -> userGroups.updateUserByService("default", SVC_USER, "NewPass1!", null));
    }

    // ── [20-23] assignUserToGroupByService / unassignUserFromGroupByService ─

    @Test @Order(20)
    @DisplayName("[20] assignUserToGroupByService(default, SVC_USER, SVC_GROUP) assigns user")
    void assignUserToGroupByService_assigns() {
        assertDoesNotThrow(() -> userGroups.assignUserToGroupByService("default", SVC_USER, SVC_GROUP));
    }

    @Test @Order(21)
    @DisplayName("[21] getGroupUsersByService(default, SVC_GROUP) contains SVC_USER")
    void getGroupUsersByService_containsUser() {
        List<SecurityUserInfo> users = userGroups.getGroupUsersByService("default", SVC_GROUP);
        assertTrue(users.stream().anyMatch(u -> SVC_USER.equals(u.getUserName())),
                "service group users must contain SVC_USER after assignment");
    }

    @Test @Order(22)
    @DisplayName("[22] getUserGroupsByService(default, SVC_USER) contains SVC_GROUP")
    void getUserGroupsByService_containsGroup() {
        List<String> groups = userGroups.getUserGroupsByService("default", SVC_USER);
        assertTrue(groups.contains(SVC_GROUP),
                "service user groups must contain SVC_GROUP after assignment");
    }

    @Test @Order(23)
    @DisplayName("[23] unassignUserFromGroupByService(default, SVC_USER, SVC_GROUP) removes user")
    void unassignUserFromGroupByService_removes() {
        assertDoesNotThrow(() -> userGroups.unassignUserFromGroupByService("default", SVC_USER, SVC_GROUP));
        List<SecurityUserInfo> users = userGroups.getGroupUsersByService("default", SVC_GROUP);
        assertFalse(users.stream().anyMatch(u -> SVC_USER.equals(u.getUserName())),
                "service group users must not contain SVC_USER after unassignment");
    }

    // ── [24-25] deleteGroupByService / deleteUserByService ──────────────

    @Test @Order(24)
    @DisplayName("[24] deleteGroupByService(default, SVC_GROUP) deletes service group")
    void deleteGroupByService_deletes() {
        assertDoesNotThrow(() -> userGroups.deleteGroupByService("default", SVC_GROUP));
        List<String> groups = userGroups.getGroupsByService("default");
        assertFalse(groups.contains(SVC_GROUP),
                "service groups must not contain SVC_GROUP after delete");
    }

    @Test @Order(25)
    @DisplayName("[25] deleteUserByService(default, SVC_USER) deletes service user")
    void deleteUserByService_deletes() {
        assertDoesNotThrow(() -> userGroups.deleteUserByService("default", SVC_USER));
        List<SecurityUserInfo> users = userGroups.getUsersByService("default");
        assertFalse(users.stream().anyMatch(u -> SVC_USER.equals(u.getUserName())),
                "service users must not contain SVC_USER after delete");
    }}
