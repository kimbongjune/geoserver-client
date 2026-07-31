package io.github.kimbongjune.geoserverclient.api.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.SecurityUserInfo;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GeoServer Security User/Group REST API client.
 *
 * <p>Manages users, groups, and their memberships in the active or a named user/group service.
 *
 * <p>Source: {@code src/restconfig/src/main/java/org/geoserver/rest/security/UsersRestController.java}
 * <br>{@code @RequestMapping("/security/usergroup")}
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET    /rest/security/usergroup/users
 * GET    /rest/security/usergroup/groups
 * GET    /rest/security/usergroup/group/{group}/users
 * GET    /rest/security/usergroup/user/{user}/groups
 * POST   /rest/security/usergroup/users
 * POST   /rest/security/usergroup/user/{user}
 * DELETE /rest/security/usergroup/user/{user}
 * POST   /rest/security/usergroup/group/{group}
 * DELETE /rest/security/usergroup/group/{group}
 * POST   /rest/security/usergroup/user/{user}/group/{group}
 * DELETE /rest/security/usergroup/user/{user}/group/{group}
 * GET    /rest/security/usergroup/service/{service}/users
 * GET    /rest/security/usergroup/service/{service}/groups
 * GET    /rest/security/usergroup/service/{service}/group/{group}/users
 * GET    /rest/security/usergroup/service/{service}/user/{user}/groups
 * POST   /rest/security/usergroup/service/{service}/users
 * POST   /rest/security/usergroup/service/{service}/user/{user}
 * DELETE /rest/security/usergroup/service/{service}/user/{user}
 * POST   /rest/security/usergroup/service/{service}/group/{group}
 * DELETE /rest/security/usergroup/service/{service}/group/{group}
 * POST   /rest/security/usergroup/service/{service}/user/{user}/group/{group}
 * DELETE /rest/security/usergroup/service/{service}/user/{user}/group/{group}
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * UserGroupManager mgr = client.userGroup();
 * mgr.createUser("alice", "P@ssw0rd!", true);
 * mgr.createGroup("editors");
 * mgr.assignUserToGroup("alice", "editors");
 * }</pre>
 *
 * @since 1.0.0
 */
public class UserGroupManager extends AbstractManager {

    /**
     * Constructs a new UserGroupManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public UserGroupManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Active User/Group Service

    /**
     * Returns all users in the active user/group service.
     * The password field is always {@code null} in the response.
     *
     * @return list of user info (never null; empty when none exist)
     */
    public List<SecurityUserInfo> getUsers() {
        return parseUsers(doGetRaw("/rest/security/usergroup/users", "application/json"));
    }

    /**
     * Returns all group names in the active user/group service.
     *
     * @return list of group names (never null; empty when none exist)
     */
    public List<String> getGroups() {
        return parseGroups(doGetRaw("/rest/security/usergroup/groups", "application/json"));
    }

    /**
     * Returns users belonging to a group in the active user/group service.
     * Unlike the Roles API, throws 404 for a non-existent group.
     *
     * @param group group name (required)
     * @return list of user info (never null; empty when none assigned)
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if group does not exist (404)
     */
    public List<SecurityUserInfo> getGroupUsers(String group) {
        requireNonEmpty(group, "group");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/group/" + group + "/users", "application/json"));
    }

    /**
     * Returns the groups a user belongs to in the active user/group service.
     * Throws 404 for a non-existent user.
     *
     * @param user user name (required)
     * @return list of group names (never null; empty when none assigned)
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if user does not exist (404)
     */
    public List<String> getUserGroups(String user) {
        requireNonEmpty(user, "user");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/user/" + user + "/groups", "application/json"));
    }

    /**
     * Creates a new user in the active user/group service.
     * Request body is wrapped as {@code {"user":{...}}} — flat JSON causes 500.
     * Password policy requires upper/lower/digit/special characters.
     *
     * @param userName user name (required)
     * @param password password meeting the server's complexity requirements (required)
     * @param enabled  whether the user is enabled on creation
     */
    public void createUser(String userName, String password, boolean enabled) {
        requireNonEmpty(userName, "userName");
        requireNonEmpty(password, "password");
        String body = buildUserBody(userName, password, enabled);
        String path = "/rest/security/usergroup/users";
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Updates an existing user in the active user/group service.
     * Pass {@code null} for any parameter to leave it unchanged.
     *
     * @param userName user name of the user to update (required)
     * @param password new password, or {@code null} to leave unchanged
     * @param enabled  new enabled state, or {@code null} to leave unchanged
     */
    public void updateUser(String userName, String password, Boolean enabled) {
        requireNonEmpty(userName, "userName");
        try {
            ObjectNode userNode = getObjectMapper().createObjectNode();
            if (password != null) {
                userNode.put("password", password);
            }
            if (enabled  != null) {
                userNode.put("enabled",  enabled);
            }
            ObjectNode wrapper = getObjectMapper().createObjectNode();
            wrapper.set("user", userNode);
            String path = "/rest/security/usergroup/user/" + userName;
            GeoServerResponse response = httpClient.post(
                    path, getObjectMapper().writeValueAsString(wrapper), "application/json", "application/json");
            handleErrorResponse(response, "POST", path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize update user request", e);
        }
    }

    /**
     * Deletes a user from the active user/group service.
     *
     * @param user user name to delete (required)
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if user does not exist (404)
     */
    public void deleteUser(String user) {
        requireNonEmpty(user, "user");
        doDelete("/rest/security/usergroup/user/" + user);
    }

    /**
     * Creates a new group in the active user/group service (no body required).
     * Throws on duplicate — GeoServer returns 404 for name conflicts.
     *
     * @param group group name to create (required)
     */
    public void createGroup(String group) {
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Deletes a group from the active user/group service.
     *
     * @param group group name to delete (required)
     * @throws io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException if group does not exist (404)
     */
    public void deleteGroup(String group) {
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/group/" + group);
    }

    /**
     * Assigns a user to a group in the active user/group service.
     * Duplicate assignments are idempotent (returns 200).
     *
     * @param user  user name to assign (required)
     * @param group group name to assign the user to (required)
     */
    public void assignUserToGroup(String user, String group) {
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/user/" + user + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a user from a group in the active user/group service.
     *
     * @param user  user name to remove (required)
     * @param group group name to remove the user from (required)
     */
    public void unassignUserFromGroup(String user, String group) {
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/user/" + user + "/group/" + group);
    }

    // Service-specific User/Group API

    /**
     * Returns all users in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @return list of user info (never null; empty when none exist)
     */
    public List<SecurityUserInfo> getUsersByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName + "/users", "application/json"));
    }

    /**
     * Returns all group names in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @return list of group names (never null; empty when none exist)
     */
    public List<String> getGroupsByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName + "/groups", "application/json"));
    }

    /**
     * Returns users belonging to a group in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param group       group name (required)
     * @return list of user info (never null; empty when none assigned)
     */
    public List<SecurityUserInfo> getGroupUsersByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName
                        + "/group/" + group + "/users", "application/json"));
    }

    /**
     * Returns the groups a user belongs to in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param user        user name (required)
     * @return list of group names (never null; empty when none assigned)
     */
    public List<String> getUserGroupsByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName
                        + "/user/" + user + "/groups", "application/json"));
    }

    /**
     * Creates a new user in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param userName    user name (required)
     * @param password    password meeting the server's complexity requirements (required)
     * @param enabled     whether the user is enabled on creation
     */
    public void createUserByService(String serviceName, String userName, String password, boolean enabled) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(userName, "userName");
        requireNonEmpty(password, "password");
        String body = buildUserBody(userName, password, enabled);
        String path = "/rest/security/usergroup/service/" + serviceName + "/users";
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Updates an existing user in a specific (named) user/group service.
     * Pass {@code null} for any parameter to leave it unchanged.
     *
     * @param serviceName user/group service name (required)
     * @param userName    user name of the user to update (required)
     * @param password    new password, or {@code null} to leave unchanged
     * @param enabled     new enabled state, or {@code null} to leave unchanged
     */
    public void updateUserByService(String serviceName, String userName, String password, Boolean enabled) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(userName, "userName");
        try {
            ObjectNode userNode = getObjectMapper().createObjectNode();
            if (password != null) {
                userNode.put("password", password);
            }
            if (enabled  != null) {
                userNode.put("enabled",  enabled);
            }
            ObjectNode wrapper = getObjectMapper().createObjectNode();
            wrapper.set("user", userNode);
            String path = "/rest/security/usergroup/service/" + serviceName + "/user/" + userName;
            GeoServerResponse response = httpClient.post(
                    path, getObjectMapper().writeValueAsString(wrapper), "application/json", "application/json");
            handleErrorResponse(response, "POST", path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize update user request", e);
        }
    }

    /**
     * Deletes a user from a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param user        user name to delete (required)
     */
    public void deleteUserByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/usergroup/service/" + serviceName + "/user/" + user);
    }

    /**
     * Creates a new group in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param group       group name to create (required)
     */
    public void createGroupByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/service/" + serviceName + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Deletes a group from a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param group       group name to delete (required)
     */
    public void deleteGroupByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/service/" + serviceName + "/group/" + group);
    }

    /**
     * Assigns a user to a group in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param user        user name to assign (required)
     * @param group       group name to assign the user to (required)
     */
    public void assignUserToGroupByService(String serviceName, String user, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/service/" + serviceName
                + "/user/" + user + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /**
     * Removes a user from a group in a specific (named) user/group service.
     *
     * @param serviceName user/group service name (required)
     * @param user        user name to remove (required)
     * @param group       group name to remove the user from (required)
     */
    public void unassignUserFromGroupByService(String serviceName, String user, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/service/" + serviceName
                + "/user/" + user + "/group/" + group);
    }

    // Helpers

    private List<SecurityUserInfo> parseUsers(String body) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            JsonNode arr = getObjectMapper().readTree(body).path("users");
            List<SecurityUserInfo> result = new ArrayList<>();
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    result.add(new SecurityUserInfo(
                            node.path("userName").asText(),
                            node.path("enabled").asBoolean(true)));
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse users response", e);
        }
    }

    private List<String> parseGroups(String body) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            JsonNode arr = getObjectMapper().readTree(body).path("groups");
            List<String> result = new ArrayList<>();
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    result.add(node.asText());
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse groups response", e);
        }
    }

    private String buildUserBody(String userName, String password, boolean enabled) {
        try {
            ObjectNode userNode = getObjectMapper().createObjectNode();
            userNode.put("userName", userName);
            userNode.put("password", password);
            userNode.put("enabled",  enabled);
            ObjectNode wrapper = getObjectMapper().createObjectNode();
            wrapper.set("user", userNode);
            return getObjectMapper().writeValueAsString(wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize user body", e);
        }
    }
}
