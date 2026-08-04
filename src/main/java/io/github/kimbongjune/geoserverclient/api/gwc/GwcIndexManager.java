package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcIndexResult;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GeoWebCache (GWC) REST resource index client.
 *
 * <h2>Endpoints (1)</h2>
 * <pre>{@code
 * [1] GET /gwc/rest    lists available GWC REST resource links    200 (text/html only)
 * }</pre>
 * <p>Only {@code text/html} is supported — {@code .json}/{@code .xml} suffixes return 406.
 * Verified against GeoServer 2.28.2.
 *
 * @since 1.0.1
 */
public class GwcIndexManager extends AbstractManager {

    private static final Pattern HREF = Pattern.compile("href=\"([^\"]+)\"");

    public GwcIndexManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /gwc/rest

    /**
     * Fetches the GWC REST resource index page and parses out resource links.
     *
     * @return the parsed resource links plus the raw HTML body
     */
    public GwcIndexResult getIndex() {
        String path = "/gwc/rest";
        GeoServerResponse response = httpClient.get(path, "text/html");
        handleErrorResponse(response, "GET", path);

        String body = response.getBody();
        List<String> links = new ArrayList<>();
        if (body != null) {
            Matcher m = HREF.matcher(body);
            while (m.find()) {
                String href = m.group(1);
                // Skip embedded images/logos, keep only content links.
                if (!href.contains(".png") && !href.contains(".jpg") && !href.contains(".gif")) {
                    links.add(href);
                }
            }
        }
        return new GwcIndexResult(links, body);
    }
}
