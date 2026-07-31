package com.example.geoserverexample.layergroup.dto;

import java.util.List;

public class CreateLayerGroupRequest {

    private String name;
    private String title;
    private List<String> layerNames;
    private String ws;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLayerNames() {
        return layerNames;
    }

    public void setLayerNames(List<String> layerNames) {
        this.layerNames = layerNames;
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }
}
