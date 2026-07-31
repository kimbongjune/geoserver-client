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

    /** Constructs an empty {@code AuthProviderConfig} for deserialization. */
    public AuthProviderConfig() {}

    /**
     * Constructs an {@code AuthProviderConfig} with the given envelope class, name, and implementation class.
     *
     * @param configClass provider type FQCN used as the JSON envelope key
     * @param name        provider name as registered in GeoServer
     * @param className   provider implementation FQCN
     */
    public AuthProviderConfig(String configClass, String name, String className) {
        this.configClass = configClass;
        this.name = name;
        this.className = className;
    }

    /**
     * Creates a username/password authentication provider configuration.
     *
     * @param name                 provider name as registered in GeoServer
     * @param userGroupServiceName name of the user/group service to authenticate against
     * @return a new {@code AuthProviderConfig} for the username/password provider type
     */
    public static AuthProviderConfig usernamePassword(String name, String userGroupServiceName) {
        AuthProviderConfig config = new AuthProviderConfig(
                "org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig",
                name, "org.geoserver.security.auth.UsernamePasswordAuthenticationProvider");
        config.extra.put("userGroupServiceName", userGroupServiceName);
        return config;
    }

    /**
     * Returns the JSON envelope key (provider type FQCN).
     *
     * @return the config class FQCN
     */
    public String getConfigClass() {
        return configClass;
    }

    /**
     * Sets the JSON envelope key (provider type FQCN).
     *
     * @param configClass the config class FQCN
     */
    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    /**
     * Returns the GeoServer-assigned internal ID.
     *
     * @return the provider ID, or {@code null} if not yet assigned
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the provider name as registered in GeoServer.
     *
     * @return the provider name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the provider implementation FQCN.
     *
     * @return the implementation class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns an unmodifiable view of extra type-specific fields.
     *
     * @return unmodifiable map of extra fields
     */
    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return Collections.unmodifiableMap(extra);
    }

    /**
     * Stores an extra type-specific field (Jackson {@code @JsonAnySetter}).
     *
     * @param key   the field name
     * @param value the field value
     */
    @JsonAnySetter
    public void putExtra(String key, Object value) {
        extra.put(key, value);
    }

    /**
     * Returns the named extra field as a String. Returns {@code null} if absent.
     *
     * @param key the field name
     * @return the value as a String, or {@code null}
     */
    public String getExtraString(String key) {
        Object v = extra.get(key);
        return v != null ? v.toString() : null;
    }

    /**
     * Returns the named extra field as a Boolean. Returns {@code null} if absent.
     *
     * @param key the field name
     * @return the value as a Boolean, or {@code null}
     */
    public Boolean getExtraBoolean(String key) {
        Object v = extra.get(key);
        if (v == null) {
            return null;
        }
        return (v instanceof Boolean) ? (Boolean) v : Boolean.parseBoolean(v.toString());
    }

    /**
     * Returns the named extra field as a Long. Returns {@code null} if absent.
     *
     * @param key the field name
     * @return the value as a Long, or {@code null}
     */
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
