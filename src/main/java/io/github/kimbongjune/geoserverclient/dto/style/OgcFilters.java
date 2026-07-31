package io.github.kimbongjune.geoserverclient.dto.style;

/**
 * Static factory class for OGC Filter predicates used in {@link SldBuilder} rules.
 *
 * <p>Mirrors the GeoTools {@code FilterFactory} API without requiring any GeoTools
 * dependency. Provides factory methods for:
 * <ul>
 *   <li><b>Comparison</b> — {@link #equalTo}, {@link #notEqualTo}, {@link #greaterThan},
 *       {@link #greaterThanOrEqualTo}, {@link #lessThan}, {@link #lessThanOrEqualTo}</li>
 *   <li><b>Range / pattern / null</b> — {@link #between}, {@link #like}, {@link #isNull}</li>
 *   <li><b>Spatial</b> — {@link #bbox}, {@link #intersects}, {@link #contains},
 *       {@link #within}, {@link #disjoint}, {@link #touches}, {@link #crosses},
 *       {@link #overlaps}, {@link #spatialEquals}, {@link #dWithin}, {@link #beyond}</li>
 *   <li><b>Identity</b> — {@link #featureId}, {@link #gmlObjectId}</li>
 *   <li><b>Logical</b> — {@link #and}, {@link #or}, {@link #not}</li>
 * </ul>
 *
 * <p>Every method returns an {@link OgcFilter} instance that serializes to the corresponding
 * OGC Filter Encoding 1.0 XML fragment when appended by {@link SldBuilder} into a
 * {@code <ogc:Filter>} element.
 *
 * <p>Comparison methods come in two overloaded forms:
 * <ol>
 *   <li><b>String / Number shorthand</b> — wraps both sides automatically in
 *       {@code <ogc:PropertyName>} and {@code <ogc:Literal>}.</li>
 *   <li><b>{@link SldExpression} overload</b> — gives full control; use when either operand
 *       is an OGC function, arithmetic expression, or any non-trivial expression.</li>
 * </ol>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
 * import static io.github.kimbongjune.geoserverclient.dto.style.SldExpression.*;
 *
 * // Simple attribute comparison
 * OgcFilter simple = equalTo("road_type", "highway");
 *
 * // Numeric range
 * OgcFilter range = between("population", 100_000, 1_000_000);
 *
 * // Pattern match
 * OgcFilter pattern = like("name", "New*");
 *
 * // Spatial — features within a bounding box
 * OgcFilter spatial = bbox("the_geom", -180, -90, 180, 90, "EPSG:4326");
 *
 * // OGC function on the left operand
 * OgcFilter fnFilter = equalTo(
 *     function("strToLowerCase", property("road_type")),
 *     literal("highway"));
 *
 * // Compound logical filter
 * OgcFilter compound = and(
 *     greaterThan("population", 500_000),
 *     not(equalTo("country", "excluded"))
 * );
 * }</pre>
 *
 * @see OgcFilter
 * @see SldExpression
 * @see SldGeometry
 * @since 1.0.0
 */
public final class OgcFilters {

    /** Not instantiable — all methods are static. */
    private OgcFilters() {}

    // ── Comparison (String convenience) ──────────────────────────────────

    /**
     * Creates an {@code <ogc:PropertyIsEqualTo>} filter — {@code property = value}.
     *
     * <p>Generates: {@code <ogc:PropertyIsEqualTo>
     * <ogc:PropertyName>property</ogc:PropertyName>
     * <ogc:Literal>value</ogc:Literal>
     * </ogc:PropertyIsEqualTo>}
     *
     * @param property the feature attribute name to compare
     * @param value    the string literal to compare against
     * @return an {@link OgcFilter} representing the equality predicate
     */
    public static OgcFilter equalTo(String property, String value) {
        return comparison("PropertyIsEqualTo", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsEqualTo>} filter for a numeric literal —
     * {@code property = value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric literal to compare against
     * @return an {@link OgcFilter} representing the numeric equality predicate
     */
    public static OgcFilter equalTo(String property, Number value) {
        return comparison("PropertyIsEqualTo", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsNotEqualTo>} filter — {@code property != value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the string literal to compare against
     * @return an {@link OgcFilter} representing the inequality predicate
     */
    public static OgcFilter notEqualTo(String property, String value) {
        return comparison("PropertyIsNotEqualTo", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsNotEqualTo>} filter for a numeric literal —
     * {@code property != value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric literal to compare against
     * @return an {@link OgcFilter} representing the numeric inequality predicate
     */
    public static OgcFilter notEqualTo(String property, Number value) {
        return comparison("PropertyIsNotEqualTo", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsGreaterThan>} filter — {@code property > value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric threshold
     * @return an {@link OgcFilter} representing the greater-than predicate
     */
    public static OgcFilter greaterThan(String property, Number value) {
        return comparison("PropertyIsGreaterThan", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsGreaterThanOrEqualTo>} filter —
     * {@code property >= value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric threshold
     * @return an {@link OgcFilter} representing the greater-than-or-equal predicate
     */
    public static OgcFilter greaterThanOrEqualTo(String property, Number value) {
        return comparison("PropertyIsGreaterThanOrEqualTo", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsLessThan>} filter — {@code property < value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric threshold
     * @return an {@link OgcFilter} representing the less-than predicate
     */
    public static OgcFilter lessThan(String property, Number value) {
        return comparison("PropertyIsLessThan", prop(property), lit(value));
    }

    /**
     * Creates an {@code <ogc:PropertyIsLessThanOrEqualTo>} filter —
     * {@code property <= value}.
     *
     * @param property the feature attribute name to compare
     * @param value    the numeric threshold
     * @return an {@link OgcFilter} representing the less-than-or-equal predicate
     */
    public static OgcFilter lessThanOrEqualTo(String property, Number value) {
        return comparison("PropertyIsLessThanOrEqualTo", prop(property), lit(value));
    }

    // ── Comparison (SldExpression — full control) ─────────────────────────

    /**
     * Creates an {@code <ogc:PropertyIsEqualTo>} filter using arbitrary {@link SldExpression}
     * operands — {@code left = right}.
     *
     * <p>Use this overload when either side is an OGC function, arithmetic expression, or
     * any expression that cannot be expressed as a plain property name or literal string:
     * <pre>{@code
     * equalTo(function("strToLowerCase", property("road_type")), literal("highway"))
     * }</pre>
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the expression equality predicate
     */
    public static OgcFilter equalTo(SldExpression left, SldExpression right) {
        return comparison("PropertyIsEqualTo", left, right);
    }

    /**
     * Creates an {@code <ogc:PropertyIsNotEqualTo>} filter using arbitrary expressions —
     * {@code left != right}.
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the expression inequality predicate
     */
    public static OgcFilter notEqualTo(SldExpression left, SldExpression right) {
        return comparison("PropertyIsNotEqualTo", left, right);
    }

    /**
     * Creates an {@code <ogc:PropertyIsGreaterThan>} filter using arbitrary expressions —
     * {@code left > right}.
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the greater-than predicate
     */
    public static OgcFilter greaterThan(SldExpression left, SldExpression right) {
        return comparison("PropertyIsGreaterThan", left, right);
    }

    /**
     * Creates an {@code <ogc:PropertyIsGreaterThanOrEqualTo>} filter using arbitrary
     * expressions — {@code left >= right}.
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the greater-than-or-equal predicate
     */
    public static OgcFilter greaterThanOrEqualTo(SldExpression left, SldExpression right) {
        return comparison("PropertyIsGreaterThanOrEqualTo", left, right);
    }

    /**
     * Creates an {@code <ogc:PropertyIsLessThan>} filter using arbitrary expressions —
     * {@code left < right}.
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the less-than predicate
     */
    public static OgcFilter lessThan(SldExpression left, SldExpression right) {
        return comparison("PropertyIsLessThan", left, right);
    }

    /**
     * Creates an {@code <ogc:PropertyIsLessThanOrEqualTo>} filter using arbitrary expressions —
     * {@code left <= right}.
     *
     * @param left  the left-hand operand expression
     * @param right the right-hand operand expression
     * @return an {@link OgcFilter} representing the less-than-or-equal predicate
     */
    public static OgcFilter lessThanOrEqualTo(SldExpression left, SldExpression right) {
        return comparison("PropertyIsLessThanOrEqualTo", left, right);
    }

    // ── Between / Like / IsNull ───────────────────────────────────────────

    /**
     * Creates an {@code <ogc:PropertyIsBetween>} filter — {@code lower <= property <= upper}.
     *
     * <p>Convenience shorthand for the {@link #between(SldExpression, Number, Number)} overload
     * with a plain attribute name.
     *
     * @param property the feature attribute name
     * @param lower    the inclusive lower bound
     * @param upper    the inclusive upper bound
     * @return an {@link OgcFilter} representing the between predicate
     */
    public static OgcFilter between(String property, Number lower, Number upper) {
        return between(SldExpression.property(property), lower, upper);
    }

    /**
     * Creates an {@code <ogc:PropertyIsBetween>} filter using an arbitrary expression —
     * {@code lower <= expr <= upper}.
     *
     * @param expr  the expression whose value is tested against the bounds
     * @param lower the inclusive lower bound literal
     * @param upper the inclusive upper bound literal
     * @return an {@link OgcFilter} representing the between predicate
     */
    public static OgcFilter between(SldExpression expr, Number lower, Number upper) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:PropertyIsBetween>\n")
              .append(indent).append("  ");
            expr.appendXml(sb);
            sb.append("\n")
              .append(indent).append("  <ogc:LowerBoundary>\n")
              .append(indent).append("    ").append("<ogc:Literal>").append(lower).append("</ogc:Literal>").append("\n")
              .append(indent).append("  </ogc:LowerBoundary>\n")
              .append(indent).append("  <ogc:UpperBoundary>\n")
              .append(indent).append("    ").append("<ogc:Literal>").append(upper).append("</ogc:Literal>").append("\n")
              .append(indent).append("  </ogc:UpperBoundary>\n")
              .append(indent).append("</ogc:PropertyIsBetween>\n");
        };
    }

    /**
     * Creates an {@code <ogc:PropertyIsLike>} filter for wildcard pattern matching.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
     *   <ogc:PropertyName>name</ogc:PropertyName>
     *   <ogc:Literal>New*</ogc:Literal>
     * </ogc:PropertyIsLike>
     * }</pre>
     *
     * @param property   the feature attribute name to match against
     * @param pattern    the pattern string; use {@code wildCard} to match any substring and
     *                   {@code singleChar} to match any single character
     * @param wildCard   the multi-character wildcard character (commonly {@code "*"})
     * @param singleChar the single-character wildcard character (commonly {@code "."})
     * @param escape     the escape character used to treat wildcard chars as literals
     *                   (commonly {@code "!"})
     * @return an {@link OgcFilter} representing the LIKE predicate
     */
    public static OgcFilter like(String property, String pattern,
                                  String wildCard, String singleChar, String escape) {
        return (sb, indent) -> sb
                .append(indent).append("<ogc:PropertyIsLike")
                .append(" wildCard=\"").append(esc(wildCard)).append("\"")
                .append(" singleChar=\"").append(esc(singleChar)).append("\"")
                .append(" escape=\"").append(esc(escape)).append("\">\n")
                .append(indent).append("  <ogc:PropertyName>").append(esc(property)).append("</ogc:PropertyName>\n")
                .append(indent).append("  <ogc:Literal>").append(esc(pattern)).append("</ogc:Literal>\n")
                .append(indent).append("</ogc:PropertyIsLike>\n");
    }

    /**
     * Creates an {@code <ogc:PropertyIsLike>} filter with default wildcard characters.
     *
     * <p>Equivalent to {@link #like(String, String, String, String, String)} with
     * {@code wildCard="*"}, {@code singleChar="."}, {@code escape="!"}.
     *
     * @param property the feature attribute name to match against
     * @param pattern  the pattern string; use {@code *} for any substring, {@code .} for
     *                 any single character, and {@code !} to escape wildcards
     * @return an {@link OgcFilter} representing the LIKE predicate
     */
    public static OgcFilter like(String property, String pattern) {
        return like(property, pattern, "*", ".", "!");
    }

    /**
     * Creates an {@code <ogc:PropertyIsNull>} filter — {@code property IS NULL}.
     *
     * <p>Convenience shorthand for {@link #isNull(SldExpression)} with a plain attribute name.
     *
     * @param property the feature attribute name to test for null
     * @return an {@link OgcFilter} representing the IS NULL predicate
     */
    public static OgcFilter isNull(String property) {
        return isNull(SldExpression.property(property));
    }

    /**
     * Creates an {@code <ogc:PropertyIsNull>} filter for an arbitrary expression.
     *
     * <p>Use this overload when the value to test is an OGC function or complex expression
     * rather than a plain attribute name.
     *
     * @param expr the expression to test for null
     * @return an {@link OgcFilter} representing the IS NULL predicate
     */
    public static OgcFilter isNull(SldExpression expr) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:PropertyIsNull>\n")
              .append(indent).append("  ");
            expr.appendXml(sb);
            sb.append("\n")
              .append(indent).append("</ogc:PropertyIsNull>\n");
        };
    }

    // ── Spatial ───────────────────────────────────────────────────────────

    /**
     * Creates an {@code <ogc:BBOX>} spatial filter — true when the feature geometry intersects
     * the given {@link SldGeometry} envelope.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:BBOX>
     *   <ogc:PropertyName>the_geom</ogc:PropertyName>
     *   <gml:Box srsName="EPSG:4326">
     *     <gml:coordinates>-180,-90 180,90</gml:coordinates>
     *   </gml:Box>
     * </ogc:BBOX>
     * }</pre>
     *
     * @param property the geometry attribute name (e.g. {@code "the_geom"})
     * @param geometry the bounding envelope; create with
     *                 {@link SldGeometry#envelope(double, double, double, double, String)}
     * @return an {@link OgcFilter} representing the BBOX predicate
     */
    public static OgcFilter bbox(String property, SldGeometry geometry) {
        return spatial("BBOX", property, geometry);
    }

    /**
     * Creates an {@code <ogc:BBOX>} filter with explicit coordinates and SRS.
     *
     * <p>Convenience shorthand that internally calls
     * {@link SldGeometry#envelope(double, double, double, double, String)}.
     *
     * @param property the geometry attribute name
     * @param minX     western boundary (minimum X / longitude)
     * @param minY     southern boundary (minimum Y / latitude)
     * @param maxX     eastern boundary (maximum X / longitude)
     * @param maxY     northern boundary (maximum Y / latitude)
     * @param srsName  the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @return an {@link OgcFilter} representing the BBOX predicate
     */
    public static OgcFilter bbox(String property, double minX, double minY,
                                  double maxX, double maxY, String srsName) {
        return bbox(property, SldGeometry.envelope(minX, minY, maxX, maxY, srsName));
    }

    /**
     * Creates an {@code <ogc:BBOX>} filter with explicit coordinates and no explicit SRS.
     *
     * <p>The server applies its default CRS. Equivalent to
     * {@link #bbox(String, double, double, double, double, String)} with a {@code null} SRS.
     *
     * @param property the geometry attribute name
     * @param minX     western boundary
     * @param minY     southern boundary
     * @param maxX     eastern boundary
     * @param maxY     northern boundary
     * @return an {@link OgcFilter} representing the BBOX predicate
     */
    public static OgcFilter bbox(String property, double minX, double minY,
                                  double maxX, double maxY) {
        return bbox(property, SldGeometry.envelope(minX, minY, maxX, maxY));
    }

    /**
     * Creates an {@code <ogc:Intersects>} spatial filter — true when the feature geometry
     * and the given geometry share at least one point (including boundaries).
     *
     * @param property the geometry attribute name
     * @param geometry the test geometry; create with factory methods on {@link SldGeometry}
     * @return an {@link OgcFilter} representing the Intersects predicate
     */
    public static OgcFilter intersects(String property, SldGeometry geometry) {
        return spatial("Intersects", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Contains>} spatial filter — true when the feature geometry
     * completely contains the given geometry (no points of the test geometry are outside).
     *
     * @param property the geometry attribute name
     * @param geometry the geometry that must be contained
     * @return an {@link OgcFilter} representing the Contains predicate
     */
    public static OgcFilter contains(String property, SldGeometry geometry) {
        return spatial("Contains", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Within>} spatial filter — true when the feature geometry is
     * entirely within the given geometry (the inverse of {@link #contains}).
     *
     * @param property the geometry attribute name
     * @param geometry the containing geometry
     * @return an {@link OgcFilter} representing the Within predicate
     */
    public static OgcFilter within(String property, SldGeometry geometry) {
        return spatial("Within", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Disjoint>} spatial filter — true when the feature geometry and
     * the given geometry share no points at all (completely separate).
     *
     * @param property the geometry attribute name
     * @param geometry the geometry to test disjointness against
     * @return an {@link OgcFilter} representing the Disjoint predicate
     */
    public static OgcFilter disjoint(String property, SldGeometry geometry) {
        return spatial("Disjoint", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Touches>} spatial filter — true when the feature geometry and
     * the given geometry share at least one boundary point but no interior points.
     *
     * @param property the geometry attribute name
     * @param geometry the geometry to test for boundary contact
     * @return an {@link OgcFilter} representing the Touches predicate
     */
    public static OgcFilter touches(String property, SldGeometry geometry) {
        return spatial("Touches", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Crosses>} spatial filter — true when the feature geometry and
     * the given geometry have some but not all interior points in common (e.g. a line crossing
     * a polygon boundary).
     *
     * @param property the geometry attribute name
     * @param geometry the geometry to test crossing against
     * @return an {@link OgcFilter} representing the Crosses predicate
     */
    public static OgcFilter crosses(String property, SldGeometry geometry) {
        return spatial("Crosses", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Overlaps>} spatial filter — true when the feature geometry and
     * the given geometry are of the same dimension and their intersection is also of the same
     * dimension (i.e. they partially overlap but neither contains the other).
     *
     * @param property the geometry attribute name
     * @param geometry the geometry to test overlapping against
     * @return an {@link OgcFilter} representing the Overlaps predicate
     */
    public static OgcFilter overlaps(String property, SldGeometry geometry) {
        return spatial("Overlaps", property, geometry);
    }

    /**
     * Creates an {@code <ogc:Equals>} spatial filter — true when the feature geometry is
     * topologically equal to the given geometry (same point set, regardless of vertex order).
     *
     * <p>Named {@code spatialEquals} to avoid a collision with
     * {@link Object#equals(Object)}.
     *
     * @param property the geometry attribute name
     * @param geometry the geometry to test equality against
     * @return an {@link OgcFilter} representing the spatial Equals predicate
     */
    public static OgcFilter spatialEquals(String property, SldGeometry geometry) {
        return spatial("Equals", property, geometry);
    }

    /**
     * Creates an {@code <ogc:DWithin>} spatial filter — true when the feature geometry is
     * within {@code distance} of the given geometry.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:DWithin>
     *   <ogc:PropertyName>the_geom</ogc:PropertyName>
     *   <gml:Point>…</gml:Point>
     *   <ogc:Distance units="meters">500</ogc:Distance>
     * </ogc:DWithin>
     * }</pre>
     *
     * @param property the geometry attribute name
     * @param geometry the reference geometry to measure distance from
     * @param distance the maximum distance threshold
     * @param units    the distance unit string recognized by GeoServer, e.g. {@code "meters"},
     *                 {@code "feet"}, {@code "kilometers"}
     * @return an {@link OgcFilter} representing the DWithin predicate
     */
    public static OgcFilter dWithin(String property, SldGeometry geometry,
                                     double distance, String units) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:DWithin>\n")
              .append(indent).append("  <ogc:PropertyName>").append(esc(property)).append("</ogc:PropertyName>\n");
            geometry.appendGml(sb, indent + "  ");
            sb.append(indent).append("  <ogc:Distance units=\"").append(esc(units)).append("\">")
              .append(distance).append("</ogc:Distance>\n")
              .append(indent).append("</ogc:DWithin>\n");
        };
    }

    /**
     * Creates an {@code <ogc:Beyond>} spatial filter — true when the feature geometry is
     * more than {@code distance} away from the given geometry (the complement of
     * {@link #dWithin}).
     *
     * @param property the geometry attribute name
     * @param geometry the reference geometry to measure distance from
     * @param distance the minimum distance threshold
     * @param units    the distance unit string, e.g. {@code "meters"}, {@code "feet"},
     *                 {@code "kilometers"}
     * @return an {@link OgcFilter} representing the Beyond predicate
     */
    public static OgcFilter beyond(String property, SldGeometry geometry,
                                    double distance, String units) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:Beyond>\n")
              .append(indent).append("  <ogc:PropertyName>").append(esc(property)).append("</ogc:PropertyName>\n");
            geometry.appendGml(sb, indent + "  ");
            sb.append(indent).append("  <ogc:Distance units=\"").append(esc(units)).append("\">")
              .append(distance).append("</ogc:Distance>\n")
              .append(indent).append("</ogc:Beyond>\n");
        };
    }

    // ── FeatureId / GmlObjectId ───────────────────────────────────────────

    /**
     * Creates an {@code <ogc:FeatureId>} filter that selects features by their WFS feature ID.
     *
     * <p>Multiple IDs form an implicit OR — a feature matches if its ID equals any of the
     * supplied values. Generates one {@code <ogc:FeatureId fid="…"/>} element per ID.
     *
     * <p>Feature IDs have the form {@code typeName.localId}, e.g. {@code "roads.1"}:
     * <pre>{@code
     * featureId("roads.1", "roads.2", "roads.3")
     * }</pre>
     *
     * @param fids one or more feature identifier strings
     * @return an {@link OgcFilter} that matches features with any of the given IDs
     */
    public static OgcFilter featureId(String... fids) {
        return (sb, indent) -> {
            for (String fid : fids)
                sb.append(indent).append("<ogc:FeatureId fid=\"").append(esc(fid)).append("\"/>\n");
        };
    }

    /**
     * Creates an {@code <ogc:GmlObjectId>} filter that selects GML objects by their
     * {@code gml:id} attribute.
     *
     * <p>Alternative to {@link #featureId} for GML 3+ / WFS 1.1 resources where the
     * object identifier is carried in the {@code gml:id} XML attribute rather than a WFS
     * feature ID. Multiple IDs form an implicit OR:
     * <pre>{@code
     * gmlObjectId("roads.1", "roads.2")
     * }</pre>
     *
     * @param gmlIds one or more GML object identifier strings
     * @return an {@link OgcFilter} that matches GML objects with any of the given IDs
     */
    public static OgcFilter gmlObjectId(String... gmlIds) {
        return (sb, indent) -> {
            for (String id : gmlIds)
                sb.append(indent).append("<ogc:GmlObjectId gml:id=\"").append(esc(id)).append("\"/>\n");
        };
    }

    // ── Logical ───────────────────────────────────────────────────────────

    /**
     * Creates an {@code <ogc:And>} logical filter — all supplied sub-filters must match.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:And>
     *   <!-- filter1 -->
     *   <!-- filter2 -->
     * </ogc:And>
     * }</pre>
     *
     * @param filters two or more filters to combine with logical AND
     * @return an {@link OgcFilter} that is true only when all {@code filters} are true
     */
    public static OgcFilter and(OgcFilter... filters) {
        return logical("And", filters);
    }

    /**
     * Creates an {@code <ogc:Or>} logical filter — at least one sub-filter must match.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:Or>
     *   <!-- filter1 -->
     *   <!-- filter2 -->
     * </ogc:Or>
     * }</pre>
     *
     * @param filters two or more filters to combine with logical OR
     * @return an {@link OgcFilter} that is true when any of {@code filters} is true
     */
    public static OgcFilter or(OgcFilter... filters) {
        return logical("Or",  filters);
    }

    /**
     * Creates an {@code <ogc:Not>} logical filter — the sub-filter must not match.
     *
     * <p>Generates:
     * <pre>{@code
     * <ogc:Not>
     *   <!-- filter -->
     * </ogc:Not>
     * }</pre>
     *
     * @param filter the filter to negate
     * @return an {@link OgcFilter} that is true when {@code filter} is false
     */
    public static OgcFilter not(OgcFilter filter) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:Not>\n");
            filter.appendXml(sb, indent + "  ");
            sb.append(indent).append("</ogc:Not>\n");
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static OgcFilter spatial(String type, String property, SldGeometry geometry) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:").append(type).append(">\n")
              .append(indent).append("  <ogc:PropertyName>").append(esc(property)).append("</ogc:PropertyName>\n");
            geometry.appendGml(sb, indent + "  ");
            sb.append(indent).append("</ogc:").append(type).append(">\n");
        };
    }

    private static OgcFilter comparison(String type, SldExpression left, SldExpression right) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:").append(type).append(">\n")
              .append(indent).append("  ");
            left.appendXml(sb);
            sb.append("\n")
              .append(indent).append("  ");
            right.appendXml(sb);
            sb.append("\n")
              .append(indent).append("</ogc:").append(type).append(">\n");
        };
    }

    private static OgcFilter logical(String type, OgcFilter[] filters) {
        return (sb, indent) -> {
            sb.append(indent).append("<ogc:").append(type).append(">\n");
            for (OgcFilter f : filters) {
                f.appendXml(sb, indent + "  ");
            }
            sb.append(indent).append("</ogc:").append(type).append(">\n");
        };
    }

    private static SldExpression prop(String name) {
        return SldExpression.property(name);
    }
    private static SldExpression lit(Object value) {
        return SldExpression.literal(value);
    }
    private static String esc(String s) {
        return SldBuilder.esc(s);
    }
}
