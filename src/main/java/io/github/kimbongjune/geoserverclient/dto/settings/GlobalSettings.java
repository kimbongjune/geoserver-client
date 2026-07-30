package io.github.kimbongjune.geoserverclient.dto.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for GeoServer global settings. Maps {@code GET /rest/settings}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalSettings {

    @JsonProperty("settings")
    private GeoServerSettings settings;

    @JsonProperty("jai")
    private Jai jai;

    @JsonProperty("coverageAccess")
    private CoverageAccess coverageAccess;

    @JsonProperty("updateSequence")
    private Integer updateSequence;

    @JsonProperty("featureTypeCacheSize")
    private Integer featureTypeCacheSize;

    @JsonProperty("globalServices")
    private Boolean globalServices;

    @JsonProperty("xmlPostRequestLogBufferSize")
    private Integer xmlPostRequestLogBufferSize;

    @JsonProperty("trailingSlashMatch")
    private Boolean trailingSlashMatch;

    @JsonProperty("webUIMode")
    private String webUIMode;

    @JsonProperty("allowStoredQueriesPerWorkspace")
    private Boolean allowStoredQueriesPerWorkspace;

    @JsonProperty("resourceErrorHandling")
    private String resourceErrorHandling;

    public GlobalSettings() {}

    public GeoServerSettings getSettings() { return settings; }
    public void setSettings(GeoServerSettings settings) { this.settings = settings; }

    public Jai getJai() { return jai; }
    public void setJai(Jai jai) { this.jai = jai; }

    public CoverageAccess getCoverageAccess() { return coverageAccess; }
    public void setCoverageAccess(CoverageAccess coverageAccess) { this.coverageAccess = coverageAccess; }

    public Integer getUpdateSequence() { return updateSequence; }
    public void setUpdateSequence(Integer updateSequence) { this.updateSequence = updateSequence; }

    public Integer getFeatureTypeCacheSize() { return featureTypeCacheSize; }
    public void setFeatureTypeCacheSize(Integer featureTypeCacheSize) { this.featureTypeCacheSize = featureTypeCacheSize; }

    public Boolean getGlobalServices() { return globalServices; }
    public void setGlobalServices(Boolean globalServices) { this.globalServices = globalServices; }

    public Integer getXmlPostRequestLogBufferSize() { return xmlPostRequestLogBufferSize; }
    public void setXmlPostRequestLogBufferSize(Integer xmlPostRequestLogBufferSize) { this.xmlPostRequestLogBufferSize = xmlPostRequestLogBufferSize; }

    public Boolean getTrailingSlashMatch() { return trailingSlashMatch; }
    public void setTrailingSlashMatch(Boolean trailingSlashMatch) { this.trailingSlashMatch = trailingSlashMatch; }

    public String getWebUIMode() { return webUIMode; }
    public void setWebUIMode(String webUIMode) { this.webUIMode = webUIMode; }

    public Boolean getAllowStoredQueriesPerWorkspace() { return allowStoredQueriesPerWorkspace; }
    public void setAllowStoredQueriesPerWorkspace(Boolean allowStoredQueriesPerWorkspace) { this.allowStoredQueriesPerWorkspace = allowStoredQueriesPerWorkspace; }

    public String getResourceErrorHandling() { return resourceErrorHandling; }
    public void setResourceErrorHandling(String resourceErrorHandling) { this.resourceErrorHandling = resourceErrorHandling; }

    //  Nested: GeoServerSettings 

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GeoServerSettings {

        @JsonProperty("id")
        private String id;

        @JsonProperty("contact")
        private Contact contact;

        @JsonProperty("charset")
        private String charset;

        @JsonProperty("numDecimals")
        private Integer numDecimals;

        @JsonProperty("verbose")
        private Boolean verbose;

        @JsonProperty("verboseExceptions")
        private Boolean verboseExceptions;

        @JsonProperty("localWorkspaceIncludesPrefix")
        private Boolean localWorkspaceIncludesPrefix;

        @JsonProperty("showCreatedTimeColumnsInAdminList")
        private Boolean showCreatedTimeColumnsInAdminList;

        @JsonProperty("showModifiedTimeColumnsInAdminList")
        private Boolean showModifiedTimeColumnsInAdminList;

        @JsonProperty("showModifiedUserAdminList")
        private Boolean showModifiedUserAdminList;

        @JsonProperty("useHeadersProxyURL")
        private Boolean useHeadersProxyURL;

        public GeoServerSettings() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Contact getContact() { return contact; }
        public void setContact(Contact contact) { this.contact = contact; }

        public String getCharset() { return charset; }
        public void setCharset(String charset) { this.charset = charset; }

        public Integer getNumDecimals() { return numDecimals; }
        public void setNumDecimals(Integer numDecimals) { this.numDecimals = numDecimals; }

        public Boolean getVerbose() { return verbose; }
        public void setVerbose(Boolean verbose) { this.verbose = verbose; }

        public Boolean getVerboseExceptions() { return verboseExceptions; }
        public void setVerboseExceptions(Boolean verboseExceptions) { this.verboseExceptions = verboseExceptions; }

        public Boolean getLocalWorkspaceIncludesPrefix() { return localWorkspaceIncludesPrefix; }
        public void setLocalWorkspaceIncludesPrefix(Boolean localWorkspaceIncludesPrefix) { this.localWorkspaceIncludesPrefix = localWorkspaceIncludesPrefix; }

        public Boolean getShowCreatedTimeColumnsInAdminList() { return showCreatedTimeColumnsInAdminList; }
        public void setShowCreatedTimeColumnsInAdminList(Boolean v) { this.showCreatedTimeColumnsInAdminList = v; }

        public Boolean getShowModifiedTimeColumnsInAdminList() { return showModifiedTimeColumnsInAdminList; }
        public void setShowModifiedTimeColumnsInAdminList(Boolean v) { this.showModifiedTimeColumnsInAdminList = v; }

        public Boolean getShowModifiedUserAdminList() { return showModifiedUserAdminList; }
        public void setShowModifiedUserAdminList(Boolean v) { this.showModifiedUserAdminList = v; }

        public Boolean getUseHeadersProxyURL() { return useHeadersProxyURL; }
        public void setUseHeadersProxyURL(Boolean useHeadersProxyURL) { this.useHeadersProxyURL = useHeadersProxyURL; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GeoServerSettings that = (GeoServerSettings) o;
            return Objects.equals(id, that.id)
                    && Objects.equals(contact, that.contact)
                    && Objects.equals(charset, that.charset)
                    && Objects.equals(numDecimals, that.numDecimals)
                    && Objects.equals(verbose, that.verbose)
                    && Objects.equals(verboseExceptions, that.verboseExceptions)
                    && Objects.equals(localWorkspaceIncludesPrefix, that.localWorkspaceIncludesPrefix)
                    && Objects.equals(showCreatedTimeColumnsInAdminList, that.showCreatedTimeColumnsInAdminList)
                    && Objects.equals(showModifiedTimeColumnsInAdminList, that.showModifiedTimeColumnsInAdminList)
                    && Objects.equals(showModifiedUserAdminList, that.showModifiedUserAdminList)
                    && Objects.equals(useHeadersProxyURL, that.useHeadersProxyURL);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, contact, charset, numDecimals, verbose, verboseExceptions, localWorkspaceIncludesPrefix, showCreatedTimeColumnsInAdminList, showModifiedTimeColumnsInAdminList, showModifiedUserAdminList, useHeadersProxyURL);
        }

        @Override
        public String toString() {
            return "GeoServerSettings{" +
                    "id=" + id +
                    ", contact=" + contact +
                    ", charset=" + charset +
                    ", numDecimals=" + numDecimals +
                    ", verbose=" + verbose +
                    ", verboseExceptions=" + verboseExceptions +
                    ", localWorkspaceIncludesPrefix=" + localWorkspaceIncludesPrefix +
                    ", showCreatedTimeColumnsInAdminList=" + showCreatedTimeColumnsInAdminList +
                    ", showModifiedTimeColumnsInAdminList=" + showModifiedTimeColumnsInAdminList +
                    ", showModifiedUserAdminList=" + showModifiedUserAdminList +
                    ", useHeadersProxyURL=" + useHeadersProxyURL +
                    '}';
        }
    }

    //  Nested: Jai 

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Jai {

        @JsonProperty("allowInterpolation")
        private Boolean allowInterpolation;

        @JsonProperty("recycling")
        private Boolean recycling;

        @JsonProperty("tilePriority")
        private Integer tilePriority;

        @JsonProperty("tileThreads")
        private Integer tileThreads;

        @JsonProperty("memoryCapacity")
        private Double memoryCapacity;

        @JsonProperty("memoryThreshold")
        private Double memoryThreshold;

        @JsonProperty("imageIOCache")
        private Boolean imageIOCache;

        @JsonProperty("pngEncoderType")
        private String pngEncoderType;

        public Jai() {}

        public Boolean getAllowInterpolation() { return allowInterpolation; }
        public void setAllowInterpolation(Boolean allowInterpolation) { this.allowInterpolation = allowInterpolation; }

        public Boolean getRecycling() { return recycling; }
        public void setRecycling(Boolean recycling) { this.recycling = recycling; }

        public Integer getTilePriority() { return tilePriority; }
        public void setTilePriority(Integer tilePriority) { this.tilePriority = tilePriority; }

        public Integer getTileThreads() { return tileThreads; }
        public void setTileThreads(Integer tileThreads) { this.tileThreads = tileThreads; }

        public Double getMemoryCapacity() { return memoryCapacity; }
        public void setMemoryCapacity(Double memoryCapacity) { this.memoryCapacity = memoryCapacity; }

        public Double getMemoryThreshold() { return memoryThreshold; }
        public void setMemoryThreshold(Double memoryThreshold) { this.memoryThreshold = memoryThreshold; }

        public Boolean getImageIOCache() { return imageIOCache; }
        public void setImageIOCache(Boolean imageIOCache) { this.imageIOCache = imageIOCache; }

        public String getPngEncoderType() { return pngEncoderType; }
        public void setPngEncoderType(String pngEncoderType) { this.pngEncoderType = pngEncoderType; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Jai that = (Jai) o;
            return Objects.equals(allowInterpolation, that.allowInterpolation)
                    && Objects.equals(recycling, that.recycling)
                    && Objects.equals(tilePriority, that.tilePriority)
                    && Objects.equals(tileThreads, that.tileThreads)
                    && Objects.equals(memoryCapacity, that.memoryCapacity)
                    && Objects.equals(memoryThreshold, that.memoryThreshold)
                    && Objects.equals(imageIOCache, that.imageIOCache)
                    && Objects.equals(pngEncoderType, that.pngEncoderType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(allowInterpolation, recycling, tilePriority, tileThreads, memoryCapacity, memoryThreshold, imageIOCache, pngEncoderType);
        }

        @Override
        public String toString() {
            return "Jai{" +
                    "allowInterpolation=" + allowInterpolation +
                    ", recycling=" + recycling +
                    ", tilePriority=" + tilePriority +
                    ", tileThreads=" + tileThreads +
                    ", memoryCapacity=" + memoryCapacity +
                    ", memoryThreshold=" + memoryThreshold +
                    ", imageIOCache=" + imageIOCache +
                    ", pngEncoderType=" + pngEncoderType +
                    '}';
        }
    }

    //  Nested: CoverageAccess 

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CoverageAccess {

        @JsonProperty("maxPoolSize")
        private Integer maxPoolSize;

        @JsonProperty("corePoolSize")
        private Integer corePoolSize;

        @JsonProperty("keepAliveTime")
        private Long keepAliveTime;

        @JsonProperty("queueType")
        private String queueType;

        @JsonProperty("imageIOCacheThreshold")
        private Long imageIOCacheThreshold;

        public CoverageAccess() {}

        public Integer getMaxPoolSize() { return maxPoolSize; }
        public void setMaxPoolSize(Integer maxPoolSize) { this.maxPoolSize = maxPoolSize; }

        public Integer getCorePoolSize() { return corePoolSize; }
        public void setCorePoolSize(Integer corePoolSize) { this.corePoolSize = corePoolSize; }

        public Long getKeepAliveTime() { return keepAliveTime; }
        public void setKeepAliveTime(Long keepAliveTime) { this.keepAliveTime = keepAliveTime; }

        public String getQueueType() { return queueType; }
        public void setQueueType(String queueType) { this.queueType = queueType; }

        public Long getImageIOCacheThreshold() { return imageIOCacheThreshold; }
        public void setImageIOCacheThreshold(Long imageIOCacheThreshold) { this.imageIOCacheThreshold = imageIOCacheThreshold; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CoverageAccess that = (CoverageAccess) o;
            return Objects.equals(maxPoolSize, that.maxPoolSize)
                    && Objects.equals(corePoolSize, that.corePoolSize)
                    && Objects.equals(keepAliveTime, that.keepAliveTime)
                    && Objects.equals(queueType, that.queueType)
                    && Objects.equals(imageIOCacheThreshold, that.imageIOCacheThreshold);
        }

        @Override
        public int hashCode() {
            return Objects.hash(maxPoolSize, corePoolSize, keepAliveTime, queueType, imageIOCacheThreshold);
        }

        @Override
        public String toString() {
            return "CoverageAccess{" +
                    "maxPoolSize=" + maxPoolSize +
                    ", corePoolSize=" + corePoolSize +
                    ", keepAliveTime=" + keepAliveTime +
                    ", queueType=" + queueType +
                    ", imageIOCacheThreshold=" + imageIOCacheThreshold +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalSettings that = (GlobalSettings) o;
        return Objects.equals(settings, that.settings)
                && Objects.equals(jai, that.jai)
                && Objects.equals(coverageAccess, that.coverageAccess)
                && Objects.equals(updateSequence, that.updateSequence)
                && Objects.equals(featureTypeCacheSize, that.featureTypeCacheSize)
                && Objects.equals(globalServices, that.globalServices)
                && Objects.equals(xmlPostRequestLogBufferSize, that.xmlPostRequestLogBufferSize)
                && Objects.equals(trailingSlashMatch, that.trailingSlashMatch)
                && Objects.equals(webUIMode, that.webUIMode)
                && Objects.equals(allowStoredQueriesPerWorkspace, that.allowStoredQueriesPerWorkspace)
                && Objects.equals(resourceErrorHandling, that.resourceErrorHandling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settings, jai, coverageAccess, updateSequence, featureTypeCacheSize, globalServices, xmlPostRequestLogBufferSize, trailingSlashMatch, webUIMode, allowStoredQueriesPerWorkspace, resourceErrorHandling);
    }

    @Override
    public String toString() {
        return "GlobalSettings{" +
                "settings=" + settings +
                ", jai=" + jai +
                ", coverageAccess=" + coverageAccess +
                ", updateSequence=" + updateSequence +
                ", featureTypeCacheSize=" + featureTypeCacheSize +
                ", globalServices=" + globalServices +
                ", xmlPostRequestLogBufferSize=" + xmlPostRequestLogBufferSize +
                ", trailingSlashMatch=" + trailingSlashMatch +
                ", webUIMode=" + webUIMode +
                ", allowStoredQueriesPerWorkspace=" + allowStoredQueriesPerWorkspace +
                ", resourceErrorHandling=" + resourceErrorHandling +
                '}';
    }
}
