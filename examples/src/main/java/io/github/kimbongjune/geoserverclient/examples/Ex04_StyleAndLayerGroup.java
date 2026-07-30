package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.CreateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroup;
import io.github.kimbongjune.geoserverclient.dto.style.Style;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Creating an SLD style from raw XML and grouping a layer into a LayerGroup.
 * Uploads a GeoTIFF (pass its path as the program argument) to have a real layer to group.
 */
public class Ex04_StyleAndLayerGroup {

    private static final String SLD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
            + "<NamedLayer><Name>example_style</Name><UserStyle><Name>example_style</Name>"
            + "<Title>Example Style</Title><FeatureTypeStyle><Rule><RasterSymbolizer/></Rule>"
            + "</FeatureTypeStyle></UserStyle></NamedLayer></StyledLayerDescriptor>";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java ... Ex04_StyleAndLayerGroup /path/to/file.tif");
            return;
        }

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        // Global style — not tied to any workspace
        String styleName = "example_style";
        client.styles().create(StyleContent.of(SLD), styleName);
        Style style = client.styles().get(styleName);
        System.out.println("Created style: " + style.getName());

        // Set up a real layer to put in the group
        String ws = "example_lg_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String storeName = "example_geotiff";
        client.coverageStores().uploadFile(ws, storeName, "file", "geotiff", new File(args[0]), "first", null, null);
        List<CoverageSummary> coverages = client.coverages().list(ws, storeName);
        String layerFullName = ws + ":" + coverages.get(0).getName();

        // LayerGroup with a single layer, using the default style for it ("" = default)
        String groupName = "example_group";
        client.layerGroups().createByWorkspace(ws, CreateLayerGroupRequest.builder(groupName)
                .layer(layerFullName)
                .styles(Collections.singletonList(""))
                .title("Example Group")
                .build());

        LayerGroup group = client.layerGroups().getByWorkspace(ws, groupName);
        System.out.println("Created layer group: " + group.getName()
                + " with " + group.getPublishables().getPublished().size() + " publishable(s)");

        // Cleanup
        client.layerGroups().deleteByWorkspace(ws, groupName);
        client.workspaces().delete(ws, true);
        client.styles().delete(styleName);
        client.close();
    }
}
