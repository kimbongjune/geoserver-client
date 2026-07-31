package com.example.geoserverexample.style.service;

import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;

public interface StyleService {

    List<WorkspaceSummary> listWorkspaces();

    List<StyleSummary> listGlobalStyles();

    List<StyleSummary> listByWorkspace(String ws);

    void createRaw(String name, String sld, String ws);

    void generate(String name, String layerName, String symbolizer, String color, double size, String ws);

    StyleContent getSldContent(String name, String ws);

    void updateRaw(String name, String sld, String ws);

    void delete(String name, String ws);
}
