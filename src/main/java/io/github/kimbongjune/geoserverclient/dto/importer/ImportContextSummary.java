package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * Summary entry returned when listing import contexts.
 *
 * <p>Maps each item in the {@code imports[]} array returned by {@code GET /rest/imports}.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportContextSummary {

    private long id;
    private String href;
    private String state;

    public ImportContextSummary() {}

    public long getId()     { return id; }
    public String getHref() { return href; }
    public String getState(){ return state; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
