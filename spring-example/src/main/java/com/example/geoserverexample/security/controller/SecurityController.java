package com.example.geoserverexample.security.controller;

import com.example.geoserverexample.security.dto.CreateFilterChainRequest;
import com.example.geoserverexample.security.dto.CreateUserRequest;
import com.example.geoserverexample.security.service.SecurityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService service;

    public SecurityController(SecurityService service) {
        this.service = service;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("roles", service.listRoles());
        model.addAttribute("users", service.listUsers());
        model.addAttribute("groups", service.listGroups());
        model.addAttribute("filterChains", service.listFilterChains());
        model.addAttribute("catalogMode", service.getCatalogMode());
        model.addAttribute("authFilters", service.listAuthFilters());
        model.addAttribute("authProviders", service.listAuthProviders());
        model.addAttribute("userGroupServices", service.listUserGroupServices());
        model.addAttribute("layerAcl", service.getLayerAcl());
        model.addAttribute("serviceAcl", service.getServiceAcl());
        model.addAttribute("restAcl", service.getRestAcl());
        return "security/index";
    }

    // Roles

    @PostMapping("/roles/create")
    public String createRole(@RequestParam String role, RedirectAttributes redirect) {
        service.createRole(role);
        redirect.addFlashAttribute("ok", "Role '" + role + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/roles/{role}/delete")
    public String deleteRole(@PathVariable String role, RedirectAttributes redirect) {
        service.deleteRole(role);
        redirect.addFlashAttribute("ok", "Role '" + role + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/roles/assign")
    public String assignRole(@RequestParam String role, @RequestParam String user, RedirectAttributes redirect) {
        service.assignRole(role, user);
        redirect.addFlashAttribute("ok", "Assigned role '" + role + "' to user '" + user + "'.");
        return "redirect:/security";
    }

    @PostMapping("/roles/unassign")
    public String unassignRole(@RequestParam String role, @RequestParam String user, RedirectAttributes redirect) {
        service.unassignRole(role, user);
        redirect.addFlashAttribute("ok", "Unassigned role '" + role + "' from user '" + user + "'.");
        return "redirect:/security";
    }

    // Users

    @PostMapping("/users/create")
    public String createUser(CreateUserRequest form, RedirectAttributes redirect) {
        service.createUser(form.getUsername(), form.getPassword(), form.isEnabled());
        redirect.addFlashAttribute("ok", "User '" + form.getUsername() + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/users/{username}/delete")
    public String deleteUser(@PathVariable String username, RedirectAttributes redirect) {
        service.deleteUser(username);
        redirect.addFlashAttribute("ok", "User '" + username + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/users/{username}/toggle")
    public String toggleUser(@PathVariable String username, @RequestParam boolean enabled,
                              RedirectAttributes redirect) {
        service.toggleUser(username, enabled);
        redirect.addFlashAttribute("ok", "User '" + username + "' enabled=" + enabled);
        return "redirect:/security";
    }

    // Groups

    @PostMapping("/groups/create")
    public String createGroup(@RequestParam String group, RedirectAttributes redirect) {
        service.createGroup(group);
        redirect.addFlashAttribute("ok", "Group '" + group + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/groups/{group}/delete")
    public String deleteGroup(@PathVariable String group, RedirectAttributes redirect) {
        service.deleteGroup(group);
        redirect.addFlashAttribute("ok", "Group '" + group + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/groups/assign")
    public String assignUserToGroup(@RequestParam String user, @RequestParam String group,
                                     RedirectAttributes redirect) {
        service.assignUserToGroup(user, group);
        redirect.addFlashAttribute("ok", "Assigned user '" + user + "' to group '" + group + "'.");
        return "redirect:/security";
    }

    // Filter chains

    @PostMapping("/filterchains/create")
    public String createFilterChain(CreateFilterChainRequest form, RedirectAttributes redirect) {
        service.createFilterChain(form.getName(), form.getPath(), form.getFilters());
        redirect.addFlashAttribute("ok", "Filter chain '" + form.getName() + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/filterchains/{name}/delete")
    public String deleteFilterChain(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteFilterChain(name);
        redirect.addFlashAttribute("ok", "Filter chain '" + name + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/filterchains/reorder")
    public String reorderFilterChains(@RequestParam List<String> order, RedirectAttributes redirect) {
        service.reorderFilterChains(order);
        redirect.addFlashAttribute("ok", "Filter chain order updated.");
        return "redirect:/security";
    }

    // Auth Filters

    @PostMapping("/authfilters/create")
    public String createAuthFilter(@RequestParam String name,
                                    @RequestParam(defaultValue = "false") boolean useRememberMe,
                                    RedirectAttributes redirect) {
        service.createAuthFilter(name, useRememberMe);
        redirect.addFlashAttribute("ok", "Auth filter '" + name + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/authfilters/{name}/update")
    public String updateAuthFilter(@PathVariable String name,
                                    @RequestParam(defaultValue = "false") boolean useRememberMe,
                                    RedirectAttributes redirect) {
        service.updateAuthFilter(name, useRememberMe);
        redirect.addFlashAttribute("ok", "Auth filter '" + name + "' updated (useRememberMe=" + useRememberMe + ").");
        return "redirect:/security";
    }

    @PostMapping("/authfilters/{name}/delete")
    public String deleteAuthFilter(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteAuthFilter(name);
        redirect.addFlashAttribute("ok", "Auth filter '" + name + "' deleted.");
        return "redirect:/security";
    }

    // Auth Providers

    @PostMapping("/authproviders/create")
    public String createAuthProvider(@RequestParam String name, @RequestParam String userGroupService,
                                      RedirectAttributes redirect) {
        service.createAuthProvider(name, userGroupService);
        redirect.addFlashAttribute("ok", "Auth provider '" + name + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/authproviders/{name}/delete")
    public String deleteAuthProvider(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteAuthProvider(name);
        redirect.addFlashAttribute("ok", "Auth provider '" + name + "' deleted.");
        return "redirect:/security";
    }

    // User/Group Services

    @PostMapping("/usergroupservices/create")
    public String createUserGroupService(@RequestParam String name, RedirectAttributes redirect) {
        service.createUserGroupService(name);
        redirect.addFlashAttribute("ok", "User/Group service '" + name + "' created.");
        return "redirect:/security";
    }

    @PostMapping("/usergroupservices/{name}/delete")
    public String deleteUserGroupService(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteUserGroupService(name);
        redirect.addFlashAttribute("ok", "User/Group service '" + name + "' deleted.");
        return "redirect:/security";
    }

    // Catalog mode

    @PostMapping("/catalog-mode")
    public String setCatalogMode(@RequestParam String mode, RedirectAttributes redirect) {
        service.setCatalogMode(mode);
        redirect.addFlashAttribute("ok", "Catalog mode set to " + mode);
        return "redirect:/security";
    }

    // ACL rules

    @PostMapping("/acl/layers/add")
    public String addLayerAcl(@RequestParam String rule, @RequestParam String roles, RedirectAttributes redirect) {
        service.addLayerAcl(rule, roles);
        redirect.addFlashAttribute("ok", "Layer ACL rule '" + rule + "' added.");
        return "redirect:/security";
    }

    @PostMapping("/acl/layers/delete")
    public String deleteLayerAcl(@RequestParam String rule, RedirectAttributes redirect) {
        service.deleteLayerAcl(rule);
        redirect.addFlashAttribute("ok", "Layer ACL rule '" + rule + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/acl/layers/delete-all")
    public String deleteAllLayerAcl(RedirectAttributes redirect) {
        service.deleteAllLayerAcl();
        redirect.addFlashAttribute("ok", "All layer ACL rules deleted.");
        return "redirect:/security";
    }

    @PostMapping("/acl/services/add")
    public String addServiceAcl(@RequestParam String rule, @RequestParam String roles, RedirectAttributes redirect) {
        service.addServiceAcl(rule, roles);
        redirect.addFlashAttribute("ok", "Service ACL rule '" + rule + "' added.");
        return "redirect:/security";
    }

    @PostMapping("/acl/services/delete")
    public String deleteServiceAcl(@RequestParam String rule, RedirectAttributes redirect) {
        service.deleteServiceAcl(rule);
        redirect.addFlashAttribute("ok", "Service ACL rule '" + rule + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/acl/services/delete-all")
    public String deleteAllServiceAcl(RedirectAttributes redirect) {
        service.deleteAllServiceAcl();
        redirect.addFlashAttribute("ok", "All service ACL rules deleted.");
        return "redirect:/security";
    }

    @PostMapping("/acl/rest/add")
    public String addRestAcl(@RequestParam String rule, @RequestParam String roles, RedirectAttributes redirect) {
        service.addRestAcl(rule, roles);
        redirect.addFlashAttribute("ok", "REST ACL rule '" + rule + "' added.");
        return "redirect:/security";
    }

    @PostMapping("/acl/rest/delete")
    public String deleteRestAcl(@RequestParam String rule, RedirectAttributes redirect) {
        service.deleteRestAcl(rule);
        redirect.addFlashAttribute("ok", "REST ACL rule '" + rule + "' deleted.");
        return "redirect:/security";
    }

    @PostMapping("/acl/rest/delete-all")
    public String deleteAllRestAcl(RedirectAttributes redirect) {
        service.deleteAllRestAcl();
        redirect.addFlashAttribute("ok", "All REST ACL rules deleted.");
        return "redirect:/security";
    }
}
