package io.github.kimbongjune.geoserverclient.api.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateContent;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GeoServer Template REST API manager.
 *
 * <p>Verified against: GeoServer 2.28.2 / 2026-04-02 (Kartoza Docker kartoza/geoserver)
 *
 * <p>Source:
 * <ul>
 *   <li>{@code src/restconfig/.../org/geoserver/rest/catalog/TemplateController.java}</li>
 *   <li>Template format: FreeMarker ({@code .ftl}) — used to customize GeoServer WFS/WMS responses</li>
 *   <li>Storage paths in GeoServer Data Directory:
 *     <ul>
 *       <li>Root: {@code workspaces/{templateName}.ftl}</li>
 *       <li>Workspace: {@code workspaces/{ws}/{templateName}.ftl}</li>
 *       <li>DataStore: {@code workspaces/{ws}/{ds}/{templateName}.ftl}</li>
 *       <li>FeatureType: {@code workspaces/{ws}/{ds}/{ft}/{templateName}.ftl}</li>
 *       <li>CoverageStore: {@code workspaces/{ws}/{cs}/{templateName}.ftl}</li>
 *       <li>Coverage: {@code workspaces/{ws}/{cs}/{coverage}/{templateName}.ftl}</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * POST  /rest/templates/{name}
 * </pre>
 */
public class TemplateManager extends AbstractManager {

    public TemplateManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // Root level

    /** [1] Lists all root-level FreeMarker templates. */
    public List<TemplateInfo> list() {
        String path = "/rest/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [2] Returns the raw FTL content of a root-level template. */
    public TemplateContent get(String name) {
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/templates/" + name, "text/plain"));
    }

    /** [3] Creates or updates a root-level FreeMarker template. Always returns 201. */
    public void put(String name, TemplateContent content) {
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/templates/" + name, content.getBody(), "text/plain", "text/plain");
    }

    /** [4] Deletes a root-level template. */
    public void delete(String name) {
        requireNonEmpty(name, "name");
        String path = "/rest/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template", name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Workspace level

    /** [5] Lists templates for a workspace. */
    public List<TemplateInfo> listByWorkspace(
            String workspace) {
        requireNonEmpty(workspace, "workspace");
        String path = "/rest/workspaces/" + workspace + "/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [6] Returns the raw FTL content of a workspace-level template. */
    public TemplateContent getByWorkspace(String workspace, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/workspaces/" + workspace + "/templates/" + name, "text/plain"));
    }

    /** [7] Creates or updates a workspace-level template. Always returns 201. */
    public void putByWorkspace(String workspace, String name,
            TemplateContent content) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/workspaces/" + workspace + "/templates/" + name,
                content.getBody(), "text/plain", "text/plain");
    }

    /** [8] Deletes a workspace-level template. */
    public void deleteByWorkspace(String workspace, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspace + "/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template", workspace + "/" + name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // DataStore level

    /** [9] Lists templates for a datastore. */
    public List<TemplateInfo> listByDatastore(
            String workspace, String datastore) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        String path = "/rest/workspaces/" + workspace + "/datastores/" + datastore + "/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [10] Returns the raw FTL content of a datastore-level template. */
    public TemplateContent getByDatastore(
            String workspace, String datastore, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/workspaces/" + workspace + "/datastores/"
                        + datastore + "/templates/" + name, "text/plain"));
    }

    /** [11] Creates or updates a datastore-level template. Always returns 201. */
    public void putByDatastore(String workspace, String datastore, String name,
            TemplateContent content) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/workspaces/" + workspace + "/datastores/"
                + datastore + "/templates/" + name, content.getBody(), "text/plain", "text/plain");
    }

    /** [12] Deletes a datastore-level template. */
    public void deleteByDatastore(String workspace, String datastore, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspace + "/datastores/"
                + datastore + "/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template", workspace + "/" + datastore + "/" + name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // FeatureType level

    /** [13] Lists templates for a featuretype. */
    public List<TemplateInfo> listByFeatureType(
            String workspace, String datastore, String featuretype) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(featuretype, "featuretype");
        String path = "/rest/workspaces/" + workspace + "/datastores/" + datastore
                + "/featuretypes/" + featuretype + "/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [14] Returns the raw FTL content of a featuretype-level template. */
    public TemplateContent getByFeatureType(String workspace, String datastore,
                                   String featuretype, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(featuretype, "featuretype");
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/workspaces/" + workspace + "/datastores/" + datastore
                        + "/featuretypes/" + featuretype + "/templates/" + name, "text/plain"));
    }

    /** [15] Creates or updates a featuretype-level template. Always returns 201. */
    public void putByFeatureType(String workspace, String datastore,
                                 String featuretype, String name,
                                 TemplateContent content) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(featuretype, "featuretype");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/workspaces/" + workspace + "/datastores/" + datastore
                + "/featuretypes/" + featuretype + "/templates/" + name,
                content.getBody(), "text/plain", "text/plain");
    }

    /** [16] Deletes a featuretype-level template. */
    public void deleteByFeatureType(String workspace, String datastore,
                                    String featuretype, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(datastore, "datastore");
        requireNonEmpty(featuretype, "featuretype");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspace + "/datastores/" + datastore
                + "/featuretypes/" + featuretype + "/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template", workspace + "/" + datastore + "/" + featuretype + "/" + name,
                    response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // CoverageStore level

    /** [17] Lists templates for a coveragestore. */
    public List<TemplateInfo> listByCoverageStore(
            String workspace, String coveragestore) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        String path = "/rest/workspaces/" + workspace
                + "/coveragestores/" + coveragestore + "/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [18] Returns the raw FTL content of a coveragestore-level template. */
    public TemplateContent getByCoverageStore(
            String workspace, String coveragestore, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/workspaces/" + workspace + "/coveragestores/"
                        + coveragestore + "/templates/" + name, "text/plain"));
    }

    /** [19] Creates or updates a coveragestore-level template. Always returns 201. */
    public void putByCoverageStore(String workspace, String coveragestore,
                                   String name, TemplateContent content) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/workspaces/" + workspace + "/coveragestores/"
                + coveragestore + "/templates/" + name, content.getBody(), "text/plain", "text/plain");
    }

    /** [20] Deletes a coveragestore-level template. */
    public void deleteByCoverageStore(String workspace, String coveragestore, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspace + "/coveragestores/"
                + coveragestore + "/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template", workspace + "/" + coveragestore + "/" + name, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Coverage level

    /** [21] Lists templates for a coverage. */
    public List<TemplateInfo> listByCoverage(
            String workspace, String coveragestore, String coverage) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(coverage, "coverage");
        String path = "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore
                + "/coverages/" + coverage + "/templates";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTemplates(response.getBody());
    }

    /** [22] Returns the raw FTL content of a coverage-level template. */
    public TemplateContent getByCoverage(String workspace, String coveragestore,
                                String coverage, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(coverage, "coverage");
        requireNonEmpty(name, "name");
        return TemplateContent.of(
                doGetRaw("/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore
                        + "/coverages/" + coverage + "/templates/" + name, "text/plain"));
    }

    /** [23] Creates or updates a coverage-level template. Always returns 201. */
    public void putByCoverage(String workspace, String coveragestore,
                              String coverage, String name,
                              TemplateContent content) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(coverage, "coverage");
        requireNonEmpty(name, "name");
        requireNonNull(content, "content");
        doPutRaw("/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore
                + "/coverages/" + coverage + "/templates/" + name,
                content.getBody(), "text/plain", "text/plain");
    }

    /** [24] Deletes a coverage-level template. */
    public void deleteByCoverage(String workspace, String coveragestore,
                                 String coverage, String name) {
        requireNonEmpty(workspace, "workspace");
        requireNonEmpty(coveragestore, "coveragestore");
        requireNonEmpty(coverage, "coverage");
        requireNonEmpty(name, "name");
        String path = "/rest/workspaces/" + workspace + "/coveragestores/" + coveragestore
                + "/coverages/" + coverage + "/templates/" + name;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Template",
                    workspace + "/" + coveragestore + "/" + coverage + "/" + name,
                    response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Private: parsing helpers

    private List<TemplateInfo> parseTemplates(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper mapper =
                    new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode outer =
                    root.path("org.geoserver.rest.catalog.TemplateInfos");
            // Empty response: {"org.geoserver.rest.catalog.TemplateInfos":""}
            if (outer.isMissingNode() || outer.isTextual()) {
                return Collections.emptyList();
            }
            JsonNode items =
                    outer.path("org.geoserver.rest.catalog.TemplateInfo");
            if (items.isMissingNode() || items.isNull()) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<TemplateInfo> result =
                        new ArrayList<>();
                for (JsonNode item : items) {
                    result.add(mapper.convertValue(
                            item, TemplateInfo.class));
                }
                return result;
            } else {
                return Collections.singletonList(mapper.convertValue(
                        items, TemplateInfo.class));
            }
        } catch (Exception e) {
            throw new SerializationException(
                    "Failed to parse template list", e);
        }
    }
}
