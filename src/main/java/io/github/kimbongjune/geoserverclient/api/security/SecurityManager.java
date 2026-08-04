package io.github.kimbongjune.geoserverclient.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.security.AclRules;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;

/**
 * GeoServer Security REST API client (Master Password / Self Password / ACL).
 *
 * <p>Covers master password management, self-service password change, catalog access mode,
 * and layer/service/REST ACL rules.
 *
 * <p>Source:
 * <ul>
 *   <li>Master Password: {@code src/restconfig/.../org/geoserver/rest/security/MasterPasswordController.java}</li>
 *   <li>Self Password: {@code src/restconfig/.../org/geoserver/rest/security/UserPasswordController.java}</li>
 *   <li>Catalog ACL: {@code src/restconfig/.../org/geoserver/rest/security/DataRuleController.java}</li>
 *   <li>Service ACL: {@code src/restconfig/.../org/geoserver/rest/security/ServiceRuleController.java}</li>
 *   <li>REST ACL: {@code src/restconfig/.../org/geoserver/rest/security/RestRuleController.java}</li>
 * </ul>
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET  /rest/security/masterpw
 * PUT  /rest/security/masterpw
 * PUT  /rest/security/self/password
 * GET  /rest/security/acl/catalog
 * PUT  /rest/security/acl/catalog
 * GET  /rest/security/acl/layers
 * POST /rest/security/acl/layers
 * PUT  /rest/security/acl/layers
 * DELETE /rest/security/acl/layers/{rule}
 * DELETE /rest/security/acl/layers            (bulk — GeoServer 2.28.2 always 500s, see deleteAllLayerAcl())
 * GET  /rest/security/acl/services
 * POST /rest/security/acl/services
 * PUT  /rest/security/acl/services
 * DELETE /rest/security/acl/services/{rule}
 * DELETE /rest/security/acl/services          (bulk — GeoServer 2.28.2 always 500s, see deleteAllServiceAcl())
 * GET  /rest/security/acl/rest
 * POST /rest/security/acl/rest
 * PUT  /rest/security/acl/rest
 * DELETE /rest/security/acl/rest/{rule}
 * DELETE /rest/security/acl/rest              (bulk — GeoServer 2.28.2 always 500s, see deleteAllRestAcl())
 * PUT  /rest/security/acl/catalog/reload
 * POST /rest/security/acl/catalog/reload
 * }</pre>
 *
 * <p><b>Note:</b> {@code GET/POST/PUT /rest/security/acl/rest/{rule}} (single-rule variants for
 * the REST ACL category) do not exist on GeoServer — confirmed via {@code OPTIONS} returning
 * {@code Allow: DELETE,OPTIONS} only. Only the bulk-collection and delete-by-rule forms above
 * are real.
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * SecurityManager mgr = client.security();
 * String mode = mgr.getCatalogMode(); // "HIDE", "CHALLENGE", or "MIXED"
 * mgr.setCatalogMode("CHALLENGE");
 * }</pre>
 *
 * @since 1.0.0
 */
public class SecurityManager extends AbstractManager {


    /**
     * Constructs a new SecurityManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public SecurityManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/masterpw

    /**
     * Returns the current master password in plain text.
     *
     * @return the current master password
     */
    public String getMasterPassword() {
        String body = doGetRaw("/rest/security/masterpw", "application/json");
        try {
            return getObjectMapper().readTree(body).path("oldMasterPassword").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse master password response", e);
        }
    }

    // [2] PUT /rest/security/masterpw

    /**
     * Changes the master password. Request body must be flat JSON — wrapping causes 400.
     *
     * @param oldPassword the current master password (required)
     * @param newPassword the new master password (required)
     */
    public void setMasterPassword(String oldPassword, String newPassword) {
        requireNonEmpty(oldPassword, "oldPassword");
        requireNonEmpty(newPassword, "newPassword");
        try {
            ObjectNode body = getObjectMapper().createObjectNode();
            body.put("oldMasterPassword", oldPassword);
            body.put("newMasterPassword", newPassword);
            doPutRaw("/rest/security/masterpw", getObjectMapper().writeValueAsString(body),
                    "application/json", "application/json");
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize master password request", e);
        }
    }

    // [3] PUT /rest/security/self/password

    /**
     * Changes the current authenticated user's own password. Flat JSON — wrapping causes 400.
     *
     * @param newPassword the new password (required)
     */
    public void setSelfPassword(String newPassword) {
        requireNonEmpty(newPassword, "newPassword");
        try {
            ObjectNode body = getObjectMapper().createObjectNode();
            body.put("newPassword", newPassword);
            doPutRaw("/rest/security/self/password", getObjectMapper().writeValueAsString(body),
                    "application/json", "application/json");
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize self password request", e);
        }
    }

    // [4] GET /rest/security/acl/catalog

    /**
     * Returns the catalog access control mode.
     *
     * @return the mode string: {@code HIDE}, {@code CHALLENGE}, or {@code MIXED}
     */
    public String getCatalogMode() {
        String body = doGetRaw("/rest/security/acl/catalog", "application/json");
        try {
            return getObjectMapper().readTree(body).path("mode").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse catalog mode response", e);
        }
    }

    // [5] PUT /rest/security/acl/catalog

    /**
     * Sets the catalog access control mode. Flat JSON — wrapping causes 404.
     *
     * @param mode the mode to set: {@code HIDE}, {@code CHALLENGE}, or {@code MIXED} (required)
     */
    public void setCatalogMode(String mode) {
        requireNonEmpty(mode, "mode");
        try {
            ObjectNode body = getObjectMapper().createObjectNode();
            body.put("mode", mode);
            doPutRaw("/rest/security/acl/catalog", getObjectMapper().writeValueAsString(body),
                    "application/json", "application/json");
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize catalog mode request", e);
        }
    }

    // [6] GET /rest/security/acl/layers

    /**
     * Returns all layer ACL rules.
     *
     * @return ACL rules map (key = rule pattern, value = comma-separated roles)
     */
    public AclRules getLayerAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/layers", "application/json"));
    }

    // [7] POST /rest/security/acl/layers

    /**
     * Adds new layer ACL rules. Existing keys are not overwritten.
     *
     * @param rules new rules to add (required)
     */
    public void addLayerAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/layers", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [8] PUT /rest/security/acl/layers

    /**
     * Updates the role list of existing layer ACL rules.
     *
     * @param rules rules to update (required; keys must already exist)
     */
    public void updateLayerAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/layers", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [9] DELETE /rest/security/acl/layers/{rule}

    /**
     * Deletes a layer ACL rule by its key.
     *
     * @param rule the rule key to delete (e.g., {@code "*.*.r"}) (required)
     */
    public void deleteLayerAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/layers/" + rule);
    }

    // [9b] DELETE /rest/security/acl/layers

    /**
     * Deletes ALL layer ACL rules at once.
     *
     * <p><b>GeoServer 2.28.2 BUG: always returns 500.</b> The server throws a
     * {@code StringIndexOutOfBoundsException} while building the empty rule-key response
     * (observed body: {@code "begin N, end N-1, length N-1"}); no rules are actually deleted
     * (verified: rules remain unchanged after the call). Calling this method will throw
     * {@link io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException}(500).
     *
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *         always thrown with 500 on GeoServer 2.28.2
     * @deprecated always throws on GeoServer 2.28.2 (server-side bug, not a client defect).
     *             Kept for API completeness; do not call this in new code expecting it to succeed.
     */
    @Deprecated
    public void deleteAllLayerAcl() {
        doDelete("/rest/security/acl/layers");
    }

    // [10] GET /rest/security/acl/services

    /**
     * Returns all service ACL rules.
     *
     * @return ACL rules map (key = service.method pattern, value = comma-separated roles)
     */
    public AclRules getServiceAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/services", "application/json"));
    }

    // [11] POST /rest/security/acl/services

    /**
     * Adds new service ACL rules. Existing keys are not overwritten.
     *
     * @param rules new rules to add (required)
     */
    public void addServiceAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/services", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [12] PUT /rest/security/acl/services

    /**
     * Updates existing service ACL rules.
     *
     * @param rules rules to update (required)
     */
    public void updateServiceAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/services", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [13] DELETE /rest/security/acl/services/{rule}

    /**
     * Deletes a service ACL rule by its key.
     *
     * @param rule the rule key to delete (required)
     */
    public void deleteServiceAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/services/" + rule);
    }

    // [13b] DELETE /rest/security/acl/services

    /**
     * Deletes ALL service ACL rules at once.
     *
     * <p><b>GeoServer 2.28.2 BUG: always returns 500.</b> Same server-side bug as
     * {@link #deleteAllLayerAcl()} — see that method's Javadoc for details.
     *
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *         always thrown with 500 on GeoServer 2.28.2
     * @deprecated always throws on GeoServer 2.28.2 (server-side bug, not a client defect).
     */
    @Deprecated
    public void deleteAllServiceAcl() {
        doDelete("/rest/security/acl/services");
    }

    // [14] GET /rest/security/acl/rest

    /**
     * Returns all REST API ACL rules.
     *
     * @return ACL rules map (key = REST path pattern, value = comma-separated HTTP methods and roles)
     */
    public AclRules getRestAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/rest", "application/json"));
    }

    // [15] POST /rest/security/acl/rest

    /**
     * Adds new REST API ACL rules. Existing keys are not overwritten.
     *
     * @param rules new rules to add (required)
     */
    public void addRestAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/rest", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [16] PUT /rest/security/acl/rest

    /**
     * Updates existing REST API ACL rules.
     *
     * @param rules rules to update (required)
     */
    public void updateRestAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/rest", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [17] DELETE /rest/security/acl/rest/{rule}

    /**
     * Deletes a REST API ACL rule by its key.
     *
     * @param rule the rule key to delete (required)
     */
    public void deleteRestAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/rest/" + rule);
    }

    // [17b] DELETE /rest/security/acl/rest

    /**
     * Deletes ALL REST API ACL rules at once.
     *
     * <p><b>GeoServer 2.28.2 BUG: always returns 500.</b> Same server-side bug as
     * {@link #deleteAllLayerAcl()} — see that method's Javadoc for details.
     *
     * @throws io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException
     *         always thrown with 500 on GeoServer 2.28.2
     * @deprecated always throws on GeoServer 2.28.2 (server-side bug, not a client defect).
     */
    @Deprecated
    public void deleteAllRestAcl() {
        doDelete("/rest/security/acl/rest");
    }

    // [18] PUT /rest/security/acl/catalog/reload

    /**
     * Triggers a reload of the external ACL service (e.g., GeoFence) via PUT.
     */
    public void reloadAcl() {
        String path = "/rest/security/acl/catalog/reload";
        GeoServerResponse response = httpClient.put(path, "", "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [19] POST /rest/security/acl/catalog/reload

    /**
     * Triggers a reload of the external ACL service (e.g., GeoFence) via POST.
     */
    public void postReloadAcl() {
        String path = "/rest/security/acl/catalog/reload";
        GeoServerResponse response = httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    // Helpers

    private AclRules parseAclRules(String body) {
        try {
            return getObjectMapper().readValue(body, AclRules.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse ACL response", e);
        }
    }

    private String serializeAcl(AclRules rules) {
        try {
            return getObjectMapper().writeValueAsString(rules.getEntries());
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize ACL rules", e);
        }
    }
}
