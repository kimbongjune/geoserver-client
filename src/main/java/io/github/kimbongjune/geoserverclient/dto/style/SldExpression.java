package io.github.kimbongjune.geoserverclient.dto.style;

/**
 * An OGC expression that can appear as a value within SLD 1.0 symbolizer properties or
 * OGC Filter comparison operands.
 *
 * <p>Mirrors the GeoTools {@code Expression} hierarchy — specifically
 * {@code PropertyName}, {@code Literal}, {@code Function}, and arithmetic nodes — without
 * requiring any GeoTools dependency. Instances are created via the static factory methods
 * on this class and are immutable.
 *
 * <h2>Where expressions are accepted</h2>
 * <ul>
 *   <li>Symbolizer numeric properties: {@code size}, {@code rotation}, {@code opacity}
 *       (e.g. in {@link SldBuilder.PointBuilder#size(SldExpression)},
 *       {@link SldBuilder.PointBuilder#rotation(SldExpression)})</li>
 *   <li>Text label content: {@link SldBuilder.RuleBuilder#label(SldExpression)}</li>
 *   <li>Filter comparison operands: {@link OgcFilters#equalTo(SldExpression, SldExpression)}
 *       and its sibling methods</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * import static io.github.kimbongjune.geoserverclient.dto.style.SldExpression.*;
 * import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
 *
 * // Proportional symbol — symbol size driven by a feature attribute
 * rule.mark(WellKnownName.CIRCLE)
 *     .fill("#FF0000")
 *     .size(property("magnitude"))
 *     .end();
 *
 * // Rotation driven by a feature attribute
 * rule.mark(WellKnownName.TRIANGLE)
 *     .fill("#0000FF").size(12)
 *     .rotation(property("bearing"))
 *     .end();
 *
 * // Dynamic label from two concatenated attributes
 * rule.label(function("strConcat",
 *         property("name"),
 *         literal(" ("),
 *         property("type"),
 *         literal(")")));
 *
 * // OGC function in a filter comparison
 * OgcFilter f = equalTo(
 *     function("strToLowerCase", property("road_type")),
 *     literal("highway"));
 *
 * // Arithmetic: label showing population in thousands
 * rule.label(function("numberFormat",
 *         literal("0.0"),
 *         div(property("population"), literal(1000))));
 * }</pre>
 *
 * @see OgcFilters
 * @see SldBuilder
 * @since 1.0.0
 */
public abstract class SldExpression {

    /**
     * Creates a {@code <ogc:PropertyName>} expression that references a feature attribute by name.
     *
     * <p>At render/evaluation time, the value of the named attribute is substituted.
     * Equivalent to GeoTools {@code FilterFactory.property(String)}.
     *
     * <p>Generates: {@code <ogc:PropertyName>name</ogc:PropertyName>}
     *
     * @param name the attribute (property) name as it appears in the feature type; must not
     *             be {@code null}
     * @return a {@link SldExpression} representing the property reference
     */
    public static SldExpression property(String name) {
        return new PropertyName(name);
    }

    /**
     * Creates an {@code <ogc:Literal>} expression for a constant value.
     *
     * <p>The value is converted to a string via {@link Object#toString()} and XML-escaped.
     * Equivalent to GeoTools {@code FilterFactory.literal(Object)}.
     *
     * <p>Generates: {@code <ogc:Literal>value</ogc:Literal>}
     *
     * @param value the literal value; {@link Number} and {@link String} are the most common
     *              types; {@code toString()} is called for all other types
     * @return a {@link SldExpression} representing the constant literal
     */
    public static SldExpression literal(Object value) {
        return new Literal(value);
    }

    /**
     * Creates an {@code <ogc:Function>} expression that invokes a named OGC function.
     *
     * <p>GeoServer ships many built-in OGC functions. Commonly used ones include:
     * <ul>
     *   <li>{@code strToLowerCase(expr)} — converts a string to lower case</li>
     *   <li>{@code strConcat(expr, expr, …)} — concatenates multiple expressions as strings</li>
     *   <li>{@code numberFormat(pattern, number)} — formats a number using a Java
     *       {@code DecimalFormat} pattern</li>
     *   <li>{@code categorize(value, v1, t1, v2, t2, …)} — piecewise constant function</li>
     *   <li>{@code interpolate(value, d1, v1, d2, v2, …)} — linear interpolation</li>
     *   <li>{@code recode(value, k1, v1, k2, v2, …)} — explicit value mapping (lookup table)</li>
     * </ul>
     *
     * <p>Generates: {@code <ogc:Function name="name">arg1 arg2 …</ogc:Function>}
     *
     * @param name the OGC function name as registered in GeoServer; must not be {@code null}
     * @param args the function argument expressions; may be empty for zero-argument functions
     * @return a {@link SldExpression} representing the function invocation
     */
    public static SldExpression function(String name, SldExpression... args) {
        return new Function(name, args);
    }

    /**
     * Creates an {@code <ogc:Add>} arithmetic expression — {@code left + right}.
     *
     * <p>Generates: {@code <ogc:Add>left right</ogc:Add>}
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link SldExpression} representing addition
     */
    public static SldExpression add(SldExpression left, SldExpression right) {
        return new Arithmetic("Add", left, right);
    }

    /**
     * Creates an {@code <ogc:Sub>} arithmetic expression — {@code left - right}.
     *
     * <p>Generates: {@code <ogc:Sub>left right</ogc:Sub>}
     *
     * @param left  the left operand (minuend)
     * @param right the right operand (subtrahend)
     * @return a {@link SldExpression} representing subtraction
     */
    public static SldExpression sub(SldExpression left, SldExpression right) {
        return new Arithmetic("Sub", left, right);
    }

    /**
     * Creates an {@code <ogc:Mul>} arithmetic expression — {@code left * right}.
     *
     * <p>Generates: {@code <ogc:Mul>left right</ogc:Mul>}
     *
     * @param left  the left operand
     * @param right the right operand
     * @return a {@link SldExpression} representing multiplication
     */
    public static SldExpression mul(SldExpression left, SldExpression right) {
        return new Arithmetic("Mul", left, right);
    }

    /**
     * Creates an {@code <ogc:Div>} arithmetic expression — {@code left / right}.
     *
     * <p>Generates: {@code <ogc:Div>left right</ogc:Div>}
     *
     * @param left  the dividend
     * @param right the divisor
     * @return a {@link SldExpression} representing division
     */
    public static SldExpression div(SldExpression left, SldExpression right) {
        return new Arithmetic("Div", left, right);
    }

    /**
     * Appends the XML fragment for this expression to {@code sb}.
     *
     * <p>The fragment is appended inline — no leading or trailing whitespace or newline is
     * added. Callers are responsible for any required surrounding whitespace.
     *
     * @param sb the {@link StringBuilder} to append the expression XML to; must not be
     *           {@code null}
     */
    abstract void appendXml(StringBuilder sb);

    // ── Implementations ───────────────────────────────────────────────────

    private static final class PropertyName extends SldExpression {
        private final String name;
        PropertyName(String name) {
          this.name = name;
      }
        @Override void appendXml(StringBuilder sb) {
            sb.append("<ogc:PropertyName>").append(SldBuilder.esc(name)).append("</ogc:PropertyName>");
        }
    }

    private static final class Literal extends SldExpression {
        private final Object value;
        Literal(Object value) {
          this.value = value;
      }
        @Override void appendXml(StringBuilder sb) {
            sb.append("<ogc:Literal>").append(SldBuilder.esc(String.valueOf(value))).append("</ogc:Literal>");
        }
    }

    private static final class Function extends SldExpression {
        private final String name;
        private final SldExpression[] args;
        Function(String name, SldExpression[] args) {
          this.name = name; this.args = args;
      }
        @Override void appendXml(StringBuilder sb) {
            sb.append("<ogc:Function name=\"").append(SldBuilder.esc(name)).append("\">");
            for (SldExpression arg : args) {
                arg.appendXml(sb);
            }
            sb.append("</ogc:Function>");
        }
    }

    private static final class Arithmetic extends SldExpression {
        private final String op;
        private final SldExpression left, right;
        Arithmetic(String op, SldExpression left, SldExpression right) {
            this.op = op; this.left = left; this.right = right;
        }
        @Override void appendXml(StringBuilder sb) {
            sb.append("<ogc:").append(op).append(">");
            left.appendXml(sb);
            right.appendXml(sb);
            sb.append("</ogc:").append(op).append(">");
        }
    }
}
