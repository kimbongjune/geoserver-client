package io.github.kimbongjune.geoserverclient.dto.style;

/** SVG {@code stroke-linecap} values for {@link SldBuilder} line and polygon stroke symbolizers. */
public enum LineCap {
    /** Flat (no extension beyond line end) line cap. */
    BUTT("butt"),
    /** Round line cap. */
    ROUND("round"),
    /** Square line cap (extends beyond line end by half the stroke width). */
    SQUARE("square");

    private final String value;
    LineCap(String value) {
      this.value = value;
  }
    /** @return the SVG stroke-linecap attribute value */
    public String getValue() {
        return value;
    }
}
