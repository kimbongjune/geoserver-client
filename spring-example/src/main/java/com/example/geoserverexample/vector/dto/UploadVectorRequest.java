package com.example.geoserverexample.vector.dto;

import org.springframework.web.multipart.MultipartFile;

public class UploadVectorRequest {

    private String ws;
    private String storeName;
    private String format;
    private MultipartFile file;

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
