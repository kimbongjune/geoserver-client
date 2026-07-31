package com.example.geoserverexample.map.controller;

import com.example.geoserverexample.layer.service.LayerService;
import com.example.geoserverexample.map.service.MapService;
import com.example.geoserverexample.workspace.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class MapController {

    private final LayerService layerService;
    private final WorkspaceService workspaceService;
    private final MapService mapService;

    @Value("${geoserver.url}")
    private String geoServerUrl;

    public MapController(LayerService layerService, WorkspaceService workspaceService, MapService mapService) {
        this.layerService = layerService;
        this.workspaceService = workspaceService;
        this.mapService = mapService;
    }

    @GetMapping("/map")
    public String map(@RequestParam(required = false) String layer, Model model) {
        model.addAttribute("layerNames", layerService.listLayerNames());
        model.addAttribute("selectedLayer", layer);
        model.addAttribute("geoServerUrl", geoServerUrl);
        model.addAttribute("namespaceUris", workspaceService.getNamespaceUris());
        return "map";
    }

    @GetMapping("/map/wfs/features")
    @ResponseBody
    public ResponseEntity<String> getFeatures(@RequestParam String layer) {
        try {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(mapService.getFeaturesAsGeoJson(layer));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping(value = "/map/wfs/transaction", consumes = MediaType.ALL_VALUE)
    @ResponseBody
    public ResponseEntity<String> submitTransaction(@RequestBody String transactionXml) {
        try {
            return ResponseEntity.ok().contentType(MediaType.TEXT_XML)
                    .body(mapService.submitTransaction(transactionXml));
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("<error>" + e.getMessage() + "</error>");
        }
    }
}
