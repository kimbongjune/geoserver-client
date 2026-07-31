package com.example.geoserverexample.raster.controller;

import com.example.geoserverexample.raster.dto.UpdateCoverageRequest;
import com.example.geoserverexample.raster.dto.UpdateRasterStoreRequest;
import com.example.geoserverexample.raster.dto.UploadRasterRequest;
import com.example.geoserverexample.raster.service.RasterDataService;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/raster")
public class RasterDataController {

    private final RasterDataService service;

    public RasterDataController(RasterDataService service) {
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
            model.addAttribute("coverages", service.listCoverageRows(ws, store));
            model.addAttribute("storeDetail", service.getStoreDetail(ws, store));
            model.addAttribute("storeType", service.getStoreDetail(ws, store).getType());
            List<GranuleCollection.Granule> granules = service.listGranulesBestEffort(ws, store);
            if (granules != null) {
                model.addAttribute("granules", granules);
            }
        }
        return "raster/index";
    }

    @PostMapping("/{ws}/{store}/update")
    public String updateStore(@PathVariable String ws, @PathVariable String store, UpdateRasterStoreRequest form,
                               RedirectAttributes redirect) {
        boolean renaming = service.isRenaming(form.getNewName());
        service.updateStore(ws, store, form.isEnabled(), form.getDescription(), form.getNewName(), form.getUrl(),
                form.isDisableOnConnFailure());
        redirect.addFlashAttribute("ok", "Coverage store '" + store + "' updated"
                + (renaming ? " (renamed to '" + form.getNewName() + "')" : "") + ".");
        return "redirect:/raster?ws=" + ws + "&store=" + (form.getNewName() != null && !form.getNewName().isBlank() ? form.getNewName() : store);
    }

    @PostMapping("/{ws}/{store}/coverages/{cov}/enable")
    public String enableCoverage(@PathVariable String ws, @PathVariable String store, @PathVariable String cov,
                                  @RequestParam boolean enabled, RedirectAttributes redirect) {
        service.enableCoverage(ws, store, cov, enabled);
        redirect.addFlashAttribute("ok", "Coverage '" + cov + "' " + (enabled ? "enabled" : "disabled") + ".");
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/coverages/{cov}/update")
    public String updateCoverage(@PathVariable String ws, @PathVariable String store, @PathVariable String cov,
                                  UpdateCoverageRequest form, RedirectAttributes redirect) {
        boolean renaming = service.isRenaming(form.getNewName());
        service.updateCoverage(ws, store, cov, form.getNewName(), form.getTitle(), form.getSrs(),
                form.getProjectionPolicy(), form.isAdvertised(), form.getDefaultInterpolationMethod());
        redirect.addFlashAttribute("ok", "Coverage '" + cov + "' updated"
                + (renaming ? " (renamed to '" + form.getNewName() + "')" : "") + ".");
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/coverages/{cov}/delete")
    public String deleteCoverage(@PathVariable String ws, @PathVariable String store, @PathVariable String cov,
                                  RedirectAttributes redirect) {
        service.deleteCoverage(ws, store, cov);
        redirect.addFlashAttribute("ok", "Coverage '" + cov + "' deleted.");
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/upload")
    public String upload(UploadRasterRequest form, RedirectAttributes redirect) {
        try {
            service.uploadFile(form.getWs(), form.getStoreName(), form.getFormat(), form.getFile());
            redirect.addFlashAttribute("ok", "Uploaded '" + form.getFile().getOriginalFilename()
                    + "' as format '" + form.getFormat() + "' into store '" + form.getStoreName() + "'.");
        } catch (Exception e) {
            redirect.addFlashAttribute("err", "Upload failed: " + e.getMessage());
        }
        return "redirect:/raster?ws=" + form.getWs() + "&store=" + form.getStoreName();
    }

    @PostMapping("/{ws}/{store}/harvest")
    public String harvest(@PathVariable String ws, @PathVariable String store,
                           @RequestParam String format,
                           @RequestParam MultipartFile file,
                           RedirectAttributes redirect) {
        try {
            service.harvest(ws, store, format, file);
            redirect.addFlashAttribute("ok", "Harvested '" + file.getOriginalFilename() + "' into the mosaic.");
        } catch (Exception e) {
            redirect.addFlashAttribute("err", "Harvest failed: " + e.getMessage());
        }
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/granules/{granuleId}/delete")
    public String deleteGranule(@PathVariable String ws, @PathVariable String store, @PathVariable String granuleId,
                                 RedirectAttributes redirect) {
        service.deleteGranule(ws, store, granuleId);
        redirect.addFlashAttribute("ok", "Granule '" + granuleId + "' deleted.");
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/reset")
    public String reset(@PathVariable String ws, @PathVariable String store, RedirectAttributes redirect) {
        service.resetStore(ws, store);
        redirect.addFlashAttribute("ok", "Store '" + store + "' cache reset.");
        return "redirect:/raster?ws=" + ws + "&store=" + store;
    }

    @PostMapping("/{ws}/{store}/delete")
    public String delete(@PathVariable String ws, @PathVariable String store, RedirectAttributes redirect) {
        service.deleteStore(ws, store);
        redirect.addFlashAttribute("ok", "Coverage store '" + store + "' deleted.");
        return "redirect:/raster?ws=" + ws;
    }
}
