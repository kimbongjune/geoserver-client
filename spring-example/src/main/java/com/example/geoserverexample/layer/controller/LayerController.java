package com.example.geoserverexample.layer.controller;

import com.example.geoserverexample.layer.dto.UpdateLayerRequest;
import com.example.geoserverexample.layer.service.LayerService;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/layers")
public class LayerController {

    private final LayerService service;

    public LayerController(LayerService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("layers", service.listLayers());
        return "layers/list";
    }

    @GetMapping("/{name}")
    public String detail(@PathVariable String name, Model model) {
        Layer layer = service.getLayer(name);
        model.addAttribute("layer", layer);
        model.addAttribute("layerName", name);
        model.addAttribute("availableStyles", service.listStylesByLayer(name));

        LayerService.ResourceEnrichment enrichment = service.enrichResource(layer);
        if (enrichment != null) {
            model.addAttribute("resourceSrs", enrichment.getSrs());
            if (enrichment.getBbox() != null) {
                model.addAttribute("resourceBbox", enrichment.getBbox());
            }
        }
        return "layers/detail";
    }

    @PostMapping("/{name}/queryable")
    public String setQueryable(@PathVariable String name, @RequestParam boolean queryable,
                                RedirectAttributes redirect) {
        service.setQueryable(name, queryable);
        redirect.addFlashAttribute("ok", "Layer '" + name + "' queryable=" + queryable);
        return "redirect:/layers/" + name;
    }

    @PostMapping("/{name}/add-style")
    public String addStyle(@PathVariable String name, @RequestParam String styleName,
                            @RequestParam(defaultValue = "false") boolean setDefault,
                            RedirectAttributes redirect) {
        service.addStyle(name, styleName, setDefault);
        redirect.addFlashAttribute("ok", "Added style '" + styleName + "' to layer '" + name + "'"
                + (setDefault ? " (set as default)." : "."));
        return "redirect:/layers/" + name;
    }

    @PostMapping("/{name}/update")
    public String update(@PathVariable String name, UpdateLayerRequest form, RedirectAttributes redirect) {
        service.updateLayer(name, form.isOpaque(), form.isEnabled(), form.isAdvertised(), form.getPath(),
                form.getAttributionTitle(), form.getAttributionHref(), form.getDefaultStyleName(),
                form.getDefaultWMSInterpolationMethod());
        redirect.addFlashAttribute("ok", "Layer '" + name + "' updated.");
        return "redirect:/layers/" + name;
    }
}
