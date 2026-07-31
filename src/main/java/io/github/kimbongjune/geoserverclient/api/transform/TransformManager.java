package io.github.kimbongjune.geoserverclient.api.transform;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoServer XSLT Transform REST API client.
 *
 * <p>Source controller (plugin-only):
 * {@code src/wfs/.../org/geoserver/wfs/xslt/rest/TransformController.java}
 * — activated only when the XSLT plugin is installed
 * ({@code @RequestMapping} path = "/services/wfs/transforms")
 *
 *
 * <h2>Plugin availability</h2>
 * <ul>
 *   <li>Stable plugins (SourceForge): {@code geoserver-2.28.2-xslt-plugin.zip} not found -> 404</li>
 *   <li>Community plugins (build.geoserver.org/geoserver/2.28.x/community-latest/): {@code xslt-plugin.zip} not listed</li>
 *   <li>Conclusion: XSLT Transform plugin is not supported on GeoServer 2.28.x</li>
 * </ul>
 *
 * <h2>Endpoint summary</h2>
 * <pre>{@code
 * [1] GET    /rest/services/wfs/transforms            200 (plugin installed) / 404 (not installed)
 * [2] GET    /rest/services/wfs/transforms/{name}     200 / 404
 * [3] POST   /rest/services/wfs/transforms            201
 * [4] PUT    /rest/services/wfs/transforms/{name}     200 / 404
 * [5] DELETE /rest/services/wfs/transforms/{name}     200 / 404
 *
 * Verified (2026-04-02):
 *   (XSLT plugin not installed -- isAvailable() always returns false)
 * }</pre>
 *
 * @author nocdev
 * @since 1.0.0
 */
public class TransformManager extends AbstractManager {

    /**
     * Constructs a new TransformManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public TransformManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    /**
     * Checks whether the XSLT Transform plugin is installed on the server.
     *
     * <p>Returns {@code true} if {@code GET /rest/services/wfs/transforms} responds with 200.
     * Returns {@code false} on 404 (plugin not installed) or any other error.
     *
     * <p>On GeoServer 2.28.x (stable and community), the XSLT plugin is not available,
     * so this method always returns {@code false}.
     *
     * @return {@code true} if XSLT Transform extension is available on the server
     */
    public boolean isAvailable() {
        try {
            GeoServerResponse response = httpClient.get(
                    "/rest/services/wfs/transforms", "application/json");
            return response != null && response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
