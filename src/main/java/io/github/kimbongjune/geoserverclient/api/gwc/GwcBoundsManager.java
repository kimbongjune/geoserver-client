package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTileBounds;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoWebCache (GWC) Bounds REST API client.
 *
 * <p>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/bounds/{layer}/{srs}/{type}}
 *
 * <h2>Endpoints (1)</h2>
 * <pre>{@code
 * [1] GET  /gwc/rest/bounds/{layer}/{srs}/{type}   get tile-index coverage bounds   200 / 400 / 404
 * }</pre>
 * POST/PUT/DELETE are not supported (405). Only {@code type=java} is accepted.
 *
 * <p>Returns tile-index bounds, not geographic coordinates. See {@link GwcTileBounds}.
 */
public class GwcBoundsManager extends AbstractManager {

    public GwcBoundsManager(GeoServerHttpClient httpClient,
                            SerializerFactory serializerFactory,
                            DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/bounds/{layer}/{srs}/java

    /**
     * Returns the tile-index bounds of the given layer for the given SRS/GridSet.
     * Returns 404 if the layer is unknown or the SRS is not supported by the layer.
     *
     * @param layer URL-encoded layer name (e.g., {@code sf%3Aarchsites} for {@code sf:archsites})
     * @param srs   the SRS/GridSet identifier (e.g., {@code EPSG:4326}, {@code EPSG:900913})
     */
    public GwcTileBounds getBounds(String layer, String srs) {
        requireNonEmpty(layer, "layer");
        requireNonEmpty(srs, "srs");
        String body = doGetRaw("/gwc/rest/bounds/" + layer + "/" + srs + "/java", "text/plain");
        return GwcTileBounds.parse(body);
    }
}
