package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a GeoServer user/group service configuration. Maps {@code GET/POST/PUT /rest/security/usergroupservices}.
 *
 * <p>{@link #getConfigClass()} is the JSON envelope key (filter type FQCN),
 * {@link #getClassName()} is the service implementation FQCN. The most common type
 * (XML-backed) is covered by {@link #xml}. LDAP/JDBC and other types use
 * {@link #getExtra()} for type-specific fields.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupServiceConfig {

    @JsonIgnore
    private String configClass;

    private String id;
    private String name;
    private String className;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<String, Object>();

    public UserGroupServiceConfig() {}

    public UserGroupServiceConfig(String configClass, String name, String className) {
        this.configClass = configClass;
        this.name = name;
        this.className = className;
    }

    /**
     * Factory for an XML-backed User/Group Service (the GeoServer default type).
     *
     * @param name                  service name
     * @param fileName             XML backing file (e.g. {@code users.xml})
     * @param checkInterval        file reload interval in ms (0 = no polling)
     * @param validating           whether to validate the XML on load
     * @param passwordEncoderName  password encoder bean name (e.g. {@code digestPasswordEncoder})
     * @param passwordPolicyName   password policy bean name (e.g. {@code default})
     */
    public static UserGroupServiceConfig xml(String name, String fileName, long checkInterval,
                                              boolean validating, String passwordEncoderName,
                                              String passwordPolicyName) {
        UserGroupServiceConfig config = new UserGroupServiceConfig(
                "org.geoserver.security.xml.XMLUserGroupServiceConfig",
                name, "org.geoserver.security.xml.XMLUserGroupService");
        config.extra.put("fileName", fileName);
        config.extra.put("checkInterval", checkInterval);
        config.extra.put("validating", validating);
        config.extra.put("passwordEncoderName", passwordEncoderName);
        config.extra.put("passwordPolicyName", passwordPolicyName);
        return config;
    }

    public String getConfigClass() {
        return configClass;
    }
    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return Collections.unmodifiableMap(extra);
    }

    @JsonAnySetter
    public void putExtra(String key, Object value) {
        extra.put(key, value);
    }

    /** Returns the named extra field as a String. Returns {@code null} if absent. */
    public String getExtraString(String key) {
        Object v = extra.get(key);
        return v != null ? v.toString() : null;
    }

    /** Returns the named extra field as a Boolean. Returns {@code null} if absent. */
    public Boolean getExtraBoolean(String key) {
        Object v = extra.get(key);
        if (v == null) {
            return null;
        }
        return (v instanceof Boolean) ? (Boolean) v : Boolean.parseBoolean(v.toString());
    }

    /** Returns the named extra field as a Long. Returns {@code null} if absent. */
    public Long getExtraLong(String key) {
        Object v = extra.get(key);
        if (v == null) {
            return null;
        }
        return (v instanceof Number) ? ((Number) v).longValue() : Long.parseLong(v.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGroupServiceConfig that = (UserGroupServiceConfig) o;
        return Objects.equals(configClass, that.configClass)
                && Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(className, that.className)
                && Objects.equals(extra, that.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configClass, id, name, className, extra);
    }

    @Override
    public String toString() {
        return "UserGroupServiceConfig{" +
                "configClass=" + configClass +
                ", id=" + id +
                ", name=" + name +
                ", className=" + className +
                ", extra=" + extra +
                '}';
    }
}
