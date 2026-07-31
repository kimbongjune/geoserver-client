package com.example.geoserverexample.workspace.dto;

public class CreateWorkspaceRequest {

    private String name;
    private boolean isolated;
    private boolean setAsDefault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    public boolean isSetAsDefault() {
        return setAsDefault;
    }

    public void setSetAsDefault(boolean setAsDefault) {
        this.setAsDefault = setAsDefault;
    }
}
