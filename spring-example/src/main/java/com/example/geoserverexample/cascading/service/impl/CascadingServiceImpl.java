package com.example.geoserverexample.cascading.service.impl;

import com.example.geoserverexample.cascading.service.CascadingService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.UpdateWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.WmsLayerSummary;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.UpdateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.PublishWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.UpdateWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.WmtsLayerSummary;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.UpdateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CascadingServiceImpl implements CascadingService {

    private final GeoServerClient client;

    public CascadingServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<WmsStore> listWmsStores(String ws) {
        List<WmsStore> wmsStores = new ArrayList<>();
        for (var s : client.wmsStores().list(ws)) {
            wmsStores.add(client.wmsStores().get(ws, s.getName()));
        }
        return wmsStores;
    }

    @Override
    public List<WmtsStore> listWmtsStores(String ws) {
        List<WmtsStore> wmtsStores = new ArrayList<>();
        for (var s : client.wmtsStores().list(ws)) {
            wmtsStores.add(client.wmtsStores().get(ws, s.getName()));
        }
        return wmtsStores;
    }

    @Override
    public List<LayerRow> listWmsLayerRows(String ws, List<WmsStore> wmsStores) {
        List<LayerRow> wmsLayers = new ArrayList<>();
        for (WmsStore s : wmsStores) {
            for (WmsLayerSummary l : client.wmsLayers().list(ws, s.getName())) {
                wmsLayers.add(new LayerRow(s.getName(), l.getName()));
            }
        }
        return wmsLayers;
    }

    @Override
    public List<LayerRow> listWmtsLayerRows(String ws, List<WmtsStore> wmtsStores) {
        List<LayerRow> wmtsLayers = new ArrayList<>();
        for (WmtsStore s : wmtsStores) {
            for (WmtsLayerSummary l : client.wmtsLayers().list(ws, s.getName())) {
                wmtsLayers.add(new LayerRow(s.getName(), l.getName()));
            }
        }
        return wmtsLayers;
    }

    @Override
    public void createWmsStore(String ws, String storeName, String capabilitiesUrl) {
        client.wmsStores().create(ws, CreateWmsStoreRequest.builder(storeName, capabilitiesUrl).enabled(true).build());
    }

    @Override
    public void updateWmsStore(String ws, String storeName, boolean enabled, String capabilitiesUrl, String user,
                                String password, Integer connectTimeout, Integer readTimeout,
                                boolean disableOnConnFailure) {
        client.wmsStores().update(ws, storeName, UpdateWmsStoreRequest.builder()
                .enabled(enabled).capabilitiesURL(capabilitiesUrl).user(user).password(password)
                .connectTimeout(connectTimeout).readTimeout(readTimeout)
                .disableOnConnFailure(disableOnConnFailure).build());
    }

    @Override
    public void deleteWmsStore(String ws, String storeName) {
        client.wmsStores().delete(ws, storeName, true);
    }

    @Override
    public void publishWmsLayer(String ws, String storeName, String layerName, String nativeName) {
        client.wmsLayers().publish(ws, storeName,
                PublishWmsLayerRequest.builder(layerName, nativeName).title(layerName).build());
    }

    @Override
    public void enableWmsLayer(String ws, String storeName, String layerName, boolean enabled) {
        client.wmsLayers().update(ws, storeName, layerName, UpdateWmsLayerRequest.builder().enabled(enabled).build());
    }

    @Override
    public void updateWmsLayer(String ws, String storeName, String layerName, String title, String description,
                                Double minScale, Double maxScale) {
        UpdateWmsLayerRequest.Builder builder = UpdateWmsLayerRequest.builder()
                .title(title).description(description).enabled(true);
        if (minScale != null) {
            builder.minScale(minScale);
        }
        if (maxScale != null) {
            builder.maxScale(maxScale);
        }
        client.wmsLayers().update(ws, storeName, layerName, builder.build());
    }

    @Override
    public void deleteWmsLayer(String ws, String storeName, String layerName) {
        client.wmsLayers().delete(ws, storeName, layerName, true);
    }

    @Override
    public void createWmtsStore(String ws, String storeName, String capabilitiesUrl) {
        client.wmtsStores().create(ws, CreateWmtsStoreRequest.builder(storeName, capabilitiesUrl).enabled(true).build());
    }

    @Override
    public void updateWmtsStore(String ws, String storeName, boolean enabled, String capabilitiesUrl, String user,
                                 String password, Integer connectTimeout, Integer readTimeout,
                                 boolean disableOnConnFailure) {
        client.wmtsStores().update(ws, storeName, UpdateWmtsStoreRequest.builder()
                .enabled(enabled).capabilitiesURL(capabilitiesUrl).user(user).password(password)
                .connectTimeout(connectTimeout).readTimeout(readTimeout)
                .disableOnConnFailure(disableOnConnFailure).build());
    }

    @Override
    public void deleteWmtsStore(String ws, String storeName) {
        client.wmtsStores().delete(ws, storeName, true);
    }

    @Override
    public void publishWmtsLayer(String ws, String storeName, String layerName, String nativeName) {
        client.wmtsLayers().publish(ws, storeName,
                PublishWmtsLayerRequest.builder(layerName, nativeName).title(layerName).build());
    }

    @Override
    public void enableWmtsLayer(String ws, String storeName, String layerName, boolean enabled) {
        client.wmtsLayers().update(ws, storeName, layerName, UpdateWmtsLayerRequest.builder().enabled(enabled).build());
    }

    @Override
    public void updateWmtsLayer(String ws, String storeName, String layerName, String title, String description) {
        client.wmtsLayers().update(ws, storeName, layerName, UpdateWmtsLayerRequest.builder()
                .title(title).description(description).enabled(true).build());
    }

    @Override
    public void deleteWmtsLayer(String ws, String storeName, String layerName) {
        client.wmtsLayers().delete(ws, storeName, layerName, true);
    }
}
