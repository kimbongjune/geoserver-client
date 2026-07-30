package io.github.kimbongjune.geoserverclient.dto.gwc;

/** Kill scope for a GWC seed operation ({@code POST /gwc/rest/seed} with {@code kill_all} parameter). */
public enum GwcKillType {
    RUNNING("running"),
    PENDING("pending"),
    ALL("all");

    private final String value;

    GwcKillType(String value) { this.value = value; }

    public String getValue() { return value; }
}
