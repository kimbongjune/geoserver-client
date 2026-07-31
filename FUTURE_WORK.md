# Future Work (local notes only — not committed)

## Typed SLD / style builder

**Problem:** creating a style currently requires the caller to write raw SLD XML text and pass it
via `StyleContent.of(sldBody)`. Every other manager in this library is 100% DTO (no raw
JSON/XML strings ever cross a method boundary) — styles are the one place where that isn't true,
because SLD content itself is a full stylesheet grammar (symbolizers, rules, filters, color maps,
etc.) with no typed model in this library.

**Goal:** a Java builder/DTO API for constructing SLD documents programmatically, so a caller
never has to hand-write XML — e.g. something like:

```java
StyleContent sld = SldBuilder.namedLayer("myStyle")
        .rule()
            .pointSymbolizer().mark(WellKnownName.CIRCLE).fill("#FF0000").size(6)
        .build();
client.styles().create(sld, "myStyle");
```

**Scope note:** this is a much bigger undertaking than anything else in this library — it means
modeling a real subset (or all) of the OGC SLD 1.0/1.1 grammar (NamedLayer, UserStyle,
FeatureTypeStyle, Rule, the symbolizer types — Point/Line/Polygon/Text/Raster — Filter/Expression,
ColorMap, etc.), analogous to what GeoTools' `org.geotools.styling.StyleBuilder` /
`org.geotools.styling` package already does. Before writing any of this:

- Pull the exact SLD 1.0.0 and SE 1.1.0 (SLD 1.1) XSD schemas from the OGC spec, not just
  examples — same "don't guess field names, verify against the real spec" discipline this project
  already used for the REST API itself (see `docs/REST_API_MASTER_LIST.md`'s 3-way research
  approach: official docs + community/forums + source code cross-check).
- Decide scope up front: full SLD grammar, or a pragmatic subset covering the common symbolizer
  cases (point/line/polygon/raster with basic fills/strokes) first, expanding later.
- YSLD/CSS/MapBox styles (which `StyleManager` also supports, per GeoServer's style format
  extensions) would need the same treatment eventually, or an explicit "SLD only, for now" scope
  note in the Javadoc.
- This should get the same "one thing at a time, integration-test each piece against a live
  GeoServer" treatment as every other manager in this library — not a big-bang builder class
  dropped in all at once.

Deferred for now — revisit when there's time to do the spec research properly.
