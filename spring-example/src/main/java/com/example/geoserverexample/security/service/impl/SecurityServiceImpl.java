package com.example.geoserverexample.security.service.impl;

import com.example.geoserverexample.security.service.SecurityService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.security.AclRules;
import io.github.kimbongjune.geoserverclient.dto.security.AuthFilterConfig;
import io.github.kimbongjune.geoserverclient.dto.security.AuthProviderConfig;
import io.github.kimbongjune.geoserverclient.dto.security.FilterChainEntry;
import io.github.kimbongjune.geoserverclient.dto.security.SecurityUserInfo;
import io.github.kimbongjune.geoserverclient.dto.security.UserGroupServiceConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final GeoServerClient client;

    public SecurityServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<String> listRoles() {
        return client.roles().getRoles();
    }

    @Override
    public List<SecurityUserInfo> listUsers() {
        return client.userGroups().getUsers();
    }

    @Override
    public List<String> listGroups() {
        return client.userGroups().getGroups();
    }

    @Override
    public List<FilterChainEntry> listFilterChains() {
        return client.filterChains().list();
    }

    @Override
    public String getCatalogMode() {
        return client.security().getCatalogMode();
    }

    @Override
    public List<String> listAuthFilters() {
        return client.authFilters().list();
    }

    @Override
    public List<AuthProviderConfig> listAuthProviders() {
        return client.authProviders().list();
    }

    @Override
    public List<String> listUserGroupServices() {
        return client.userGroupServices().list();
    }

    // Roles

    @Override
    public void createRole(String role) {
        client.roles().createRole(role);
    }

    @Override
    public void deleteRole(String role) {
        client.roles().deleteRole(role);
    }

    @Override
    public void assignRole(String role, String user) {
        client.roles().assignRoleToUser(role, user);
    }

    @Override
    public void unassignRole(String role, String user) {
        client.roles().unassignRoleFromUser(role, user);
    }

    // Users

    @Override
    public void createUser(String username, String password, boolean enabled) {
        client.userGroups().createUser(username, password, enabled);
    }

    @Override
    public void deleteUser(String username) {
        client.userGroups().deleteUser(username);
    }

    @Override
    public void toggleUser(String username, boolean enabled) {
        client.userGroups().updateUser(username, null, enabled);
    }

    // Groups

    @Override
    public void createGroup(String group) {
        client.userGroups().createGroup(group);
    }

    @Override
    public void deleteGroup(String group) {
        client.userGroups().deleteGroup(group);
    }

    @Override
    public void assignUserToGroup(String user, String group) {
        client.userGroups().assignUserToGroup(user, group);
    }

    // Filter chains

    @Override
    public void createFilterChain(String name, String path, String filters) {
        List<String> filterList = Arrays.asList(filters.split("\\s*,\\s*"));
        client.filterChains().create(new FilterChainEntry(name,
                "org.geoserver.security.ServiceLoginFilterChain", path, filterList));
    }

    @Override
    public void deleteFilterChain(String name) {
        client.filterChains().delete(name);
    }

    @Override
    public void reorderFilterChains(List<String> order) {
        client.filterChains().updateOrder(order);
    }

    // Auth Filters

    @Override
    public void createAuthFilter(String name, boolean useRememberMe) {
        client.authFilters().create(AuthFilterConfig.basic(name, useRememberMe));
    }

    @Override
    public void updateAuthFilter(String name, boolean useRememberMe) {
        client.authFilters().update(name, AuthFilterConfig.basic(name, useRememberMe));
    }

    @Override
    public void deleteAuthFilter(String name) {
        client.authFilters().delete(name);
    }

    // Auth Providers

    @Override
    public void createAuthProvider(String name, String userGroupService) {
        client.authProviders().create(AuthProviderConfig.usernamePassword(name, userGroupService));
    }

    @Override
    public void deleteAuthProvider(String name) {
        client.authProviders().delete(name);
    }

    // User/Group Services

    @Override
    public void createUserGroupService(String name) {
        client.userGroupServices().create(UserGroupServiceConfig.xml(
                name, name + ".xml", 0, true, "digestPasswordEncoder", "default"));
    }

    @Override
    public void deleteUserGroupService(String name) {
        client.userGroupServices().delete(name);
    }

    // Catalog mode

    @Override
    public void setCatalogMode(String mode) {
        client.security().setCatalogMode(mode);
    }

    // ACL rules

    @Override
    public Map<String, String> getLayerAcl() {
        return client.security().getLayerAcl().getEntries();
    }

    @Override
    public Map<String, String> getServiceAcl() {
        return client.security().getServiceAcl().getEntries();
    }

    @Override
    public Map<String, String> getRestAcl() {
        return client.security().getRestAcl().getEntries();
    }

    @Override
    public void addLayerAcl(String rule, String roles) {
        client.security().addLayerAcl(AclRules.of(rule, roles));
    }

    @Override
    public void addServiceAcl(String rule, String roles) {
        client.security().addServiceAcl(AclRules.of(rule, roles));
    }

    @Override
    public void addRestAcl(String rule, String roles) {
        client.security().addRestAcl(AclRules.of(rule, roles));
    }

    @Override
    public void deleteLayerAcl(String rule) {
        client.security().deleteLayerAcl(rule);
    }

    @Override
    public void deleteServiceAcl(String rule) {
        client.security().deleteServiceAcl(rule);
    }

    @Override
    public void deleteRestAcl(String rule) {
        client.security().deleteRestAcl(rule);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void deleteAllLayerAcl() {
        client.security().deleteAllLayerAcl();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void deleteAllServiceAcl() {
        client.security().deleteAllServiceAcl();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void deleteAllRestAcl() {
        client.security().deleteAllRestAcl();
    }
}
