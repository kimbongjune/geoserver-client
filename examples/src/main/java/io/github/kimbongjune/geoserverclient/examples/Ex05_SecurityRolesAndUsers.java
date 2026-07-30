package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.api.security.RoleManager;
import io.github.kimbongjune.geoserverclient.api.security.UserGroupManager;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * The Security API: creating roles, users, and groups, and wiring them together
 * (user↔group membership, role↔user assignment).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>GeoServer enforces a default password policy — upper case, lower case, a digit, and a
 *       special character. {@code createUser(...)} will reject a password that doesn't satisfy it,
 *       so the example password below is deliberately built to pass.</li>
 *   <li>Assigning a role to a user and adding a user to a group are two separate, independent
 *       operations ({@code RoleManager.assignRoleToUser} vs. {@code UserGroupManager
 *       .assignUserToGroup}) — GeoServer's security model treats roles and groups as distinct
 *       concepts, not a hierarchy.</li>
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

        System.out.println("[1/4] Creating role '" + role + "'...");
        roles.createRole(role);
        List<String> allRoles = roles.getRoles();
        System.out.println("      -> getRoles() now contains it: " + allRoles.contains(role));

        System.out.println("[2/4] Creating user '" + user + "' and group '" + group + "'...");
        // Password must satisfy GeoServer's default policy: upper + lower + digit + special char.
        userGroups.createUser(user, "Example#Pass1", true);
        userGroups.createGroup(group);
        System.out.println("      -> both created");

        System.out.println("[3/4] Wiring them together: user->group membership, role->user assignment...");
        userGroups.assignUserToGroup(user, group);
        roles.assignRoleToUser(role, user);
        System.out.println("      -> groups for " + user + ": " + userGroups.getUserGroups(user));
        System.out.println("      -> roles for " + user + ": " + roles.getUserRoles(user));

        System.out.println("[4/4] Unwinding the assignments (order matters: undo assignments before deleting)...");
        roles.unassignRoleFromUser(role, user);
        userGroups.unassignUserFromGroup(user, group);

        System.out.println("\nCleaning up (deleting the group, user, and role)...");
        userGroups.deleteGroup(group);
        userGroups.deleteUser(user);
        roles.deleteRole(role);
        System.out.println("Done.");

        client.close();
    }
}
