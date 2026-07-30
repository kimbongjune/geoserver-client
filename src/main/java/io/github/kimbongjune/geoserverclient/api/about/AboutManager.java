package io.github.kimbongjune.geoserverclient.api.about;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.about.SystemMetric;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;


/**
 * GeoServer About REST API client.
 *
 * <p>Source controllers:
 * <ul>
 *   <li>version/manifest: {@code src/restconfig/.../org/geoserver/rest/catalog/AboutController.java}
 *       ({@code @RequestMapping} path = "/about", {@code @GetMapping("/version")}, {@code @GetMapping("/manifest")})</li>
 *   <li>status: {@code src/restconfig/.../org/geoserver/rest/catalog/AboutStatusController.java}
 *       ({@code @RequestMapping} path = "/about/status")</li>
 *   <li>system-status: {@code src/restconfig/.../org/geoserver/rest/system/status/MonitorRest.java}
 *       ({@code @RequestMapping} path = "/about/system-status")</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * </pre>
 */
public class AboutManager extends AbstractManager {

    public AboutManager(GeoServerHttpClient httpClient,
                        SerializerFactory serializerFactory,
                        DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/about/version

    /**
     * Returns GeoServer, GeoTools, and GeoWebCache version information.
     *
     * @return version resource list (always 3 entries: GeoServer/GeoTools/GeoWebCache)
     */
    public List<AboutResource> getVersion() {
        String path = "/rest/about/version.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAboutResources(response.getBody());
    }

    /**
     * Returns filtered version information.
     *
     * @param manifestFilter regex filter on {@code @name} (nullable)
     * @param key            property key filter (nullable)
     * @param value          property value filter (nullable)
     */
    public List<AboutResource> getVersion(
            String manifestFilter, String key, String value) {
        String path = buildPath("/rest/about/version.json", manifestFilter, null, null, key, value);
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAboutResources(response.getBody());
    }

    // [2] GET /rest/about/manifest

    /**
     * Returns MANIFEST.MF entries for all JARs loaded by GeoServer (384+ entries).
     *
     * @return manifest resource list
     */
    public List<AboutResource> getManifest() {
        String path = "/rest/about/manifest.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAboutResources(response.getBody());
    }

    /**
     * Returns filtered manifest entries.
     *
     * @param manifestFilter regex filter on JAR name ({@code @name}) (nullable)
     * @param key            MANIFEST header key filter (nullable)
     * @param value          MANIFEST header value filter (nullable)
     */
    public List<AboutResource> getManifest(
            String manifestFilter, String key, String value) {
        String path = buildPath("/rest/about/manifest.json", manifestFilter, null, null, key, value);
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseAboutResources(response.getBody());
    }

    // [3] GET /rest/about/status

    /**
     * Returns the list of all registered module statuses.
     *
     * @return module status summary list (each entry contains name + href only)
     */
    public List<ModuleStatusSummary> getStatus() {
        String path = "/rest/about/status.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseModuleStatusList(response.getBody());
    }

    // [4] GET /rest/about/status/{module}

    /**
     * Returns the status of a specific module.
     *
     * @param module internal module ID (e.g., {@code gs-main}, {@code rendering-engine})
     * @return module status summary list for the given module
     * @throws ResourceNotFoundException if module not found
     */
    public List<ModuleStatusSummary> getModuleStatus(
            String module) {
        requireNonEmpty(module, "module");
        String path = "/rest/about/status/" + module + ".json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseModuleStatusList(response.getBody());
    }

    // [5] GET /rest/about/system-status

    /**
     * Returns 31 system resource metrics (CPU, memory, filesystem, etc.).
     *
     * @return system metric list
     */
    public List<SystemMetric> getSystemStatus() {
        String path = "/rest/about/system-status.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseSystemMetrics(response.getBody());
    }

    // Parsing helpers

    private List<AboutResource> parseAboutResources(
            String body) {
        return parseList(body, "about", "resource",
                AboutResource.class);
    }

    private List<ModuleStatusSummary> parseModuleStatusList(
            String body) {
        return parseList(body, "statuss", "status",
                ModuleStatusSummary.class);
    }

    private List<SystemMetric> parseSystemMetrics(
            String body) {
        return parseList(body, "metrics", "metric",
                SystemMetric.class);
    }

    private <T> List<T> parseList(String body, String rootKey, String listKey,
                                             Class<T> itemType) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode listNode =
                    root.path(rootKey).path(listKey);
            if (listNode.isMissingNode() || listNode.isNull()) {
                return new ArrayList<T>();
            }
            if (listNode.isObject()) {
                // single-item GeoServer response — wrap in list
                T item = om.treeToValue(listNode, itemType);
                List<T> single = new ArrayList<T>();
                single.add(item);
                return single;
            }
            return om.convertValue(listNode,
                    om.getTypeFactory().constructCollectionType(
                            List.class, itemType));
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse " + itemType.getSimpleName() + " list", e);
        }
    }

    // Query builder

    private String buildPath(String base, String manifest, String from, String to,
                             String key, String value) {
        StringBuilder sb = new StringBuilder(base);
        boolean first = true;
        if (manifest != null && !manifest.isEmpty()) {
            sb.append(first ? "?" : "&").append("manifest=").append(enc(manifest));
            first = false;
        }
        if (from != null && !from.isEmpty()) {
            sb.append(first ? "?" : "&").append("from=").append(enc(from));
            first = false;
        }
        if (to != null && !to.isEmpty()) {
            sb.append(first ? "?" : "&").append("to=").append(enc(to));
            first = false;
        }
        if (key != null && !key.isEmpty()) {
            sb.append(first ? "?" : "&").append("key=").append(enc(key));
            first = false;
        }
        if (value != null && !value.isEmpty()) {
            sb.append(first ? "?" : "&").append("value=").append(enc(value));
        }
        return sb.toString();
    }

    private static String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

}
