package com.example.geoserverexample.gwc.service.impl;

import com.example.geoserverexample.gwc.service.GwcService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcDiskQuotaConfig;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFileBlobStore;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcFilterContent;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGlobalSettings;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcGridSet;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcKillType;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcLayer;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcReloadResult;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedRequest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcSeedType;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTileBounds;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcTruncateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GwcServiceImpl implements GwcService {

    private final GeoServerClient client;

    public GwcServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<String> listGridSets() {
        return client.gwcGridSets().list();
    }

    @Override
    public GwcGlobalSettings getGlobal() {
        return client.gwcGlobal().get();
    }

    @Override
    public List<LayerSummary> listLayers() {
        return client.layers().list();
    }

    @Override
    public List<String> listBlobStores() {
        return client.gwcBlobStores().list();
    }

    @Override
    public GwcLayer getGwcLayerBestEffort(String gwcLayer) {
        try {
            return client.gwcLayers().get(gwcLayer);
        } catch (GeoServerException e) {
            // No explicit GWC config for this layer yet (e.g. after "Remove GWC override",
            // or it was never upserted) — the template shows the "no override" state instead.
            return null;
        }
    }

    @Override
    public GwcDiskQuotaConfig getDiskQuotaBestEffort() {
        try {
            return client.gwcDiskQuota().get();
        } catch (GeoServerException ignored) {
            // DiskQuota module not installed — fine, template handles a null diskQuota.
            return null;
        }
    }

    @Override
    public void createBlobStore(String name, String baseDirectory, boolean enabled) {
        GwcFileBlobStore blobStore = new GwcFileBlobStore(name, baseDirectory);
        blobStore.setEnabled(enabled);
        client.gwcBlobStores().upsert(name, blobStore);
    }

    @Override
    public void deleteBlobStore(String name) {
        client.gwcBlobStores().delete(name);
    }

    @Override
    public void updateDiskQuota(boolean enabled, int cacheCleanUpFrequency) {
        GwcDiskQuotaConfig config = client.gwcDiskQuota().get();
        config.setEnabled(enabled);
        config.setCacheCleanUpFrequency(cacheCleanUpFrequency);
        // GeoServer's GWC diskquota PUT rejects the globalQuota shape that its own GET returns
        // (XStream expects a <value> element where GET emits <id>) — omit it so the server
        // preserves the existing quota untouched, per GwcDiskQuotaManager's update() Javadoc.
        config.setGlobalQuota(null);
        client.gwcDiskQuota().update(config);
    }

    @Override
    public void upsertGwcLayer(String layerName, boolean enabled, Long expireCache) {
        GwcLayer layer = new GwcLayer(layerName);
        layer.setEnabled(enabled);
        layer.gridSetNames("EPSG:4326");
        layer.setMimeFormats(java.util.List.of("image/png"));
        if (expireCache != null) {
            layer.setExpireCache(expireCache);
        }
        client.gwcLayers().upsert(layerName, layer);
    }

    @Override
    public void deleteGwcLayer(String name) {
        client.gwcLayers().delete(name);
    }

    @Override
    public void createGridSet(String name, int epsg, double minx, double miny, double maxx, double maxy) {
        client.gwcGridSets().upsert(name, new GwcGridSet(name, epsg, minx, miny, maxx, maxy));
    }

    @Override
    public void deleteGridSet(String name) {
        client.gwcGridSets().delete(name);
    }

    @Override
    public void updateGlobal(int backendTimeout) {
        GwcGlobalSettings settings = new GwcGlobalSettings();
        settings.setBackendTimeout(backendTimeout);
        client.gwcGlobal().update(settings);
    }

    @Override
    public void seedLayer(String layer) {
        String encoded = layer.replace(":", "%3A");
        client.gwcSeed().seed(encoded, new GwcSeedRequest(layer, 4326, 0, 1, "image/png", GwcSeedType.SEED)
                .threadCount(1));
    }

    @Override
    public void truncateLayer(String layer) {
        client.gwcMassTruncate().truncate(new GwcTruncateLayerRequest(layer));
    }

    @Override
    public String killAll() {
        return client.gwcSeed().killAll(GwcKillType.ALL);
    }

    @Override
    public GwcReloadResult reload() {
        return client.gwcReload().reload();
    }

    @Override
    public String inspectLayer(String layer) {
        String encoded = layer.replace(":", "%3A");
        GwcLayer gwcLayer = client.gwcLayers().get(layer);
        GwcTileBounds bounds = client.gwcBounds().getBounds(encoded, "EPSG:4326");
        return "gwcLayers().get(): enabled=" + gwcLayer.getEnabled()
                + ", mimeFormats=" + gwcLayer.getMimeFormats() + " — gwcBounds().getBounds(): " + bounds;
    }

    @Override
    public String filterUpdate(String filterName) {
        return client.gwcFilterUpdates().updateFilterXml(filterName, GwcFilterContent.empty());
    }
}
