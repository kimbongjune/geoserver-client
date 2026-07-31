package com.example.geoserverexample.settings.controller;

import com.example.geoserverexample.settings.dto.SaveWorkspaceServiceSettingsRequest;
import com.example.geoserverexample.settings.dto.UpdateServiceRequest;
import com.example.geoserverexample.settings.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

    private final SettingsService service;

    public SettingsController(SettingsService service) {
        this.service = service;
    }

    @GetMapping("/settings")
    public String index(@RequestParam(required = false) String ws, Model model) {
        model.addAttribute("global", service.getGlobalSettings());
        model.addAttribute("contact", service.getContact());
        model.addAttribute("logging", service.getLogging());
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("wms", service.getWms());
        model.addAttribute("wfs", service.getWfs());
        model.addAttribute("wcs", service.getWcs());
        model.addAttribute("wmts", service.getWmts());
        model.addAttribute("selectedWs", ws);
        if (ws != null && !ws.isEmpty()) {
            model.addAttribute("workspaceSettings", service.getWorkspaceSettingsBestEffort(ws));
            model.addAttribute("wsWms", service.getWorkspaceServiceSettingsBestEffort("wms", ws));
            model.addAttribute("wsWfs", service.getWorkspaceServiceSettingsBestEffort("wfs", ws));
            model.addAttribute("wsWcs", service.getWorkspaceServiceSettingsBestEffort("wcs", ws));
            model.addAttribute("wsWmts", service.getWorkspaceServiceSettingsBestEffort("wmts", ws));
        }
        return "settings/index";
    }

    @PostMapping("/settings/service/{svc}/update")
    public String updateService(@PathVariable String svc, UpdateServiceRequest form, RedirectAttributes redirect) {
        service.updateService(svc, form.isEnabled(), form.getTitle(), form.isVerbose());
        redirect.addFlashAttribute("ok", svc.toUpperCase() + " service updated (enabled=" + form.isEnabled() + ").");
        return "redirect:/settings";
    }

    @PostMapping("/settings/service/{svc}/workspace/{ws}/save")
    public String saveWorkspaceServiceSettings(@PathVariable String svc, @PathVariable String ws,
                                                SaveWorkspaceServiceSettingsRequest form,
                                                RedirectAttributes redirect) {
        service.saveWorkspaceServiceSettings(svc, ws, form.isEnabled(), form.getTitle(), form.isVerbose());
        redirect.addFlashAttribute("ok", svc.toUpperCase() + " settings for workspace '" + ws + "' saved.");
        return "redirect:/settings?ws=" + ws;
    }

    @PostMapping("/settings/service/{svc}/workspace/{ws}/delete")
    public String deleteWorkspaceServiceSettings(@PathVariable String svc, @PathVariable String ws,
                                                  RedirectAttributes redirect) {
        service.deleteWorkspaceServiceSettings(svc, ws);
        redirect.addFlashAttribute("ok", svc.toUpperCase() + " workspace override for '" + ws + "' removed.");
        return "redirect:/settings?ws=" + ws;
    }

    @PostMapping("/settings/global")
    public String updateGlobal(@RequestParam int numDecimals,
                                @RequestParam(defaultValue = "false") boolean verbose,
                                RedirectAttributes redirect) {
        service.updateGlobal(numDecimals, verbose);
        redirect.addFlashAttribute("ok", "Global settings updated.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/contact")
    public String updateContact(@RequestParam String organization, RedirectAttributes redirect) {
        service.updateContact(organization);
        redirect.addFlashAttribute("ok", "Contact organization updated.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/logging")
    public String updateLogging(@RequestParam String level,
                                 @RequestParam(defaultValue = "false") boolean stdOutLogging,
                                 RedirectAttributes redirect) {
        service.updateLogging(level, stdOutLogging);
        redirect.addFlashAttribute("ok", "Logging config updated.");
        return "redirect:/settings";
    }

    @PostMapping("/settings/workspace/create")
    public String createWorkspaceSettings(@RequestParam String ws, RedirectAttributes redirect) {
        service.createWorkspaceSettings(ws);
        redirect.addFlashAttribute("ok", "Workspace settings created for '" + ws + "'.");
        return "redirect:/settings?ws=" + ws;
    }

    @PostMapping("/settings/workspace/{ws}/update")
    public String updateWorkspaceSettings(@PathVariable String ws,
                                           @RequestParam int numDecimals,
                                           @RequestParam(defaultValue = "false") boolean verbose,
                                           RedirectAttributes redirect) {
        service.updateWorkspaceSettings(ws, numDecimals, verbose);
        redirect.addFlashAttribute("ok", "Workspace settings for '" + ws + "' updated.");
        return "redirect:/settings?ws=" + ws;
    }

    @PostMapping("/settings/workspace/{ws}/delete")
    public String deleteWorkspaceSettings(@PathVariable String ws, RedirectAttributes redirect) {
        service.deleteWorkspaceSettings(ws);
        redirect.addFlashAttribute("ok", "Workspace settings for '" + ws + "' deleted.");
        return "redirect:/settings";
    }
}
