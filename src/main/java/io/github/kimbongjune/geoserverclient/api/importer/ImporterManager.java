package io.github.kimbongjune.geoserverclient.api.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.api.AbstractManager;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportData;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportLayer;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTarget;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTaskUpdate;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransform;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTransformChain;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.exception.SerializationException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GeoServer Importer REST API manager.
 *
 * <p>Requires Importer Extension plugin:
 * <ul>
 *   <li>Download: https://sourceforge.net/projects/geoserver/files/GeoServer/2.28.2/extensions/geoserver-2.28.2-importer-plugin.zip/download</li>
 *   <li>kartoza/geoserver Docker: {@code STABLE_EXTENSIONS=importer-plugin}</li>
 * </ul>
 *
 * <p>Verified against: GeoServer 2.28.2 / 2026-04-02 (Docker geoserver, STABLE_EXTENSIONS=importer-plugin)
 *
 * <p>Source:
 * <ul>
 *   <li>{@code gs-importer-rest-2.28.2.jar -> org/geoserver/importer/rest/ImportController.class}</li>
 *   <li>{@code gs-importer-rest-2.28.2.jar -> org/geoserver/importer/rest/ImportTaskController.class}</li>
 *   <li>{@code gs-importer-rest-2.28.2.jar -> org/geoserver/importer/rest/ImportDataController.class}</li>
 *   <li>{@code gs-importer-rest-2.28.2.jar -> org/geoserver/importer/rest/ImportTransformController.class}</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 * <pre>
 * </pre>
 */
public class ImporterManager extends AbstractManager {


    public ImporterManager(GeoServerHttpClient httpClient,
                           SerializerFactory serializerFactory,
                           DataFormat defaultFormat) {
        super(httpClient, serializerFactory, defaultFormat);
    }

    // [1] GET /rest/imports

    public List<ImportContextSummary> listImports() {
        GeoServerResponse response =
                httpClient.get("/rest/imports", "application/json");
        handleErrorResponse(response, "GET", "/rest/imports");
        return parseImportList(response.getBody());
    }

    // [2] POST /rest/imports

    public ImportContext createImport(
            String targetWorkspace) {
        requireNonEmpty(targetWorkspace, "targetWorkspace");
        String body = buildImportBody(targetWorkspace);
        GeoServerResponse response =
                httpClient.post("/rest/imports", body, "application/json", "application/json");
        handleErrorResponse(response, "POST", "/rest/imports");
        return parseImport(response.getBody());
    }

    public ImportContext createAndRunImport(
            String targetWorkspace) {
        requireNonEmpty(targetWorkspace, "targetWorkspace");
        String body = buildImportBody(targetWorkspace);
        GeoServerResponse response =
                httpClient.post("/rest/imports?exec=true", body, "application/json", "application/json");
        handleErrorResponse(response, "POST", "/rest/imports?exec=true");
        return parseImport(response.getBody());
    }

    // [3] PUT /rest/imports/{id}

    public ImportContext createImportAtId(
            long importId, String targetWorkspace) {
        requireNonEmpty(targetWorkspace, "targetWorkspace");
        String path = "/rest/imports/" + importId;
        String body = buildImportBody(targetWorkspace);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
        return parseImport(response.getBody());
    }

    // [4] GET /rest/imports/{id}

    public ImportContext getImport(long importId) {
        String path = "/rest/imports/" + importId;
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Import", String.valueOf(importId), response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseImport(response.getBody());
    }

    // [5] POST /rest/imports/{id} - run import

    public void runImport(long importId) {
        String path = "/rest/imports/" + importId;
        GeoServerResponse response =
                httpClient.post(path, "{}", "application/json", "application/json");
        handleErrorResponse(response, "POST (run)", path);
    }

    // [6] DELETE /rest/imports/{id}

    public void deleteImport(long importId) {
        String path = "/rest/imports/" + importId;
        GeoServerResponse response =
                httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "Import", String.valueOf(importId), response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // [7] DELETE /rest/imports

    public void deleteAllImports() {
        GeoServerResponse response =
                httpClient.delete("/rest/imports");
        handleErrorResponse(response, "DELETE", "/rest/imports");
    }

    // [8] GET /rest/imports/{id}/tasks

    public List<ImportTask> listTasks(long importId) {
        String path = "/rest/imports/" + importId + "/tasks";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTaskList(response.getBody());
    }

    // [10] PUT /rest/imports/{id}/tasks/{file}

    public ImportTask uploadTask(
            long importId, File file, String contentType) {
        requireNonNull(file, "file");
        requireNonEmpty(contentType, "contentType");
        String path = "/rest/imports/" + importId + "/tasks/" + file.getName();
        GeoServerResponse response =
                httpClient.putFile(path, file, contentType, "application/json");
        handleErrorResponse(response, "PUT (file)", path);
        return parseTask(response.getBody());
    }

    // [11] GET /rest/imports/{id}/tasks/{taskId}

    public ImportTask getTask(long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId;
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "ImportTask", importId + "/tasks/" + taskId, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseTask(response.getBody());
    }

    // [12] PUT /rest/imports/{id}/tasks/{taskId}

    public void updateTask(long importId, long taskId,
                           ImportTaskUpdate update) {
        requireNonNull(update, "update");
        String path = "/rest/imports/" + importId + "/tasks/" + taskId;
        String body = serializeTaskUpdate(update);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "ImportTask", importId + "/tasks/" + taskId, response.getBody());
        }
        // NOTE: when task layer is null, server may return 500 — do not call in that state
        handleErrorResponse(response, "PUT", path);
    }

    // [13] DELETE /rest/imports/{id}/tasks/{taskId}

    public void deleteTask(long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId;
        GeoServerResponse response =
                httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "ImportTask", importId + "/tasks/" + taskId, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // [14] GET /rest/imports/{id}/tasks/{taskId}/progress

    public String getTaskProgress(long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/progress";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return response.getBody();
    }

    // [15] GET /rest/imports/{id}/tasks/{taskId}/target

    public ImportTarget getTaskTarget(
            long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/target";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException(
                    "ImportTaskTarget", importId + "/tasks/" + taskId, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseTarget(response.getBody());
    }

    // [16] PUT /rest/imports/{id}/tasks/{taskId}/target

    public void setTaskTarget(long importId, long taskId,
                              ImportTarget target) {
        requireNonNull(target, "target");
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/target";
        String body = serializeTarget(target);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
    }

    // [17] GET /rest/imports/{id}/tasks/{taskId}/layer

    public ImportLayer getTaskLayer(
            long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/layer";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseLayer(response.getBody());
    }

    // [18] PUT /rest/imports/{id}/tasks/{taskId}/layer

    public ImportLayer setTaskLayer(
            long importId, long taskId,
            ImportLayer layer) {
        requireNonNull(layer, "layer");
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/layer";
        String body = serializeLayer(layer);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
        return parseLayer(response.getBody());
    }

    // [27] GET /rest/imports/{id}/tasks/{taskId}/transforms

    public ImportTransformChain listTransforms(
            long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/transforms";
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTransformChain(response.getBody());
    }

    // [28] GET /rest/imports/{id}/tasks/{taskId}/transforms/{tId}

    public ImportTransform getTransform(
            long importId, long taskId, long transformId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/transforms/" + transformId;
        GeoServerResponse response =
                httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseTransform(response.getBody());
    }

    // [29] POST /rest/imports/{id}/tasks/{taskId}/transforms

    public void addTransform(long importId, long taskId,
                             ImportTransform transform) {
        requireNonNull(transform, "transform");
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/transforms";
        String body = serializeTransform(transform);
        GeoServerResponse response =
                httpClient.post(path, body, "application/json", "application/json");
        handleErrorResponse(response, "POST", path);
    }

    // [30] PUT /rest/imports/{id}/tasks/{taskId}/transforms/{tId}

    public ImportTransform updateTransform(
            long importId, long taskId, long transformId,
            ImportTransform transform) {
        requireNonNull(transform, "transform");
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/transforms/" + transformId;
        String body = serializeTransform(transform);
        GeoServerResponse response =
                httpClient.put(path, body, "application/json", "application/json");
        handleErrorResponse(response, "PUT", path);
        return parseTransform(response.getBody());
    }

    // [31] DELETE /rest/imports/{id}/tasks/{taskId}/transforms/{tId}

    public void deleteTransform(long importId, long taskId, long transformId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/transforms/" + transformId;
        GeoServerResponse response =
                httpClient.delete(path);
        handleErrorResponse(response, "DELETE", path);
    }

    // ── ImporterData ─────────────────────────────────────────────────────
    //
    // Endpoint reality check performed against a live GeoServer 2.28.2 (OPTIONS + real calls),
    // since the general REST API coverage audit this section is based on got several of these
    // wrong (marked as valid-but-missing when some routes don't actually exist / don't support
    // the verb claimed):
    //   - GET  /rest/imports/{id}/data                                real (500 NPE bug when the
    //                                                                  import has no import-level
    //                                                                  data, e.g. task-based imports)
    //   - GET  /rest/imports/{id}/tasks/{taskId}/data                 real, 200
    //   - GET  /rest/imports/{id}/data/files                          real (400 "Data is not a
    //                                                                  directory" unless data.type
    //                                                                  == "directory")
    //   - GET  /rest/imports/{id}/tasks/{taskId}/data/files           real, same directory-only rule
    //   - GET/DELETE /rest/imports/{id}/data/files/{filename}         real (OPTIONS confirms
    //                                                                  Allow: GET,HEAD,DELETE,OPTIONS)
    //   - DELETE /rest/imports/{id}/data/files (bulk)                 NOT supported — OPTIONS
    //                                                                  confirms Allow: GET,HEAD,OPTIONS
    //   - DELETE /rest/imports/{id}/tasks/{taskId}/data/files (bulk)  NOT supported, same reason
    //   - GET/DELETE /rest/imports/{id}/tasks/{taskId}/data/files/{filename}
    //                                                                  route DOES NOT EXIST at all
    //                                                                  on 2.28.2 (OPTIONS itself
    //                                                                  returns 404) — not implemented

    // [32] GET /rest/imports/{id}/data

    /**
     * Gets the data source attached directly to an import (not a specific task).
     *
     * <p><b>GeoServer 2.28.2 bug:</b> throws 500 ({@code NullPointerException}) when the import
     * has no import-level data assigned — the common case for task-based imports, where data
     * lives on each {@link ImportTask} instead (see {@link #getTaskData(long, long)}).
     *
     * @param importId the import ID (required)
     * @return the import-level data source
     */
    public ImportData getImportData(long importId) {
        String path = "/rest/imports/" + importId + "/data";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("ImportData", String.valueOf(importId), response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseImportData(response.getBody());
    }

    // [33] GET /rest/imports/{id}/tasks/{taskId}/data

    /**
     * Gets the data source attached to a specific import task.
     *
     * @param importId the import ID (required)
     * @param taskId   the task ID (required)
     * @return the task's data source
     */
    public ImportData getTaskData(long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/data";
        GeoServerResponse response = httpClient.get(path, "application/json");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("ImportData", importId + "/tasks/" + taskId, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return parseImportData(response.getBody());
    }

    // [34] GET /rest/imports/{id}/data/files

    /**
     * Lists file names under an import's data source. Only meaningful when the import-level
     * data is a directory ({@code getImportData(importId).getType().equals("directory")});
     * GeoServer returns 400 ("Data is not a directory") otherwise.
     *
     * @param importId the import ID (required)
     * @return file names in the directory
     */
    public List<String> listImportDataFiles(long importId) {
        String path = "/rest/imports/" + importId + "/data/files";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseFileList(response.getBody());
    }

    // [35] GET /rest/imports/{id}/tasks/{taskId}/data/files

    /**
     * Lists file names under a task's data source. Only meaningful when the task-level data is
     * a directory; GeoServer returns 400 ("Data is not a directory") otherwise.
     *
     * @param importId the import ID (required)
     * @param taskId   the task ID (required)
     * @return file names in the directory
     */
    public List<String> listTaskDataFiles(long importId, long taskId) {
        String path = "/rest/imports/" + importId + "/tasks/" + taskId + "/data/files";
        GeoServerResponse response = httpClient.get(path, "application/json");
        handleErrorResponse(response, "GET", path);
        return parseFileList(response.getBody());
    }

    // [36] GET /rest/imports/{id}/data/files/{filename}

    /**
     * Downloads a single file's raw bytes from an import's (directory-type) data source.
     *
     * @param importId the import ID (required)
     * @param filename the file name (required)
     * @return the raw file bytes
     * @throws ResourceNotFoundException if the file does not exist
     */
    public byte[] getImportDataFile(long importId, String filename) {
        requireNonEmpty(filename, "filename");
        String path = "/rest/imports/" + importId + "/data/files/" + filename;
        GeoServerResponse response = httpClient.getBinary(path, "application/octet-stream");
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("ImportDataFile", importId + "/" + filename, response.getBody());
        }
        handleErrorResponse(response, "GET", path);
        return response.getBinaryBody();
    }

    // [37] DELETE /rest/imports/{id}/data/files/{filename}

    /**
     * Deletes a single file from an import's (directory-type) data source. Confirmed supported
     * via {@code OPTIONS} ({@code Allow: GET,HEAD,DELETE,OPTIONS}) — unlike the bulk
     * {@code DELETE .../data/files} collection endpoint, which GeoServer does not support.
     *
     * @param importId the import ID (required)
     * @param filename the file name (required)
     * @throws ResourceNotFoundException if the file does not exist
     */
    public void deleteImportDataFile(long importId, String filename) {
        requireNonEmpty(filename, "filename");
        String path = "/rest/imports/" + importId + "/data/files/" + filename;
        GeoServerResponse response = httpClient.delete(path);
        if (response.isNotFound()) {
            throw new ResourceNotFoundException("ImportDataFile", importId + "/" + filename, response.getBody());
        }
        handleErrorResponse(response, "DELETE", path);
    }

    // Parsing helpers

    private List<ImportContextSummary> parseImportList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode items = root.path("imports");
            if (items.isMissingNode() || items.isNull() || items.isTextual()) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<ImportContextSummary> result =
                        new ArrayList<>();
                for (JsonNode item : items) {
                    result.add(getObjectMapper().treeToValue(item,
                            ImportContextSummary.class));
                }
                return result;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse import list", e);
        }
    }

    private ImportContext parseImport(String body) {
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path("import");
            if (node.isMissingNode()) {
                throw new SerializationException(
                        "Missing 'import' key in response", null);
            }
            return getObjectMapper().treeToValue(node,
                    ImportContext.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse import response", e);
        }
    }

    private List<ImportTask> parseTaskList(
            String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode items = root.path("tasks");
            if (items.isMissingNode() || items.isNull() || items.isTextual()) {
                return Collections.emptyList();
            }
            if (items.isArray()) {
                List<ImportTask> result =
                        new ArrayList<>();
                for (JsonNode item : items) {
                    result.add(getObjectMapper().treeToValue(item,
                            ImportTask.class));
                }
                return result;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse task list", e);
        }
    }

    private ImportTask parseTask(String body) {
        try {
            if (body == null || body.trim().isEmpty()) return null;
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path("task");
            if (!node.isMissingNode()) {
                return getObjectMapper().treeToValue(node,
                        ImportTask.class);
            }
            JsonNode tasks = root.path("tasks");
            if (!tasks.isMissingNode() && tasks.isArray() && tasks.size() > 0) {
                return getObjectMapper().treeToValue(tasks.get(0),
                        ImportTask.class);
            }
            return null;
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse task response", e);
        }
    }

    private ImportTarget parseTarget(String body) {
        try {
            JsonNode root = getObjectMapper().readTree(body);
            return getObjectMapper().treeToValue(root,
                    ImportTarget.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse target response", e);
        }
    }

    private ImportLayer parseLayer(String body) {
        try {
            if (body == null || body.trim().isEmpty()) return null;
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path("layer");
            if (!node.isMissingNode()) {
                return getObjectMapper().treeToValue(node,
                        ImportLayer.class);
            }
            return getObjectMapper().treeToValue(root,
                    ImportLayer.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse layer response", e);
        }
    }

    private ImportTransformChain parseTransformChain(
            String body) {
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode node = root.path("transformChain");
            if (!node.isMissingNode()) {
                return getObjectMapper().treeToValue(node,
                        ImportTransformChain.class);
            }
            return getObjectMapper().treeToValue(root,
                    ImportTransformChain.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse transform chain response", e);
        }
    }

    private ImportTransform parseTransform(String body) {
        try {
            if (body == null || body.trim().isEmpty()) return null;
            JsonNode root = getObjectMapper().readTree(body);
            return getObjectMapper().treeToValue(root,
                    ImportTransform.class);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to parse transform response", e);
        }
    }

    // Serialization helpers

    private String buildImportBody(String targetWorkspace) {
        try {
            Map<String, Object> ws = new LinkedHashMap<>();
            ws.put("name", targetWorkspace);
            Map<String, Object> wsWrapper = new LinkedHashMap<>();
            wsWrapper.put("workspace", ws);
            Map<String, Object> importObj = new LinkedHashMap<>();
            importObj.put("targetWorkspace", wsWrapper);
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("import", importObj);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize import request", e);
        }
    }

    private String serializeTaskUpdate(
            ImportTaskUpdate update) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("task", update);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize task update", e);
        }
    }

    private String serializeTarget(
            ImportTarget target) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("dataStore", target.getDataStore());
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize target", e);
        }
    }

    private String serializeLayer(
            ImportLayer layer) {
        try {
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("layer", layer);
            return getObjectMapper().writeValueAsString(root);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize layer", e);
        }
    }

    private String serializeTransform(
            ImportTransform transform) {
        try {
            return getObjectMapper().writeValueAsString(transform);
        } catch (IOException e) {
            throw new SerializationException(
                    "Failed to serialize transform", e);
        }
    }

    private ImportData parseImportData(String body) {
        try {
            return getObjectMapper().readValue(body, ImportData.class);
        } catch (IOException e) {
            throw new SerializationException("Failed to parse import data response", e);
        }
    }

    private List<String> parseFileList(String body) {
        if (body == null || body.isEmpty()) return Collections.emptyList();
        try {
            JsonNode root = getObjectMapper().readTree(body);
            JsonNode files = root.path("files");
            if (files.isArray()) {
                List<String> result = new ArrayList<>();
                for (JsonNode item : files) {
                    result.add(item.isTextual() ? item.asText() : item.path("file").asText(null));
                }
                return result;
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new SerializationException("Failed to parse data file list response", e);
        }
    }
}