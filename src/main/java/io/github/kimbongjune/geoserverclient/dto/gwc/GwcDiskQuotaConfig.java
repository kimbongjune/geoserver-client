package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;

/**
 * DTO for the GWC DiskQuota configuration. Maps {@code GET/PUT /gwc/rest/diskquota};
 * the JSON envelope key is {@code "org.geowebcache.diskquota.DiskQuotaConfig"}.
 *
 * <p>PUT requires XML — JSON PUT causes an XStream 500 error
 * (see GwcDiskQuotaManager). The {@link #quotaStore} field is read-only on PUT.
 *
 * <p><b>Known issue (2026-07-29):</b> The {@link #globalQuota} field is ignored on PUT
 * (the server accepts the request but applies no change).
 */
@JacksonXmlRootElement(localName = "org.geowebcache.diskquota.DiskQuotaConfig")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcDiskQuotaConfig {

    private Boolean enabled;
    private Integer diskBlockSize;
    private Integer cacheCleanUpFrequency;
    private String cacheCleanUpUnits;
    private Integer maxConcurrentCleanUps;
    private String globalExpirationPolicyName;
    private GwcQuota globalQuota;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String quotaStore;

    /** @return {@code true} if disk quota enforcement is enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @param enabled {@code true} to enable */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /** @return the file system block size in bytes */
    public Integer getDiskBlockSize() {
        return diskBlockSize;
    }
    /** @param diskBlockSize the block size in bytes */
    public void setDiskBlockSize(Integer diskBlockSize) {
        this.diskBlockSize = diskBlockSize;
    }

    /** @return the cache clean-up frequency */
    public Integer getCacheCleanUpFrequency() {
        return cacheCleanUpFrequency;
    }
    /** @param cacheCleanUpFrequency the frequency */
    public void setCacheCleanUpFrequency(Integer cacheCleanUpFrequency) {
        this.cacheCleanUpFrequency = cacheCleanUpFrequency;
    }

    /**
     * Cleanup interval unit. Valid values: {@code SECONDS}, {@code MINUTES}, {@code HOURS}, {@code DAYS}.
     * @return the time unit for the clean-up interval
     */
    public String getCacheCleanUpUnits() {
        return cacheCleanUpUnits;
    }
    /** @param cacheCleanUpUnits the time unit (SECONDS, MINUTES, HOURS, DAYS) */
    public void setCacheCleanUpUnits(String cacheCleanUpUnits) {
        this.cacheCleanUpUnits = cacheCleanUpUnits;
    }

    /** @return the maximum number of concurrent clean-up threads */
    public Integer getMaxConcurrentCleanUps() {
        return maxConcurrentCleanUps;
    }
    /** @param maxConcurrentCleanUps the maximum concurrent clean-ups */
    public void setMaxConcurrentCleanUps(Integer maxConcurrentCleanUps) {
        this.maxConcurrentCleanUps = maxConcurrentCleanUps;
    }

    /**
     * Returns the global expiration policy name ({@code LFU} or {@code LRU}).
     * @return the policy name
     */
    public String getGlobalExpirationPolicyName() {
        return globalExpirationPolicyName;
    }
    /** @param globalExpirationPolicyName the policy name (LFU or LRU) */
    public void setGlobalExpirationPolicyName(String globalExpirationPolicyName) {
        this.globalExpirationPolicyName = globalExpirationPolicyName;
    }

    /** @return the global disk quota */
    public GwcQuota getGlobalQuota() {
        return globalQuota;
    }
    /** @param globalQuota the global disk quota */
    public void setGlobalQuota(GwcQuota globalQuota) {
        this.globalQuota = globalQuota;
    }

    /** @return the quota store type (read-only) */
    public String getQuotaStore() {
        return quotaStore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcDiskQuotaConfig that = (GwcDiskQuotaConfig) o;
        return Objects.equals(enabled, that.enabled)
                && Objects.equals(diskBlockSize, that.diskBlockSize)
                && Objects.equals(cacheCleanUpFrequency, that.cacheCleanUpFrequency)
                && Objects.equals(cacheCleanUpUnits, that.cacheCleanUpUnits)
                && Objects.equals(maxConcurrentCleanUps, that.maxConcurrentCleanUps)
                && Objects.equals(globalExpirationPolicyName, that.globalExpirationPolicyName)
                && Objects.equals(globalQuota, that.globalQuota)
                && Objects.equals(quotaStore, that.quotaStore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, diskBlockSize, cacheCleanUpFrequency, cacheCleanUpUnits, maxConcurrentCleanUps, globalExpirationPolicyName, globalQuota, quotaStore);
    }

    @Override
    public String toString() {
        return "GwcDiskQuotaConfig{" +
                "enabled=" + enabled +
                ", diskBlockSize=" + diskBlockSize +
                ", cacheCleanUpFrequency=" + cacheCleanUpFrequency +
                ", cacheCleanUpUnits=" + cacheCleanUpUnits +
                ", maxConcurrentCleanUps=" + maxConcurrentCleanUps +
                ", globalExpirationPolicyName=" + globalExpirationPolicyName +
                ", globalQuota=" + globalQuota +
                ", quotaStore=" + quotaStore +
                '}';
    }
}
