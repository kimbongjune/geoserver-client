package io.github.kimbongjune.geoserverclient.dto.coverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;

import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for coverage (raster layer) details.
 *
 * <p>Maps the response body of {@code GET /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{name}}.</p>
 *
 * <p>GeoServer returns many fields (grid, dimensions, parameters, etc.). This DTO maps only the
 * commonly used ones; unknown fields are silently ignored via {@code @JsonIgnoreProperties(ignoreUnknown = true)}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coverage {

    @JsonProperty("name")
    private String name;

    @JsonProperty("nativeName")
    private String nativeName;

    @JsonProperty("nativeCoverageName")
    private String nativeCoverageName;

    @JsonProperty("namespace")
    private NamespaceLink namespace;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("keywords")
    private StringListWrapper keywords;

    @JsonProperty("srs")
    private String srs;

    @JsonProperty("projectionPolicy")
    private ProjectionPolicy projectionPolicy;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("advertised")
    private Boolean advertised;

    @JsonProperty("store")
    private StoreLink store;

    @JsonProperty("serviceConfiguration")
    private Boolean serviceConfiguration;

    @JsonProperty("simpleConversionEnabled")
    private Boolean simpleConversionEnabled;

    @JsonProperty("nativeFormat")
    private String nativeFormat;

    @JsonProperty("defaultInterpolationMethod")
    private String defaultInterpolationMethod;

    @JsonProperty("supportedFormats")
    private StringListWrapper supportedFormats;

    @JsonProperty("interpolationMethods")
    private StringListWrapper interpolationMethods;

    @JsonProperty("requestSRS")
    private StringListWrapper requestSRS;

    @JsonProperty("responseSRS")
    private StringListWrapper responseSRS;

    public Coverage() {}

    public String getName() {
        return name;
    }
    public String getNativeName() {
        return nativeName;
    }
    public String getNativeCoverageName() {
        return nativeCoverageName;
    }
    public NamespaceLink getNamespace() {
        return namespace;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public StringListWrapper getKeywords() {
        return keywords;
    }
    public String getSrs() {
        return srs;
    }
    public ProjectionPolicy getProjectionPolicy() {
        return projectionPolicy;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
    public Boolean getAdvertised() {
        return advertised;
    }
    public StoreLink getStore() {
        return store;
    }
    public Boolean getServiceConfiguration() {
        return serviceConfiguration;
    }
    public Boolean getSimpleConversionEnabled() {
        return simpleConversionEnabled;
    }
    public String getNativeFormat() {
        return nativeFormat;
    }
    public String getDefaultInterpolationMethod() {
        return defaultInterpolationMethod;
    }
    public StringListWrapper getSupportedFormats() {
        return supportedFormats;
    }
    public StringListWrapper getInterpolationMethods() {
        return interpolationMethods;
    }
    public StringListWrapper getRequestSRS() {
        return requestSRS;
    }
    public StringListWrapper getResponseSRS() {
        return responseSRS;
    }

    // Inner DTOs

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceLink {
        @JsonProperty("name") private String name;
        @JsonProperty("href") private String href;
        public NamespaceLink() {}
        public String getName() {
            return name;
        }
        public String getHref() {
            return href;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NamespaceLink that = (NamespaceLink) o;
            return Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, href);
        }

        @Override
        public String toString() {
            return "NamespaceLink{" +
                    "name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreLink {
        @JsonProperty("@class") private String cls;
        @JsonProperty("name")   private String name;
        @JsonProperty("href")   private String href;
        public StoreLink() {}
        public String getCls() {
            return cls;
        }
        public String getName() {
            return name;
        }
        public String getHref() {
            return href;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StoreLink that = (StoreLink) o;
            return Objects.equals(cls, that.cls)
                    && Objects.equals(name, that.name)
                    && Objects.equals(href, that.href);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cls, name, href);
        }

        @Override
        public String toString() {
            return "StoreLink{" +
                    "cls=" + cls +
                    ", name=" + name +
                    ", href=" + href +
                    '}';
        }
    }

    /**
     * Handles GeoServer's {@code {"string": [...]}} or {@code {"string": "singleValue"}} wrapper format.
     *
     * <p>GeoServer may return a single string instead of a one-element array; this wrapper normalises both forms.</p>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StringListWrapper {
        @JsonProperty("string")
        private Object string; // String or List<String>

        public StringListWrapper() {}

        @SuppressWarnings("unchecked")
        public List<String> getValues() {
            if (string == null) return Collections.emptyList();
            if (string instanceof List) return (List<String>) string;
            return Collections.singletonList(String.valueOf(string));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringListWrapper that = (StringListWrapper) o;
            return Objects.equals(string, that.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string);
        }

        @Override
        public String toString() {
            return "StringListWrapper{" +
                    "string=" + string +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coverage that = (Coverage) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nativeName, that.nativeName)
                && Objects.equals(nativeCoverageName, that.nativeCoverageName)
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(srs, that.srs)
                && Objects.equals(projectionPolicy, that.projectionPolicy)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(advertised, that.advertised)
                && Objects.equals(store, that.store)
                && Objects.equals(serviceConfiguration, that.serviceConfiguration)
                && Objects.equals(simpleConversionEnabled, that.simpleConversionEnabled)
                && Objects.equals(nativeFormat, that.nativeFormat)
                && Objects.equals(defaultInterpolationMethod, that.defaultInterpolationMethod)
                && Objects.equals(supportedFormats, that.supportedFormats)
                && Objects.equals(interpolationMethods, that.interpolationMethods)
                && Objects.equals(requestSRS, that.requestSRS)
                && Objects.equals(responseSRS, that.responseSRS);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nativeName, nativeCoverageName, namespace, title, description, keywords, srs, projectionPolicy, enabled, advertised, store, serviceConfiguration, simpleConversionEnabled, nativeFormat, defaultInterpolationMethod, supportedFormats, interpolationMethods, requestSRS, responseSRS);
    }

    @Override
    public String toString() {
        return "Coverage{" +
                "name=" + name +
                ", nativeName=" + nativeName +
                ", nativeCoverageName=" + nativeCoverageName +
                ", namespace=" + namespace +
                ", title=" + title +
                ", description=" + description +
                ", keywords=" + keywords +
                ", srs=" + srs +
                ", projectionPolicy=" + projectionPolicy +
                ", enabled=" + enabled +
                ", advertised=" + advertised +
                ", store=" + store +
                ", serviceConfiguration=" + serviceConfiguration +
                ", simpleConversionEnabled=" + simpleConversionEnabled +
                ", nativeFormat=" + nativeFormat +
                ", defaultInterpolationMethod=" + defaultInterpolationMethod +
                ", supportedFormats=" + supportedFormats +
                ", interpolationMethods=" + interpolationMethods +
                ", requestSRS=" + requestSRS +
                ", responseSRS=" + responseSRS +
                '}';
    }
}
