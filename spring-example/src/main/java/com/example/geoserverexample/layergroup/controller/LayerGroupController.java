package com.example.geoserverexample.layergroup.controller;

import com.example.geoserverexample.layergroup.dto.CreateLayerGroupRequest;
import com.example.geoserverexample.layergroup.service.LayerGroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/layergroups")
public class LayerGroupController {

    private final LayerGroupService service;

    public LayerGroupController(LayerGroupService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ws, Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("selectedWs", ws);
        model.addAttribute("layers", service.listLayers());
        model.addAttribute("globalGroups", service.listGlobalGroups());
        if (ws != null && !ws.isEmpty()) {
            model.addAttribute("wsGroups", service.listByWorkspace(ws));
        }
        return "layergroups/index";
    }

    @PostMapping("/create")
    public String create(CreateLayerGroupRequest form, RedirectAttributes redirect) {
        service.create(form.getName(), form.getTitle(), form.getLayerNames(), form.getWs());
        redirect.addFlashAttribute("ok", "Layer group '" + form.getName() + "' created with " + form.getLayerNames().size() + " layer(s).");
        return "redirect:/layergroups" + (form.getWs() != null && !form.getWs().isEmpty() ? "?ws=" + form.getWs() : "");
    }

    @PostMapping("/{name}/title")
    public String updateTitle(@PathVariable String name, @RequestParam String title,
                               @RequestParam(required = false) String ws,
                               RedirectAttributes redirect) {
        service.updateTitle(name, title, ws);
        redirect.addFlashAttribute("ok", "Layer group '" + name + "' title updated.");
        return "redirect:/layergroups" + (ws != null && !ws.isEmpty() ? "?ws=" + ws : "");
    }

    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name, @RequestParam(required = false) String ws,
                          RedirectAttributes redirect) {
        service.delete(name, ws);
        redirect.addFlashAttribute("ok", "Layer group '" + name + "' deleted.");
        return "redirect:/layergroups" + (ws != null && !ws.isEmpty() ? "?ws=" + ws : "");
    }
}
