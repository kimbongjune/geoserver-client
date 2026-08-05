package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * Summary entry returned when listing import contexts.
 *
 * <p>Maps each item in the {@code imports[]} array returned by {@code GET /rest/imports}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportContextSummary {

    private long id;
    private String href;
    private ImportState state;

    /** Constructs an empty {@code ImportContextSummary} for deserialization. */
    public ImportContextSummary() {}

    /** @return the import context ID */
    public long getId() {
        return id;
    }
    /** @return the href to this import context resource */
    public String getHref() {
        return href;
    }
    /** @return the import state */
    public ImportState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportContextSummary that = (ImportContextSummary) o;
        return Objects.equals(id, that.id)
                && Objects.equals(href, that.href)
                && Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, state);
    }

    @Override
    public String toString() {
        return "ImportContextSummary{" +
                "id=" + id +
                ", href=" + href +
                ", state=" + state +
                '}';
    }
}
