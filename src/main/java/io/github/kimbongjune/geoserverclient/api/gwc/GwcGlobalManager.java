package io.github.kimbongjune.geoserverclient.api.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGlobalSettings;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoWebCache (GWC) Global configuration REST API client.
 *
 * <p>Source: {@code geowebcache/rest/src/main/java/org/geowebcache/rest/controller/ServerController.java}
 * <br>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/global}
 *
 * <h2>Endpoints (2)</h2>
 * <pre>{@code
 * [1] GET    /gwc/rest/global    get GWC global settings     200
 * [2] PUT    /gwc/rest/global    update GWC global settings  200
 * }</pre>
 * <p><b>Note:</b> POST and DELETE are not supported.
 *
 * <p><b>GET observed response (2026-07-29, GeoServer integrated):</b>
 * <pre>{@code
 * {"global":{"identifier":"...","runtimeStatsEnabled":true,"backendTimeout":120,
 *            "wmtsCiteCompliant":false,"location":"...","version":"1.24.0"}}
 * }</pre>
 * Some fields (fullWMS, cacheBypassAllowed, lockProvider, serviceInformation)
 * may be absent (null) in the integrated environment.
 *
 * <p>PUT is a partial update. {@link GwcGlobalSettings#getVersion()},
 * {@link GwcGlobalSettings#getIdentifier()}, and {@link GwcGlobalSettings#getLocation()} are
 * read-only and never sent in PUT requests.
 */
public class GwcGlobalManager extends AbstractManager {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class GlobalEnvelope {
        @JsonProperty("global")
        public GwcGlobalSettings global;

        public GlobalEnvelope() {}
        public GlobalEnvelope(GwcGlobalSettings global) { this.global = global; }
    }

    public GwcGlobalManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/global

    /** Returns the GWC global configuration. */
    public GwcGlobalSettings get() {
        GlobalEnvelope envelope = doGet("/gwc/rest/global", GlobalEnvelope.class, DataFormat.JSON);
        return envelope != null ? envelope.global : null;
    }

    // [2] PUT /gwc/rest/global

    /**
     * Partially updates the GWC global configuration.
     * Read-only fields (version, identifier, location) are never sent.
     */
    public void update(GwcGlobalSettings request) {
        requireNonNull(request, "request");
        doPut("/gwc/rest/global", new GlobalEnvelope(request), DataFormat.JSON);
    }
}
