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
 * Workspace + Namespace CRUD: the two most fundamental resources in GeoServer — every store,
 * layer, and style belongs to a workspace, and creating a workspace auto-creates a matching
 * namespace.
 *
 * Run against a local GeoServer (see ../README.md): {@code docker-compose up -d} from the repo
 * root, then run this class's main method.
 */
public class Ex01_WorkspaceAndNamespace {

    public static void main(String[] args) throws Exception {
        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String wsName = "example_ws";

        try {
            // Create — every Create/Update/Publish DTO in the library supports both
            // Xxx.of(...) and the equivalent Xxx.builder(...).build() spelling.
            Workspace created = client.workspaces().create(
                    CreateWorkspaceRequest.builder(wsName).isolated(false).build());
            System.out.println("Created workspace: " + created.getName());
        } catch (ResourceAlreadyExistsException e) {
            System.out.println("Workspace already existed, continuing: " + e.getMessage());
        }

        // Read
        Workspace fetched = client.workspaces().get(wsName);
        System.out.println("Fetched: " + fetched);

        // Creating the workspace auto-created a namespace with the same prefix + a default URI
        Namespace ns = client.namespaces().get(wsName);
        System.out.println("Auto-created namespace: prefix=" + ns.getPrefix() + " uri=" + ns.getUri());

        // Update — partial: only fields you set are sent
        Workspace updated = client.workspaces().update(wsName,
                UpdateWorkspaceRequest.builder().isolated(true).build());
        System.out.println("Updated isolated flag: " + updated.getIsolated());

        // A second, independent namespace not tied to a workspace
        String nsPrefix = "example_ns";
        try {
            client.namespaces().create(CreateNamespaceRequest.builder(nsPrefix, "http://example.com/ns").build());
            System.out.println("Created standalone namespace: " + nsPrefix);
        } catch (ResourceAlreadyExistsException e) {
            System.out.println("Namespace already existed, continuing: " + e.getMessage());
        }

        // Typed exceptions: no bare RuntimeException — every "not found" is resource-specific
        try {
            client.workspaces().get("does-not-exist");
        } catch (WorkspaceNotFoundException e) {
            System.out.println("Correctly caught: " + e.getMessage());
        }

        // Cleanup
        client.namespaces().delete(nsPrefix);
        client.workspaces().delete(wsName, true); // recurse=true also removes the auto-created namespace
        System.out.println("Cleaned up.");

        client.close();
    }
}
