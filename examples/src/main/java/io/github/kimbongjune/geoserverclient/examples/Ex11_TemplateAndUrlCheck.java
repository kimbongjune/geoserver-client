package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateContent;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheck;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

/**
 * <h2>What this covers</h2>
 * Freemarker Template CRUD (used by WFS/WMS GetFeatureInfo HTML output, among other things) and
 * URL Check rules (allow-lists for outbound URLs GeoServer is permitted to fetch, e.g. for remote
 * WMS/WFS/SLD sources).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>Like {@code StyleContent} for SLD, {@code TemplateContent} wraps raw Freemarker template
 *       text in a thin DTO — templates are themselves a text-templating language, same reasoning
 *       as styles (see {@code CONTRIBUTING.md}).</li>
 *   <li>{@code TemplateManager} has parallel method families for every scope a template can live
 *       at: global, workspace, datastore, feature type, coverage store, coverage. This example
 *       only exercises the global ones ({@code put}/{@code get}/{@code delete}); the workspace/
 *       datastore/etc.-scoped variants follow the identical pattern with extra path parameters.</li>
 *   <li>{@code UrlCheckRequest} takes a name, description, a regex, and an enabled flag directly
 *       via its constructor — one of the few DTOs that's a plain constructor rather than
 *       {@code of(...)}/{@code builder(...)}, since all 4 fields are effectively required together.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone.
 */
public class Ex11_TemplateAndUrlCheck {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex11: Template + UrlCheck ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/2] Creating a global Freemarker template...");
        String templateName = "example_content.ftl";
        client.templates().put(templateName, TemplateContent.of("<#-- example template -->Hello, ${name}!"));
        TemplateContent fetched = client.templates().get(templateName);
        System.out.println("      -> stored body: " + fetched.getBody());
        client.templates().delete(templateName);
        System.out.println("      -> deleted");

        System.out.println("[2/2] Creating a URL Check rule (an allow-list entry for outbound requests)...");
        String checkName = "example_url_check";
        UrlCheck check = client.urlChecks().create(new UrlCheckRequest(
                checkName, "Only allow example.com", "^https?://.*\\.example\\.com/.*$", true));
        System.out.println("      -> created: " + check.getName() + ", regex=" + check.getRegex()
                + ", enabled=" + check.isEnabled());
        client.urlChecks().delete(checkName);
        System.out.println("      -> deleted");

        System.out.println("\nDone.");
        client.close();
    }
}
