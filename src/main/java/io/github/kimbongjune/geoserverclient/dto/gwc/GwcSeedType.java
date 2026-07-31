package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonValue;

/** Seed operation type for the {@code <type>} element in {@code POST /gwc/rest/seed/{layer}.xml}. */
public enum GwcSeedType {
    /** Seed: pre-generates tiles that do not yet exist. */
    SEED("seed"),
    /** Reseed: regenerates all tiles regardless of whether they exist. */
    RESEED("reseed"),
    /** Truncate: deletes cached tiles. */
    TRUNCATE("truncate");

    private final String value;

    GwcSeedType(String value) {
      this.value = value;
  }

    /**
     * Returns the JSON/XML serialization value.
     * @return the lowercase value string ({@code "seed"}, {@code "reseed"}, or {@code "truncate"})
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}
