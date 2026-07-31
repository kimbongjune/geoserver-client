package com.example.geoserverexample.vector.dto;

public class UpdateFeatureTypeRequest {

    private String newName;
    private String title;
    private String srs;
    private String projectionPolicy;
    private Integer maxFeatures;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrs() {
        return srs;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public String getProjectionPolicy() {
        return projectionPolicy;
    }

    public void setProjectionPolicy(String projectionPolicy) {
        this.projectionPolicy = projectionPolicy;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }
}
