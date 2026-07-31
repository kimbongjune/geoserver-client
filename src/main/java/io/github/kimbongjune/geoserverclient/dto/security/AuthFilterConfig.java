package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a GeoServer authentication filter configuration. Maps {@code GET/POST/PUT /rest/security/authfilters}.
 *
 * <p>GeoServer uses a polymorphic envelope for authentication filters
 * (XStream serializes each type under a JSON key equal to its FQCN).
 * This DTO captures the {@link #getConfigClass()} (JSON envelope key = filter FQCN)
 * and {@link #getClassName()} (filter implementation FQCN). Four factory methods
 * ({@link #anonymous}, {@link #basic}, {@link #form}, {@link #rememberMe}) cover the
 * common cases. Extra type-specific fields are stored in {@link #getExtra()}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthFilterConfig {

    /** JSON envelope key (filter type FQCN). Not serialized into the payload directly. */
    @JsonIgnore
    private String configClass;

    private String id;
    private String name;
    private String className;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<String, Object>();

    /** Constructs an empty {@code AuthFilterConfig} for deserialization. */
    public AuthFilterConfig() {}

    /**
     * Constructs an {@code AuthFilterConfig} with the given envelope class, name, and implementation class.
     *
     * @param configClass filter type FQCN used as the JSON envelope key
     * @param name        filter name as registered in GeoServer
     * @param className   filter implementation FQCN
     */
    public AuthFilterConfig(String configClass, String name, String className) {
        this.configClass = configClass;
        this.name = name;
        this.className = className;
    }

    /**
     * Creates an anonymous authentication filter configuration.
     *
     * @param name filter name as registered in GeoServer
     * @return a new {@code AuthFilterConfig} for the anonymous filter type
     */
    public static AuthFilterConfig anonymous(String name) {
        return new AuthFilterConfig(
                "org.geoserver.security.config.AnonymousAuthenticationFilterConfig",
                name, "org.geoserver.security.filter.GeoServerAnonymousAuthenticationFilter");
    }

    /**
     * Creates a basic authentication filter configuration.
     *
     * @param name          filter name as registered in GeoServer
     * @param useRememberMe whether to enable remember-me support
     * @return a new {@code AuthFilterConfig} for the basic authentication filter type
     */
    public static AuthFilterConfig basic(String name, boolean useRememberMe) {
        AuthFilterConfig config = new AuthFilterConfig(
                "org.geoserver.security.config.BasicAuthenticationFilterConfig",
                name, "org.geoserver.security.filter.GeoServerBasicAuthenticationFilter");
        config.extra.put("useRememberMe", useRememberMe);
        return config;
    }

    /**
     * Creates a form-based (username/password) authentication filter configuration.
     *
     * @param name                   filter name as registered in GeoServer
     * @param usernameParameterName  HTTP parameter name for the username field
     * @param passwordParameterName  HTTP parameter name for the password field
     * @return a new {@code AuthFilterConfig} for the form login filter type
     */
    public static AuthFilterConfig form(String name, String usernameParameterName, String passwordParameterName) {
        AuthFilterConfig config = new AuthFilterConfig(
                "org.geoserver.security.config.UsernamePasswordAuthenticationFilterConfig",
                name, "org.geoserver.security.filter.GeoServerUserNamePasswordAuthenticationFilter");
        config.extra.put("usernameParameterName", usernameParameterName);
        config.extra.put("passwordParameterName", passwordParameterName);
        return config;
    }

    /**
     * Creates a remember-me authentication filter configuration.
     *
     * @param name filter name as registered in GeoServer
     * @return a new {@code AuthFilterConfig} for the remember-me filter type
     */
    public static AuthFilterConfig rememberMe(String name) {
        return new AuthFilterConfig(
                "org.geoserver.security.config.RememberMeAuthenticationFilterConfig",
                name, "org.geoserver.security.filter.GeoServerRememberMeAuthenticationFilter");
    }

    /**
     * Returns the JSON envelope key (filter type FQCN).
     *
     * @return the config class FQCN
     */
    public String getConfigClass() {
        return configClass;
    }

    /**
     * Sets the JSON envelope key (filter type FQCN).
     *
     * @param configClass the config class FQCN
     */
    public void setConfigClass(String configClass) {
        this.configClass = configClass;
    }

    /**
     * Auto-assigned GeoServer internal ID (not needed on create).
     *
     * @return the filter ID, or {@code null} if not yet assigned
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the filter name as registered in GeoServer.
     *
     * @return the filter name
     */
    public String getName() {
        return name;
    }

    /**
     * Filter implementation FQCN (e.g. {@code GeoServerBasicAuthenticationFilter}).
     *
     * @return the implementation class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Extra type-specific fields (e.g. {@code useRememberMe}, {@code usernameParameterName}).
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
        AuthFilterConfig that = (AuthFilterConfig) o;
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
        return "AuthFilterConfig{" +
                "configClass=" + configClass +
                ", id=" + id +
                ", name=" + name +
                ", className=" + className +
                ", extra=" + extra +
                '}';
    }
}
