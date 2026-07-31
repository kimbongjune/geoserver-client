package io.github.kimbongjune.geoserverclient.dto.style;

/** Contrast enhancement algorithm for raster channel and global contrast enhancement. */
public enum ContrastMethod {
    /** Normalize contrast enhancement. */
    NORMALIZE("Normalize"),
    /** Histogram equalization contrast enhancement. */
    HISTOGRAM("Histogram");

    private final String tag;

    /**
     * Constructs a {@code ContrastMethod} with the given SLD tag name.
     * @param tag the SLD XML element name
     */
    ContrastMethod(String tag) {
      this.tag = tag;
  }
    /**
     * Returns the SLD XML element name, e.g. {@code "Normalize"}.
     *
     * @return the SLD tag string
     */
    public String getTag() {
        return tag;
    }
}
