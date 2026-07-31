package com.example.geoserverexample.gwc.dto;

public class CreateGridSetRequest {

    private String name;
    private int epsg;
    private double minx;
    private double miny;
    private double maxx;
    private double maxy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEpsg() {
        return epsg;
    }

    public void setEpsg(int epsg) {
        this.epsg = epsg;
    }

    public double getMinx() {
        return minx;
    }

    public void setMinx(double minx) {
        this.minx = minx;
    }

    public double getMiny() {
        return miny;
    }

    public void setMiny(double miny) {
        this.miny = miny;
    }

    public double getMaxx() {
        return maxx;
    }

    public void setMaxx(double maxx) {
        this.maxx = maxx;
    }

    public double getMaxy() {
        return maxy;
    }

    public void setMaxy(double maxy) {
        this.maxy = maxy;
    }
}
