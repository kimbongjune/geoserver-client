package io.github.kimbongjune.geoserverclient.dto.style;

/**
 * A serializable OGC Filter predicate for use within SLD 1.0 {@code <Rule>} elements.
 *
 * <p>This interface represents a single OGC Filter expression node (comparison, spatial
 * predicate, logical operator, or feature ID selector) that knows how to emit its own
 * XML fragment. All concrete implementations are lambdas created by the factory methods
 * in {@link OgcFilters}; callers never implement this interface directly.
 *
 * <p>Instances are composable via {@link OgcFilters#and}, {@link OgcFilters#or}, and
 * {@link OgcFilters#not} to build arbitrarily nested filter trees.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * import static io.github.kimbongjune.geoserverclient.dto.style.OgcFilters.*;
 *
 * OgcFilter f = and(
 *     greaterThan("population", 1_000_000),
 *     like("name", "New*")
 * );
 *
 * SldBuilder.create("cities")
 *     .rule("large-cities").filter(f)
 *         .point(WellKnownName.CIRCLE, "#FF0000", 10)
 *     .build();
 * }</pre>
 *
 * @see OgcFilters
 * @since 1.0.0
 */
@FunctionalInterface
public interface OgcFilter {

    /**
     * Appends the OGC Filter XML fragment for this predicate to {@code sb}.
     *
     * <p>The fragment should be well-indented using {@code indent} as the prefix for the
     * outermost element; child elements are indented relative to that. No surrounding
     * whitespace is prepended before {@code indent}.
     *
     * <p>Callers (typically {@link SldBuilder} internals) wrap the emitted fragment in
     * {@code <ogc:Filter>…</ogc:Filter>}; this method emits only the inner content.
     *
     * @param sb     the {@link StringBuilder} to append the XML fragment to; must not be
     *               {@code null}
     * @param indent the whitespace prefix for the outermost XML element (e.g. {@code "    "})
     */
    void appendXml(StringBuilder sb, String indent);
}
