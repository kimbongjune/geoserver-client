package com.example.geoserverexample.importer.service.impl;

import com.example.geoserverexample.importer.service.ImporterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.datastore.DataStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContext;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportContextSummary;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTarget;
import io.github.kimbongjune.geoserverclient.dto.importer.ImportTask;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class ImporterServiceImpl implements ImporterService {

    private final GeoServerClient client;
    private final ObjectMapper jackson = new ObjectMapper();

    public ImporterServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<ImportContextSummary> listImports() {
        return client.importer().listImports();
    }

    @Override
    public ImportContext getImport(long importId) {
        return client.importer().getImport(importId);
    }

    @Override
    public List<ImportTask> listTasks(long importId) {
        return client.importer().listTasks(importId);
    }

    @Override
    public ImportTarget getTaskTargetBestEffort(long importId, long taskId) {
        try {
            return client.importer().getTaskTarget(importId, taskId);
        } catch (GeoServerException ignored) {
            return null;
        }
    }

    @Override
    public List<DataStoreSummary> listDatastores(String ws) {
        return client.datastores().list(ws);
    }

    @Override
    public ImportContext createImport(String ws) {
        return client.importer().createImport(ws);
    }

    @Override
    public ImportTask uploadTask(long importId, MultipartFile file) throws Exception {
        // The Importer derives the target table/schema name from the file's own name, so it
        // must be preserved exactly — File.createTempFile()'s prefix/suffix would corrupt it
        // (e.g. "import-12345-places.geojson" instead of "places.geojson").
        File tmpDir = java.nio.file.Files.createTempDirectory("geoserver-import").toFile();
        tmpDir.deleteOnExit();
        File tmp = new File(tmpDir, file.getOriginalFilename());
        file.transferTo(tmp);
        tmp.deleteOnExit();
        return client.importer().uploadTask(importId, tmp, "application/octet-stream");
    }

    @Override
    public void setTaskTarget(long importId, long taskId, String ws, String storeName) throws Exception {
        String targetJson = "{\"dataStore\":{\"name\":\"" + storeName + "\",\"workspace\":{\"name\":\"" + ws + "\"}}}";
        ImportTarget target = jackson.readValue(targetJson, ImportTarget.class);
        client.importer().setTaskTarget(importId, taskId, target);
    }

    @Override
    public ImportContext runImport(long importId) {
        client.importer().runImport(importId);
        return client.importer().getImport(importId);
    }

    @Override
    public void deleteImport(long importId) {
        client.importer().deleteImport(importId);
    }
}
