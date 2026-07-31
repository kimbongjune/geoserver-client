package com.example.geoserverexample.importer.service;

import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTarget;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImporterService {

    List<WorkspaceSummary> listWorkspaces();

    List<ImportContextSummary> listImports();

    ImportContext getImport(long importId);

    List<ImportTask> listTasks(long importId);

    ImportTarget getTaskTargetBestEffort(long importId, long taskId);

    List<DataStoreSummary> listDatastores(String ws);

    ImportContext createImport(String ws);

    // The Importer derives the target table/schema name from the file's own name, so it
    // must be preserved exactly — File.createTempFile()'s prefix/suffix would corrupt it
    // (e.g. "import-12345-places.geojson" instead of "places.geojson").
    ImportTask uploadTask(long importId, MultipartFile file) throws Exception;

    void setTaskTarget(long importId, long taskId, String ws, String storeName) throws Exception;

    ImportContext runImport(long importId);

    void deleteImport(long importId);
}
