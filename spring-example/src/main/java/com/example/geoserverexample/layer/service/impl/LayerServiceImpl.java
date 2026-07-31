package com.example.geoserverexample.layer.service.impl;

import com.example.geoserverexample.layer.service.LayerService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LayerServiceImpl implements LayerService {

    private final GeoServerClient client;

    public LayerServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<LayerSummary> listLayers() {
        return client.layers().list();
    }

    @Override
    public List<String> listLayerNames() {
        return client.layers().list().stream()
                .map(LayerSummary::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Layer getLayer(String name) {
        return client.layers().get(name);
    }

    @Override
    public List<StyleSummary> listStylesByLayer(String name) {
        return client.styles().listByLayer(name);
    }

    @Override
    public ResourceEnrichment enrichResource(Layer layer) {
        Layer.ResourceRef resource = layer.getResource();
        if (resource == null || resource.getName() == null) {
            return null;
        }
        String[] parts = resource.getName().split(":", 2);
        if (parts.length != 2) {
            return null;
        }
        String ws = parts[0];
        String resName = parts[1];
        try {
            if ("featureType".equals(resource.getResourceClass())) {
                for (var s : client.datastores().list(ws)) {
                    if (client.featureTypes().exists(ws, s.getName(), resName)) {
                        var ft = client.featureTypes().get(ws, s.getName(), resName);
                        return new ResourceEnrichment(ft.getSrs(), ft.getLatLonBoundingBox());
                    }
                }
            } else if ("coverage".equals(resource.getResourceClass())) {
                for (var s : client.coverageStores().list(ws)) {
                    if (client.coverages().exists(ws, s.getName(), resName)) {
                        return new ResourceEnrichment(client.coverages().get(ws, s.getName(), resName).getSrs(), null);
                    }
                }
            }
        } catch (GeoServerException ignored) {
            // Best-effort enrichment only — the core layer detail above already rendered fine.
        }
        return null;
    }

    @Override
    public void setQueryable(String name, boolean queryable) {
        client.layers().update(name, UpdateLayerRequest.builder().queryable(queryable).build());
    }

    @Override
    public void addStyle(String name, String styleName, boolean setDefault) {
        client.styles().addStyleToLayer(name, styleName, setDefault);
    }

    @Override
    public void updateLayer(String name, boolean opaque, boolean enabled, boolean advertised, String path,
                             String attributionTitle, String attributionHref, String defaultStyleName,
                             String defaultWMSInterpolationMethod) {
        UpdateLayerRequest.Builder builder = UpdateLayerRequest.builder()
                .opaque(opaque).enabled(enabled).advertised(advertised)
                .path(path).attribution(attributionTitle, attributionHref)
                .defaultWMSInterpolationMethod(defaultWMSInterpolationMethod);
        if (defaultStyleName != null && !defaultStyleName.isBlank()) {
            builder.defaultStyleName(defaultStyleName);
        }
        client.layers().update(name, builder.build());
    }
}
