package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterContent;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterUpdateType;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;

import java.io.File;

/**
 * GeoWebCache (GWC) FilterUpdate REST API client.
 *
 * <p>Source: {@code geowebcache/rest/src/main/java/org/geowebcache/rest/controller/FilterUpdateController.java}
 * <br>GeoServer integrated GWC path: {@code /geoserver/gwc/rest/filter/{filterName}/update/{updateType}}
 *
 * <h2>Endpoints (1)</h2>
 * <pre>{@code
 * [1] POST /gwc/rest/filter/{filterName}/update/{updateType}   run filter update   200 / 400
 * }</pre>
 * GET/PUT/DELETE are not supported (405).
 *
 * <p><b>DEADLOCK WARNING:</b> Using the server's own WMS URL as {@code capabilitiesURL}
 * can cause a Tomcat thread deadlock [source-verified].
 *
 * <p><b>Note:</b> unknown updateType returns 400 {@code "Unknow update type: {updateType}"}
 * (the typo "Unknow" instead of "Unknown" is in the upstream source code).
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * GwcFilterUpdateManager mgr = client.gwcFilterUpdate();
 * mgr.updateFilterXml("my-wms-filter", GwcFilterContent.empty());
 * }</pre>
 *
 * @since 1.0.0
 */
public class GwcFilterUpdateManager extends AbstractManager {

    /**
     * Constructs a new GwcFilterUpdateManager.
     *
     * @param httpClient        HTTP client used to communicate with GeoServer
     * @param serializerFactory factory for JSON/XML serializers
     * @param defaultFormat     default serialization format (typically JSON)
     */
    public GwcFilterUpdateManager(GeoServerHttpClient httpClient,
                                  SerializerFactory serializerFactory,
                                  DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] POST .../update/xml

    /**
     * Updates a WMSRasterFilter's allowed values via WMS GetCapabilities.
     *
     * @param filterName the filter name (as registered in GWC)
     * @param content    WMSRasterFilter definition body; use {@link GwcFilterContent#empty()}
     *                   to update using the filter's existing definition
     * @return {@code "Filter update completed, no problems encountered."} on success
     */
    public String updateFilterXml(String filterName, GwcFilterContent content) {
        requireNonEmpty(filterName, "filterName");
        requireNonNull(content, "content");
        String path = "/gwc/rest/filter/" + filterName + "/update/" + GwcFilterUpdateType.XML.getValue();
        GeoServerResponse response = httpClient.post(
                path, content.getXmlBody(), "text/xml", "text/plain");
        handleErrorResponse(response, "POST", path);
        return response.getBody();
    }

    // [1] POST .../update/zip

    /**
     * Updates a parameter filter's allowed values from a ZIP file listing.
     * File names inside the ZIP must follow {@code {prefix}_{gridSetId}_{zoom}.{ext}}.
     *
     * @param filterName filter name (as registered in GWC; required)
     * @param zipFile    ZIP file containing the parameter value listing (required)
     * @return {@code "Filter update completed, no problems encountered."} on success
     */
    public String updateFilterZip(String filterName, File zipFile) {
        requireNonEmpty(filterName, "filterName");
        requireNonNull(zipFile, "zipFile");
        String path = "/gwc/rest/filter/" + filterName + "/update/" + GwcFilterUpdateType.ZIP.getValue();
        GeoServerResponse response = httpClient.postFile(path, zipFile, "application/zip", "text/plain");
        handleErrorResponse(response, "POST", path);
        return response.getBody();
    }
}
