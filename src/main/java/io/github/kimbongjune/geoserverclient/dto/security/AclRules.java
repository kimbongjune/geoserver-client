package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

/**
 * DTO for ACL rule sets used by the GeoServer layer, service, and REST ACL APIs.
 *
 * <p>Holds dynamic key-value pairs where the key format depends on the ACL type:
 * <ul>
 *   <li>Layer ACL: {@code {workspace}.{layer}.{access}} (e.g. {@code "*.*.r"})</li>
 *   <li>Service ACL: {@code {service}.{operation}} (e.g. {@code "wms.GetMap"})</li>
 *   <li>REST ACL: {@code {path}:{HTTP_METHOD}} (e.g. {@code "workspaces/**:GET"})</li>
 * </ul>
 * The value is a comma-separated role list or {@code "*"} to allow everyone.
 *
 * <p><b>Usage:</b>
 * <pre>{@code
 * // Single rule
 * AclRules rule = AclRules.of("*.*.r", "*");
 *
 * // Multiple rules (builder style)
 * AclRules rules = new AclRules()
 *         .put("cite.*.r", "ROLE_AUTHENTICATED")
 *         .put("ne.world.w", "ROLE_ADMIN");
 * }</pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AclRules {

    private final LinkedHashMap<String, String> entries = new LinkedHashMap<>();

    /** Constructs an empty {@code AclRules} instance. */
    public AclRules() {}

    /**
     * Factory method that creates an {@code AclRules} instance with a single rule.
     *
     * @param rule  the ACL rule key (e.g. {@code "*.*.r"})
     * @param roles comma-separated role list, or {@code "*"} for everyone
     * @return a new {@code AclRules} containing the single rule
     */
    public static AclRules of(String rule, String roles) {
        return new AclRules().put(rule, roles);
    }

    /**
     * Adds a rule and returns {@code this} for chaining (builder style).
     *
     * @param rule  the ACL rule key
     * @param roles comma-separated role list, or {@code "*"} for everyone
     * @return this instance
     */
    public AclRules put(String rule, String roles) {
        entries.put(rule, roles);
        return this;
    }

    /**
     * Returns the roles string for the given rule key, or {@code null} if absent.
     *
     * @param rule the ACL rule key to look up
     * @return the roles string, or {@code null}
     */
    public String get(String rule) {
        return entries.get(rule);
    }

    /**
     * Returns {@code true} if the given rule key is present.
     *
     * @param rule the ACL rule key to test
     * @return {@code true} if the key exists
     */
    public boolean containsRule(String rule) {
        return entries.containsKey(rule);
    }

    /**
     * Returns an unmodifiable view of all rule keys.
     *
     * @return unmodifiable set of rule keys
     */
    public Set<String> ruleNames() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    /**
     * Returns the number of rules.
     *
     * @return the rule count
     */
    public int size() {
        return entries.size();
    }

    /**
     * Returns {@code true} if there are no rules.
     *
     * @return {@code true} if this instance contains no rules
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    // Jackson serialization

    /**
     * Captures a JSON key-value pair into the rule map (Jackson {@code @JsonAnySetter}).
     *
     * @param key   the rule key
     * @param value the roles value
     */
    @JsonAnySetter
    public void setEntry(String key, Object value) {
        entries.put(key, value != null ? value.toString() : null);
    }

    /**
     * Returns an unmodifiable view of the underlying rule map for Jackson serialization.
     *
     * @return unmodifiable map of rule key to roles string
     */
    @JsonAnyGetter
    public Map<String, String> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AclRules that = (AclRules) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public String toString() {
        return "AclRules{" +
                "entries=" + entries +
                '}';
    }
}
