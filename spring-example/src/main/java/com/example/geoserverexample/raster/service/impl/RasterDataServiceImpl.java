package com.example.geoserverexample.raster.service.impl;

import com.example.geoserverexample.raster.service.RasterDataService;
import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.common.ProjectionPolicy;
import io.github.kimbongjune.geoserverclient.dto.coverage.Coverage;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.coverage.UpdateCoverageRequest;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStore;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CoverageStoreSummary;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.UpdateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.structuredcoverage.GranuleCollection;
import io.github.kimbongjune.geoserverclient.dto.workspace.WorkspaceSummary;
import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RasterDataServiceImpl implements RasterDataService {

    private final GeoServerClient client;

    public RasterDataServiceImpl(GeoServerClient client) {
        this.client = client;
    }

    @Override
    public List<WorkspaceSummary> listWorkspaces() {
        return client.workspaces().list();
    }

    @Override
    public List<CoverageStoreSummary> listStores(String ws) {
        return client.coverageStores().list(ws);
    }

    @Override
    public CoverageStore getStoreDetail(String ws, String store) {
        return client.coverageStores().get(ws, store);
    }

    @Override
    public List<CovRow> listCoverageRows(String ws, String store) {
        List<CovRow> rows = new ArrayList<>();
        for (CoverageSummary s : client.coverages().list(ws, store)) {
            try {
                Coverage cov = client.coverages().get(ws, store, s.getName());
                rows.add(new CovRow(s.getName(), cov.getEnabled(), cov.getSrs(), cov.getTitle()));
            } catch (GeoServerException e) {
                rows.add(new CovRow(s.getName(), null, "(unreadable: " + e.getMessage() + ")", null));
            }
        }
        return rows;
    }

    @Override
    public List<GranuleCollection.Granule> listGranulesBestEffort(String ws, String store) {
        try {
            GranuleCollection granules = client.structuredCoverages()
                    .listGranules(ws, store, client.coverages().list(ws, store).get(0).getName());
            return granules.getFeatures();
        } catch (GeoServerException ignored) {
            // Not an ImageMosaic store — StructuredCoverage API only applies to those.
            return null;
        }
    }

    @Override
    public boolean isRenaming(String newName) {
        return newName != null && !newName.isBlank();
    }

    @Override
    public void updateStore(String ws, String store, boolean enabled, String description, String newName,
                             String url, boolean disableOnConnFailure) {
        UpdateCoverageStoreRequest.Builder builder = UpdateCoverageStoreRequest.builder()
                .enabled(enabled).description(description)
                .url(url).disableOnConnFailure(disableOnConnFailure);
        if (isRenaming(newName)) {
            builder.name(newName);
        }
        client.coverageStores().update(ws, store, builder.build());
    }

    @Override
    public void enableCoverage(String ws, String store, String cov, boolean enabled) {
        client.coverages().update(ws, store, cov, UpdateCoverageRequest.builder().enabled(enabled).build());
    }

    @Override
    public void updateCoverage(String ws, String store, String cov, String newName, String title, String srs,
                                String projectionPolicy, boolean advertised, String defaultInterpolationMethod) {
        UpdateCoverageRequest.Builder builder = UpdateCoverageRequest.builder()
                .title(title).srs(srs).projectionPolicy(parseProjectionPolicy(projectionPolicy))
                .advertised(advertised).defaultInterpolationMethod(defaultInterpolationMethod);
        if (isRenaming(newName)) {
            builder.name(newName);
        }
        client.coverages().update(ws, store, cov, builder.build());
    }

    private static ProjectionPolicy parseProjectionPolicy(String value) {
        return (value == null || value.trim().isEmpty()) ? null : ProjectionPolicy.valueOf(value.trim());
    }

    @Override
    public void deleteCoverage(String ws, String store, String cov) {
        client.coverages().delete(ws, store, cov);
    }

    @Override
    public void uploadFile(String ws, String storeName, String format, MultipartFile file) throws Exception {
        File tmp = File.createTempFile("upload-", "-" + file.getOriginalFilename());
        file.transferTo(tmp);
        tmp.deleteOnExit();
        client.coverageStores().uploadFile(ws, storeName, "file", format, tmp, "first", null, null);
    }

    @Override
    public void harvest(String ws, String store, String format, MultipartFile file) throws Exception {
        File tmp = File.createTempFile("harvest-", "-" + file.getOriginalFilename());
        file.transferTo(tmp);
        tmp.deleteOnExit();
        // Pass the granule's own format (e.g. "geotiff"), not "imagemosaic" — see
        // Ex14_ImageMosaicAndStructuredCoverage's Javadoc for why "imagemosaic" 500s here.
        client.coverageStores().harvest(ws, store, "file", format, tmp);
    }

    @Override
    public void deleteGranule(String ws, String store, String granuleId) {
        String coverageName = client.coverages().list(ws, store).get(0).getName();
        client.structuredCoverages().deleteGranule(ws, store, coverageName, granuleId);
    }

    @Override
    public void resetStore(String ws, String store) {
        client.coverageStores().reset(ws, store);
    }

    @Override
    public void deleteStore(String ws, String store) {
        client.coverageStores().delete(ws, store, true);
    }
}
