package io.github.kimbongjune.geoserverclient.examples;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.dto.style.*;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;

import java.util.List;

import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;

/**
 * <h2>What this covers</h2>
 * Every {@link SldBuilder} symbolizer type — Point, Line, Polygon, Text, Raster — with
 * OGC filter predicates and {@link SldExpression} usage. Shows both the one-liner convenience
 * methods on {@link io.github.kimbongjune.geoserverclient.dto.style.SldBuilder.RuleBuilder}
 * and the full sub-builder variants (PointBuilder, LineBuilder, PolygonBuilder, TextBuilder,
 * RasterBuilder).
 *
 * <h2>Key things to notice</h2>
 * <ul>
 *   <li>{@code SldBuilder} produces valid SLD 1.0 XML without any raw XML string concatenation.</li>
 *   <li>{@code OgcFilters.*} static factory methods cover all OGC comparison, spatial, and
 *       logical predicates.</li>
 *   <li>{@code SldExpression} handles dynamic values — attribute-driven size/rotation and OGC
 *       function-based label expressions.</li>
 *   <li>Every style is created workspace-scoped and cleaned up at the end — safe to re-run.</li>
 * </ul>
 *
 * <h2>Prerequisites</h2>
 * A local GeoServer at {@code http://localhost:8100/geoserver} with admin/geoserver credentials.
 * No file or data is required — all styles are created and immediately deleted.
 */
public class Ex12_SldBuilder {

    private static final String WS = "example_sld_ws";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ex12: SldBuilder — all symbolizer types ===\n");

        GeoServerClient client = GeoServerClient.builder()
                .url("http://localhost:8100/geoserver")
                .credentials("admin", "geoserver")
                .defaultFormat(DataFormat.JSON)
                .build();

        // Setup workspace
        if (!client.workspaces().exists(WS)) {
            client.workspaces().create(CreateWorkspaceRequest.builder(WS).build());
            System.out.println("[setup] Created workspace: " + WS);
        }

        try {
            step1_point(client);
            step2_line(client);
            step3_polygon(client);
            step4_text(client);
            step5_raster(client);
            step6_ogcFilters(client);
            step7_sldExpressions(client);
            step8_multiScale(client);
        } finally {
            // Clean up all created styles
            List<StyleSummary> styles = client.styles().listByWorkspace(WS);
            for (StyleSummary s : styles) {
                client.styles().deleteByWorkspace(WS, s.getName(), true, true);
            }
            client.workspaces().delete(WS, true);
            System.out.println("\n[cleanup] Removed workspace and all styles");
        }

        System.out.println("\nDone.");
    }

    // ── 1. Point symbolizer ────────────────────────────────────────────────

    private static void step1_point(GeoServerClient client) {
        System.out.println("\n[1] Point symbolizers");

        // Simple one-liner
        StyleContent simple = SldBuilder.create("cities")
                .styleName("ex-point-simple")
                .rule("large-city")
                    .filter(greaterThan("population", 1_000_000))
                    .point(WellKnownName.CIRCLE, "#FF6B35", 12)
                .rule("small-city")
                    .elseFilter()
                    .point(WellKnownName.CIRCLE, "#FFAA55", 8)
                .build();
        client.styles().createByWorkspace(WS, simple, "ex-point-simple");
        System.out.println("    -> ex-point-simple (WellKnownName, elseFilter)");

        // Full PointBuilder — fill, stroke, size, rotation
        StyleContent full = SldBuilder.create("airports")
                .styleName("ex-point-full")
                .rule("airport")
                    .mark(WellKnownName.SQUARE)
                        .fill("#4A90D9", 0.9)
                        .stroke("#FFFFFF", 2.0)
                        .size(14.0)
                        .rotation(45.0)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, full, "ex-point-full");
        System.out.println("    -> ex-point-full (PointBuilder: fill+stroke+rotation)");

        // External graphic
        StyleContent external = SldBuilder.create("pois")
                .styleName("ex-point-external")
                .rule("poi")
                    .externalPoint("http://example.com/icons/marker.png", "image/png")
                        .size(24.0)
                        .opacity(0.85)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, external, "ex-point-external");
        System.out.println("    -> ex-point-external (ExternalGraphic PNG)");
    }

    // ── 2. Line symbolizer ─────────────────────────────────────────────────

    private static void step2_line(GeoServerClient client) {
        System.out.println("\n[2] Line symbolizers");

        // Simple
        StyleContent simple = SldBuilder.create("roads")
                .styleName("ex-line-simple")
                .rule("highway")
                    .filter(equalTo("road_type", "highway"))
                    .line("#CC0000", 4.0)
                .rule("local")
                    .elseFilter()
                    .line("#888888", 1.5)
                .build();
        client.styles().createByWorkspace(WS, simple, "ex-line-simple");
        System.out.println("    -> ex-line-simple");

        // Full LineBuilder — dash, cap, join
        StyleContent full = SldBuilder.create("railways")
                .styleName("ex-line-full")
                .rule("track")
                    .stroke("#333333", 3.0)
                        .dashArray("8 4")
                        .lineCap(LineCap.ROUND)
                        .lineJoin(LineJoin.ROUND)
                        .opacity(0.9)
                        .perpendicularOffset(2.0)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, full, "ex-line-full");
        System.out.println("    -> ex-line-full (LineBuilder: dash+cap+join+offset)");

        // Graphic stroke (repeating mark along line)
        StyleContent graphicStroke = SldBuilder.create("fences")
                .styleName("ex-line-graphic-stroke")
                .rule("fence")
                    .stroke("#8B4513", 1.0)
                        .graphicStroke(WellKnownName.X, "#8B4513", 6.0)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, graphicStroke, "ex-line-graphic-stroke");
        System.out.println("    -> ex-line-graphic-stroke (GraphicStroke)");
    }

    // ── 3. Polygon symbolizer ──────────────────────────────────────────────

    private static void step3_polygon(GeoServerClient client) {
        System.out.println("\n[3] Polygon symbolizers");

        // Simple
        StyleContent simple = SldBuilder.create("landuse")
                .styleName("ex-polygon-simple")
                .rule("residential")
                    .filter(equalTo("land_use", "residential"))
                    .polygon("#E8F5E9", "#4CAF50", 1.5)
                .rule("commercial")
                    .filter(equalTo("land_use", "commercial"))
                    .polygon("#E3F2FD", "#1565C0", 1.5)
                .rule("other")
                    .elseFilter()
                    .polygon("#F5F5F5", "#9E9E9E", 0.5)
                .build();
        client.styles().createByWorkspace(WS, simple, "ex-polygon-simple");
        System.out.println("    -> ex-polygon-simple (fill+stroke, 3 rules)");

        // Full PolygonBuilder — opacity, stroke options
        StyleContent full = SldBuilder.create("parcels")
                .styleName("ex-polygon-full")
                .rule("selected")
                    .fill("#336699")
                        .fillOpacity(0.6)
                        .stroke("#003366", 2.0)
                            .strokeLineCap(LineCap.ROUND)
                            .strokeLineJoin(LineJoin.MITRE)
                            .strokeDashArray("6 3")
                    .end()
                .build();
        client.styles().createByWorkspace(WS, full, "ex-polygon-full");
        System.out.println("    -> ex-polygon-full (PolygonBuilder: opacity+strokeOptions)");

        // Graphic fill pattern
        StyleContent pattern = SldBuilder.create("forest")
                .styleName("ex-polygon-pattern")
                .rule("forest")
                    .fill("#228B22")
                        .fillPattern(WellKnownName.CROSS, "#005500", 8.0)
                        .stroke("#005500", 1.0)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, pattern, "ex-polygon-pattern");
        System.out.println("    -> ex-polygon-pattern (GraphicFill pattern)");
    }

    // ── 4. Text (label) symbolizer ─────────────────────────────────────────

    private static void step4_text(GeoServerClient client) {
        System.out.println("\n[4] Text symbolizers");

        // Simple with halo
        StyleContent simple = SldBuilder.create("cities")
                .styleName("ex-text-simple")
                .rule("labels")
                    .text("name", "#333333", "Arial", 12.0, "#FFFFFF", 2.0)
                .build();
        client.styles().createByWorkspace(WS, simple, "ex-text-simple");
        System.out.println("    -> ex-text-simple (attribute label + halo)");

        // Full TextBuilder — bold, halo, displacement, vendor options
        StyleContent full = SldBuilder.create("countries")
                .styleName("ex-text-full")
                .rule("large")
                    .filter(greaterThan("pop", 10_000_000))
                    .label("name")
                        .font("Arial", 14.0).bold()
                        .color("#FFFFFF")
                        .halo("#000000", 2.0)
                        .displacement(0, 6)
                        .vendorOption("labelObstacle", "true")
                        .vendorOption("autoWrap", "80")
                    .end()
                .rule("small")
                    .elseFilter()
                    .label("name")
                        .font("Arial", 10.0)
                        .color("#333333")
                        .halo("#FFFFFF", 1.5)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, full, "ex-text-full");
        System.out.println("    -> ex-text-full (bold+halo+displacement+vendorOptions)");

        // Line label (LinePlacement)
        StyleContent lineLabel = SldBuilder.create("roads")
                .styleName("ex-text-line")
                .rule("road-names")
                    .textOnLine("name", "#333333", "SansSerif", 11.0, 8.0)
                .build();
        client.styles().createByWorkspace(WS, lineLabel, "ex-text-line");
        System.out.println("    -> ex-text-line (LinePlacement)");

        // OGC function label expression
        StyleContent funcLabel = SldBuilder.create("parcels")
                .styleName("ex-text-function")
                .rule("combined")
                    .label(SldExpression.function("strConcat",
                            SldExpression.property("id"),
                            SldExpression.literal(" ("),
                            SldExpression.property("area"),
                            SldExpression.literal(" m²)")))
                        .font("Arial", 10).color("#000000")
                    .end()
                .build();
        client.styles().createByWorkspace(WS, funcLabel, "ex-text-function");
        System.out.println("    -> ex-text-function (strConcat function label)");
    }

    // ── 5. Raster symbolizer ───────────────────────────────────────────────

    private static void step5_raster(GeoServerClient client) {
        System.out.println("\n[5] Raster symbolizers");

        // Color RAMP (DEM elevation)
        StyleContent ramp = SldBuilder.create("elevation")
                .styleName("ex-raster-ramp")
                .rule("dem")
                    .raster(1.0, SldColorMapType.RAMP,
                        SldColorMapEntry.of("#313695", 0,    "sea level"),
                        SldColorMapEntry.of("#74ADD1", 500,  "low"),
                        SldColorMapEntry.of("#ABD9E9", 1000, "mid-low"),
                        SldColorMapEntry.of("#FEE090", 2000, "mid"),
                        SldColorMapEntry.of("#F46D43", 3000, "high"),
                        SldColorMapEntry.of("#FFFFFF", 4000, "peak"))
                .build();
        client.styles().createByWorkspace(WS, ramp, "ex-raster-ramp");
        System.out.println("    -> ex-raster-ramp (RAMP colormap, 6 entries)");

        // INTERVALS (classified slope)
        StyleContent intervals = SldBuilder.create("slope")
                .styleName("ex-raster-intervals")
                .rule("classes")
                    .raster(1.0, SldColorMapType.INTERVALS,
                        SldColorMapEntry.of("#1A9641", 0,  "flat"),
                        SldColorMapEntry.of("#A6D96A", 10, "gentle"),
                        SldColorMapEntry.of("#FFFFBF", 25, "moderate"),
                        SldColorMapEntry.of("#D7191C", 45, "steep"))
                .build();
        client.styles().createByWorkspace(WS, intervals, "ex-raster-intervals");
        System.out.println("    -> ex-raster-intervals (INTERVALS colormap)");

        // Full RasterBuilder — RGB channels, contrast enhancement, shaded relief
        StyleContent rgb = SldBuilder.create("satellite")
                .styleName("ex-raster-rgb")
                .rule("rgb")
                    .rasterBuilder()
                        .opacity(0.95)
                        .channelRGB("1", "2", "3")
                        .contrastEnhancement(ContrastMethod.NORMALIZE)
                        .overlapBehavior(OverlapBehavior.LATEST_ON_TOP)
                        .shadedRelief(55, false)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, rgb, "ex-raster-rgb");
        System.out.println("    -> ex-raster-rgb (RGB channels+normalize+shaded relief)");

        // Gray channel with gamma correction and image outline
        StyleContent gray = SldBuilder.create("panchromatic")
                .styleName("ex-raster-gray")
                .rule("gray")
                    .rasterBuilder()
                        .channelGray("1", 1.5)
                        .colorMap(SldColorMapType.RAMP,
                            SldColorMapEntry.of("#000000", 0),
                            SldColorMapEntry.of("#FFFFFF", 255))
                        .imageOutlineLine("#333333", 0.5)
                    .end()
                .build();
        client.styles().createByWorkspace(WS, gray, "ex-raster-gray");
        System.out.println("    -> ex-raster-gray (gray channel, gamma, imageOutline)");
    }

    // ── 6. OGC Filters ────────────────────────────────────────────────────

    private static void step6_ogcFilters(GeoServerClient client) {
        System.out.println("\n[6] OGC filter predicates");

        StyleContent sld = SldBuilder.create("features")
                .styleName("ex-filters")
                // Comparison operators
                .rule("equal")
                    .filter(equalTo("type", "A"))
                    .polygon("#FF0000", "#CC0000", 1)
                .rule("not-equal")
                    .filter(notEqualTo("status", "closed"))
                    .polygon("#00FF00", "#00CC00", 1)
                .rule("range")
                    .filter(and(
                        greaterThanOrEqualTo("rank", 1),
                        lessThanOrEqualTo("rank", 5)))
                    .polygon("#0000FF", "#0000CC", 1)
                .rule("between")
                    .filter(between("score", 40, 60))
                    .polygon("#FF8800", "#CC6600", 1)
                .rule("like")
                    .filter(like("name", "New*"))
                    .polygon("#FF00FF", "#CC00CC", 1)
                .rule("null")
                    .filter(isNull("deleted_at"))
                    .polygon("#00FFFF", "#00CCCC", 1)
                // Logical operators
                .rule("and-or")
                    .filter(or(
                        and(equalTo("type", "school"), greaterThan("capacity", 100)),
                        equalTo("type", "hospital")))
                    .polygon("#FFFF00", "#CCCC00", 1)
                .rule("not")
                    .filter(not(equalTo("status", "demolished")))
                    .polygon("#8800FF", "#6600CC", 1)
                // Spatial
                .rule("bbox")
                    .filter(bbox("geometry", 126.9, 37.5, 127.1, 37.7, "EPSG:4326"))
                    .polygon("#FF6600", "#CC4400", 1)
                .build();
        client.styles().createByWorkspace(WS, sld, "ex-filters");
        System.out.println("    -> ex-filters (9 filter types: equal, between, like, null, and, or, not, bbox)");
    }

    // ── 7. SldExpression ──────────────────────────────────────────────────

    private static void step7_sldExpressions(GeoServerClient client) {
        System.out.println("\n[7] SldExpression — dynamic sizes and functions");

        // Proportional symbol driven by attribute
        StyleContent proportional = SldBuilder.create("earthquakes")
                .styleName("ex-expr-proportional")
                .rule("quake")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#FF4444", 0.7)
                        .stroke("#CC0000", 1.0)
                        // size driven by the "magnitude" attribute
                        .size(SldExpression.mul(
                                SldExpression.property("magnitude"),
                                SldExpression.literal(3)))
                    .end()
                .build();
        client.styles().createByWorkspace(WS, proportional, "ex-expr-proportional");
        System.out.println("    -> ex-expr-proportional (size = magnitude * 3)");

        // Rotation driven by attribute
        StyleContent rotated = SldBuilder.create("wind")
                .styleName("ex-expr-rotation")
                .rule("wind-arrow")
                    .mark(WellKnownName.TRIANGLE)
                        .fill("#0066CC").size(12)
                        .rotation(SldExpression.property("direction"))
                    .end()
                .build();
        client.styles().createByWorkspace(WS, rotated, "ex-expr-rotation");
        System.out.println("    -> ex-expr-rotation (rotation = direction attribute)");

        // Function-based filter
        StyleContent funcFilter = SldBuilder.create("roads")
                .styleName("ex-expr-func-filter")
                .rule("highways")
                    .filter(equalTo(
                            SldExpression.function("strToLowerCase", SldExpression.property("type")),
                            SldExpression.literal("highway")))
                    .line("#CC0000", 3)
                .build();
        client.styles().createByWorkspace(WS, funcFilter, "ex-expr-func-filter");
        System.out.println("    -> ex-expr-func-filter (strToLowerCase function in filter)");

        // Arithmetic expression
        StyleContent arithmetic = SldBuilder.create("data")
                .styleName("ex-expr-arithmetic")
                .rule("computed")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#6600CC")
                        // size = (value / max_value) * 20
                        .size(SldExpression.mul(
                                SldExpression.div(
                                        SldExpression.property("value"),
                                        SldExpression.literal(100)),
                                SldExpression.literal(20)))
                    .end()
                .build();
        client.styles().createByWorkspace(WS, arithmetic, "ex-expr-arithmetic");
        System.out.println("    -> ex-expr-arithmetic (size = (value/100)*20)");
    }

    // ── 8. Multi-scale rules ──────────────────────────────────────────────

    private static void step8_multiScale(GeoServerClient client) {
        System.out.println("\n[8] Multi-scale rules with scale denominators");

        StyleContent multiScale = SldBuilder.create("roads")
                .styleName("ex-multiscale")
                // Detailed: zoom in (scale < 50,000)
                .rule("detail")
                    .maxScale(50_000)
                    .stroke("#CC0000", 3.0)
                        .dashArray("none")
                    .end()
                    .label("name")
                        .font("Arial", 12).color("#333333")
                        .halo("#FFFFFF", 1.5)
                    .end()
                // Medium: 50,000 to 250,000
                .rule("medium")
                    .minScale(50_000).maxScale(250_000)
                    .line("#CC0000", 1.5)
                // Overview: 250,000+  → thin gray line, no label
                .rule("overview")
                    .minScale(250_000)
                    .line("#AAAAAA", 0.5)
                .build();
        client.styles().createByWorkspace(WS, multiScale, "ex-multiscale");
        System.out.println("    -> ex-multiscale (3 scale levels: <50k / 50k-250k / 250k+)");

        System.out.println("\n[summary] Created " + 3 + 3 + 3 + 4 + 4 + 1 + 4 + 1 +
                " styles across 8 steps in workspace '" + WS + "'");
    }
}
