package io.github.kimbongjune.geoserverclient.api.font;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
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
 * GeoServer Font REST API client.
 *
 * <p>Source controller:
 * {@code src/restconfig/.../org/geoserver/rest/FontListController.java}
 * ({@code @RequestMapping} path = "/fonts")
 *
 * <h2>Endpoint summary</h2>
 * <pre>{@code
 * [1] GET /rest/fonts   Returns the list of fonts registered in GeoServer   200
 *
 * 405 Method Not Allowed (only @GetMapping is defined; others are not implemented):
 *
 * 404 Not Found (endpoint does not exist):
 *   GET /rest/fonts/{name} → 404  (individual font lookup not supported)
 *
 * 401 Unauthorized:
 *   Access without authentication → 401
 *
 * 406 Not Acceptable:
 * }</pre>
 *
 * <h2>[1] GET /rest/fonts</h2>
 * <p>Returns all available font names registered in GeoServer.
 * Uses GeoTools {@code FontCache.getDefaultInstance()}, which includes JVM system fonts
 * and custom fonts installed in the {@code styles} folder of the GeoServer data directory.
 * The font list is sorted alphabetically (TreeSet).
 *
 * <p><b>Query Parameters:</b> none (source confirms @GetMapping has no parameters)
 *
 * <p><b>Supported formats:</b>
 * <ul>
 *   <li>{@code application/json} (URL: {@code /rest/fonts.json})</li>
 *   <li>{@code application/xml}  (URL: {@code /rest/fonts.xml})</li>
 *   <li>HTML: not supported (returns 406, unlike most other APIs)</li>
 * </ul>
 *
 * <p><b>Response 200 (application/json):</b>
 * <pre>{@code
 * {
 *   "fonts": [
 *     "Arial",
 *     "Arial Bold",
 *     "Courier New",
 *     "DejaVu Sans",
 *     "SansSerif",
 *     "Serif",
 *     "Times New Roman",
 *     ...
 *   ]
 * }
 * // Verified: 637 fonts returned (Docker image)
 * // When no fonts: {"fonts": []}  (empty array)
 * }</pre>
 *
 * <p><b>Response 200 (application/xml):</b>
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <root>
 *     <fonts>
 *         <entry>Arial</entry>
 *         <entry>Arial Bold</entry>
 *         <entry>Courier New</entry>
 *         ...
 *     </fonts>
 * </root>
 * // Root tag is <root>; <fonts> is a single element. Each font is an <entry>.
 * }</pre>
 *
 * <p><b>Use cases:</b>
 * <ul>
 *   <li>Verify that a {@code font-family} value exists before using it in an SLD TextSymbolizer</li>
 *   <li>Populate a font selection dropdown in a client UI</li>
 * </ul>
 */
public class FontManager extends AbstractManager {

    public FontManager(GeoServerHttpClient httpClient,
                       SerializerFactory serializerFactory,
                       DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    /**
     * Returns the list of fonts registered in GeoServer.
     *
     * <p>Includes JVM system fonts and custom fonts from the GeoServer data directory styles folder.
     * Sorted alphabetically.</p>
     *
     * @return font name list; empty list if none are registered.
     */
    public List<String> list() {
        GeoServerResponse response =
                httpClient.get("/rest/fonts.json", "application/json");
        handleErrorResponse(response, "GET", "/rest/fonts");
        return parseList(response.getBody());
    }

    // Parsing

    private List<String> parseList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            JsonNode fontsNode = root.path("fonts");
            if (fontsNode.isMissingNode() || fontsNode.isNull()) {
                return Collections.emptyList();
            }
            if (fontsNode.isArray()) {
                List<String> result = new ArrayList<String>();
                for (JsonNode n : fontsNode) {
                    result.add(n.asText());
                }
                return result;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse font list response", e);
        }
    }

}
