package com.example.geoserverexample.vector.dto;

public class UpdateVectorStoreRequest {

    private boolean enabled = true;
    private String description;
    private String newName;
    private boolean defaultStore;
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

    public boolean isDefaultStore() {
        return defaultStore;
    }

    public void setDefaultStore(boolean defaultStore) {
        this.defaultStore = defaultStore;
    }

    public boolean isDisableOnConnFailure() {
        return disableOnConnFailure;
    }

    public void setDisableOnConnFailure(boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
    }
}
