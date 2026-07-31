package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateContent;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheck;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * Freemarker Template CRUD at <em>every</em> scope it can live at — global, workspace, datastore,
 * featuretype, coveragestore, and coverage — plus URL Check rules (allow-lists for outbound URLs
 * GeoServer is permitted to fetch, e.g. for remote WMS/WFS/SLD sources).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>Like {@code StyleContent} for SLD, {@code TemplateContent} wraps raw Freemarker template
 *       text in a thin DTO — templates are themselves a text-templating language, same reasoning
 *       as styles (see {@code CONTRIBUTING.md}).</li>
 *   <li>{@code TemplateManager} has parallel method families for every scope a template can live
 *       at: global, workspace, datastore, feature type, coverage store, coverage. They all follow
 *       the identical {@code put}/{@code get}/{@code list}/{@code delete} pattern, just with extra
 *       path parameters the deeper the scope goes.</li>
 *   <li>{@code UrlCheckRequest} takes a name, description, a regex, and an enabled flag directly
 *       via its constructor — one of the few DTOs that's a plain constructor rather than
 *       {@code of(...)}/{@code builder(...)}, since all 4 fields are effectively required together.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Uses the bundled {@code sample.tif}
 * and shapefile ZIP to have a real datastore/featuretype/coveragestore/coverage to attach templates to.
 */
public class Ex11_TemplateAndUrlCheck {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex11: Template + UrlCheck ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/7] Global template — put, get, delete...");
        String templateName = "example_content.ftl";
        client.templates().put(templateName, TemplateContent.of("<#-- example template -->Hello, ${name}!"));
        TemplateContent fetched = client.templates().get(templateName);
        System.out.println("      -> stored body: " + fetched.getBody());
        System.out.println("      -> list(): " + client.templates().list().size() + " global template(s)");
        client.templates().delete(templateName);
        System.out.println("      -> deleted");

        String ws = "example_template_ws";
        System.out.println("\n[setup] Creating workspace + a real datastore/featuretype and coveragestore/coverage...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String shpStore = "example_shp";
        client.datastores().uploadFile(ws, shpStore, "file", "shp",
                new File("src/main/resources/ne_110m_admin_0_countries.zip"), "first", null, null);
        String featureTypeName = client.featureTypes().list(ws, shpStore).get(0).getName();
        String covStore = "example_geotiff";
        client.coverageStores().uploadFile(ws, covStore, "file", "geotiff", extractBundledSample(), "first", null, null);
        List<CoverageSummary> coverages = client.coverages().list(ws, covStore);
        String coverageName = coverages.get(0).getName();
        System.out.println("      -> ready: " + ws + "/" + shpStore + "/" + featureTypeName
                + " and " + ws + "/" + covStore + "/" + coverageName);

        System.out.println("\n[2/7] Workspace-level template...");
        client.templates().putByWorkspace(ws, templateName, TemplateContent.of("Workspace-level template"));
        System.out.println("      -> get: " + client.templates().getByWorkspace(ws, templateName).getBody());
        System.out.println("      -> list: " + client.templates().listByWorkspace(ws).size());
        client.templates().deleteByWorkspace(ws, templateName);
        System.out.println("      -> deleted");

        System.out.println("\n[3/7] DataStore-level template...");
        client.templates().putByDatastore(ws, shpStore, templateName, TemplateContent.of("DataStore-level template"));
        System.out.println("      -> get: " + client.templates().getByDatastore(ws, shpStore, templateName).getBody());
        System.out.println("      -> list: " + client.templates().listByDatastore(ws, shpStore).size());
        client.templates().deleteByDatastore(ws, shpStore, templateName);
        System.out.println("      -> deleted");

        System.out.println("\n[4/7] FeatureType-level template...");
        client.templates().putByFeatureType(ws, shpStore, featureTypeName, templateName,
                TemplateContent.of("FeatureType-level template"));
        System.out.println("      -> get: "
                + client.templates().getByFeatureType(ws, shpStore, featureTypeName, templateName).getBody());
        System.out.println("      -> list: " + client.templates().listByFeatureType(ws, shpStore, featureTypeName).size());
        client.templates().deleteByFeatureType(ws, shpStore, featureTypeName, templateName);
        System.out.println("      -> deleted");

        System.out.println("\n[5/7] CoverageStore-level template...");
        client.templates().putByCoverageStore(ws, covStore, templateName, TemplateContent.of("CoverageStore-level template"));
        System.out.println("      -> get: " + client.templates().getByCoverageStore(ws, covStore, templateName).getBody());
        System.out.println("      -> list: " + client.templates().listByCoverageStore(ws, covStore).size());
        client.templates().deleteByCoverageStore(ws, covStore, templateName);
        System.out.println("      -> deleted");

        System.out.println("\n[6/7] Coverage-level template...");
        client.templates().putByCoverage(ws, covStore, coverageName, templateName,
                TemplateContent.of("Coverage-level template"));
        System.out.println("      -> get: "
                + client.templates().getByCoverage(ws, covStore, coverageName, templateName).getBody());
        System.out.println("      -> list: " + client.templates().listByCoverage(ws, covStore, coverageName).size());
        client.templates().deleteByCoverage(ws, covStore, coverageName, templateName);
        System.out.println("      -> deleted");

        System.out.println("\n[7/7] URL Check — an allow-list entry for outbound requests...");
        String checkName = "example_url_check";
        UrlCheck check = client.urlChecks().create(new UrlCheckRequest(
                checkName, "Only allow example.com", "^https?://.*\\.example\\.com/.*$", true));
        System.out.println("      -> created: " + check.getName() + ", regex=" + check.getRegex()
                + ", enabled=" + check.isEnabled());
        client.urlChecks().delete(checkName);
        System.out.println("      -> deleted");

        System.out.println("\nCleaning up the workspace...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }

    private static File extractBundledSample() throws Exception {
        File tmp = File.createTempFile("geoserver-client-example-sample", ".tif");
        tmp.deleteOnExit();
        try (InputStream in = Ex11_TemplateAndUrlCheck.class.getClassLoader().getResourceAsStream("sample.tif");
             OutputStream out = new FileOutputStream(tmp)) {
            if (in == null) {
                throw new IllegalStateException("sample.tif not found on classpath — did the examples build correctly?");
            }
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
        return tmp;
    }
}
