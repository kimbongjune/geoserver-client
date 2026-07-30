package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonValue;

/** Seed operation type for the {@code <type>} element in {@code POST /gwc/rest/seed/{layer}.xml}. */
public enum GwcSeedType {
    SEED("seed"),
    RESEED("reseed"),
    TRUNCATE("truncate");

    private final String value;

    GwcSeedType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }
}
