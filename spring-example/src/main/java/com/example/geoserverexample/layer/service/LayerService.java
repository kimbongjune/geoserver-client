package com.example.geoserverexample.layer.service;

import io.github.kimbongjune.geoserverclient.dto.featuretype.FeatureType;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.LayerSummary;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;

import java.util.List;

public interface LayerService {

    List<LayerSummary> listLayers();

    List<String> listLayerNames();

    Layer getLayer(String name);

    List<StyleSummary> listStylesByLayer(String name);

    // Best-effort enrichment only — the core layer detail renders fine even if this returns
    // null (e.g. the underlying resource lookup fails with a GeoServerException).
    ResourceEnrichment enrichResource(Layer layer);

    void setQueryable(String name, boolean queryable);

    void addStyle(String name, String styleName, boolean setDefault);

    void updateLayer(String name, boolean opaque, boolean enabled, boolean advertised, String path,
                      String attributionTitle, String attributionHref, String defaultStyleName,
                      String defaultWMSInterpolationMethod);

    class ResourceEnrichment {
        private final String srs;
        private final FeatureType.BoundingBox bbox;
        public ResourceEnrichment(String srs, FeatureType.BoundingBox bbox) {
            this.srs = srs;
            this.bbox = bbox;
        }
        public String getSrs() { return srs; }
        public FeatureType.BoundingBox getBbox() { return bbox; }
    }
}
