package io.github.kimbongjune.geoserverclient.dto.style;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.github.kimbongjune.geoserverclient.dto.style.SldExpression.*;

@DisplayName("[UnitTest] SldBuilder")
class SldBuilderTest {

    // ── PointSymbolizer (simple) ──────────────────────────────────────────

    @Test
    void point_markWithFill() {
        String xml = build(SldBuilder.create("l").rule().point(WellKnownName.CIRCLE, "#FF0000", 8));
        assertTrue(xml.contains("<WellKnownName>circle</WellKnownName>"));
        assertTrue(xml.contains("<CssParameter name=\"fill\">#FF0000</CssParameter>"));
        assertTrue(xml.contains("<Size>8</Size>"));
        assertFalse(xml.contains("<Stroke>"));
    }

    @Test
    void pointExternal_externalGraphic() {
        String xml = build(SldBuilder.create("l").rule()
                .pointExternal("http://host/icon.png", "image/png", 32));
        assertTrue(xml.contains("<ExternalGraphic>"));
        assertTrue(xml.contains("xlink:href=\"http://host/icon.png\""));
        assertTrue(xml.contains("<Format>image/png</Format>"));
        assertTrue(xml.contains("<Size>32</Size>"));
    }

    // ── PointBuilder (full) ───────────────────────────────────────────────

    @Test
    void mark_withRotation() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.TRIANGLE)
                    .fill("#FF0000").stroke("#000000", 1).size(10).rotation(45)
                .end().build().getSldBody();
        assertTrue(xml.contains("<WellKnownName>triangle</WellKnownName>"));
        assertTrue(xml.contains("<Rotation>45</Rotation>"));
    }

    @Test
    void mark_withOpacity() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE).fill("#0000FF").size(8).opacity(0.5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Opacity>0.5</Opacity>"));
    }

    @Test
    void externalPoint_withRotation() {
        String xml = SldBuilder.create("l").rule()
                .externalPoint("http://host/arrow.svg", "image/svg+xml")
                    .size(24).rotation(90)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ExternalGraphic>"));
        assertTrue(xml.contains("<Rotation>90</Rotation>"));
    }

    @Test
    void externalMark_ttfGlyph() {
        String xml = SldBuilder.create("l").rule()
                .externalMark("fonts/symbols.ttf", "font/ttf", 15)
                    .fill("#FF0000").size(16)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ExternalMark>"));
        assertTrue(xml.contains("symbols.ttf"));
        assertTrue(xml.contains("<MarkIndex>15</MarkIndex>"));
    }

    @Test
    void mark_withGeometryProperty() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE).fill("#FF0000").size(8).geometry("point_geom")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:PropertyName>point_geom</ogc:PropertyName>"));
    }

    // ── LineSymbolizer ────────────────────────────────────────────────────

    @Test
    void line_solid() {
        String xml = build(SldBuilder.create("l").rule().line("#0000FF", 2));
        assertTrue(xml.contains("<LineSymbolizer>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke\">#0000FF</CssParameter>"));
        assertFalse(xml.contains("dasharray"));
    }

    @Test
    void line_dashed() {
        String xml = build(SldBuilder.create("l").rule().line("#000000", 1, "4 2"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-dasharray\">4 2</CssParameter>"));
    }

    // ── PolygonSymbolizer ─────────────────────────────────────────────────

    @Test
    void polygon_fillAndStroke() {
        String xml = build(SldBuilder.create("l").rule()
                .polygon("#AAAAAA", 0.5, "#000000", 1));
        assertTrue(xml.contains("<PolygonSymbolizer>"));
        assertTrue(xml.contains("<CssParameter name=\"fill-opacity\">0.5</CssParameter>"));
    }

    @Test
    void polygon_fillOnly() {
        String xml = build(SldBuilder.create("l").rule().polygon("#AAAAAA"));
        assertFalse(xml.contains("<Stroke>"));
    }

    // ── TextSymbolizer (simple) ───────────────────────────────────────────

    @Test
    void text_propertyLabel() {
        String xml = build(SldBuilder.create("l").rule()
                .text("name", "#000000", "Arial", 12));
        assertTrue(xml.contains("<TextSymbolizer>"));
        assertTrue(xml.contains("<ogc:PropertyName>name</ogc:PropertyName>"));
        assertTrue(xml.contains("<PointPlacement>"));
    }

    @Test
    void text_withHalo() {
        String xml = build(SldBuilder.create("l").rule()
                .text("name", "#000000", "Arial", 12, "#FFFFFF", 2));
        assertTrue(xml.contains("<Halo>"));
        assertTrue(xml.contains("<Radius>2</Radius>"));
    }

    @Test
    void staticText_literalLabel() {
        String xml = build(SldBuilder.create("l").rule()
                .staticText("Fixed", "#000000", "Arial", 10));
        assertTrue(xml.contains("<ogc:Literal>Fixed</ogc:Literal>"));
        assertFalse(xml.contains("<ogc:PropertyName>"));
    }

    @Test
    void textOnLine_linePlacement() {
        String xml = build(SldBuilder.create("l").rule()
                .textOnLine("name", "#000000", "Arial", 11, 5));
        assertTrue(xml.contains("<LinePlacement>"));
        assertTrue(xml.contains("<PerpendicularOffset>5</PerpendicularOffset>"));
    }

    // ── TextBuilder (full) ────────────────────────────────────────────────

    @Test
    void label_bold() {
        String xml = SldBuilder.create("l").rule()
                .label("name").color("#000000").font("Arial", 14).bold()
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"font-weight\">bold</CssParameter>"));
    }

    @Test
    void label_italic() {
        String xml = SldBuilder.create("l").rule()
                .label("name").font("Arial", 12).italic()
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"font-style\">italic</CssParameter>"));
    }

    @Test
    void label_haloAndDisplacement() {
        String xml = SldBuilder.create("l").rule()
                .label("name").color("#000000").font("Arial", 12)
                    .halo("#FFFFFF", 2).displacement(5, 10)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Halo>"));
        assertTrue(xml.contains("<DisplacementX>5</DisplacementX>"));
        assertTrue(xml.contains("<DisplacementY>10</DisplacementY>"));
    }

    @Test
    void label_rotation() {
        String xml = SldBuilder.create("l").rule()
                .label("name").font("Arial", 12).rotation(45)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Rotation>45</Rotation>"));
    }

    @Test
    void label_linePlacement() {
        String xml = SldBuilder.create("l").rule()
                .label("name").font("Arial", 11).linePlacement(5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<LinePlacement>"));
        assertFalse(xml.contains("<PointPlacement>"));
    }

    @Test
    void label_vendorOptions() {
        String xml = SldBuilder.create("l").rule()
                .label("name").font("Arial", 12)
                    .vendorOption("labelObstacle", "true")
                    .vendorOption("maxDisplacement", "40")
                .end().build().getSldBody();
        assertTrue(xml.contains("<VendorOption name=\"labelObstacle\">true</VendorOption>"));
        assertTrue(xml.contains("<VendorOption name=\"maxDisplacement\">40</VendorOption>"));
    }

    @Test
    void label_geometryProperty() {
        String xml = SldBuilder.create("l").rule()
                .label("name").font("Arial", 12).geometry("label_geom")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:PropertyName>label_geom</ogc:PropertyName>"));
    }

    @Test
    void staticLabel_withBold() {
        String xml = SldBuilder.create("l").rule()
                .staticLabel("STATIC").font("Arial", 10).bold()
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Literal>STATIC</ogc:Literal>"));
        assertTrue(xml.contains("<CssParameter name=\"font-weight\">bold</CssParameter>"));
    }

    // ── RasterSymbolizer ──────────────────────────────────────────────────

    @Test
    void raster_colourRamp() {
        String xml = build(SldBuilder.create("dem").rule()
                .raster(0.8, SldColorMapType.RAMP,
                        SldColorMapEntry.of("#0000FF", 0, "Low"),
                        SldColorMapEntry.of("#FF0000", 100, "High")));
        assertTrue(xml.contains("<RasterSymbolizer>"));
        assertTrue(xml.contains("<ColorMap type=\"ramp\">"));
        assertTrue(xml.contains("color=\"#0000FF\""));
        assertTrue(xml.contains("label=\"High\""));
    }

    // ── OGC Filters ───────────────────────────────────────────────────────

    @Test
    void filter_equalTo() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(equalTo("type", "highway")).line("#FF0000", 3));
        assertTrue(xml.contains("<ogc:PropertyIsEqualTo>"));
        assertTrue(xml.contains("<ogc:Literal>highway</ogc:Literal>"));
    }

    @Test
    void filter_between() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(between("speed", 50, 100)).line("#000", 1));
        assertTrue(xml.contains("<ogc:LowerBoundary>"));
        assertTrue(xml.contains("<ogc:UpperBoundary>"));
    }

    @Test
    void filter_and_composition() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(and(equalTo("type", "road"), lessThan("lanes", 3)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:And>"));
        assertTrue(xml.contains("<ogc:PropertyIsLessThan>"));
    }

    @Test
    void filter_or_composition() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(or(equalTo("type", "A"), equalTo("type", "B"))).line("#000", 1));
        assertTrue(xml.contains("<ogc:Or>"));
    }

    @Test
    void filter_not() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(not(isNull("name"))).line("#000", 1));
        assertTrue(xml.contains("<ogc:Not>"));
    }

    @Test
    void filter_like() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(like("name", "Road*")).line("#000", 1));
        assertTrue(xml.contains("wildCard=\"*\""));
    }

    // ── ElseFilter ────────────────────────────────────────────────────────

    @Test
    void elseFilter_generatesElseFilterElement() {
        String xml = SldBuilder.create("l")
                .rule("main").filter(equalTo("type", "A")).polygon("#336699")
                .rule("others").elseFilter().polygon("#CCCCCC")
                .build().getSldBody();
        assertTrue(xml.contains("<ElseFilter/>"));
        // first rule has ogc:Filter, second has ElseFilter — exactly one ogc:Filter block
        assertEquals(1, xml.split("<ogc:Filter>").length - 1);
        assertEquals(2, xml.split("<Rule>").length - 1);
    }

    @Test
    void filter_appearsBeforeScaleDenominator() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(equalTo("type", "A")).maxScale(50000).line("#000", 1));
        assertTrue(xml.indexOf("<ogc:Filter>") < xml.indexOf("<MaxScaleDenominator>"));
    }

    // ── Multi-rule ────────────────────────────────────────────────────────

    @Test
    void multiRule_scaleAndFilter() {
        String xml = SldBuilder.create("roads")
                .rule("thin").maxScale(50000).line("#666", 1)
                .rule("thick").minScale(50000).line("#666", 3)
                .build().getSldBody();
        assertEquals(2, xml.split("<Rule>").length - 1);
        assertTrue(xml.contains("<MaxScaleDenominator>50000</MaxScaleDenominator>"));
        assertTrue(xml.contains("<MinScaleDenominator>50000</MinScaleDenominator>"));
    }

    // ── Misc ──────────────────────────────────────────────────────────────

    @Test
    void title_appearsInXml() {
        assertTrue(build(SldBuilder.create("l").title("T").rule().line("#000", 1))
                .contains("<Title>T</Title>"));
    }

    @Test
    void xmlEscaping_specialChars() {
        assertTrue(build(SldBuilder.create("a&b<c>").rule().line("#000", 1))
                .contains("a&amp;b&lt;c&gt;"));
    }

    @Test
    void create_emptyName_throws() {
        assertThrows(IllegalArgumentException.class, () -> SldBuilder.create(""));
    }

    @Test
    void build_noRules_throws() {
        assertThrows(IllegalStateException.class, () -> SldBuilder.create("l").build());
    }

    @Test
    void fmt_wholeDouble_omitsDecimal() {
        assertEquals("6",   SldBuilder.fmt(6.0));
        assertEquals("1.5", SldBuilder.fmt(1.5));
    }

    // ── LineBuilder ───────────────────────────────────────────────────────

    @Test
    void stroke_withLineCap() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#CC0000", 2).lineCap(LineCap.ROUND)
                .end().build().getSldBody();
        assertTrue(xml.contains("<LineSymbolizer>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-linecap\">round</CssParameter>"));
    }

    @Test
    void stroke_withLineJoin() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#CC0000", 2).lineJoin(LineJoin.BEVEL)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-linejoin\">bevel</CssParameter>"));
    }

    @Test
    void stroke_withDashAndOpacity() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).dashArray("4 2").opacity(0.7)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-dasharray\">4 2</CssParameter>"));
        assertTrue(xml.contains("<Opacity>0.7</Opacity>"));
    }

    @Test
    void stroke_withGeometry() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).geometry("line_geom")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:PropertyName>line_geom</ogc:PropertyName>"));
    }

    @Test
    void stroke_fullOptions() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#336699", 3)
                    .dashArray("8 4").lineCap(LineCap.BUTT).lineJoin(LineJoin.MITRE)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-linecap\">butt</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-linejoin\">mitre</CssParameter>"));
    }

    // ── SldExpression size/rotation on PointBuilder ───────────────────────

    @Test
    void mark_proportionalSize_fromAttribute() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE)
                    .fill("#FF0000")
                    .size(SldExpression.property("magnitude"))
                .end().build().getSldBody();
        assertTrue(xml.contains("<Size><ogc:PropertyName>magnitude</ogc:PropertyName></Size>"));
    }

    @Test
    void mark_rotationFromAttribute() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.TRIANGLE)
                    .fill("#0000FF").size(12)
                    .rotation(SldExpression.property("bearing"))
                .end().build().getSldBody();
        assertTrue(xml.contains("<Rotation><ogc:PropertyName>bearing</ogc:PropertyName></Rotation>"));
    }

    @Test
    void externalPoint_proportionalSize() {
        String xml = SldBuilder.create("l").rule()
                .externalPoint("http://host/icon.png", "image/png")
                    .size(SldExpression.function("mul", SldExpression.property("pop"),
                            SldExpression.literal(0.001)))
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Function name=\"mul\">"));
    }

    // ── OGC function filters ──────────────────────────────────────────────

    @Test
    void filter_functionExpression() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.equalTo(
                        SldExpression.function("strToLowerCase", SldExpression.property("type")),
                        SldExpression.literal("highway")))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Function name=\"strToLowerCase\">"));
        assertTrue(xml.contains("<ogc:PropertyName>type</ogc:PropertyName>"));
        assertTrue(xml.contains("<ogc:Literal>highway</ogc:Literal>"));
    }

    @Test
    void filter_isNull_expression() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.isNull(SldExpression.property("name")))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:PropertyIsNull>"));
        assertTrue(xml.contains("<ogc:PropertyName>name</ogc:PropertyName>"));
    }

    @Test
    void filter_between_expressionVariant() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.between(
                        SldExpression.function("strLength", SldExpression.property("name")),
                        3, 10))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Function name=\"strLength\">"));
        assertTrue(xml.contains("<ogc:LowerBoundary>"));
    }

    // ── UserStyle Abstract / IsDefault ───────────────────────────────────

    @Test
    void userStyle_abstract() {
        String xml = SldBuilder.create("l").abstract_("Demo style")
                .rule().line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("<Abstract>Demo style</Abstract>"));
    }

    @Test
    void userStyle_isDefault() {
        String xml = SldBuilder.create("l").isDefault(true)
                .rule().line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("<IsDefault>1</IsDefault>"));
    }

    // ── Multiple FeatureTypeStyle ─────────────────────────────────────────

    @Test
    void multipleFeatureTypeStyle_twoBlocks() {
        String xml = SldBuilder.create("roads")
                .rule().polygon("#336699")
                .featureTypeStyle()
                .rule().line("#000000", 1)
                .build().getSldBody();
        assertEquals(2, xml.split("<FeatureTypeStyle>").length - 1);
    }

    @Test
    void featureTypeStyle_withNameAndTitle() {
        String xml = SldBuilder.create("l")
                .featureTypeStyle("casing").ftsTitle("Road Casing")
                .rule().line("#000000", 4)
                .build().getSldBody();
        assertTrue(xml.contains("<Name>casing</Name>"));
        assertTrue(xml.contains("<Title>Road Casing</Title>"));
    }

    @Test
    void featureTypeStyle_withFeatureTypeName() {
        String xml = SldBuilder.create("l")
                .ftsFeatureTypeName("sf:roads")
                .rule().line("#000", 1)
                .build().getSldBody();
        assertTrue(xml.contains("<FeatureTypeName>sf:roads</FeatureTypeName>"));
    }

    @Test
    void featureTypeStyle_emptyBlockSkipped() {
        // Empty FTS (no rules) should not appear in output
        String xml = SldBuilder.create("l")
                .rule().line("#000", 1)
                .featureTypeStyle() // empty — no rules added
                .build().getSldBody();
        assertEquals(1, xml.split("<FeatureTypeStyle>").length - 1);
    }

    // ── PointBuilder mark stroke full options ─────────────────────────────

    @Test
    void mark_strokeOpacity() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE).fill("#FF0000")
                    .stroke("#000000", 1).strokeOpacity(0.5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-opacity\">0.5</CssParameter>"));
    }

    @Test
    void mark_strokeLineCap() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.STAR).fill("#FF0000")
                    .stroke("#000000", 1).strokeLineCap(LineCap.ROUND)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-linecap\">round</CssParameter>"));
    }

    @Test
    void mark_strokeLineJoin() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.SQUARE).fill("#FF0000")
                    .stroke("#000000", 1).strokeLineJoin(LineJoin.BEVEL)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-linejoin\">bevel</CssParameter>"));
    }

    @Test
    void mark_strokeDashArray() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE).fill("#FF0000")
                    .stroke("#000000", 1).strokeDashArray("4 2").strokeDashOffset(1.0)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-dasharray\">4 2</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-dashoffset\">1</CssParameter>"));
    }

    // ── OGC Spatial Filters ───────────────────────────────────────────────

    @Test
    void spatialFilter_bbox_withCoords() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.bbox("the_geom", -180, -90, 180, 90, "EPSG:4326"))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:BBOX>"));
        assertTrue(xml.contains("<gml:Box"));
        assertTrue(xml.contains("-180,-90 180,90"));
    }

    @Test
    void spatialFilter_bbox_withGeometry() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.bbox("geom", SldGeometry.envelope(0, 0, 10, 10)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:BBOX>"));
        assertTrue(xml.contains("<gml:Box>"));
    }

    @Test
    void spatialFilter_intersects_point() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.intersects("geom", SldGeometry.point(5, 5, "EPSG:4326")))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Intersects>"));
        assertTrue(xml.contains("<gml:Point"));
        assertTrue(xml.contains("5,5"));
    }

    @Test
    void spatialFilter_intersects_lineString() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.intersects("geom", SldGeometry.lineString("EPSG:4326", 0, 0, 1, 1)))
                .line("#000", 1));
        assertTrue(xml.contains("<gml:LineString"));
        assertTrue(xml.contains("0,0 1,1"));
    }

    @Test
    void spatialFilter_within_polygon() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.within("geom",
                        SldGeometry.polygon("EPSG:4326", 0, 0, 1, 0, 1, 1, 0, 1, 0, 0)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Within>"));
        assertTrue(xml.contains("<gml:Polygon"));
    }

    @Test
    void spatialFilter_contains() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.contains("geom", SldGeometry.point(0, 0)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Contains>"));
    }

    @Test
    void spatialFilter_disjoint() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.disjoint("geom", SldGeometry.envelope(100, 100, 200, 200)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Disjoint>"));
    }

    @Test
    void spatialFilter_touches() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.touches("geom", SldGeometry.point(0, 0)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Touches>"));
    }

    @Test
    void spatialFilter_crosses() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.crosses("geom", SldGeometry.lineString(0, 0, 10, 10)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Crosses>"));
    }

    @Test
    void spatialFilter_overlaps() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.overlaps("geom", SldGeometry.envelope(0, 0, 5, 5)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Overlaps>"));
    }

    @Test
    void spatialFilter_spatialEquals() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.spatialEquals("geom", SldGeometry.point(1, 2)))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Equals>"));
    }

    @Test
    void spatialFilter_dWithin() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.dWithin("geom", SldGeometry.point(0, 0, "EPSG:4326"), 1000, "meters"))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:DWithin>"));
        assertTrue(xml.contains("<ogc:Distance units=\"meters\">1000.0</ogc:Distance>"));
    }

    @Test
    void spatialFilter_beyond() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.beyond("geom", SldGeometry.point(0, 0), 500, "feet"))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:Beyond>"));
        assertTrue(xml.contains("units=\"feet\""));
    }

    @Test
    void gmlNamespace_presentInOutput() {
        String xml = SldBuilder.create("l").rule().line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("xmlns:gml=\"http://www.opengis.net/gml\""));
    }

    // ── OGC FeatureId Filter ──────────────────────────────────────────────

    @Test
    void filter_featureId_single() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.featureId("roads.1"))
                .line("#000", 1));
        assertTrue(xml.contains("<ogc:FeatureId fid=\"roads.1\"/>"));
    }

    @Test
    void filter_featureId_multiple() {
        String xml = build(SldBuilder.create("l").rule()
                .filter(OgcFilters.featureId("roads.1", "roads.2", "roads.3"))
                .line("#000", 1));
        assertTrue(xml.contains("fid=\"roads.1\""));
        assertTrue(xml.contains("fid=\"roads.2\""));
        assertTrue(xml.contains("fid=\"roads.3\""));
    }

    // ── Rule Title / Abstract / LegendGraphic ────────────────────────────

    @Test
    void rule_titleAndAbstract() {
        String xml = SldBuilder.create("l").rule("r1")
                .title("Highways").abstract_("All highway features")
                .line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("<Title>Highways</Title>"));
        assertTrue(xml.contains("<Abstract>All highway features</Abstract>"));
    }

    @Test
    void rule_legendGraphicMark() {
        String xml = SldBuilder.create("l").rule()
                .legendGraphicMark(WellKnownName.SQUARE, "#336699", 16)
                .line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("<LegendGraphic>"));
        assertTrue(xml.contains("<WellKnownName>square</WellKnownName>"));
        assertTrue(xml.contains("<Size>16</Size>"));
    }

    @Test
    void rule_legendGraphicExternal() {
        String xml = SldBuilder.create("l").rule()
                .legendGraphicExternal("http://host/icon.png", "image/png", 24)
                .line("#000", 1).build().getSldBody();
        assertTrue(xml.contains("<LegendGraphic>"));
        assertTrue(xml.contains("xlink:href=\"http://host/icon.png\""));
    }

    // ── PolygonBuilder ────────────────────────────────────────────────────

    @Test
    void polygonBuilder_fillAndStroke() {
        String xml = SldBuilder.create("l").rule()
                .fill("#336699").fillOpacity(0.8)
                    .stroke("#000000", 1)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"fill\">#336699</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"fill-opacity\">0.8</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke\">#000000</CssParameter>"));
    }

    @Test
    void polygonBuilder_strokeLineCap_LineJoin() {
        String xml = SldBuilder.create("l").rule()
                .fill("#AAAAAA")
                    .stroke("#000", 2)
                    .strokeLineCap(LineCap.ROUND)
                    .strokeLineJoin(LineJoin.BEVEL)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-linecap\">round</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-linejoin\">bevel</CssParameter>"));
    }

    @Test
    void polygonBuilder_strokeDashAndOffset() {
        String xml = SldBuilder.create("l").rule()
                .fill("#AAAAAA").stroke("#000", 1)
                    .strokeDashArray("4 2").strokeDashOffset(1.5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-dasharray\">4 2</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"stroke-dashoffset\">1.5</CssParameter>"));
    }

    @Test
    void polygonBuilder_strokeOpacity() {
        String xml = SldBuilder.create("l").rule()
                .fill("#AAAAAA").stroke("#000", 1).strokeOpacity(0.5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-opacity\">0.5</CssParameter>"));
    }

    @Test
    void polygonBuilder_fillPattern_mark() {
        String xml = SldBuilder.create("l").rule()
                .fill().fillPattern(WellKnownName.CROSS, "#000000", 8)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GraphicFill>"));
        assertTrue(xml.contains("<WellKnownName>cross</WellKnownName>"));
        assertTrue(xml.contains("<Size>8</Size>"));
    }

    @Test
    void polygonBuilder_fillPattern_external() {
        String xml = SldBuilder.create("l").rule()
                .fill().fillPattern("http://host/hatch.png", "image/png", 12)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GraphicFill>"));
        assertTrue(xml.contains("xlink:href=\"http://host/hatch.png\""));
    }

    @Test
    void polygonBuilder_strokeGraphicStroke_mark() {
        String xml = SldBuilder.create("l").rule()
                .fill("#AAAAAA").strokeGraphicStroke(WellKnownName.CIRCLE, "#000000", 4)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GraphicStroke>"));
        assertTrue(xml.contains("<WellKnownName>circle</WellKnownName>"));
    }

    @Test
    void polygonBuilder_geometry() {
        String xml = SldBuilder.create("l").rule()
                .fill("#AAAAAA").geometry("poly_geom")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:PropertyName>poly_geom</ogc:PropertyName>"));
    }

    // ── LineBuilder — new features ────────────────────────────────────────

    @Test
    void stroke_strokeOpacity() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).strokeOpacity(0.6)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-opacity\">0.6</CssParameter>"));
    }

    @Test
    void stroke_dashOffset() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).dashArray("4 2").dashOffset(2.0)
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"stroke-dashoffset\">2</CssParameter>"));
    }

    @Test
    void stroke_perpendicularOffset() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).perpendicularOffset(5.0)
                .end().build().getSldBody();
        assertTrue(xml.contains("<PerpendicularOffset>5</PerpendicularOffset>"));
    }

    @Test
    void stroke_graphicStroke_mark() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).graphicStroke(WellKnownName.CIRCLE, "#FF0000", 6)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GraphicStroke>"));
        assertTrue(xml.contains("<WellKnownName>circle</WellKnownName>"));
    }

    @Test
    void stroke_graphicFill_external() {
        String xml = SldBuilder.create("l").rule()
                .stroke("#000", 1).graphicFill("http://host/fill.png", "image/png", 8)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GraphicFill>"));
        assertTrue(xml.contains("xlink:href=\"http://host/fill.png\""));
    }

    // ── RasterBuilder ─────────────────────────────────────────────────────

    @Test
    void rasterBuilder_colorMap() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder()
                    .opacity(0.9)
                    .colorMap(SldColorMapType.RAMP,
                            SldColorMapEntry.of("#0000FF", 0, "Low"),
                            SldColorMapEntry.of("#FF0000", 100, "High"))
                .end().build().getSldBody();
        assertTrue(xml.contains("<RasterSymbolizer>"));
        assertTrue(xml.contains("<Opacity>0.9</Opacity>"));
        assertTrue(xml.contains("<ColorMap type=\"ramp\">"));
        assertTrue(xml.contains("color=\"#FF0000\""));
    }

    @Test
    void rasterBuilder_channelGray() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().channelGray("1")
                .end().build().getSldBody();
        assertTrue(xml.contains("<GrayChannel>"));
        assertTrue(xml.contains("<SourceChannelName>1</SourceChannelName>"));
    }

    @Test
    void rasterBuilder_channelGray_withContrastMethod() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().channelGray("1", ContrastMethod.NORMALIZE)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Normalize/>"));
    }

    @Test
    void rasterBuilder_channelGray_withGamma() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().channelGray("1", 1.5)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GammaValue>1.5</GammaValue>"));
    }

    @Test
    void rasterBuilder_channelRGB() {
        String xml = SldBuilder.create("multi").rule()
                .rasterBuilder().channelRGB("3", "2", "1")
                .end().build().getSldBody();
        assertTrue(xml.contains("<RedChannel>"));
        assertTrue(xml.contains("<GreenChannel>"));
        assertTrue(xml.contains("<BlueChannel>"));
    }

    @Test
    void rasterBuilder_channelRGB_withCE() {
        String xml = SldBuilder.create("multi").rule()
                .rasterBuilder().channelRGB(
                        "3", ContrastMethod.HISTOGRAM,
                        "2", ContrastMethod.NORMALIZE,
                        "1", ContrastMethod.HISTOGRAM)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Histogram/>"));
        assertTrue(xml.contains("<Normalize/>"));
    }

    @Test
    void rasterBuilder_globalContrastEnhancement() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().contrastEnhancement(ContrastMethod.NORMALIZE)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ContrastEnhancement>"));
        assertTrue(xml.contains("<Normalize/>"));
    }

    @Test
    void rasterBuilder_gammaValue() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().gammaValue(2.0)
                .end().build().getSldBody();
        assertTrue(xml.contains("<GammaValue>2</GammaValue>"));
    }

    @Test
    void rasterBuilder_overlapBehavior() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().overlapBehavior(OverlapBehavior.LATEST_ON_TOP)
                .end().build().getSldBody();
        assertTrue(xml.contains("<OverlapBehavior>"));
        assertTrue(xml.contains("<LATEST_ON_TOP/>"));
    }

    @Test
    void rasterBuilder_colorMapExtended() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder()
                    .colorMapExtended(SldColorMapType.RAMP, SldColorMapEntry.of("#000000", 0, "Z"))
                .end().build().getSldBody();
        assertTrue(xml.contains("extended=\"true\""));
    }

    @Test
    void rasterBuilder_shadedRelief() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().shadedRelief(55, false)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ShadedRelief>"));
        assertTrue(xml.contains("<ReliefFactor>55</ReliefFactor>"));
        assertTrue(xml.contains("<BrightnessOnly>false</BrightnessOnly>"));
    }

    @Test
    void rasterBuilder_imageOutlineLine() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().imageOutlineLine("#000000", 1)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ImageOutline>"));
        assertTrue(xml.contains("<LineSymbolizer>"));
    }

    @Test
    void rasterBuilder_imageOutlinePolygon() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().imageOutlinePolygon("#CCCCCC", "#000000", 1)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ImageOutline>"));
        assertTrue(xml.contains("<PolygonSymbolizer>"));
    }

    @Test
    void rasterBuilder_geometry() {
        String xml = SldBuilder.create("dem").rule()
                .rasterBuilder().geometry("raster_geom")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:PropertyName>raster_geom</ogc:PropertyName>"));
    }

    // ── UserStyle.Name (styleName) ────────────────────────────────────────

    @Test
    void styleName_emitsNameInsideUserStyle() {
        String xml = SldBuilder.create("roads")
                .styleName("my-point-style")
                .rule().polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<UserStyle>"));
        assertTrue(xml.contains("<Name>my-point-style</Name>"));
    }

    // ── Multiple UserStyle ────────────────────────────────────────────────

    @Test
    void multipleUserStyle_twoStylesEmitted() {
        String xml = SldBuilder.create("roads")
                .title("Style A")
                .rule("r1").polygon("#336699")
                .userStyle("highlight")
                    .rule("r2").polygon("#FF0000")
                .build().getSldBody();
        // Both UserStyle blocks present
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<UserStyle>", idx)) != -1) { count++; idx++; }
        assertEquals(2, count);
        assertTrue(xml.contains("<Name>highlight</Name>"));
        assertTrue(xml.contains("Style A"));
    }

    @Test
    void userStyleNavigation_fromRuleBuilder() {
        String xml = SldBuilder.create("roads")
                .rule("r1").polygon("#336699")
                .userStyle()
                    .rule("r2").polygon("#FF0000")
                .build().getSldBody();
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<UserStyle>", idx)) != -1) { count++; idx++; }
        assertEquals(2, count);
    }

    @Test
    void ruleBuilder_end_returnsParentSldBuilder() {
        // .end() on RuleBuilder should return SldBuilder for further configuration
        String xml = SldBuilder.create("roads")
                .rule("r1").polygon("#336699").end()
                .title("my-title")
                .build().getSldBody();
        assertTrue(xml.contains("<Title>my-title</Title>"));
    }

    // ── NamedStyle reference ──────────────────────────────────────────────

    @Test
    void namedStyle_emitsNamedStyleElement() {
        String xml = SldBuilder.create("roads")
                .namedStyle("point")
                .buildForNamedStyle().getSldBody();
        assertTrue(xml.contains("<NamedLayer>"));
        assertTrue(xml.contains("<NamedStyle>"));
        assertTrue(xml.contains("<Name>point</Name>"));
        assertFalse(xml.contains("<UserStyle>"));
    }

    @Test
    void namedStyle_multipleRefs() {
        String xml = SldBuilder.create("roads")
                .namedStyle("style-a")
                .namedStyle("style-b")
                .buildForNamedStyle().getSldBody();
        assertTrue(xml.contains("style-a"));
        assertTrue(xml.contains("style-b"));
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<NamedStyle>", idx)) != -1) { count++; idx++; }
        assertEquals(2, count);
    }

    @Test
    void buildForNamedStyle_throwsWhenNoNamedStyleAdded() {
        assertThrows(IllegalStateException.class, () ->
                SldBuilder.create("roads").buildForNamedStyle());
    }

    // ── SldExpression arithmetic ──────────────────────────────────────────

    @Test
    void expression_add() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE)
                    .fill("#FF0000")
                    .size(SldExpression.add(property("width"), literal(2)))
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Add>"));
        assertTrue(xml.contains("<ogc:PropertyName>width</ogc:PropertyName>"));
        assertTrue(xml.contains("<ogc:Literal>2</ogc:Literal>"));
        assertTrue(xml.contains("</ogc:Add>"));
    }

    @Test
    void expression_sub() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE)
                    .fill("#FF0000")
                    .size(SldExpression.sub(property("width"), literal(1)))
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Sub>"));
    }

    @Test
    void expression_mul() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE)
                    .fill("#FF0000")
                    .size(SldExpression.mul(property("pop"), literal(0.001)))
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Mul>"));
        assertTrue(xml.contains("<ogc:PropertyName>pop</ogc:PropertyName>"));
    }

    @Test
    void expression_div() {
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.CIRCLE)
                    .fill("#FF0000")
                    .size(SldExpression.div(property("area"), literal(1000)))
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Div>"));
    }

    @Test
    void expression_nestedArithmetic() {
        // (a + b) * 2
        SldExpression expr = SldExpression.mul(
                SldExpression.add(property("a"), property("b")),
                literal(2));
        String xml = SldBuilder.create("l").rule()
                .mark(WellKnownName.SQUARE).fill("#FF0000").size(expr)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Mul>"));
        assertTrue(xml.contains("<ogc:Add>"));
    }

    // ── OgcFilters.gmlObjectId ────────────────────────────────────────────

    @Test
    void gmlObjectId_singleId() {
        String xml = SldBuilder.create("roads")
                .rule().filter(gmlObjectId("roads.1"))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<ogc:GmlObjectId gml:id=\"roads.1\"/>"));
    }

    @Test
    void gmlObjectId_multipleIds() {
        String xml = SldBuilder.create("roads")
                .rule().filter(gmlObjectId("roads.1", "roads.2", "roads.3"))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("gml:id=\"roads.1\""));
        assertTrue(xml.contains("gml:id=\"roads.2\""));
        assertTrue(xml.contains("gml:id=\"roads.3\""));
    }

    // ── SemanticTypeIdentifier ────────────────────────────────────────────

    @Test
    void ftsSemanticTypeIdentifier_emitted() {
        String xml = SldBuilder.create("roads")
                .ftsSemanticTypeIdentifier("generic:line")
                .rule().line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<SemanticTypeIdentifier>generic:line</SemanticTypeIdentifier>"));
    }

    @Test
    void ftsSemanticTypeIdentifier_perFts() {
        String xml = SldBuilder.create("roads")
                .ftsSemanticTypeIdentifier("generic:polygon")
                .rule().polygon("#CCCCCC")
                .featureTypeStyle("labels")
                    .ftsSemanticTypeIdentifier("generic:text")
                    .rule().text("name", "#000000", "Arial", 10)
                .build().getSldBody();
        assertTrue(xml.contains("<SemanticTypeIdentifier>generic:polygon</SemanticTypeIdentifier>"));
        assertTrue(xml.contains("<SemanticTypeIdentifier>generic:text</SemanticTypeIdentifier>"));
    }

    // ── LayerFeatureConstraints ───────────────────────────────────────────

    @Test
    void layerConstraint_noFilter() {
        String xml = SldBuilder.create("roads")
                .layerConstraint("sf:roads", null)
                .rule().line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<LayerFeatureConstraints>"));
        assertTrue(xml.contains("<FeatureTypeConstraint>"));
        assertTrue(xml.contains("<FeatureTypeName>sf:roads</FeatureTypeName>"));
        assertFalse(xml.contains("<ogc:Filter>"));
    }

    @Test
    void layerConstraint_withFilter() {
        String xml = SldBuilder.create("roads")
                .layerConstraint("sf:roads", equalTo("type", "highway"))
                .rule().line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<LayerFeatureConstraints>"));
        assertTrue(xml.contains("<ogc:Filter>"));
        assertTrue(xml.contains("<ogc:PropertyIsEqualTo>"));
    }

    @Test
    void layerConstraint_multiple() {
        String xml = SldBuilder.create("roads")
                .layerConstraint("sf:roads", equalTo("type", "highway"))
                .layerConstraint("sf:rails", null)
                .rule().line("#336699", 2)
                .build().getSldBody();
        int count = 0;
        int idx = 0;
        while ((idx = xml.indexOf("<FeatureTypeConstraint>", idx)) != -1) { count++; idx++; }
        assertEquals(2, count);
    }

    // ── UserLayer ─────────────────────────────────────────────────────────

    @Test
    void createUserLayer_emitsUserLayerTag() {
        String xml = SldBuilder.createUserLayer("inline-data")
                .rule().polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<UserLayer>"));
        assertTrue(xml.contains("</UserLayer>"));
        assertFalse(xml.contains("<NamedLayer>"));
    }

    @Test
    void createUserLayer_withRemoteOws() {
        String xml = SldBuilder.createUserLayer("wfs-data")
                .remoteOws("WFS", "http://example.org/wfs")
                .rule().polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<UserLayer>"));
        assertTrue(xml.contains("<RemoteOWS>"));
        assertTrue(xml.contains("<Service>WFS</Service>"));
        assertTrue(xml.contains("xlink:href=\"http://example.org/wfs\""));
    }

    @Test
    void createUserLayer_withLayerConstraintAndStyle() {
        String xml = SldBuilder.createUserLayer("constrained")
                .layerConstraint("sf:mytype", greaterThan("pop", 1000))
                .rule().polygon("#FF0000")
                .build().getSldBody();
        assertTrue(xml.contains("<UserLayer>"));
        assertTrue(xml.contains("<LayerFeatureConstraints>"));
        assertTrue(xml.contains("<UserStyle>"));
    }

    // ── TextBuilder.label(SldExpression) ─────────────────────────────────

    @Test
    void label_expression_function() {
        String xml = SldBuilder.create("l").rule()
                .label(SldExpression.function("strConcat",
                        property("name"),
                        literal(" ("),
                        property("type"),
                        literal(")")))
                    .color("#000000").font("Arial", 12)
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Function name=\"strConcat\">"));
        assertTrue(xml.contains("<ogc:PropertyName>name</ogc:PropertyName>"));
        assertTrue(xml.contains("<ogc:Literal> (</ogc:Literal>"));
    }

    @Test
    void label_expression_arithmetic() {
        String xml = SldBuilder.create("l").rule()
                .label(SldExpression.add(property("prefix"), property("suffix")))
                    .color("#000000")
                .end().build().getSldBody();
        assertTrue(xml.contains("<ogc:Add>"));
        assertTrue(xml.contains("<TextSymbolizer>"));
    }

    // ── SldGeometry multi-types ───────────────────────────────────────────

    @Test
    void geometry_multiPoint() {
        String xml = SldBuilder.create("l").rule()
                .filter(intersects("geom", SldGeometry.multiPoint("EPSG:4326", 0, 0, 1, 1)))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<gml:MultiPoint"));
        assertTrue(xml.contains("<gml:pointMember>"));
        assertTrue(xml.contains("srsName=\"EPSG:4326\""));
    }

    @Test
    void geometry_multiPoint_noSrs() {
        String xml = SldBuilder.create("l").rule()
                .filter(intersects("geom", SldGeometry.multiPoint(0, 0, 5, 5)))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<gml:MultiPoint>"));
        assertTrue(xml.contains("0,0"));
        assertTrue(xml.contains("5,5"));
    }

    @Test
    void geometry_multiLineString() {
        String xml = SldBuilder.create("l").rule()
                .filter(intersects("geom", SldGeometry.multiLineString(
                        new double[]{0, 0, 1, 1},
                        new double[]{2, 2, 3, 3})))
                .line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<gml:MultiLineString>"));
        assertTrue(xml.contains("<gml:lineStringMember>"));
        assertTrue(xml.contains("0,0 1,1"));
        assertTrue(xml.contains("2,2 3,3"));
    }

    @Test
    void geometry_multiLineString_withSrs() {
        String xml = SldBuilder.create("l").rule()
                .filter(within("geom", SldGeometry.multiLineString("EPSG:4326",
                        new double[]{0, 0, 1, 1})))
                .line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<gml:MultiLineString srsName=\"EPSG:4326\">"));
    }

    @Test
    void geometry_multiPolygon() {
        String xml = SldBuilder.create("l").rule()
                .filter(within("geom", SldGeometry.multiPolygon(
                        new double[]{0, 0, 1, 0, 1, 1, 0, 1, 0, 0},
                        new double[]{2, 2, 3, 2, 3, 3, 2, 3, 2, 2})))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<gml:MultiPolygon>"));
        assertTrue(xml.contains("<gml:polygonMember>"));
        assertTrue(xml.contains("<gml:LinearRing>"));
    }

    @Test
    void geometry_multiPolygon_withSrs() {
        String xml = SldBuilder.create("l").rule()
                .filter(within("geom", SldGeometry.multiPolygon("EPSG:4326",
                        new double[]{0, 0, 1, 0, 1, 1, 0, 0})))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("srsName=\"EPSG:4326\""));
    }

    @Test
    void geometry_geometryCollection() {
        String xml = SldBuilder.create("l").rule()
                .filter(intersects("geom", SldGeometry.geometryCollection(
                        SldGeometry.point(0, 0),
                        SldGeometry.lineString(0, 0, 1, 1))))
                .polygon("#336699")
                .build().getSldBody();
        assertTrue(xml.contains("<gml:GeometryCollection>"));
        assertTrue(xml.contains("<gml:geometryMember>"));
        assertTrue(xml.contains("<gml:Point>"));
        assertTrue(xml.contains("<gml:LineString>"));
    }

    // ── FeatureTypeConstraint.Extent ──────────────────────────────────────

    @Test
    void layerConstraintExtent_emitsExtentElement() {
        String xml = SldBuilder.create("roads")
                .layerConstraint("sf:roads", null, -180, -90, 180, 90)
                .rule().line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<Extent>"));
        assertTrue(xml.contains("<Name>BBOX</Name>"));
        assertTrue(xml.contains("<Value>-180,-90,180,90</Value>"));
    }

    @Test
    void layerConstraintExtent_withFilter() {
        String xml = SldBuilder.create("roads")
                .layerConstraint("sf:roads", equalTo("type", "highway"), 0, 0, 100, 100)
                .rule().line("#336699", 2)
                .build().getSldBody();
        assertTrue(xml.contains("<Extent>"));
        assertTrue(xml.contains("<Value>0,0,100,100</Value>"));
        assertTrue(xml.contains("<ogc:Filter>"));
    }

    // ── Multiple font families ────────────────────────────────────────────

    @Test
    void fontFamilies_singleFamily() {
        String xml = SldBuilder.create("l").rule()
                .label("name").fontFamilies("Arial").color("#000000")
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"font-family\">Arial</CssParameter>"));
    }

    @Test
    void fontFamilies_multipleFamilies() {
        String xml = SldBuilder.create("l").rule()
                .label("name")
                    .fontFamilies("Arial", "Helvetica", "SansSerif")
                    .color("#000000")
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"font-family\">Arial</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"font-family\">Helvetica</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"font-family\">SansSerif</CssParameter>"));
    }

    @Test
    void fontFamilies_preservesOtherFontSettings() {
        String xml = SldBuilder.create("l").rule()
                .label("name")
                    .fontFamilies("Arial", "Helvetica")
                    .bold().italic()
                    .font("Arial", 14)  // overrides first family + size
                    .color("#000000")
                .end().build().getSldBody();
        assertTrue(xml.contains("<CssParameter name=\"font-weight\">bold</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"font-style\">italic</CssParameter>"));
        assertTrue(xml.contains("<CssParameter name=\"font-size\">14</CssParameter>"));
    }

    // ── Gap fixes (1.1.2) ────────────────────────────────────────────────

    @Test
    void polygonBuilder_opacity() {
        String xml = SldBuilder.create("l").rule()
                .fill("#336699").fillOpacity(0.5).stroke("#000000", 1).opacity(0.7)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Opacity>0.7</Opacity>"));
        assertTrue(xml.contains("<PolygonSymbolizer>"));
    }

    @Test
    void textBuilder_priority_literal() {
        String xml = SldBuilder.create("l").rule()
                .label("name")
                .color("#000000").font("SansSerif", 12)
                .priority(100.0)
                .end().build().getSldBody();
        assertTrue(xml.contains("<Priority><ogc:Literal>100.0</ogc:Literal></Priority>"));
    }

    @Test
    void textBuilder_priority_expression() {
        String xml = SldBuilder.create("l").rule()
                .label("name")
                .color("#000000").font("SansSerif", 12)
                .priority(SldExpression.property("pop"))
                .end().build().getSldBody();
        assertTrue(xml.contains("<Priority><ogc:PropertyName>pop</ogc:PropertyName></Priority>"));
    }

    @Test
    void textBuilder_linePlacement_repeated() {
        String xml = SldBuilder.create("l").rule()
                .label("name")
                .color("#000000").font("SansSerif", 12)
                .linePlacement(0).linePlacementRepeated(true, 400, 200).linePlacementAligned(true)
                .end().build().getSldBody();
        assertTrue(xml.contains("<IsRepeated>true</IsRepeated>"));
        assertTrue(xml.contains("<Gap>400</Gap>"));
        assertTrue(xml.contains("<InitialGap>200</InitialGap>"));
        assertTrue(xml.contains("<IsAligned>true</IsAligned>"));
    }

    @Test
    void rasterBuilder_vendorOption() {
        String xml = SldBuilder.create("l").rule()
                .rasterBuilder()
                .opacity(1.0)
                .channelGray("1")
                .vendorOption("algorithm", "StretchToMinimumMaximum")
                .end().build().getSldBody();
        assertTrue(xml.contains("<VendorOption name=\"algorithm\">StretchToMinimumMaximum</VendorOption>"));
        assertTrue(xml.contains("<RasterSymbolizer>"));
    }

    // ── Helper ───────────────────────────────────────────────────────────

    private static String build(SldBuilder.RuleBuilder rb) {
        return rb.build().getSldBody();
    }
}
