package com.example.geoserverexample.vector.dto;

public class CreatePostgisRequest {

    private String ws;
    private String storeName;
    private boolean createSampleTable;

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean isCreateSampleTable() {
        return createSampleTable;
    }

    public void setCreateSampleTable(boolean createSampleTable) {
        this.createSampleTable = createSampleTable;
    }
}
