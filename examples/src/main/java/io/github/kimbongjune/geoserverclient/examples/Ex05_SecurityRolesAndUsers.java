package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.api.security.RoleManager;
import io.github.kimbongjune.geoserverclient.api.security.UserGroupManager;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * Security API: managing roles, users, and groups for the active security realm.
 * These mutate real GeoServer security config — safe to run against a disposable/dev instance,
 * but think twice before running against a shared or production server.
 */
public class Ex05_SecurityRolesAndUsers {

    public static void main(String[] args) throws Exception {
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

        roles.createRole(role);
        List<String> allRoles = roles.getRoles();
        System.out.println("Roles now include " + role + ": " + allRoles.contains(role));

        // Passwords must satisfy GeoServer's default policy: upper + lower + digit + special char
        userGroups.createUser(user, "Example#Pass1", true);
        userGroups.createGroup(group);
        userGroups.assignUserToGroup(user, group);
        roles.assignRoleToUser(role, user);

        System.out.println("Groups for " + user + ": " + userGroups.getUserGroups(user));
        System.out.println("Roles for " + user + ": " + roles.getUserRoles(user));

        // Cleanup
        roles.unassignRoleFromUser(role, user);
        userGroups.unassignUserFromGroup(user, group);
        userGroups.deleteGroup(group);
        userGroups.deleteUser(user);
        roles.deleteRole(role);
        System.out.println("Cleaned up.");

        client.close();
    }
}
