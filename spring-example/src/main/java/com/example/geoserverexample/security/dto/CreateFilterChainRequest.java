package com.example.geoserverexample.security.dto;

public class CreateFilterChainRequest {

    private String name;
    private String path;
    private String filters = "anonymous";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }
}
