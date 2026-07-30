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
 * <p>Source:
 * <ul>
 *   <li>Master Password: {@code src/restconfig/.../org/geoserver/rest/security/MasterPasswordController.java}</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * </pre>
 */
public class SecurityManager extends AbstractManager {


    public SecurityManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/security/masterpw

    /** Returns the current master password in plain text. */
    public String getMasterPassword() {
        String body = doGetRaw("/rest/security/masterpw", "application/json");
        try {
            return getObjectMapper().readTree(body).path("oldMasterPassword").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse master password response", e);
        }
    }

    // [2] PUT /rest/security/masterpw

    /** Changes the master password. Flat JSON — wrapping causes 400. */
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

    /** Changes the current authenticated user's password. Flat JSON — wrapping causes 400. */
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

    /** Returns the catalog access control mode: HIDE | CHALLENGE | MIXED. */
    public String getCatalogMode() {
        String body = doGetRaw("/rest/security/acl/catalog", "application/json");
        try {
            return getObjectMapper().readTree(body).path("mode").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse catalog mode response", e);
        }
    }

    // [5] PUT /rest/security/acl/catalog

    /** Sets the catalog access control mode. Flat JSON — wrapping causes 404. */
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

    /** Returns all layer ACL rules (key = rule pattern, value = comma-separated roles). */
    public AclRules getLayerAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/layers", "application/json"));
    }

    // [7] POST /rest/security/acl/layers

    /** Adds new layer ACL rules. Existing keys are not overwritten. */
    public void addLayerAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/layers", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [8] PUT /rest/security/acl/layers

    /** Updates role list of existing layer ACL rules. */
    public void updateLayerAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/layers", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [9] DELETE /rest/security/acl/layers/{rule}

    /** Deletes a layer ACL rule by its key. */
    public void deleteLayerAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/layers/" + rule);
    }

    // [10] GET /rest/security/acl/services

    /** Returns all service ACL rules. */
    public AclRules getServiceAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/services", "application/json"));
    }

    // [11] POST /rest/security/acl/services

    /** Adds new service ACL rules. */
    public void addServiceAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/services", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [12] PUT /rest/security/acl/services

    /** Updates existing service ACL rules. */
    public void updateServiceAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/services", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [13] DELETE /rest/security/acl/services/{rule}

    /** Deletes a service ACL rule by its key. */
    public void deleteServiceAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/services/" + rule);
    }

    // [14] GET /rest/security/acl/rest

    /** Returns all REST API ACL rules. */
    public AclRules getRestAcl() {
        return parseAclRules(doGetRaw("/rest/security/acl/rest", "application/json"));
    }

    // [15] POST /rest/security/acl/rest

    /** Adds new REST API ACL rules. */
    public void addRestAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPostRaw("/rest/security/acl/rest", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [16] PUT /rest/security/acl/rest

    /** Updates existing REST API ACL rules. */
    public void updateRestAcl(AclRules rules) {
        requireNonNull(rules, "rules");
        doPutRaw("/rest/security/acl/rest", serializeAcl(rules),
                "application/json", "application/json");
    }

    // [17] DELETE /rest/security/acl/rest/{rule}

    /** Deletes a REST API ACL rule by its key. */
    public void deleteRestAcl(String rule) {
        requireNonEmpty(rule, "rule");
        doDelete("/rest/security/acl/rest/" + rule);
    }

    // [18] PUT /rest/security/acl/catalog/reload

    /** Triggers a reload of the external ACL service (e.g., GeoFence). */
    public void reloadAcl() {
        String path = "/rest/security/acl/catalog/reload";
        GeoServerResponse response = httpClient.put(path, "", "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [19] POST /rest/security/acl/catalog/reload

    /** Triggers a reload of the external ACL service (POST variant). */
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
