package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.logging.LoggingInfo;
import io.github.kimbongjune.geoserverclient.dto.service.ServiceSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.Contact;
import io.github.kimbongjune.geoserverclient.dto.settings.GlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.settings.WorkspaceSettings;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

/**
 * <h2>What this covers</h2>
 * Global and workspace-scoped Settings, and Logging configuration.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code SettingsManager} DTOs ({@link GlobalSettings}, {@link WorkspaceSettings}) are plain
 *       mutable getter/setter POJOs, not builders — unlike the Create/Update request DTOs
 *       elsewhere in this library. That's deliberate: these represent a <em>full server-side
 *       configuration object</em> you fetch, mutate a field or two on, and send back whole — a
 *       builder pattern doesn't fit that "GET, tweak, PUT" round-trip shape.</li>
 *   <li><b>{@code updateGlobal()} is REPLACE semantics</b> — you must fetch the current settings
 *       first and mutate them, not construct a partial object, or you'll blank out every field you
 *       didn't set. Contrast with {@code ServiceManager.updateWfs()}/etc. elsewhere in this
 *       library, which are MERGE semantics — always check a manager's Javadoc for which one
 *       applies before writing an update.</li>
 *   <li>Logging works the same way: fetch, mutate one field, send the whole object back.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone.
 */
public class Ex07_SettingsAndLogging {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex07: Settings + Logging ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/6] Reading global settings, tweaking one field, sending the whole object back...");
        GlobalSettings global = client.settings().getGlobal();
        int original = global.getSettings().getNumDecimals();
        System.out.println("      -> current numDecimals: " + original);
        global.getSettings().setNumDecimals(original); // no-op change, just demonstrating the round-trip
        client.settings().updateGlobal(global);
        System.out.println("      -> updateGlobal() sent (REPLACE semantics — the whole object, not just the diff)");

        System.out.println("[2/6] Contact settings — get, update (MERGE — partial body is safe), restore...");
        Contact originalContact = client.settings().getContact();
        String originalOrg = originalContact.getContactOrganization();
        System.out.println("      -> current organization: " + originalOrg);
        Contact contactUpdate = new Contact();
        contactUpdate.setContactOrganization("Example Org (temporary)");
        client.settings().updateContact(contactUpdate);
        System.out.println("      -> organization is now: " + client.settings().getContact().getContactOrganization());
        Contact contactRestore = new Contact();
        contactRestore.setContactOrganization(originalOrg);
        client.settings().updateContact(contactRestore);
        System.out.println("      -> restored to: " + client.settings().getContact().getContactOrganization());

        System.out.println("[3/6] Creating workspace-scoped settings (a workspace can override the global ones)...");
        String ws = "example_settings_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        client.settings().createWorkspaceSettings(ws, WorkspaceSettings.of(ws));
        WorkspaceSettings wsSettings = client.settings().getWorkspaceSettings(ws);
        System.out.println("      -> hasLocalSettings(): " + wsSettings.hasLocalSettings()
                + ", charset=" + wsSettings.getCharset());

        System.out.println("      -> updateWorkspaceSettings(): REPLACE semantics, full object round-trip...");
        wsSettings.setCharset("UTF-8");
        client.settings().updateWorkspaceSettings(ws, wsSettings);
        System.out.println("      -> charset is now: " + client.settings().getWorkspaceSettings(ws).getCharset());

        System.out.println("[4/6] Service settings (WMS/WFS/WCS/WMTS) — get/update round trip on each...");
        ServiceSettings wms = client.services().getWms();
        Boolean originalVerbose = wms.getVerbose();
        wms.setVerbose(originalVerbose == null || !originalVerbose);
        client.services().updateWms(wms);
        System.out.println("      -> WMS verbose toggled to: " + client.services().getWms().getVerbose());
        wms.setVerbose(originalVerbose);
        client.services().updateWms(wms);
        System.out.println("      -> WMS verbose restored to: " + originalVerbose);

        ServiceSettings wfs = client.services().getWfs();
        client.services().updateWfs(wfs); // re-save unchanged — exercises the WFS get/update pair
        System.out.println("      -> WFS get()/update() round trip completed");

        ServiceSettings wcs = client.services().getWcs();
        client.services().updateWcs(wcs);
        System.out.println("      -> WCS get()/update() round trip completed");

        ServiceSettings wmts = client.services().getWmts();
        client.services().updateWmts(wmts);
        System.out.println("      -> WMTS get()/update() round trip completed");

        System.out.println("[5/6] Service settings, workspace-scoped — get/update/delete for WMS on '" + ws + "'...");
        ServiceSettings wsWms = new ServiceSettings();
        wsWms.setName("WMS");
        wsWms.setEnabled(true);
        client.services().updateWorkspaceSettings("wms", ws, wsWms);
        System.out.println("      -> created via updateWorkspaceSettings() (also upserts): "
                + client.services().getWorkspaceSettings("wms", ws).getEnabled());
        client.services().deleteWorkspaceSettings("wms", ws);
        System.out.println("      -> deleted");

        System.out.println("[6/6] Reading logging config, toggling stdOutLogging, then reverting it...");
        LoggingInfo logging = client.logging().getLogging();
        Boolean originalStdOut = logging.getStdOutLogging();
        System.out.println("      -> current level=" + logging.getLevel() + ", stdOutLogging=" + originalStdOut);
        logging.setStdOutLogging(originalStdOut == null || !originalStdOut);
        client.logging().updateLogging(logging);
        System.out.println("      -> toggled to " + logging.getStdOutLogging());
        logging.setStdOutLogging(originalStdOut);
        client.logging().updateLogging(logging);
        System.out.println("      -> reverted back to " + originalStdOut
                + " (leaving no trace — always revert settings you didn't mean to permanently change)");

        System.out.println("\nCleaning up the workspace...");
        client.settings().deleteWorkspaceSettings(ws);
        client.workspaces().delete(ws, true);
        System.out.println("Done.");

        client.close();
    }
}
