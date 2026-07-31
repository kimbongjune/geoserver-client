package io.github.kimbongjune.geoserverclient.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportLayer;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTarget;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTaskUpdate;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransform;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransformChain;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * The Importer extension's full task/transform lifecycle — not just the empty-context shell, but a
 * real end-to-end import of a real file: create context → upload a real GeoJSON →
 * inspect/adjust its task, target, and layer → add and inspect a transform → run the import → the
 * resulting layer is real and queryable. The Importer is GeoServer's bulk-data-import subsystem
 * (drag-and-drop a folder of files in the Admin UI uses this same API underneath) and is also the
 * REST-native way to ingest formats the DataStore file-upload endpoint doesn't accept directly,
 * like plain GeoJSON.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code uploadTask(importId, file, contentType)} both uploads the file <em>and</em> creates
 *       the task in one call. <b>Content-Type matters</b>: passing {@code "application/json"} for a
 *       {@code .geojson}/{@code .json} file causes a 400 with an empty body and no task created —
 *       confirmed directly against the REST API — because that content type routes to a different
 *       handler on this endpoint. Use {@code "application/octet-stream"} (or {@code "text/plain"})
 *       for the raw file bytes regardless of the file's actual format.</li>
 *   <li>GeoJSON has no store of its own (unlike Shapefile/GeoTIFF, which auto-create a file-based
 *       store) — the task starts in an {@code ERROR} state ("No target store for task") until you
 *       call {@code setTaskTarget(...)} pointing it at a real datastore. This example targets a real
 *       PostGIS store, the same pattern as Ex02.</li>
 *   <li>{@code ImportTarget} has no public setter for its {@code dataStore} field (only getters —
 *       it's populated by Jackson reflection when deserializing a GET response). To build one to
 *       send in a PUT, this example round-trips a small JSON string through Jackson's
 *       {@code ObjectMapper} directly rather than the DTO's (nonexistent) builder API.</li>
 *   <li>{@code addTransform(...)} attaches a transform (here, {@link ImportTransform#reproject}) to
 *       a task <em>before</em> running the import — transforms only apply during the run. The
 *       transform "id" used by {@code getTransform}/{@code updateTransform}/{@code deleteTransform}
 *       is just its 0-based position in the chain array; {@link ImportTransform} itself has no
 *       persistent id field.</li>
 *   <li>Requires the importer-plugin extension to be installed — it is, in this repo's Docker
 *       image (see {@code docker-compose.yml}, {@code STABLE_EXTENSIONS=importer-plugin}).</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Uses the bundled real
 * {@code ne_110m_populated_places.geojson} (Natural Earth, public domain).
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
        System.out.println("[setup] Creating target workspace '" + ws + "' and a real PostGIS target store...");
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String pgStoreName = "importer_target_pg";
        client.datastores().create(ws, CreateDataStoreRequest.builder(pgStoreName)
                .type("PostGIS")
                .connectionParam("host", "postgres")
                .connectionParam("port", "5432")
                .connectionParam("database", "geoserver")
                .connectionParam("schema", "public")
                .connectionParam("user", "geoserver")
                .connectionParam("passwd", "geoserver")
                .connectionParam("dbtype", "postgis")
                .build());

        System.out.println("[1/8] Creating an import context targeting that workspace...");
        ImportContext ctx = client.importer().createImport(ws);
        long importId = ctx.getId();
        System.out.println("      -> created import #" + importId);

        System.out.println("[2/8] Uploading a real GeoJSON file as a task (upload + task creation in one call)...");
        File geojson = new File("src/main/resources/ne_110m_populated_places.geojson");
        ImportTask task = client.importer().uploadTask(importId, geojson, "application/octet-stream");
        long taskId = task.getId();
        System.out.println("      -> task #" + taskId + " created, state=" + task.getState()
                + " (ERROR is expected here — GeoJSON has no store of its own yet)");

        System.out.println("[3/8] Inspecting the task and pointing it at the real PostGIS store...");
        ImportTask fetchedTask = client.importer().getTask(importId, taskId);
        System.out.println("      -> getTask(): " + fetchedTask);
        ObjectMapper jackson = new ObjectMapper();
        String targetJson = "{\"dataStore\":{\"name\":\"" + pgStoreName + "\",\"workspace\":{\"name\":\"" + ws + "\"}}}";
        ImportTarget realTarget = jackson.readValue(targetJson, ImportTarget.class);
        client.importer().setTaskTarget(importId, taskId, realTarget);
        ImportTarget confirmedTarget = client.importer().getTaskTarget(importId, taskId);
        System.out.println("      -> getTaskTarget() after setTaskTarget(): " + confirmedTarget);
        ImportLayer layer = client.importer().getTaskLayer(importId, taskId);
        System.out.println("      -> getTaskLayer(): name=" + layer.getName()
                + " (task state now: " + client.importer().getTask(importId, taskId).getState() + ")");
        client.importer().setTaskLayer(importId, taskId, new ImportLayer("Populated Places (example)", "EPSG:4326"));
        System.out.println("      -> setTaskLayer(): set a custom title, srs unchanged");

        System.out.println("[4/8] Transforms — add a reproject, inspect it, add + delete a throwaway second one...");
        client.importer().addTransform(importId, taskId, ImportTransform.reproject("EPSG:4326", "EPSG:3857"));
        ImportTransformChain chain = client.importer().listTransforms(importId, taskId);
        System.out.println("      -> listTransforms(): " + chain);
        long transformId = 0; // the transform "id" is just its 0-based position in the chain array
        ImportTransform fetchedTransform = client.importer().getTransform(importId, taskId, transformId);
        System.out.println("      -> getTransform(0): " + fetchedTransform);
        client.importer().updateTransform(importId, taskId, transformId,
                ImportTransform.reproject("EPSG:4326", "EPSG:3857"));
        System.out.println("      -> updateTransform(0): re-saved unchanged");
        client.importer().addTransform(importId, taskId, ImportTransform.reproject("EPSG:4326", "EPSG:4326"));
        System.out.println("      -> addTransform(): added a second, throwaway transform at index 1");
        client.importer().deleteTransform(importId, taskId, 1);
        System.out.println("      -> deleteTransform(1): removed it, keeping only the real reproject at index 0");

        System.out.println("[5/8] Checking progress, setting update mode, then running the import...");
        System.out.println("      -> getTaskProgress(): " + client.importer().getTaskProgress(importId, taskId));
        client.importer().updateTask(importId, taskId, new ImportTaskUpdate("CREATE"));
        client.importer().runImport(importId);
        ImportContext ranImport = client.importer().getImport(importId);
        System.out.println("      -> import state after run: " + ranImport.getState()
                + " — a real layer now exists at '" + ws + ":" + layer.getName() + "'");

        System.out.println("[6/8] Listing all import contexts and tasks (this one included)...");
        List<ImportContextSummary> allImports = client.importer().listImports();
        System.out.println("      -> " + allImports.size() + " total import context(s) currently exist");
        List<ImportTask> allTasks = client.importer().listTasks(importId);
        System.out.println("      -> " + allTasks.size() + " task(s) in this import");

        System.out.println("[7/8] createAndRunImport() (create + exec=true in one call) and createImportAtId()...");
        ImportContext secondImport = client.importer().createAndRunImport(ws);
        System.out.println("      -> createAndRunImport(): #" + secondImport.getId()
                + ", state=" + secondImport.getState() + " (empty — no tasks attached, but exercises exec=true)");
        long explicitId = secondImport.getId() + 1000;
        ImportContext atId = client.importer().createImportAtId(explicitId, ws);
        System.out.println("      -> createImportAtId(): created explicitly at id " + atId.getId());

        System.out.println("[8/8] Cleanup — deleteTask, deleteImport (single), then deleteAllImports for the rest...");
        client.importer().deleteTask(importId, taskId);
        System.out.println("      -> deleteTask(): removed (the published layer itself is untouched)");
        client.importer().deleteImport(atId.getId());
        System.out.println("      -> deleteImport(" + atId.getId() + "): removed that one context specifically");
        client.importer().deleteAllImports();
        System.out.println("      -> deleteAllImports(): cleared everything else");

        System.out.println("\nCleaning up the workspace (also removes the PostGIS store and the imported layer)...");
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }
}
