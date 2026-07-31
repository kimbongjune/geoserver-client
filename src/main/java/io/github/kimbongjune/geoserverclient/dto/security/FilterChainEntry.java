package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for a GeoServer security filter chain entry. Maps {@code GET/POST/PUT /rest/security/filterchain}.
 *
 * <p>Fields use {@code @}-prefixed JSON keys (XStream convention).
 * The {@link #filters} field may contain either one value or a list;
 * {@code ACCEPT_SINGLE_VALUE_AS_ARRAY} handles both cases transparently.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterChainEntry {

    @JsonProperty("@name")
    private String name;

    @JsonProperty("@class")
    private String clazz;

    @JsonProperty("@path")
    private String path;

    @JsonProperty("@disabled")
    private Boolean disabled;

    @JsonProperty("@allowSessionCreation")
    private Boolean allowSessionCreation;

    @JsonProperty("@ssl")
    private Boolean requireSSL;

    @JsonProperty("@matchHTTPMethod")
    private Boolean matchHTTPMethod;

    @JsonProperty("@roleFilterName")
    private String roleFilterName;

    @JsonProperty("@interceptorName")
    private String interceptorName;

    @JsonProperty("@exceptionTranslationName")
    private String exceptionTranslationName;

    @JsonProperty("filter")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> filters;

    /** Constructs an empty {@code FilterChainEntry} for deserialization. */
    public FilterChainEntry() {}

    /**
     * Constructs a {@code FilterChainEntry} with the essential fields.
     * @param name    the chain name
     * @param clazz   the fully-qualified RequestFilterChain class name
     * @param path    the URL pattern(s) this chain matches (CSV)
     * @param filters the ordered list of filter bean names
     */
    public FilterChainEntry(String name, String clazz, String path, List<String> filters) {
        this.name = name;
        this.clazz = clazz;
        this.path = path;
        this.filters = filters;
    }

    /** @return the chain name */
    public String getName() {
        return name;
    }
    /**
     * Sets the chain name.
     * @param name the chain name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fully-qualified class name of the RequestFilterChain (e.g. {@code ServiceLoginFilterChain}).
     * @return the class name
     */
    public String getClazz() {
        return clazz;
    }
    /**
     * Sets the RequestFilterChain class name.
     * @param clazz the fully-qualified class name
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * URL pattern this chain matches (multiple patterns as CSV).
     * @return the URL pattern(s)
     */
    public String getPath() {
        return path;
    }
    /**
     * Sets the URL pattern(s) for this chain.
     * @param path the URL pattern (CSV for multiple)
     */
    public void setPath(String path) {
        this.path = path;
    }

    /** @return {@code true} if this chain is disabled */
    public Boolean getDisabled() {
        return disabled;
    }
    /**
     * Sets whether this chain is disabled.
     * @param disabled {@code true} to disable
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /** @return {@code true} if session creation is allowed */
    public Boolean getAllowSessionCreation() {
        return allowSessionCreation;
    }
    /**
     * Sets whether session creation is allowed.
     * @param allowSessionCreation {@code true} to allow
     */
    public void setAllowSessionCreation(Boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }

    /** @return {@code true} if SSL is required */
    public Boolean getRequireSSL() {
        return requireSSL;
    }
    /**
     * Sets whether SSL is required.
     * @param requireSSL {@code true} to require SSL
     */
    public void setRequireSSL(Boolean requireSSL) {
        this.requireSSL = requireSSL;
    }

    /** @return {@code true} if HTTP method matching is enabled */
    public Boolean getMatchHTTPMethod() {
        return matchHTTPMethod;
    }
    /**
     * Sets whether HTTP method matching is enabled.
     * @param matchHTTPMethod {@code true} to enable
     */
    public void setMatchHTTPMethod(Boolean matchHTTPMethod) {
        this.matchHTTPMethod = matchHTTPMethod;
    }

    /** @return the role filter bean name */
    public String getRoleFilterName() {
        return roleFilterName;
    }
    /**
     * Sets the role filter bean name.
     * @param roleFilterName the bean name
     */
    public void setRoleFilterName(String roleFilterName) {
        this.roleFilterName = roleFilterName;
    }

    /** @return the interceptor bean name */
    public String getInterceptorName() {
        return interceptorName;
    }
    /**
     * Sets the interceptor bean name.
     * @param interceptorName the bean name
     */
    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    /** @return the exception translation bean name */
    public String getExceptionTranslationName() {
        return exceptionTranslationName;
    }
    /**
     * Sets the exception translation bean name.
     * @param exceptionTranslationName the bean name
     */
    public void setExceptionTranslationName(String exceptionTranslationName) {
        this.exceptionTranslationName = exceptionTranslationName;
    }

    /** @return the ordered list of filter bean names */
    public List<String> getFilters() {
        return filters == null ? null : Collections.unmodifiableList(filters);
    }
    /**
     * Sets the ordered list of filter bean names.
     * @param filters the filter bean names
     */
    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilterChainEntry that = (FilterChainEntry) o;
        return Objects.equals(name, that.name)
                && Objects.equals(clazz, that.clazz)
                && Objects.equals(path, that.path)
                && Objects.equals(disabled, that.disabled)
                && Objects.equals(allowSessionCreation, that.allowSessionCreation)
                && Objects.equals(requireSSL, that.requireSSL)
                && Objects.equals(matchHTTPMethod, that.matchHTTPMethod)
                && Objects.equals(roleFilterName, that.roleFilterName)
                && Objects.equals(interceptorName, that.interceptorName)
                && Objects.equals(exceptionTranslationName, that.exceptionTranslationName)
                && Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clazz, path, disabled, allowSessionCreation, requireSSL, matchHTTPMethod, roleFilterName, interceptorName, exceptionTranslationName, filters);
    }

    @Override
    public String toString() {
        return "FilterChainEntry{" +
                "name=" + name +
                ", clazz=" + clazz +
                ", path=" + path +
                ", disabled=" + disabled +
                ", allowSessionCreation=" + allowSessionCreation +
                ", requireSSL=" + requireSSL +
                ", matchHTTPMethod=" + matchHTTPMethod +
                ", roleFilterName=" + roleFilterName +
                ", interceptorName=" + interceptorName +
                ", exceptionTranslationName=" + exceptionTranslationName +
                ", filters=" + filters +
                '}';
    }
}
