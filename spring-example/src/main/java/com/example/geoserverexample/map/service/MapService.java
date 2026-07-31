package com.example.geoserverexample.map.service;

import java.io.IOException;

public interface MapService {

    /**
     * Proxies a WFS GetFeature request through the backend's authenticated connection so the
     * browser never needs GeoServer credentials for read access either.
     */
    String getFeaturesAsGeoJson(String typeName) throws IOException, InterruptedException;

    /**
     * Proxies a WFS-T Transaction request (insert/update/delete) through the backend's
     * authenticated connection — the browser builds the XML via OpenLayers' WFS format but
     * never holds GeoServer credentials, since anonymous WFS-T requests are rejected as
     * "read-only" by GeoServer's default security config.
     */
    String submitTransaction(String transactionXml) throws IOException, InterruptedException;
}
