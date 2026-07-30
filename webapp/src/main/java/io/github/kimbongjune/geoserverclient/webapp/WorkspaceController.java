package io.github.kimbongjune.geoserverclient.webapp;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Full CRUD over GeoServer Workspaces, driven entirely by the {@link GeoServerClient} bean —
 * this controller contains no HTTP/JSON/XML handling of its own; that's all inside the library.
 * Every action redirects back to the list view (POST-redirect-GET) so a page refresh never
 * re-submits a form.
 */
@Controller
public class WorkspaceController {

    private final GeoServerClient client;

    public WorkspaceController(GeoServerClient client) {
        this.client = client;
    }

    @GetMapping("/")
    public String list(Model model) {
        List<WorkspaceSummary> workspaces = client.workspaces().list();
        model.addAttribute("workspaces", workspaces);
        return "workspaces";
    }

    @PostMapping("/workspaces")
    public String create(@RequestParam String name, @RequestParam(required = false) Boolean isolated,
                          RedirectAttributes redirectAttributes) {
        try {
            client.workspaces().create(
                    CreateWorkspaceRequest.builder(name).isolated(isolated != null && isolated).build());
            redirectAttributes.addFlashAttribute("message", "Created workspace: " + name);
        } catch (GeoServerException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/workspaces/{name}/delete")
    public String delete(@PathVariable String name, RedirectAttributes redirectAttributes) {
        try {
            client.workspaces().delete(name, true); // recurse=true: also remove anything inside it
            redirectAttributes.addFlashAttribute("message", "Deleted workspace: " + name);
        } catch (GeoServerException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }
}
