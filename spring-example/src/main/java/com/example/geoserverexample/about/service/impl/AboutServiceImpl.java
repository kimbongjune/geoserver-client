package com.example.geoserverexample.about.service.impl;

import com.example.geoserverexample.about.service.AboutService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AboutServiceImpl implements AboutService {

    private final GeoServerClient client;

    public AboutServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<AboutResource> getVersion() {
        return client.about().getVersion();
    }

    @Override
    public List<ModuleStatusSummary> getStatus() {
        return client.about().getStatus();
    }

    @Override
    public boolean isFile(String dir) {
        // A resource path can be either a directory or a file — list() 500s on a file path,
        // so check the type via getMetadata() first rather than assuming it's a directory.
        String type = client.resources().getMetadata(dir).getType();
        return "resource".equalsIgnoreCase(type);
    }

    @Override
    public List<ResourceChild> listResources(String dir) {
        return dir == null || dir.isEmpty()
                ? client.resources().listRoot()
                : client.resources().list(dir);
    }

    @Override
    public String safeReadAsText(String path) {
        try {
            return client.resources().getFileContent(path);
        } catch (Exception e) {
            return "(binary or unreadable file — " + client.resources().getFileBytes(path).length + " bytes)";
        }
    }

    @Override
    public List<MonitorRequestSummary> recentRequests() {
        return client.monitoring().list(new MonitoringQuery().count(10).order("startTime;DESC"));
    }

    @Override
    public int fontsCount() {
        return client.fonts().list().size();
    }

    @Override
    public int rootTemplatesCount() {
        return client.output().getTemplates().size();
    }

    @Override
    public boolean transformAvailable() {
        return client.transforms().isAvailable();
    }

    @Override
    public void reset() {
        client.reset().reset();
    }

    @Override
    public void reload() {
        client.reset().reload();
    }
}
