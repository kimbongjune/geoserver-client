package io.github.kimbongjune.geoserverclient.api.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcDiskQuotaConfig;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoWebCache (GWC) DiskQuota REST API client.
 *
 * <p>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/diskquota}
 *
 * <h2>Endpoints (2)</h2>
 * <pre>{@code
 * [1] GET  /gwc/rest/diskquota[.json/.xml]   get disk quota config    200 / 500
 * [2] PUT  /gwc/rest/diskquota[.json/.xml]   update disk quota config  200 / 500
 * }</pre>
 *
 * <p><b>DiskQuota module not installed:</b> both GET and PUT return 500 if the module is absent.
 *
 * <p><b>JSON PUT:</b> JSON PUT causes an XStream 500.
 * {@link #update(GwcDiskQuotaConfig)} always sends XML.
 *
 * <p><b>Validation errors:</b>
 * <pre>{@code
 * cacheCleanUpFrequency < 0  → 500 Internal Server Error
 * maxConcurrentCleanUps <= 0 → 500 Internal Server Error
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * GwcDiskQuotaManager mgr = client.gwcDiskQuota();
 * GwcDiskQuotaConfig cfg = mgr.get();
 * cfg.setEnabled(true);
 * mgr.update(cfg);
 * }</pre>
 *
 * @since 1.0.0
 */
public class GwcDiskQuotaManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class DiskQuotaEnvelope {
        @JsonProperty("org.geowebcache.diskquota.DiskQuotaConfig")
        public GwcDiskQuotaConfig config;
    }

    /**
     * Constructs a new GwcDiskQuotaManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public GwcDiskQuotaManager(GeoServerHttpClient httpClient,
                               SerializerFactory serializerFactory,
                               DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/diskquota

    /**
     * Returns the GWC disk quota configuration.
     * <p>The URL extension (not the Accept header) decides the response format on this
     * endpoint [server-verified] — this method always requests {@code .json} explicitly.
     * @return the disk quota configuration, or {@code null} if unavailable
     */
    public GwcDiskQuotaConfig get() {
        DiskQuotaEnvelope envelope = doGet("/gwc/rest/diskquota.json", DiskQuotaEnvelope.class, DataFormat.JSON);
        return envelope != null ? envelope.config : null;
    }

    // [2] PUT /gwc/rest/diskquota

    /**
     * Updates the disk quota configuration. Always sent as XML — JSON causes
     * an XStream 500 on this endpoint. Fields omitted from {@code request}
     * (e.g. {@code globalQuota}) are preserved by the server rather than cleared
     * [server-verified 2026-07-29].
     * @param request the configuration to apply
     */
    public void update(GwcDiskQuotaConfig request) {
        requireNonNull(request, "request");
        doPut("/gwc/rest/diskquota.xml", request, DataFormat.XML);
    }
}
