package com.example.geoserverexample.cascading.controller;

import com.example.geoserverexample.cascading.dto.CreateWmsStoreRequest;
import com.example.geoserverexample.cascading.dto.CreateWmtsStoreRequest;
import com.example.geoserverexample.cascading.dto.EnableWmsLayerRequest;
import com.example.geoserverexample.cascading.dto.EnableWmtsLayerRequest;
import com.example.geoserverexample.cascading.dto.PublishWmsLayerRequest;
import com.example.geoserverexample.cascading.dto.PublishWmtsLayerRequest;
import com.example.geoserverexample.cascading.dto.UpdateWmsLayerRequest;
import com.example.geoserverexample.cascading.dto.UpdateWmsStoreRequest;
import com.example.geoserverexample.cascading.dto.UpdateWmtsLayerRequest;
import com.example.geoserverexample.cascading.dto.UpdateWmtsStoreRequest;
import com.example.geoserverexample.cascading.service.CascadingService;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cascading")
public class CascadingController {

    private final CascadingService service;

    public CascadingController(CascadingService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ws, Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("selectedWs", ws);
        if (ws != null && !ws.isEmpty()) {
            List<WmsStore> wmsStores = service.listWmsStores(ws);
            List<WmtsStore> wmtsStores = service.listWmtsStores(ws);
            model.addAttribute("wmsStores", wmsStores);
            model.addAttribute("wmtsStores", wmtsStores);
            model.addAttribute("wmsLayers", service.listWmsLayerRows(ws, wmsStores));
            model.addAttribute("wmtsLayers", service.listWmtsLayerRows(ws, wmtsStores));
        }
        return "cascading/index";
    }

    @PostMapping("/wms/store")
    public String createWmsStore(CreateWmsStoreRequest form, RedirectAttributes redirect) {
        service.createWmsStore(form.getWs(), form.getStoreName(), form.getCapabilitiesUrl());
        redirect.addFlashAttribute("ok", "WMS store '" + form.getStoreName() + "' created.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wms/store/{storeName}/update")
    public String updateWmsStore(@PathVariable String storeName, UpdateWmsStoreRequest form,
                                  RedirectAttributes redirect) {
        service.updateWmsStore(form.getWs(), storeName, form.isEnabled(), form.getCapabilitiesUrl(), form.getUser(),
                form.getPassword(), form.getConnectTimeout(), form.getReadTimeout(), form.isDisableOnConnFailure());
        redirect.addFlashAttribute("ok", "WMS store '" + storeName + "' updated (enabled=" + form.isEnabled() + ").");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wms/store/{storeName}/delete")
    public String deleteWmsStore(@RequestParam String ws, @PathVariable String storeName, RedirectAttributes redirect) {
        service.deleteWmsStore(ws, storeName);
        redirect.addFlashAttribute("ok", "WMS store '" + storeName + "' deleted.");
        return "redirect:/cascading?ws=" + ws;
    }

    @PostMapping("/wms/layer")
    public String publishWmsLayer(PublishWmsLayerRequest form, RedirectAttributes redirect) {
        service.publishWmsLayer(form.getWs(), form.getStoreName(), form.getLayerName(), form.getNativeName());
        redirect.addFlashAttribute("ok", "Cascaded WMS layer '" + form.getLayerName() + "' published.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wms/layer/{layerName}/enable")
    public String enableWmsLayer(@PathVariable String layerName, EnableWmsLayerRequest form,
                                  RedirectAttributes redirect) {
        service.enableWmsLayer(form.getWs(), form.getStoreName(), layerName, form.isEnabled());
        redirect.addFlashAttribute("ok", "Cascaded WMS layer '" + layerName + "' " + (form.isEnabled() ? "enabled" : "disabled") + ".");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wms/layer/{layerName}/update")
    public String updateWmsLayer(@PathVariable String layerName, UpdateWmsLayerRequest form,
                                  RedirectAttributes redirect) {
        service.updateWmsLayer(form.getWs(), form.getStoreName(), layerName, form.getTitle(), form.getDescription(),
                form.getMinScale(), form.getMaxScale());
        redirect.addFlashAttribute("ok", "Cascaded WMS layer '" + layerName + "' metadata updated.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wms/layer/{layerName}/delete")
    public String deleteWmsLayer(@RequestParam String ws, @RequestParam String storeName,
                                  @PathVariable String layerName, RedirectAttributes redirect) {
        service.deleteWmsLayer(ws, storeName, layerName);
        redirect.addFlashAttribute("ok", "Cascaded WMS layer '" + layerName + "' deleted.");
        return "redirect:/cascading?ws=" + ws;
    }

    @PostMapping("/wmts/store")
    public String createWmtsStore(CreateWmtsStoreRequest form, RedirectAttributes redirect) {
        service.createWmtsStore(form.getWs(), form.getStoreName(), form.getCapabilitiesUrl());
        redirect.addFlashAttribute("ok", "WMTS store '" + form.getStoreName() + "' created.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wmts/store/{storeName}/update")
    public String updateWmtsStore(@PathVariable String storeName, UpdateWmtsStoreRequest form,
                                   RedirectAttributes redirect) {
        service.updateWmtsStore(form.getWs(), storeName, form.isEnabled(), form.getCapabilitiesUrl(), form.getUser(),
                form.getPassword(), form.getConnectTimeout(), form.getReadTimeout(), form.isDisableOnConnFailure());
        redirect.addFlashAttribute("ok", "WMTS store '" + storeName + "' updated (enabled=" + form.isEnabled() + ").");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wmts/store/{storeName}/delete")
    public String deleteWmtsStore(@RequestParam String ws, @PathVariable String storeName, RedirectAttributes redirect) {
        service.deleteWmtsStore(ws, storeName);
        redirect.addFlashAttribute("ok", "WMTS store '" + storeName + "' deleted.");
        return "redirect:/cascading?ws=" + ws;
    }

    @PostMapping("/wmts/layer")
    public String publishWmtsLayer(PublishWmtsLayerRequest form, RedirectAttributes redirect) {
        service.publishWmtsLayer(form.getWs(), form.getStoreName(), form.getLayerName(), form.getNativeName());
        redirect.addFlashAttribute("ok", "Cascaded WMTS layer '" + form.getLayerName() + "' published.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wmts/layer/{layerName}/enable")
    public String enableWmtsLayer(@PathVariable String layerName, EnableWmtsLayerRequest form,
                                   RedirectAttributes redirect) {
        service.enableWmtsLayer(form.getWs(), form.getStoreName(), layerName, form.isEnabled());
        redirect.addFlashAttribute("ok", "Cascaded WMTS layer '" + layerName + "' " + (form.isEnabled() ? "enabled" : "disabled") + ".");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wmts/layer/{layerName}/update")
    public String updateWmtsLayer(@PathVariable String layerName, UpdateWmtsLayerRequest form,
                                   RedirectAttributes redirect) {
        service.updateWmtsLayer(form.getWs(), form.getStoreName(), layerName, form.getTitle(), form.getDescription());
        redirect.addFlashAttribute("ok", "Cascaded WMTS layer '" + layerName + "' metadata updated.");
        return "redirect:/cascading?ws=" + form.getWs();
    }

    @PostMapping("/wmts/layer/{layerName}/delete")
    public String deleteWmtsLayer(@RequestParam String ws, @RequestParam String storeName,
                                   @PathVariable String layerName, RedirectAttributes redirect) {
        service.deleteWmtsLayer(ws, storeName, layerName);
        redirect.addFlashAttribute("ok", "Cascaded WMTS layer '" + layerName + "' deleted.");
        return "redirect:/cascading?ws=" + ws;
    }
}
