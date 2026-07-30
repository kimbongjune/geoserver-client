package io.github.kimbongjune.geoserverclient.dto.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for GeoServer OWS service settings, shared across WMS/WFS/WCS/WMTS.
 *
 * <p>Covers common fields plus service-specific ones in a single class.
 * Fields that do not apply to a given service are omitted from serialisation via
 * {@code @JsonInclude(NON_NULL)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceSettings {

    // Common fields

    @JsonProperty("name")
    private String name;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("title")
    private String title;

    @JsonProperty("maintainer")
    private String maintainer;

    @JsonProperty("abstrct")
    private String abstrct;

    @JsonProperty("accessConstraints")
    private String accessConstraints;

    @JsonProperty("fees")
    private String fees;

    @JsonProperty("onlineResource")
    private String onlineResource;

    @JsonProperty("schemaBaseURL")
    private String schemaBaseURL;

    @JsonProperty("verbose")
    private Boolean verbose;

    @JsonProperty("citeCompliant")
    private Boolean citeCompliant;

    /** {"string": [...]} or {"string": "single"} */
    @JsonProperty("keywords")
    private Object keywords;

    /** {"org.geotools.util.Version": [...]} or single object (WMTS) */
    @JsonProperty("versions")
    private Object versions;

    /** {"string": [...]} or {"string": "single"} */
    @JsonProperty("srs")
    private Object srs;

    /** {"entry": {...}} or {"entry": [...]} */
    @JsonProperty("metadata")
    private Object metadata;

    @JsonProperty("workspace")
    private WorkspaceRef workspace;

    // WMS-specific fields

    @JsonProperty("bboxForEachCRS")
    private Boolean bboxForEachCRS;

    @JsonProperty("watermark")
    private Watermark watermark;

    @JsonProperty("interpolation")
    private String interpolation;

    @JsonProperty("getMapMimeTypeCheckingEnabled")
    private Boolean getMapMimeTypeCheckingEnabled;

    @JsonProperty("getMapMimeTypes")
    private Object getMapMimeTypes;

    @JsonProperty("getFeatureInfoMimeTypeCheckingEnabled")
    private Boolean getFeatureInfoMimeTypeCheckingEnabled;

    @JsonProperty("getFeatureInfoMimeTypes")
    private Object getFeatureInfoMimeTypes;

    @JsonProperty("rootLayerTitle")
    private String rootLayerTitle;

    @JsonProperty("rootLayerAbstract")
    private String rootLayerAbstract;

    @JsonProperty("dynamicStylingDisabled")
    private Boolean dynamicStylingDisabled;

    @JsonProperty("featuresReprojectionDisabled")
    private Boolean featuresReprojectionDisabled;

    @JsonProperty("maxBuffer")
    private Integer maxBuffer;

    @JsonProperty("maxRequestMemory")
    private Integer maxRequestMemory;

    @JsonProperty("maxRenderingTime")
    private Integer maxRenderingTime;

    @JsonProperty("maxRenderingErrors")
    private Integer maxRenderingErrors;

    @JsonProperty("maxRequestedDimensionValues")
    private Integer maxRequestedDimensionValues;

    @JsonProperty("cacheConfiguration")
    private CacheConfiguration cacheConfiguration;

    @JsonProperty("remoteStyleMaxRequestTime")
    private Integer remoteStyleMaxRequestTime;

    @JsonProperty("remoteStyleTimeout")
    private Integer remoteStyleTimeout;

    @JsonProperty("defaultGroupStyleEnabled")
    private Boolean defaultGroupStyleEnabled;

    @JsonProperty("transformFeatureInfoDisabled")
    private Boolean transformFeatureInfoDisabled;

    @JsonProperty("autoEscapeTemplateValues")
    private Boolean autoEscapeTemplateValues;

    @JsonProperty("exceptionOnInvalidDimension")
    private Boolean exceptionOnInvalidDimension;

    // WFS-specific fields

    @JsonProperty("serviceLevel")
    private String serviceLevel;

    @JsonProperty("maxFeatures")
    private Integer maxFeatures;

    @JsonProperty("featureBounding")
    private Boolean featureBounding;

    @JsonProperty("canonicalSchemaLocation")
    private Boolean canonicalSchemaLocation;

    @JsonProperty("encodeFeatureMember")
    private Boolean encodeFeatureMember;

    @JsonProperty("hitsIgnoreMaxFeatures")
    private Boolean hitsIgnoreMaxFeatures;

    @JsonProperty("includeWFSRequestDumpFile")
    private Boolean includeWFSRequestDumpFile;

    @JsonProperty("disableStoredQueriesManagement")
    private Boolean disableStoredQueriesManagement;

    @JsonProperty("allowGlobalQueries")
    private Boolean allowGlobalQueries;

    @JsonProperty("simpleConversionEnabled")
    private Boolean simpleConversionEnabled;

    @JsonProperty("getFeatureOutputTypeCheckingEnabled")
    private Boolean getFeatureOutputTypeCheckingEnabled;

    @JsonProperty("getFeatureOutputTypes")
    private Object getFeatureOutputTypes;

    @JsonProperty("csvDateFormat")
    private String csvDateFormat;

    /** WFS GML version settings. Including this on a workspace-scoped PUT causes a 500 error. */
    @JsonProperty("gml")
    private Object gml;

    // WCS-specific fields

    @JsonProperty("gmlPrefixing")
    private Boolean gmlPrefixing;

    @JsonProperty("latLon")
    private Boolean latLon;

    @JsonProperty("maxInputMemory")
    private Integer maxInputMemory;

    @JsonProperty("maxOutputMemory")
    private Integer maxOutputMemory;

    @JsonProperty("defaultDeflateCompressionLevel")
    private Integer defaultDeflateCompressionLevel;

    @JsonProperty("subsamplingEnabled")
    private Boolean subsamplingEnabled;

    @JsonProperty("overviewPolicy")
    private String overviewPolicy;

    // Constructor

    public ServiceSettings() {}

    // Getters & Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMaintainer() { return maintainer; }
    public void setMaintainer(String maintainer) { this.maintainer = maintainer; }

    public String getAbstrct() { return abstrct; }
    public void setAbstrct(String abstrct) { this.abstrct = abstrct; }

    public String getAccessConstraints() { return accessConstraints; }
    public void setAccessConstraints(String accessConstraints) { this.accessConstraints = accessConstraints; }

    public String getFees() { return fees; }
    public void setFees(String fees) { this.fees = fees; }

    public String getOnlineResource() { return onlineResource; }
    public void setOnlineResource(String onlineResource) { this.onlineResource = onlineResource; }

    public String getSchemaBaseURL() { return schemaBaseURL; }
    public void setSchemaBaseURL(String schemaBaseURL) { this.schemaBaseURL = schemaBaseURL; }

    public Boolean getVerbose() { return verbose; }
    public void setVerbose(Boolean verbose) { this.verbose = verbose; }

    public Boolean getCiteCompliant() { return citeCompliant; }
    public void setCiteCompliant(Boolean citeCompliant) { this.citeCompliant = citeCompliant; }

    public Object getKeywords() { return keywords; }
    public void setKeywords(Object keywords) { this.keywords = keywords; }

    public Object getVersions() { return versions; }
    public void setVersions(Object versions) { this.versions = versions; }

    public Object getSrs() { return srs; }
    public void setSrs(Object srs) { this.srs = srs; }

    public Object getMetadata() { return metadata; }
    public void setMetadata(Object metadata) { this.metadata = metadata; }

    public WorkspaceRef getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceRef workspace) { this.workspace = workspace; }

    public Boolean getBboxForEachCRS() { return bboxForEachCRS; }
    public void setBboxForEachCRS(Boolean bboxForEachCRS) { this.bboxForEachCRS = bboxForEachCRS; }

    public Watermark getWatermark() { return watermark; }
    public void setWatermark(Watermark watermark) { this.watermark = watermark; }

    public String getInterpolation() { return interpolation; }
    public void setInterpolation(String interpolation) { this.interpolation = interpolation; }

    public Boolean getGetMapMimeTypeCheckingEnabled() { return getMapMimeTypeCheckingEnabled; }
    public void setGetMapMimeTypeCheckingEnabled(Boolean v) { this.getMapMimeTypeCheckingEnabled = v; }

    public Object getGetMapMimeTypes() { return getMapMimeTypes; }
    public void setGetMapMimeTypes(Object getMapMimeTypes) { this.getMapMimeTypes = getMapMimeTypes; }

    public Boolean getGetFeatureInfoMimeTypeCheckingEnabled() { return getFeatureInfoMimeTypeCheckingEnabled; }
    public void setGetFeatureInfoMimeTypeCheckingEnabled(Boolean v) { this.getFeatureInfoMimeTypeCheckingEnabled = v; }

    public Object getGetFeatureInfoMimeTypes() { return getFeatureInfoMimeTypes; }
    public void setGetFeatureInfoMimeTypes(Object v) { this.getFeatureInfoMimeTypes = v; }

    public String getRootLayerTitle() { return rootLayerTitle; }
    public void setRootLayerTitle(String rootLayerTitle) { this.rootLayerTitle = rootLayerTitle; }

    public String getRootLayerAbstract() { return rootLayerAbstract; }
    public void setRootLayerAbstract(String rootLayerAbstract) { this.rootLayerAbstract = rootLayerAbstract; }

    public Boolean getDynamicStylingDisabled() { return dynamicStylingDisabled; }
    public void setDynamicStylingDisabled(Boolean v) { this.dynamicStylingDisabled = v; }

    public Boolean getFeaturesReprojectionDisabled() { return featuresReprojectionDisabled; }
    public void setFeaturesReprojectionDisabled(Boolean v) { this.featuresReprojectionDisabled = v; }

    public Integer getMaxBuffer() { return maxBuffer; }
    public void setMaxBuffer(Integer maxBuffer) { this.maxBuffer = maxBuffer; }

    public Integer getMaxRequestMemory() { return maxRequestMemory; }
    public void setMaxRequestMemory(Integer maxRequestMemory) { this.maxRequestMemory = maxRequestMemory; }

    public Integer getMaxRenderingTime() { return maxRenderingTime; }
    public void setMaxRenderingTime(Integer maxRenderingTime) { this.maxRenderingTime = maxRenderingTime; }

    public Integer getMaxRenderingErrors() { return maxRenderingErrors; }
    public void setMaxRenderingErrors(Integer maxRenderingErrors) { this.maxRenderingErrors = maxRenderingErrors; }

    public Integer getMaxRequestedDimensionValues() { return maxRequestedDimensionValues; }
    public void setMaxRequestedDimensionValues(Integer v) { this.maxRequestedDimensionValues = v; }

    public CacheConfiguration getCacheConfiguration() { return cacheConfiguration; }
    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) { this.cacheConfiguration = cacheConfiguration; }

    public Integer getRemoteStyleMaxRequestTime() { return remoteStyleMaxRequestTime; }
    public void setRemoteStyleMaxRequestTime(Integer v) { this.remoteStyleMaxRequestTime = v; }

    public Integer getRemoteStyleTimeout() { return remoteStyleTimeout; }
    public void setRemoteStyleTimeout(Integer remoteStyleTimeout) { this.remoteStyleTimeout = remoteStyleTimeout; }

    public Boolean getDefaultGroupStyleEnabled() { return defaultGroupStyleEnabled; }
    public void setDefaultGroupStyleEnabled(Boolean v) { this.defaultGroupStyleEnabled = v; }

    public Boolean getTransformFeatureInfoDisabled() { return transformFeatureInfoDisabled; }
    public void setTransformFeatureInfoDisabled(Boolean v) { this.transformFeatureInfoDisabled = v; }

    public Boolean getAutoEscapeTemplateValues() { return autoEscapeTemplateValues; }
    public void setAutoEscapeTemplateValues(Boolean v) { this.autoEscapeTemplateValues = v; }

    public Boolean getExceptionOnInvalidDimension() { return exceptionOnInvalidDimension; }
    public void setExceptionOnInvalidDimension(Boolean v) { this.exceptionOnInvalidDimension = v; }

    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }

    public Integer getMaxFeatures() { return maxFeatures; }
    public void setMaxFeatures(Integer maxFeatures) { this.maxFeatures = maxFeatures; }

    public Boolean getFeatureBounding() { return featureBounding; }
    public void setFeatureBounding(Boolean featureBounding) { this.featureBounding = featureBounding; }

    public Boolean getCanonicalSchemaLocation() { return canonicalSchemaLocation; }
    public void setCanonicalSchemaLocation(Boolean canonicalSchemaLocation) { this.canonicalSchemaLocation = canonicalSchemaLocation; }

    public Boolean getEncodeFeatureMember() { return encodeFeatureMember; }
    public void setEncodeFeatureMember(Boolean encodeFeatureMember) { this.encodeFeatureMember = encodeFeatureMember; }

    public Boolean getHitsIgnoreMaxFeatures() { return hitsIgnoreMaxFeatures; }
    public void setHitsIgnoreMaxFeatures(Boolean hitsIgnoreMaxFeatures) { this.hitsIgnoreMaxFeatures = hitsIgnoreMaxFeatures; }

    public Boolean getIncludeWFSRequestDumpFile() { return includeWFSRequestDumpFile; }
    public void setIncludeWFSRequestDumpFile(Boolean v) { this.includeWFSRequestDumpFile = v; }

    public Boolean getDisableStoredQueriesManagement() { return disableStoredQueriesManagement; }
    public void setDisableStoredQueriesManagement(Boolean v) { this.disableStoredQueriesManagement = v; }

    public Boolean getAllowGlobalQueries() { return allowGlobalQueries; }
    public void setAllowGlobalQueries(Boolean allowGlobalQueries) { this.allowGlobalQueries = allowGlobalQueries; }

    public Boolean getSimpleConversionEnabled() { return simpleConversionEnabled; }
    public void setSimpleConversionEnabled(Boolean simpleConversionEnabled) { this.simpleConversionEnabled = simpleConversionEnabled; }

    public Boolean getGetFeatureOutputTypeCheckingEnabled() { return getFeatureOutputTypeCheckingEnabled; }
    public void setGetFeatureOutputTypeCheckingEnabled(Boolean v) { this.getFeatureOutputTypeCheckingEnabled = v; }

    public Object getGetFeatureOutputTypes() { return getFeatureOutputTypes; }
    public void setGetFeatureOutputTypes(Object v) { this.getFeatureOutputTypes = v; }

    public String getCsvDateFormat() { return csvDateFormat; }
    public void setCsvDateFormat(String csvDateFormat) { this.csvDateFormat = csvDateFormat; }

    public Object getGml() { return gml; }
    public void setGml(Object gml) { this.gml = gml; }

    public Boolean getGmlPrefixing() { return gmlPrefixing; }
    public void setGmlPrefixing(Boolean gmlPrefixing) { this.gmlPrefixing = gmlPrefixing; }

    public Boolean getLatLon() { return latLon; }
    public void setLatLon(Boolean latLon) { this.latLon = latLon; }

    public Integer getMaxInputMemory() { return maxInputMemory; }
    public void setMaxInputMemory(Integer maxInputMemory) { this.maxInputMemory = maxInputMemory; }

    public Integer getMaxOutputMemory() { return maxOutputMemory; }
    public void setMaxOutputMemory(Integer maxOutputMemory) { this.maxOutputMemory = maxOutputMemory; }

    public Integer getDefaultDeflateCompressionLevel() { return defaultDeflateCompressionLevel; }
    public void setDefaultDeflateCompressionLevel(Integer v) { this.defaultDeflateCompressionLevel = v; }

    public Boolean getSubsamplingEnabled() { return subsamplingEnabled; }
    public void setSubsamplingEnabled(Boolean subsamplingEnabled) { this.subsamplingEnabled = subsamplingEnabled; }

    public String getOverviewPolicy() { return overviewPolicy; }
    public void setOverviewPolicy(String overviewPolicy) { this.overviewPolicy = overviewPolicy; }

    // Nested classes

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Watermark {

        @JsonProperty("enabled")
        private Boolean enabled;

        @JsonProperty("position")
        private String position;

        @JsonProperty("transparency")
        private Integer transparency;

        public Watermark() {}

        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public Integer getTransparency() { return transparency; }
        public void setTransparency(Integer transparency) { this.transparency = transparency; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Watermark that = (Watermark) o;
            return Objects.equals(enabled, that.enabled)
                    && Objects.equals(position, that.position)
                    && Objects.equals(transparency, that.transparency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enabled, position, transparency);
        }

        @Override
        public String toString() {
            return "Watermark{" +
                    "enabled=" + enabled +
                    ", position=" + position +
                    ", transparency=" + transparency +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CacheConfiguration {

        @JsonProperty("enabled")
        private Boolean enabled;

        @JsonProperty("maxEntries")
        private Integer maxEntries;

        @JsonProperty("maxEntrySize")
        private Integer maxEntrySize;

        public CacheConfiguration() {}

        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }

        public Integer getMaxEntries() { return maxEntries; }
        public void setMaxEntries(Integer maxEntries) { this.maxEntries = maxEntries; }

        public Integer getMaxEntrySize() { return maxEntrySize; }
        public void setMaxEntrySize(Integer maxEntrySize) { this.maxEntrySize = maxEntrySize; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheConfiguration that = (CacheConfiguration) o;
            return Objects.equals(enabled, that.enabled)
                    && Objects.equals(maxEntries, that.maxEntries)
                    && Objects.equals(maxEntrySize, that.maxEntrySize);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enabled, maxEntries, maxEntrySize);
        }

        @Override
        public String toString() {
            return "CacheConfiguration{" +
                    "enabled=" + enabled +
                    ", maxEntries=" + maxEntries +
                    ", maxEntrySize=" + maxEntrySize +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkspaceRef {

        @JsonProperty("name")
        private String name;

        public WorkspaceRef() {}

        public WorkspaceRef(String name) { this.name = name; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkspaceRef that = (WorkspaceRef) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "WorkspaceRef{" +
                    "name=" + name +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceSettings that = (ServiceSettings) o;
        return Objects.equals(name, that.name)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(title, that.title)
                && Objects.equals(maintainer, that.maintainer)
                && Objects.equals(abstrct, that.abstrct)
                && Objects.equals(accessConstraints, that.accessConstraints)
                && Objects.equals(fees, that.fees)
                && Objects.equals(onlineResource, that.onlineResource)
                && Objects.equals(schemaBaseURL, that.schemaBaseURL)
                && Objects.equals(verbose, that.verbose)
                && Objects.equals(citeCompliant, that.citeCompliant)
                && Objects.equals(keywords, that.keywords)
                && Objects.equals(versions, that.versions)
                && Objects.equals(srs, that.srs)
                && Objects.equals(metadata, that.metadata)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(bboxForEachCRS, that.bboxForEachCRS)
                && Objects.equals(watermark, that.watermark)
                && Objects.equals(interpolation, that.interpolation)
                && Objects.equals(getMapMimeTypeCheckingEnabled, that.getMapMimeTypeCheckingEnabled)
                && Objects.equals(getMapMimeTypes, that.getMapMimeTypes)
                && Objects.equals(getFeatureInfoMimeTypeCheckingEnabled, that.getFeatureInfoMimeTypeCheckingEnabled)
                && Objects.equals(getFeatureInfoMimeTypes, that.getFeatureInfoMimeTypes)
                && Objects.equals(rootLayerTitle, that.rootLayerTitle)
                && Objects.equals(rootLayerAbstract, that.rootLayerAbstract)
                && Objects.equals(dynamicStylingDisabled, that.dynamicStylingDisabled)
                && Objects.equals(featuresReprojectionDisabled, that.featuresReprojectionDisabled)
                && Objects.equals(maxBuffer, that.maxBuffer)
                && Objects.equals(maxRequestMemory, that.maxRequestMemory)
                && Objects.equals(maxRenderingTime, that.maxRenderingTime)
                && Objects.equals(maxRenderingErrors, that.maxRenderingErrors)
                && Objects.equals(maxRequestedDimensionValues, that.maxRequestedDimensionValues)
                && Objects.equals(cacheConfiguration, that.cacheConfiguration)
                && Objects.equals(remoteStyleMaxRequestTime, that.remoteStyleMaxRequestTime)
                && Objects.equals(remoteStyleTimeout, that.remoteStyleTimeout)
                && Objects.equals(defaultGroupStyleEnabled, that.defaultGroupStyleEnabled)
                && Objects.equals(transformFeatureInfoDisabled, that.transformFeatureInfoDisabled)
                && Objects.equals(autoEscapeTemplateValues, that.autoEscapeTemplateValues)
                && Objects.equals(exceptionOnInvalidDimension, that.exceptionOnInvalidDimension)
                && Objects.equals(serviceLevel, that.serviceLevel)
                && Objects.equals(maxFeatures, that.maxFeatures)
                && Objects.equals(featureBounding, that.featureBounding)
                && Objects.equals(canonicalSchemaLocation, that.canonicalSchemaLocation)
                && Objects.equals(encodeFeatureMember, that.encodeFeatureMember)
                && Objects.equals(hitsIgnoreMaxFeatures, that.hitsIgnoreMaxFeatures)
                && Objects.equals(includeWFSRequestDumpFile, that.includeWFSRequestDumpFile)
                && Objects.equals(disableStoredQueriesManagement, that.disableStoredQueriesManagement)
                && Objects.equals(allowGlobalQueries, that.allowGlobalQueries)
                && Objects.equals(simpleConversionEnabled, that.simpleConversionEnabled)
                && Objects.equals(getFeatureOutputTypeCheckingEnabled, that.getFeatureOutputTypeCheckingEnabled)
                && Objects.equals(getFeatureOutputTypes, that.getFeatureOutputTypes)
                && Objects.equals(csvDateFormat, that.csvDateFormat)
                && Objects.equals(gml, that.gml)
                && Objects.equals(gmlPrefixing, that.gmlPrefixing)
                && Objects.equals(latLon, that.latLon)
                && Objects.equals(maxInputMemory, that.maxInputMemory)
                && Objects.equals(maxOutputMemory, that.maxOutputMemory)
                && Objects.equals(defaultDeflateCompressionLevel, that.defaultDeflateCompressionLevel)
                && Objects.equals(subsamplingEnabled, that.subsamplingEnabled)
                && Objects.equals(overviewPolicy, that.overviewPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, enabled, title, maintainer, abstrct, accessConstraints, fees, onlineResource, schemaBaseURL, verbose, citeCompliant, keywords, versions, srs, metadata, workspace, bboxForEachCRS, watermark, interpolation, getMapMimeTypeCheckingEnabled, getMapMimeTypes, getFeatureInfoMimeTypeCheckingEnabled, getFeatureInfoMimeTypes, rootLayerTitle, rootLayerAbstract, dynamicStylingDisabled, featuresReprojectionDisabled, maxBuffer, maxRequestMemory, maxRenderingTime, maxRenderingErrors, maxRequestedDimensionValues, cacheConfiguration, remoteStyleMaxRequestTime, remoteStyleTimeout, defaultGroupStyleEnabled, transformFeatureInfoDisabled, autoEscapeTemplateValues, exceptionOnInvalidDimension, serviceLevel, maxFeatures, featureBounding, canonicalSchemaLocation, encodeFeatureMember, hitsIgnoreMaxFeatures, includeWFSRequestDumpFile, disableStoredQueriesManagement, allowGlobalQueries, simpleConversionEnabled, getFeatureOutputTypeCheckingEnabled, getFeatureOutputTypes, csvDateFormat, gml, gmlPrefixing, latLon, maxInputMemory, maxOutputMemory, defaultDeflateCompressionLevel, subsamplingEnabled, overviewPolicy);
    }

    @Override
    public String toString() {
        return "ServiceSettings{" +
                "name=" + name +
                ", enabled=" + enabled +
                ", title=" + title +
                ", maintainer=" + maintainer +
                ", abstrct=" + abstrct +
                ", accessConstraints=" + accessConstraints +
                ", fees=" + fees +
                ", onlineResource=" + onlineResource +
                ", schemaBaseURL=" + schemaBaseURL +
                ", verbose=" + verbose +
                ", citeCompliant=" + citeCompliant +
                ", keywords=" + keywords +
                ", versions=" + versions +
                ", srs=" + srs +
                ", metadata=" + metadata +
                ", workspace=" + workspace +
                ", bboxForEachCRS=" + bboxForEachCRS +
                ", watermark=" + watermark +
                ", interpolation=" + interpolation +
                ", getMapMimeTypeCheckingEnabled=" + getMapMimeTypeCheckingEnabled +
                ", getMapMimeTypes=" + getMapMimeTypes +
                ", getFeatureInfoMimeTypeCheckingEnabled=" + getFeatureInfoMimeTypeCheckingEnabled +
                ", getFeatureInfoMimeTypes=" + getFeatureInfoMimeTypes +
                ", rootLayerTitle=" + rootLayerTitle +
                ", rootLayerAbstract=" + rootLayerAbstract +
                ", dynamicStylingDisabled=" + dynamicStylingDisabled +
                ", featuresReprojectionDisabled=" + featuresReprojectionDisabled +
                ", maxBuffer=" + maxBuffer +
                ", maxRequestMemory=" + maxRequestMemory +
                ", maxRenderingTime=" + maxRenderingTime +
                ", maxRenderingErrors=" + maxRenderingErrors +
                ", maxRequestedDimensionValues=" + maxRequestedDimensionValues +
                ", cacheConfiguration=" + cacheConfiguration +
                ", remoteStyleMaxRequestTime=" + remoteStyleMaxRequestTime +
                ", remoteStyleTimeout=" + remoteStyleTimeout +
                ", defaultGroupStyleEnabled=" + defaultGroupStyleEnabled +
                ", transformFeatureInfoDisabled=" + transformFeatureInfoDisabled +
                ", autoEscapeTemplateValues=" + autoEscapeTemplateValues +
                ", exceptionOnInvalidDimension=" + exceptionOnInvalidDimension +
                ", serviceLevel=" + serviceLevel +
                ", maxFeatures=" + maxFeatures +
                ", featureBounding=" + featureBounding +
                ", canonicalSchemaLocation=" + canonicalSchemaLocation +
                ", encodeFeatureMember=" + encodeFeatureMember +
                ", hitsIgnoreMaxFeatures=" + hitsIgnoreMaxFeatures +
                ", includeWFSRequestDumpFile=" + includeWFSRequestDumpFile +
                ", disableStoredQueriesManagement=" + disableStoredQueriesManagement +
                ", allowGlobalQueries=" + allowGlobalQueries +
                ", simpleConversionEnabled=" + simpleConversionEnabled +
                ", getFeatureOutputTypeCheckingEnabled=" + getFeatureOutputTypeCheckingEnabled +
                ", getFeatureOutputTypes=" + getFeatureOutputTypes +
                ", csvDateFormat=" + csvDateFormat +
                ", gml=" + gml +
                ", gmlPrefixing=" + gmlPrefixing +
                ", latLon=" + latLon +
                ", maxInputMemory=" + maxInputMemory +
                ", maxOutputMemory=" + maxOutputMemory +
                ", defaultDeflateCompressionLevel=" + defaultDeflateCompressionLevel +
                ", subsamplingEnabled=" + subsamplingEnabled +
                ", overviewPolicy=" + overviewPolicy +
                '}';
    }
}
