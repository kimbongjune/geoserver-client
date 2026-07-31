package com.example.geoserverexample.style.dto;

public class CreateRawStyleRequest {

    private String name;
    private String sld;
    private String ws;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSld() {
        return sld;
    }

    public void setSld(String sld) {
        this.sld = sld;
    }

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }
}
