package com.example.geoserverexample.gwc.service;

import io.github.kimbongjune.geoserverclient.dto.gwc.GwcDiskQuotaConfig;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcIndexResult;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;

import java.util.List;

public interface GwcService {

    List<String> listGridSets();

    GwcGlobalSettings getGlobal();

    List<LayerSummary> listLayers();

    List<String> listBlobStores();

    // No explicit GWC config for this layer yet (e.g. after "Remove GWC override",
    // or it was never upserted) — returns null instead of propagating a GeoServerException
    // so the template can show the "no override" state.
    GwcLayer getGwcLayerBestEffort(String gwcLayer);

    // DiskQuota module not installed — returns null (best-effort); the template handles a
    // null diskQuota.
    GwcDiskQuotaConfig getDiskQuotaBestEffort();

    void createBlobStore(String name, String baseDirectory, boolean enabled);

    void deleteBlobStore(String name);

    void updateDiskQuota(boolean enabled, int cacheCleanUpFrequency);

    void upsertGwcLayer(String layerName, boolean enabled, Long expireCache);

    void deleteGwcLayer(String name);

    void createGridSet(String name, int epsg, double minx, double miny, double maxx, double maxy);

    void deleteGridSet(String name);

    void updateGlobal(int backendTimeout);

    void seedLayer(String layer);

    void truncateLayer(String layer);

    String killAll();

    GwcReloadResult reload();

    String inspectLayer(String layer);

    String filterUpdate(String filterName);

    GwcIndexResult getIndex();
}
