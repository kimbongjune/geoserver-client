package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.Namespace;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.UpdateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.Workspace;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.WorkspaceNotFoundException;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

/**
 * <h2>What this covers</h2>
 * Workspace and Namespace CRUD — the two most fundamental resources in GeoServer. Every store,
 * layer, and (workspace-scoped) style lives inside a workspace, so this is usually the very first
 * thing any GeoServer client does.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>Creating a workspace <b>auto-creates a matching namespace</b> with the same prefix —
 *       GeoServer does this for you, you don't call {@code namespaces().create(...)} separately
 *       for the workspace's own namespace.</li>
 *   <li>Every {@code Create}/{@code Update}/{@code Publish} DTO in this library supports two
 *       equivalent spellings: {@code Xxx.of(...)} and {@code Xxx.builder(...).build()} — this
 *       example uses {@code builder(...)} throughout, but {@code of(...)} does exactly the same
 *       thing if you prefer that style.</li>
 *   <li>Updates are <b>partial</b>: {@code UpdateWorkspaceRequest.builder().isolated(true).build()}
 *       only touches the {@code isolated} field — anything you don't set is left alone.</li>
 *   <li>Every "not found" is a specific typed exception ({@link WorkspaceNotFoundException}),
 *       never a generic {@code RuntimeException} you'd have to inspect a message string to
 *       understand.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer running at {@code http://localhost:8100/geoserver} — from the repo root:
 * {@code docker-compose up -d}. See {@code examples/README.md} for the full setup.
 *
 * <h2>What you'll see</h2>
 * This example is fully self-contained: it creates a workspace and a namespace, exercises every
 * operation, and deletes everything it created before exiting — safe to run over and over.
 */
public class Ex01_WorkspaceAndNamespace {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex01: Workspace + Namespace ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON) // JSON is the only supported client-wide default today
                .build();

        String wsName = "example_ws";

        System.out.println("[1/6] Creating workspace '" + wsName + "'...");
        try {
            Workspace created = client.workspaces().create(
                    CreateWorkspaceRequest.builder(wsName).isolated(false).build());
            System.out.println("      -> created: " + created.getName());
        } catch (ResourceAlreadyExistsException e) {
            // Left over from a previous run that didn't clean up (e.g. you Ctrl+C'd it) — fine,
            // just keep going with the existing one.
            System.out.println("      -> already existed from a previous run, continuing: " + e.getMessage());
        }

        System.out.println("[2/6] Fetching it back with get(name)...");
        Workspace fetched = client.workspaces().get(wsName);
        System.out.println("      -> " + fetched);

        System.out.println("[3/6] Checking the namespace GeoServer auto-created for us...");
        Namespace ns = client.namespaces().get(wsName);
        System.out.println("      -> prefix=" + ns.getPrefix() + ", uri=" + ns.getUri()
                + " (same prefix as the workspace name, created automatically)");

        System.out.println("[4/6] Partially updating the workspace (isolated=true only)...");
        Workspace updated = client.workspaces().update(wsName,
                UpdateWorkspaceRequest.builder().isolated(true).build());
        System.out.println("      -> isolated is now: " + updated.getIsolated());

        String nsPrefix = "example_ns";
        System.out.println("[5/6] Creating a second, standalone namespace (not tied to a workspace)...");
        try {
            client.namespaces().create(CreateNamespaceRequest.builder(nsPrefix, "http://example.com/ns").build());
            System.out.println("      -> created: " + nsPrefix);
        } catch (ResourceAlreadyExistsException e) {
            System.out.println("      -> already existed, continuing: " + e.getMessage());
        }

        System.out.println("[6/8] Confirming a lookup on a nonexistent workspace throws the right typed exception...");
        try {
            client.workspaces().get("does-not-exist");
            System.out.println("      -> unexpected: no exception was thrown!");
        } catch (WorkspaceNotFoundException e) {
            System.out.println("      -> correctly caught WorkspaceNotFoundException: " + e.getMessage());
        }

        System.out.println("[7/8] Reading and temporarily changing the server-wide default workspace/namespace...");
        Workspace originalDefaultWs = client.workspaces().getDefault();
        System.out.println("      -> current default workspace: " + originalDefaultWs.getName());
        client.workspaces().setDefault(wsName);
        System.out.println("      -> default workspace is now '" + client.workspaces().getDefault().getName() + "'");
        System.out.println("      -> NOTE: GeoServer keeps a single default-workspace setting under the hood —"
                + " namespaces().setDefault(prefix) below changes that *same* setting (by namespace prefix"
                + " instead of workspace name), it does not track an independent value:");
        client.namespaces().setDefault(nsPrefix);
        System.out.println("      -> default namespace is now '" + client.namespaces().getDefault().getPrefix()
                + "', which also flipped the default workspace back to '"
                + client.workspaces().getDefault().getName() + "' (same underlying setting)");
        System.out.println("[8/8] Restoring the original default (one call restores both, since they're the same setting)...");
        client.workspaces().setDefault(originalDefaultWs.getName());
        System.out.println("      -> restored to '" + originalDefaultWs.getName() + "'");

        System.out.println("\nCleaning up everything this example created...");
        client.namespaces().delete(nsPrefix);
        client.workspaces().delete(wsName, true); // recurse=true also removes the auto-created namespace
        System.out.println("Done — nothing left behind. Re-run this anytime.");

        client.close();
    }
}
