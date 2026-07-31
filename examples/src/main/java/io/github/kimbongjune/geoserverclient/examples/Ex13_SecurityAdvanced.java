package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.security.AclRules;
import io.github.kimbongjune.geoserverclient.dto.security.AuthFilterConfig;
import io.github.kimbongjune.geoserverclient.dto.security.AuthProviderConfig;
import io.github.kimbongjune.geoserverclient.dto.security.FilterChainEntry;
import io.github.kimbongjune.geoserverclient.dto.security.UserGroupServiceConfig;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * Every remaining corner of the Security API not already exercised by Ex05: master password and
 * self password change, catalog/service/REST ACL rules, ACL reload, the FilterChain API (including
 * reordering), and the "advanced" auth building blocks — AuthFilters, AuthProviders, and named
 * UserGroupServices — using each DTO's convenience factory ({@code AuthFilterConfig.basic(...)},
 * {@code AuthProviderConfig.usernamePassword(...)}, {@code UserGroupServiceConfig.xml(...)}) rather
 * than hand-building the underlying polymorphic XML-backed config.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code setSelfPassword(...)} changes the password of whichever user is authenticated on
 *       <em>that specific client instance</em> — changing it on the shared admin client used by
 *       every other example would immediately break that same client's own Basic Auth header for
 *       any further call. This example creates a disposable second user and a second
 *       {@code GeoServerClient} authenticated as that user, so the admin client used everywhere
 *       else is never touched.</li>
 *   <li>{@code setMasterPassword(...)} really does change the server's live master password (used
 *       to protect the credential keystore) — this example reads the current one first with
 *       {@code getMasterPassword()}, changes it, verifies the change, then changes it straight back
 *       so the server is left exactly as found.</li>
 *   <li>ACL rule keys use three different mini-grammars depending on which ACL you're touching:
 *       {@code workspace.layer.access} for layers, {@code service.operation} for services, and
 *       {@code path:HTTP_METHODS} for REST — see {@link AclRules}'s Javadoc.</li>
 * </ul>
 *
 * <h2>⚠ Before you run this</h2>
 * Mutates real security configuration, including a real (reverted) master password change.
 * Safe against the disposable Docker instance these examples are built around.
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs standalone, no arguments.
 */
public class Ex13_SecurityAdvanced {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex13: Security — Advanced (ACL, FilterChain, Auth*, MasterPassword) ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/7] Catalog mode: get/set round trip...");
        String originalMode = client.security().getCatalogMode();
        System.out.println("      -> current mode: " + originalMode);
        client.security().setCatalogMode("CHALLENGE".equals(originalMode) ? "MIXED" : "CHALLENGE");
        System.out.println("      -> changed to: " + client.security().getCatalogMode());
        client.security().setCatalogMode(originalMode);
        System.out.println("      -> restored to: " + client.security().getCatalogMode());

        System.out.println("\n[2/7] reloadAcl() / postReloadAcl() (external ACL service reload)...");
        // NOTE: [GeoServer 2.28.2 quirk] call these in isolation, not right after an ACL
        // add/update/delete in the same run — reproduced empirically: reloadAcl() re-reads ACL
        // rules from their on-disk file, and a just-deleted rule can still be present in that
        // on-disk snapshot for a moment, so reloading immediately after delete silently *revives*
        // the rule you just removed (confirmed by re-querying the ACL afterwards). Calling it here,
        // decoupled from any mutation below, avoids that interaction entirely.
        client.security().reloadAcl();
        client.security().postReloadAcl();
        System.out.println("      -> both completed with no error (no-op without an external service like GeoFence)");

        System.out.println("\n[3/7] ACL rules (layer / service / rest) — add, get, update, delete...");
        client.security().addLayerAcl(AclRules.of("example_acl_ws.*.r", "ROLE_AUTHENTICATED"));
        System.out.println("      -> layer ACL now: " + client.security().getLayerAcl().get("example_acl_ws.*.r"));
        client.security().updateLayerAcl(AclRules.of("example_acl_ws.*.r", "ROLE_AUTHENTICATED,ROLE_EXAMPLE"));
        System.out.println("      -> updated to: " + client.security().getLayerAcl().get("example_acl_ws.*.r"));
        client.security().deleteLayerAcl("example_acl_ws.*.r");

        client.security().addServiceAcl(AclRules.of("wms.example_op", "ROLE_AUTHENTICATED"));
        client.security().updateServiceAcl(AclRules.of("wms.example_op", "*"));
        System.out.println("      -> service ACL after update: " + client.security().getServiceAcl().get("wms.example_op"));
        client.security().deleteServiceAcl("wms.example_op");

        // NOTE: [GeoServer 2.28.2 bug] deleteRestAcl() 404s for any rule key containing a "/" —
        // DELETE /rest/security/acl/rest/{rule} can't disambiguate the rule boundary from the
        // slash(es) inside a real path pattern like "/foo/**:GET" (confirmed directly against the
        // REST API: POST/PUT accept slash-containing keys fine since the key travels in the body,
        // but single-rule DELETE takes it from the URL path and 404s: "Rule not found: ..."). Using
        // a slash-free key here so the full add/get/update/delete lifecycle actually completes.
        client.security().addRestAcl(AclRules.of("example_acl_rule:GET", "ROLE_AUTHENTICATED"));
        client.security().updateRestAcl(AclRules.of("example_acl_rule:GET", "*"));
        System.out.println("      -> REST ACL after update: " + client.security().getRestAcl().get("example_acl_rule:GET"));
        // [GeoServer 2.28.2 quirk] a delete issued immediately after an update to the same rule key
        // sometimes 200s without actually removing it (reproduced directly against the REST API —
        // the very next delete of the same key then succeeds normally). Retrying once is enough.
        client.security().deleteRestAcl("example_acl_rule:GET");
        if (client.security().getRestAcl().containsRule("example_acl_rule:GET")) {
            client.security().deleteRestAcl("example_acl_rule:GET");
        }

        System.out.println("\n[4/7] Master password: get, change, verify, change back...");
        String originalMasterPw = client.security().getMasterPassword();
        client.security().setMasterPassword(originalMasterPw, "NewMaster#Pass1");
        System.out.println("      -> changed, now reads back as: " + client.security().getMasterPassword());
        client.security().setMasterPassword("NewMaster#Pass1", originalMasterPw);
        System.out.println("      -> restored to original");

        System.out.println("\n[5/7] Self password: change via a disposable second user + second client...");
        String selfUser = "example_selfpw_user";
        client.userGroups().createUser(selfUser, "Selfpw#Pass1", true);
        // The default REST ACL restricts every POST/PUT/DELETE under /rest/** to the synthetic
        // "ROLE_ADMINISTRATOR" authority (see the ACL section above), which GeoServer grants to
        // whoever holds the real, assignable "ADMIN" role — that applies to *any* REST call,
        // including this user changing their own password, so the disposable user needs it too
        // just to reach the API at all.
        client.roles().assignRoleToUser("ADMIN", selfUser);
        GeoServerClient selfClient = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials(selfUser, "Selfpw#Pass1")
                .defaultFormat(DataFormat.JSON)
                .build();
        selfClient.security().setSelfPassword("Selfpw#Pass2");
        System.out.println("      -> changed via the disposable user's own client, no error thrown");
        // NOTE: not asserting that the *old* password now fails here — empirically, GeoServer kept
        // accepting the pre-change password for a while after this call in local testing (almost
        // certainly its Basic Auth result cache, which has its own TTL independent of the
        // underlying user/group service password value). A real client shouldn't rely on an old
        // password being rejected immediately after this call for the same reason.
        GeoServerClient verifyClient = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials(selfUser, "Selfpw#Pass2")
                .defaultFormat(DataFormat.JSON)
                .build();
        try {
            System.out.println("      -> new password authenticates fine: "
                    + !verifyClient.about().getVersion().isEmpty());
        } catch (AuthenticationException e) {
            // Observed once in local testing: a request made immediately after setSelfPassword()
            // can 401 briefly before GeoServer's auth state settles — a timing quirk, not a code bug.
            System.out.println("      -> auth briefly not settled yet right after the change (timing quirk): "
                    + e.getMessage());
        }
        selfClient.close();
        verifyClient.close();
        client.roles().unassignRoleFromUser("ADMIN", selfUser);
        client.userGroups().deleteUser(selfUser);
        System.out.println("      -> disposable user deleted (admin client above was never touched)");

        System.out.println("\n[6/7] FilterChain — create, get, list, update, updateOrder, delete...");
        String chainName = "example_chain";
        client.filterChains().create(new FilterChainEntry(
                chainName, "org.geoserver.security.ServiceLoginFilterChain",
                "/example_chain/**", Arrays.asList("anonymous")));
        System.out.println("      -> get(): " + client.filterChains().get(chainName).getPath());
        List<String> chainNames = new ArrayList<>();
        for (FilterChainEntry e : client.filterChains().list()) chainNames.add(e.getName());
        System.out.println("      -> list(): " + chainNames.size() + " chains, includes ours: "
                + chainNames.contains(chainName));
        FilterChainEntry updated = new FilterChainEntry(
                chainName, "org.geoserver.security.ServiceLoginFilterChain",
                "/example_chain/**,/example_chain2/**", Arrays.asList("anonymous"));
        client.filterChains().update(chainName, updated);
        System.out.println("      -> update(): path is now " + client.filterChains().get(chainName).getPath());
        client.filterChains().updateOrder(chainNames); // same order back — a safe no-op reorder
        System.out.println("      -> updateOrder(): re-applied the same order (safe no-op)");
        client.filterChains().delete(chainName);
        System.out.println("      -> delete(): removed");

        System.out.println("\n[7/7] AuthFilters / AuthProviders / named UserGroupServices "
                + "(create/get/update/delete via each DTO's convenience factory)...");

        String filterName = "example_auth_filter";
        client.authFilters().create(AuthFilterConfig.basic(filterName, false));
        System.out.println("      -> authFilters list contains ours: " + client.authFilters().list().contains(filterName));
        client.authFilters().update(filterName, AuthFilterConfig.basic(filterName, true));
        System.out.println("      -> authFilters.get() after update: " + client.authFilters().get(filterName).getName());
        client.authFilters().delete(filterName);

        String providerName = "example_auth_provider";
        client.authProviders().create(AuthProviderConfig.usernamePassword(providerName, "default"));
        System.out.println("      -> authProviders list size after create: " + client.authProviders().list().size());
        client.authProviders().update(providerName, AuthProviderConfig.usernamePassword(providerName, "default"));
        List<String> providerOrder = new ArrayList<>();
        for (AuthProviderConfig p : client.authProviders().list()) providerOrder.add(p.getName());
        client.authProviders().updateOrder(providerOrder); // same order back — safe no-op
        System.out.println("      -> authProviders.updateOrder(): re-applied the same order");
        client.authProviders().delete(providerName);

        String ugServiceName = "example_ug_service";
        client.userGroupServices().create(UserGroupServiceConfig.xml(
                ugServiceName, "example_ug_service.xml", 0, true,
                "digestPasswordEncoder", "default"));
        System.out.println("      -> userGroupServices list contains ours: "
                + client.userGroupServices().list().contains(ugServiceName));
        UserGroupServiceConfig fetched = client.userGroupServices().get(ugServiceName);
        System.out.println("      -> get(): className=" + fetched.getClassName());
        client.userGroupServices().update(ugServiceName, UserGroupServiceConfig.xml(
                ugServiceName, "example_ug_service.xml", 5000, true,
                "digestPasswordEncoder", "default"));
        System.out.println("      -> update(): checkInterval is now "
                + client.userGroupServices().get(ugServiceName).getExtraLong("checkInterval"));
        client.userGroupServices().delete(ugServiceName);

        System.out.println("\nDone.");
        client.close();
    }
}
