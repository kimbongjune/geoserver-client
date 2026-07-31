package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.api.security.RoleManager;
import io.github.kimbongjune.geoserverclient.api.security.UserGroupManager;
import io.github.kimbongjune.geoserverclient.dto.security.SecurityUserInfo;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * The Security Roles + User/Group API in full: creating roles, users, and groups, wiring them
 * together (user↔group membership, role↔user/group assignment), reading them back every way the
 * API allows ({@code getUsers}/{@code getGroups}/{@code getGroupUsers}/{@code getGroupRoles}),
 * updating a user, and every method's "named (named) service" twin
 * ({@code *ByService(serviceName, ...)}).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>GeoServer enforces a default password policy — upper case, lower case, a digit, and a
 *       special character. {@code createUser(...)} will reject a password that doesn't satisfy it,
 *       so the example password below is deliberately built to pass.</li>
 *   <li>Assigning a role to a user and adding a user to a group are two separate, independent
 *       operations ({@code RoleManager.assignRoleToUser} vs. {@code UserGroupManager
 *       .assignUserToGroup}) — GeoServer's security model treats roles and groups as distinct
 *       concepts, not a hierarchy. Roles can also be assigned directly to a <em>group</em>
 *       ({@code assignRoleToGroup}), which every member of that group then inherits.</li>
 *   <li>Every plain method here (e.g. {@code createRole}) operates on the server's <b>active</b>
 *       role/user-group service. The {@code *ByService(serviceName, ...)} twin does exactly the
 *       same thing against an explicitly named service instead. On a stock GeoServer install
 *       there's only one service, literally named {@code "default"} — so in this example the two
 *       families of calls are operating on the same underlying data, just addressed two different
 *       ways (confirmed by pointing the "ByService" calls at {@code "default"} explicitly).</li>
 * </ul>
 *
 * <h2>⚠ Before you run this</h2>
 * This mutates <b>real security configuration</b> on whatever GeoServer instance you point it at —
 * safe against the disposable Docker instance this repo's examples are built around, but think
 * twice before running it against a shared or production server.
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone, no arguments or
 * external files needed.
 */
public class Ex05_SecurityRolesAndUsers {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex05: Security — Roles, Users, Groups ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        RoleManager roles = client.roles();
        UserGroupManager userGroups = client.userGroups();

        String role = "ROLE_EXAMPLE";
        String user = "example_user";
        String group = "example_group";

        System.out.println("[1/5] Creating role '" + role + "'...");
        roles.createRole(role);
        List<String> allRoles = roles.getRoles();
        System.out.println("      -> getRoles() now contains it: " + allRoles.contains(role));

        System.out.println("[2/5] Creating user '" + user + "' and group '" + group + "'...");
        // Password must satisfy GeoServer's default policy: upper + lower + digit + special char.
        userGroups.createUser(user, "Example#Pass1", true);
        userGroups.createGroup(group);
        System.out.println("      -> getUsers() contains it: " + containsUser(userGroups.getUsers(), user));
        System.out.println("      -> getGroups() contains it: " + userGroups.getGroups().contains(group));

        System.out.println("[3/5] Wiring them together: user->group, role->user, role->group...");
        userGroups.assignUserToGroup(user, group);
        roles.assignRoleToUser(role, user);
        roles.assignRoleToGroup(role, group);
        System.out.println("      -> groups for " + user + ": " + userGroups.getUserGroups(user));
        System.out.println("      -> roles for " + user + ": " + roles.getUserRoles(user));
        System.out.println("      -> roles for group '" + group + "': " + roles.getGroupRoles(group));
        System.out.println("      -> users in group '" + group + "': "
                + namesOf(userGroups.getGroupUsers(group)));

        System.out.println("[4/5] Updating the user (rotating the password only, enabled left unchanged)...");
        userGroups.updateUser(user, "NewExample#Pass2", null);
        System.out.println("      -> updateUser() completed with no error");

        System.out.println("[5/5] Unwinding the assignments (order matters: undo assignments before deleting)...");
        roles.unassignRoleFromGroup(role, group);
        roles.unassignRoleFromUser(role, user);
        userGroups.unassignUserFromGroup(user, group);
        userGroups.deleteGroup(group);
        userGroups.deleteUser(user);
        roles.deleteRole(role);
        System.out.println("      -> cleaned up (active-service part)");

        // -----------------------------------------------------------------
        // Same operations again, via the explicitly-named-service ("default") twins
        // -----------------------------------------------------------------
        System.out.println("\n=== Repeating via the *ByService(\"default\", ...) twins ===");
        String svc = "default";
        String role2 = "ROLE_EXAMPLE_SVC";
        String user2 = "example_user_svc";
        String group2 = "example_group_svc";

        System.out.println("[1/4] createRoleByService / createUserByService / createGroupByService...");
        roles.createRoleByService(svc, role2);
        userGroups.createUserByService(svc, user2, "Example#Pass1", true);
        userGroups.createGroupByService(svc, group2);
        System.out.println("      -> getRolesByService: " + roles.getRolesByService(svc).contains(role2));
        System.out.println("      -> getUsersByService: " + containsUser(userGroups.getUsersByService(svc), user2));
        System.out.println("      -> getGroupsByService: " + userGroups.getGroupsByService(svc).contains(group2));

        System.out.println("[2/4] assignUserToGroupByService / assignRoleToUserByService / "
                + "assignRoleToGroupByService...");
        userGroups.assignUserToGroupByService(svc, user2, group2);
        roles.assignRoleToUserByService(svc, role2, user2);
        roles.assignRoleToGroupByService(svc, role2, group2);
        System.out.println("      -> getUserGroupsByService: " + userGroups.getUserGroupsByService(svc, user2));
        System.out.println("      -> getUserRolesByService: " + roles.getUserRolesByService(svc, user2));
        System.out.println("      -> getGroupRolesByService: " + roles.getGroupRolesByService(svc, group2));
        System.out.println("      -> getGroupUsersByService: "
                + namesOf(userGroups.getGroupUsersByService(svc, group2)));

        System.out.println("[3/4] updateUserByService (rotating the password)...");
        userGroups.updateUserByService(svc, user2, "NewExample#Pass2", null);
        System.out.println("      -> completed with no error");

        System.out.println("[4/4] Unwinding via the ByService unassign/delete twins...");
        roles.unassignRoleFromGroupByService(svc, role2, group2);
        roles.unassignRoleFromUserByService(svc, role2, user2);
        userGroups.unassignUserFromGroupByService(svc, user2, group2);
        userGroups.deleteGroupByService(svc, group2);
        userGroups.deleteUserByService(svc, user2);
        roles.deleteRoleByService(svc, role2);
        System.out.println("      -> cleaned up (ByService part)");

        System.out.println("\nDone.");
        client.close();
    }

    private static boolean containsUser(List<SecurityUserInfo> users, String name) {
        for (SecurityUserInfo u : users) {
            if (name.equals(u.getUserName())) return true;
        }
        return false;
    }

    private static String namesOf(List<SecurityUserInfo> users) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(users.get(i).getUserName());
        }
        return sb.append("]").toString();
    }
}
