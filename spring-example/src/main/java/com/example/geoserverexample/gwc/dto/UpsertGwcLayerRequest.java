package com.example.geoserverexample.gwc.dto;

public class UpsertGwcLayerRequest {

    private String layerName;
    private boolean enabled = true;
    private Long expireCache;

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getExpireCache() {
        return expireCache;
    }

    public void setExpireCache(Long expireCache) {
        this.expireCache = expireCache;
    }
}
