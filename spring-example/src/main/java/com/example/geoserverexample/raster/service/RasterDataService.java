package com.example.geoserverexample.raster.service;

import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStore;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RasterDataService {

    List<WorkspaceSummary> listWorkspaces();

    List<CoverageStoreSummary> listStores(String ws);

    CoverageStore getStoreDetail(String ws, String store);

    List<CovRow> listCoverageRows(String ws, String store);

    // Not an ImageMosaic store — StructuredCoverage API only applies to those, so this
    // returns null (best-effort) rather than propagating a GeoServerException.
    List<GranuleCollection.Granule> listGranulesBestEffort(String ws, String store);

    boolean isRenaming(String newName);

    void updateStore(String ws, String store, boolean enabled, String description, String newName,
                      String url, boolean disableOnConnFailure);

    void enableCoverage(String ws, String store, String cov, boolean enabled);

    void updateCoverage(String ws, String store, String cov, String newName, String title, String srs,
                         String projectionPolicy, boolean advertised, String defaultInterpolationMethod);

    void deleteCoverage(String ws, String store, String cov);

    void uploadFile(String ws, String storeName, String format, MultipartFile file) throws Exception;

    // Pass the granule's own format (e.g. "geotiff"), not "imagemosaic" — see
    // Ex14_ImageMosaicAndStructuredCoverage's Javadoc for why "imagemosaic" 500s here.
    void harvest(String ws, String store, String format, MultipartFile file) throws Exception;

    void deleteGranule(String ws, String store, String granuleId);

    void resetStore(String ws, String store);

    void deleteStore(String ws, String store);

    class CovRow {
        private final String name;
        private final boolean enabled;
        private final String srs;
        private final String title;
        public CovRow(String name, Boolean enabled, String srs, String title) {
            this.name = name;
            this.enabled = Boolean.TRUE.equals(enabled);
            this.srs = srs;
            this.title = title;
        }
        public String getName() { return name; }
        public boolean isEnabled() { return enabled; }
        public String getSrs() { return srs; }
        public String getTitle() { return title; }
    }
}
