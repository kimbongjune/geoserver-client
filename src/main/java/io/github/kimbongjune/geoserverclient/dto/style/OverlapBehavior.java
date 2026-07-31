package io.github.kimbongjune.geoserverclient.dto.style;

/** Raster overlap rendering policy for {@link SldBuilder} raster symbolizers. */
public enum OverlapBehavior {
    /** Render the latest raster on top. */
    LATEST_ON_TOP("LATEST_ON_TOP"),
    /** Render the earliest raster on top. */
    EARLIEST_ON_TOP("EARLIEST_ON_TOP"),
    /** Render an average of overlapping rasters. */
    AVERAGE("AVERAGE"),
    /** Render a random raster when overlapping. */
    RANDOM("RANDOM");

    private final String value;
    OverlapBehavior(String value) {
      this.value = value;
  }
    /** @return the SLD OverlapBehavior value string */
    public String getValue() {
        return value;
    }
}
