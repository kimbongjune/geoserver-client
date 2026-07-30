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
 * {@code ACCEPT_SINGLE_VALUE_AS_ARRAY} handles both cases transparently.</p>
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

    public FilterChainEntry() {}

    public FilterChainEntry(String name, String clazz, String path, List<String> filters) {
        this.name = name;
        this.clazz = clazz;
        this.path = path;
        this.filters = filters;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /** Fully-qualified class name of the RequestFilterChain (e.g. {@code ServiceLoginFilterChain}). */
    public String getClazz() { return clazz; }
    public void setClazz(String clazz) { this.clazz = clazz; }

    /** URL pattern this chain matches (multiple patterns as CSV). */
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }

    public Boolean getAllowSessionCreation() { return allowSessionCreation; }
    public void setAllowSessionCreation(Boolean allowSessionCreation) { this.allowSessionCreation = allowSessionCreation; }

    public Boolean getRequireSSL() { return requireSSL; }
    public void setRequireSSL(Boolean requireSSL) { this.requireSSL = requireSSL; }

    public Boolean getMatchHTTPMethod() { return matchHTTPMethod; }
    public void setMatchHTTPMethod(Boolean matchHTTPMethod) { this.matchHTTPMethod = matchHTTPMethod; }

    public String getRoleFilterName() { return roleFilterName; }
    public void setRoleFilterName(String roleFilterName) { this.roleFilterName = roleFilterName; }

    public String getInterceptorName() { return interceptorName; }
    public void setInterceptorName(String interceptorName) { this.interceptorName = interceptorName; }

    public String getExceptionTranslationName() { return exceptionTranslationName; }
    public void setExceptionTranslationName(String exceptionTranslationName) { this.exceptionTranslationName = exceptionTranslationName; }

    public List<String> getFilters() { return filters == null ? null : Collections.unmodifiableList(filters); }
    public void setFilters(List<String> filters) { this.filters = filters; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
