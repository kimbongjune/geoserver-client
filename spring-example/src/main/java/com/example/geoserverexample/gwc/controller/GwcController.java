package com.example.geoserverexample.gwc.controller;

import com.example.geoserverexample.gwc.dto.CreateBlobStoreRequest;
import com.example.geoserverexample.gwc.dto.CreateGridSetRequest;
import com.example.geoserverexample.gwc.dto.UpsertGwcLayerRequest;
import com.example.geoserverexample.gwc.service.GwcService;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/gwc")
public class GwcController {

    private final GwcService service;

    public GwcController(GwcService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String gwcLayer, Model model) {
        model.addAttribute("gridsets", service.listGridSets());
        model.addAttribute("global", service.getGlobal());
        model.addAttribute("layers", service.listLayers());
        model.addAttribute("blobstores", service.listBlobStores());
        model.addAttribute("selectedGwcLayer", gwcLayer);
        if (gwcLayer != null && !gwcLayer.isEmpty()) {
            GwcLayer detail = service.getGwcLayerBestEffort(gwcLayer);
            if (detail != null) {
                model.addAttribute("gwcLayerDetail", detail);
            }
        }
        var diskQuota = service.getDiskQuotaBestEffort();
        if (diskQuota != null) {
            model.addAttribute("diskQuota", diskQuota);
        }
        return "gwc/index";
    }

    @PostMapping("/blobstores/create")
    public String createBlobStore(CreateBlobStoreRequest form, RedirectAttributes redirect) {
        service.createBlobStore(form.getName(), form.getBaseDirectory(), form.isEnabled());
        redirect.addFlashAttribute("ok", "Blob store '" + form.getName() + "' created.");
        return "redirect:/gwc";
    }

    @PostMapping("/blobstores/{name}/delete")
    public String deleteBlobStore(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteBlobStore(name);
        redirect.addFlashAttribute("ok", "Blob store '" + name + "' deleted.");
        return "redirect:/gwc";
    }

    @PostMapping("/diskquota/update")
    public String updateDiskQuota(@RequestParam(defaultValue = "false") boolean enabled,
                                   @RequestParam int cacheCleanUpFrequency,
                                   RedirectAttributes redirect) {
        service.updateDiskQuota(enabled, cacheCleanUpFrequency);
        redirect.addFlashAttribute("ok", "Disk quota updated (enabled=" + enabled + ").");
        return "redirect:/gwc";
    }

    @PostMapping("/gwclayers/upsert")
    public String upsertGwcLayer(UpsertGwcLayerRequest form, RedirectAttributes redirect) {
        service.upsertGwcLayer(form.getLayerName(), form.isEnabled(), form.getExpireCache());
        redirect.addFlashAttribute("ok", "GWC layer config '" + form.getLayerName() + "' saved.");
        return "redirect:/gwc?gwcLayer=" + form.getLayerName();
    }

    @PostMapping("/gwclayers/{name}/delete")
    public String deleteGwcLayer(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteGwcLayer(name);
        redirect.addFlashAttribute("ok", "GWC layer config '" + name + "' removed (reverted to defaults).");
        return "redirect:/gwc";
    }

    @PostMapping("/gridsets/create")
    public String createGridSet(CreateGridSetRequest form, RedirectAttributes redirect) {
        service.createGridSet(form.getName(), form.getEpsg(), form.getMinx(), form.getMiny(), form.getMaxx(),
                form.getMaxy());
        redirect.addFlashAttribute("ok", "GridSet '" + form.getName() + "' created.");
        return "redirect:/gwc";
    }

    @PostMapping("/gridsets/{name}/delete")
    public String deleteGridSet(@PathVariable String name,
                                 RedirectAttributes redirect) {
        service.deleteGridSet(name);
        redirect.addFlashAttribute("ok", "GridSet '" + name + "' deleted.");
        return "redirect:/gwc";
    }

    @PostMapping("/global")
    public String updateGlobal(@RequestParam int backendTimeout, RedirectAttributes redirect) {
        service.updateGlobal(backendTimeout);
        redirect.addFlashAttribute("ok", "GWC global backendTimeout set to " + backendTimeout);
        return "redirect:/gwc";
    }

    @PostMapping("/layers/seed")
    public String seedLayer(@RequestParam String layer, RedirectAttributes redirect) {
        service.seedLayer(layer);
        redirect.addFlashAttribute("ok", "Seed task started for '" + layer + "' (zoom 0-1).");
        return "redirect:/gwc";
    }

    @PostMapping("/layers/truncate")
    public String truncateLayer(@RequestParam String layer, RedirectAttributes redirect) {
        service.truncateLayer(layer);
        redirect.addFlashAttribute("ok", "Truncated cache for '" + layer + "'.");
        return "redirect:/gwc";
    }

    @PostMapping("/kill-all")
    public String killAll(RedirectAttributes redirect) {
        String result = service.killAll();
        redirect.addFlashAttribute("ok", "killAll(ALL): " + result);
        return "redirect:/gwc";
    }

    @PostMapping("/reload")
    public String reload(RedirectAttributes redirect) {
        var result = service.reload();
        redirect.addFlashAttribute("ok", "Reloaded: " + result.isReloaded() + ", layers=" + result.getLayerCount());
        return "redirect:/gwc";
    }

    @PostMapping("/layers/inspect")
    public String inspectLayer(@RequestParam String layer, RedirectAttributes redirect) {
        redirect.addFlashAttribute("ok", service.inspectLayer(layer));
        return "redirect:/gwc";
    }

    @PostMapping("/filter-update")
    public String filterUpdate(@RequestParam String filterName, RedirectAttributes redirect) {
        String result = service.filterUpdate(filterName);
        redirect.addFlashAttribute("ok", "updateFilterXml(): " + result);
        return "redirect:/gwc";
    }
}
