package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcKillType;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedRequest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedStatus;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

/**
 * GeoWebCache (GWC) Seed REST API client.
 *
 * <p>Source: {@code geowebcache/rest/src/main/java/org/geowebcache/rest/controller/SeedController.java}
 * <br>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/seed}
 *
 * <h2>Endpoints (8)</h2>
 * <pre>{@code
 * [1] GET    /gwc/rest/seed.json                 list all tasks (all layers)       200
 * [2] GET    /gwc/rest/seed/{layerName}.json     list tasks for a specific layer   200
 * [4] POST   /gwc/rest/seed/{layerName}.xml      start seed/reseed/truncate task   200 (server-verified 2026-07-29)
 * [6][7] POST /gwc/rest/seed[/{layerName}]       kill_all                          200
 * }</pre>
 *
 * <p><b>Task state codes:</b> -2=UNSET, -1=DEAD, 0=PENDING, 1=RUNNING, 2=DONE
 *
 * <p><b>JSON POST bug:</b> Jettison library array parsing issues cause JSON POST to fail.
 * {@link #seed(String, GwcSeedRequest)} always uses XML.
 *
 * <p><b>truncate threadCount:</b> threadCount is ignored for truncate tasks; always runs on 1 thread.
 *
 * <p><b>Layer name URL encoding:</b> colons (:) must be percent-encoded
 * (e.g., {@code sf:archsites} → {@code sf%3Aarchsites}).
 */
public class GwcSeedManager extends AbstractManager {

    public GwcSeedManager(GeoServerHttpClient httpClient,
                          SerializerFactory serializerFactory,
                          DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest/seed.json

    /** Returns all running/pending seed tasks across all layers. */
    public GwcSeedStatus getAllRunningTasks() {
        return doGet("/gwc/rest/seed.json", GwcSeedStatus.class, DataFormat.JSON);
    }

    // [2] GET /gwc/rest/seed/{layer}.json

    /**
     * Returns running/pending seed tasks for a specific layer.
     * Layer name must be URL-encoded (e.g., {@code sf%3Aarchsites} for {@code sf:archsites}).
     * Returns 400 if layer is not registered in GWC.
     */
    public GwcSeedStatus getRunningTasks(String layer) {
        requireNonEmpty(layer, "layer");
        return doGet("/gwc/rest/seed/" + layer + ".json", GwcSeedStatus.class, DataFormat.JSON);
    }

    // [4] POST /gwc/rest/seed/{layer}.xml

    /**
     * Starts a seed, reseed, or truncate task for the given layer asynchronously.
     * Always sent as XML — JSON causes Jettison parsing issues with array fields.
     * Monitor progress with {@link #getRunningTasks(String)}.
     */
    public void seed(String layer, GwcSeedRequest request) {
        requireNonEmpty(layer, "layer");
        requireNonNull(request, "request");
        if (request.getName() == null) {
            request.setName(layer);
        }
        String path = "/gwc/rest/seed/" + layer + ".xml";
        String content = serializerFactory.getSerializer(DataFormat.XML).serialize(request);
        doPostRaw(path, content, "application/xml", "text/plain");
    }

    // [6][7][8] POST /gwc/rest/seed[/{layer}] (kill_all)

    /**
     * Kills all tasks matching the given kill type, across all layers.
     * Must use Content-Type {@code text/plain} — form-encoded causes 500.
     *
     * @return server's plain-text/HTML confirmation message
     */
    public String killAll(GwcKillType type) {
        return killAll(null, type);
    }

    /**
     * Kills all tasks matching the given kill type for a specific layer.
     *
     * @param layer optional layer name; if null, applies to all layers
     * @return server's plain-text/HTML confirmation message
     */
    public String killAll(String layer, GwcKillType type) {
        requireNonNull(type, "type");
        String path = layer != null && !layer.isEmpty() ? "/gwc/rest/seed/" + layer : "/gwc/rest/seed";
        GeoServerResponse response = httpClient.post(path, "kill_all=" + type.getValue(),
                "text/plain", "text/plain");
        handleErrorResponse(response, "POST", path);
        return response.getBody();
    }
}
