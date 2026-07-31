package com.example.geoserverexample.workspace.service;

import io.github.kimbongjune.geoserverclient.dto.namespace.NamespaceSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;
import java.util.Map;

public interface WorkspaceService {

    List<WorkspaceSummary> listWorkspaces();

    List<NamespaceSummary> listNamespaces();

    /**
     * Maps each namespace prefix (== workspace name) to its URI — used by the map viewer to
     * build WFS-T {@code featureNS} values without a dedicated per-workspace REST round-trip.
     */
    Map<String, String> getNamespaceUris();

    String getDefaultWorkspaceName();

    void createWorkspace(String name, boolean isolated, boolean setAsDefault);

    void updateWorkspace(String name, boolean isolated, String newName);

    void setDefaultWorkspace(String name);

    void deleteWorkspace(String name, boolean recurse);

    void createNamespace(String prefix, String uri, boolean isolated);

    void updateNamespaceUri(String prefix, String uri);

    void setDefaultNamespace(String prefix);

    void deleteNamespace(String prefix);

    boolean isRenaming(String newName);
}
