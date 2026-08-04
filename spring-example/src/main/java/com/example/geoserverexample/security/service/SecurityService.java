package com.example.geoserverexample.security.service;

import io.github.kimbongjune.geoserverclient.dto.security.AuthProviderConfig;
import io.github.kimbongjune.geoserverclient.dto.security.FilterChainEntry;
import io.github.kimbongjune.geoserverclient.dto.security.SecurityUserInfo;

import java.util.List;
import java.util.Map;

public interface SecurityService {

    List<String> listRoles();

    List<SecurityUserInfo> listUsers();

    List<String> listGroups();

    List<FilterChainEntry> listFilterChains();

    String getCatalogMode();

    List<String> listAuthFilters();

    List<AuthProviderConfig> listAuthProviders();

    List<String> listUserGroupServices();

    // Roles

    void createRole(String role);

    void deleteRole(String role);

    void assignRole(String role, String user);

    void unassignRole(String role, String user);

    // Users

    void createUser(String username, String password, boolean enabled);

    void deleteUser(String username);

    void toggleUser(String username, boolean enabled);

    // Groups

    void createGroup(String group);

    void deleteGroup(String group);

    void assignUserToGroup(String user, String group);

    // Filter chains

    void createFilterChain(String name, String path, String filters);

    void deleteFilterChain(String name);

    void reorderFilterChains(List<String> order);

    // Auth Filters

    void createAuthFilter(String name, boolean useRememberMe);

    void updateAuthFilter(String name, boolean useRememberMe);

    void deleteAuthFilter(String name);

    // Auth Providers

    void createAuthProvider(String name, String userGroupService);

    void deleteAuthProvider(String name);

    // User/Group Services

    void createUserGroupService(String name);

    void deleteUserGroupService(String name);

    // Catalog mode

    void setCatalogMode(String mode);

    // ACL rules

    Map<String, String> getLayerAcl();

    Map<String, String> getServiceAcl();

    Map<String, String> getRestAcl();

    void addLayerAcl(String rule, String roles);

    void addServiceAcl(String rule, String roles);

    void addRestAcl(String rule, String roles);

    void deleteLayerAcl(String rule);

    void deleteServiceAcl(String rule);

    void deleteRestAcl(String rule);

    /** Always throws — confirmed GeoServer 2.28.2 server bug (always 500). Kept to demonstrate real behavior. */
    void deleteAllLayerAcl();

    /** Always throws — confirmed GeoServer 2.28.2 server bug (always 500). Kept to demonstrate real behavior. */
    void deleteAllServiceAcl();

    /** Always throws — confirmed GeoServer 2.28.2 server bug (always 500). Kept to demonstrate real behavior. */
    void deleteAllRestAcl();
}
