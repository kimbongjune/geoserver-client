package com.example.geoserverexample.layergroup.service.impl;

import com.example.geoserverexample.layergroup.service.LayerGroupService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.CreateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroupSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.UpdateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LayerGroupServiceImpl implements LayerGroupService {

    private final GeoServerClient client;

    public LayerGroupServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<LayerSummary> listLayers() {
        return client.layers().list();
    }

    @Override
    public List<LayerGroupSummary> listGlobalGroups() {
        return client.layerGroups().list();
    }

    @Override
    public List<LayerGroupSummary> listByWorkspace(String ws) {
        return client.layerGroups().listByWorkspace(ws);
    }

    @Override
    public void create(String name, String title, List<String> layerNames, String ws) {
        List<String> styles = new ArrayList<>();
        for (int i = 0; i < layerNames.size(); i++) styles.add("");
        CreateLayerGroupRequest.Builder builder = CreateLayerGroupRequest.builder(name);
        for (String layerName : layerNames) {
            builder.layer(layerName);
        }
        CreateLayerGroupRequest request = builder
                .styles(styles)
                .title(title)
                .build();
        if (ws != null && !ws.isEmpty()) {
            client.layerGroups().createByWorkspace(ws, request);
        } else {
            client.layerGroups().create(request);
        }
    }

    @Override
    public void updateTitle(String name, String title, String ws) {
        UpdateLayerGroupRequest request = UpdateLayerGroupRequest.builder().title(title).build();
        if (ws != null && !ws.isEmpty()) {
            client.layerGroups().updateByWorkspace(ws, name, request);
        } else {
            client.layerGroups().update(name, request);
        }
    }

    @Override
    public void delete(String name, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.layerGroups().deleteByWorkspace(ws, name);
        } else {
            client.layerGroups().delete(name);
        }
    }
}
