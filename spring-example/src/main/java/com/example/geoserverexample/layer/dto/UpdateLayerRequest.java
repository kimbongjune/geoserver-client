package com.example.geoserverexample.layer.dto;

public class UpdateLayerRequest {

    private boolean opaque;
    private boolean enabled = true;
    private boolean advertised = true;
    private String path;
    private String attributionTitle;
    private String attributionHref;
    private String defaultStyleName;
    private String defaultWMSInterpolationMethod;

    public boolean isOpaque() {
        return opaque;
    }

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdvertised() {
        return advertised;
    }

    public void setAdvertised(boolean advertised) {
        this.advertised = advertised;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAttributionTitle() {
        return attributionTitle;
    }

    public void setAttributionTitle(String attributionTitle) {
        this.attributionTitle = attributionTitle;
    }

    public String getAttributionHref() {
        return attributionHref;
    }

    public void setAttributionHref(String attributionHref) {
        this.attributionHref = attributionHref;
    }

    public String getDefaultStyleName() {
        return defaultStyleName;
    }

    public void setDefaultStyleName(String defaultStyleName) {
        this.defaultStyleName = defaultStyleName;
    }

    public String getDefaultWMSInterpolationMethod() {
        return defaultWMSInterpolationMethod;
    }

    public void setDefaultWMSInterpolationMethod(String defaultWMSInterpolationMethod) {
        this.defaultWMSInterpolationMethod = defaultWMSInterpolationMethod;
    }
}
