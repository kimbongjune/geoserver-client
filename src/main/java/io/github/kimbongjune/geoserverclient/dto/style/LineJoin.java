package io.github.kimbongjune.geoserverclient.dto.style;

/** SVG {@code stroke-linejoin} values for {@link SldBuilder} line and polygon stroke symbolizers. */
public enum LineJoin {
    /** Miter (sharp corner) line join. */
    MITRE("mitre"),
    /** Round line join. */
    ROUND("round"),
    /** Bevel (cut corner) line join. */
    BEVEL("bevel");

    private final String value;
    LineJoin(String value) {
      this.value = value;
  }
    /** @return the SVG stroke-linejoin attribute value */
    public String getValue() {
        return value;
    }
}
