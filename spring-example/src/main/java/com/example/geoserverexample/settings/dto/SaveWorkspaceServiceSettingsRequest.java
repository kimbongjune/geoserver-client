package com.example.geoserverexample.settings.dto;

public class SaveWorkspaceServiceSettingsRequest {

    private boolean enabled = true;
    private String title;
    private boolean verbose;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
