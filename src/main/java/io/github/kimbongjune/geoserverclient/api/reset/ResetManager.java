package io.github.kimbongjune.geoserverclient.api.reset;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoServer Reset and Reload REST API client.
 *
 * <p>Source controller:
 * <ul>
 *   <li>{@code src/restconfig/.../org/geoserver/rest/CatalogReloadController.java}
 *       ({@code @RequestMapping} path = ROOT_PATH, value = "/reset" | "/reload",
 *        method = {POST, PUT})</li>
 * </ul>
 *
 *
 * <h2>Endpoint summary</h2>
 * <pre>{@code
 * [1] POST /rest/reset    Clear server caches   200
 * [2] PUT  /rest/reset    Clear server caches   200  (same as POST)
 * [3] POST /rest/reload   Reload catalog        200
 * [4] PUT  /rest/reload   Reload catalog        200  (same as POST)
 *
 * 405 Method Not Allowed (not implemented in source):
 * }</pre>
 *
 *
 * <h2>[1][2] POST|PUT /rest/reset</h2>
 * <p>Clears GeoServer's internal caches (feature type cache, raster cache, coverage reader cache, etc.)
 * without reloading configuration or the catalog. Use this after data has changed on disk and you
 * want to flush stale cached metadata.
 * Source: {@code geoServer.reset()}
 *
 * <p><b>Request Body:</b> none (no Content-Type header required)
 *
 * <p><b>Response 200:</b> empty body
 *
 *
 * <h2>[3][4] POST|PUT /rest/reload</h2>
 * <p>Reloads GeoServer configuration and catalog (workspaces, stores, layers, styles, etc.) from
 * the data directory. Also performs a cache reset implicitly.
 * Source: {@code geoServer.reload()} (internally includes reset)
 *
 * <p><b>Request Body:</b> none (no Content-Type header required)
 *
 * <p><b>Response 200:</b> empty body
 *
 *
 * @see <a href="https://docs.geoserver.org/latest/en/api/#1.0.0/reset.yaml">OGC API - reset.yaml</a>
 * @see <a href="https://docs.geoserver.org/latest/en/api/#1.0.0/reload.yaml">OGC API - reload.yaml</a>
 * @see <a href="https://docs.geoserver.org/main/en/user/rest/api/reset.html">GeoServer REST API - Reset</a>
 * @see <a href="https://docs.geoserver.org/main/en/user/rest/api/reload.html">GeoServer REST API - Reload</a>
 */
public class ResetManager extends AbstractManager {

    public ResetManager(GeoServerHttpClient httpClient,
                        SerializerFactory serializerFactory,
                        DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1][2] POST|PUT /rest/reset

    /**
     * Clears GeoServer's internal caches. Configuration and catalog are not reloaded.
     */
    public void reset() {
        String path = "/rest/reset";
        GeoServerResponse response =
                httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    // [3][4] POST|PUT /rest/reload

    /**
     * Reloads GeoServer configuration and catalog from the data directory.
     * Also clears caches (includes reset).
     */
    public void reload() {
        String path = "/rest/reload";
        GeoServerResponse response =
                httpClient.post(path, "", "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }
}
