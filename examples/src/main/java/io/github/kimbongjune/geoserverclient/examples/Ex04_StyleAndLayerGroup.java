package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.coverage.CoverageSummary;
import io.github.kimbongjune.geoserverclient.dto.layer.UpdateLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.CreateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroup;
import io.github.kimbongjune.geoserverclient.dto.layergroup.UpdateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.style.Style;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * <h2>What this covers</h2>
 * Creating an SLD style from raw XML, and grouping a real layer into a LayerGroup.
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code StyleContent.of(sldXml)} is how raw SLD text enters the library — see
 *       {@code CONTRIBUTING.md} for why styles are the one place a text document (not a fully
 *       structured DTO) is unavoidable: SLD <em>is</em> a stylesheet grammar, and modeling that
 *       grammar as Java objects is out of scope for a REST client library.</li>
 *   <li>A global style (created via {@code styles().create(...)}, no workspace argument) is not
 *       tied to any workspace — contrast with the layer group below, which <em>is</em>
 *       workspace-scoped ({@code createByWorkspace}).</li>
 *   <li>{@code styles(Collections.singletonList(""))} — an empty string means "use that layer's
 *       own default style" rather than naming a specific style explicitly.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver}. Runs out of the box with no
 * arguments (uses the same tiny bundled {@code sample.tif} as Ex03 to have a real layer to group).
 * Pass your own GeoTIFF path as the program argument to use real imagery instead.
 */
public class Ex04_StyleAndLayerGroup {

    // A minimal-but-valid SLD document. Real styles usually have far more rules/symbolizers —
    // this one just needs to be valid enough for GeoServer to accept and store.
    private static final String SLD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
            + "<NamedLayer><Name>example_style</Name><UserStyle><Name>example_style</Name>"
            + "<Title>Example Style</Title><FeatureTypeStyle><Rule><RasterSymbolizer/></Rule>"
            + "</FeatureTypeStyle></UserStyle></NamedLayer></StyledLayerDescriptor>";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex04: Style + LayerGroup ===\n");

        File tif = args.length > 0 ? new File(args[0]) : extractBundledSample();
        System.out.println("Using GeoTIFF for the layer group's member layer: " + tif.getAbsolutePath()
                + (args.length > 0 ? "" : " (bundled sample)"));

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        System.out.println("[1/3] Creating a global style from raw SLD XML...");
        String styleName = "example_style";
        client.styles().create(StyleContent.of(SLD), styleName);
        Style style = client.styles().get(styleName);
        System.out.println("      -> created: " + style.getName() + " (global — not tied to any workspace)");

        System.out.println("      -> getSld(): fetching the raw SLD body back...");
        StyleContent fetchedSld = client.styles().getSld(styleName);
        System.out.println("      -> got " + fetchedSld.getSldBody().length() + " chars of SLD back");

        System.out.println("[2/3] Setting up a real layer to put in the group (same recipe as Ex03)...");
        String ws = "example_lg_ws";
        client.workspaces().create(CreateWorkspaceRequest.builder(ws).build());
        String storeName = "example_geotiff";
        client.coverageStores().uploadFile(ws, storeName, "file", "geotiff", tif, "first", null, null);
        List<CoverageSummary> coverages = client.coverages().list(ws, storeName);
        String coverageName = coverages.get(0).getName();
        String layerFullName = ws + ":" + coverageName;
        System.out.println("      -> layer ready: " + layerFullName);

        System.out.println("      -> existsByWorkspace()/updateByWorkspace() on that layer...");
        System.out.println("      -> exists: " + client.layers().existsByWorkspace(ws, coverageName));
        client.layers().updateByWorkspace(ws, coverageName,
                UpdateLayerRequest.builder().queryable(true).build());
        System.out.println("      -> updated queryable=true via the workspace-scoped path");

        System.out.println("      -> creating a workspace-scoped style, then getSldByWorkspace()/"
                + "existsByWorkspace()/updateByWorkspace()/addStyleToLayer()...");
        String wsStyleName = "example_ws_style";
        client.styles().createByWorkspace(ws, StyleContent.of(SLD), wsStyleName);
        System.out.println("      -> existsByWorkspace: " + client.styles().existsByWorkspace(ws, wsStyleName));
        StyleContent wsSld = client.styles().getSldByWorkspace(ws, wsStyleName);
        System.out.println("      -> getSldByWorkspace: got " + wsSld.getSldBody().length() + " chars");
        client.styles().updateByWorkspace(ws, wsStyleName, StyleContent.of(SLD));
        System.out.println("      -> updateByWorkspace: re-saved the same SLD body");
        client.styles().addStyleToLayer(layerFullName, styleName, false);
        List<io.github.kimbongjune.geoserverclient.dto.style.StyleSummary> layerStyles =
                client.styles().listByLayer(layerFullName);
        System.out.println("      -> listByLayer: layer now has " + layerStyles.size()
                + " available style(s) after addStyleToLayer()");

        System.out.println("[3/3] Creating a workspace-scoped LayerGroup containing that one layer...");
        String groupName = "example_group";
        client.layerGroups().createByWorkspace(ws, CreateLayerGroupRequest.builder(groupName)
                .layer(layerFullName)
                .styles(Collections.singletonList("")) // "" = use the layer's own default style
                .title("Example Group")
                .build());

        LayerGroup group = client.layerGroups().getByWorkspace(ws, groupName);
        System.out.println("      -> created: " + group.getName()
                + " with " + group.getPublishables().getPublished().size() + " publishable(s)");

        System.out.println("      -> existsByWorkspace()/updateByWorkspace() on the LayerGroup...");
        System.out.println("      -> exists: " + client.layerGroups().existsByWorkspace(ws, groupName));
        client.layerGroups().updateByWorkspace(ws, groupName,
                UpdateLayerGroupRequest.builder().title("Example Group (updated)").build());
        System.out.println("      -> title updated via the workspace-scoped path");

        System.out.println("\nCleaning up...");
        client.layerGroups().deleteByWorkspace(ws, groupName);
        client.styles().deleteByWorkspace(ws, wsStyleName, true, true);
        client.workspaces().delete(ws, true);
        client.styles().delete(styleName, true, true);
        System.out.println("Done.");

        client.close();
    }

    private static File extractBundledSample() throws Exception {
        File tmp = File.createTempFile("geoserver-client-example-sample", ".tif");
        tmp.deleteOnExit();
        try (InputStream in = Ex04_StyleAndLayerGroup.class.getClassLoader().getResourceAsStream("sample.tif");
             OutputStream out = new FileOutputStream(tmp)) {
            if (in == null) {
                throw new IllegalStateException("sample.tif not found on classpath — did the examples build correctly?");
            }
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }
        return tmp;
    }
}
