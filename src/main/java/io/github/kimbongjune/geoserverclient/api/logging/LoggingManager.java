package io.github.kimbongjune.geoserverclient.api.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.logging.LoggingInfo;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * GeoServer Logging REST API client.
 *
 * <p>Provides read and write access to GeoServer's logging configuration:
 * log level profile, log file location, and standard-output logging toggle.
 *
 * <p>Source controllers:
 * <ul>
 *   <li>{@code src/restconfig/.../org/geoserver/rest/LoggingController.java}
 *       ({@code @RequestMapping} path = "/logging")</li>
 *   <li>{@code src/main/.../org/geoserver/config/impl/LoggingInfoImpl.java}
 *       (fields: {@code String level}, {@code String location}, {@code boolean stdOutLogging})</li>
 * </ul>
 *
 * <h2>Endpoints covered</h2>
 * <pre>{@code
 * GET /rest/logging
 * PUT /rest/logging
 * }</pre>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * LoggingManager mgr = client.logging();
 * LoggingInfo info = mgr.getLogging();
 * info.setLevel("VERBOSE_LOGGING.properties");
 * mgr.updateLogging(info);
 * }</pre>
 *
 * @since 1.0.0
 */
public class LoggingManager extends AbstractManager {

    /**
     * Constructs a new LoggingManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public LoggingManager(GeoServerHttpClient httpClient,
                          SerializerFactory serializerFactory,
                          DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/logging

    /**
     * Returns the current logging settings.
     *
     * @return current logging configuration
     */
    public LoggingInfo getLogging() {
        String path = "/rest/logging.json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parse(response.getBody());
    }

    // [2] PUT /rest/logging

    /**
     * Updates the logging settings.
     *
     * <p><b>Warning on stdOutLogging:</b> if this field is null (absent from the body), the server
     * deserializes it as {@code false} and overwrites the existing value. Always include the
     * current {@code stdOutLogging} value explicitly in the PUT body.
     *
     * @param settings updated logging settings
     */
    public void updateLogging(LoggingInfo settings) {
        requireNonNull(settings, "settings");
        String path = "/rest/logging";
        String body = serialize(settings);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // Serialization helpers

    private LoggingInfo parse(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode node = root.path("logging");
            return om.treeToValue(node,
                    LoggingInfo.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse LoggingInfo", e);
        }
    }

    private String serialize(LoggingInfo settings) {
        try {
            Map<String, Object> root =
                    new LinkedHashMap<String, Object>();
            root.put("logging", settings);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize LoggingInfo", e);
        }
    }

}
