package io.github.kimbongjune.geoserverclient.api.monitoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequest;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URLEncoder;

/**
 * GeoServer Monitoring REST API manager.
 *
 * <p>Requires Monitor Core Extension plugin:
 * <ul>
 *   <li>Download: https://sourceforge.net/projects/geoserver/files/GeoServer/2.28.0/extensions/geoserver-2.28.0-monitor-plugin.zip/download</li>
 *   <li>Install: place the JAR in WEB-INF/lib</li>
 * </ul>
 *
 * <p>Verified against: GeoServer 2.28.2 / 2026-04-02 (Docker geoserver, STABLE_EXTENSIONS=importer-plugin)
 *
 * <p>Source:
 * <ul>
 *   <li>{@code gs-monitor-core-2.28.2.jar -> org/geoserver/monitor/rest/MonitorRequestController.class}</li>
 *   <li>Query class: {@code org/geoserver/monitor/Query.class} (from, to, filter, order, offset, count)</li>
 *   <li>Comparison enum: {@code Query$Comparison} - EQ, NEQ, LT, LTE, GT, GTE, IN</li>
 *   <li>SortOrder enum: {@code Query$SortOrder} - ASC, DESC</li>
 *   <li>RequestData fields in CSV export: 37 fields</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * GET     /rest/monitor/requests/{id}
 * DELETE  /rest/monitor/requests/{id}
 * </pre>
 */
public class MonitoringManager extends AbstractManager {

    private static final String JSON_ROOT = "org.geoserver.monitor.RequestDatas";
    private static final String JSON_ITEM = "org.geoserver.monitor.RequestData";


    public MonitoringManager(GeoServerHttpClient httpClient,
                             SerializerFactory serializerFactory,
                             DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/monitor/requests

    /**
     * Returns all monitored requests with no filter.
     *
     * @return list of request summaries (empty list if none, never null)
     */
    public List<MonitorRequestSummary> list() {
        return list(new MonitoringQuery());
    }

    /**
     * Returns monitored requests matching the given query parameters.
     *
     * @param query query filter DTO (nullable; null treated as no filter)
     * @return list of request summaries (empty list if none, never null)
     */
    public List<MonitorRequestSummary> list(
            MonitoringQuery query) {
        String path = buildPath("/rest/monitor/requests", query);
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseList(response.getBody());
    }

    // [3] GET /rest/monitor/requests/{id}

    /**
     * Returns the full details of a monitored request by ID.
     *
     * @param id request ID (required)
     * @return request detail object
     * @throws ResourceNotFoundException if not found (404)
     */
    public MonitorRequest get(long id) {
        String path = "/rest/monitor/requests/" + id;
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "MonitorRequest", String.valueOf(id), response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseRequest(response.getBody());
    }

    // [4] DELETE /rest/monitor/requests

    /**
     * Deletes all monitoring records.
     */
    public void deleteAll() {
        String path = "/rest/monitor/requests";
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // [4b] DELETE /rest/monitor/requests/{id} — always 405

    /**
     * Attempts to delete a monitoring record by id.
     * GeoServer always returns 405 (Method Not Allowed) for this endpoint [verified].
     *
     * @param id request ID
     * @throws GeoServerResponseException always (405)
     */
    public void deleteById(long id) {
        String path = "/rest/monitor/requests/" + id;
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // [2] GET /rest/monitor/requests — CSV / Excel / ZIP

    /**
     * Returns all monitored requests as XML.
     *
     * @return XML string (empty string if none)
     */
    public String listXml() {
        return listXml(new MonitoringQuery());
    }

    /**
     * Returns monitored requests matching the given query as XML.
     *
     * @param query query filter DTO (nullable; null treated as no filter)
     * @return XML string (empty string if none)
     */
    public String listXml(MonitoringQuery query) {
        String path = buildPath("/rest/monitor/requests", query);
        GeoServerResponse response =
                httpClient.get(path, "application/xml");
        handleErrorResponse(response, "GET", path);
        return response.getBody() != null ? response.getBody() : "";
    }

    /**
     * Returns all monitored requests as CSV.
     *
     * @return CSV string (empty string if none)
     */
    public String listCsv() {
        return listCsv(new MonitoringQuery());
    }

    /**
     * Returns monitored requests matching the given query as CSV.
     *
     * @param query query filter DTO (nullable; null treated as no filter)
     * @return CSV string
     */
    public String listCsv(MonitoringQuery query) {
        String path = buildPath("/rest/monitor/requests", query);
        GeoServerResponse response =
                httpClient.get(path, "application/csv");
        handleErrorResponse(response, "GET", path);
        return response.getBody() != null ? response.getBody() : "";
    }

    /**
     * Returns all monitored requests as an Excel binary.
     *
     * @return Excel file bytes (empty array if none)
     */
    public byte[] listExcel() {
        return listExcel(new MonitoringQuery());
    }

    /**
     * Returns monitored requests matching the given query as an Excel binary.
     *
     * @param query query filter DTO (nullable; null treated as no filter)
     * @return Excel file bytes
     */
    public byte[] listExcel(MonitoringQuery query) {
        String path = buildPath("/rest/monitor/requests", query);
        GeoServerResponse response =
                httpClient.getBinary(path, "application/vnd.ms-excel");
        handleErrorResponse(response, "GET", path);
        byte[] bytes = response.getBinaryBody();
        return bytes != null ? bytes : new byte[0];
    }

    /**
     * Returns all monitored requests as a ZIP archive.
     *
     * @return ZIP file bytes (empty array if none)
     */
    public byte[] listZip() {
        return listZip(new MonitoringQuery());
    }

    /**
     * Returns monitored requests matching the given query as a ZIP archive.
     *
     * @param query query filter DTO (nullable; null treated as no filter)
     * @return ZIP file bytes
     */
    public byte[] listZip(MonitoringQuery query) {
        String path = buildPath("/rest/monitor/requests", query);
        GeoServerResponse response =
                httpClient.getBinary(path, "application/zip");
        handleErrorResponse(response, "GET", path);
        byte[] bytes = response.getBinaryBody();
        return bytes != null ? bytes : new byte[0];
    }

    // Parsing helpers

    private List<MonitorRequestSummary> parseList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode outer = root.path(JSON_ROOT);
            if (outer.isMissingNode() || outer.isTextual()) {
                return Collections.emptyList();
            }
            JsonNode items = outer.path(JSON_ITEM);
            if (items.isMissingNode() || items.isNull()) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<MonitorRequestSummary> result =
                        new ArrayList<>();
                for (JsonNode item : items) {
                    result.add(getObjectMapper().treeToValue(
                            item,
                            MonitorRequestSummary.class));
                }
                return result;
            }
            // Single item (not wrapped in array)
            List<MonitorRequestSummary> single =
                    new ArrayList<>();
            single.add(getObjectMapper().treeToValue(
                    items,
                    MonitorRequestSummary.class));
            return single;
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse monitor request list", e);
        }
    }

    private MonitorRequest parseRequest(String body) {
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path(JSON_ITEM);
            if (node.isMissingNode()) {
                throw new SerializationException(
                        "Missing '" + JSON_ITEM + "' key in response", null);
            }
            return getObjectMapper().treeToValue(
                    node, MonitorRequest.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse monitor request", e);
        }
    }

    // Query string builder

    private String buildPath(String base,
            MonitoringQuery query) {
        if (query == null || query.isEmpty()) {
            return base;
        }
        StringBuilder sb = new StringBuilder(base).append('?');
        boolean first = true;
        if (query.getFrom() != null) {
            sb.append("from=").append(encode(query.getFrom()));
            first = false;
        }
        if (query.getTo() != null) {
            if (!first) sb.append('&');
            sb.append("to=").append(encode(query.getTo()));
            first = false;
        }
        if (query.getFilter() != null) {
            if (!first) sb.append('&');
            sb.append("filter=").append(encode(query.getFilter()));
            first = false;
        }
        if (query.getOrder() != null) {
            if (!first) sb.append('&');
            sb.append("order=").append(encode(query.getOrder()));
            first = false;
        }
        if (query.getOffset() != null) {
            if (!first) sb.append('&');
            sb.append("offset=").append(query.getOffset());
            first = false;
        }
        if (query.getCount() != null) {
            if (!first) sb.append('&');
            sb.append("count=").append(query.getCount());
            first = false;
        }
        if (query.getLive() != null) {
            if (!first) sb.append('&');
            sb.append("live=").append(query.getLive());
        }
        return sb.toString();
    }

    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
}
