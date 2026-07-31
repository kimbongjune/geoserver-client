package com.example.geoserverexample.cascading.service;

import io.github.kimbongjune.geoserverclient.dto.wmsstore.WmsStore;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.WmtsStore;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;

import java.util.List;

public interface CascadingService {

    List<WorkspaceSummary> listWorkspaces();

    List<WmsStore> listWmsStores(String ws);

    List<WmtsStore> listWmtsStores(String ws);

    List<LayerRow> listWmsLayerRows(String ws, List<WmsStore> wmsStores);

    List<LayerRow> listWmtsLayerRows(String ws, List<WmtsStore> wmtsStores);

    void createWmsStore(String ws, String storeName, String capabilitiesUrl);

    void updateWmsStore(String ws, String storeName, boolean enabled, String capabilitiesUrl, String user,
                         String password, Integer connectTimeout, Integer readTimeout,
                         boolean disableOnConnFailure);

    void deleteWmsStore(String ws, String storeName);

    void publishWmsLayer(String ws, String storeName, String layerName, String nativeName);

    void enableWmsLayer(String ws, String storeName, String layerName, boolean enabled);

    void updateWmsLayer(String ws, String storeName, String layerName, String title, String description,
                         Double minScale, Double maxScale);

    void deleteWmsLayer(String ws, String storeName, String layerName);

    void createWmtsStore(String ws, String storeName, String capabilitiesUrl);

    void updateWmtsStore(String ws, String storeName, boolean enabled, String capabilitiesUrl, String user,
                          String password, Integer connectTimeout, Integer readTimeout,
                          boolean disableOnConnFailure);

    void deleteWmtsStore(String ws, String storeName);

    void publishWmtsLayer(String ws, String storeName, String layerName, String nativeName);

    void enableWmtsLayer(String ws, String storeName, String layerName, boolean enabled);

    void updateWmtsLayer(String ws, String storeName, String layerName, String title, String description);

    void deleteWmtsLayer(String ws, String storeName, String layerName);

    class LayerRow {
        private final String storeName;
        private final String layerName;
        public LayerRow(String storeName, String layerName) {
            this.storeName = storeName;
            this.layerName = layerName;
        }
        public String getStoreName() { return storeName; }
        public String getLayerName() { return layerName; }
    }
}
