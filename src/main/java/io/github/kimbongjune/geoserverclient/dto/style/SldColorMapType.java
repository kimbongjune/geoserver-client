package io.github.kimbongjune.geoserverclient.dto.style;

/** Color map interpolation type for {@link SldBuilder} raster symbolizers. */
public enum SldColorMapType {
    /** Smooth gradient between entries. */
    RAMP("ramp"),
    /** Flat colour bands between entries. */
    INTERVALS("intervals"),
    /** Exact value matching only. */
    VALUES("values");

    private final String value;

    SldColorMapType(String value) {
      this.value = value;
  }

    public String getValue() {
        return value;
    }
}
