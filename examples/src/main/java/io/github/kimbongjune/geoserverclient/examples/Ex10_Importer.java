package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

/**
 * <h2>What this covers</h2>
 * The Importer extension's basic workflow: creating an import context targeting a workspace, and
 * listing/cleaning it up. The Importer is GeoServer's bulk-data-import subsystem (drag-and-drop a
 * folder of shapefiles in the Admin UI uses this same API underneath).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>An import context is created empty — you'd normally follow this with
 *       {@code uploadTask(importId, file, contentType)} to attach one or more files, then
 *       {@code runImport(importId)} to actually execute it. This example only shows the
 *       context lifecycle itself (create → list → delete) to keep it self-contained and fast;
 *       see {@code ImporterManager}'s Javadoc for the full task/transform API if you need the
 *       complete upload-and-run flow.</li>
 *   <li>Requires the importer-plugin extension to be installed — it is, in this repo's Docker
 *       image (see {@code docker-compose.yml}, {@code STABLE_EXTENSIONS=importer-plugin}).</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone.
 */
public class Ex10_Importer {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex10: Importer ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_importer_ws";
        System.out.println("[setup] Creating target workspace '" + ws + "'...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        System.out.println("[1/3] Creating an import context targeting that workspace...");
        ImportContext ctx = client.importer().createImport(ws);
        long id = ctx.getId();
        System.out.println("      -> created import #" + id);

        System.out.println("[2/3] Listing all import contexts...");
        List<ImportContextSummary> all = client.importer().listImports();
        System.out.println("      -> " + all.size() + " total import context(s) currently exist");

        System.out.println("[3/3] Deleting the import context we created...");
        client.importer().deleteImport(id);
        System.out.println("      -> deleted");

        System.out.println("\nCleaning up the workspace...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }
}
