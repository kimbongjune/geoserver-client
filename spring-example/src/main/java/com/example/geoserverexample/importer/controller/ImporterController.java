package com.example.geoserverexample.importer.controller;

import com.example.geoserverexample.importer.service.ImporterService;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTarget;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/importer")
public class ImporterController {

    private final ImporterService service;

    public ImporterController(ImporterService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Long importId,
                         @RequestParam(required = false) String ws, Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        List<ImportContextSummary> imports = service.listImports();
        model.addAttribute("imports", imports);
        if (importId != null) {
            ImportContext ctx = service.getImport(importId);
            model.addAttribute("selectedImport", ctx);
            model.addAttribute("selectedWs", ws);
            model.addAttribute("tasks", service.listTasks(importId));
            if (!ctx.getTasks().isEmpty()) {
                long taskId = ctx.getTasks().get(0).getId();
                model.addAttribute("firstTaskId", taskId);
                ImportTarget target = service.getTaskTargetBestEffort(importId, taskId);
                if (target != null) {
                    model.addAttribute("target", target);
                }
            }
            if (ws != null) {
                model.addAttribute("datastores", service.listDatastores(ws));
            }
        }
        return "importer/index";
    }

    @PostMapping("/create")
    public String create(@RequestParam String ws, RedirectAttributes redirect) {
        ImportContext ctx = service.createImport(ws);
        redirect.addFlashAttribute("ok", "Import context #" + ctx.getId() + " created for workspace '" + ws + "'.");
        return "redirect:/importer?importId=" + ctx.getId() + "&ws=" + ws;
    }

    @PostMapping("/{importId}/upload")
    public String upload(@PathVariable long importId, @RequestParam String ws,
                          @RequestParam MultipartFile file, RedirectAttributes redirect) {
        try {
            ImportTask task = service.uploadTask(importId, file);
            redirect.addFlashAttribute("ok", "Uploaded '" + file.getOriginalFilename()
                    + "' as task #" + task.getId() + " (state=" + task.getState() + ").");
        } catch (Exception e) {
            redirect.addFlashAttribute("err", "Upload failed: " + e.getMessage());
        }
        return "redirect:/importer?importId=" + importId + "&ws=" + ws;
    }

    @PostMapping("/{importId}/{taskId}/target")
    public String setTarget(@PathVariable long importId, @PathVariable long taskId,
                             @RequestParam String ws, @RequestParam String storeName,
                             RedirectAttributes redirect) {
        try {
            service.setTaskTarget(importId, taskId, ws, storeName);
            redirect.addFlashAttribute("ok", "Task target set to '" + ws + ":" + storeName + "'.");
        } catch (Exception e) {
            redirect.addFlashAttribute("err", "Failed: " + e.getMessage());
        }
        return "redirect:/importer?importId=" + importId + "&ws=" + ws;
    }

    @PostMapping("/{importId}/run")
    public String run(@PathVariable long importId, @RequestParam(required = false) String ws,
                       RedirectAttributes redirect) {
        ImportContext ctx = service.runImport(importId);
        redirect.addFlashAttribute("ok", "Import run — state is now " + ctx.getState() + ".");
        return "redirect:/importer?importId=" + importId + (ws != null ? "&ws=" + ws : "");
    }

    @PostMapping("/{importId}/delete")
    public String delete(@PathVariable long importId, RedirectAttributes redirect) {
        service.deleteImport(importId);
        redirect.addFlashAttribute("ok", "Import #" + importId + " deleted.");
        return "redirect:/importer";
    }
}
