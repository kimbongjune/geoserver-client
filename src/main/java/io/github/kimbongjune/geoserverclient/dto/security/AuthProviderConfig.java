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
 * GeoServer  (Authentication Provider)  DTO. {@code GET/POST/PUT /rest/security/authproviders}.
 *
 * <p>{@link #getConfigClass()} JSON  envelope (  FQCN),
 * {@link #getClassName()} provider  FQCN.   
 * {@link #usernamePassword} .      {@link #getExtra()} .
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthProviderConfig {

    @JsonIgnore
    private String configClass;

    private String id;
    private String name;
    private String className;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<String, Object>();

    public AuthProviderConfig() {}

    public AuthProviderConfig(String configClass, String name, String className) {
        this.configClass = configClass;
        this.name = name;
        this.className = className;
    }

    public static AuthProviderConfig usernamePassword(String name, String userGroupServiceName) {
        AuthProviderConfig config = new AuthProviderConfig(
                "org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig",
                name, "org.geoserver.security.auth.UsernamePasswordAuthenticationProvider");
        config.extra.put("userGroupServiceName", userGroupServiceName);
        return config;
    }

    public String getConfigClass() { return configClass; }
    public void setConfigClass(String configClass) { this.configClass = configClass; }

    public String getId() { return id; }

    public String getName() { return name; }

    public String getClassName() { return className; }

    @JsonAnyGetter
    public Map<String, Object> getExtra() { return Collections.unmodifiableMap(extra); }

    @JsonAnySetter
    public void putExtra(String key, Object value) { extra.put(key, value); }

    /** {@link #getExtra()}      .  {@code null}. */
    public String getExtraString(String key) {
        Object v = extra.get(key);
        return v != null ? v.toString() : null;
    }

    /** {@link #getExtra()} boolean     .  {@code null}. */
    public Boolean getExtraBoolean(String key) {
        Object v = extra.get(key);
        if (v == null) return null;
        return (v instanceof Boolean) ? (Boolean) v : Boolean.parseBoolean(v.toString());
    }

    /** {@link #getExtra()}      .  {@code null}. */
    public Long getExtraLong(String key) {
        Object v = extra.get(key);
        if (v == null) return null;
        return (v instanceof Number) ? ((Number) v).longValue() : Long.parseLong(v.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthProviderConfig that = (AuthProviderConfig) o;
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
        return "AuthProviderConfig{" +
                "configClass=" + configClass +
                ", id=" + id +
                ", name=" + name +
                ", className=" + className +
                ", extra=" + extra +
                '}';
    }
}
