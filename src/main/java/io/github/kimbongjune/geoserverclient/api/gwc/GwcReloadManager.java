package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GeoWebCache (GWC) Reload REST API client.
 *
 * <p>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/reload}
 * (distinct from the GeoServer REST {@code /rest/reload} endpoint — this one reloads GWC only).
 *
 * <h2>Endpoints (1)</h2>
 * <pre>{@code
 * [1] POST /gwc/rest/reload    reload all GWC tile layer config    200 / 400
 * }</pre>
 * <p>GET/PUT/DELETE are not supported (405). The {@code reload_configuration} parameter
 * must be present in the request body or query string [server-verified].
 *
 * <p><b>Success response (2026-07-29):</b> HTML body containing
 * {@code "...Read 1 layers from configuration resources..."}
 */
public class GwcReloadManager extends AbstractManager {

    private static final Pattern LAYER_COUNT = Pattern.compile("Read (\\d+) layers");

    public GwcReloadManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] POST /gwc/rest/reload

    /** Triggers a GWC configuration reload. Returns 400 if the server rejects the request. */
    public GwcReloadResult reload() {
        String path = "/gwc/rest/reload";
        GeoServerResponse response = httpClient.post(path, "reload_configuration=1",
                "application/x-www-form-urlencoded", "text/html");
        handleErrorResponse(response, "POST", path);

        String body = response.getBody();
        boolean reloaded = body != null && body.contains("reloaded");
        Integer count = null;
        if (body != null) {
            Matcher m = LAYER_COUNT.matcher(body);
            if (m.find()) {
                count = Integer.valueOf(m.group(1));
            }
        }
        return new GwcReloadResult(reloaded, count, body);
    }
}
