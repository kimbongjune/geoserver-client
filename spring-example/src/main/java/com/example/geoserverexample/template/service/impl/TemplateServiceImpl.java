package com.example.geoserverexample.template.service.impl;

import com.example.geoserverexample.template.service.TemplateService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateContent;
import io.github.kimbongjune.geoserverclient.dto.template.TemplateInfo;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckRequest;
import io.github.kimbongjune.geoserverclient.dto.urlcheck.UrlCheckSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final GeoServerClient client;

    public TemplateServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<TemplateInfo> listGlobalTemplates() {
        return client.templates().list();
    }

    @Override
    public List<TemplateInfo> listByWorkspace(String ws) {
        return client.templates().listByWorkspace(ws);
    }

    @Override
    public List<UrlCheckSummary> listUrlChecks() {
        return client.urlChecks().list();
    }

    @Override
    public void create(String name, String body, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.templates().putByWorkspace(ws, name, TemplateContent.of(body));
        } else {
            client.templates().put(name, TemplateContent.of(body));
        }
    }

    @Override
    public String getBody(String name, String ws) {
        if (ws != null && !ws.isEmpty()) {
            return client.templates().getByWorkspace(ws, name).getBody();
        }
        return client.templates().get(name).getBody();
    }

    @Override
    public void delete(String name, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.templates().deleteByWorkspace(ws, name);
        } else {
            client.templates().delete(name);
        }
    }

    @Override
    public void createUrlCheck(String name, String description, String regex, boolean enabled) {
        client.urlChecks().create(new UrlCheckRequest(name, description, regex, enabled));
    }

    @Override
    public void deleteUrlCheck(String name) {
        client.urlChecks().delete(name);
    }
}
