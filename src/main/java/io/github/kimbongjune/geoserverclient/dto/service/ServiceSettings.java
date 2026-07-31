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

    /** Constructs an empty {@code ServiceSettings} for deserialization. */
    public ServiceSettings() {}

    // Getters & Setters

    /** @return the service name */
    public String getName() {
        return name;
    }
    /**
     * Sets the service name.
     * @param name the service name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return {@code true} if the service is enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /**
     * Sets whether the service is enabled.
     * @param enabled {@code true} to enable the service
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /** @return the service title */
    public String getTitle() {
        return title;
    }
    /**
     * Sets the service title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the maintainer contact information */
    public String getMaintainer() {
        return maintainer;
    }
    /**
     * Sets the maintainer contact information.
     * @param maintainer the maintainer info
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    /** @return the service abstract text */
    public String getAbstrct() {
        return abstrct;
    }
    /**
     * Sets the service abstract text.
     * @param abstrct the abstract text
     */
    public void setAbstrct(String abstrct) {
        this.abstrct = abstrct;
    }

    /** @return the access constraints */
    public String getAccessConstraints() {
        return accessConstraints;
    }
    /**
     * Sets the access constraints.
     * @param accessConstraints the access constraints
     */
    public void setAccessConstraints(String accessConstraints) {
        this.accessConstraints = accessConstraints;
    }

    /** @return the fees description */
    public String getFees() {
        return fees;
    }
    /**
     * Sets the fees description.
     * @param fees the fees description
     */
    public void setFees(String fees) {
        this.fees = fees;
    }

    /** @return the online resource URL */
    public String getOnlineResource() {
        return onlineResource;
    }
    /**
     * Sets the online resource URL.
     * @param onlineResource the URL
     */
    public void setOnlineResource(String onlineResource) {
        this.onlineResource = onlineResource;
    }

    /** @return the schema base URL */
    public String getSchemaBaseURL() {
        return schemaBaseURL;
    }
    /**
     * Sets the schema base URL.
     * @param schemaBaseURL the base URL
     */
    public void setSchemaBaseURL(String schemaBaseURL) {
        this.schemaBaseURL = schemaBaseURL;
    }

    /** @return {@code true} if verbose output is enabled */
    public Boolean getVerbose() {
        return verbose;
    }
    /**
     * Sets verbose output.
     * @param verbose {@code true} to enable verbose output
     */
    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    /** @return {@code true} if CITE compliance is enabled */
    public Boolean getCiteCompliant() {
        return citeCompliant;
    }
    /**
     * Sets CITE compliance mode.
     * @param citeCompliant {@code true} to enable CITE compliance
     */
    public void setCiteCompliant(Boolean citeCompliant) {
        this.citeCompliant = citeCompliant;
    }

    /** @return the keywords structure */
    public Object getKeywords() {
        return keywords;
    }
    /**
     * Sets the keywords structure.
     * @param keywords the keywords
     */
    public void setKeywords(Object keywords) {
        this.keywords = keywords;
    }

    /** @return the supported versions structure */
    public Object getVersions() {
        return versions;
    }
    /**
     * Sets the supported versions structure.
     * @param versions the versions
     */
    public void setVersions(Object versions) {
        this.versions = versions;
    }

    /** @return the supported SRS list structure */
    public Object getSrs() {
        return srs;
    }
    /**
     * Sets the supported SRS list structure.
     * @param srs the SRS list
     */
    public void setSrs(Object srs) {
        this.srs = srs;
    }

    /** @return the metadata entries */
    public Object getMetadata() {
        return metadata;
    }
    /**
     * Sets the metadata entries.
     * @param metadata the metadata
     */
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    /** @return the workspace reference for workspace-scoped services */
    public WorkspaceRef getWorkspace() {
        return workspace;
    }
    /**
     * Sets the workspace reference.
     * @param workspace the workspace reference
     */
    public void setWorkspace(WorkspaceRef workspace) {
        this.workspace = workspace;
    }

    /** @return {@code true} if a bounding box is output for each CRS */
    public Boolean getBboxForEachCRS() {
        return bboxForEachCRS;
    }
    /**
     * Sets whether a bounding box is output for each CRS.
     * @param bboxForEachCRS {@code true} to enable per-CRS bounding boxes
     */
    public void setBboxForEachCRS(Boolean bboxForEachCRS) {
        this.bboxForEachCRS = bboxForEachCRS;
    }

    /** @return the watermark configuration */
    public Watermark getWatermark() {
        return watermark;
    }
    /**
     * Sets the watermark configuration.
     * @param watermark the watermark settings
     */
    public void setWatermark(Watermark watermark) {
        this.watermark = watermark;
    }

    /** @return the interpolation method */
    public String getInterpolation() {
        return interpolation;
    }
    /**
     * Sets the interpolation method.
     * @param interpolation the interpolation method name
     */
    public void setInterpolation(String interpolation) {
        this.interpolation = interpolation;
    }

    /** @return {@code true} if GetMap MIME type checking is enabled */
    public Boolean getGetMapMimeTypeCheckingEnabled() {
        return getMapMimeTypeCheckingEnabled;
    }
    /**
     * Sets whether GetMap MIME type checking is enabled.
     * @param v {@code true} to enable checking
     */
    public void setGetMapMimeTypeCheckingEnabled(Boolean v) {
        this.getMapMimeTypeCheckingEnabled = v;
    }

    /** @return the allowed GetMap MIME types */
    public Object getGetMapMimeTypes() {
        return getMapMimeTypes;
    }
    /**
     * Sets the allowed GetMap MIME types.
     * @param getMapMimeTypes the MIME types
     */
    public void setGetMapMimeTypes(Object getMapMimeTypes) {
        this.getMapMimeTypes = getMapMimeTypes;
    }

    /** @return {@code true} if GetFeatureInfo MIME type checking is enabled */
    public Boolean getGetFeatureInfoMimeTypeCheckingEnabled() {
        return getFeatureInfoMimeTypeCheckingEnabled;
    }
    /**
     * Sets whether GetFeatureInfo MIME type checking is enabled.
     * @param v {@code true} to enable checking
     */
    public void setGetFeatureInfoMimeTypeCheckingEnabled(Boolean v) {
        this.getFeatureInfoMimeTypeCheckingEnabled = v;
    }

    /** @return the allowed GetFeatureInfo MIME types */
    public Object getGetFeatureInfoMimeTypes() {
        return getFeatureInfoMimeTypes;
    }
    /**
     * Sets the allowed GetFeatureInfo MIME types.
     * @param v the MIME types
     */
    public void setGetFeatureInfoMimeTypes(Object v) {
        this.getFeatureInfoMimeTypes = v;
    }

    /** @return the root layer title */
    public String getRootLayerTitle() {
        return rootLayerTitle;
    }
    /**
     * Sets the root layer title.
     * @param rootLayerTitle the title
     */
    public void setRootLayerTitle(String rootLayerTitle) {
        this.rootLayerTitle = rootLayerTitle;
    }

    /** @return the root layer abstract */
    public String getRootLayerAbstract() {
        return rootLayerAbstract;
    }
    /**
     * Sets the root layer abstract.
     * @param rootLayerAbstract the abstract text
     */
    public void setRootLayerAbstract(String rootLayerAbstract) {
        this.rootLayerAbstract = rootLayerAbstract;
    }

    /** @return {@code true} if dynamic styling is disabled */
    public Boolean getDynamicStylingDisabled() {
        return dynamicStylingDisabled;
    }
    /**
     * Sets whether dynamic styling is disabled.
     * @param v {@code true} to disable dynamic styling
     */
    public void setDynamicStylingDisabled(Boolean v) {
        this.dynamicStylingDisabled = v;
    }

    /** @return {@code true} if feature reprojection is disabled */
    public Boolean getFeaturesReprojectionDisabled() {
        return featuresReprojectionDisabled;
    }
    /**
     * Sets whether feature reprojection is disabled.
     * @param v {@code true} to disable reprojection
     */
    public void setFeaturesReprojectionDisabled(Boolean v) {
        this.featuresReprojectionDisabled = v;
    }

    /** @return the maximum map buffer size in pixels */
    public Integer getMaxBuffer() {
        return maxBuffer;
    }
    /**
     * Sets the maximum map buffer size in pixels.
     * @param maxBuffer the buffer size
     */
    public void setMaxBuffer(Integer maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    /** @return the maximum request memory in kilobytes */
    public Integer getMaxRequestMemory() {
        return maxRequestMemory;
    }
    /**
     * Sets the maximum request memory in kilobytes.
     * @param maxRequestMemory the memory limit
     */
    public void setMaxRequestMemory(Integer maxRequestMemory) {
        this.maxRequestMemory = maxRequestMemory;
    }

    /** @return the maximum rendering time in milliseconds */
    public Integer getMaxRenderingTime() {
        return maxRenderingTime;
    }
    /**
     * Sets the maximum rendering time in milliseconds.
     * @param maxRenderingTime the time limit
     */
    public void setMaxRenderingTime(Integer maxRenderingTime) {
        this.maxRenderingTime = maxRenderingTime;
    }

    /** @return the maximum number of rendering errors allowed */
    public Integer getMaxRenderingErrors() {
        return maxRenderingErrors;
    }
    /**
     * Sets the maximum number of rendering errors allowed.
     * @param maxRenderingErrors the error limit
     */
    public void setMaxRenderingErrors(Integer maxRenderingErrors) {
        this.maxRenderingErrors = maxRenderingErrors;
    }

    /** @return the maximum number of requested dimension values */
    public Integer getMaxRequestedDimensionValues() {
        return maxRequestedDimensionValues;
    }
    /**
     * Sets the maximum number of requested dimension values.
     * @param v the limit
     */
    public void setMaxRequestedDimensionValues(Integer v) {
        this.maxRequestedDimensionValues = v;
    }

    /** @return the capabilities cache configuration */
    public CacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }
    /**
     * Sets the capabilities cache configuration.
     * @param cacheConfiguration the cache settings
     */
    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    /** @return the maximum time for a remote style request in milliseconds */
    public Integer getRemoteStyleMaxRequestTime() {
        return remoteStyleMaxRequestTime;
    }
    /**
     * Sets the maximum time for a remote style request in milliseconds.
     * @param v the time limit
     */
    public void setRemoteStyleMaxRequestTime(Integer v) {
        this.remoteStyleMaxRequestTime = v;
    }

    /** @return the remote style connection timeout in milliseconds */
    public Integer getRemoteStyleTimeout() {
        return remoteStyleTimeout;
    }
    /**
     * Sets the remote style connection timeout in milliseconds.
     * @param remoteStyleTimeout the timeout
     */
    public void setRemoteStyleTimeout(Integer remoteStyleTimeout) {
        this.remoteStyleTimeout = remoteStyleTimeout;
    }

    /** @return {@code true} if default group style is enabled */
    public Boolean getDefaultGroupStyleEnabled() {
        return defaultGroupStyleEnabled;
    }
    /**
     * Sets whether default group style is enabled.
     * @param v {@code true} to enable
     */
    public void setDefaultGroupStyleEnabled(Boolean v) {
        this.defaultGroupStyleEnabled = v;
    }

    /** @return {@code true} if GetFeatureInfo transformation is disabled */
    public Boolean getTransformFeatureInfoDisabled() {
        return transformFeatureInfoDisabled;
    }
    /**
     * Sets whether GetFeatureInfo transformation is disabled.
     * @param v {@code true} to disable
     */
    public void setTransformFeatureInfoDisabled(Boolean v) {
        this.transformFeatureInfoDisabled = v;
    }

    /** @return {@code true} if template value auto-escaping is enabled */
    public Boolean getAutoEscapeTemplateValues() {
        return autoEscapeTemplateValues;
    }
    /**
     * Sets whether template value auto-escaping is enabled.
     * @param v {@code true} to enable auto-escaping
     */
    public void setAutoEscapeTemplateValues(Boolean v) {
        this.autoEscapeTemplateValues = v;
    }

    /** @return {@code true} if an exception is thrown on invalid dimension values */
    public Boolean getExceptionOnInvalidDimension() {
        return exceptionOnInvalidDimension;
    }
    /**
     * Sets whether an exception is thrown on invalid dimension values.
     * @param v {@code true} to throw on invalid dimension
     */
    public void setExceptionOnInvalidDimension(Boolean v) {
        this.exceptionOnInvalidDimension = v;
    }

    /** @return the WFS service level (e.g. {@code "BASIC"}, {@code "TRANSACTIONAL"}) */
    public String getServiceLevel() {
        return serviceLevel;
    }
    /**
     * Sets the WFS service level.
     * @param serviceLevel the service level
     */
    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    /** @return the maximum number of features returned per request */
    public Integer getMaxFeatures() {
        return maxFeatures;
    }
    /**
     * Sets the maximum number of features returned per request.
     * @param maxFeatures the feature limit
     */
    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    /** @return {@code true} if feature bounding boxes are included in responses */
    public Boolean getFeatureBounding() {
        return featureBounding;
    }
    /**
     * Sets whether feature bounding boxes are included in responses.
     * @param featureBounding {@code true} to include bounding boxes
     */
    public void setFeatureBounding(Boolean featureBounding) {
        this.featureBounding = featureBounding;
    }

    /** @return {@code true} if canonical schema locations are used */
    public Boolean getCanonicalSchemaLocation() {
        return canonicalSchemaLocation;
    }
    /**
     * Sets whether canonical schema locations are used.
     * @param canonicalSchemaLocation {@code true} to use canonical locations
     */
    public void setCanonicalSchemaLocation(Boolean canonicalSchemaLocation) {
        this.canonicalSchemaLocation = canonicalSchemaLocation;
    }

    /** @return {@code true} if feature members are encoded individually */
    public Boolean getEncodeFeatureMember() {
        return encodeFeatureMember;
    }
    /**
     * Sets whether feature members are encoded individually.
     * @param encodeFeatureMember {@code true} to encode individually
     */
    public void setEncodeFeatureMember(Boolean encodeFeatureMember) {
        this.encodeFeatureMember = encodeFeatureMember;
    }

    /** @return {@code true} if hits requests ignore the max features limit */
    public Boolean getHitsIgnoreMaxFeatures() {
        return hitsIgnoreMaxFeatures;
    }
    /**
     * Sets whether hits requests ignore the max features limit.
     * @param hitsIgnoreMaxFeatures {@code true} to ignore the limit
     */
    public void setHitsIgnoreMaxFeatures(Boolean hitsIgnoreMaxFeatures) {
        this.hitsIgnoreMaxFeatures = hitsIgnoreMaxFeatures;
    }

    /** @return {@code true} if WFS request dump files are included */
    public Boolean getIncludeWFSRequestDumpFile() {
        return includeWFSRequestDumpFile;
    }
    /**
     * Sets whether WFS request dump files are included.
     * @param v {@code true} to include dump files
     */
    public void setIncludeWFSRequestDumpFile(Boolean v) {
        this.includeWFSRequestDumpFile = v;
    }

    /** @return {@code true} if stored query management is disabled */
    public Boolean getDisableStoredQueriesManagement() {
        return disableStoredQueriesManagement;
    }
    /**
     * Sets whether stored query management is disabled.
     * @param v {@code true} to disable management
     */
    public void setDisableStoredQueriesManagement(Boolean v) {
        this.disableStoredQueriesManagement = v;
    }

    /** @return {@code true} if global queries are allowed */
    public Boolean getAllowGlobalQueries() {
        return allowGlobalQueries;
    }
    /**
     * Sets whether global queries are allowed.
     * @param allowGlobalQueries {@code true} to allow global queries
     */
    public void setAllowGlobalQueries(Boolean allowGlobalQueries) {
        this.allowGlobalQueries = allowGlobalQueries;
    }

    /** @return {@code true} if simple conversion is enabled */
    public Boolean getSimpleConversionEnabled() {
        return simpleConversionEnabled;
    }
    /**
     * Sets whether simple conversion is enabled.
     * @param simpleConversionEnabled {@code true} to enable
     */
    public void setSimpleConversionEnabled(Boolean simpleConversionEnabled) {
        this.simpleConversionEnabled = simpleConversionEnabled;
    }

    /** @return {@code true} if GetFeature output type checking is enabled */
    public Boolean getGetFeatureOutputTypeCheckingEnabled() {
        return getFeatureOutputTypeCheckingEnabled;
    }
    /**
     * Sets whether GetFeature output type checking is enabled.
     * @param v {@code true} to enable checking
     */
    public void setGetFeatureOutputTypeCheckingEnabled(Boolean v) {
        this.getFeatureOutputTypeCheckingEnabled = v;
    }

    /** @return the allowed GetFeature output types */
    public Object getGetFeatureOutputTypes() {
        return getFeatureOutputTypes;
    }
    /**
     * Sets the allowed GetFeature output types.
     * @param v the output types
     */
    public void setGetFeatureOutputTypes(Object v) {
        this.getFeatureOutputTypes = v;
    }

    /** @return the date format used in CSV output */
    public String getCsvDateFormat() {
        return csvDateFormat;
    }
    /**
     * Sets the date format used in CSV output.
     * @param csvDateFormat the date format pattern
     */
    public void setCsvDateFormat(String csvDateFormat) {
        this.csvDateFormat = csvDateFormat;
    }

    /** @return the GML version settings */
    public Object getGml() {
        return gml;
    }
    /**
     * Sets the GML version settings.
     * @param gml the GML settings
     */
    public void setGml(Object gml) {
        this.gml = gml;
    }

    /** @return {@code true} if GML prefixing is enabled */
    public Boolean getGmlPrefixing() {
        return gmlPrefixing;
    }
    /**
     * Sets whether GML prefixing is enabled.
     * @param gmlPrefixing {@code true} to enable prefixing
     */
    public void setGmlPrefixing(Boolean gmlPrefixing) {
        this.gmlPrefixing = gmlPrefixing;
    }

    /** @return {@code true} if lat/lon axis ordering is used */
    public Boolean getLatLon() {
        return latLon;
    }
    /**
     * Sets whether lat/lon axis ordering is used.
     * @param latLon {@code true} to use lat/lon ordering
     */
    public void setLatLon(Boolean latLon) {
        this.latLon = latLon;
    }

    /** @return the maximum input memory in kilobytes */
    public Integer getMaxInputMemory() {
        return maxInputMemory;
    }
    /**
     * Sets the maximum input memory in kilobytes.
     * @param maxInputMemory the memory limit
     */
    public void setMaxInputMemory(Integer maxInputMemory) {
        this.maxInputMemory = maxInputMemory;
    }

    /** @return the maximum output memory in kilobytes */
    public Integer getMaxOutputMemory() {
        return maxOutputMemory;
    }
    /**
     * Sets the maximum output memory in kilobytes.
     * @param maxOutputMemory the memory limit
     */
    public void setMaxOutputMemory(Integer maxOutputMemory) {
        this.maxOutputMemory = maxOutputMemory;
    }

    /** @return the default deflate compression level (0-9) */
    public Integer getDefaultDeflateCompressionLevel() {
        return defaultDeflateCompressionLevel;
    }
    /**
     * Sets the default deflate compression level (0-9).
     * @param v the compression level
     */
    public void setDefaultDeflateCompressionLevel(Integer v) {
        this.defaultDeflateCompressionLevel = v;
    }

    /** @return {@code true} if subsampling is enabled */
    public Boolean getSubsamplingEnabled() {
        return subsamplingEnabled;
    }
    /**
     * Sets whether subsampling is enabled.
     * @param subsamplingEnabled {@code true} to enable subsampling
     */
    public void setSubsamplingEnabled(Boolean subsamplingEnabled) {
        this.subsamplingEnabled = subsamplingEnabled;
    }

    /** @return the overview policy name */
    public String getOverviewPolicy() {
        return overviewPolicy;
    }
    /**
     * Sets the overview policy name.
     * @param overviewPolicy the policy name
     */
    public void setOverviewPolicy(String overviewPolicy) {
        this.overviewPolicy = overviewPolicy;
    }

    // Nested classes

    /** WMS watermark configuration. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Watermark {

        @JsonProperty("enabled")
        private Boolean enabled;

        @JsonProperty("position")
        private String position;

        @JsonProperty("transparency")
        private Integer transparency;

        /** Constructs an empty {@code Watermark} for deserialization. */
        public Watermark() {}

        /** @return {@code true} if the watermark is enabled */
        public Boolean getEnabled() {
            return enabled;
        }
        /**
         * Sets whether the watermark is enabled.
         * @param enabled {@code true} to enable the watermark
         */
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        /** @return the watermark position (e.g. {@code "BOT_RIGHT"}) */
        public String getPosition() {
            return position;
        }
        /**
         * Sets the watermark position.
         * @param position the position identifier
         */
        public void setPosition(String position) {
            this.position = position;
        }

        /** @return the watermark transparency (0–100) */
        public Integer getTransparency() {
            return transparency;
        }
        /**
         * Sets the watermark transparency.
         * @param transparency the transparency value (0–100)
         */
        public void setTransparency(Integer transparency) {
            this.transparency = transparency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** WMS capabilities response cache configuration. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CacheConfiguration {

        @JsonProperty("enabled")
        private Boolean enabled;

        @JsonProperty("maxEntries")
        private Integer maxEntries;

        @JsonProperty("maxEntrySize")
        private Integer maxEntrySize;

        /** Constructs an empty {@code CacheConfiguration} for deserialization. */
        public CacheConfiguration() {}

        /** @return {@code true} if capabilities caching is enabled */
        public Boolean getEnabled() {
            return enabled;
        }
        /**
         * Sets whether capabilities caching is enabled.
         * @param enabled {@code true} to enable caching
         */
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        /** @return the maximum number of cached entries */
        public Integer getMaxEntries() {
            return maxEntries;
        }
        /**
         * Sets the maximum number of cached entries.
         * @param maxEntries the entry limit
         */
        public void setMaxEntries(Integer maxEntries) {
            this.maxEntries = maxEntries;
        }

        /** @return the maximum size of a single cached entry in kilobytes */
        public Integer getMaxEntrySize() {
            return maxEntrySize;
        }
        /**
         * Sets the maximum size of a single cached entry in kilobytes.
         * @param maxEntrySize the size limit
         */
        public void setMaxEntrySize(Integer maxEntrySize) {
            this.maxEntrySize = maxEntrySize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** Workspace reference for workspace-scoped service settings. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkspaceRef {

        @JsonProperty("name")
        private String name;

        /** Constructs an empty {@code WorkspaceRef} for deserialization. */
        public WorkspaceRef() {}

        /**
         * Constructs a {@code WorkspaceRef} with the given workspace name.
         * @param name the workspace name
         */
        public WorkspaceRef(String name) {
            this.name = name;
        }

        /** @return the workspace name */
        public String getName() {
            return name;
        }
        /**
         * Sets the workspace name.
         * @param name the workspace name
         */
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
