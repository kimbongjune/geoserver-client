package com.example.geoserverexample.workspace.controller;

import com.example.geoserverexample.workspace.dto.CreateNamespaceRequest;
import com.example.geoserverexample.workspace.dto.CreateWorkspaceRequest;
import com.example.geoserverexample.workspace.service.WorkspaceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workspaces")
public class WorkspaceController {

    private final WorkspaceService service;

    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("namespaces", service.listNamespaces());
        model.addAttribute("defaultWorkspace", service.getDefaultWorkspaceName());
        return "workspaces/list";
    }

    @PostMapping("/create")
    public String create(CreateWorkspaceRequest form, RedirectAttributes redirect) {
        service.createWorkspace(form.getName(), form.isIsolated(), form.isSetAsDefault());
        redirect.addFlashAttribute("ok", "Workspace '" + form.getName() + "' created.");
        return "redirect:/workspaces";
    }

    @PostMapping("/{name}/update")
    public String update(@PathVariable String name,
                          @RequestParam(defaultValue = "false") boolean isolated,
                          @RequestParam(required = false) String newName,
                          RedirectAttributes redirect) {
        boolean renaming = service.isRenaming(newName);
        service.updateWorkspace(name, isolated, newName);
        redirect.addFlashAttribute("ok", "Workspace '" + name + "' updated"
                + (renaming ? " (renamed to '" + newName + "')" : "") + " (isolated=" + isolated + ").");
        return "redirect:/workspaces";
    }

    @PostMapping("/{name}/set-default")
    public String setDefault(@PathVariable String name, RedirectAttributes redirect) {
        service.setDefaultWorkspace(name);
        redirect.addFlashAttribute("ok", "'" + name + "' is now the default workspace.");
        return "redirect:/workspaces";
    }

    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name,
                          @RequestParam(defaultValue = "true") boolean recurse,
                          RedirectAttributes redirect) {
        service.deleteWorkspace(name, recurse);
        redirect.addFlashAttribute("ok", "Workspace '" + name + "' deleted.");
        return "redirect:/workspaces";
    }

    @PostMapping("/namespaces/create")
    public String createNamespace(CreateNamespaceRequest form, RedirectAttributes redirect) {
        service.createNamespace(form.getPrefix(), form.getUri(), form.isIsolated());
        redirect.addFlashAttribute("ok", "Namespace '" + form.getPrefix() + "' created.");
        return "redirect:/workspaces";
    }

    @PostMapping("/namespaces/{prefix}/update")
    public String updateNamespace(@PathVariable String prefix, @RequestParam String uri,
                                   RedirectAttributes redirect) {
        service.updateNamespaceUri(prefix, uri);
        redirect.addFlashAttribute("ok", "Namespace '" + prefix + "' URI updated.");
        return "redirect:/workspaces";
    }

    @PostMapping("/namespaces/{prefix}/set-default")
    public String setDefaultNamespace(@PathVariable String prefix, RedirectAttributes redirect) {
        service.setDefaultNamespace(prefix);
        redirect.addFlashAttribute("ok", "'" + prefix + "' is now the default namespace"
                + " (this also changes the default workspace — GeoServer shares one setting for both).");
        return "redirect:/workspaces";
    }

    @PostMapping("/namespaces/{prefix}/delete")
    public String deleteNamespace(@PathVariable String prefix, RedirectAttributes redirect) {
        service.deleteNamespace(prefix);
        redirect.addFlashAttribute("ok", "Namespace '" + prefix + "' deleted.");
        return "redirect:/workspaces";
    }
}
