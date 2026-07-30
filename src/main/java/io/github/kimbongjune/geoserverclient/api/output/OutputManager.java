package io.github.kimbongjune.geoserverclient.api.output;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convenience wrapper for GeoServer output-format related REST endpoints.
 *
 * <p>GeoServer does not have a dedicated "output formats" REST endpoint. This class groups the
 * existing font and template listing endpoints for convenience. For full CRUD operations use
 * {@code FontManager} and {@code TemplateManager} directly.
 *
 *
 * <h2>Wrapped endpoints</h2>
 * <pre>{@code
 * [1] GET /rest/fonts      -> font list (raw JSON string)  HTTP 200
 * [2] GET /rest/templates  -> root-level template list     HTTP 200
 *
 * Verified responses (GeoServer 2.28.2, 2026-04-02):
 * }</pre>
 *
 * @author nocdev
 * @since 1.0.0
 * @see io.github.kimbongjune.geoserverclient.api.font.FontManager
 * @see io.github.kimbongjune.geoserverclient.api.template.TemplateManager
 */
public class OutputManager extends AbstractManager {

    public OutputManager(GeoServerHttpClient httpClient,
                         SerializerFactory serializerFactory,
                         DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    /**
     * Returns the list of fonts registered in GeoServer.
     * For a full font management API use {@code FontManager.list()}.
     *
     * @return list of font names, never null; empty when none are registered
     */
    public List<String> getFonts() {
        GeoServerResponse response = httpClient.get("/rest/fonts.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/fonts");
        try {
            JsonNode fontsNode = getObjectMapper().readTree(response.getBody()).path("fonts");
            List<String> result = new ArrayList<String>();
            if (fontsNode.isArray()) {
                for (JsonNode n : fontsNode) {
                    result.add(n.asText());
                }
            }
            return result;
        } catch (IOException e) {
            throw new SerializationException("Failed to parse fonts response", e);
        }
    }

    /**
     * Returns the list of root-level FreeMarker templates registered in GeoServer.
     * For full template CRUD use {@code TemplateManager}.
     *
     * @return list of template infos, never null; empty when none exist
     */
    public List<TemplateInfo> getTemplates() {
        GeoServerResponse response = httpClient.get("/rest/templates.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/templates");
        try {
            JsonNode outer = getObjectMapper().readTree(response.getBody())
                    .path("org.geoserver.rest.catalog.TemplateInfos");
            if (outer.isMissingNode() || outer.isTextual()) {
                return Collections.emptyList();
            }
            JsonNode items = outer.path("org.geoserver.rest.catalog.TemplateInfo");
            if (items.isMissingNode() || items.isNull()) {
                return Collections.emptyList();
            }
            if (!items.isArray()) {
                return Collections.singletonList(getObjectMapper().treeToValue(items, TemplateInfo.class));
            }
            return getObjectMapper().readValue(items.traverse(),
                    getObjectMapper().getTypeFactory().constructCollectionType(List.class, TemplateInfo.class));
        } catch (IOException e) {
            throw new SerializationException("Failed to parse templates response", e);
        }
    }

}
