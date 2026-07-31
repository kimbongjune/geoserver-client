package com.example.geoserverexample.about.service;

import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.dto.resource.ResourceChild;

import java.util.List;

public interface AboutService {

    List<AboutResource> getVersion();

    List<ModuleStatusSummary> getStatus();

    // A resource path can be either a directory or a file — list() 500s on a file path,
    // so check the type via getMetadata() first rather than assuming it's a directory.
    boolean isFile(String dir);

    List<ResourceChild> listResources(String dir);

    String safeReadAsText(String path);

    List<MonitorRequestSummary> recentRequests();

    int fontsCount();

    int rootTemplatesCount();

    boolean transformAvailable();

    void reset();

    void reload();
}
