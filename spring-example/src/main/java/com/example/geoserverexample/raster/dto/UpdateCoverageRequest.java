package com.example.geoserverexample.raster.dto;

public class UpdateCoverageRequest {

    private String newName;
    private String title;
    private String srs;
    private String projectionPolicy;
    private boolean advertised = true;
    private String defaultInterpolationMethod;

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

    public boolean isAdvertised() {
        return advertised;
    }

    public void setAdvertised(boolean advertised) {
        this.advertised = advertised;
    }

    public String getDefaultInterpolationMethod() {
        return defaultInterpolationMethod;
    }

    public void setDefaultInterpolationMethod(String defaultInterpolationMethod) {
        this.defaultInterpolationMethod = defaultInterpolationMethod;
    }
}
