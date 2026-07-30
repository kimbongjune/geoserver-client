package io.github.kimbongjune.geoserverclient.api.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GeoServer Security Roles REST API client.
 *
 * <p>Source: {@code src/restconfig/src/main/java/org/geoserver/rest/security/RolesRestController.java}
 * <br>{@code @RequestMapping("/security/roles")}
 *
 * <p>All endpoints take <b>no request body</b>.
 * Role names (role), user names (user), and group names (group) are all Path Parameters.
 */
public class RoleManager extends AbstractManager {


    public RoleManager(GeoServerHttpClient httpClient,
                       SerializerFactory serializerFactory,
                       DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Active Role Service

    /** [1] Returns all roles in the active role service. */
    public List<String> getRoles() {
        return parseRoles(doGetRaw("/rest/security/roles", "application/json"));
    }

    /** [2] Returns roles assigned to a user. Returns empty list for non-existent user. */
    public List<String> getUserRoles(String user) {
        requireNonEmpty(user, "user");
        return parseRoles(doGetRaw("/rest/security/roles/user/" + user, "application/json"));
    }

    /** [3] Returns roles assigned to a group. Returns empty list for non-existent group. */
    public List<String> getGroupRoles(String group) {
        requireNonEmpty(group, "group");
        return parseRoles(doGetRaw("/rest/security/roles/group/" + group, "application/json"));
    }

    /** [4] Creates a new role. Returns 201 on success, throws on duplicate (404 from GeoServer). */
    public void createRole(String role) {
        requireNonEmpty(role, "role");
        String path = "/rest/security/roles/role/" + role;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [5] Deletes a role. Throws ResourceNotFoundException if not found. */
    public void deleteRole(String role) {
        requireNonEmpty(role, "role");
        doDelete("/rest/security/roles/role/" + role);
    }

    /** [6] Assigns a role to a user. */
    public void assignRoleToUser(String role, String user) {
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        String path = "/rest/security/roles/role/" + role + "/user/" + user;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [7] Unassigns a role from a user. */
    public void unassignRoleFromUser(String role, String user) {
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/roles/role/" + role + "/user/" + user);
    }

    /** [8] Assigns a role to a group. */
    public void assignRoleToGroup(String role, String group) {
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        String path = "/rest/security/roles/role/" + role + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [9] Unassigns a role from a group. */
    public void unassignRoleFromGroup(String role, String group) {
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/roles/role/" + role + "/group/" + group);
    }

    // Service-specific Role API

    /** [10] Returns all roles in a specific role service. Throws 404 for unknown service. */
    public List<String> getRolesByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseRoles(doGetRaw("/rest/security/roles/service/" + serviceName, "application/json"));
    }

    /** [11] Returns user roles in a specific role service. */
    public List<String> getUserRolesByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        return parseRoles(doGetRaw(
                "/rest/security/roles/service/" + serviceName + "/user/" + user, "application/json"));
    }

    /** [12] Returns group roles in a specific role service. */
    public List<String> getGroupRolesByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        return parseRoles(doGetRaw(
                "/rest/security/roles/service/" + serviceName + "/group/" + group, "application/json"));
    }

    /** [13] Creates a role in a specific role service. */
    public void createRoleByService(String serviceName, String role) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        String path = "/rest/security/roles/service/" + serviceName + "/role/" + role;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [14] Deletes a role from a specific role service. */
    public void deleteRoleByService(String serviceName, String role) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        doDelete("/rest/security/roles/service/" + serviceName + "/role/" + role);
    }

    /** [15] Assigns a role to a user in a specific role service. */
    public void assignRoleToUserByService(String serviceName, String role, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        String path = "/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/user/" + user;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [16] Unassigns a role from a user in a specific role service. */
    public void unassignRoleFromUserByService(String serviceName, String role, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/user/" + user);
    }

    /** [17] Assigns a role to a group in a specific role service. */
    public void assignRoleToGroupByService(String serviceName, String role, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        String path = "/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [18] Unassigns a role from a group in a specific role service. */
    public void unassignRoleFromGroupByService(String serviceName, String role, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/group/" + group);
    }

    // Helpers

    private List<String> parseRoles(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode arr = getObjectMapper().readTree(body).path("roles");
            List<String> result = new ArrayList<>();
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    result.add(node.asText());
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse roles response", e);
        }
    }
}
