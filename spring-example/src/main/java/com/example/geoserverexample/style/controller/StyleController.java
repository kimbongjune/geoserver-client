package com.example.geoserverexample.style.controller;

import com.example.geoserverexample.style.dto.CreateRawStyleRequest;
import com.example.geoserverexample.style.dto.GenerateStyleRequest;
import com.example.geoserverexample.style.service.StyleService;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/styles")
public class StyleController {

    private final StyleService service;

    public StyleController(StyleService service) {
        this.service = service;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String ws, Model model) {
        model.addAttribute("workspaces", service.listWorkspaces());
        model.addAttribute("selectedWs", ws);
        model.addAttribute("globalStyles", service.listGlobalStyles());
        if (ws != null && !ws.isEmpty()) {
            model.addAttribute("wsStyles", service.listByWorkspace(ws));
        }
        return "styles/index";
    }

    @PostMapping("/raw")
    public String createRaw(CreateRawStyleRequest form, RedirectAttributes redirect) {
        service.createRaw(form.getName(), form.getSld(), form.getWs());
        redirect.addFlashAttribute("ok", "Style '" + form.getName() + "' created from raw SLD.");
        return "redirect:/styles" + (form.getWs() != null && !form.getWs().isEmpty() ? "?ws=" + form.getWs() : "");
    }

    @PostMapping("/generate")
    public String generate(GenerateStyleRequest form, RedirectAttributes redirect) {
        service.generate(form.getName(), form.getLayerName(), form.getSymbolizer(), form.getColor(), form.getSize(),
                form.getWs());
        redirect.addFlashAttribute("ok", "Style '" + form.getName() + "' generated via SldBuilder ("
                + form.getSymbolizer() + ", " + form.getColor() + ").");
        return "redirect:/styles" + (form.getWs() != null && !form.getWs().isEmpty() ? "?ws=" + form.getWs() : "");
    }

    @GetMapping("/{name}/edit")
    public String edit(@PathVariable String name, @RequestParam(required = false) String ws, Model model) {
        StyleContent content = service.getSldContent(name, ws);
        model.addAttribute("name", name);
        model.addAttribute("selectedWs", ws);
        model.addAttribute("sldBody", content.getSldBody());
        return "styles/edit";
    }

    @GetMapping(value = "/{name}/sld", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String viewSld(@PathVariable String name, @RequestParam(required = false) String ws) {
        // Explicit text/plain — without it, opening this in a new tab lets the browser treat the
        // raw SLD as (X)HTML and silently strip every tag, leaving only bare text nodes visible.
        return service.getSldContent(name, ws).getSldBody();
    }

    @PostMapping("/{name}/update-raw")
    public String updateRaw(@PathVariable String name, @RequestParam String sld,
                             @RequestParam(required = false) String ws, RedirectAttributes redirect) {
        service.updateRaw(name, sld, ws);
        redirect.addFlashAttribute("ok", "Style '" + name + "' updated.");
        return "redirect:/styles" + (ws != null && !ws.isEmpty() ? "?ws=" + ws : "");
    }

    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name, @RequestParam(required = false) String ws,
                          RedirectAttributes redirect) {
        service.delete(name, ws);
        redirect.addFlashAttribute("ok", "Style '" + name + "' deleted.");
        return "redirect:/styles" + (ws != null && !ws.isEmpty() ? "?ws=" + ws : "");
    }
}
