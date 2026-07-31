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

    /** Constructs an empty {@code GlobalSettings} for deserialization. */
    public GlobalSettings() {}

    /** @return the GeoServer settings block */
    public GeoServerSettings getSettings() {
        return settings;
    }
    /**
     * Sets the GeoServer settings block.
     * @param settings the settings
     */
    public void setSettings(GeoServerSettings settings) {
        this.settings = settings;
    }

    /** @return the JAI settings block */
    public Jai getJai() {
        return jai;
    }
    /**
     * Sets the JAI settings block.
     * @param jai the JAI settings
     */
    public void setJai(Jai jai) {
        this.jai = jai;
    }

    /** @return the coverage access settings block */
    public CoverageAccess getCoverageAccess() {
        return coverageAccess;
    }
    /**
     * Sets the coverage access settings block.
     * @param coverageAccess the coverage access settings
     */
    public void setCoverageAccess(CoverageAccess coverageAccess) {
        this.coverageAccess = coverageAccess;
    }

    /** @return the update sequence number */
    public Integer getUpdateSequence() {
        return updateSequence;
    }
    /**
     * Sets the update sequence number.
     * @param updateSequence the update sequence
     */
    public void setUpdateSequence(Integer updateSequence) {
        this.updateSequence = updateSequence;
    }

    /** @return the feature type cache size */
    public Integer getFeatureTypeCacheSize() {
        return featureTypeCacheSize;
    }
    /**
     * Sets the feature type cache size.
     * @param featureTypeCacheSize the cache size
     */
    public void setFeatureTypeCacheSize(Integer featureTypeCacheSize) {
        this.featureTypeCacheSize = featureTypeCacheSize;
    }

    /** @return {@code true} if global services are enabled */
    public Boolean getGlobalServices() {
        return globalServices;
    }
    /**
     * Sets whether global services are enabled.
     * @param globalServices {@code true} to enable
     */
    public void setGlobalServices(Boolean globalServices) {
        this.globalServices = globalServices;
    }

    /** @return the XML POST request log buffer size */
    public Integer getXmlPostRequestLogBufferSize() {
        return xmlPostRequestLogBufferSize;
    }
    /**
     * Sets the XML POST request log buffer size.
     * @param xmlPostRequestLogBufferSize the buffer size
     */
    public void setXmlPostRequestLogBufferSize(Integer xmlPostRequestLogBufferSize) {
        this.xmlPostRequestLogBufferSize = xmlPostRequestLogBufferSize;
    }

    /** @return {@code true} if trailing slash matching is enabled */
    public Boolean getTrailingSlashMatch() {
        return trailingSlashMatch;
    }
    /**
     * Sets whether trailing slash matching is enabled.
     * @param trailingSlashMatch {@code true} to enable
     */
    public void setTrailingSlashMatch(Boolean trailingSlashMatch) {
        this.trailingSlashMatch = trailingSlashMatch;
    }

    /** @return the web UI mode */
    public String getWebUIMode() {
        return webUIMode;
    }
    /**
     * Sets the web UI mode.
     * @param webUIMode the web UI mode
     */
    public void setWebUIMode(String webUIMode) {
        this.webUIMode = webUIMode;
    }

    /** @return {@code true} if stored queries per workspace are allowed */
    public Boolean getAllowStoredQueriesPerWorkspace() {
        return allowStoredQueriesPerWorkspace;
    }
    /**
     * Sets whether stored queries per workspace are allowed.
     * @param allowStoredQueriesPerWorkspace {@code true} to allow
     */
    public void setAllowStoredQueriesPerWorkspace(Boolean allowStoredQueriesPerWorkspace) {
        this.allowStoredQueriesPerWorkspace = allowStoredQueriesPerWorkspace;
    }

    /** @return the resource error handling mode */
    public String getResourceErrorHandling() {
        return resourceErrorHandling;
    }
    /**
     * Sets the resource error handling mode.
     * @param resourceErrorHandling the mode
     */
    public void setResourceErrorHandling(String resourceErrorHandling) {
        this.resourceErrorHandling = resourceErrorHandling;
    }

    /** Nested GeoServer-specific settings (contact, charset, decimal places, etc.). */
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

        /** Constructs an empty {@code GeoServerSettings} for deserialization. */
        public GeoServerSettings() {}

        /** @return the settings ID */
        public String getId() {
            return id;
        }
        /**
         * Sets the settings ID.
         * @param id the ID
         */
        public void setId(String id) {
            this.id = id;
        }

        /** @return the contact information */
        public Contact getContact() {
            return contact;
        }
        /**
         * Sets the contact information.
         * @param contact the contact
         */
        public void setContact(Contact contact) {
            this.contact = contact;
        }

        /** @return the default character set */
        public String getCharset() {
            return charset;
        }
        /**
         * Sets the default character set.
         * @param charset the charset
         */
        public void setCharset(String charset) {
            this.charset = charset;
        }

        /** @return the number of decimal places */
        public Integer getNumDecimals() {
            return numDecimals;
        }
        /**
         * Sets the number of decimal places.
         * @param numDecimals the decimal count
         */
        public void setNumDecimals(Integer numDecimals) {
            this.numDecimals = numDecimals;
        }

        /** @return {@code true} if verbose output is enabled */
        public Boolean getVerbose() {
            return verbose;
        }
        /**
         * Sets verbose output.
         * @param verbose {@code true} to enable
         */
        public void setVerbose(Boolean verbose) {
            this.verbose = verbose;
        }

        /** @return {@code true} if verbose exceptions are enabled */
        public Boolean getVerboseExceptions() {
            return verboseExceptions;
        }
        /**
         * Sets verbose exceptions.
         * @param verboseExceptions {@code true} to enable
         */
        public void setVerboseExceptions(Boolean verboseExceptions) {
            this.verboseExceptions = verboseExceptions;
        }

        /** @return {@code true} if the local workspace includes the prefix */
        public Boolean getLocalWorkspaceIncludesPrefix() {
            return localWorkspaceIncludesPrefix;
        }
        /**
         * Sets whether local workspace includes prefix.
         * @param localWorkspaceIncludesPrefix {@code true} to include
         */
        public void setLocalWorkspaceIncludesPrefix(Boolean localWorkspaceIncludesPrefix) {
            this.localWorkspaceIncludesPrefix = localWorkspaceIncludesPrefix;
        }

        /** @return {@code true} if created-time columns are shown in admin list */
        public Boolean getShowCreatedTimeColumnsInAdminList() {
            return showCreatedTimeColumnsInAdminList;
        }
        /**
         * Sets whether created-time columns are shown.
         * @param v {@code true} to show
         */
        public void setShowCreatedTimeColumnsInAdminList(Boolean v) {
            this.showCreatedTimeColumnsInAdminList = v;
        }

        /** @return {@code true} if modified-time columns are shown in admin list */
        public Boolean getShowModifiedTimeColumnsInAdminList() {
            return showModifiedTimeColumnsInAdminList;
        }
        /**
         * Sets whether modified-time columns are shown.
         * @param v {@code true} to show
         */
        public void setShowModifiedTimeColumnsInAdminList(Boolean v) {
            this.showModifiedTimeColumnsInAdminList = v;
        }

        /** @return {@code true} if modified-user column is shown in admin list */
        public Boolean getShowModifiedUserAdminList() {
            return showModifiedUserAdminList;
        }
        /**
         * Sets whether modified-user column is shown.
         * @param v {@code true} to show
         */
        public void setShowModifiedUserAdminList(Boolean v) {
            this.showModifiedUserAdminList = v;
        }

        /** @return {@code true} if proxy URL from headers is used */
        public Boolean getUseHeadersProxyURL() {
            return useHeadersProxyURL;
        }
        /**
         * Sets whether proxy URL from headers is used.
         * @param useHeadersProxyURL {@code true} to use
         */
        public void setUseHeadersProxyURL(Boolean useHeadersProxyURL) {
            this.useHeadersProxyURL = useHeadersProxyURL;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** Java Advanced Imaging (JAI) settings (tile size, memory, PNG encoder, etc.). */
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

        /** Constructs an empty {@code Jai} for deserialization. */
        public Jai() {}

        /** @return {@code true} if interpolation is allowed */
        public Boolean getAllowInterpolation() {
            return allowInterpolation;
        }
        /**
         * Sets whether interpolation is allowed.
         * @param allowInterpolation {@code true} to allow
         */
        public void setAllowInterpolation(Boolean allowInterpolation) {
            this.allowInterpolation = allowInterpolation;
        }

        /** @return {@code true} if tile recycling is enabled */
        public Boolean getRecycling() {
            return recycling;
        }
        /**
         * Sets whether tile recycling is enabled.
         * @param recycling {@code true} to enable
         */
        public void setRecycling(Boolean recycling) {
            this.recycling = recycling;
        }

        /** @return the tile processing priority */
        public Integer getTilePriority() {
            return tilePriority;
        }
        /**
         * Sets the tile processing priority.
         * @param tilePriority the priority
         */
        public void setTilePriority(Integer tilePriority) {
            this.tilePriority = tilePriority;
        }

        /** @return the number of tile processing threads */
        public Integer getTileThreads() {
            return tileThreads;
        }
        /**
         * Sets the number of tile processing threads.
         * @param tileThreads the thread count
         */
        public void setTileThreads(Integer tileThreads) {
            this.tileThreads = tileThreads;
        }

        /** @return the memory capacity fraction (0.0-1.0) */
        public Double getMemoryCapacity() {
            return memoryCapacity;
        }
        /**
         * Sets the memory capacity fraction.
         * @param memoryCapacity the fraction (0.0-1.0)
         */
        public void setMemoryCapacity(Double memoryCapacity) {
            this.memoryCapacity = memoryCapacity;
        }

        /** @return the memory threshold fraction (0.0-1.0) */
        public Double getMemoryThreshold() {
            return memoryThreshold;
        }
        /**
         * Sets the memory threshold fraction.
         * @param memoryThreshold the fraction (0.0-1.0)
         */
        public void setMemoryThreshold(Double memoryThreshold) {
            this.memoryThreshold = memoryThreshold;
        }

        /** @return {@code true} if ImageIO caching is enabled */
        public Boolean getImageIOCache() {
            return imageIOCache;
        }
        /**
         * Sets whether ImageIO caching is enabled.
         * @param imageIOCache {@code true} to enable
         */
        public void setImageIOCache(Boolean imageIOCache) {
            this.imageIOCache = imageIOCache;
        }

        /** @return the PNG encoder type */
        public String getPngEncoderType() {
            return pngEncoderType;
        }
        /**
         * Sets the PNG encoder type.
         * @param pngEncoderType the encoder type
         */
        public void setPngEncoderType(String pngEncoderType) {
            this.pngEncoderType = pngEncoderType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

    /** Coverage access thread pool settings (pool size, queue type, cache threshold). */
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

        /** Constructs an empty {@code CoverageAccess} for deserialization. */
        public CoverageAccess() {}

        /** @return the maximum thread pool size */
        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }
        /**
         * Sets the maximum thread pool size.
         * @param maxPoolSize the pool size
         */
        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        /** @return the core thread pool size */
        public Integer getCorePoolSize() {
            return corePoolSize;
        }
        /**
         * Sets the core thread pool size.
         * @param corePoolSize the pool size
         */
        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        /** @return the thread keep-alive time in milliseconds */
        public Long getKeepAliveTime() {
            return keepAliveTime;
        }
        /**
         * Sets the thread keep-alive time.
         * @param keepAliveTime the keep-alive time in milliseconds
         */
        public void setKeepAliveTime(Long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        /** @return the work queue type */
        public String getQueueType() {
            return queueType;
        }
        /**
         * Sets the work queue type.
         * @param queueType the queue type
         */
        public void setQueueType(String queueType) {
            this.queueType = queueType;
        }

        /** @return the ImageIO cache threshold in bytes */
        public Long getImageIOCacheThreshold() {
            return imageIOCacheThreshold;
        }
        /**
         * Sets the ImageIO cache threshold.
         * @param imageIOCacheThreshold the threshold in bytes
         */
        public void setImageIOCacheThreshold(Long imageIOCacheThreshold) {
            this.imageIOCacheThreshold = imageIOCacheThreshold;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
