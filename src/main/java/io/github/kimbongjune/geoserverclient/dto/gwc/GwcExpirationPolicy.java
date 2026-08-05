package io.github.kimbongjune.geoserverclient.dto.gwc;

/**
 * GeoWebCache disk quota eviction policy — which cached tiles are purged first once the global
 * quota is exceeded.
 *
 * <p><b>Unverified against a live server:</b> the {@code /gwc/rest/diskquota} endpoint returns
 * HTTP 404 on GeoServer 2.28.2 (stable and community), so these values could not be confirmed
 * end-to-end — see {@link GwcDiskQuotaConfig} Javadoc, which already documented "Valid values:
 * LFU or LRU" prior to this enum's introduction.
 */
public enum GwcExpirationPolicy {
    /** Least Frequently Used: evict the tiles accessed the fewest times. */
    LFU,
    /** Least Recently Used: evict the tiles accessed longest ago. */
    LRU
}
