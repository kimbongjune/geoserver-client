package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.Layer;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.util.List;

/**
 * Uploading a GeoTIFF with {@code configure=first} auto-creates the CoverageStore, the Coverage,
 * AND a publishable Layer in a single call — there is no separate "create layer" REST endpoint;
 * layers are always a side effect of publishing a Coverage or FeatureType.
 *
 * Pass a real GeoTIFF file path as the program argument.
 */
public class Ex03_CoverageUploadAndLayer {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java ... Ex03_CoverageUploadAndLayer /path/to/file.tif");
            return;
        }

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        String ws = "example_cov_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());

        String storeName = "example_geotiff";
        File tif = new File(args[0]);
        client.coverageStores().uploadFile(ws, storeName, "file", "geotiff", tif, "first", null, null);

        List<CoverageSummary> coverages = client.coverages().list(ws, storeName);
        String coverageName = coverages.get(0).getName();
        System.out.println("Auto-created coverage: " + coverageName);

        // The Layer was auto-created too, under "ws:coverageName"
        String layerFullName = ws + ":" + coverageName;
        Layer layer = client.layers().get(layerFullName);
        System.out.println("Auto-created layer: " + layer.getName()
                + " defaultStyle=" + layer.getDefaultStyle().getName());

        // Layers support partial updates — only the fields you set are sent
        Layer updated = client.layers().update(layerFullName,
                UpdateLayerRequest.builder().queryable(true).build());
        System.out.println("queryable now: " + updated.getQueryable());

        client.workspaces().delete(ws, true); // recurse=true tears down the store/coverage/layer too
        client.close();
    }
}
