package com.example.geoserverexample.layergroup.service;

import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroupSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;

public interface LayerGroupService {

    List<WorkspaceSummary> listWorkspaces();

    List<LayerSummary> listLayers();

    List<LayerGroupSummary> listGlobalGroups();

    List<LayerGroupSummary> listByWorkspace(String ws);

    void create(String name, String title, List<String> layerNames, String ws);

    void updateTitle(String name, String title, String ws);

    void delete(String name, String ws);
}
