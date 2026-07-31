package io.github.kimbongjune.geoserverclient.dto.style;

/**
 * GML 2 geometry representation for use in OGC spatial filter predicates.
 *
 * <p>Instances are created exclusively via the static factory methods on this class and
 * passed to the spatial filter factories in {@link OgcFilters} (e.g. {@link OgcFilters#bbox},
 * {@link OgcFilters#intersects}, {@link OgcFilters#dWithin}). Each factory method produces
 * an object that serializes to the corresponding GML 2 XML fragment when embedded in an
 * SLD 1.0 {@code <ogc:Filter>}.
 *
 * <p>All coordinate arrays use <em>interleaved x,y pairs</em> in the order the spatial
 * reference system expects (longitude, latitude for geographic CRS such as EPSG:4326).
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
 * import static io.github.kimbongjune.geoserverclient.dto.style.SldGeometry.*;
 *
 * // Bounding-box filter
 * OgcFilter worldBbox = bbox("the_geom", envelope(-180, -90, 180, 90, "EPSG:4326"));
 *
 * // Point intersection
 * OgcFilter atOrigin = intersects("the_geom", point(0, 0, "EPSG:4326"));
 *
 * // Line crossing
 * OgcFilter crossLine = crosses("the_geom",
 *     lineString("EPSG:4326", -74.0, 40.7, -73.9, 40.8));
 *
 * // Polygon within
 * OgcFilter inSquare = within("the_geom",
 *     polygon("EPSG:4326", 0, 0, 10, 0, 10, 10, 0, 10, 0, 0));
 * }</pre>
 *
 * @see OgcFilters
 * @since 1.0.0
 */
public abstract class SldGeometry {

    /**
     * Creates a GML 2 {@code <gml:Box>} envelope for use in {@code BBOX} spatial filters.
     *
     * <p>Generates:
     * <pre>{@code
     * <gml:Box srsName="EPSG:4326">
     *   <gml:coordinates>minX,minY maxX,maxY</gml:coordinates>
     * </gml:Box>
     * }</pre>
     *
     * @param minX     western boundary (minimum X / minimum longitude in geographic CRS)
     * @param minY     southern boundary (minimum Y / minimum latitude in geographic CRS)
     * @param maxX     eastern boundary (maximum X / maximum longitude in geographic CRS)
     * @param maxY     northern boundary (maximum Y / maximum latitude in geographic CRS)
     * @param srsName  the spatial reference system identifier (e.g. {@code "EPSG:4326"});
     *                 must not be {@code null} or blank
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Box>} element
     */
    public static SldGeometry envelope(double minX, double minY, double maxX, double maxY, String srsName) {
        return new Envelope(minX, minY, maxX, maxY, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:Box>} envelope without an explicit SRS.
     *
     * <p>The server applies its layer-native CRS. Prefer
     * {@link #envelope(double, double, double, double, String)} in cross-CRS scenarios.
     *
     * @param minX western boundary
     * @param minY southern boundary
     * @param maxX eastern boundary
     * @param maxY northern boundary
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Box>} element
     */
    public static SldGeometry envelope(double minX, double minY, double maxX, double maxY) {
        return new Envelope(minX, minY, maxX, maxY, null);
    }

    /**
     * Creates a GML 2 {@code <gml:Point>} geometry.
     *
     * <p>Generates:
     * <pre>{@code
     * <gml:Point srsName="EPSG:4326">
     *   <gml:coordinates>x,y</gml:coordinates>
     * </gml:Point>
     * }</pre>
     *
     * @param x       the X coordinate (longitude for geographic CRS)
     * @param y       the Y coordinate (latitude for geographic CRS)
     * @param srsName the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Point>} element
     */
    public static SldGeometry point(double x, double y, String srsName) {
        return new GmlPoint(x, y, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:Point>} geometry without an explicit SRS.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Point>} element
     */
    public static SldGeometry point(double x, double y) {
        return new GmlPoint(x, y, null);
    }

    /**
     * Creates a GML 2 {@code <gml:LineString>} geometry from interleaved x,y coordinate pairs.
     *
     * <p>Generates:
     * <pre>{@code
     * <gml:LineString srsName="EPSG:4326">
     *   <gml:coordinates>x1,y1 x2,y2 x3,y3</gml:coordinates>
     * </gml:LineString>
     * }</pre>
     *
     * @param srsName the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @param coords  interleaved x,y coordinate pairs: {@code x1, y1, x2, y2, …}.
     *                Must contain an even number of values (at least 4 for a valid line).
     * @return a {@link SldGeometry} that serializes to a {@code <gml:LineString>} element
     */
    public static SldGeometry lineString(String srsName, double... coords) {
        return new GmlLineString(coords, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:LineString>} geometry without an explicit SRS.
     *
     * @param coords interleaved x,y coordinate pairs: {@code x1, y1, x2, y2, …}
     * @return a {@link SldGeometry} that serializes to a {@code <gml:LineString>} element
     */
    public static SldGeometry lineString(double... coords) {
        return new GmlLineString(coords, null);
    }

    /**
     * Creates a GML 2 {@code <gml:Polygon>} geometry from exterior ring coordinates.
     *
     * <p>Only the exterior (outer) ring is supported; interior rings (holes) are not modelled.
     * The ring must be closed — the last coordinate pair must equal the first.
     *
     * <p>Generates:
     * <pre>{@code
     * <gml:Polygon srsName="EPSG:4326">
     *   <gml:outerBoundaryIs>
     *     <gml:LinearRing>
     *       <gml:coordinates>0,0 10,0 10,10 0,10 0,0</gml:coordinates>
     *     </gml:LinearRing>
     *   </gml:outerBoundaryIs>
     * </gml:Polygon>
     * }</pre>
     *
     * @param srsName     the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @param shellCoords interleaved x,y coordinate pairs for the exterior ring:
     *                    {@code x1, y1, x2, y2, …, x1, y1}. The ring must close
     *                    (first coordinate == last coordinate).
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Polygon>} element
     */
    public static SldGeometry polygon(String srsName, double... shellCoords) {
        return new GmlPolygon(shellCoords, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:Polygon>} geometry without an explicit SRS.
     *
     * @param shellCoords interleaved x,y exterior ring coordinate pairs (must close)
     * @return a {@link SldGeometry} that serializes to a {@code <gml:Polygon>} element
     */
    public static SldGeometry polygon(double... shellCoords) {
        return new GmlPolygon(shellCoords, null);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiPoint>} geometry from interleaved x,y coordinate pairs.
     *
     * <p>Each consecutive x,y pair becomes one {@code <gml:pointMember>} inside the
     * collection. Generates:
     * <pre>{@code
     * <gml:MultiPoint srsName="EPSG:4326">
     *   <gml:pointMember><gml:Point><gml:coordinates>x1,y1</gml:coordinates></gml:Point></gml:pointMember>
     *   <gml:pointMember><gml:Point><gml:coordinates>x2,y2</gml:coordinates></gml:Point></gml:pointMember>
     * </gml:MultiPoint>
     * }</pre>
     *
     * @param srsName the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @param coords  interleaved x,y coordinate pairs: {@code x1, y1, x2, y2, …}.
     *                Must contain an even number of values.
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiPoint>} element
     */
    public static SldGeometry multiPoint(String srsName, double... coords) {
        return new GmlMultiPoint(coords, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiPoint>} geometry without an explicit SRS.
     *
     * @param coords interleaved x,y coordinate pairs: {@code x1, y1, x2, y2, …}
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiPoint>} element
     */
    public static SldGeometry multiPoint(double... coords) {
        return new GmlMultiPoint(coords, null);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiLineString>} geometry.
     *
     * <p>Each inner array represents one line member and must contain interleaved x,y pairs.
     * Generates one {@code <gml:lineStringMember>} per inner array.
     *
     * @param srsName    the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @param lineCoords each inner {@code double[]} contains the interleaved x,y coordinate
     *                   pairs for one line segment (at least 4 values: two points)
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiLineString>} element
     */
    public static SldGeometry multiLineString(String srsName, double[]... lineCoords) {
        return new GmlMultiLineString(lineCoords, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiLineString>} geometry without an explicit SRS.
     *
     * @param lineCoords each inner {@code double[]} is the interleaved x,y coords for one line
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiLineString>} element
     */
    public static SldGeometry multiLineString(double[]... lineCoords) {
        return new GmlMultiLineString(lineCoords, null);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiPolygon>} geometry (exterior rings only).
     *
     * <p>Each inner array represents one polygon exterior ring and must contain interleaved
     * x,y pairs. Interior rings (holes) are not modelled. Each ring must close
     * (first coordinate == last coordinate). Generates one {@code <gml:polygonMember>}
     * per inner array.
     *
     * @param srsName      the spatial reference system identifier (e.g. {@code "EPSG:4326"})
     * @param polygonRings each inner {@code double[]} contains the interleaved x,y exterior
     *                     ring coordinate pairs for one polygon (must close)
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiPolygon>} element
     */
    public static SldGeometry multiPolygon(String srsName, double[]... polygonRings) {
        return new GmlMultiPolygon(polygonRings, srsName);
    }

    /**
     * Creates a GML 2 {@code <gml:MultiPolygon>} geometry without an explicit SRS.
     *
     * @param polygonRings each inner {@code double[]} is the exterior ring coords for one polygon
     * @return a {@link SldGeometry} that serializes to a {@code <gml:MultiPolygon>} element
     */
    public static SldGeometry multiPolygon(double[]... polygonRings) {
        return new GmlMultiPolygon(polygonRings, null);
    }

    /**
     * Creates a GML 2 {@code <gml:GeometryCollection>} from heterogeneous member geometries.
     *
     * <p>Any mix of point, line, polygon, multi-*, or nested collection geometries may be
     * supplied. Each member is wrapped in a {@code <gml:geometryMember>} element. The
     * collection itself carries no SRS — each member geometry carries its own.
     *
     * @param members the member geometries; must not be {@code null} or empty
     * @return a {@link SldGeometry} that serializes to a {@code <gml:GeometryCollection>} element
     */
    public static SldGeometry geometryCollection(SldGeometry... members) {
        return new GmlGeometryCollection(members);
    }

    /**
     * Appends the GML XML fragment for this geometry to {@code sb}.
     *
     * <p>Called by OGC spatial filter implementations (e.g. {@link OgcFilters#bbox}) to embed
     * this geometry's XML into the containing filter element. No surrounding whitespace is
     * added before the first element — {@code indent} is prepended to each emitted line.
     *
     * @param sb     the {@link StringBuilder} to append the GML fragment to
     * @param indent the whitespace prefix for the outermost GML element
     */
    abstract void appendGml(StringBuilder sb, String indent);

    static String fmt(double v) {
        return SldBuilder.fmt(v);
    }

    // ── Implementations ───────────────────────────────────────────────────

    private static final class Envelope extends SldGeometry {
        private final double minX, minY, maxX, maxY;
        private final String srsName;
        Envelope(double minX, double minY, double maxX, double maxY, String srsName) {
            this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
            this.srsName = srsName;
        }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:Box");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n")
              .append(indent).append("  <gml:coordinates>")
              .append(fmt(minX)).append(",").append(fmt(minY)).append(" ")
              .append(fmt(maxX)).append(",").append(fmt(maxY))
              .append("</gml:coordinates>\n")
              .append(indent).append("</gml:Box>\n");
        }
    }

    private static final class GmlPoint extends SldGeometry {
        private final double x, y;
        private final String srsName;
        GmlPoint(double x, double y, String srsName) {
          this.x = x; this.y = y; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:Point");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n")
              .append(indent).append("  <gml:coordinates>").append(fmt(x)).append(",").append(fmt(y)).append("</gml:coordinates>\n")
              .append(indent).append("</gml:Point>\n");
        }
    }

    private static final class GmlLineString extends SldGeometry {
        private final double[] coords;
        private final String srsName;
        GmlLineString(double[] coords, String srsName) {
          this.coords = coords; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:LineString");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n").append(indent).append("  <gml:coordinates>");
            for (int i = 0; i < coords.length; i += 2) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(fmt(coords[i])).append(",").append(fmt(coords[i + 1]));
            }
            sb.append("</gml:coordinates>\n").append(indent).append("</gml:LineString>\n");
        }
    }

    private static final class GmlPolygon extends SldGeometry {
        private final double[] shell;
        private final String srsName;
        GmlPolygon(double[] shell, String srsName) {
          this.shell = shell; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:Polygon");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n")
              .append(indent).append("  <gml:outerBoundaryIs>\n")
              .append(indent).append("    <gml:LinearRing>\n")
              .append(indent).append("      <gml:coordinates>");
            for (int i = 0; i < shell.length; i += 2) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(fmt(shell[i])).append(",").append(fmt(shell[i + 1]));
            }
            sb.append("</gml:coordinates>\n")
              .append(indent).append("    </gml:LinearRing>\n")
              .append(indent).append("  </gml:outerBoundaryIs>\n")
              .append(indent).append("</gml:Polygon>\n");
        }
    }

    private static final class GmlMultiPoint extends SldGeometry {
        private final double[] coords;
        private final String srsName;
        GmlMultiPoint(double[] coords, String srsName) {
          this.coords = coords; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:MultiPoint");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n");
            for (int i = 0; i < coords.length; i += 2) {
                sb.append(indent).append("  <gml:pointMember>\n")
                  .append(indent).append("    <gml:Point>\n")
                  .append(indent).append("      <gml:coordinates>")
                  .append(fmt(coords[i])).append(",").append(fmt(coords[i + 1]))
                  .append("</gml:coordinates>\n")
                  .append(indent).append("    </gml:Point>\n")
                  .append(indent).append("  </gml:pointMember>\n");
            }
            sb.append(indent).append("</gml:MultiPoint>\n");
        }
    }

    private static final class GmlMultiLineString extends SldGeometry {
        private final double[][] lines;
        private final String srsName;
        GmlMultiLineString(double[][] lines, String srsName) {
          this.lines = lines; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:MultiLineString");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n");
            for (double[] line : lines) {
                sb.append(indent).append("  <gml:lineStringMember>\n")
                  .append(indent).append("    <gml:LineString>\n")
                  .append(indent).append("      <gml:coordinates>");
                for (int i = 0; i < line.length; i += 2) {
                    if (i > 0) {
                        sb.append(" ");
                    }
                    sb.append(fmt(line[i])).append(",").append(fmt(line[i + 1]));
                }
                sb.append("</gml:coordinates>\n")
                  .append(indent).append("    </gml:LineString>\n")
                  .append(indent).append("  </gml:lineStringMember>\n");
            }
            sb.append(indent).append("</gml:MultiLineString>\n");
        }
    }

    private static final class GmlMultiPolygon extends SldGeometry {
        private final double[][] rings;
        private final String srsName;
        GmlMultiPolygon(double[][] rings, String srsName) {
          this.rings = rings; this.srsName = srsName;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:MultiPolygon");
            if (srsName != null) {
                sb.append(" srsName=\"").append(SldBuilder.esc(srsName)).append("\"");
            }
            sb.append(">\n");
            for (double[] ring : rings) {
                sb.append(indent).append("  <gml:polygonMember>\n")
                  .append(indent).append("    <gml:Polygon>\n")
                  .append(indent).append("      <gml:outerBoundaryIs>\n")
                  .append(indent).append("        <gml:LinearRing>\n")
                  .append(indent).append("          <gml:coordinates>");
                for (int i = 0; i < ring.length; i += 2) {
                    if (i > 0) {
                        sb.append(" ");
                    }
                    sb.append(fmt(ring[i])).append(",").append(fmt(ring[i + 1]));
                }
                sb.append("</gml:coordinates>\n")
                  .append(indent).append("        </gml:LinearRing>\n")
                  .append(indent).append("      </gml:outerBoundaryIs>\n")
                  .append(indent).append("    </gml:Polygon>\n")
                  .append(indent).append("  </gml:polygonMember>\n");
            }
            sb.append(indent).append("</gml:MultiPolygon>\n");
        }
    }

    private static final class GmlGeometryCollection extends SldGeometry {
        private final SldGeometry[] members;
        GmlGeometryCollection(SldGeometry[] members) {
          this.members = members;
      }
        @Override void appendGml(StringBuilder sb, String indent) {
            sb.append(indent).append("<gml:GeometryCollection>\n");
            for (SldGeometry m : members) {
                sb.append(indent).append("  <gml:geometryMember>\n");
                m.appendGml(sb, indent + "    ");
                sb.append(indent).append("  </gml:geometryMember>\n");
            }
            sb.append(indent).append("</gml:GeometryCollection>\n");
        }
    }
}
