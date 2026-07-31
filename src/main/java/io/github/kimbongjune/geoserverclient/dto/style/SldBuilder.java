package io.github.kimbongjune.geoserverclient.dto.style;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for SLD 1.0 (Styled Layer Descriptor) style documents.
 *
 * <p>Mirrors the scope of GeoTools {@code StyleBuilder} / {@code StyleFactory} without
 * requiring any GeoTools dependency. All output is well-formed SLD 1.0 XML serialized
 * into a {@link StyleContent} object ready for upload via
 * {@code StyleManager.create(String, StyleContent)}.
 *
 * <h2>Symbolizers supported</h2>
 * <ul>
 *   <li><b>Point</b> — well-known marks ({@link WellKnownName}), external graphic images,
 *       and external marks (TTF font glyphs); fill, stroke, size, rotation, opacity</li>
 *   <li><b>Line</b> — stroke colour, width, opacity, dash pattern, dash offset, line-cap,
 *       line-join, perpendicular offset, graphic fill, graphic stroke</li>
 *   <li><b>Polygon</b> — solid fill with opacity, graphic fill pattern, outline stroke
 *       (all stroke options), graphic stroke on outline</li>
 *   <li><b>Text</b> — font family (with fallback list), size, bold, italic, oblique; halo
 *       radius and colour; point placement (anchor, displacement, rotation) or line
 *       placement (perpendicular offset); GeoServer vendor options</li>
 *   <li><b>Raster</b> — opacity, channel selection (gray / RGB with per-channel contrast
 *       enhancement and gamma), global contrast enhancement, overlap behaviour, colour map
 *       (ramp / intervals / values, extended), shaded relief, image outline</li>
 * </ul>
 *
 * <h2>SLD document structure</h2>
 * <ul>
 *   <li>Root: {@code <StyledLayerDescriptor version="1.0.0">}</li>
 *   <li>Exactly one layer per builder: {@code <NamedLayer>} ({@link #create}) or
 *       {@code <UserLayer>} ({@link #createUserLayer})</li>
 *   <li>One or more {@code <UserStyle>} blocks ({@link #userStyle()})</li>
 *   <li>Optional {@code <NamedStyle>} references ({@link #namedStyle(String)})</li>
 *   <li>One or more {@code <FeatureTypeStyle>} blocks per UserStyle
 *       ({@link #featureTypeStyle()})</li>
 *   <li>One or more {@code <Rule>} blocks per FeatureTypeStyle ({@link #rule(String)})</li>
 *   <li>Optional {@code <LayerFeatureConstraints>} ({@link #layerConstraint})</li>
 * </ul>
 *
 * <h2>Full fluent example — all symbolizer types</h2>
 * <pre>{@code
 * import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
 * import static io.github.kimbongjune.geoserverclient.dto.style.SldExpression.*;
 *
 * StyleContent sld = SldBuilder.create("my_layer")
 *     .styleName("full-demo")
 *     .title("Full symbolizer demo")
 *     .isDefault(true)
 *
 *     // ── FeatureTypeStyle 1: point features ──
 *     .featureTypeStyle("points")
 *     .rule("cities-large").filter(greaterThan("population", 1_000_000))
 *         .mark(WellKnownName.CIRCLE)
 *             .fill("#FF4444", 0.9)
 *             .stroke("#880000", 1.5)
 *             .size(property("population_rank"))   // proportional symbol
 *             .rotation(45)
 *         .end()
 *         .label("name")
 *             .font("Arial", 14).bold()
 *             .color("#FFFFFF")
 *             .halo("#000000", 2)
 *             .vendorOption("maxDisplacement", "40")
 *             .vendorOption("conflictResolution", "true")
 *         .end()
 *     .rule("cities-small").elseFilter()
 *         .point(WellKnownName.CIRCLE, "#AAAAFF", 6)
 *
 *     // ── FeatureTypeStyle 2: line features ──
 *     .featureTypeStyle("lines")
 *     .rule("highways").filter(equalTo("road_class", "highway"))
 *         .stroke("#CC0000", 4)
 *             .dashArray("10 4")
 *             .lineCap(LineCap.ROUND)
 *             .lineJoin(LineJoin.ROUND)
 *         .end()
 *         .textOnLine("name", "#333333", "Arial", 11, 6)
 *
 *     // ── FeatureTypeStyle 3: polygon features ──
 *     .featureTypeStyle("polygons")
 *     .rule("parks").filter(equalTo("landuse", "park"))
 *         .fill("#336633")
 *             .fillOpacity(0.75)
 *             .stroke("#114411", 1)
 *         .end()
 *
 *     // ── FeatureTypeStyle 4: raster DEM ──
 *     .featureTypeStyle("dem")
 *     .rule("dem-rule")
 *         .rasterBuilder()
 *             .opacity(1.0)
 *             .channelGray("1", ContrastMethod.NORMALIZE)
 *             .colorMap(SldColorMapType.RAMP,
 *                 SldColorMapEntry.of("#0000FF", 0,   "Low"),
 *                 SldColorMapEntry.of("#00FF00", 500,  "Mid"),
 *                 SldColorMapEntry.of("#FF0000", 1000, "High"))
 *             .shadedRelief(55, false)
 *         .end()
 *
 *     .build();
 * }</pre>
 *
 * @see OgcFilters
 * @see SldExpression
 * @see SldGeometry
 * @see RuleBuilder
 * @see PointBuilder
 * @see LineBuilder
 * @see PolygonBuilder
 * @see TextBuilder
 * @see RasterBuilder
 * @since 1.0.0
 */
public final class SldBuilder {

    private final String layerName;
    private final boolean isUserLayer;

    // UserLayer only
    private String remoteOwsService;
    private String remoteOwsUrl;

    // NamedLayer / UserLayer shared: zero or more UserStyle blocks + NamedStyle refs
    private final List<UserStyleState> userStyles = new ArrayList<>();
    private final List<String> namedStyleRefs = new ArrayList<>();

    // LayerFeatureConstraints (NamedLayer or UserLayer)
    private final List<FeatureTypeConstraint> layerConstraints = new ArrayList<>();

    private SldBuilder(String layerName, boolean isUserLayer) {
        this.layerName   = layerName;
        this.isUserLayer = isUserLayer;
        userStyles.add(new UserStyleState());
    }

    // ── State holders ─────────────────────────────────────────────────────

    private static final class UserStyleState {
        String name;
        String title;
        String abstract_;
        boolean isDefault;
        final List<FtsState> featureTypeStyles = new ArrayList<>();

        UserStyleState() {
            featureTypeStyles.add(new FtsState());
        }

        FtsState currentFts() {
            return featureTypeStyles.get(featureTypeStyles.size() - 1);
        }
    }

    static final class FtsState {
        String name;
        String title;
        String abstract_;
        String featureTypeName;
        String semanticTypeIdentifier;
        final List<RuleBuilder> rules = new ArrayList<>();
    }

    private static final class FeatureTypeConstraint {
        final String featureTypeName;
        final OgcFilter filter;
        Double extMinX;
        Double extMinY;
        Double extMaxX;
        Double extMaxY;

        FeatureTypeConstraint(String ftn, OgcFilter filter) {
            this.featureTypeName = ftn;
            this.filter = filter;
        }
    }

    private UserStyleState currentUserStyle() {
        return userStyles.get(userStyles.size() - 1);
    }

    FtsState currentFts() {
        return currentUserStyle().currentFts();
    }

    // ── Static factories ──────────────────────────────────────────────────

    /**
     * Creates a new {@link SldBuilder} for a {@code <NamedLayer>}.
     *
     * <p>A {@code <NamedLayer>} references an existing layer published in GeoServer by name.
     * This is the most common starting point for styles uploaded via
     * {@code StyleManager.create()} or {@code StyleManager.update()}.
     *
     * <pre>{@code
     * StyleContent sld = SldBuilder.create("topp:tasmania_roads")
     *     .rule("all").line("#CC0000", 2).build();
     * }</pre>
     *
     * @param layerName the GeoServer layer name (may include workspace prefix, e.g.
     *                  {@code "topp:tasmania_roads"}); must not be {@code null} or empty
     * @return a new {@link SldBuilder} targeting a {@code <NamedLayer>}
     * @throws IllegalArgumentException if {@code layerName} is {@code null} or empty
     */
    public static SldBuilder create(String layerName) {
        if (layerName == null || layerName.isEmpty())
            throw new IllegalArgumentException("layerName must not be empty");
        return new SldBuilder(layerName, false);
    }

    /**
     * Creates a new {@link SldBuilder} for a {@code <UserLayer>}.
     *
     * <p>A {@code <UserLayer>} is used when styling inline or remote data not yet published
     * as a named GeoServer layer. Attach a remote WFS or WCS data source with
     * {@link #remoteOws(String, String)}. The resulting SLD is typically submitted directly
     * as part of a WMS {@code SLD} parameter rather than stored as a named style.
     *
     * <pre>{@code
     * StyleContent sld = SldBuilder.createUserLayer("mydata")
     *     .remoteOws("WFS", "http://example.com/geoserver/wfs")
     *     .layerConstraint("sf:archsites", null)
     *     .rule("all").point(WellKnownName.CIRCLE, "#FF0000", 8).build();
     * }</pre>
     *
     * @param name the user layer name; must not be {@code null} or empty
     * @return a new {@link SldBuilder} targeting a {@code <UserLayer>}
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty
     */
    public static SldBuilder createUserLayer(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("name must not be empty");
        return new SldBuilder(name, true);
    }

    // ── UserStyle settings (current UserStyle) ────────────────────────────

    /**
     * Sets the {@code <Name>} element of the current {@code <UserStyle>}.
     *
     * <p>This name identifies the style variant within the layer — it is separate from the
     * layer name set in {@link #create(String)}. GeoServer uses it when the client requests a
     * specific named style via WMS {@code STYLES} parameter.
     *
     * @param name the style name; must not be {@code null}
     * @return this builder for chaining
     */
    public SldBuilder styleName(String name) {
        currentUserStyle().name = name; return this;
    }

    /**
     * Sets the human-readable {@code <Title>} of the current {@code <UserStyle>}.
     *
     * <p>The title appears in legend displays and GetCapabilities responses.
     *
     * @param title a short, human-readable title; must not be {@code null}
     * @return this builder for chaining
     */
    public SldBuilder title(String title) {
        currentUserStyle().title = title; return this;
    }

    /**
     * Sets the {@code <Abstract>} description of the current {@code <UserStyle>}.
     *
     * <p>The abstract provides additional documentation about the style's purpose or usage.
     *
     * @param text a longer description; must not be {@code null}
     * @return this builder for chaining
     */
    public SldBuilder abstract_(String text) {
        currentUserStyle().abstract_ = text; return this;
    }

    /**
     * Controls whether the current {@code <UserStyle>} is marked as the default style for
     * the layer ({@code <IsDefault>1</IsDefault>} in the SLD output).
     *
     * <p>GeoServer renders the default style when the client does not explicitly request a
     * named style. Exactly one UserStyle per layer should be marked as default.
     *
     * @param value {@code true} to mark as default; {@code false} (the factory default) otherwise
     * @return this builder for chaining
     */
    public SldBuilder isDefault(boolean value) {
        currentUserStyle().isDefault = value; return this;
    }

    /**
     * Starts a new anonymous {@code <UserStyle>} block within the same layer.
     *
     * <p>All subsequent {@link #rule()} calls target this new UserStyle block. Multiple
     * UserStyles within a single layer allow the WMS client to select which variant to render
     * using the {@code STYLES} parameter.
     *
     * @return this builder for chaining (now targeting the new UserStyle)
     */
    public SldBuilder userStyle() {
        userStyles.add(new UserStyleState()); return this;
    }

    /**
     * Starts a new named {@code <UserStyle>} block within the same layer.
     *
     * <p>Equivalent to {@link #userStyle()} followed immediately by
     * {@link #styleName(String)}.
     *
     * @param name the {@code <Name>} of the new UserStyle block
     * @return this builder for chaining (now targeting the new UserStyle)
     */
    public SldBuilder userStyle(String name) {
        UserStyleState s = new UserStyleState();
        s.name = name;
        userStyles.add(s);
        return this;
    }

    /**
     * Adds a {@code <NamedStyle>} reference to the layer.
     *
     * <p>A {@code <NamedStyle>} refers to an existing server-side style by name without
     * embedding any rules. Use {@link #buildForNamedStyle()} to finalize the SLD when
     * only NamedStyle references are present (no inline rules).
     *
     * <pre>{@code
     * StyleContent sld = SldBuilder.create("roads")
     *     .namedStyle("point")
     *     .buildForNamedStyle();
     * }</pre>
     *
     * @param styleName the server-side style name to reference; must not be {@code null}
     * @return this builder for chaining
     */
    public SldBuilder namedStyle(String styleName) {
        namedStyleRefs.add(styleName); return this;
    }

    // ── FeatureTypeStyle settings ─────────────────────────────────────────

    /**
     * Starts a new anonymous {@code <FeatureTypeStyle>} block within the current UserStyle.
     *
     * <p>Subsequent {@link #rule()} calls add rules to this new block. Multiple
     * {@code <FeatureTypeStyle>} blocks are rendered in document order, which controls
     * the z-order of drawn features. For example, placing a road-casing FTS before a
     * road-fill FTS achieves an outline effect.
     *
     * @return this builder for chaining (now targeting the new FeatureTypeStyle)
     */
    public SldBuilder featureTypeStyle() {
        currentUserStyle().featureTypeStyles.add(new FtsState()); return this;
    }

    /**
     * Starts a new named {@code <FeatureTypeStyle>} block within the current UserStyle.
     *
     * @param name the {@code <Name>} of the new FeatureTypeStyle block
     * @return this builder for chaining (now targeting the new FeatureTypeStyle)
     */
    public SldBuilder featureTypeStyle(String name) {
        FtsState fts = new FtsState(); fts.name = name;
        currentUserStyle().featureTypeStyles.add(fts); return this;
    }

    /**
     * Sets the human-readable {@code <Title>} of the current {@code <FeatureTypeStyle>}.
     *
     * @param title a short descriptive title
     * @return this builder for chaining
     */
    public SldBuilder ftsTitle(String title) {
        currentFts().title = title; return this;
    }

    /**
     * Sets the {@code <Abstract>} description of the current {@code <FeatureTypeStyle>}.
     *
     * @param text a longer description
     * @return this builder for chaining
     */
    public SldBuilder ftsAbstract(String text) {
        currentFts().abstract_ = text; return this;
    }

    /**
     * Sets the {@code <FeatureTypeName>} constraint on the current {@code <FeatureTypeStyle>}.
     *
     * <p>When set, this FeatureTypeStyle block is applied only to features of the specified
     * type. Useful when a layer contains multiple feature types.
     *
     * @param name the feature type name (e.g. {@code "sf:archsites"})
     * @return this builder for chaining
     */
    public SldBuilder ftsFeatureTypeName(String name) {
        currentFts().featureTypeName = name; return this;
    }

    /**
     * Sets a {@code <SemanticTypeIdentifier>} on the current {@code <FeatureTypeStyle>}.
     *
     * <p>The semantic type identifier is a hint to the renderer about what geometry type the
     * FTS applies to. Common values:
     * <ul>
     *   <li>{@code "generic:geometry"} — any geometry</li>
     *   <li>{@code "generic:point"} — point geometries</li>
     *   <li>{@code "generic:line"} — line/linestring geometries</li>
     *   <li>{@code "generic:polygon"} — polygon geometries</li>
     *   <li>{@code "generic:raster"} — raster/coverage data</li>
     * </ul>
     *
     * @param identifier the semantic type identifier string
     * @return this builder for chaining
     */
    public SldBuilder ftsSemanticTypeIdentifier(String identifier) {
        currentFts().semanticTypeIdentifier = identifier; return this;
    }

    // ── LayerFeatureConstraints ───────────────────────────────────────────

    /**
     * Adds a {@code <FeatureTypeConstraint>} to the layer's {@code <LayerFeatureConstraints>}.
     * This constrains which features the style applies to.
     *
     * @param featureTypeName the feature type name (may be null for wildcard)
     * @param filter          the OGC filter to apply (may be null for no filter)
     * @return this builder for chaining
     */
    public SldBuilder layerConstraint(String featureTypeName, OgcFilter filter) {
        layerConstraints.add(new FeatureTypeConstraint(featureTypeName, filter)); return this;
    }

    /**
     * Adds a {@code <FeatureTypeConstraint>} with a spatial {@code <Extent>} (BBOX) constraint.
     * The {@code <Extent>} element limits rendering to features within the given bounding box.
     *
     * @param featureTypeName the feature type name (may be null)
     * @param filter          the OGC filter (may be null)
     * @param minX            western extent
     * @param minY            southern extent
     * @param maxX            eastern extent
     * @param maxY            northern extent
     * @return this builder for chaining
     */
    public SldBuilder layerConstraint(String featureTypeName, OgcFilter filter,
                                       double minX, double minY, double maxX, double maxY) {
        FeatureTypeConstraint lfc = new FeatureTypeConstraint(featureTypeName, filter);
        lfc.extMinX = minX; lfc.extMinY = minY; lfc.extMaxX = maxX; lfc.extMaxY = maxY;
        layerConstraints.add(lfc);
        return this;
    }

    // ── UserLayer: RemoteOWS ──────────────────────────────────────────────

    /**
     * Attaches a remote OWS data source to a UserLayer.
     *
     * @param service  OWS service type, e.g. {@code "WFS"} or {@code "WCS"}
     * @param url      service URL
     * @return this builder for chaining
     */
    public SldBuilder remoteOws(String service, String url) {
        this.remoteOwsService = service;
        this.remoteOwsUrl     = url;
        return this;
    }

    // ── Rules ─────────────────────────────────────────────────────────────

    /**
     * Starts a new auto-named {@code <Rule>} in the current {@code <FeatureTypeStyle>}.
     *
     * <p>The auto-generated name is {@code "rule"} followed by the one-based index of the
     * rule within the current FTS (e.g. {@code "rule1"}, {@code "rule2"}).
     *
     * @return a new {@link RuleBuilder} for the auto-named rule
     */
    public RuleBuilder rule() {
        return rule("rule" + (currentFts().rules.size() + 1));
    }

    /**
     * Starts a new named {@code <Rule>} in the current {@code <FeatureTypeStyle>}.
     *
     * <p>Rule names appear in legend labels and are referenced by some GeoServer features
     * (e.g. legend icons). Use descriptive names like {@code "highways"} or {@code "parks"}.
     *
     * @param name the rule name; must not be {@code null} or empty
     * @return a new {@link RuleBuilder} for the named rule
     */
    public RuleBuilder rule(String name) {
        RuleBuilder r = new RuleBuilder(this, name);
        currentFts().rules.add(r);
        return r;
    }

    // ── Build ─────────────────────────────────────────────────────────────

    /**
     * Builds and returns the complete SLD 1.0 document as a {@link StyleContent}.
     *
     * <p>At least one rule must have been added to at least one UserStyle before calling
     * this method. Use {@link #buildForNamedStyle()} instead when only {@link #namedStyle}
     * references were added.
     *
     * @return a {@link StyleContent} containing the serialized SLD XML, ready for upload
     *         via {@code StyleManager.create()} or {@code StyleManager.update()}
     * @throws IllegalStateException if no rules were added and no NamedStyle references exist
     */
    public StyleContent build() {
        if (namedStyleRefs.isEmpty()) {
            boolean hasRules = false;
            outer:
            for (UserStyleState us : userStyles)
                for (FtsState fts : us.featureTypeStyles)
                    if (!fts.rules.isEmpty()) { hasRules = true; break outer; }
            if (!hasRules) {
                throw new IllegalStateException("SldBuilder requires at least one rule (or use namedStyle)");
            }
        }
        return StyleContent.of(toXml());
    }

    /**
     * Builds the SLD document for a layer that contains only {@code <NamedStyle>} references
     * (no inline rules).
     *
     * <p>Use this variant when all style references were added via {@link #namedStyle(String)}
     * and no rules were added via {@link #rule(String)}. Calling {@link #build()} in this
     * scenario would throw because there are no inline rules; this method skips that check.
     *
     * @return a {@link StyleContent} containing the serialized SLD XML
     * @throws IllegalStateException if no NamedStyle references have been added
     */
    public StyleContent buildForNamedStyle() {
        if (namedStyleRefs.isEmpty())
            throw new IllegalStateException("No namedStyle references added — use namedStyle(String) first");
        return StyleContent.of(toXml());
    }

    private String toXml() {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
          .append("<StyledLayerDescriptor version=\"1.0.0\"\n")
          .append("    xmlns=\"http://www.opengis.net/sld\"\n")
          .append("    xmlns:ogc=\"http://www.opengis.net/ogc\"\n")
          .append("    xmlns:gml=\"http://www.opengis.net/gml\"\n")
          .append("    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n")
          .append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

        String layerTag = isUserLayer ? "UserLayer" : "NamedLayer";
        sb.append("  <").append(layerTag).append(">\n")
          .append("    <Name>").append(esc(layerName)).append("</Name>\n");

        // UserLayer: RemoteOWS
        if (isUserLayer && remoteOwsService != null) {
            sb.append("    <RemoteOWS>\n")
              .append("      <Service>").append(esc(remoteOwsService)).append("</Service>\n")
              .append("      <OnlineResource xlink:type=\"simple\" xlink:href=\"")
              .append(esc(remoteOwsUrl)).append("\"/>\n")
              .append("    </RemoteOWS>\n");
        }

        // LayerFeatureConstraints
        if (!layerConstraints.isEmpty()) {
            sb.append("    <LayerFeatureConstraints>\n");
            for (FeatureTypeConstraint lfc : layerConstraints) {
                sb.append("      <FeatureTypeConstraint>\n");
                if (lfc.featureTypeName != null)
                    sb.append("        <FeatureTypeName>").append(esc(lfc.featureTypeName)).append("</FeatureTypeName>\n");
                if (lfc.filter != null) {
                    sb.append("        <ogc:Filter>\n");
                    lfc.filter.appendXml(sb, "          ");
                    sb.append("        </ogc:Filter>\n");
                }
                if (lfc.extMinX != null) {
                    sb.append("        <Extent>\n")
                      .append("          <Name>BBOX</Name>\n")
                      .append("          <Value>")
                      .append(fmt(lfc.extMinX)).append(",")
                      .append(fmt(lfc.extMinY)).append(",")
                      .append(fmt(lfc.extMaxX)).append(",")
                      .append(fmt(lfc.extMaxY))
                      .append("</Value>\n")
                      .append("        </Extent>\n");
                }
                sb.append("      </FeatureTypeConstraint>\n");
            }
            sb.append("    </LayerFeatureConstraints>\n");
        }

        // NamedStyle references
        for (String nsName : namedStyleRefs) {
            sb.append("    <NamedStyle>\n")
              .append("      <Name>").append(esc(nsName)).append("</Name>\n")
              .append("    </NamedStyle>\n");
        }

        // UserStyle blocks (only emit those with at least one rule, or if no rules and only one style)
        for (UserStyleState us : userStyles) {
            boolean hasRules = false;
            for (FtsState fts : us.featureTypeStyles)
                if (!fts.rules.isEmpty()) { hasRules = true; break; }
            if (!hasRules) {
                continue;
            }

            sb.append("    <UserStyle>\n");
            if (us.name    != null) {
                sb.append("      <Name>").append(esc(us.name)).append("</Name>\n");
            }
            if (us.title   != null) {
                sb.append("      <Title>").append(esc(us.title)).append("</Title>\n");
            }
            if (us.abstract_ != null) {
                sb.append("      <Abstract>").append(esc(us.abstract_)).append("</Abstract>\n");
            }
            if (us.isDefault) {
                sb.append("      <IsDefault>1</IsDefault>\n");
            }

            for (FtsState fts : us.featureTypeStyles) {
                if (fts.rules.isEmpty()) {
                    continue;
                }
                sb.append("      <FeatureTypeStyle>\n");
                if (fts.name != null) {
                    sb.append("        <Name>").append(esc(fts.name)).append("</Name>\n");
                }
                if (fts.title != null) {
                    sb.append("        <Title>").append(esc(fts.title)).append("</Title>\n");
                }
                if (fts.abstract_ != null) {
                    sb.append("        <Abstract>").append(esc(fts.abstract_)).append("</Abstract>\n");
                }
                if (fts.featureTypeName != null)
                    sb.append("        <FeatureTypeName>").append(esc(fts.featureTypeName)).append("</FeatureTypeName>\n");
                if (fts.semanticTypeIdentifier != null)
                    sb.append("        <SemanticTypeIdentifier>").append(esc(fts.semanticTypeIdentifier)).append("</SemanticTypeIdentifier>\n");
                for (RuleBuilder r : fts.rules) {
                    r.appendXml(sb);
                }
                sb.append("      </FeatureTypeStyle>\n");
            }

            sb.append("    </UserStyle>\n");
        }

        sb.append("  </").append(layerTag).append(">\n")
          .append("</StyledLayerDescriptor>");
        return sb.toString();
    }

    /**
     * XML-escapes a string for safe inclusion in element content or attribute values.
     *
     * <p>Replaces the five XML metacharacters: {@code &} → {@code &amp;},
     * {@code <} → {@code &lt;}, {@code >} → {@code &gt;},
     * {@code "} → {@code &quot;}.
     *
     * @param s the string to escape; must not be {@code null}
     * @return the XML-escaped string
     */
    static String esc(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }

    /**
     * Formats a {@code double} value for embedding in XML.
     *
     * <p>Whole-number values (e.g. {@code 6.0}) are emitted without a decimal point
     * ({@code "6"}) to produce cleaner SLD output. Non-integer values are formatted by
     * {@link Double#toString(double)}. {@code Infinity} and {@code NaN} are emitted as-is
     * (they are invalid in SLD contexts — validate inputs before calling).
     *
     * @param v the value to format
     * @return the string representation of {@code v}
     */
    static String fmt(double v) {
        return (v == Math.floor(v) && !Double.isInfinite(v)) ? String.valueOf((long) v) : String.valueOf(v);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RuleBuilder
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Fluent builder for an SLD {@code <Rule>} element.
     *
     * <p>Obtained via {@link SldBuilder#rule(String)} or {@link SldBuilder#rule()}. Each rule
     * specifies an optional {@link OgcFilter} (or {@link #elseFilter()}), optional scale
     * denominators, and one or more symbolizers.
     *
     * <h2>Symbolizer methods</h2>
     * <p>Two styles of symbolizer factory exist on this class:
     * <ol>
     *   <li><b>Simple one-liners</b> — return {@code RuleBuilder} directly for chaining:
     *       {@link #point(WellKnownName, String, double)},
     *       {@link #line(String, double)},
     *       {@link #polygon(String, String, double)},
     *       {@link #text(String, String, String, double)},
     *       {@link #raster(double, SldColorMapType, SldColorMapEntry...)}
     *   </li>
     *   <li><b>Sub-builder variants</b> — return a specialized builder for full options;
     *       call {@code end()} to return to this rule:
     *       {@link #mark(WellKnownName)} → {@link PointBuilder},
     *       {@link #externalPoint(String, String)} → {@link PointBuilder},
     *       {@link #stroke(String, double)} → {@link LineBuilder},
     *       {@link #fill(String)} → {@link PolygonBuilder},
     *       {@link #label(String)} → {@link TextBuilder},
     *       {@link #rasterBuilder()} → {@link RasterBuilder}
     *   </li>
     * </ol>
     *
     * <h2>Navigation</h2>
     * <p>From a {@code RuleBuilder} you can navigate to:
     * <ul>
     *   <li>{@link #rule()} / {@link #rule(String)} — next rule in same FTS</li>
     *   <li>{@link #featureTypeStyle()} — new FTS block</li>
     *   <li>{@link #userStyle()} — new UserStyle block</li>
     *   <li>{@link #end()} — back to the parent {@link SldBuilder}</li>
     *   <li>{@link #build()} — finalize and return {@link StyleContent}</li>
     * </ul>
     *
     * @since 1.0.0
     */
    public static final class RuleBuilder {

        private final SldBuilder parent;
        private final String name;
        private String ruleTitle;
        private String ruleAbstract;
        private String legendGraphicXml;
        private OgcFilter filter;
        private boolean elseFilter;
        private Double minScale, maxScale;
        final List<String> symbolizerXmls = new ArrayList<>();

        private RuleBuilder(SldBuilder parent, String name) {
            this.parent = parent;
            this.name   = name;
        }

        // ── Rule settings ─────────────────────────────────────────────────

        /** Human-readable title for this rule (shown in map legend). */
        public RuleBuilder title(String title) {
            this.ruleTitle = title; return this;
        }

        /** Optional abstract / description for this rule. */
        public RuleBuilder abstract_(String text) {
            this.ruleAbstract = text; return this;
        }

        /** Legend graphic — well-known mark. */
        public RuleBuilder legendGraphicMark(WellKnownName mark, String color, double size) {
            legendGraphicXml = buildLegendMarkXml(mark, color, size);
            return this;
        }

        /** Legend graphic — external image. */
        public RuleBuilder legendGraphicExternal(String url, String format, double size) {
            legendGraphicXml = buildLegendExternalXml(url, format, size);
            return this;
        }

        /** Attaches an OGC filter. Create filters via {@link OgcFilters}. */
        public RuleBuilder filter(OgcFilter filter) {
            this.filter = filter; return this;
        }

        /**
         * Marks this rule as an else-filter — it catches all features not matched
         * by any other rule in the same FeatureTypeStyle.
         * Mutually exclusive with {@link #filter}.
         */
        public RuleBuilder elseFilter() {
            this.elseFilter = true; return this;
        }

        /** Show only when zoomed in beyond this scale denominator. */
        public RuleBuilder minScale(double denominator) {
            this.minScale = denominator; return this;
        }

        /** Show only when zoomed out beyond this scale denominator. */
        public RuleBuilder maxScale(double denominator) {
            this.maxScale = denominator; return this;
        }

        // ── Simple symbolizers (return RuleBuilder) ───────────────────────

        /** Point: well-known mark with solid fill, no stroke. */
        public RuleBuilder point(WellKnownName mark, String fillColor, double size) {
            return point(mark, fillColor, 1.0, null, 0, size);
        }

        /** Point: well-known mark with fill opacity and optional stroke. */
        public RuleBuilder point(WellKnownName mark, String fillColor, double fillOpacity,
                                  String strokeColor, double strokeWidth, double size) {
            symbolizerXmls.add(buildMarkPointXml(mark, null, null, -1,
                    fillColor, fillOpacity, strokeColor, strokeWidth, size, null, 0, null, 1.0, null));
            return this;
        }

        /** Point: external graphic (image file). */
        public RuleBuilder pointExternal(String url, String format, double size) {
            symbolizerXmls.add(buildExternalGraphicPointXml(url, format, size, null, 0, null, 1.0, null));
            return this;
        }

        /** Line: solid stroke. */
        public RuleBuilder line(String strokeColor, double strokeWidth) {
            return line(strokeColor, strokeWidth, null);
        }

        /** Line: stroke with optional SVG dash pattern, e.g. {@code "4 2"}. */
        public RuleBuilder line(String strokeColor, double strokeWidth, String dashArray) {
            symbolizerXmls.add(buildLineXml(strokeColor, strokeWidth, dashArray));
            return this;
        }

        /** Polygon: fill only (no outline). */
        public RuleBuilder polygon(String fillColor) {
            return polygon(fillColor, 1.0, null, 0);
        }

        /** Polygon: fill and stroke. */
        public RuleBuilder polygon(String fillColor, String strokeColor, double strokeWidth) {
            return polygon(fillColor, 1.0, strokeColor, strokeWidth);
        }

        /** Polygon: fill with opacity and optional stroke. */
        public RuleBuilder polygon(String fillColor, double fillOpacity,
                                    String strokeColor, double strokeWidth) {
            symbolizerXmls.add(buildPolygonXml(fillColor, fillOpacity, strokeColor, strokeWidth));
            return this;
        }

        /** Text: attribute label, point placement, no halo. */
        public RuleBuilder text(String labelAttribute, String color,
                                 String fontFamily, double fontSize) {
            symbolizerXmls.add(buildTextXml(labelAttribute, false, color,
                    fontFamily, fontSize, "normal", "normal",
                    null, 0, false, 0.5, 0.5, 0, 0, 0, 0, null, null));
            return this;
        }

        /** Text: attribute label with halo, point placement. */
        public RuleBuilder text(String labelAttribute, String color, String fontFamily,
                                 double fontSize, String haloColor, double haloRadius) {
            symbolizerXmls.add(buildTextXml(labelAttribute, false, color,
                    fontFamily, fontSize, "normal", "normal",
                    haloColor, haloRadius, false, 0.5, 0.5, 0, 0, 0, 0, null, null));
            return this;
        }

        /** Text: literal (static) label, point placement. */
        public RuleBuilder staticText(String label, String color, String fontFamily, double fontSize) {
            symbolizerXmls.add(buildTextXml(label, true, color,
                    fontFamily, fontSize, "normal", "normal",
                    null, 0, false, 0.5, 0.5, 0, 0, 0, 0, null, null));
            return this;
        }

        /** Text: attribute label on a line feature (LinePlacement). */
        public RuleBuilder textOnLine(String labelAttribute, String color, String fontFamily,
                                       double fontSize, double perpendicularOffset) {
            symbolizerXmls.add(buildTextXml(labelAttribute, false, color,
                    fontFamily, fontSize, "normal", "normal",
                    null, 0, true, 0, 0, 0, 0, 0, perpendicularOffset, null, null));
            return this;
        }

        /** Text: attribute label on a line feature with halo. */
        public RuleBuilder textOnLine(String labelAttribute, String color, String fontFamily,
                                       double fontSize, String haloColor, double haloRadius,
                                       double perpendicularOffset) {
            symbolizerXmls.add(buildTextXml(labelAttribute, false, color,
                    fontFamily, fontSize, "normal", "normal",
                    haloColor, haloRadius, true, 0, 0, 0, 0, 0, perpendicularOffset, null, null));
            return this;
        }

        /** Raster: colour map symbolizer. */
        public RuleBuilder raster(double opacity, SldColorMapType type, SldColorMapEntry... entries) {
            symbolizerXmls.add(buildRasterXml(opacity, type, entries));
            return this;
        }

        // ── Sub-builder symbolizers ───────────────────────────────────────

        /**
         * Starts a {@link PointBuilder} for a well-known mark.
         * Call {@link PointBuilder#end()} to return to this rule.
         */
        public PointBuilder mark(WellKnownName markName) {
            return new PointBuilder(this, markName);
        }

        /**
         * Starts a {@link PointBuilder} for an external graphic (image file).
         * Call {@link PointBuilder#end()} to return to this rule.
         */
        public PointBuilder externalPoint(String url, String format) {
            return new PointBuilder(this, url, format);
        }

        /**
         * Starts a {@link PointBuilder} for an external mark (e.g. a TTF glyph).
         * Call {@link PointBuilder#end()} to return to this rule.
         *
         * @param markIndex glyph index within the font file
         */
        public PointBuilder externalMark(String url, String format, int markIndex) {
            return new PointBuilder(this, url, format, markIndex);
        }

        /**
         * Starts a {@link LineBuilder} for a full-featured line symbolizer.
         * Call {@link LineBuilder#end()} to return to this rule.
         */
        public LineBuilder stroke(String strokeColor, double strokeWidth) {
            return new LineBuilder(this, strokeColor, strokeWidth);
        }

        /**
         * Starts a {@link PolygonBuilder} with an initial fill colour.
         * Call {@link PolygonBuilder#end()} to return to this rule.
         */
        public PolygonBuilder fill(String fillColor) {
            return new PolygonBuilder(this, fillColor);
        }

        /**
         * Starts a {@link PolygonBuilder} without a solid fill colour
         * (use when you only need a graphic fill pattern or stroke).
         * Call {@link PolygonBuilder#end()} to return to this rule.
         */
        public PolygonBuilder fill() {
            return new PolygonBuilder(this, null);
        }

        /**
         * Starts a {@link RasterBuilder} for a full-featured raster symbolizer.
         * Call {@link RasterBuilder#end()} to return to this rule.
         */
        public RasterBuilder rasterBuilder() {
            return new RasterBuilder(this);
        }

        /**
         * Starts a {@link TextBuilder} for an attribute-based label.
         * Call {@link TextBuilder#end()} to return to this rule.
         */
        public TextBuilder label(String labelAttribute) {
            return new TextBuilder(this, labelAttribute, false);
        }

        /**
         * Starts a {@link TextBuilder} for a label driven by an OGC expression
         * (e.g. an OGC function such as {@code strConcat}, {@code numberFormat}).
         * Call {@link TextBuilder#end()} to return to this rule.
         *
         * <pre>{@code
         * .label(SldExpression.function("strConcat",
         *         SldExpression.property("name"),
         *         SldExpression.literal(" ("),
         *         SldExpression.property("type"),
         *         SldExpression.literal(")")))
         * }</pre>
         */
        public TextBuilder label(SldExpression labelExpr) {
            return new TextBuilder(this, labelExpr);
        }

        /**
         * Starts a {@link TextBuilder} for a literal (static) label.
         * Call {@link TextBuilder#end()} to return to this rule.
         */
        public TextBuilder staticLabel(String text) {
            return new TextBuilder(this, text, true);
        }

        // ── Navigation ────────────────────────────────────────────────────

        /** Ends this rule and starts a new auto-named rule in the same FeatureTypeStyle. */
        public RuleBuilder rule() {
            return parent.rule();
        }

        /** Ends this rule and starts a new named rule in the same FeatureTypeStyle. */
        public RuleBuilder rule(String name) {
            return parent.rule(name);
        }

        /** Ends this rule and starts a new anonymous FeatureTypeStyle block. */
        public SldBuilder featureTypeStyle() {
            return parent.featureTypeStyle();
        }

        /** Ends this rule and starts a new named FeatureTypeStyle block. */
        public SldBuilder featureTypeStyle(String name) {
            return parent.featureTypeStyle(name);
        }

        /** Ends this rule and starts a new anonymous UserStyle block. */
        public SldBuilder userStyle() {
            return parent.userStyle();
        }

        /** Ends this rule and starts a new named UserStyle block. */
        public SldBuilder userStyle(String name) {
            return parent.userStyle(name);
        }

        /** Returns the parent {@link SldBuilder} to continue layer-level configuration. */
        public SldBuilder end() {
            return parent;
        }

        /** Finalizes all rules and returns the built {@link StyleContent}. */
        public StyleContent build() {
            return parent.build();
        }

        // ── XML ───────────────────────────────────────────────────────────

        void appendXml(StringBuilder sb) {
            sb.append("        <Rule>\n");
            if (name != null && !name.isEmpty())
                sb.append("          <Name>").append(esc(name)).append("</Name>\n");
            if (ruleTitle != null)
                sb.append("          <Title>").append(esc(ruleTitle)).append("</Title>\n");
            if (ruleAbstract != null)
                sb.append("          <Abstract>").append(esc(ruleAbstract)).append("</Abstract>\n");
            if (legendGraphicXml != null)
                sb.append(legendGraphicXml);
            if (elseFilter) {
                sb.append("          <ElseFilter/>\n");
            } else if (filter != null) {
                sb.append("          <ogc:Filter>\n");
                filter.appendXml(sb, "            ");
                sb.append("          </ogc:Filter>\n");
            }
            if (minScale != null)
                sb.append("          <MinScaleDenominator>").append(fmt(minScale)).append("</MinScaleDenominator>\n");
            if (maxScale != null)
                sb.append("          <MaxScaleDenominator>").append(fmt(maxScale)).append("</MaxScaleDenominator>\n");
            for (String xml : symbolizerXmls) {
                sb.append(xml);
            }
            sb.append("        </Rule>\n");
        }

        // ── Private XML builders ──────────────────────────────────────────

        static String buildMarkPointXml(WellKnownName mark,
                                         String extUrl, String extFormat, int extMarkIndex,
                                         String fillColor, double fillOpacity,
                                         String strokeColor, double strokeWidth,
                                         double size, SldExpression sizeExpr,
                                         double rotation, SldExpression rotExpr,
                                         double opacity, String geometryProperty) {
            StringBuilder sb = new StringBuilder();
            if (geometryProperty != null)
                sb.append("          <Geometry><ogc:PropertyName>")
                  .append(esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("          <PointSymbolizer>\n")
              .append("            <Graphic>\n");
            sb.append("              <Mark>\n");
            if (mark != null) {
                sb.append("                <WellKnownName>").append(mark.getValue()).append("</WellKnownName>\n");
            } else {
                sb.append("                <ExternalMark>\n")
                  .append("                  <OnlineResource xlink:type=\"simple\" xlink:href=\"")
                  .append(esc(extUrl)).append("\"/>\n")
                  .append("                  <Format>").append(esc(extFormat)).append("</Format>\n");
                if (extMarkIndex >= 0)
                    sb.append("                  <MarkIndex>").append(extMarkIndex).append("</MarkIndex>\n");
                sb.append("                </ExternalMark>\n");
            }
            if (fillColor != null) {
                sb.append("                <Fill>\n")
                  .append("                  <CssParameter name=\"fill\">").append(esc(fillColor)).append("</CssParameter>\n");
                if (fillOpacity != 1.0)
                    sb.append("                  <CssParameter name=\"fill-opacity\">").append(fmt(fillOpacity)).append("</CssParameter>\n");
                sb.append("                </Fill>\n");
            }
            if (strokeColor != null) {
                sb.append("                <Stroke>\n")
                  .append("                  <CssParameter name=\"stroke\">").append(esc(strokeColor)).append("</CssParameter>\n")
                  .append("                  <CssParameter name=\"stroke-width\">").append(fmt(strokeWidth)).append("</CssParameter>\n")
                  .append("                </Stroke>\n");
            }
            sb.append("              </Mark>\n");
            if (opacity != 1.0)
                sb.append("              <Opacity>").append(fmt(opacity)).append("</Opacity>\n");
            if (sizeExpr != null) {
                sb.append("              <Size>"); sizeExpr.appendXml(sb); sb.append("</Size>\n");
            } else {
                sb.append("              <Size>").append(fmt(size)).append("</Size>\n");
            }
            if (rotExpr != null) {
                sb.append("              <Rotation>"); rotExpr.appendXml(sb); sb.append("</Rotation>\n");
            } else if (rotation != 0) {
                sb.append("              <Rotation>").append(fmt(rotation)).append("</Rotation>\n");
            }
            sb.append("            </Graphic>\n")
              .append("          </PointSymbolizer>\n");
            return sb.toString();
        }

        static String buildExternalGraphicPointXml(String url, String format,
                                                     double size, SldExpression sizeExpr,
                                                     double rotation, SldExpression rotExpr,
                                                     double opacity, String geometryProperty) {
            StringBuilder sb = new StringBuilder();
            if (geometryProperty != null)
                sb.append("          <Geometry><ogc:PropertyName>")
                  .append(esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("          <PointSymbolizer>\n")
              .append("            <Graphic>\n")
              .append("              <ExternalGraphic>\n")
              .append("                <OnlineResource xlink:type=\"simple\" xlink:href=\"")
              .append(esc(url)).append("\"/>\n")
              .append("                <Format>").append(esc(format)).append("</Format>\n")
              .append("              </ExternalGraphic>\n");
            if (opacity != 1.0)
                sb.append("              <Opacity>").append(fmt(opacity)).append("</Opacity>\n");
            if (sizeExpr != null) {
                sb.append("              <Size>"); sizeExpr.appendXml(sb); sb.append("</Size>\n");
            } else {
                sb.append("              <Size>").append(fmt(size)).append("</Size>\n");
            }
            if (rotExpr != null) {
                sb.append("              <Rotation>"); rotExpr.appendXml(sb); sb.append("</Rotation>\n");
            } else if (rotation != 0) {
                sb.append("              <Rotation>").append(fmt(rotation)).append("</Rotation>\n");
            }
            sb.append("            </Graphic>\n")
              .append("          </PointSymbolizer>\n");
            return sb.toString();
        }

        static String buildLineXml(String strokeColor, double strokeWidth, String dashArray) {
            return buildLineXmlFull(strokeColor, strokeWidth, dashArray, null, null, 1.0, null);
        }

        static String buildLineXmlFull(String strokeColor, double strokeWidth, String dashArray,
                                        LineCap lineCap, LineJoin lineJoin,
                                        double opacity, String geometryProperty) {
            StringBuilder sb = new StringBuilder();
            sb.append("          <LineSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("            <Stroke>\n")
              .append("              <CssParameter name=\"stroke\">").append(esc(strokeColor)).append("</CssParameter>\n")
              .append("              <CssParameter name=\"stroke-width\">").append(fmt(strokeWidth)).append("</CssParameter>\n");
            if (dashArray != null)
                sb.append("              <CssParameter name=\"stroke-dasharray\">").append(esc(dashArray)).append("</CssParameter>\n");
            if (lineCap != null)
                sb.append("              <CssParameter name=\"stroke-linecap\">").append(lineCap.getValue()).append("</CssParameter>\n");
            if (lineJoin != null)
                sb.append("              <CssParameter name=\"stroke-linejoin\">").append(lineJoin.getValue()).append("</CssParameter>\n");
            sb.append("            </Stroke>\n");
            if (opacity != 1.0)
                sb.append("            <Opacity>").append(fmt(opacity)).append("</Opacity>\n");
            sb.append("          </LineSymbolizer>\n");
            return sb.toString();
        }

        static String buildPolygonXml(String fillColor, double fillOpacity,
                                       String strokeColor, double strokeWidth) {
            StringBuilder sb = new StringBuilder();
            sb.append("          <PolygonSymbolizer>\n");
            if (fillColor != null) {
                sb.append("            <Fill>\n")
                  .append("              <CssParameter name=\"fill\">").append(esc(fillColor)).append("</CssParameter>\n");
                if (fillOpacity != 1.0)
                    sb.append("              <CssParameter name=\"fill-opacity\">").append(fmt(fillOpacity)).append("</CssParameter>\n");
                sb.append("            </Fill>\n");
            }
            if (strokeColor != null) {
                sb.append("            <Stroke>\n")
                  .append("              <CssParameter name=\"stroke\">").append(esc(strokeColor)).append("</CssParameter>\n")
                  .append("              <CssParameter name=\"stroke-width\">").append(fmt(strokeWidth)).append("</CssParameter>\n")
                  .append("            </Stroke>\n");
            }
            sb.append("          </PolygonSymbolizer>\n");
            return sb.toString();
        }

        static String buildLegendMarkXml(WellKnownName mark, String color, double size) {
            return "          <LegendGraphic>\n            <Graphic>\n              <Mark>\n" +
                   "                <WellKnownName>" + mark.getValue() + "</WellKnownName>\n" +
                   "                <Fill><CssParameter name=\"fill\">" + esc(color) + "</CssParameter></Fill>\n" +
                   "              </Mark>\n              <Size>" + fmt(size) + "</Size>\n" +
                   "            </Graphic>\n          </LegendGraphic>\n";
        }

        static String buildLegendExternalXml(String url, String format, double size) {
            return "          <LegendGraphic>\n            <Graphic>\n" +
                   "              <ExternalGraphic>\n" +
                   "                <OnlineResource xlink:type=\"simple\" xlink:href=\"" + esc(url) + "\"/>\n" +
                   "                <Format>" + esc(format) + "</Format>\n" +
                   "              </ExternalGraphic>\n              <Size>" + fmt(size) + "</Size>\n" +
                   "            </Graphic>\n          </LegendGraphic>\n";
        }

        static String buildTextXml(String label, boolean isLiteral,
                                    String color, String fontFamily, double fontSize,
                                    String fontStyle, String fontWeight,
                                    String haloColor, double haloRadius,
                                    boolean linePlacement,
                                    double anchorX, double anchorY,
                                    double dispX, double dispY, double rotation,
                                    double perpOffset,
                                    String geometryProperty,
                                    Map<String, String> vendorOptions) {
            StringBuilder sb = new StringBuilder();
            sb.append("          <TextSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("            <Label>\n");
            if (isLiteral)
                sb.append("              <ogc:Literal>").append(esc(label)).append("</ogc:Literal>\n");
            else
                sb.append("              <ogc:PropertyName>").append(esc(label)).append("</ogc:PropertyName>\n");
            sb.append("            </Label>\n")
              .append("            <Font>\n")
              .append("              <CssParameter name=\"font-family\">").append(esc(fontFamily)).append("</CssParameter>\n")
              .append("              <CssParameter name=\"font-size\">").append(fmt(fontSize)).append("</CssParameter>\n")
              .append("              <CssParameter name=\"font-style\">").append(fontStyle).append("</CssParameter>\n")
              .append("              <CssParameter name=\"font-weight\">").append(fontWeight).append("</CssParameter>\n")
              .append("            </Font>\n")
              .append("            <LabelPlacement>\n");
            if (linePlacement) {
                sb.append("              <LinePlacement>\n")
                  .append("                <PerpendicularOffset>").append(fmt(perpOffset)).append("</PerpendicularOffset>\n")
                  .append("              </LinePlacement>\n");
            } else {
                sb.append("              <PointPlacement>\n")
                  .append("                <AnchorPoint>\n")
                  .append("                  <AnchorPointX>").append(fmt(anchorX)).append("</AnchorPointX>\n")
                  .append("                  <AnchorPointY>").append(fmt(anchorY)).append("</AnchorPointY>\n")
                  .append("                </AnchorPoint>\n");
                if (dispX != 0 || dispY != 0)
                    sb.append("                <Displacement>\n")
                      .append("                  <DisplacementX>").append(fmt(dispX)).append("</DisplacementX>\n")
                      .append("                  <DisplacementY>").append(fmt(dispY)).append("</DisplacementY>\n")
                      .append("                </Displacement>\n");
                if (rotation != 0)
                    sb.append("                <Rotation>").append(fmt(rotation)).append("</Rotation>\n");
                sb.append("              </PointPlacement>\n");
            }
            sb.append("            </LabelPlacement>\n");
            if (haloColor != null)
                sb.append("            <Halo>\n")
                  .append("              <Radius>").append(fmt(haloRadius)).append("</Radius>\n")
                  .append("              <Fill>\n")
                  .append("                <CssParameter name=\"fill\">").append(esc(haloColor)).append("</CssParameter>\n")
                  .append("              </Fill>\n")
                  .append("            </Halo>\n");
            sb.append("            <Fill>\n")
              .append("              <CssParameter name=\"fill\">").append(esc(color)).append("</CssParameter>\n")
              .append("            </Fill>\n");
            if (vendorOptions != null)
                for (Map.Entry<String, String> e : vendorOptions.entrySet())
                    sb.append("            <VendorOption name=\"").append(esc(e.getKey())).append("\">")
                      .append(esc(e.getValue())).append("</VendorOption>\n");
            sb.append("          </TextSymbolizer>\n");
            return sb.toString();
        }

        static String buildRasterXml(double opacity, SldColorMapType type, SldColorMapEntry[] entries) {
            StringBuilder sb = new StringBuilder();
            sb.append("          <RasterSymbolizer>\n")
              .append("            <Opacity>").append(fmt(opacity)).append("</Opacity>\n")
              .append("            <ColorMap type=\"").append(type.getValue()).append("\">\n");
            for (SldColorMapEntry e : entries) {
                sb.append("              <ColorMapEntry color=\"").append(esc(e.getColor())).append("\"")
                  .append(" quantity=\"").append(fmt(e.getQuantity())).append("\"");
                if (e.getLabel() != null) {
                    sb.append(" label=\"").append(esc(e.getLabel())).append("\"");
                }
                if (e.getOpacity() != 1.0) {
                    sb.append(" opacity=\"").append(fmt(e.getOpacity())).append("\"");
                }
                sb.append("/>\n");
            }
            sb.append("            </ColorMap>\n")
              .append("          </RasterSymbolizer>\n");
            return sb.toString();
        }

        private static String esc(String s) {
            return SldBuilder.esc(s);
        }
        private static String fmt(double v) {
            return SldBuilder.fmt(v);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PointBuilder  (mirrors GeoTools Graphic + Mark / ExternalGraphic)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Full-featured builder for a {@code <PointSymbolizer>}.
     *
     * <p>Supports three graphic source types:
     * <ul>
     *   <li><b>Well-known mark</b> — obtained via {@link RuleBuilder#mark(WellKnownName)};
     *       fill and stroke are configurable</li>
     *   <li><b>External graphic</b> — obtained via {@link RuleBuilder#externalPoint(String, String)};
     *       references an image file (PNG, GIF, SVG) by URL</li>
     *   <li><b>External mark</b> (TTF glyph) — obtained via
     *       {@link RuleBuilder#externalMark(String, String, int)}; references a font file
     *       glyph by URL, format, and mark index; fill and stroke are configurable</li>
     * </ul>
     *
     * <p>Call {@link #end()} to finalize the symbolizer and return to the parent
     * {@link RuleBuilder}.
     *
     * <pre>{@code
     * rule.mark(WellKnownName.CIRCLE)
     *     .fill("#FF0000", 0.8)
     *     .stroke("#880000", 1.5)
     *     .size(property("magnitude"))   // attribute-driven size
     *     .rotation(45)
     *     .opacity(0.9)
     *     .end();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class PointBuilder {

        private final RuleBuilder parent;

        // source type
        private final WellKnownName markName;   // non-null → WellKnownName mark
        private final String extUrl;            // non-null → ExternalGraphic or ExternalMark
        private final String extFormat;
        private final int extMarkIndex;         // ≥ 0 → ExternalMark; -1 → ExternalGraphic
        private final boolean isExternalMark;

        // style
        private String fillColor;
        private double fillOpacity    = 1.0;
        private String strokeColor;
        private double strokeWidth    = 1.0;
        private Double strokeOpacity;
        private LineCap strokeLineCap;
        private LineJoin strokeLineJoin;
        private String strokeDashArray;
        private Double strokeDashOffset;
        private double size           = 6.0;
        private SldExpression sizeExpr;
        private double rotation       = 0.0;
        private SldExpression rotExpr;
        private double opacity        = 1.0;
        private String geometryProperty;

        /** Well-known mark. */
        PointBuilder(RuleBuilder parent, WellKnownName markName) {
            this.parent = parent; this.markName = markName;
            this.extUrl = null; this.extFormat = null;
            this.extMarkIndex = -1; this.isExternalMark = false;
        }

        /** External graphic (PNG, SVG image file). */
        PointBuilder(RuleBuilder parent, String url, String format) {
            this.parent = parent; this.markName = null;
            this.extUrl = url; this.extFormat = format;
            this.extMarkIndex = -1; this.isExternalMark = false;
        }

        /** External mark (TTF glyph, SVG symbol). */
        PointBuilder(RuleBuilder parent, String url, String format, int markIndex) {
            this.parent = parent; this.markName = null;
            this.extUrl = url; this.extFormat = format;
            this.extMarkIndex = markIndex; this.isExternalMark = true;
        }

        /** Fill colour for the mark. */
        public PointBuilder fill(String color) {
            this.fillColor = color; return this;
        }

        /** Fill colour with opacity. */
        public PointBuilder fill(String color, double opacity) {
            this.fillColor = color; this.fillOpacity = opacity; return this;
        }

        /** Stroke colour and width for the mark outline. */
        public PointBuilder stroke(String color, double width) {
            this.strokeColor = color; this.strokeWidth = width; return this;
        }

        /** Stroke opacity (0.0–1.0). */
        public PointBuilder strokeOpacity(double opacity) {
            this.strokeOpacity = opacity; return this;
        }

        /** Stroke line-cap style. */
        public PointBuilder strokeLineCap(LineCap cap) {
            this.strokeLineCap = cap; return this;
        }

        /** Stroke line-join style. */
        public PointBuilder strokeLineJoin(LineJoin join) {
            this.strokeLineJoin = join; return this;
        }

        /** Stroke dash pattern, e.g. {@code "4 2"}. */
        public PointBuilder strokeDashArray(String pattern) {
            this.strokeDashArray = pattern; return this;
        }

        /** Stroke dash offset in pixels. */
        public PointBuilder strokeDashOffset(double offset) {
            this.strokeDashOffset = offset; return this;
        }

        /** Display size in pixels (constant). */
        public PointBuilder size(double size) {
            this.size = size; this.sizeExpr = null; return this;
        }

        /** Display size driven by a feature attribute or OGC function (e.g. proportional symbols). */
        public PointBuilder size(SldExpression expr) {
            this.sizeExpr = expr; return this;
        }

        /** Rotation in degrees, clockwise (constant). */
        public PointBuilder rotation(double degrees) {
            this.rotation = degrees; this.rotExpr = null; return this;
        }

        /** Rotation driven by a feature attribute or OGC function (e.g. orientation field). */
        public PointBuilder rotation(SldExpression expr) {
            this.rotExpr = expr; return this;
        }

        /** Overall graphic opacity (0.0–1.0). */
        public PointBuilder opacity(double opacity) {
            this.opacity = opacity; return this;
        }

        /** Geometry attribute to use (for multi-geometry features). */
        public PointBuilder geometry(String propertyName) {
            this.geometryProperty = propertyName; return this;
        }

        /** Finalizes this PointSymbolizer and returns to the parent {@link RuleBuilder}. */
        public RuleBuilder end() {
            if (markName != null || isExternalMark) {
                parent.symbolizerXmls.add(buildMarkXmlInline());
            } else {
                parent.symbolizerXmls.add(RuleBuilder.buildExternalGraphicPointXml(
                        extUrl, extFormat, size, sizeExpr, rotation, rotExpr, opacity, geometryProperty));
            }
            return parent;
        }

        private String buildMarkXmlInline() {
            StringBuilder sb = new StringBuilder();
            if (geometryProperty != null)
                sb.append("          <Geometry><ogc:PropertyName>")
                  .append(SldBuilder.esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("          <PointSymbolizer>\n            <Graphic>\n              <Mark>\n");
            if (markName != null) {
                sb.append("                <WellKnownName>").append(markName.getValue()).append("</WellKnownName>\n");
            } else {
                sb.append("                <ExternalMark>\n")
                  .append("                  <OnlineResource xlink:type=\"simple\" xlink:href=\"")
                  .append(SldBuilder.esc(extUrl)).append("\"/>\n")
                  .append("                  <Format>").append(SldBuilder.esc(extFormat)).append("</Format>\n");
                if (extMarkIndex >= 0)
                    sb.append("                  <MarkIndex>").append(extMarkIndex).append("</MarkIndex>\n");
                sb.append("                </ExternalMark>\n");
            }
            if (fillColor != null) {
                sb.append("                <Fill>\n")
                  .append("                  <CssParameter name=\"fill\">").append(SldBuilder.esc(fillColor)).append("</CssParameter>\n");
                if (fillOpacity != 1.0)
                    sb.append("                  <CssParameter name=\"fill-opacity\">").append(SldBuilder.fmt(fillOpacity)).append("</CssParameter>\n");
                sb.append("                </Fill>\n");
            }
            if (strokeColor != null) {
                sb.append("                <Stroke>\n")
                  .append("                  <CssParameter name=\"stroke\">").append(SldBuilder.esc(strokeColor)).append("</CssParameter>\n")
                  .append("                  <CssParameter name=\"stroke-width\">").append(SldBuilder.fmt(strokeWidth)).append("</CssParameter>\n");
                if (strokeOpacity != null)
                    sb.append("                  <CssParameter name=\"stroke-opacity\">").append(SldBuilder.fmt(strokeOpacity)).append("</CssParameter>\n");
                if (strokeLineCap != null)
                    sb.append("                  <CssParameter name=\"stroke-linecap\">").append(strokeLineCap.getValue()).append("</CssParameter>\n");
                if (strokeLineJoin != null)
                    sb.append("                  <CssParameter name=\"stroke-linejoin\">").append(strokeLineJoin.getValue()).append("</CssParameter>\n");
                if (strokeDashArray != null)
                    sb.append("                  <CssParameter name=\"stroke-dasharray\">").append(SldBuilder.esc(strokeDashArray)).append("</CssParameter>\n");
                if (strokeDashOffset != null)
                    sb.append("                  <CssParameter name=\"stroke-dashoffset\">").append(SldBuilder.fmt(strokeDashOffset)).append("</CssParameter>\n");
                sb.append("                </Stroke>\n");
            }
            sb.append("              </Mark>\n");
            if (opacity != 1.0)
                sb.append("              <Opacity>").append(SldBuilder.fmt(opacity)).append("</Opacity>\n");
            if (sizeExpr != null) {
                sb.append("              <Size>"); sizeExpr.appendXml(sb); sb.append("</Size>\n");
            } else {
                sb.append("              <Size>").append(SldBuilder.fmt(size)).append("</Size>\n");
            }
            if (rotExpr != null) {
                sb.append("              <Rotation>"); rotExpr.appendXml(sb); sb.append("</Rotation>\n");
            } else if (rotation != 0) {
                sb.append("              <Rotation>").append(SldBuilder.fmt(rotation)).append("</Rotation>\n");
            }
            sb.append("            </Graphic>\n          </PointSymbolizer>\n");
            return sb.toString();
        }

        /** Shortcut: {@link #end()} then start a new auto-named rule. */
        public RuleBuilder rule() {
            return end().rule();
        }

        /** Shortcut: {@link #end()} then start a new named rule. */
        public RuleBuilder rule(String name) {
            return end().rule(name);
        }

        /** Shortcut: {@link #end()} then build. */
        public StyleContent build() {
            return end().build();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TextBuilder  (mirrors GeoTools TextSymbolizer + Font + LabelPlacement)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Full-featured builder for a {@code <TextSymbolizer>}.
     *
     * <p>Controls every aspect of label rendering:
     * <ul>
     *   <li><b>Label content</b> — feature attribute ({@link RuleBuilder#label(String)}),
     *       OGC expression ({@link RuleBuilder#label(SldExpression)}), or static text
     *       ({@link RuleBuilder#staticLabel(String)})</li>
     *   <li><b>Font</b> — family ({@link #font(String, double)} / {@link #fontFamilies}),
     *       weight ({@link #bold()}), style ({@link #italic()}, {@link #oblique()})</li>
     *   <li><b>Halo</b> — outline around the text for readability
     *       ({@link #halo(String, double)})</li>
     *   <li><b>Placement</b> — point with anchor and displacement
     *       ({@link #pointPlacement(double, double)}) or following the line geometry
     *       ({@link #linePlacement(double)})</li>
     *   <li><b>GeoServer vendor options</b> — labelling behaviour tuning via
     *       {@link #vendorOption(String, String)}</li>
     * </ul>
     *
     * <p>Call {@link #end()} to finalize the symbolizer and return to the parent
     * {@link RuleBuilder}.
     *
     * @since 1.0.0
     */
    public static final class TextBuilder {

        private final RuleBuilder parent;
        private final String label;
        private final boolean isLiteral;

        private SldExpression labelExpr;   // non-null → use expression; null → use label/isLiteral
        private String  color          = "#000000";
        private String  fontFamily     = "SansSerif";
        private List<String> extraFontFamilies; // additional fallback font families
        private double  fontSize       = 10.0;
        private String  fontStyle      = "normal";   // normal | italic | oblique
        private String  fontWeight     = "normal";   // normal | bold
        private String  haloColor;
        private double  haloRadius     = 1.0;
        private boolean linePlacement  = false;
        private double  anchorX        = 0.5;
        private double  anchorY        = 0.5;
        private double  dispX          = 0.0;
        private double  dispY          = 0.0;
        private double  rotation       = 0.0;
        private double  perpOffset     = 0.0;
        private String  geometryProperty;
        private final LinkedHashMap<String, String> vendorOptions = new LinkedHashMap<>();

        TextBuilder(RuleBuilder parent, String label, boolean isLiteral) {
            this.parent    = parent;
            this.label     = label;
            this.isLiteral = isLiteral;
        }

        TextBuilder(RuleBuilder parent, SldExpression labelExpr) {
            this.parent    = parent;
            this.labelExpr = labelExpr;
            this.label     = null;
            this.isLiteral = false;
        }

        /** Label fill colour. */
        public TextBuilder color(String color) {
            this.color = color; return this;
        }

        /** Font family and size. */
        public TextBuilder font(String family, double size) {
            this.fontFamily = family; this.fontSize = size; return this;
        }

        /**
         * Specifies multiple font families for fallback rendering.
         * The first matching font available in the renderer is used.
         * Equivalent to multiple {@code font-family} CssParameters in the SLD {@code <Font>} element.
         *
         * <pre>{@code
         * .fontFamilies("Arial", "Helvetica", "SansSerif")
         * }</pre>
         */
        public TextBuilder fontFamilies(String... families) {
            if (families.length == 0) {
                return this;
            }
            this.fontFamily = families[0];
            if (families.length > 1) {
                this.extraFontFamilies = new ArrayList<>();
                for (int i = 1; i < families.length; i++) {
                    extraFontFamilies.add(families[i]);
                }
            }
            return this;
        }

        /** Bold font weight. */
        public TextBuilder bold() {
            this.fontWeight = "bold"; return this;
        }

        /** Italic font style. */
        public TextBuilder italic() {
            this.fontStyle = "italic"; return this;
        }

        /** Oblique font style. */
        public TextBuilder oblique() {
            this.fontStyle = "oblique"; return this;
        }

        /** Halo around the label text. */
        public TextBuilder halo(String color, double radius) {
            this.haloColor = color; this.haloRadius = radius; return this;
        }

        /**
         * Point placement with default anchor (0.5, 0.5).
         * This is the default; only call explicitly when switching back from line placement.
         */
        public TextBuilder pointPlacement() {
            this.linePlacement = false; return this;
        }

        /** Point placement with explicit anchor. */
        public TextBuilder pointPlacement(double anchorX, double anchorY) {
            this.linePlacement = false; this.anchorX = anchorX; this.anchorY = anchorY; return this;
        }

        /** Label displacement relative to anchor point (pixels). */
        public TextBuilder displacement(double dx, double dy) {
            this.dispX = dx; this.dispY = dy; return this;
        }

        /** Label rotation in degrees (clockwise). Only for point placement. */
        public TextBuilder rotation(double degrees) {
            this.rotation = degrees; return this;
        }

        /**
         * Line placement — label follows the line geometry.
         *
         * @param perpendicularOffset offset from line in pixels (0 = on the line)
         */
        public TextBuilder linePlacement(double perpendicularOffset) {
            this.linePlacement = true; this.perpOffset = perpendicularOffset; return this;
        }

        /** Geometry attribute to use (for multi-geometry features). */
        public TextBuilder geometry(String propertyName) {
            this.geometryProperty = propertyName; return this;
        }

        /**
         * Adds a GeoServer vendor option.
         *
         * <p>Common options: {@code labelObstacle}, {@code maxDisplacement},
         * {@code autoWrap}, {@code followLine}, {@code repeat}, {@code spaceAround},
         * {@code conflictResolution}, {@code group}.
         */
        public TextBuilder vendorOption(String name, String value) {
            this.vendorOptions.put(name, value); return this;
        }

        /** Finalizes this TextSymbolizer and returns to the parent {@link RuleBuilder}. */
        public RuleBuilder end() {
            parent.symbolizerXmls.add(buildTextXmlFull());
            return parent;
        }

        private String buildTextXmlFull() {
            String rawLabel;
            if (labelExpr != null) {
                StringBuilder lb = new StringBuilder();
                labelExpr.appendXml(lb);
                rawLabel = lb.toString();
            } else if (isLiteral) {
                rawLabel = "<ogc:Literal>" + SldBuilder.esc(label) + "</ogc:Literal>";
            } else {
                rawLabel = "<ogc:PropertyName>" + SldBuilder.esc(label) + "</ogc:PropertyName>";
            }
            return buildTextXmlWithRawLabel(rawLabel,
                    color, fontFamily, extraFontFamilies, fontSize, fontStyle, fontWeight,
                    haloColor, haloRadius, linePlacement,
                    anchorX, anchorY, dispX, dispY, rotation, perpOffset,
                    geometryProperty, vendorOptions.isEmpty() ? null : vendorOptions);
        }

        private static String buildTextXmlWithRawLabel(String rawLabelXml,
                String color, String fontFamily, List<String> extraFamilies, double fontSize,
                String fontStyle, String fontWeight,
                String haloColor, double haloRadius,
                boolean linePlacement,
                double anchorX, double anchorY,
                double dispX, double dispY, double rotation,
                double perpOffset,
                String geometryProperty,
                Map<String, String> vendorOptions) {
            StringBuilder sb = new StringBuilder();
            sb.append("          <TextSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(SldBuilder.esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("            <Label>\n")
              .append("              ").append(rawLabelXml).append("\n")
              .append("            </Label>\n")
              .append("            <Font>\n")
              .append("              <CssParameter name=\"font-family\">").append(SldBuilder.esc(fontFamily)).append("</CssParameter>\n");
            if (extraFamilies != null)
                for (String f : extraFamilies)
                    sb.append("              <CssParameter name=\"font-family\">").append(SldBuilder.esc(f)).append("</CssParameter>\n");
            sb.append("              <CssParameter name=\"font-size\">").append(SldBuilder.fmt(fontSize)).append("</CssParameter>\n")
              .append("              <CssParameter name=\"font-style\">").append(fontStyle).append("</CssParameter>\n")
              .append("              <CssParameter name=\"font-weight\">").append(fontWeight).append("</CssParameter>\n")
              .append("            </Font>\n")
              .append("            <LabelPlacement>\n");
            if (linePlacement) {
                sb.append("              <LinePlacement>\n")
                  .append("                <PerpendicularOffset>").append(SldBuilder.fmt(perpOffset)).append("</PerpendicularOffset>\n")
                  .append("              </LinePlacement>\n");
            } else {
                sb.append("              <PointPlacement>\n")
                  .append("                <AnchorPoint>\n")
                  .append("                  <AnchorPointX>").append(SldBuilder.fmt(anchorX)).append("</AnchorPointX>\n")
                  .append("                  <AnchorPointY>").append(SldBuilder.fmt(anchorY)).append("</AnchorPointY>\n")
                  .append("                </AnchorPoint>\n");
                if (dispX != 0 || dispY != 0)
                    sb.append("                <Displacement>\n")
                      .append("                  <DisplacementX>").append(SldBuilder.fmt(dispX)).append("</DisplacementX>\n")
                      .append("                  <DisplacementY>").append(SldBuilder.fmt(dispY)).append("</DisplacementY>\n")
                      .append("                </Displacement>\n");
                if (rotation != 0)
                    sb.append("                <Rotation>").append(SldBuilder.fmt(rotation)).append("</Rotation>\n");
                sb.append("              </PointPlacement>\n");
            }
            sb.append("            </LabelPlacement>\n");
            if (haloColor != null)
                sb.append("            <Halo>\n")
                  .append("              <Radius>").append(SldBuilder.fmt(haloRadius)).append("</Radius>\n")
                  .append("              <Fill>\n")
                  .append("                <CssParameter name=\"fill\">").append(SldBuilder.esc(haloColor)).append("</CssParameter>\n")
                  .append("              </Fill>\n")
                  .append("            </Halo>\n");
            sb.append("            <Fill>\n")
              .append("              <CssParameter name=\"fill\">").append(SldBuilder.esc(color)).append("</CssParameter>\n")
              .append("            </Fill>\n");
            if (vendorOptions != null)
                for (Map.Entry<String, String> e : vendorOptions.entrySet())
                    sb.append("            <VendorOption name=\"").append(SldBuilder.esc(e.getKey())).append("\">")
                      .append(SldBuilder.esc(e.getValue())).append("</VendorOption>\n");
            sb.append("          </TextSymbolizer>\n");
            return sb.toString();
        }

        /** Shortcut: {@link #end()} then start a new auto-named rule. */
        public RuleBuilder rule() {
            return end().rule();
        }

        /** Shortcut: {@link #end()} then start a new named rule. */
        public RuleBuilder rule(String name) {
            return end().rule(name);
        }

        /** Shortcut: {@link #end()} then build. */
        public StyleContent build() {
            return end().build();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LineBuilder  (mirrors GeoTools LineSymbolizer + Stroke)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Full-featured builder for a {@code <LineSymbolizer>}.
     *
     * <p>Controls the full set of SVG stroke properties supported by SLD 1.0:
     * <ul>
     *   <li>Stroke colour and width (set at construction via
     *       {@link RuleBuilder#stroke(String, double)})</li>
     *   <li>Dash pattern ({@link #dashArray(String)}) and dash offset
     *       ({@link #dashOffset(double)})</li>
     *   <li>Line-cap style ({@link #lineCap(LineCap)}) and line-join style
     *       ({@link #lineJoin(LineJoin)})</li>
     *   <li>Stroke-level opacity ({@link #strokeOpacity(double)}) and symbolizer-level
     *       opacity ({@link #opacity(double)})</li>
     *   <li>Perpendicular offset from the line geometry ({@link #perpendicularOffset(double)})</li>
     *   <li>Graphic fill or graphic stroke (repeated marks/images along the line)</li>
     * </ul>
     *
     * <p>Call {@link #end()} to finalize the symbolizer and return to the parent
     * {@link RuleBuilder}.
     *
     * <pre>{@code
     * SldBuilder.create("roads").rule()
     *     .stroke("#CC0000", 2)
     *         .dashArray("4 2")
     *         .lineCap(LineCap.ROUND)
     *         .lineJoin(LineJoin.ROUND)
     *     .end().build();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class LineBuilder {

        private final RuleBuilder parent;
        private final String strokeColor;
        private final double strokeWidth;
        private String dashArray;
        private Double dashOffset;
        private LineCap lineCap;
        private LineJoin lineJoin;
        private Double strokeOpacity;
        private double opacity = 1.0;
        private Double perpendicularOffset;
        private String geometryProperty;
        // GraphicFill / GraphicStroke (mutually exclusive with color stroke, but SLD allows both)
        private String graphicFillXml;
        private String graphicStrokeXml;

        LineBuilder(RuleBuilder parent, String strokeColor, double strokeWidth) {
            this.parent      = parent;
            this.strokeColor = strokeColor;
            this.strokeWidth = strokeWidth;
        }

        /** SVG dash pattern, e.g. {@code "4 2"} (dash 4px, gap 2px). */
        public LineBuilder dashArray(String pattern) {
            this.dashArray = pattern; return this;
        }

        /** Dash pattern offset in pixels. */
        public LineBuilder dashOffset(double offset) {
            this.dashOffset = offset; return this;
        }

        /** Controls how line endpoints are drawn (butt / round / square). */
        public LineBuilder lineCap(LineCap cap) {
            this.lineCap = cap; return this;
        }

        /** Controls how line joins are drawn (mitre / round / bevel). */
        public LineBuilder lineJoin(LineJoin join) {
            this.lineJoin = join; return this;
        }

        /** Stroke opacity separate from symbolizer opacity (0.0–1.0). */
        public LineBuilder strokeOpacity(double opacity) {
            this.strokeOpacity = opacity; return this;
        }

        /** Overall symbolizer opacity (0.0–1.0). */
        public LineBuilder opacity(double opacity) {
            this.opacity = opacity; return this;
        }

        /** Offset line from geometry in pixels (positive = left of direction of travel). */
        public LineBuilder perpendicularOffset(double offset) {
            this.perpendicularOffset = offset; return this;
        }

        /** Fill the line's interior area with a repeated well-known mark. */
        public LineBuilder graphicFill(WellKnownName mark, String color, double size) {
            this.graphicFillXml = buildInlineGraphicFillMark(mark, color, size);
            return this;
        }

        /** Fill the line's interior area with a repeated external graphic. */
        public LineBuilder graphicFill(String url, String format, double size) {
            this.graphicFillXml = buildInlineGraphicFillExternal(url, format, size);
            return this;
        }

        /** Repeat a well-known mark along the line stroke. */
        public LineBuilder graphicStroke(WellKnownName mark, String color, double size) {
            this.graphicStrokeXml = buildInlineGraphicStrokeMark(mark, color, size);
            return this;
        }

        /** Repeat an external graphic along the line stroke. */
        public LineBuilder graphicStroke(String url, String format, double size) {
            this.graphicStrokeXml = buildInlineGraphicStrokeExternal(url, format, size);
            return this;
        }

        /** Geometry attribute to use (for multi-geometry features). */
        public LineBuilder geometry(String propertyName) {
            this.geometryProperty = propertyName; return this;
        }

        /** Finalizes this LineSymbolizer and returns to the parent {@link RuleBuilder}. */
        public RuleBuilder end() {
            parent.symbolizerXmls.add(buildLineXmlFull());
            return parent;
        }

        private String buildLineXmlFull() {
            StringBuilder sb = new StringBuilder();
            sb.append("          <LineSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(SldBuilder.esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("            <Stroke>\n");
            if (graphicFillXml != null) {
                sb.append(graphicFillXml);
            }
            if (graphicStrokeXml != null) {
                sb.append(graphicStrokeXml);
            }
            sb.append("              <CssParameter name=\"stroke\">").append(SldBuilder.esc(strokeColor)).append("</CssParameter>\n")
              .append("              <CssParameter name=\"stroke-width\">").append(SldBuilder.fmt(strokeWidth)).append("</CssParameter>\n");
            if (strokeOpacity != null)
                sb.append("              <CssParameter name=\"stroke-opacity\">").append(SldBuilder.fmt(strokeOpacity)).append("</CssParameter>\n");
            if (dashArray != null)
                sb.append("              <CssParameter name=\"stroke-dasharray\">").append(SldBuilder.esc(dashArray)).append("</CssParameter>\n");
            if (dashOffset != null)
                sb.append("              <CssParameter name=\"stroke-dashoffset\">").append(SldBuilder.fmt(dashOffset)).append("</CssParameter>\n");
            if (lineCap != null)
                sb.append("              <CssParameter name=\"stroke-linecap\">").append(lineCap.getValue()).append("</CssParameter>\n");
            if (lineJoin != null)
                sb.append("              <CssParameter name=\"stroke-linejoin\">").append(lineJoin.getValue()).append("</CssParameter>\n");
            sb.append("            </Stroke>\n");
            if (opacity != 1.0)
                sb.append("            <Opacity>").append(SldBuilder.fmt(opacity)).append("</Opacity>\n");
            if (perpendicularOffset != null)
                sb.append("            <PerpendicularOffset>").append(SldBuilder.fmt(perpendicularOffset)).append("</PerpendicularOffset>\n");
            sb.append("          </LineSymbolizer>\n");
            return sb.toString();
        }

        /** Shortcut: {@link #end()} then start a new auto-named rule. */
        public RuleBuilder rule() {
            return end().rule();
        }

        /** Shortcut: {@link #end()} then start a new named rule. */
        public RuleBuilder rule(String name) {
            return end().rule(name);
        }

        /** Shortcut: {@link #end()} then build. */
        public StyleContent build() {
            return end().build();
        }

        // shared by PolygonBuilder too
        static String buildInlineGraphicFillMark(WellKnownName mark, String color, double size) {
            return "              <GraphicFill><Graphic>\n" +
                   "                <Mark><WellKnownName>" + mark.getValue() + "</WellKnownName>\n" +
                   "                  <Fill><CssParameter name=\"fill\">" + SldBuilder.esc(color) + "</CssParameter></Fill>\n" +
                   "                </Mark>\n" +
                   "                <Size>" + SldBuilder.fmt(size) + "</Size>\n" +
                   "              </Graphic></GraphicFill>\n";
        }

        static String buildInlineGraphicFillExternal(String url, String format, double size) {
            return "              <GraphicFill><Graphic>\n" +
                   "                <ExternalGraphic>\n" +
                   "                  <OnlineResource xlink:type=\"simple\" xlink:href=\"" + SldBuilder.esc(url) + "\"/>\n" +
                   "                  <Format>" + SldBuilder.esc(format) + "</Format>\n" +
                   "                </ExternalGraphic>\n" +
                   "                <Size>" + SldBuilder.fmt(size) + "</Size>\n" +
                   "              </Graphic></GraphicFill>\n";
        }

        static String buildInlineGraphicStrokeMark(WellKnownName mark, String color, double size) {
            return "              <GraphicStroke><Graphic>\n" +
                   "                <Mark><WellKnownName>" + mark.getValue() + "</WellKnownName>\n" +
                   "                  <Fill><CssParameter name=\"fill\">" + SldBuilder.esc(color) + "</CssParameter></Fill>\n" +
                   "                </Mark>\n" +
                   "                <Size>" + SldBuilder.fmt(size) + "</Size>\n" +
                   "              </Graphic></GraphicStroke>\n";
        }

        static String buildInlineGraphicStrokeExternal(String url, String format, double size) {
            return "              <GraphicStroke><Graphic>\n" +
                   "                <ExternalGraphic>\n" +
                   "                  <OnlineResource xlink:type=\"simple\" xlink:href=\"" + SldBuilder.esc(url) + "\"/>\n" +
                   "                  <Format>" + SldBuilder.esc(format) + "</Format>\n" +
                   "                </ExternalGraphic>\n" +
                   "                <Size>" + SldBuilder.fmt(size) + "</Size>\n" +
                   "              </Graphic></GraphicStroke>\n";
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PolygonBuilder  (mirrors GeoTools PolygonSymbolizer)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Full-featured builder for a {@code <PolygonSymbolizer>}.
     * Supports fill (solid colour, opacity, graphic fill pattern) and stroke
     * (colour, width, opacity, cap, join, dash, graphic stroke), geometry property.
     *
     * <p>Obtain via {@link RuleBuilder#fill(String)} or {@link RuleBuilder#fill()}.
     * Call {@link #end()} to return to the rule.
     *
     * <pre>{@code
     * SldBuilder.create("parcels").rule()
     *     .fill("#336699")
     *         .fillOpacity(0.8)
     *         .stroke("#000000", 1)
     *             .strokeLineCap(LineCap.ROUND).strokeLineJoin(LineJoin.MITRE)
     *     .end().build();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class PolygonBuilder {

        private final RuleBuilder parent;
        private final String fillColor;          // null = no solid fill (graphic fill only)
        private double fillOpacity   = 1.0;
        private String fillGraphicXml;           // GraphicFill inside <Fill>
        private String strokeColor;
        private double strokeWidth   = 1.0;
        private Double strokeOpacity;
        private LineCap strokeLineCap;
        private LineJoin strokeLineJoin;
        private String strokeDashArray;
        private Double strokeDashOffset;
        private String strokeGraphicFillXml;     // GraphicFill inside <Stroke>
        private String strokeGraphicStrokeXml;   // GraphicStroke inside <Stroke>
        private String geometryProperty;

        PolygonBuilder(RuleBuilder parent, String fillColor) {
            this.parent    = parent;
            this.fillColor = fillColor;
        }

        // ── Fill ──────────────────────────────────────────────────────────

        /** Fill opacity (0.0–1.0). */
        public PolygonBuilder fillOpacity(double opacity) {
            this.fillOpacity = opacity; return this;
        }

        /** Fill with a repeated well-known mark pattern instead of (or in addition to) solid colour. */
        public PolygonBuilder fillPattern(WellKnownName mark, String color, double size) {
            this.fillGraphicXml = LineBuilder.buildInlineGraphicFillMark(mark, color, size);
            return this;
        }

        /** Fill with a repeated external graphic pattern. */
        public PolygonBuilder fillPattern(String url, String format, double size) {
            this.fillGraphicXml = LineBuilder.buildInlineGraphicFillExternal(url, format, size);
            return this;
        }

        // ── Stroke ────────────────────────────────────────────────────────

        /** Outline stroke colour and width. */
        public PolygonBuilder stroke(String color, double width) {
            this.strokeColor = color; this.strokeWidth = width; return this;
        }

        /** Stroke opacity (0.0–1.0). */
        public PolygonBuilder strokeOpacity(double opacity) {
            this.strokeOpacity = opacity; return this;
        }

        /** Stroke line-cap style. */
        public PolygonBuilder strokeLineCap(LineCap cap) {
            this.strokeLineCap = cap; return this;
        }

        /** Stroke line-join style. */
        public PolygonBuilder strokeLineJoin(LineJoin join) {
            this.strokeLineJoin = join; return this;
        }

        /** Stroke dash pattern, e.g. {@code "4 2"}. */
        public PolygonBuilder strokeDashArray(String pattern) {
            this.strokeDashArray = pattern; return this;
        }

        /** Stroke dash offset in pixels. */
        public PolygonBuilder strokeDashOffset(double offset) {
            this.strokeDashOffset = offset; return this;
        }

        /** Fill the stroke interior with a repeated well-known mark. */
        public PolygonBuilder strokeGraphicFill(WellKnownName mark, String color, double size) {
            this.strokeGraphicFillXml = LineBuilder.buildInlineGraphicFillMark(mark, color, size);
            return this;
        }

        /** Fill the stroke interior with a repeated external graphic. */
        public PolygonBuilder strokeGraphicFill(String url, String format, double size) {
            this.strokeGraphicFillXml = LineBuilder.buildInlineGraphicFillExternal(url, format, size);
            return this;
        }

        /** Repeat a well-known mark along the stroke. */
        public PolygonBuilder strokeGraphicStroke(WellKnownName mark, String color, double size) {
            this.strokeGraphicStrokeXml = LineBuilder.buildInlineGraphicStrokeMark(mark, color, size);
            return this;
        }

        /** Repeat an external graphic along the stroke. */
        public PolygonBuilder strokeGraphicStroke(String url, String format, double size) {
            this.strokeGraphicStrokeXml = LineBuilder.buildInlineGraphicStrokeExternal(url, format, size);
            return this;
        }

        // ── Misc ──────────────────────────────────────────────────────────

        /** Geometry attribute to use (for multi-geometry features). */
        public PolygonBuilder geometry(String propertyName) {
            this.geometryProperty = propertyName; return this;
        }

        /** Finalizes this PolygonSymbolizer and returns to the parent {@link RuleBuilder}. */
        public RuleBuilder end() {
            parent.symbolizerXmls.add(buildXml());
            return parent;
        }

        /** Shortcut: {@link #end()} then start a new auto-named rule. */
        public RuleBuilder rule() {
            return end().rule();
        }

        /** Shortcut: {@link #end()} then start a new named rule. */
        public RuleBuilder rule(String name) {
            return end().rule(name);
        }

        /** Shortcut: {@link #end()} then build. */
        public StyleContent build() {
            return end().build();
        }

        private String buildXml() {
            StringBuilder sb = new StringBuilder();
            sb.append("          <PolygonSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(SldBuilder.esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            // Fill
            if (fillColor != null || fillGraphicXml != null) {
                sb.append("            <Fill>\n");
                if (fillGraphicXml != null) {
                    sb.append(fillGraphicXml);
                }
                if (fillColor != null) {
                    sb.append("              <CssParameter name=\"fill\">").append(SldBuilder.esc(fillColor)).append("</CssParameter>\n");
                    if (fillOpacity != 1.0)
                        sb.append("              <CssParameter name=\"fill-opacity\">").append(SldBuilder.fmt(fillOpacity)).append("</CssParameter>\n");
                }
                sb.append("            </Fill>\n");
            }
            // Stroke
            if (strokeColor != null || strokeGraphicFillXml != null || strokeGraphicStrokeXml != null) {
                sb.append("            <Stroke>\n");
                if (strokeGraphicFillXml != null) {
                    sb.append(strokeGraphicFillXml);
                }
                if (strokeGraphicStrokeXml != null) {
                    sb.append(strokeGraphicStrokeXml);
                }
                if (strokeColor != null)
                    sb.append("              <CssParameter name=\"stroke\">").append(SldBuilder.esc(strokeColor)).append("</CssParameter>\n")
                      .append("              <CssParameter name=\"stroke-width\">").append(SldBuilder.fmt(strokeWidth)).append("</CssParameter>\n");
                if (strokeOpacity != null)
                    sb.append("              <CssParameter name=\"stroke-opacity\">").append(SldBuilder.fmt(strokeOpacity)).append("</CssParameter>\n");
                if (strokeDashArray != null)
                    sb.append("              <CssParameter name=\"stroke-dasharray\">").append(SldBuilder.esc(strokeDashArray)).append("</CssParameter>\n");
                if (strokeDashOffset != null)
                    sb.append("              <CssParameter name=\"stroke-dashoffset\">").append(SldBuilder.fmt(strokeDashOffset)).append("</CssParameter>\n");
                if (strokeLineCap != null)
                    sb.append("              <CssParameter name=\"stroke-linecap\">").append(strokeLineCap.getValue()).append("</CssParameter>\n");
                if (strokeLineJoin != null)
                    sb.append("              <CssParameter name=\"stroke-linejoin\">").append(strokeLineJoin.getValue()).append("</CssParameter>\n");
                sb.append("            </Stroke>\n");
            }
            sb.append("          </PolygonSymbolizer>\n");
            return sb.toString();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // RasterBuilder  (mirrors GeoTools RasterSymbolizer full options)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Full-featured builder for a {@code <RasterSymbolizer>}.
     * Supports ChannelSelection, ContrastEnhancement, OverlapBehavior,
     * ColorMap, ShadedRelief, and ImageOutline.
     *
     * <p>Obtain via {@link RuleBuilder#rasterBuilder()}.
     * Call {@link #end()} to return to the rule.
     *
     * <pre>{@code
     * SldBuilder.create("dem").rule()
     *     .rasterBuilder()
     *         .opacity(0.9)
     *         .channelGray("1")
     *         .contrastEnhancement(ContrastMethod.NORMALIZE)
     *         .overlapBehavior(OverlapBehavior.LATEST_ON_TOP)
     *         .colorMap(SldColorMapType.RAMP,
     *                   SldColorMapEntry.of("#0000FF", 0, "Low"),
     *                   SldColorMapEntry.of("#FF0000", 100, "High"))
     *         .shadedRelief(55, false)
     *     .end().build();
     * }</pre>
     *
     * @since 1.0.0
     */
    public static final class RasterBuilder {

        private final RuleBuilder parent;
        private double opacity = 1.0;
        private String geometryProperty;

        // ChannelSelection
        private String redChannel, greenChannel, blueChannel, grayChannel;
        private ContrastMethod redCE, greenCE, blueCE, grayCE;
        private Double redGamma, greenGamma, blueGamma, grayGamma;

        // Global ContrastEnhancement
        private ContrastMethod globalCE;
        private Double globalGamma;

        // OverlapBehavior
        private OverlapBehavior overlapBehavior;

        // ColorMap
        private SldColorMapType colorMapType;
        private SldColorMapEntry[] colorMapEntries;
        private boolean colorMapExtended;

        // ShadedRelief
        private Double reliefFactor;
        private boolean brightnessOnly;

        // ImageOutline (stores the inner symbolizer XML, either Line or Polygon)
        private String imageOutlineXml;

        RasterBuilder(RuleBuilder parent) {
          this.parent = parent;
      }

        /** Overall raster opacity (0.0–1.0). */
        public RasterBuilder opacity(double opacity) {
            this.opacity = opacity; return this;
        }

        /** Geometry attribute to use. */
        public RasterBuilder geometry(String propertyName) {
            this.geometryProperty = propertyName; return this;
        }

        // ── ChannelSelection ─────────────────────────────────────────────

        /** Select RGB channels by band name/number. No per-channel contrast enhancement. */
        public RasterBuilder channelRGB(String red, String green, String blue) {
            this.redChannel = red; this.greenChannel = green; this.blueChannel = blue;
            return this;
        }

        /** Select RGB channels with per-channel contrast enhancement. */
        public RasterBuilder channelRGB(String red, ContrastMethod redCE,
                                         String green, ContrastMethod greenCE,
                                         String blue, ContrastMethod blueCE) {
            this.redChannel = red;   this.redCE   = redCE;
            this.greenChannel = green; this.greenCE = greenCE;
            this.blueChannel = blue; this.blueCE   = blueCE;
            return this;
        }

        /** Select a single gray channel by band name/number. */
        public RasterBuilder channelGray(String band) {
            this.grayChannel = band; return this;
        }

        /** Select a single gray channel with contrast enhancement. */
        public RasterBuilder channelGray(String band, ContrastMethod method) {
            this.grayChannel = band; this.grayCE = method; return this;
        }

        /** Select a single gray channel with gamma correction. */
        public RasterBuilder channelGray(String band, double gamma) {
            this.grayChannel = band; this.grayGamma = gamma; return this;
        }

        // ── ContrastEnhancement (global) ─────────────────────────────────

        /** Apply contrast enhancement across all channels. */
        public RasterBuilder contrastEnhancement(ContrastMethod method) {
            this.globalCE = method; return this;
        }

        /** Apply gamma correction across all channels. */
        public RasterBuilder gammaValue(double gamma) {
            this.globalGamma = gamma; return this;
        }

        // ── OverlapBehavior ───────────────────────────────────────────────

        /** How to render overlapping raster pixels. */
        public RasterBuilder overlapBehavior(OverlapBehavior behavior) {
            this.overlapBehavior = behavior; return this;
        }

        // ── ColorMap ─────────────────────────────────────────────────────

        /** Color map definition. */
        public RasterBuilder colorMap(SldColorMapType type, SldColorMapEntry... entries) {
            this.colorMapType = type; this.colorMapEntries = entries; return this;
        }

        /** Color map with {@code extended="true"} (more than 256 entries). */
        public RasterBuilder colorMapExtended(SldColorMapType type, SldColorMapEntry... entries) {
            colorMap(type, entries); this.colorMapExtended = true; return this;
        }

        // ── ShadedRelief ─────────────────────────────────────────────────

        /**
         * Add shaded relief.
         *
         * @param reliefFactor scaling factor (suggested range 55–75; higher = more pronounced)
         * @param brightnessOnly {@code true} to only adjust brightness, not hue/saturation
         */
        public RasterBuilder shadedRelief(double reliefFactor, boolean brightnessOnly) {
            this.reliefFactor = reliefFactor; this.brightnessOnly = brightnessOnly; return this;
        }

        // ── ImageOutline ─────────────────────────────────────────────────

        /** Outline raster cells with a simple line symbolizer. */
        public RasterBuilder imageOutlineLine(String strokeColor, double strokeWidth) {
            imageOutlineXml = "            <ImageOutline>\n" +
                              RuleBuilder.buildLineXml(strokeColor, strokeWidth, null) +
                              "            </ImageOutline>\n";
            return this;
        }

        /** Outline raster cells with a simple polygon symbolizer (fill + stroke). */
        public RasterBuilder imageOutlinePolygon(String fillColor, String strokeColor, double strokeWidth) {
            imageOutlineXml = "            <ImageOutline>\n" +
                              RuleBuilder.buildPolygonXml(fillColor, 1.0, strokeColor, strokeWidth) +
                              "            </ImageOutline>\n";
            return this;
        }

        /** Finalizes this RasterSymbolizer and returns to the parent {@link RuleBuilder}. */
        public RuleBuilder end() {
            parent.symbolizerXmls.add(buildXml());
            return parent;
        }

        /** Shortcut: {@link #end()} then start a new auto-named rule. */
        public RuleBuilder rule() {
            return end().rule();
        }

        /** Shortcut: {@link #end()} then start a new named rule. */
        public RuleBuilder rule(String name) {
            return end().rule(name);
        }

        /** Shortcut: {@link #end()} then build. */
        public StyleContent build() {
            return end().build();
        }

        private String buildXml() {
            StringBuilder sb = new StringBuilder();
            sb.append("          <RasterSymbolizer>\n");
            if (geometryProperty != null)
                sb.append("            <Geometry><ogc:PropertyName>")
                  .append(SldBuilder.esc(geometryProperty)).append("</ogc:PropertyName></Geometry>\n");
            sb.append("            <Opacity>").append(SldBuilder.fmt(opacity)).append("</Opacity>\n");

            // ChannelSelection
            if (grayChannel != null) {
                sb.append("            <ChannelSelection>\n")
                  .append("              <GrayChannel>\n")
                  .append("                <SourceChannelName>").append(SldBuilder.esc(grayChannel)).append("</SourceChannelName>\n");
                appendCE(sb, "                ", grayCE, grayGamma);
                sb.append("              </GrayChannel>\n")
                  .append("            </ChannelSelection>\n");
            } else if (redChannel != null) {
                sb.append("            <ChannelSelection>\n");
                appendChannel(sb, "RedChannel",   redChannel,   redCE,   redGamma);
                appendChannel(sb, "GreenChannel", greenChannel, greenCE, greenGamma);
                appendChannel(sb, "BlueChannel",  blueChannel,  blueCE,  blueGamma);
                sb.append("            </ChannelSelection>\n");
            }

            // OverlapBehavior
            if (overlapBehavior != null)
                sb.append("            <OverlapBehavior><").append(overlapBehavior.getValue()).append("/></OverlapBehavior>\n");

            // ColorMap
            if (colorMapType != null && colorMapEntries != null) {
                sb.append("            <ColorMap type=\"").append(colorMapType.getValue()).append("\"");
                if (colorMapExtended) {
                    sb.append(" extended=\"true\"");
                }
                sb.append(">\n");
                for (SldColorMapEntry e : colorMapEntries) {
                    sb.append("              <ColorMapEntry color=\"").append(SldBuilder.esc(e.getColor())).append("\"")
                      .append(" quantity=\"").append(SldBuilder.fmt(e.getQuantity())).append("\"");
                    if (e.getLabel() != null) {
                        sb.append(" label=\"").append(SldBuilder.esc(e.getLabel())).append("\"");
                    }
                    if (e.getOpacity() != 1.0) {
                        sb.append(" opacity=\"").append(SldBuilder.fmt(e.getOpacity())).append("\"");
                    }
                    sb.append("/>\n");
                }
                sb.append("            </ColorMap>\n");
            }

            // Global ContrastEnhancement
            if (globalCE != null || globalGamma != null) {
                sb.append("            <ContrastEnhancement>\n");
                if (globalCE != null)
                    sb.append("              <").append(globalCE.getTag()).append("/>\n");
                if (globalGamma != null)
                    sb.append("              <GammaValue>").append(SldBuilder.fmt(globalGamma)).append("</GammaValue>\n");
                sb.append("            </ContrastEnhancement>\n");
            }

            // ShadedRelief
            if (reliefFactor != null)
                sb.append("            <ShadedRelief>\n")
                  .append("              <BrightnessOnly>").append(brightnessOnly).append("</BrightnessOnly>\n")
                  .append("              <ReliefFactor>").append(SldBuilder.fmt(reliefFactor)).append("</ReliefFactor>\n")
                  .append("            </ShadedRelief>\n");

            // ImageOutline
            if (imageOutlineXml != null) {
                sb.append(imageOutlineXml);
            }

            sb.append("          </RasterSymbolizer>\n");
            return sb.toString();
        }

        private static void appendChannel(StringBuilder sb, String tag,
                                           String band, ContrastMethod ce, Double gamma) {
            sb.append("              <").append(tag).append(">\n")
              .append("                <SourceChannelName>").append(SldBuilder.esc(band)).append("</SourceChannelName>\n");
            appendCE(sb, "                ", ce, gamma);
            sb.append("              </").append(tag).append(">\n");
        }

        private static void appendCE(StringBuilder sb, String indent,
                                      ContrastMethod ce, Double gamma) {
            if (ce == null && gamma == null) {
                return;
            }
            sb.append(indent).append("<ContrastEnhancement>\n");
            if (ce != null)
                sb.append(indent).append("  <").append(ce.getTag()).append("/>\n");
            if (gamma != null)
                sb.append(indent).append("  <GammaValue>").append(SldBuilder.fmt(gamma)).append("</GammaValue>\n");
            sb.append(indent).append("</ContrastEnhancement>\n");
        }
    }
}
