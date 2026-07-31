package com.example.geoserverexample.vector.controller;

import com.example.geoserverexample.vector.dto.CreatePostgisRequest;
import com.example.geoserverexample.vector.dto.UpdateFeatureTypeRequest;
import com.example.geoserverexample.vector.dto.UpdateVectorStoreRequest;
import com.example.geoserverexample.vector.dto.UploadVectorRequest;
import com.example.geoserverexample.vector.service.VectorDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/vector")
public class VectorDataController {

    private final VectorDataService service;

    public VectorDataController(VectorDataService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ws,
                         @RequestParam(required = false) String store,
                         Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("selectedWs", ws);
        model.addAttribute("selectedStore", store);
        if (ws != null && !ws.isEmpty()) {
            model.addAttribute("stores", service.listStores(ws));
        }
        if (ws != null && store != null && !store.isEmpty()) {
            model.addAttribute("featureTypes", service.listFeatureTypeRows(ws, store));
            model.addAttribute("available", service.listAvailable(ws, store));
            model.addAttribute("storeDetail", service.getStoreDetail(ws, store));
        }
        return "vector/index";
    }

    @PostMapping("/{ws}/{store}/update")
    public String updateStore(@PathVariable String ws, @PathVariable String store, UpdateVectorStoreRequest form,
                               RedirectAttributes redirect) {
        boolean renaming = service.isRenaming(form.getNewName());
        service.updateStore(ws, store, form.isEnabled(), form.getDescription(), form.getNewName(),
                form.isDefaultStore(), form.isDisableOnConnFailure());
        redirect.addFlashAttribute("ok", "Data store '" + store + "' updated"
                + (renaming ? " (renamed to '" + form.getNewName() + "')" : "") + ".");
        return "redirect:/vector?ws=" + ws + "&store=" + (form.getNewName() != null && !form.getNewName().isBlank() ? form.getNewName() : store);
    }

    @PostMapping("/{ws}/{store}/connection/update")
    public String updateConnection(@PathVariable String ws, @PathVariable String store,
                                    @RequestParam Map<String, String> allParams,
                                    RedirectAttributes redirect) {
        service.updateConnectionParams(ws, store, allParams);
        redirect.addFlashAttribute("ok", "Connection parameters for '" + store + "' updated.");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/postgis/connect")
    public String createPostgis(CreatePostgisRequest form, RedirectAttributes redirect) {
        try {
            service.createPostgisStore(form.getWs(), form.getStoreName(), form.isCreateSampleTable());
            redirect.addFlashAttribute("ok", "PostGIS store '" + form.getStoreName() + "' connected"
                    + (form.isCreateSampleTable() ? " with a sample '" + form.getStoreName() + "_cities' table created." : "."));
        } catch (Exception e) {
            redirect.addFlashAttribute("err", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return "redirect:/vector?ws=" + form.getWs() + "&store=" + form.getStoreName();
    }

    @PostMapping("/upload")
    public String upload(UploadVectorRequest form, RedirectAttributes redirect) {
        try {
            service.uploadFile(form.getWs(), form.getStoreName(), form.getFormat(), form.getFile());
            redirect.addFlashAttribute("ok", "Uploaded '" + form.getFile().getOriginalFilename() + "' into store '" + form.getStoreName() + "'.");
        } catch (Exception e) {
            redirect.addFlashAttribute("err", "Upload failed: " + e.getMessage());
        }
        return "redirect:/vector?ws=" + form.getWs() + "&store=" + form.getStoreName();
    }

    @PostMapping("/{ws}/{store}/publish")
    public String publishExisting(@PathVariable String ws, @PathVariable String store,
                                   @RequestParam String tableName, RedirectAttributes redirect) {
        service.publishExisting(ws, store, tableName);
        redirect.addFlashAttribute("ok", "Published table '" + tableName + "' as a feature type.");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/reset")
    public String resetStore(@PathVariable String ws, @PathVariable String store, RedirectAttributes redirect) {
        service.resetStore(ws, store);
        redirect.addFlashAttribute("ok", "Store '" + store + "' cache reset.");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/delete")
    public String delete(@PathVariable String ws, @PathVariable String store, RedirectAttributes redirect) {
        service.deleteStore(ws, store);
        redirect.addFlashAttribute("ok", "Data store '" + store + "' deleted.");
        return "redirect:/vector?ws=" + ws;
    }

    @PostMapping("/{ws}/{store}/featuretypes/{ft}/enable")
    public String enable(@PathVariable String ws, @PathVariable String store, @PathVariable String ft,
                          @RequestParam boolean enabled, RedirectAttributes redirect) {
        service.enableFeatureType(ws, store, ft, enabled);
        redirect.addFlashAttribute("ok", "Feature type '" + ft + "' " + (enabled ? "enabled" : "disabled") + ".");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/featuretypes/{ft}/update")
    public String updateFt(@PathVariable String ws, @PathVariable String store, @PathVariable String ft,
                            UpdateFeatureTypeRequest form, RedirectAttributes redirect) {
        boolean renaming = service.isRenaming(form.getNewName());
        service.updateFeatureType(ws, store, ft, form.getNewName(), form.getTitle(), form.getSrs(),
                form.getProjectionPolicy(), form.getMaxFeatures());
        redirect.addFlashAttribute("ok", "Feature type '" + ft + "' updated"
                + (renaming ? " (renamed to '" + form.getNewName() + "')" : "") + ".");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/featuretypes/{ft}/delete")
    public String deleteFt(@PathVariable String ws, @PathVariable String store, @PathVariable String ft,
                            RedirectAttributes redirect) {
        service.deleteFeatureType(ws, store, ft);
        redirect.addFlashAttribute("ok", "Feature type '" + ft + "' deleted.");
        return "redirect:/vector?ws=" + ws + "&store=" + store;
    }
}
