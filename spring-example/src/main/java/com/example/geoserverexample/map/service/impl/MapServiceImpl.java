package com.example.geoserverexample.map.service.impl;

import com.example.geoserverexample.map.service.MapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MapServiceImpl implements MapService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${geoserver.url}")
    private String geoServerUrl;

    @Value("${geoserver.username}")
    private String username;

    @Value("${geoserver.password}")
    private String password;

    private String basicAuthHeader() {
        String creds = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getFeaturesAsGeoJson(String typeName) throws IOException, InterruptedException {
        String url = geoServerUrl + "/wfs?service=WFS&version=2.0.0&request=GetFeature&typeNames="
                + URLEncoder.encode(typeName, StandardCharsets.UTF_8) + "&outputFormat=application/json";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", basicAuthHeader())
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    @Override
    public String submitTransaction(String transactionXml) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(geoServerUrl + "/wfs"))
                .header("Authorization", basicAuthHeader())
                .header("Content-Type", "text/xml")
                .POST(HttpRequest.BodyPublishers.ofString(transactionXml, StandardCharsets.UTF_8))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
