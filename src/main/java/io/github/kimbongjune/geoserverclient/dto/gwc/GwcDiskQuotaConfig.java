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
 * (see GwcDiskQuotaManager). The {@link #quotaStore} field is read-only on PUT.</p>
 *
 * <p><b>Known issue (2026-07-29):</b> The {@link #globalQuota} field is ignored on PUT
 * (the server accepts the request but applies no change).</p>
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

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getDiskBlockSize() { return diskBlockSize; }
    public void setDiskBlockSize(Integer diskBlockSize) { this.diskBlockSize = diskBlockSize; }

    public Integer getCacheCleanUpFrequency() { return cacheCleanUpFrequency; }
    public void setCacheCleanUpFrequency(Integer cacheCleanUpFrequency) { this.cacheCleanUpFrequency = cacheCleanUpFrequency; }

    /** Cleanup interval unit. Valid values: SECONDS, MINUTES, HOURS, DAYS. */
    public String getCacheCleanUpUnits() { return cacheCleanUpUnits; }
    public void setCacheCleanUpUnits(String cacheCleanUpUnits) { this.cacheCleanUpUnits = cacheCleanUpUnits; }

    public Integer getMaxConcurrentCleanUps() { return maxConcurrentCleanUps; }
    public void setMaxConcurrentCleanUps(Integer maxConcurrentCleanUps) { this.maxConcurrentCleanUps = maxConcurrentCleanUps; }

    /** LFU (Least Frequently Used) | LRU (Least Recently Used) */
    public String getGlobalExpirationPolicyName() { return globalExpirationPolicyName; }
    public void setGlobalExpirationPolicyName(String globalExpirationPolicyName) { this.globalExpirationPolicyName = globalExpirationPolicyName; }

    public GwcQuota getGlobalQuota() { return globalQuota; }
    public void setGlobalQuota(GwcQuota globalQuota) { this.globalQuota = globalQuota; }

    public String getQuotaStore() { return quotaStore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
