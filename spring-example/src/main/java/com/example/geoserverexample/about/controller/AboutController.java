package com.example.geoserverexample.about.controller;

import com.example.geoserverexample.about.service.AboutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AboutController {

    private final AboutService service;

    public AboutController(AboutService service) {
        this.service = service;
    }

    @GetMapping("/about")
    public String index(@RequestParam(required = false) String dir, Model model) {
        model.addAttribute("version", service.getVersion());
        model.addAttribute("status", service.getStatus());
        model.addAttribute("dir", dir);

        boolean isFile = false;
        String fileContent = null;
        if (dir != null && !dir.isEmpty()) {
            isFile = service.isFile(dir);
        }
        if (isFile) {
            fileContent = service.safeReadAsText(dir);
        } else {
            model.addAttribute("resources", service.listResources(dir));
        }
        model.addAttribute("isFile", isFile);
        model.addAttribute("fileContent", fileContent);

        model.addAttribute("recentRequests", service.recentRequests());
        model.addAttribute("fonts", service.fontsCount());
        model.addAttribute("rootTemplates", service.rootTemplatesCount());
        model.addAttribute("transformAvailable", service.transformAvailable());
        return "about/index";
    }

    @PostMapping("/about/reset")
    public String reset(RedirectAttributes redirect) {
        service.reset();
        redirect.addFlashAttribute("ok", "reset(): caches cleared.");
        return "redirect:/about";
    }

    @PostMapping("/about/reload")
    public String reload(RedirectAttributes redirect) {
        service.reload();
        redirect.addFlashAttribute("ok", "reload(): catalog + config reloaded from the data directory.");
        return "redirect:/about";
    }
}
