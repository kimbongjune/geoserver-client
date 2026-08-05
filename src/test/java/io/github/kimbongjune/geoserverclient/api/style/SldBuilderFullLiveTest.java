package io.github.kimbongjune.geoserverclient.api.style;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.style.*;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
import static io.github.kimbongjune.geoserverclient.dto.style.SldExpression.*;
import static io.github.kimbongjune.geoserverclient.dto.style.SldGeometry.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive live test: every public SldBuilder API method is exercised against
 * the real GeoServer Docker container (localhost:8100).
 *
 * Cross-check strategy for each test:
 *   1. Generate SLD via SldBuilder → upload via StyleManager (library path)
 *   2. POST equivalent raw SLD directly via HttpURLConnection (curl path)
 *   3. Both must return 201 / exist
 *   4. Fetch both back → verify key XML elements are present in both
 */
@DisplayName("[FullLiveTest] SldBuilder 전수조사 — 실제 GeoServer + curl cross-check")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SldBuilderFullLiveTest extends BaseIntegrationTest {

    private static final long   TS = System.currentTimeMillis();
    private static final String WS = "sldfull_" + TS;
    private static final String GS = "http://localhost:8100/geoserver";

    private StyleManager styles;
    private String       auth;

    // ── lifecycle ────────────────────────────────────────────────────────────

    @BeforeAll
    void setUp() {
        client.workspaces().create(CreateWorkspaceRequest.builder(WS));
        styles = client.styles();
        auth   = "Basic " + Base64.getEncoder().encodeToString(
                "admin:geoserver".getBytes(StandardCharsets.UTF_8));
    }

    @AfterAll
    void cleanUp() {
        try { client.workspaces().delete(WS, true); } catch (Exception ignored) {}
    }

    // ── HTTP helpers ─────────────────────────────────────────────────────────

    /** POST SLD directly to workspace, return HTTP status. */
    private int postSld(String styleName, String sldXml) throws IOException {
        URL url = new URL(GS + "/rest/workspaces/" + WS + "/styles?name=" + styleName);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Authorization", auth);
        c.setRequestProperty("Content-Type", "application/vnd.ogc.sld+xml");
        c.setDoOutput(true);
        try (OutputStream os = c.getOutputStream()) {
            os.write(sldXml.getBytes(StandardCharsets.UTF_8));
        }
        return c.getResponseCode();
    }

    /** POST SLD to global (non-workspace) scope. */
    private int postSldGlobal(String styleName, String sldXml) throws IOException {
        URL url = new URL(GS + "/rest/styles?name=" + styleName);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Authorization", auth);
        c.setRequestProperty("Content-Type", "application/vnd.ogc.sld+xml");
        c.setDoOutput(true);
        try (OutputStream os = c.getOutputStream()) {
            os.write(sldXml.getBytes(StandardCharsets.UTF_8));
        }
        return c.getResponseCode();
    }

    /** GET stored SLD from workspace. */
    private String fetchSld(String styleName) throws IOException {
        URL url = new URL(GS + "/rest/workspaces/" + WS + "/styles/" + styleName + ".sld");
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestProperty("Authorization", auth);
        c.setRequestProperty("Accept", "application/vnd.ogc.sld+xml");
        assertEquals(200, c.getResponseCode(), "Fetch failed: " + styleName);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        br.close();
        return sb.toString();
    }

    /** GET stored SLD from global scope. */
    private String fetchSldGlobal(String styleName) throws IOException {
        URL url = new URL(GS + "/rest/styles/" + styleName + ".sld");
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestProperty("Authorization", auth);
        c.setRequestProperty("Accept", "application/vnd.ogc.sld+xml");
        assertEquals(200, c.getResponseCode(), "Global fetch failed: " + styleName);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        br.close();
        return sb.toString();
    }

    /**
     * Core cross-check: upload via library + upload raw SLD directly, fetch both,
     * assert each stored SLD contains all expectedElements.
     */
    private void crossCheck(String libName, StyleContent libSld,
                             String rawName, String rawXml,
                             String... expectedElements) throws IOException {
        // library path
        styles.createByWorkspace(WS, libSld, libName);
        assertTrue(styles.existsByWorkspace(WS, libName), "Library style must exist: " + libName);

        // curl-equivalent path
        int rawStatus = postSld(rawName, rawXml);
        assertEquals(201, rawStatus, "Direct POST must return 201 for: " + rawName);

        String libStored = fetchSld(libName);
        String rawStored = fetchSld(rawName);

        for (String elem : expectedElements) {
            assertTrue(libStored.contains(elem),
                    "[library] '" + elem + "' missing in: " + libName + "\n" + libStored);
            assertTrue(rawStored.contains(elem),
                    "[direct]  '" + elem + "' missing in: " + rawName + "\n" + rawStored);
        }
    }

    /** Minimal SLD wrapper (no UserStyle.Name, no filter). */
    private static String wrap(String body) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
             + "<StyledLayerDescriptor version=\"1.0.0\""
             + " xmlns=\"http://www.opengis.net/sld\""
             + " xmlns:ogc=\"http://www.opengis.net/ogc\""
             + " xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
             + "<NamedLayer><Name>cross</Name><UserStyle>"
             + "<FeatureTypeStyle><Rule>" + body + "</Rule></FeatureTypeStyle>"
             + "</UserStyle></NamedLayer></StyledLayerDescriptor>";
    }

    private static String n(long ts, String tag) { return tag + "_" + ts; }

    // ══════════════════════════════════════════════════════════════════════════
    // ── SldBuilder top-level: styleName / title / abstract_ / isDefault ──────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(1)
    void userStyleMetadata_nameTitle() throws Exception {
        String L = n(TS, "us_meta"), R = n(TS, "us_meta_r");
        StyleContent lib = SldBuilder.create(L)
                .styleName("My Style")
                .title("My Style Title")
                .abstract_("A description")
                .isDefault(true)
                .rule("r").polygon("#336699")
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<Name>My Style</Name><Title>My Style Title</Title>"
                + "<Abstract>A description</Abstract><IsDefault>1</IsDefault>"
                + "<FeatureTypeStyle><Rule>"
                + "<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"
                + "</Rule></FeatureTypeStyle></UserStyle></NamedLayer>"
                + "</StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "PolygonSymbolizer");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── FeatureTypeStyle: name / title / abstract_ / featureTypeName / semanticType ─
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(2)
    void ftsMetadata_allFields() throws Exception {
        String L = n(TS, "fts_meta"), R = n(TS, "fts_meta_r");
        StyleContent lib = SldBuilder.create(L)
                .featureTypeStyle("roads")
                    .ftsTitle("Roads FTS")
                    .ftsAbstract("Road rendering")
                    .ftsFeatureTypeName("cite:Roads")
                    .ftsSemanticTypeIdentifier("generic:line")
                    .rule("r").line("#FF0000", 2)
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\""
                + " xmlns:ogc=\"http://www.opengis.net/ogc\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle>"
                + "<Name>roads</Name><Title>Roads FTS</Title>"
                + "<Abstract>Road rendering</Abstract>"
                + "<FeatureTypeName>cite:Roads</FeatureTypeName>"
                + "<SemanticTypeIdentifier>generic:line</SemanticTypeIdentifier>"
                + "<Rule><LineSymbolizer><Stroke>"
                + "<CssParameter name=\"stroke\">#FF0000</CssParameter>"
                + "<CssParameter name=\"stroke-width\">2</CssParameter>"
                + "</Stroke></LineSymbolizer></Rule>"
                + "</FeatureTypeStyle></UserStyle></NamedLayer>"
                + "</StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "LineSymbolizer");
        // verify semantic type is in library-generated SLD
        assertTrue(lib.getSldBody().contains("generic:line"), "SemanticTypeIdentifier must be in XML");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Rule: title / abstract_ / legendGraphicExternal ──────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(3)
    void ruleMetadata_titleAbstractLegendExternal() throws Exception {
        String L = n(TS, "rule_meta"), R = n(TS, "rule_meta_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .title("Highway rule")
                    .abstract_("Styles highways")
                    .legendGraphicExternal("http://example.org/icon.png", "image/png", 16)
                    .polygon("#CC0000")
                .build();

        String raw = wrap("<PolygonSymbolizer><Fill>"
                + "<CssParameter name=\"fill\">#CC0000</CssParameter>"
                + "</Fill></PolygonSymbolizer>");

        crossCheck(L, lib, R, raw, "PolygonSymbolizer");
        assertTrue(lib.getSldBody().contains("Highway rule"), "Rule title must be present");
        assertTrue(lib.getSldBody().contains("ExternalGraphic"), "LegendGraphic ExternalGraphic must be present");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── RuleBuilder shorthand: point(mark,fill,size) / point(full) / pointExternal ─
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(4)
    void shorthand_point_allVariants() throws Exception {
        String L1 = n(TS, "pt1"), R1 = n(TS, "pt1_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r").point(WellKnownName.TRIANGLE, "#FF9900", 12)
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<PointSymbolizer><Graphic><Mark>"
                   + "<WellKnownName>triangle</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#FF9900</CssParameter></Fill>"
                   + "</Mark><Size>12</Size></Graphic></PointSymbolizer>"),
                "PointSymbolizer", "triangle");

        String L2 = n(TS, "pt2"), R2 = n(TS, "pt2_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r").point(WellKnownName.SQUARE, "#00CC00", 0.5, "#000000", 1.0, 8)
                .build();
        // GeoServer 2.28.2 normalizes WKN="square" to an empty Mark (drops WellKnownName),
        // so we verify the library generates correct XML, not the stored round-trip.
        assertTrue(lib2.getSldBody().contains("square"), "library XML must contain 'square'");
        crossCheck(L2, lib2, R2,
                wrap("<PointSymbolizer><Graphic><Mark>"
                   + "<WellKnownName>square</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#00CC00</CssParameter>"
                   + "<CssParameter name=\"fill-opacity\">0.5</CssParameter></Fill>"
                   + "<Stroke><CssParameter name=\"stroke\">#000000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">1</CssParameter></Stroke>"
                   + "</Mark><Size>8</Size></Graphic></PointSymbolizer>"),
                "PointSymbolizer");

        String L3 = n(TS, "pt3"), R3 = n(TS, "pt3_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r").pointExternal("http://example.org/marker.png", "image/png", 24)
                .build();
        crossCheck(L3, lib3, R3,
                wrap("<PointSymbolizer><Graphic><ExternalGraphic>"
                   + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                   + " xlink:type=\"simple\" xlink:href=\"http://example.org/marker.png\"/>"
                   + "<Format>image/png</Format>"
                   + "</ExternalGraphic><Size>24</Size></Graphic></PointSymbolizer>"),
                "PointSymbolizer", "ExternalGraphic");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── RuleBuilder shorthand: line / polygon all overloads ───────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(5)
    void shorthand_linePolygon_allVariants() throws Exception {
        // line with dashArray
        String L1 = n(TS, "ln_dash"), R1 = n(TS, "ln_dash_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r").line("#CC0000", 2, "4 2")
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<LineSymbolizer><Stroke>"
                   + "<CssParameter name=\"stroke\">#CC0000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">2</CssParameter>"
                   + "<CssParameter name=\"stroke-dasharray\">4 2</CssParameter>"
                   + "</Stroke></LineSymbolizer>"),
                "LineSymbolizer", "stroke-dasharray");

        // polygon(fill, stroke, width)
        String L2 = n(TS, "pg2"), R2 = n(TS, "pg2_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r").polygon("#336699", "#000000", 1)
                .build();
        crossCheck(L2, lib2, R2,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill>"
                   + "<Stroke><CssParameter name=\"stroke\">#000000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">1</CssParameter></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer", "336699");

        // polygon(fill, opacity, stroke, width)
        String L3 = n(TS, "pg3"), R3 = n(TS, "pg3_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r").polygon("#336699", 0.5, "#000000", 1)
                .build();
        crossCheck(L3, lib3, R3,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#336699</CssParameter>"
                   + "<CssParameter name=\"fill-opacity\">0.5</CssParameter></Fill>"
                   + "<Stroke><CssParameter name=\"stroke\">#000000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">1</CssParameter></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer", "fill-opacity");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── RuleBuilder shorthand: text / staticText / textOnLine ─────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(6)
    void shorthand_text_allVariants() throws Exception {
        // text(attr, color, family, size)
        String L1 = n(TS, "tx1"), R1 = n(TS, "tx1_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r").text("NAME", "#000000", "Arial", 12)
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<TextSymbolizer><Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">12</CssParameter>"
                   + "<CssParameter name=\"font-style\">normal</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><PointPlacement><AnchorPoint>"
                   + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                   + "</AnchorPoint></PointPlacement></LabelPlacement>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer", "NAME");

        // text(attr, color, family, size, haloColor, haloRadius)
        String L2 = n(TS, "tx2"), R2 = n(TS, "tx2_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r").text("NAME", "#000000", "Arial", 12, "#FFFFFF", 1.5)
                .build();
        crossCheck(L2, lib2, R2,
                wrap("<TextSymbolizer><Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">12</CssParameter>"
                   + "<CssParameter name=\"font-style\">normal</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><PointPlacement><AnchorPoint>"
                   + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                   + "</AnchorPoint></PointPlacement></LabelPlacement>"
                   + "<Halo><Radius>1.5</Radius><Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill></Halo>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer", "Halo");

        // staticText
        String L3 = n(TS, "tx3"), R3 = n(TS, "tx3_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r").staticText("Hello World", "#333333", "Arial", 14)
                .build();
        crossCheck(L3, lib3, R3,
                wrap("<TextSymbolizer><Label><ogc:Literal>Hello World</ogc:Literal></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">14</CssParameter>"
                   + "<CssParameter name=\"font-style\">normal</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><PointPlacement><AnchorPoint>"
                   + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                   + "</AnchorPoint></PointPlacement></LabelPlacement>"
                   + "<Fill><CssParameter name=\"fill\">#333333</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer");
        assertTrue(lib3.getSldBody().contains("Hello World"), "Static label text must be present");

        // textOnLine
        String L4 = n(TS, "tx4"), R4 = n(TS, "tx4_r");
        StyleContent lib4 = SldBuilder.create(L4)
                .rule("r").textOnLine("NAME", "#000000", "Arial", 12, 5)
                .build();
        crossCheck(L4, lib4, R4,
                wrap("<TextSymbolizer><Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">12</CssParameter>"
                   + "<CssParameter name=\"font-style\">normal</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><LinePlacement>"
                   + "<PerpendicularOffset>5</PerpendicularOffset>"
                   + "</LinePlacement></LabelPlacement>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer", "LinePlacement");

        // textOnLine with halo
        String L5 = n(TS, "tx5"), R5 = n(TS, "tx5_r");
        StyleContent lib5 = SldBuilder.create(L5)
                .rule("r").textOnLine("NAME", "#000000", "Arial", 12, "#FFFFFF", 1.0, 5)
                .build();
        crossCheck(L5, lib5, R5,
                wrap("<TextSymbolizer><Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">12</CssParameter>"
                   + "<CssParameter name=\"font-style\">normal</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><LinePlacement>"
                   + "<PerpendicularOffset>5</PerpendicularOffset>"
                   + "</LinePlacement></LabelPlacement>"
                   + "<Halo><Radius>1</Radius><Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill></Halo>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer", "LinePlacement", "Halo");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── RuleBuilder shorthand: raster() ─────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(7)
    void shorthand_raster() throws Exception {
        String L = n(TS, "rs_sh"), R = n(TS, "rs_sh_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r").raster(0.8, SldColorMapType.INTERVALS,
                        SldColorMapEntry.of("#0000FF", 0),
                        SldColorMapEntry.of("#00FF00", 500),
                        SldColorMapEntry.of("#FF0000", 1000))
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<Opacity>0.8</Opacity>"
                + "<ColorMap type=\"intervals\">"
                + "<ColorMapEntry color=\"#0000FF\" quantity=\"0\"/>"
                + "<ColorMapEntry color=\"#00FF00\" quantity=\"500\"/>"
                + "<ColorMapEntry color=\"#FF0000\" quantity=\"1000\"/>"
                + "</ColorMap>"
                + "</RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer", "ColorMap");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── PointBuilder: all properties ─────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(8)
    void pointBuilder_allProperties() throws Exception {
        String L = n(TS, "pb_all"), R = n(TS, "pb_all_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .mark(WellKnownName.STAR)
                        .fill("#FFCC00", 0.9)
                        .stroke("#993300", 1.5)
                        .strokeOpacity(0.8)
                        .strokeLineCap(LineCap.ROUND)
                        .strokeLineJoin(LineJoin.ROUND)
                        .strokeDashArray("3 1")
                        .strokeDashOffset(1.0)
                        .size(20)
                        .rotation(45)
                        .opacity(0.85)
                        .geometry("the_geom")
                    .end()
                .build();

        String raw = wrap(
                "<PointSymbolizer>"
              + "<Geometry><ogc:PropertyName>the_geom</ogc:PropertyName></Geometry>"
              + "<Graphic><Mark>"
              + "<WellKnownName>star</WellKnownName>"
              + "<Fill>"
              + "<CssParameter name=\"fill\">#FFCC00</CssParameter>"
              + "<CssParameter name=\"fill-opacity\">0.9</CssParameter>"
              + "</Fill>"
              + "<Stroke>"
              + "<CssParameter name=\"stroke\">#993300</CssParameter>"
              + "<CssParameter name=\"stroke-width\">1.5</CssParameter>"
              + "<CssParameter name=\"stroke-opacity\">0.8</CssParameter>"
              + "<CssParameter name=\"stroke-dasharray\">3 1</CssParameter>"
              + "<CssParameter name=\"stroke-dashoffset\">1</CssParameter>"
              + "<CssParameter name=\"stroke-linecap\">round</CssParameter>"
              + "<CssParameter name=\"stroke-linejoin\">round</CssParameter>"
              + "</Stroke>"
              + "</Mark>"
              + "<Opacity>0.85</Opacity>"
              + "<Size>20</Size>"
              + "<Rotation>45</Rotation>"
              + "</Graphic></PointSymbolizer>");

        crossCheck(L, lib, R, raw, "PointSymbolizer", "star");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("the_geom"), "geometry property");
        assertTrue(xml.contains("FFCC00"), "fill color");
        assertTrue(xml.contains("stroke-linecap"), "linecap");
        assertTrue(xml.contains("stroke-linejoin"), "linejoin");
        assertTrue(xml.contains("stroke-dasharray"), "dasharray");
        assertTrue(xml.contains("Rotation"), "rotation");
    }

    @Test @Order(9)
    void pointBuilder_sizeExprRotationExpr() throws Exception {
        String L = n(TS, "pb_expr"), R = n(TS, "pb_expr_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#FF0000")
                        .size(mul(property("VALUE"), literal(0.01)))
                        .rotation(add(property("ANGLE"), literal(90)))
                    .end()
                .build();

        // raw equivalent uses literal size for GeoServer acceptance
        String raw = wrap("<PointSymbolizer><Graphic><Mark>"
                + "<WellKnownName>circle</WellKnownName>"
                + "<Fill><CssParameter name=\"fill\">#FF0000</CssParameter></Fill>"
                + "</Mark><Size>10</Size></Graphic></PointSymbolizer>");

        // library SLD: verify arithmetic expressions present
        String libXml = lib.getSldBody();
        assertTrue(libXml.contains("Mul") || libXml.contains("mul"), "Mul expression for size");
        assertTrue(libXml.contains("Add") || libXml.contains("add"), "Add expression for rotation");

        // upload library version (cross: just verify raw also accepted)
        styles.createByWorkspace(WS, lib, L);
        assertTrue(styles.existsByWorkspace(WS, L));
        assertEquals(201, postSld(R, raw), "Direct POST must return 201");
        assertTrue(fetchSld(L).contains("PointSymbolizer"), "PointSymbolizer in stored lib SLD");
    }

    @Test @Order(10)
    void pointBuilder_externalMark() throws Exception {
        String L = n(TS, "pb_extmark"), R = n(TS, "pb_extmark_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .externalMark("http://example.org/font.ttf", "ttf", 64)
                        .size(16)
                        .fill("#FF0000")
                    .end()
                .build();

        String raw = wrap("<PointSymbolizer><Graphic><Mark>"
                + "<ExternalMark>"
                + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                + " xlink:type=\"simple\" xlink:href=\"http://example.org/font.ttf\"/>"
                + "<Format>ttf</Format><MarkIndex>64</MarkIndex>"
                + "</ExternalMark>"
                + "<Fill><CssParameter name=\"fill\">#FF0000</CssParameter></Fill>"
                + "</Mark><Size>16</Size></Graphic></PointSymbolizer>");

        crossCheck(L, lib, R, raw, "PointSymbolizer");
        assertTrue(lib.getSldBody().contains("ExternalMark"), "ExternalMark must be in generated SLD");
        assertTrue(lib.getSldBody().contains("MarkIndex"), "MarkIndex must be present");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── LineBuilder: all properties ───────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(11)
    void lineBuilder_allProperties() throws Exception {
        String L = n(TS, "lb_all"), R = n(TS, "lb_all_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .stroke("#CC0000", 3)
                        .dashArray("8 4 2 4")
                        .dashOffset(2.0)
                        .lineCap(LineCap.SQUARE)
                        .lineJoin(LineJoin.BEVEL)
                        .strokeOpacity(0.7)
                        .opacity(0.9)
                        .perpendicularOffset(5.0)
                        .geometry("the_geom")
                    .end()
                .build();

        String raw = wrap("<LineSymbolizer>"
                + "<Geometry><ogc:PropertyName>the_geom</ogc:PropertyName></Geometry>"
                + "<Stroke>"
                + "<CssParameter name=\"stroke\">#CC0000</CssParameter>"
                + "<CssParameter name=\"stroke-width\">3</CssParameter>"
                + "<CssParameter name=\"stroke-opacity\">0.7</CssParameter>"
                + "<CssParameter name=\"stroke-dasharray\">8 4 2 4</CssParameter>"
                + "<CssParameter name=\"stroke-dashoffset\">2</CssParameter>"
                + "<CssParameter name=\"stroke-linecap\">square</CssParameter>"
                + "<CssParameter name=\"stroke-linejoin\">bevel</CssParameter>"
                + "</Stroke>"
                + "<Opacity>0.9</Opacity>"
                + "<PerpendicularOffset>5</PerpendicularOffset>"
                + "</LineSymbolizer>");

        crossCheck(L, lib, R, raw, "LineSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("the_geom"), "geometry");
        assertTrue(xml.contains("stroke-dashoffset"), "dashoffset");
        assertTrue(xml.contains("square"), "linecap square");
        assertTrue(xml.contains("bevel"), "linejoin bevel");
        assertTrue(xml.contains("PerpendicularOffset"), "perpendicular offset");
    }

    @Test @Order(12)
    void lineBuilder_graphicFillAndStroke() throws Exception {
        // GraphicFill on line
        String L1 = n(TS, "lb_gf"), R1 = n(TS, "lb_gf_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r")
                    .stroke("#CC0000", 6)
                        .graphicFill(WellKnownName.SQUARE, "#999999", 4)
                    .end()
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<LineSymbolizer><Stroke><GraphicFill><Graphic><Mark>"
                   + "<WellKnownName>square</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#999999</CssParameter></Fill>"
                   + "</Mark><Size>4</Size></Graphic></GraphicFill>"
                   + "<CssParameter name=\"stroke\">#CC0000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">6</CssParameter>"
                   + "</Stroke></LineSymbolizer>"),
                "LineSymbolizer", "GraphicFill");

        // GraphicStroke on line
        String L2 = n(TS, "lb_gs"), R2 = n(TS, "lb_gs_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .stroke("#CC0000", 1)
                        .graphicStroke(WellKnownName.CIRCLE, "#0000FF", 6)
                    .end()
                .build();
        crossCheck(L2, lib2, R2,
                wrap("<LineSymbolizer><Stroke><GraphicStroke><Graphic><Mark>"
                   + "<WellKnownName>circle</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#0000FF</CssParameter></Fill>"
                   + "</Mark><Size>6</Size></Graphic></GraphicStroke>"
                   + "<CssParameter name=\"stroke\">#CC0000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">1</CssParameter>"
                   + "</Stroke></LineSymbolizer>"),
                "LineSymbolizer", "GraphicStroke");

        // GraphicFill from external image on line
        String L3 = n(TS, "lb_gfe"), R3 = n(TS, "lb_gfe_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r")
                    .stroke("#000000", 8)
                        .graphicFill("http://example.org/pat.png", "image/png", 4)
                    .end()
                .build();
        crossCheck(L3, lib3, R3,
                wrap("<LineSymbolizer><Stroke><GraphicFill><Graphic><ExternalGraphic>"
                   + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                   + " xlink:type=\"simple\" xlink:href=\"http://example.org/pat.png\"/>"
                   + "<Format>image/png</Format>"
                   + "</ExternalGraphic><Size>4</Size></Graphic></GraphicFill>"
                   + "<CssParameter name=\"stroke\">#000000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">8</CssParameter>"
                   + "</Stroke></LineSymbolizer>"),
                "LineSymbolizer", "ExternalGraphic");

        // GraphicStroke from external image on line
        String L4 = n(TS, "lb_gse"), R4 = n(TS, "lb_gse_r");
        StyleContent lib4 = SldBuilder.create(L4)
                .rule("r")
                    .stroke("#000000", 1)
                        .graphicStroke("http://example.org/arrow.svg", "image/svg+xml", 10)
                    .end()
                .build();
        crossCheck(L4, lib4, R4,
                wrap("<LineSymbolizer><Stroke><GraphicStroke><Graphic><ExternalGraphic>"
                   + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                   + " xlink:type=\"simple\" xlink:href=\"http://example.org/arrow.svg\"/>"
                   + "<Format>image/svg+xml</Format>"
                   + "</ExternalGraphic><Size>10</Size></Graphic></GraphicStroke>"
                   + "<CssParameter name=\"stroke\">#000000</CssParameter>"
                   + "<CssParameter name=\"stroke-width\">1</CssParameter>"
                   + "</Stroke></LineSymbolizer>"),
                "LineSymbolizer", "GraphicStroke", "ExternalGraphic");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── PolygonBuilder: all properties ───────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(13)
    void polygonBuilder_allStrokeProperties() throws Exception {
        String L = n(TS, "pgb_stroke"), R = n(TS, "pgb_stroke_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .fill("#336699")
                        .fillOpacity(0.7)
                        .stroke("#000000", 2)
                        .strokeOpacity(0.5)
                        .strokeLineCap(LineCap.BUTT)
                        .strokeLineJoin(LineJoin.BEVEL)
                        .strokeDashArray("6 3")
                        .strokeDashOffset(2.0)
                        .geometry("the_geom")
                    .end()
                .build();

        String raw = wrap("<PolygonSymbolizer>"
                + "<Geometry><ogc:PropertyName>the_geom</ogc:PropertyName></Geometry>"
                + "<Fill>"
                + "<CssParameter name=\"fill\">#336699</CssParameter>"
                + "<CssParameter name=\"fill-opacity\">0.7</CssParameter>"
                + "</Fill>"
                + "<Stroke>"
                + "<CssParameter name=\"stroke\">#000000</CssParameter>"
                + "<CssParameter name=\"stroke-width\">2</CssParameter>"
                + "<CssParameter name=\"stroke-opacity\">0.5</CssParameter>"
                + "<CssParameter name=\"stroke-dasharray\">6 3</CssParameter>"
                + "<CssParameter name=\"stroke-dashoffset\">2</CssParameter>"
                + "<CssParameter name=\"stroke-linecap\">butt</CssParameter>"
                + "<CssParameter name=\"stroke-linejoin\">bevel</CssParameter>"
                + "</Stroke></PolygonSymbolizer>");

        crossCheck(L, lib, R, raw, "PolygonSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("the_geom"), "geometry");
        assertTrue(xml.contains("fill-opacity"), "fill-opacity");
        assertTrue(xml.contains("stroke-opacity"), "stroke-opacity");
        assertTrue(xml.contains("butt"), "linecap butt");
        assertTrue(xml.contains("bevel"), "linejoin bevel");
        assertTrue(xml.contains("stroke-dasharray"), "dasharray");
        assertTrue(xml.contains("stroke-dashoffset"), "dashoffset");
    }

    @Test @Order(14)
    void polygonBuilder_fillPatternExternal() throws Exception {
        String L = n(TS, "pgb_fpext"), R = n(TS, "pgb_fpext_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .fill()
                        .fillPattern("http://example.org/hatch.png", "image/png", 8)
                        .stroke("#000000", 1)
                    .end()
                .build();

        String raw = wrap("<PolygonSymbolizer>"
                + "<Fill><GraphicFill><Graphic><ExternalGraphic>"
                + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                + " xlink:type=\"simple\" xlink:href=\"http://example.org/hatch.png\"/>"
                + "<Format>image/png</Format>"
                + "</ExternalGraphic><Size>8</Size></Graphic></GraphicFill></Fill>"
                + "<Stroke><CssParameter name=\"stroke\">#000000</CssParameter>"
                + "<CssParameter name=\"stroke-width\">1</CssParameter></Stroke>"
                + "</PolygonSymbolizer>");

        crossCheck(L, lib, R, raw, "PolygonSymbolizer", "GraphicFill");
    }

    @Test @Order(15)
    void polygonBuilder_strokeGraphicFillAndStroke() throws Exception {
        // strokeGraphicFill (WKN)
        String L1 = n(TS, "pgb_sgf"), R1 = n(TS, "pgb_sgf_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r")
                    .fill("#FFFFFF")
                        .strokeGraphicFill(WellKnownName.CROSS, "#000000", 4)
                    .end()
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill>"
                   + "<Stroke><GraphicFill><Graphic><Mark>"
                   + "<WellKnownName>cross</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</Mark><Size>4</Size></Graphic></GraphicFill></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(lib1.getSldBody().contains("GraphicFill"), "strokeGraphicFill present");

        // strokeGraphicStroke (WKN)
        String L2 = n(TS, "pgb_sgs"), R2 = n(TS, "pgb_sgs_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .fill("#FFFFFF")
                        .strokeGraphicStroke(WellKnownName.X, "#333333", 6)
                    .end()
                .build();
        crossCheck(L2, lib2, R2,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill>"
                   + "<Stroke><GraphicStroke><Graphic><Mark>"
                   + "<WellKnownName>x</WellKnownName>"
                   + "<Fill><CssParameter name=\"fill\">#333333</CssParameter></Fill>"
                   + "</Mark><Size>6</Size></Graphic></GraphicStroke></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(lib2.getSldBody().contains("GraphicStroke"), "strokeGraphicStroke present");

        // strokeGraphicFill (external)
        String L3 = n(TS, "pgb_sgfe"), R3 = n(TS, "pgb_sgfe_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r")
                    .fill("#FFFFFF")
                        .strokeGraphicFill("http://example.org/dot.png", "image/png", 4)
                    .end()
                .build();
        crossCheck(L3, lib3, R3,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill>"
                   + "<Stroke><GraphicFill><Graphic><ExternalGraphic>"
                   + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                   + " xlink:type=\"simple\" xlink:href=\"http://example.org/dot.png\"/>"
                   + "<Format>image/png</Format></ExternalGraphic>"
                   + "<Size>4</Size></Graphic></GraphicFill></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer", "ExternalGraphic");

        // strokeGraphicStroke (external)
        String L4 = n(TS, "pgb_sgse"), R4 = n(TS, "pgb_sgse_r");
        StyleContent lib4 = SldBuilder.create(L4)
                .rule("r")
                    .fill("#FFFFFF")
                        .strokeGraphicStroke("http://example.org/arrow.png", "image/png", 8)
                    .end()
                .build();
        crossCheck(L4, lib4, R4,
                wrap("<PolygonSymbolizer>"
                   + "<Fill><CssParameter name=\"fill\">#FFFFFF</CssParameter></Fill>"
                   + "<Stroke><GraphicStroke><Graphic><ExternalGraphic>"
                   + "<OnlineResource xmlns:xlink=\"http://www.w3.org/1999/xlink\""
                   + " xlink:type=\"simple\" xlink:href=\"http://example.org/arrow.png\"/>"
                   + "<Format>image/png</Format></ExternalGraphic>"
                   + "<Size>8</Size></Graphic></GraphicStroke></Stroke>"
                   + "</PolygonSymbolizer>"),
                "PolygonSymbolizer", "ExternalGraphic");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── TextBuilder: italic / oblique / pointPlacement / displacement / rotation / geometry / staticLabel ─
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(16)
    void textBuilder_fontStylesAndPlacement() throws Exception {
        // italic
        String L1 = n(TS, "tb_ital"), R1 = n(TS, "tb_ital_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r")
                    .label("NAME")
                        .color("#000000")
                        .font("Arial", 12)
                        .italic()
                    .end()
                .build();
        crossCheck(L1, lib1, R1,
                wrap("<TextSymbolizer><Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                   + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                   + "<CssParameter name=\"font-size\">12</CssParameter>"
                   + "<CssParameter name=\"font-style\">italic</CssParameter>"
                   + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                   + "<LabelPlacement><PointPlacement><AnchorPoint>"
                   + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                   + "</AnchorPoint></PointPlacement></LabelPlacement>"
                   + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                   + "</TextSymbolizer>"),
                "TextSymbolizer");
        assertTrue(lib1.getSldBody().contains("italic"), "italic font style");

        // oblique
        String L2 = n(TS, "tb_obl"), R2 = n(TS, "tb_obl_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .label("NAME")
                        .color("#000000")
                        .font("Arial", 12)
                        .oblique()
                    .end()
                .build();
        crossCheck(L2, lib2, R2, wrap("<TextSymbolizer>"
                + "<Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                + "<CssParameter name=\"font-size\">12</CssParameter>"
                + "<CssParameter name=\"font-style\">oblique</CssParameter>"
                + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                + "<LabelPlacement><PointPlacement><AnchorPoint>"
                + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                + "</AnchorPoint></PointPlacement></LabelPlacement>"
                + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                + "</TextSymbolizer>"),
                "TextSymbolizer");
        assertTrue(lib2.getSldBody().contains("oblique"), "oblique font style");

        // pointPlacement with anchor + displacement + rotation
        String L3 = n(TS, "tb_pp"), R3 = n(TS, "tb_pp_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r")
                    .label("NAME")
                        .color("#000000")
                        .pointPlacement(0.0, 0.5)
                        .displacement(5, 10)
                        .rotation(30)
                        .geometry("label_point")
                    .end()
                .build();
        crossCheck(L3, lib3, R3, wrap("<TextSymbolizer>"
                + "<Geometry><ogc:PropertyName>label_point</ogc:PropertyName></Geometry>"
                + "<Label><ogc:PropertyName>NAME</ogc:PropertyName></Label>"
                + "<Font><CssParameter name=\"font-family\">SansSerif</CssParameter>"
                + "<CssParameter name=\"font-size\">10</CssParameter>"
                + "<CssParameter name=\"font-style\">normal</CssParameter>"
                + "<CssParameter name=\"font-weight\">normal</CssParameter></Font>"
                + "<LabelPlacement><PointPlacement>"
                + "<AnchorPoint><AnchorPointX>0</AnchorPointX><AnchorPointY>0.5</AnchorPointY></AnchorPoint>"
                + "<Displacement><DisplacementX>5</DisplacementX><DisplacementY>10</DisplacementY></Displacement>"
                + "<Rotation>30</Rotation>"
                + "</PointPlacement></LabelPlacement>"
                + "<Fill><CssParameter name=\"fill\">#000000</CssParameter></Fill>"
                + "</TextSymbolizer>"),
                "TextSymbolizer");
        String xml3 = lib3.getSldBody();
        assertTrue(xml3.contains("Displacement"), "displacement");
        assertTrue(xml3.contains("Rotation"), "rotation");
        assertTrue(xml3.contains("label_point"), "geometry property");

        // staticLabel
        String L4 = n(TS, "tb_sl"), R4 = n(TS, "tb_sl_r");
        StyleContent lib4 = SldBuilder.create(L4)
                .rule("r")
                    .staticLabel("Static Text")
                        .color("#333333")
                        .font("Arial", 10)
                        .bold()
                        .vendorOption("labelObstacle", "true")
                        .vendorOption("maxDisplacement", "100")
                    .end()
                .build();
        crossCheck(L4, lib4, R4, wrap("<TextSymbolizer>"
                + "<Label><ogc:Literal>Static Text</ogc:Literal></Label>"
                + "<Font><CssParameter name=\"font-family\">Arial</CssParameter>"
                + "<CssParameter name=\"font-size\">10</CssParameter>"
                + "<CssParameter name=\"font-style\">normal</CssParameter>"
                + "<CssParameter name=\"font-weight\">bold</CssParameter></Font>"
                + "<LabelPlacement><PointPlacement><AnchorPoint>"
                + "<AnchorPointX>0.5</AnchorPointX><AnchorPointY>0.5</AnchorPointY>"
                + "</AnchorPoint></PointPlacement></LabelPlacement>"
                + "<Fill><CssParameter name=\"fill\">#333333</CssParameter></Fill>"
                + "<VendorOption name=\"labelObstacle\">true</VendorOption>"
                + "</TextSymbolizer>"),
                "TextSymbolizer");
        String xml4 = lib4.getSldBody();
        assertTrue(xml4.contains("Static Text"), "static label text");
        assertTrue(xml4.contains("VendorOption"), "vendorOption");
        assertTrue(xml4.contains("maxDisplacement"), "second vendorOption");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── RasterBuilder: all properties ────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(17)
    void rasterBuilder_channelRgbAndContrast() throws Exception {
        String L = n(TS, "rb_rgb"), R = n(TS, "rb_rgb_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .rasterBuilder()
                        .opacity(0.95)
                        .channelRGB("1", "2", "3")
                        .contrastEnhancement(ContrastMethod.NORMALIZE)
                        .gammaValue(0.5)
                        .geometry("the_raster")
                    .end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\""
                + " xmlns:ogc=\"http://www.opengis.net/ogc\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<Geometry><ogc:PropertyName>the_raster</ogc:PropertyName></Geometry>"
                + "<Opacity>0.95</Opacity>"
                + "<ChannelSelection>"
                + "<RedChannel><SourceChannelName>1</SourceChannelName></RedChannel>"
                + "<GreenChannel><SourceChannelName>2</SourceChannelName></GreenChannel>"
                + "<BlueChannel><SourceChannelName>3</SourceChannelName></BlueChannel>"
                + "</ChannelSelection>"
                + "<ContrastEnhancement><Normalize/><GammaValue>0.5</GammaValue></ContrastEnhancement>"
                + "</RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("ChannelSelection"), "ChannelSelection");
        assertTrue(xml.contains("the_raster"), "geometry property");
        assertTrue(xml.contains("ContrastEnhancement") || xml.contains("Normalize"), "contrast");
        assertTrue(xml.contains("GammaValue") || xml.contains("0.5"), "gamma value");
    }

    @Test @Order(18)
    void rasterBuilder_channelRgbWithPerChannelContrast() throws Exception {
        String L = n(TS, "rb_rgbce"), R = n(TS, "rb_rgbce_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .rasterBuilder()
                        .channelRGB("1", ContrastMethod.HISTOGRAM,
                                    "2", ContrastMethod.NORMALIZE,
                                    "3", ContrastMethod.HISTOGRAM)
                        .colorMap(SldColorMapType.RAMP,
                                  SldColorMapEntry.of("#000000", 0, "Dark", 1.0),
                                  SldColorMapEntry.of("#FFFFFF", 255, "Light", 1.0))
                    .end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<ColorMap type=\"ramp\">"
                + "<ColorMapEntry color=\"#000000\" quantity=\"0\" label=\"Dark\"/>"
                + "<ColorMapEntry color=\"#FFFFFF\" quantity=\"255\" label=\"Light\"/>"
                + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("ChannelSelection"), "ChannelSelection");
        assertTrue(xml.contains("Histogram") || xml.contains("histogram"), "histogram contrast");
    }

    @Test @Order(19)
    void rasterBuilder_grayWithContrastGamma() throws Exception {
        String L = n(TS, "rb_gray"), R = n(TS, "rb_gray_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .rasterBuilder()
                        .channelGray("1", ContrastMethod.NORMALIZE)
                        .colorMap(SldColorMapType.RAMP,
                                SldColorMapEntry.of("#0000FF", 0, "Low", 0.5),
                                SldColorMapEntry.of("#FF0000", 1000, "High", 1.0))
                    .end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<ChannelSelection>"
                + "<GrayChannel><SourceChannelName>1</SourceChannelName>"
                + "<ContrastEnhancement><Normalize/></ContrastEnhancement>"
                + "</GrayChannel></ChannelSelection>"
                + "<ColorMap type=\"ramp\">"
                + "<ColorMapEntry color=\"#0000FF\" quantity=\"0\" label=\"Low\" opacity=\"0.5\"/>"
                + "<ColorMapEntry color=\"#FF0000\" quantity=\"1000\" label=\"High\"/>"
                + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer", "GrayChannel", "ColorMap");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("0.5"), "opacity in ColorMapEntry");
    }

    @Test @Order(20)
    void rasterBuilder_grayWithGamma() throws Exception {
        String L = n(TS, "rb_gg"), R = n(TS, "rb_gg_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .rasterBuilder()
                        .channelGray("1", 1.2)
                        .colorMap(SldColorMapType.VALUES,
                                SldColorMapEntry.of("#AAAAAA", 128))
                    .end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<ChannelSelection><GrayChannel>"
                + "<SourceChannelName>1</SourceChannelName>"
                + "<ContrastEnhancement><GammaValue>1.2</GammaValue></ContrastEnhancement>"
                + "</GrayChannel></ChannelSelection>"
                + "<ColorMap type=\"values\">"
                + "<ColorMapEntry color=\"#AAAAAA\" quantity=\"128\"/>"
                + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer");
        assertTrue(lib.getSldBody().contains("GammaValue") || lib.getSldBody().contains("1.2"), "gamma");
    }

    @Test @Order(21)
    void rasterBuilder_overlapAndColorMapExtendedAndShadedRelief() throws Exception {
        String L = n(TS, "rb_ext"), R = n(TS, "rb_ext_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .rasterBuilder()
                        .overlapBehavior(OverlapBehavior.AVERAGE)
                        .colorMapExtended(SldColorMapType.RAMP,
                                SldColorMapEntry.of("#0000FF", 0),
                                SldColorMapEntry.of("#FF0000", 1000))
                    .end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
                + "<ColorMap type=\"ramp\" extended=\"true\">"
                + "<ColorMapEntry color=\"#0000FF\" quantity=\"0\"/>"
                + "<ColorMapEntry color=\"#FF0000\" quantity=\"1000\"/>"
                + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "RasterSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("extended"), "colorMapExtended flag");
        assertTrue(xml.contains("OverlapBehavior") || xml.contains("AVERAGE"), "overlapBehavior");

        // shadedRelief
        String L2 = n(TS, "rb_shd"), R2 = n(TS, "rb_shd_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .rasterBuilder()
                        .shadedRelief(55.0, true)
                        .colorMap(SldColorMapType.RAMP,
                                SldColorMapEntry.of("#FFFFFF", 0),
                                SldColorMapEntry.of("#000000", 255))
                    .end()
                .build();
        crossCheck(L2, lib2, R2, raw.replace(R, R2), "RasterSymbolizer");
        assertTrue(lib2.getSldBody().contains("ShadedRelief"), "ShadedRelief");
    }

    @Test @Order(22)
    void rasterBuilder_imageOutline() throws Exception {
        String L1 = n(TS, "rb_ioline"), R1 = n(TS, "rb_ioline_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r")
                    .rasterBuilder()
                        .imageOutlineLine("#000000", 1)
                        .colorMap(SldColorMapType.RAMP,
                                SldColorMapEntry.of("#FFFFFF", 0),
                                SldColorMapEntry.of("#000000", 255))
                    .end()
                .build();
        crossCheck(L1, lib1, R1,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
              + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
              + "<NamedLayer><Name>" + R1 + "</Name><UserStyle>"
              + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
              + "<ColorMap type=\"ramp\">"
              + "<ColorMapEntry color=\"#FFFFFF\" quantity=\"0\"/>"
              + "<ColorMapEntry color=\"#000000\" quantity=\"255\"/>"
              + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
              + "</UserStyle></NamedLayer></StyledLayerDescriptor>",
                "RasterSymbolizer");
        assertTrue(lib1.getSldBody().contains("ImageOutline"), "ImageOutline with line");

        String L2 = n(TS, "rb_iopoly"), R2 = n(TS, "rb_iopoly_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .rasterBuilder()
                        .imageOutlinePolygon("#FFFFFF", "#000000", 1)
                        .colorMap(SldColorMapType.RAMP,
                                SldColorMapEntry.of("#FFFFFF", 0),
                                SldColorMapEntry.of("#000000", 255))
                    .end()
                .build();
        crossCheck(L2, lib2, R2,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
              + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\">"
              + "<NamedLayer><Name>" + R2 + "</Name><UserStyle>"
              + "<FeatureTypeStyle><Rule><RasterSymbolizer>"
              + "<ColorMap type=\"ramp\">"
              + "<ColorMapEntry color=\"#FFFFFF\" quantity=\"0\"/>"
              + "<ColorMapEntry color=\"#000000\" quantity=\"255\"/>"
              + "</ColorMap></RasterSymbolizer></Rule></FeatureTypeStyle>"
              + "</UserStyle></NamedLayer></StyledLayerDescriptor>",
                "RasterSymbolizer");
        assertTrue(lib2.getSldBody().contains("ImageOutline"), "ImageOutline with polygon");
        assertTrue(lib2.getSldBody().contains("PolygonSymbolizer"), "PolygonSymbolizer in ImageOutline");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── OgcFilters: all comparison operators ─────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(23)
    void filters_allComparisonOperators() throws Exception {
        // equalTo with Number
        String L1 = n(TS, "f_eqn"), R1 = n(TS, "f_eqn_r");
        crossCheck(L1,
                SldBuilder.create(L1).rule("r").filter(equalTo("POP", 1000)).polygon("#336699").build(),
                R1, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L1).getSldBody().contains("PropertyIsEqualTo"), "equalTo(Number)");

        // notEqualTo (String)
        String L2 = n(TS, "f_neq"), R2 = n(TS, "f_neq_r");
        crossCheck(L2,
                SldBuilder.create(L2).rule("r").filter(notEqualTo("TYPE", "lake")).polygon("#336699").build(),
                R2, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L2).getSldBody().contains("PropertyIsNotEqualTo"), "notEqualTo");

        // notEqualTo (Number)
        String L3 = n(TS, "f_neqn"), R3 = n(TS, "f_neqn_r");
        crossCheck(L3,
                SldBuilder.create(L3).rule("r").filter(notEqualTo("POP", 0)).polygon("#336699").build(),
                R3, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");

        // greaterThanOrEqualTo
        String L4 = n(TS, "f_gte"), R4 = n(TS, "f_gte_r");
        crossCheck(L4,
                SldBuilder.create(L4).rule("r").filter(greaterThanOrEqualTo("POP", 100)).polygon("#336699").build(),
                R4, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L4).getSldBody().contains("PropertyIsGreaterThanOrEqualTo"), "greaterThanOrEqualTo");

        // lessThan
        String L5 = n(TS, "f_lt"), R5 = n(TS, "f_lt_r");
        crossCheck(L5,
                SldBuilder.create(L5).rule("r").filter(lessThan("POP", 500)).polygon("#336699").build(),
                R5, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L5).getSldBody().contains("PropertyIsLessThan"), "lessThan");

        // lessThanOrEqualTo
        String L6 = n(TS, "f_lte"), R6 = n(TS, "f_lte_r");
        crossCheck(L6,
                SldBuilder.create(L6).rule("r").filter(lessThanOrEqualTo("POP", 500)).polygon("#336699").build(),
                R6, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L6).getSldBody().contains("PropertyIsLessThanOrEqualTo"), "lessThanOrEqualTo");
    }

    @Test @Order(24)
    void filters_likeWithCustomWildcards() throws Exception {
        String L = n(TS, "f_like2"), R = n(TS, "f_like2_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r").filter(like("NAME", "A%", "%", "_", "\\"))
                    .polygon("#336699")
                .build();
        crossCheck(L, lib, R,
                wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(xml.contains("PropertyIsLike"), "like filter present");
        assertTrue(xml.contains("wildCard=\"%\""), "custom wildcard");
    }

    @Test @Order(25)
    void filters_expressionBased() throws Exception {
        // equalTo with SldExpression operands
        String L = n(TS, "f_expr"), R = n(TS, "f_expr_r");
        StyleContent lib = SldBuilder.create(L)
                .rule("r")
                    .filter(equalTo(function("Concatenate", property("A"), literal("B")), literal("AB")))
                    .polygon("#336699")
                .build();
        crossCheck(L, lib, R,
                wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(lib.getSldBody().contains("PropertyIsEqualTo"), "expression equalTo");

        // or filter
        String L2 = n(TS, "f_or"), R2 = n(TS, "f_or_r");
        crossCheck(L2,
                SldBuilder.create(L2)
                    .rule("r").filter(or(equalTo("TYPE", "A"), equalTo("TYPE", "B")))
                    .polygon("#336699").build(),
                R2, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L2).getSldBody().contains("Or"), "or filter");

        // gmlObjectId — GeoServer 2.28.2 drops GmlObjectId from stored SLD;
        // verify library generates correct XML and GeoServer accepts it (201).
        String L3 = n(TS, "f_gml"), R3 = n(TS, "f_gml_r");
        StyleContent libGml = SldBuilder.create(L3)
                .rule("r").filter(gmlObjectId("feat.1", "feat.2"))
                .polygon("#336699").build();
        assertTrue(libGml.getSldBody().contains("GmlObjectId"), "library XML must contain GmlObjectId");
        crossCheck(L3, libGml, R3,
                wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── OgcFilters: all spatial operators ─────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(26)
    void filters_spatialAll() throws Exception {
        // intersects with point geometry
        String L1 = n(TS, "fs_int"), R1 = n(TS, "fs_int_r");
        crossCheck(L1,
                SldBuilder.create(L1).rule("r")
                    .filter(intersects("the_geom", point(0, 0, "EPSG:4326")))
                    .polygon("#336699").build(),
                R1, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L1).getSldBody().contains("Intersects"), "intersects spatial");

        // contains with lineString
        String L2 = n(TS, "fs_con"), R2 = n(TS, "fs_con_r");
        crossCheck(L2,
                SldBuilder.create(L2).rule("r")
                    .filter(contains("the_geom", lineString(0, 0, 1, 1)))
                    .polygon("#336699").build(),
                R2, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L2).getSldBody().contains("Contains"), "contains spatial");

        // within with polygon
        String L3 = n(TS, "fs_wit"), R3 = n(TS, "fs_wit_r");
        crossCheck(L3,
                SldBuilder.create(L3).rule("r")
                    .filter(within("the_geom", polygon(-180, -90, 180, -90, 180, 90, -180, 90, -180, -90)))
                    .polygon("#336699").build(),
                R3, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L3).getSldBody().contains("Within"), "within spatial");

        // disjoint
        String L4 = n(TS, "fs_dis"), R4 = n(TS, "fs_dis_r");
        crossCheck(L4,
                SldBuilder.create(L4).rule("r")
                    .filter(disjoint("the_geom", point(200, 200)))
                    .polygon("#336699").build(),
                R4, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L4).getSldBody().contains("Disjoint"), "disjoint");

        // touches
        String L5 = n(TS, "fs_tou"), R5 = n(TS, "fs_tou_r");
        crossCheck(L5,
                SldBuilder.create(L5).rule("r")
                    .filter(touches("the_geom", point(0, 0)))
                    .polygon("#336699").build(),
                R5, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L5).getSldBody().contains("Touches"), "touches");

        // crosses
        String L6 = n(TS, "fs_cro"), R6 = n(TS, "fs_cro_r");
        crossCheck(L6,
                SldBuilder.create(L6).rule("r")
                    .filter(crosses("the_geom", lineString(-1, 0, 1, 0)))
                    .polygon("#336699").build(),
                R6, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L6).getSldBody().contains("Crosses"), "crosses");

        // overlaps
        String L7 = n(TS, "fs_ove"), R7 = n(TS, "fs_ove_r");
        crossCheck(L7,
                SldBuilder.create(L7).rule("r")
                    .filter(overlaps("the_geom", polygon(0, 0, 1, 0, 1, 1, 0, 1, 0, 0)))
                    .polygon("#336699").build(),
                R7, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L7).getSldBody().contains("Overlaps"), "overlaps");

        // spatialEquals
        String L8 = n(TS, "fs_seq"), R8 = n(TS, "fs_seq_r");
        crossCheck(L8,
                SldBuilder.create(L8).rule("r")
                    .filter(spatialEquals("the_geom", point(0, 0)))
                    .polygon("#336699").build(),
                R8, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L8).getSldBody().contains("Equals"), "spatialEquals");

        // dWithin
        String L9 = n(TS, "fs_dwi"), R9 = n(TS, "fs_dwi_r");
        crossCheck(L9,
                SldBuilder.create(L9).rule("r")
                    .filter(dWithin("the_geom", point(0, 0), 100, "meters"))
                    .polygon("#336699").build(),
                R9, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L9).getSldBody().contains("DWithin"), "dWithin");

        // beyond
        String L10 = n(TS, "fs_bey"), R10 = n(TS, "fs_bey_r");
        crossCheck(L10,
                SldBuilder.create(L10).rule("r")
                    .filter(beyond("the_geom", point(0, 0), 1000, "meters"))
                    .polygon("#336699").build(),
                R10, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L10).getSldBody().contains("Beyond"), "beyond");

        // bbox with SldGeometry envelope (not convenience overload)
        String L11 = n(TS, "fs_bge"), R11 = n(TS, "fs_bge_r");
        crossCheck(L11,
                SldBuilder.create(L11).rule("r")
                    .filter(bbox("the_geom", envelope(-180, -90, 180, 90, "EPSG:4326")))
                    .polygon("#336699").build(),
                R11, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L11).getSldBody().contains("BBOX"), "bbox with envelope");

        // bbox without SRS
        String L12 = n(TS, "fs_bgn"), R12 = n(TS, "fs_bgn_r");
        crossCheck(L12,
                SldBuilder.create(L12).rule("r")
                    .filter(bbox("the_geom", -180, -90, 180, 90))
                    .polygon("#336699").build(),
                R12, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L12).getSldBody().contains("BBOX"), "bbox without SRS");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── SldGeometry: all geometry types in spatial filters ────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(27)
    void geometry_allTypes() throws Exception {
        // point with SRS
        String L1 = n(TS, "geo_pt"), R1 = n(TS, "geo_pt_r");
        crossCheck(L1,
                SldBuilder.create(L1).rule("r")
                    .filter(intersects("g", point(10, 20, "EPSG:4326")))
                    .polygon("#336699").build(),
                R1, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L1).getSldBody().contains("Point"), "Point geometry");

        // lineString with SRS
        String L2 = n(TS, "geo_ls"), R2 = n(TS, "geo_ls_r");
        crossCheck(L2,
                SldBuilder.create(L2).rule("r")
                    .filter(intersects("g", lineString("EPSG:4326", 0, 0, 10, 10)))
                    .polygon("#336699").build(),
                R2, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L2).getSldBody().contains("LineString"), "LineString geometry");

        // polygon with SRS
        String L3 = n(TS, "geo_pg"), R3 = n(TS, "geo_pg_r");
        crossCheck(L3,
                SldBuilder.create(L3).rule("r")
                    .filter(intersects("g", polygon("EPSG:4326", -1, -1, 1, -1, 1, 1, -1, 1, -1, -1)))
                    .polygon("#336699").build(),
                R3, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L3).getSldBody().contains("Polygon"), "Polygon geometry");

        // multiPoint
        String L4 = n(TS, "geo_mp"), R4 = n(TS, "geo_mp_r");
        crossCheck(L4,
                SldBuilder.create(L4).rule("r")
                    .filter(intersects("g", multiPoint("EPSG:4326", 0, 0, 10, 10, 20, 20)))
                    .polygon("#336699").build(),
                R4, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L4).getSldBody().contains("MultiPoint"), "MultiPoint geometry");

        // multiLineString
        String L5 = n(TS, "geo_ml"), R5 = n(TS, "geo_ml_r");
        crossCheck(L5,
                SldBuilder.create(L5).rule("r")
                    .filter(intersects("g", multiLineString(
                            new double[]{0, 0, 10, 10},
                            new double[]{20, 20, 30, 30})))
                    .polygon("#336699").build(),
                R5, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L5).getSldBody().contains("MultiLineString"), "MultiLineString geometry");

        // multiPolygon
        String L6 = n(TS, "geo_mpg"), R6 = n(TS, "geo_mpg_r");
        crossCheck(L6,
                SldBuilder.create(L6).rule("r")
                    .filter(intersects("g", multiPolygon(
                            new double[]{0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
                            new double[]{5, 5, 6, 5, 6, 6, 5, 6, 5, 5})))
                    .polygon("#336699").build(),
                R6, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
        assertTrue(styles.getSldByWorkspace(WS, L6).getSldBody().contains("MultiPolygon"), "MultiPolygon geometry");

        // geometryCollection — GeoServer 2.28.2 drops the geometry body from stored SLD;
        // verify library generates correct XML and GeoServer accepts the SLD (201).
        String L7 = n(TS, "geo_gc"), R7 = n(TS, "geo_gc_r");
        StyleContent libGC = SldBuilder.create(L7).rule("r")
                .filter(intersects("g", geometryCollection(point(0, 0), lineString(0, 0, 1, 1))))
                .polygon("#336699").build();
        assertTrue(libGC.getSldBody().contains("GeometryCollection"), "library XML must contain GeometryCollection");
        crossCheck(L7, libGC, R7,
                wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");

        // multiPoint without SRS
        String L8 = n(TS, "geo_mpn"), R8 = n(TS, "geo_mpn_r");
        crossCheck(L8,
                SldBuilder.create(L8).rule("r")
                    .filter(intersects("g", multiPoint(0, 0, 10, 10)))
                    .polygon("#336699").build(),
                R8, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");

        // multiLineString without SRS
        String L9 = n(TS, "geo_mln"), R9 = n(TS, "geo_mln_r");
        crossCheck(L9,
                SldBuilder.create(L9).rule("r")
                    .filter(intersects("g", multiLineString(new double[]{0, 0, 1, 1})))
                    .polygon("#336699").build(),
                R9, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");

        // multiPolygon without SRS
        String L10 = n(TS, "geo_mpgn"), R10 = n(TS, "geo_mpgn_r");
        crossCheck(L10,
                SldBuilder.create(L10).rule("r")
                    .filter(intersects("g", multiPolygon(new double[]{0, 0, 1, 0, 1, 1, 0, 1, 0, 0})))
                    .polygon("#336699").build(),
                R10, wrap("<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"),
                "PolygonSymbolizer");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── SldExpression: add / sub / div / nested arithmetic ────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(28)
    void expression_allArithmetic() throws Exception {
        // add
        String L1 = n(TS, "ex_add"), R1 = n(TS, "ex_add_r");
        StyleContent lib1 = SldBuilder.create(L1)
                .rule("r")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#FF0000")
                        .size(add(property("A"), literal(5)))
                    .end()
                .build();
        assertTrue(lib1.getSldBody().contains("Add"), "Add arithmetic in generated SLD");
        styles.createByWorkspace(WS, lib1, L1);
        assertTrue(styles.existsByWorkspace(WS, L1));
        assertEquals(201, postSld(R1, wrap("<PointSymbolizer><Graphic><Mark>"
                + "<WellKnownName>circle</WellKnownName>"
                + "<Fill><CssParameter name=\"fill\">#FF0000</CssParameter></Fill>"
                + "</Mark><Size>10</Size></Graphic></PointSymbolizer>")));

        // sub
        String L2 = n(TS, "ex_sub"), R2 = n(TS, "ex_sub_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .rule("r")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#00FF00")
                        .size(sub(property("MAX"), property("MIN")))
                    .end()
                .build();
        assertTrue(lib2.getSldBody().contains("Sub"), "Sub arithmetic");
        styles.createByWorkspace(WS, lib2, L2);
        assertTrue(styles.existsByWorkspace(WS, L2));
        assertEquals(201, postSld(R2, wrap("<PointSymbolizer><Graphic><Mark>"
                + "<WellKnownName>circle</WellKnownName>"
                + "<Fill><CssParameter name=\"fill\">#00FF00</CssParameter></Fill>"
                + "</Mark><Size>10</Size></Graphic></PointSymbolizer>")));

        // div
        String L3 = n(TS, "ex_div"), R3 = n(TS, "ex_div_r");
        StyleContent lib3 = SldBuilder.create(L3)
                .rule("r")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#0000FF")
                        .size(div(property("VALUE"), literal(100)))
                    .end()
                .build();
        assertTrue(lib3.getSldBody().contains("Div"), "Div arithmetic");
        styles.createByWorkspace(WS, lib3, L3);
        assertTrue(styles.existsByWorkspace(WS, L3));
        assertEquals(201, postSld(R3, wrap("<PointSymbolizer><Graphic><Mark>"
                + "<WellKnownName>circle</WellKnownName>"
                + "<Fill><CssParameter name=\"fill\">#0000FF</CssParameter></Fill>"
                + "</Mark><Size>10</Size></Graphic></PointSymbolizer>")));

        // nested: (A + B) * C
        String L4 = n(TS, "ex_nest"), R4 = n(TS, "ex_nest_r");
        StyleContent lib4 = SldBuilder.create(L4)
                .rule("r")
                    .mark(WellKnownName.CIRCLE)
                        .fill("#FF00FF")
                        .size(mul(add(property("A"), property("B")), literal(0.5)))
                    .end()
                .build();
        String xml4 = lib4.getSldBody();
        assertTrue(xml4.contains("Mul") && xml4.contains("Add"), "nested arithmetic Mul(Add)");
        styles.createByWorkspace(WS, lib4, L4);
        assertTrue(styles.existsByWorkspace(WS, L4));
        assertEquals(201, postSld(R4, wrap("<PointSymbolizer><Graphic><Mark>"
                + "<WellKnownName>circle</WellKnownName>"
                + "<Fill><CssParameter name=\"fill\">#FF00FF</CssParameter></Fill>"
                + "</Mark><Size>10</Size></Graphic></PointSymbolizer>")));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── UserLayer + RemoteOWS + LayerFeatureConstraints + Extent ─────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(29)
    void userLayer_withRemoteOwsAndConstraints() throws Exception {
        // UserLayer with RemoteOWS
        StyleContent ulSld = SldBuilder.createUserLayer("my-wfs")
                .remoteOws("WFS", "http://example.org/wfs")
                .layerConstraint("cite:Roads", equalTo("TYPE", "highway"))
                .styleName("road-style")
                .rule("highway").line("#FF0000", 2)
                .build();

        String xml = ulSld.getSldBody();
        assertTrue(xml.contains("UserLayer"), "UserLayer element");
        assertTrue(xml.contains("RemoteOWS"), "RemoteOWS element");
        assertTrue(xml.contains("LayerFeatureConstraints"), "LayerFeatureConstraints element");
        assertTrue(xml.contains("cite:Roads"), "FeatureTypeName in constraint");
        assertTrue(xml.contains("highway"), "filter in constraint");
        assertTrue(xml.contains("UserStyle"), "UserStyle inside UserLayer");

        // Upload to GeoServer global scope (UserLayer SLDs must be global)
        int status = postSldGlobal(n(TS, "ul_rmt"), xml);
        assertEquals(201, status, "UserLayer SLD must be accepted by GeoServer");
        String stored = fetchSldGlobal(n(TS, "ul_rmt"));
        assertFalse(stored.isEmpty(), "stored UserLayer SLD must not be empty");

        // LayerFeatureConstraints with Extent
        StyleContent ulExtSld = SldBuilder.createUserLayer("my-wfs-extent")
                .layerConstraint("cite:Roads", null,
                        -180, -90, 180, 90)
                .rule("r").polygon("#336699")
                .build();

        String xmlExt = ulExtSld.getSldBody();
        assertTrue(xmlExt.contains("UserLayer"), "UserLayer with extent");
        assertTrue(xmlExt.contains("Extent"), "Extent element in constraint");
        assertTrue(xmlExt.contains("BBOX"), "Extent Name=BBOX");

        int statusExt = postSldGlobal(n(TS, "ul_ext"), xmlExt);
        assertEquals(201, statusExt, "UserLayer+Extent SLD must be accepted");

        // LayerFeatureConstraints with no filter (just featureTypeName)
        StyleContent ulNoFSld = SldBuilder.createUserLayer("my-wfs-nf")
                .layerConstraint("cite:Buildings", null)
                .rule("r").polygon("#336699")
                .build();
        String xmlNF = ulNoFSld.getSldBody();
        assertTrue(xmlNF.contains("FeatureTypeConstraint"), "FeatureTypeConstraint present");
        int statusNF = postSldGlobal(n(TS, "ul_nof"), xmlNF);
        assertEquals(201, statusNF, "UserLayer+no-filter SLD must be accepted");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Multiple FTS / multiple UserStyle / navigation helpers ─────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(30)
    void multipleFtsAndUserStyle_navigation() throws Exception {
        String L = n(TS, "multi_fts"), R = n(TS, "multi_fts_r");

        // Multiple FTS with semantic type identifiers
        StyleContent lib = SldBuilder.create(L)
                .featureTypeStyle("fill-fts")
                    .ftsSemanticTypeIdentifier("generic:polygon")
                    .rule("fill").polygon("#336699")
                .featureTypeStyle("stroke-fts")
                    .ftsSemanticTypeIdentifier("generic:line")
                    .rule("stroke").line("#000000", 1)
                .featureTypeStyle("label-fts")
                    .ftsSemanticTypeIdentifier("generic:text")
                    .rule("label").label("NAME").color("#000000").end()
                .build();

        String raw = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<StyledLayerDescriptor version=\"1.0.0\" xmlns=\"http://www.opengis.net/sld\""
                + " xmlns:ogc=\"http://www.opengis.net/ogc\">"
                + "<NamedLayer><Name>" + R + "</Name><UserStyle>"
                + "<FeatureTypeStyle><Rule>"
                + "<PolygonSymbolizer><Fill><CssParameter name=\"fill\">#336699</CssParameter></Fill></PolygonSymbolizer>"
                + "</Rule></FeatureTypeStyle>"
                + "<FeatureTypeStyle><Rule>"
                + "<LineSymbolizer><Stroke><CssParameter name=\"stroke\">#000000</CssParameter>"
                + "<CssParameter name=\"stroke-width\">1</CssParameter></Stroke></LineSymbolizer>"
                + "</Rule></FeatureTypeStyle>"
                + "</UserStyle></NamedLayer></StyledLayerDescriptor>";

        crossCheck(L, lib, R, raw, "PolygonSymbolizer", "LineSymbolizer");
        String xml = lib.getSldBody();
        assertTrue(countOccurrences(xml, "FeatureTypeStyle") >= 3, "3 FTS blocks");
        assertTrue(xml.contains("generic:polygon"), "semantic type polygon");
        assertTrue(xml.contains("generic:line"), "semantic type line");
        assertTrue(xml.contains("generic:text"), "semantic type text");

        // Multiple UserStyle
        String L2 = n(TS, "multi_us"), R2 = n(TS, "multi_us_r");
        StyleContent lib2 = SldBuilder.create(L2)
                .styleName("default").title("Default Style").isDefault(true)
                .rule("r").polygon("#336699")
                .userStyle("alternate")
                    .rule("r").polygon("#FF9900")
                .build();

        crossCheck(L2, lib2, R2, raw.replace(R, R2).replace("336699", "FF9900"), "PolygonSymbolizer");
        assertTrue(lib2.getSldBody().contains("UserStyle"), "multiple UserStyle");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── NamedStyle reference (XML-only — GeoServer cannot store NamedStyle-only SLDs) ──
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(31)
    void namedStyleRef_xmlValidation() {
        StyleContent ns = SldBuilder.create("ref-layer")
                .namedStyle("existing-style")
                .buildForNamedStyle();
        String xml = ns.getSldBody();
        assertTrue(xml.contains("NamedStyle"), "NamedStyle element");
        assertTrue(xml.contains("existing-style"), "style name in NamedStyle");
        assertTrue(xml.contains("StyledLayerDescriptor"), "SLD root");
        assertTrue(xml.contains("NamedLayer"), "NamedLayer wrapper");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Completeness: every WellKnownName value ───────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(32)
    void wellKnownNames_allValues() throws Exception {
        WellKnownName[] marks = WellKnownName.values();
        for (WellKnownName mark : marks) {
            String L = n(TS, "wkn_" + mark.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r").mark(mark).fill("#FF0000").size(10).end()
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "WKN style must exist: " + mark);
            assertTrue(lib.getSldBody().contains(mark.getValue()),
                    "WellKnownName value '" + mark.getValue() + "' must be in SLD");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Completeness: SldColorMapType all values ──────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(33)
    void colorMapTypes_allValues() throws Exception {
        for (SldColorMapType type : SldColorMapType.values()) {
            String L = n(TS, "cmt_" + type.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r").raster(1.0, type,
                            SldColorMapEntry.of("#FFFFFF", 0),
                            SldColorMapEntry.of("#000000", 100))
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "ColorMapType style: " + type);
            assertTrue(lib.getSldBody().contains(type.getValue()),
                    "ColorMapType value must be in SLD: " + type.getValue());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Completeness: LineCap / LineJoin all values ────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(34)
    void lineCapJoin_allValues() throws Exception {
        for (LineCap cap : LineCap.values()) {
            String L = n(TS, "cap_" + cap.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r").stroke("#000000", 1).lineCap(cap).end()
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "LineCap style: " + cap);
            assertTrue(lib.getSldBody().contains(cap.getValue()), "LineCap value: " + cap.getValue());
        }
        for (LineJoin join : LineJoin.values()) {
            String L = n(TS, "join_" + join.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r").stroke("#000000", 1).lineJoin(join).end()
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "LineJoin style: " + join);
            assertTrue(lib.getSldBody().contains(join.getValue()), "LineJoin value: " + join.getValue());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Completeness: OverlapBehavior all values ──────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(35)
    void overlapBehavior_allValues() throws Exception {
        for (OverlapBehavior ob : OverlapBehavior.values()) {
            String L = n(TS, "ob_" + ob.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r")
                        .rasterBuilder()
                            .overlapBehavior(ob)
                            .colorMap(SldColorMapType.RAMP,
                                    SldColorMapEntry.of("#FFFFFF", 0),
                                    SldColorMapEntry.of("#000000", 100))
                        .end()
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "OverlapBehavior: " + ob);
            assertTrue(lib.getSldBody().contains(ob.getValue()), "OverlapBehavior value: " + ob.getValue());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── Completeness: ContrastMethod all values ───────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    @Test @Order(36)
    void contrastMethod_allValues() throws Exception {
        for (ContrastMethod cm : ContrastMethod.values()) {
            String L = n(TS, "cm_" + cm.name().toLowerCase());
            StyleContent lib = SldBuilder.create(L)
                    .rule("r")
                        .rasterBuilder()
                            .contrastEnhancement(cm)
                            .colorMap(SldColorMapType.RAMP,
                                    SldColorMapEntry.of("#FFFFFF", 0),
                                    SldColorMapEntry.of("#000000", 100))
                        .end()
                    .build();
            styles.createByWorkspace(WS, lib, L);
            assertTrue(styles.existsByWorkspace(WS, L), "ContrastMethod: " + cm);
            assertTrue(lib.getSldBody().contains(cm.getTag()), "ContrastMethod tag: " + cm.getTag());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ── helper ────────────────────────────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    private static long countOccurrences(String text, String needle) {
        long count = 0;
        int idx = 0;
        while ((idx = text.indexOf(needle, idx)) != -1) { count++; idx++; }
        return count;
    }
}
