package com.example.geoserverexample.workspace.service.impl;

import com.example.geoserverexample.workspace.service.WorkspaceService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.NamespaceSummary;
import io.github.kimbongjune.geoserverclient.dto.namespace.UpdateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.UpdateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.Workspace;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final GeoServerClient client;

    public WorkspaceServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<NamespaceSummary> listNamespaces() {
        return client.namespaces().list();
    }

    @Override
    public Map<String, String> getNamespaceUris() {
        Map<String, String> uris = new LinkedHashMap<>();
        for (NamespaceSummary ns : client.namespaces().list()) {
            uris.put(ns.getName(), client.namespaces().get(ns.getName()).getUri());
        }
        return uris;
    }

    @Override
    public String getDefaultWorkspaceName() {
        Workspace defaultWs = client.workspaces().getDefault();
        return defaultWs == null ? null : defaultWs.getName();
    }

    @Override
    public void createWorkspace(String name, boolean isolated, boolean setAsDefault) {
        client.workspaces().create(CreateWorkspaceRequest.builder(name)
                .isolated(isolated)
                .setAsDefault(setAsDefault)
                .build());
    }

    @Override
    public void updateWorkspace(String name, boolean isolated, String newName) {
        UpdateWorkspaceRequest.Builder builder = UpdateWorkspaceRequest.builder().isolated(isolated);
        if (isRenaming(newName)) {
            builder.name(newName);
        }
        client.workspaces().update(name, builder.build());
    }

    @Override
    public void setDefaultWorkspace(String name) {
        client.workspaces().setDefault(name);
    }

    @Override
    public void deleteWorkspace(String name, boolean recurse) {
        client.workspaces().delete(name, recurse);
    }

    @Override
    public void createNamespace(String prefix, String uri, boolean isolated) {
        client.namespaces().create(CreateNamespaceRequest.builder(prefix, uri).isolated(isolated).build());
    }

    @Override
    public void updateNamespaceUri(String prefix, String uri) {
        client.namespaces().update(prefix, UpdateNamespaceRequest.builder().uri(uri).build());
    }

    @Override
    public void setDefaultNamespace(String prefix) {
        client.namespaces().setDefault(prefix);
    }

    @Override
    public void deleteNamespace(String prefix) {
        client.namespaces().delete(prefix);
    }

    @Override
    public boolean isRenaming(String newName) {
        return newName != null && !newName.isBlank();
    }
}
