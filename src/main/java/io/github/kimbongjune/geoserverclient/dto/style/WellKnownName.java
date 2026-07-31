package io.github.kimbongjune.geoserverclient.dto.style;

/** OGC SLD 1.0 well-known mark names for {@link SldBuilder} point symbolizers. */
public enum WellKnownName {
    CIRCLE("circle"),
    SQUARE("square"),
    TRIANGLE("triangle"),
    STAR("star"),
    CROSS("cross"),
    X("x");

    private final String value;

    WellKnownName(String value) {
      this.value = value;
  }

    public String getValue() {
        return value;
    }
}
