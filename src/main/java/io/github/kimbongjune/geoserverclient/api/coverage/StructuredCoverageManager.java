package io.github.kimbongjune.geoserverclient.api.coverage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.IndexSchema;
import io.github.kimbongjune.geoserverclient.exception.GranuleNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Manager for the GeoServer Structured Coverage (granule index) REST API
 * ({@code /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index}).
 *
 * <p>The Structured Coverage API manages the internal granule index of an ImageMosaic
 * coverage store. ImageMosaic assembles multiple raster files (granules) into a single
 * virtual coverage; this API supports schema inspection and granule listing/deletion.
 *
 * <p><b>Note:</b> This API works only with stores that implement
 * {@code StructuredGridCoverage2DReader} (primarily ImageMosaic). Calling it on a
 * GeoTIFF, WorldImage, or RST store causes {@code 500 Internal Server Error}
 * (ClassCastException in GeoServer).
 *
 * <h2>Endpoints covered</h2>
 * <pre>
 * GET     /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index
 * GET     /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index/granules
 * GET     /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index/granules/{granuleId}
 * DELETE  /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index/granules
 * DELETE  /rest/workspaces/{ws}/coveragestores/{cs}/coverages/{c}/index/granules/{granuleId}
 * </pre>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * IndexSchema schema = client.structuredCoverages().getSchema("myws", "mymosaic", "dem");
 * GranuleCollection all = client.structuredCoverages().listGranules("myws", "mymosaic", "dem");
 * }</pre>
 *
 * @since 1.0.0
 */
public class StructuredCoverageManager extends AbstractManager {

    public StructuredCoverageManager(GeoServerHttpClient httpClient,
                                     SerializerFactory serializerFactory,
                                     DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // index schema

    /**
     * Get the coverage index schema (attribute list).
     * Works only with imagemosaic-type coverage stores.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required, imagemosaic type)
     * @param coverageName  coverage name (required)
     * @return index schema
     */
    public IndexSchema getSchema(String workspaceName, String storeName, String coverageName) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");

        String path = buildBase(workspaceName, storeName, coverageName) + "/index.json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseSchema(response.getBody());
    }

    // granules list

    /**
     * List all granules.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @return GeoJSON FeatureCollection
     */
    public GranuleCollection listGranules(String workspaceName, String storeName,
                                          String coverageName) {
        return listGranules(workspaceName, storeName, coverageName, null, null, null);
    }

    /**
     * List granules with optional filter and paging.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @param filter        ECQL filter expression (null returns all)
     * @param offset        start index for paging (null for default)
     * @param limit         maximum result count (null for default)
     * @return GeoJSON FeatureCollection
     */
    public GranuleCollection listGranules(String workspaceName, String storeName,
                                          String coverageName, String filter,
                                          Integer offset, Integer limit) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");

        StringBuilder path = new StringBuilder(
                buildBase(workspaceName, storeName, coverageName) + "/index/granules.json");
        boolean first = true;
        if (filter != null) {
            path.append("?filter=").append(urlEncode(filter)); first = false;
        }
        if (offset != null) {
            path.append(first ? "?" : "&").append("offset=").append(offset); first = false;
        }
        if (limit != null) {
            path.append(first ? "?" : "&").append("limit=").append(limit);
        }

        GeoServerResponse response = httpClient.get(path.toString(), "application/json");
        handleErrorResponse(response, "GET", path.toString());
        return parseGranules(response.getBody());
    }

    // granule single get

    /**
     * Get a single granule by FID.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @param granuleId     granule FID (e.g. "coverageName.1") (required)
     * @return GeoJSON FeatureCollection (contains one feature)
     * @throws GranuleNotFoundException if the granule does not exist
     */
    public GranuleCollection getGranule(String workspaceName, String storeName,
                                        String coverageName, String granuleId) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");
        requireNonEmpty(granuleId, "granuleId");

        String path = buildBase(workspaceName, storeName, coverageName)
                + "/index/granules/" + granuleId + ".json";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new GranuleNotFoundException(workspaceName, storeName, coverageName,
                    granuleId, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseGranules(response.getBody());
    }

    // granules delete (bulk)

    /**
     * Bulk delete all granules (no filter).
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @throws io.github.kimbongjune.geoserverclient.exception.InvalidParameterException
     *         if any parameter is null/empty
     */
    public void deleteGranules(String workspaceName, String storeName, String coverageName) {
        deleteGranules(workspaceName, storeName, coverageName, null, "none", false);
    }

    /**
     * Bulk delete granules matching an ECQL filter.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @param filter        ECQL filter expression (null deletes all)
     * @param purge         file deletion policy: "none" (default), "metadata", "all"
     * @param updateBBox    when true, recalculates nativeBoundingBox after deletion
     * @throws io.github.kimbongjune.geoserverclient.exception.InvalidParameterException
     *         if workspaceName, storeName, or coverageName is null/empty
     */
    public void deleteGranules(String workspaceName, String storeName, String coverageName,
                               String filter, String purge, boolean updateBBox) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");

        StringBuilder path = new StringBuilder(
                buildBase(workspaceName, storeName, coverageName) + "/index/granules");
        boolean first = true;
        if (filter != null) {
            path.append("?filter=").append(urlEncode(filter)); first = false;
        }
        if (purge != null && !purge.equals("none")) {
            path.append(first ? "?" : "&").append("purge=").append(purge); first = false;
        }
        if (updateBBox) {
            path.append(first ? "?" : "&").append("updateBBox=true");
        }

        GeoServerResponse response = httpClient.delete(path.toString());
        handleErrorResponse(response, "DELETE", path.toString());
    }

    // granule delete (single)

    /**
     * Delete a single granule by FID.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @param granuleId     granule FID to delete (required)
     * @throws GranuleNotFoundException if the granule does not exist
     */
    public void deleteGranule(String workspaceName, String storeName,
                              String coverageName, String granuleId) {
        deleteGranule(workspaceName, storeName, coverageName, granuleId, "none", false);
    }

    /**
     * Delete a single granule by FID with purge/updateBBox options.
     *
     * @param workspaceName workspace name (required)
     * @param storeName     coverage store name (required)
     * @param coverageName  coverage name (required)
     * @param granuleId     granule FID to delete (required)
     * @param purge         file deletion policy: "none" (default), "metadata", "all"
     * @param updateBBox    recalculate bbox after deletion
     * @throws GranuleNotFoundException if the granule does not exist
     */
    public void deleteGranule(String workspaceName, String storeName,
                              String coverageName, String granuleId,
                              String purge, boolean updateBBox) {
        requireNonEmpty(workspaceName, "workspaceName");
        requireNonEmpty(storeName, "storeName");
        requireNonEmpty(coverageName, "coverageName");
        requireNonEmpty(granuleId, "granuleId");

        StringBuilder path = new StringBuilder(
                buildBase(workspaceName, storeName, coverageName)
                        + "/index/granules/" + granuleId);
        boolean first = true;
        if (purge != null && !purge.equals("none")) {
            path.append("?purge=").append(purge); first = false;
        }
        if (updateBBox) {
            path.append(first ? "?" : "&").append("updateBBox=true");
        }

        GeoServerResponse response = httpClient.delete(path.toString());
        if (response.isNotFound()) {
            throw new GranuleNotFoundException(workspaceName, storeName, coverageName,
                    granuleId, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path.toString());
    }

    // helpers

    private String buildBase(String ws, String store, String coverage) {
        return "/rest/workspaces/" + ws + "/coveragestores/" + store
                + "/coverages/" + coverage;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private IndexSchema parseSchema(String body) {
        try {
            ObjectMapper om = getObjectMapper();
            JsonNode root = om.readTree(body);
            return om.treeToValue(root.path("Schema"), IndexSchema.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse IndexSchema response", e);
        }
    }

    private GranuleCollection parseGranules(String body) {
        try {
            return getObjectMapper().readValue(body, GranuleCollection.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse GranuleCollection response", e);
        }
    }

}
