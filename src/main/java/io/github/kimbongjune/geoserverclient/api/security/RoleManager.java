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
 * <p>Manages roles within the active or a named role service, and handles
 * role-to-user and role-to-group assignments.
 *
 * <p>Source: {@code src/restconfig/src/main/java/org/geoserver/rest/security/RolesRestController.java}
 * <br>{@code @RequestMapping("/security/roles")}
 *
 * <p>All endpoints take <b>no request body</b>.
 * Role names (role), user names (user), and group names (group) are all path parameters.
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET    /rest/security/roles
 * GET    /rest/security/roles/user/{user}
 * GET    /rest/security/roles/group/{group}
 * POST   /rest/security/roles/role/{role}
 * DELETE /rest/security/roles/role/{role}
 * POST   /rest/security/roles/role/{role}/user/{user}
 * DELETE /rest/security/roles/role/{role}/user/{user}
 * POST   /rest/security/roles/role/{role}/group/{group}
 * DELETE /rest/security/roles/role/{role}/group/{group}
 * GET    /rest/security/roles/service/{service}
 * GET    /rest/security/roles/service/{service}/user/{user}
 * GET    /rest/security/roles/service/{service}/group/{group}
 * POST   /rest/security/roles/service/{service}/role/{role}
 * DELETE /rest/security/roles/service/{service}/role/{role}
 * POST   /rest/security/roles/service/{service}/role/{role}/user/{user}
 * DELETE /rest/security/roles/service/{service}/role/{role}/user/{user}
 * POST   /rest/security/roles/service/{service}/role/{role}/group/{group}
 * DELETE /rest/security/roles/service/{service}/role/{role}/group/{group}
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * RoleManager mgr = client.role();
 * mgr.createRole("DATA_EDITOR");
 * mgr.assignRoleToUser("DATA_EDITOR", "alice");
 * List<String> roles = mgr.getUserRoles("alice");
 * }</pre>
 *
 * @since 1.0.0
 */
public class RoleManager extends AbstractManager {


    /**
     * Constructs a new RoleManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public RoleManager(GeoServerHttpClient httpClient,
                       SerializerFactory serializerFactory,
                       DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Active Role Service

    /**
     * Returns all roles in the active role service.
     *
     * @return list of role names; empty list when none exist
     */
    public List<String> getRoles() {
        return parseRoles(doGetRaw("/rest/security/roles", "application/json"));
    }

    /**
     * Returns roles assigned to a user in the active role service.
     * Returns an empty list for a non-existent user.
     *
     * @param user user name (required)
     * @return list of role names; empty list when none assigned
     */
    public List<String> getUserRoles(String user) {
        requireNonEmpty(user, "user");
        return parseRoles(doGetRaw("/rest/security/roles/user/" + user, "application/json"));
    }

    /**
     * Returns roles assigned to a group in the active role service.
     * Returns an empty list for a non-existent group.
     *
     * @param group group name (required)
     * @return list of role names; empty list when none assigned
     */
    public List<String> getGroupRoles(String group) {
        requireNonEmpty(group, "group");
        return parseRoles(doGetRaw("/rest/security/roles/group/" + group, "application/json"));
    }

    /**
     * Creates a new role in the active role service.
     * Returns 201 on success; throws on duplicate (GeoServer returns 404 for conflicts).
     *
     * @param role role name to create (required)
     */
    public void createRole(String role) {
        requireNonEmpty(role, "role");
        String path = "/rest/security/roles/role/" + role;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Deletes a role from the active role service.
     *
     * @param role role name to delete (required)
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if no such role exists (404)
     */
    public void deleteRole(String role) {
        requireNonEmpty(role, "role");
        doDelete("/rest/security/roles/role/" + role);
    }

    /**
     * Assigns a role to a user in the active role service.
     *
     * @param role role name to assign (required)
     * @param user user name to receive the role (required)
     */
    public void assignRoleToUser(String role, String user) {
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        String path = "/rest/security/roles/role/" + role + "/user/" + user;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a role assignment from a user in the active role service.
     *
     * @param role role name to unassign (required)
     * @param user user name to remove the role from (required)
     */
    public void unassignRoleFromUser(String role, String user) {
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/roles/role/" + role + "/user/" + user);
    }

    /**
     * Assigns a role to a group in the active role service.
     *
     * @param role  role name to assign (required)
     * @param group group name to receive the role (required)
     */
    public void assignRoleToGroup(String role, String group) {
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        String path = "/rest/security/roles/role/" + role + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a role assignment from a group in the active role service.
     *
     * @param role  role name to unassign (required)
     * @param group group name to remove the role from (required)
     */
    public void unassignRoleFromGroup(String role, String group) {
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/roles/role/" + role + "/group/" + group);
    }

    // Service-specific Role API

    /**
     * Returns all roles in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @return list of role names; empty list when none exist
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if service does not exist (404)
     */
    public List<String> getRolesByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseRoles(doGetRaw("/rest/security/roles/service/" + serviceName, "application/json"));
    }

    /**
     * Returns roles assigned to a user in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param user        user name (required)
     * @return list of role names; empty list when none assigned
     */
    public List<String> getUserRolesByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        return parseRoles(doGetRaw(
                "/rest/security/roles/service/" + serviceName + "/user/" + user, "application/json"));
    }

    /**
     * Returns roles assigned to a group in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param group       group name (required)
     * @return list of role names; empty list when none assigned
     */
    public List<String> getGroupRolesByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        return parseRoles(doGetRaw(
                "/rest/security/roles/service/" + serviceName + "/group/" + group, "application/json"));
    }

    /**
     * Creates a new role in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to create (required)
     */
    public void createRoleByService(String serviceName, String role) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        String path = "/rest/security/roles/service/" + serviceName + "/role/" + role;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Deletes a role from a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to delete (required)
     */
    public void deleteRoleByService(String serviceName, String role) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        doDelete("/rest/security/roles/service/" + serviceName + "/role/" + role);
    }

    /**
     * Assigns a role to a user in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to assign (required)
     * @param user        user name to receive the role (required)
     */
    public void assignRoleToUserByService(String serviceName, String role, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        String path = "/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/user/" + user;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a role assignment from a user in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to unassign (required)
     * @param user        user name to remove the role from (required)
     */
    public void unassignRoleFromUserByService(String serviceName, String role, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/user/" + user);
    }

    /**
     * Assigns a role to a group in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to assign (required)
     * @param group       group name to receive the role (required)
     */
    public void assignRoleToGroupByService(String serviceName, String role, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        String path = "/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a role assignment from a group in a specific (named) role service.
     *
     * @param serviceName role service name (required)
     * @param role        role name to unassign (required)
     * @param group       group name to remove the role from (required)
     */
    public void unassignRoleFromGroupByService(String serviceName, String role, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(role, "role");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/roles/service/" + serviceName
                + "/role/" + role + "/group/" + group);
    }

    // Helpers

    private List<String> parseRoles(String body) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyList();
        }
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
