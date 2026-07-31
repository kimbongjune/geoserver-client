package com.example.geoserverexample.template.controller;

import com.example.geoserverexample.template.dto.CreateTemplateRequest;
import com.example.geoserverexample.template.dto.CreateUrlCheckRequest;
import com.example.geoserverexample.template.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ws, Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("selectedWs", ws);
        model.addAttribute("globalTemplates", service.listGlobalTemplates());
        if (ws != null && !ws.isEmpty()) {
            model.addAttribute("wsTemplates", service.listByWorkspace(ws));
        }
        model.addAttribute("urlChecks", service.listUrlChecks());
        return "templates/index";
    }

    @PostMapping("/create")
    public String create(CreateTemplateRequest form, RedirectAttributes redirect) {
        service.create(form.getName(), form.getBody(), form.getWs());
        redirect.addFlashAttribute("ok", "Template '" + form.getName() + "' saved.");
        return "redirect:/templates" + (form.getWs() != null && !form.getWs().isEmpty() ? "?ws=" + form.getWs() : "");
    }

    @GetMapping("/{name}/view")
    @ResponseBody
    public String view(@PathVariable String name, @RequestParam(required = false) String ws) {
        return service.getBody(name, ws);
    }

    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name, @RequestParam(required = false) String ws,
                          RedirectAttributes redirect) {
        service.delete(name, ws);
        redirect.addFlashAttribute("ok", "Template '" + name + "' deleted.");
        return "redirect:/templates" + (ws != null && !ws.isEmpty() ? "?ws=" + ws : "");
    }

    @PostMapping("/urlchecks/create")
    public String createUrlCheck(CreateUrlCheckRequest form, RedirectAttributes redirect) {
        service.createUrlCheck(form.getName(), form.getDescription(), form.getRegex(), form.isEnabled());
        redirect.addFlashAttribute("ok", "URL check '" + form.getName() + "' created.");
        return "redirect:/templates";
    }

    @PostMapping("/urlchecks/{name}/delete")
    public String deleteUrlCheck(@PathVariable String name, RedirectAttributes redirect) {
        service.deleteUrlCheck(name);
        redirect.addFlashAttribute("ok", "URL check '" + name + "' deleted.");
        return "redirect:/templates";
    }
}
