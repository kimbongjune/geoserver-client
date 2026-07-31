package com.example.geoserverexample.raster.dto;

public class UpdateRasterStoreRequest {

    private boolean enabled = true;
    private String description;
    private String newName;
    private String url;
    private boolean disableOnConnFailure;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDisableOnConnFailure() {
        return disableOnConnFailure;
    }

    public void setDisableOnConnFailure(boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
    }
}
