package com.example.geoserverexample.cascading.dto;

public class UpdateWmsLayerRequest {

    private String ws;
    private String storeName;
    private String title;
    private String description;
    private Double minScale;
    private Double maxScale;

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinScale() {
        return minScale;
    }

    public void setMinScale(Double minScale) {
        this.minScale = minScale;
    }

    public Double getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(Double maxScale) {
        this.maxScale = maxScale;
    }
}
