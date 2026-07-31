package com.example.geoserverexample.template.service;

import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;

public interface TemplateService {

    List<WorkspaceSummary> listWorkspaces();

    List<TemplateInfo> listGlobalTemplates();

    List<TemplateInfo> listByWorkspace(String ws);

    List<UrlCheckSummary> listUrlChecks();

    void create(String name, String body, String ws);

    String getBody(String name, String ws);

    void delete(String name, String ws);

    void createUrlCheck(String name, String description, String regex, boolean enabled);

    void deleteUrlCheck(String name);
}
