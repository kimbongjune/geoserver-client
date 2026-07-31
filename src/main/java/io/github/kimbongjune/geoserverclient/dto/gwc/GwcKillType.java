package io.github.kimbongjune.geoserverclient.dto.gwc;

/** Kill scope for a GWC seed operation ({@code POST /gwc/rest/seed} with {@code kill_all} parameter). */
public enum GwcKillType {
    /** Kill only currently running tasks. */
    RUNNING("running"),
    /** Kill only pending (queued) tasks. */
    PENDING("pending"),
    /** Kill all tasks (running and pending). */
    ALL("all");

    private final String value;

    GwcKillType(String value) {
      this.value = value;
  }

    /** @return the kill type value ({@code "running"}, {@code "pending"}, or {@code "all"}) */
    public String getValue() {
        return value;
    }
}
