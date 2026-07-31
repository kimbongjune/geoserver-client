package com.example.geoserverexample.cascading.dto;

public class UpdateWmtsStoreRequest {

    private String ws;
    private boolean enabled;
    private String capabilitiesUrl;
    private String user;
    private String password;
    private Integer connectTimeout;
    private Integer readTimeout;
    private boolean disableOnConnFailure;

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCapabilitiesUrl() {
        return capabilitiesUrl;
    }

    public void setCapabilitiesUrl(String capabilitiesUrl) {
        this.capabilitiesUrl = capabilitiesUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isDisableOnConnFailure() {
        return disableOnConnFailure;
    }

    public void setDisableOnConnFailure(boolean disableOnConnFailure) {
        this.disableOnConnFailure = disableOnConnFailure;
    }
}
