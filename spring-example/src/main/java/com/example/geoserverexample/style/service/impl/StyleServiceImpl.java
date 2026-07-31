package com.example.geoserverexample.style.service.impl;

import com.example.geoserverexample.style.service.StyleService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.style.SldBuilder;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.dto.style.WellKnownName;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StyleServiceImpl implements StyleService {

    private final GeoServerClient client;

    public StyleServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<StyleSummary> listGlobalStyles() {
        return client.styles().list();
    }

    @Override
    public List<StyleSummary> listByWorkspace(String ws) {
        return client.styles().listByWorkspace(ws);
    }

    @Override
    public void createRaw(String name, String sld, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.styles().createByWorkspace(ws, StyleContent.of(sld), name);
        } else {
            client.styles().create(StyleContent.of(sld), name);
        }
    }

    @Override
    public void generate(String name, String layerName, String symbolizer, String color, double size, String ws) {
        SldBuilder.RuleBuilder rule = SldBuilder.create(layerName).rule("rule-1");
        StyleContent content;
        switch (symbolizer) {
            case "line":
                content = rule.line(color, size).build();
                break;
            case "polygon":
                content = rule.polygon(color, "#000000", 1.0).build();
                break;
            default:
                content = rule.point(WellKnownName.CIRCLE, color, size).build();
        }
        if (ws != null && !ws.isEmpty()) {
            client.styles().createByWorkspace(ws, content, name);
        } else {
            client.styles().create(content, name);
        }
    }

    @Override
    public StyleContent getSldContent(String name, String ws) {
        return (ws != null && !ws.isEmpty())
                ? client.styles().getSldByWorkspace(ws, name)
                : client.styles().getSld(name);
    }

    @Override
    public void updateRaw(String name, String sld, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.styles().updateByWorkspace(ws, name, StyleContent.of(sld));
        } else {
            client.styles().update(name, StyleContent.of(sld));
        }
    }

    @Override
    public void delete(String name, String ws) {
        if (ws != null && !ws.isEmpty()) {
            client.styles().deleteByWorkspace(ws, name, true, true);
        } else {
            client.styles().delete(name, true, true);
        }
    }
}
