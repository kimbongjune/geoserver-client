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
 * <p>Source: {@code src/restconfig/src/main/java/org/geoserver/rest/security/UsersRestController.java}
 * <br>{@code @RequestMapping("/security/usergroup")}
 */
public class UserGroupManager extends AbstractManager {


    public UserGroupManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Active User/Group Service

    /** [1] Returns all users. Password is always null in the response. */
    public List<SecurityUserInfo> getUsers() {
        return parseUsers(doGetRaw("/rest/security/usergroup/users", "application/json"));
    }

    /** [2] Returns all group names. */
    public List<String> getGroups() {
        return parseGroups(doGetRaw("/rest/security/usergroup/groups", "application/json"));
    }

    /** [3] Returns users in a group. Throws 404 for non-existent group (unlike Roles API). */
    public List<SecurityUserInfo> getGroupUsers(String group) {
        requireNonEmpty(group, "group");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/group/" + group + "/users", "application/json"));
    }

    /** [4] Returns groups a user belongs to. Throws 404 for non-existent user. */
    public List<String> getUserGroups(String user) {
        requireNonEmpty(user, "user");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/user/" + user + "/groups", "application/json"));
    }

    /**
     * [5] Creates a new user. Body MUST use {"user":{...}} wrapper — flat JSON causes 500.
     * Password policy requires upper/lower/digit/special characters.
     */
    public void createUser(String userName, String password, boolean enabled) {
        requireNonEmpty(userName, "userName");
        requireNonEmpty(password, "password");
        String body = buildUserBody(userName, password, enabled);
        String path = "/rest/security/usergroup/users";
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [6] Updates an existing user. Omitted fields keep their current value. */
    public void updateUser(String userName, String password, Boolean enabled) {
        requireNonEmpty(userName, "userName");
        try {
            ObjectNode userNode = getObjectMapper().createObjectNode();
            if (password != null) userNode.put("password", password);
            if (enabled  != null) userNode.put("enabled",  enabled);
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

    /** [7] Deletes a user. Throws ResourceNotFoundException if not found. */
    public void deleteUser(String user) {
        requireNonEmpty(user, "user");
        doDelete("/rest/security/usergroup/user/" + user);
    }

    /** [8] Creates a new group (no body). Throws on duplicate (404 from GeoServer). */
    public void createGroup(String group) {
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [9] Deletes a group. Throws ResourceNotFoundException if not found. */
    public void deleteGroup(String group) {
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/group/" + group);
    }

    /** [10] Assigns a user to a group. Duplicate assignments return 200. */
    public void assignUserToGroup(String user, String group) {
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/user/" + user + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [11] Removes a user from a group. */
    public void unassignUserFromGroup(String user, String group) {
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/user/" + user + "/group/" + group);
    }

    // Service-specific User/Group API

    /** [12] Returns all users in a specific service. */
    public List<SecurityUserInfo> getUsersByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName + "/users", "application/json"));
    }

    /** [13] Returns all groups in a specific service. */
    public List<String> getGroupsByService(String serviceName) {
        requireNonEmpty(serviceName, "serviceName");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName + "/groups", "application/json"));
    }

    /** [14] Returns users in a group within a specific service. */
    public List<SecurityUserInfo> getGroupUsersByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        return parseUsers(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName
                        + "/group/" + group + "/users", "application/json"));
    }

    /** [15] Returns groups a user belongs to within a specific service. */
    public List<String> getUserGroupsByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        return parseGroups(doGetRaw(
                "/rest/security/usergroup/service/" + serviceName
                        + "/user/" + user + "/groups", "application/json"));
    }

    /** [16] Creates a user in a specific service. */
    public void createUserByService(String serviceName, String userName, String password, boolean enabled) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(userName, "userName");
        requireNonEmpty(password, "password");
        String body = buildUserBody(userName, password, enabled);
        String path = "/rest/security/usergroup/service/" + serviceName + "/users";
        GeoServerResponse response = httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [17] Updates a user in a specific service. */
    public void updateUserByService(String serviceName, String userName, String password, Boolean enabled) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(userName, "userName");
        try {
            ObjectNode userNode = getObjectMapper().createObjectNode();
            if (password != null) userNode.put("password", password);
            if (enabled  != null) userNode.put("enabled",  enabled);
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

    /** [18] Deletes a user in a specific service. */
    public void deleteUserByService(String serviceName, String user) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        doDelete("/rest/security/usergroup/service/" + serviceName + "/user/" + user);
    }

    /** [19] Creates a group in a specific service. */
    public void createGroupByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/service/" + serviceName + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [20] Deletes a group in a specific service. */
    public void deleteGroupByService(String serviceName, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/service/" + serviceName + "/group/" + group);
    }

    /** [21] Assigns a user to a group in a specific service. */
    public void assignUserToGroupByService(String serviceName, String user, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        String path = "/rest/security/usergroup/service/" + serviceName
                + "/user/" + user + "/group/" + group;
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    /** [22] Removes a user from a group in a specific service. */
    public void unassignUserFromGroupByService(String serviceName, String user, String group) {
        requireNonEmpty(serviceName, "serviceName");
        requireNonEmpty(user, "user");
        requireNonEmpty(group, "group");
        doDelete("/rest/security/usergroup/service/" + serviceName
                + "/user/" + user + "/group/" + group);
    }

    // Helpers

    private List<SecurityUserInfo> parseUsers(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
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
        if (body == null || body.isEmpty()) return Collections.emptyList();
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
