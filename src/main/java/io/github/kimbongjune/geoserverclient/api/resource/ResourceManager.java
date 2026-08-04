package io.github.kimbongjune.geoserverclient.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceHeadInfo;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceMetadata;
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
 * GeoServer Resource REST API manager.
 *
 * <p>Verified against: GeoServer 2.28.2 / 2026-04-02 (Kartoza Docker kartoza/geoserver)
 *
 * <p>Source:
 * <ul>
 *   <li>{@code src/restconfig/.../org/geoserver/rest/resources/ResourceController.java}
 *       ({@code @RequestMapping} path = "/resource", "/resource/**")</li>
 *   <li>Backend storage: {@code ResourceStore} (GeoServer Data Directory)</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * PUT   /rest/resource/{directory}
 * HEAD  /rest/resource/{path}
 * </pre>
 */
public class ResourceManager extends AbstractManager {

    public ResourceManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET directory listings

    /** Lists the root Data Directory. */
    public List<ResourceChild> listRoot() {
        String path = "/rest/resource?format=json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseChildren(response.getBody());
    }

    /** Lists the contents of a directory. */
    public List<ResourceChild> list(
            String directoryPath) {
        requireNonEmpty(directoryPath, "directoryPath");
        String path = "/rest/resource/" + directoryPath + "?format=json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseChildren(response.getBody());
    }

    // [1] GET file content

    /** Returns the raw text content of a file resource. */
    public String getFileContent(String filePath) {
        requireNonEmpty(filePath, "filePath");
        return doGetRaw("/rest/resource/" + filePath, "text/plain");
    }

    /** Returns the binary content of a file resource. */
    public byte[] getFileBytes(String filePath) {
        requireNonEmpty(filePath, "filePath");
        String path = "/rest/resource/" + filePath;
        GeoServerResponse response =
                httpClient.getBinary(path, "application/octet-stream");
        handleErrorResponse(response, "GET", path);
        return response.getBinaryBody();
    }

    // [1] GET metadata (directories only)

    /**
     * Returns metadata (name, type, lastModified) for a directory.
     *
     * <p>NOTE: {@code operation=metadata&format=json} returns XML for files (GeoServer quirk).
     * Use this method with directory paths only.</p>
     */
    public ResourceMetadata getMetadata(
            String resourcePath) {
        requireNonEmpty(resourcePath, "resourcePath");
        String path = "/rest/resource/" + resourcePath + "?operation=metadata&format=json";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseMetadata(response.getBody());
    }

    // [2] Exist check (HEAD via GET metadata)

    /**
     * Returns {@code true} if the resource (file or directory) exists.
     *
     * <p>Uses a plain GET to avoid GeoServer 2.28.2 bug where
     * {@code ?operation=metadata&format=json} on non-existent files returns HTTP 500
     * instead of 404.</p>
     */
    public boolean exists(String resourcePath) {
        requireNonEmpty(resourcePath, "resourcePath");
        String path = "/rest/resource/" + resourcePath;
        GeoServerResponse response =
                httpClient.get(path, "application/octet-stream");
        if (response.isNotFound()) {
            return false;
        }
        if (response.isSuccessful()) {
            return true;
        }
        handleErrorResponse(response, "GET", path);
        return false;
    }

    // [2b] HEAD

    /**
     * Returns lightweight metadata for a file or directory resource via a HEAD request
     * (no body transferred), or {@code null} if the resource does not exist.
     *
     * <p>Works for both files and directories, unlike {@link #getMetadata(String)} which only
     * works reliably for directories (GeoServer 2.28.2 quirk). Verified against GeoServer 2.28.2.
     *
     * @param resourcePath the resource path (required)
     * @return head metadata, or {@code null} if not found (404)
     */
    public ResourceHeadInfo headMetadata(String resourcePath) {
        requireNonEmpty(resourcePath, "resourcePath");
        String path = "/rest/resource/" + resourcePath;
        GeoServerResponse response = httpClient.head(path);
        if (response.isNotFound()) {
            return null;
        }
        handleErrorResponse(response, "HEAD", path);
        return new ResourceHeadInfo(
                response.getHeader("Resource-Type"),
                response.getHeader("Last-Modified"),
                response.getHeader("Content-Type"),
                parseFileName(response.getHeader("Content-Disposition")));
    }

    private static String parseFileName(String contentDisposition) {
        if (contentDisposition == null) {
            return null;
        }
        int idx = contentDisposition.indexOf("filename=\"");
        if (idx < 0) {
            return null;
        }
        int start = idx + "filename=\"".length();
        int end = contentDisposition.indexOf('"', start);
        return end > start ? contentDisposition.substring(start, end) : null;
    }

    // [3] PUT

    /** Creates or updates a file resource. Always returns 201 (GeoServer behaviour). */
    public void createOrUpdate(String filePath, String content, String contentType) {
        requireNonEmpty(filePath, "filePath");
        requireNonEmpty(contentType, "contentType");
        doPutRaw("/rest/resource/" + filePath, content, contentType, "application/json");
    }

    /**
     * Copies a file from {@code sourcePath} to {@code destPath}. Always returns 201.
     *
     * @throws ResourceNotFoundException if source not found (404)
     */
    public void copy(String destPath, String sourcePath) {
        requireNonEmpty(destPath, "destPath");
        requireNonEmpty(sourcePath, "sourcePath");
        doPutRaw("/rest/resource/" + destPath + "?operation=copy",
                sourcePath, "text/plain", "application/json");
    }

    /**
     * Moves a file from {@code sourcePath} to {@code destPath}. Always returns 201.
     * The source file is deleted after a successful move.
     *
     * @throws ResourceNotFoundException if source not found (404)
     */
    public void move(String destPath, String sourcePath) {
        requireNonEmpty(destPath, "destPath");
        requireNonEmpty(sourcePath, "sourcePath");
        doPutRaw("/rest/resource/" + destPath + "?operation=move",
                sourcePath, "text/plain", "application/json");
    }

    // [4] DELETE

    /**
     * Deletes a file or directory (recursively).
     *
     * @throws ResourceNotFoundException if not found (404)
     */
    public void delete(String resourcePath) {
        requireNonEmpty(resourcePath, "resourcePath");
        String path = "/rest/resource/" + resourcePath;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Resource", resourcePath, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Private: parsing helpers

    private List<ResourceChild> parseChildren(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            ObjectMapper mapper =
                    new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode childNode =
                    root.path("ResourceDirectory").path("children").path("child");
            if (childNode.isMissingNode() || childNode.isNull()) {
                return Collections.emptyList();
            }
            if (childNode.isArray()) {
                List<ResourceChild> result =
                        new ArrayList<>();
                for (JsonNode item : childNode) {
                    result.add(mapper.convertValue(
                            item, ResourceChild.class));
                }
                return result;
            } else {
                return Collections.singletonList(mapper.convertValue(
                        childNode, ResourceChild.class));
            }
        } catch (Exception e) {
            throw new SerializationException(
                    "Failed to parse resource directory listing", e);
        }
    }

    private ResourceMetadata parseMetadata(String body) {
        try {
            ObjectMapper mapper =
                    new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            return mapper.convertValue(root.path("ResourceMetadata"),
                    ResourceMetadata.class);
        } catch (Exception e) {
            throw new SerializationException(
                    "Failed to parse resource metadata", e);
        }
    }
}
