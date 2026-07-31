package com.example.geoserverexample.style.dto;

public class GenerateStyleRequest {

    private String name;
    private String layerName;
    private String symbolizer;
    private String color;
    private double size = 6;
    private String ws;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getSymbolizer() {
        return symbolizer;
    }

    public void setSymbolizer(String symbolizer) {
        this.symbolizer = symbolizer;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }
}
