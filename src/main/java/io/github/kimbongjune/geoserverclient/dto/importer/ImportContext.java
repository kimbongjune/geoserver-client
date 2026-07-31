package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for an import context (detail).
 *
 * <p>Maps the {@code "import"} object returned by
 * {@code POST /rest/imports} or {@code GET /rest/imports/{id}}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportContext {

    private long id;
    private String href;
    private String state;
    private boolean archive;
    private List<ImportTask> tasks;

    /** Constructs an empty {@code ImportContext} for deserialization. */
    public ImportContext() {}

    /** @return the import context ID */
    public long getId() {
        return id;
    }
    /** @return the href to this import context resource */
    public String getHref() {
        return href;
    }
    /** @return the import state (e.g. {@code "READY"}, {@code "COMPLETE"}) */
    public String getState() {
        return state;
    }
    /** @return {@code true} if the import is archived */
    public boolean isArchive() {
        return archive;
    }
    /** @return the list of tasks in this import context */
    public List<ImportTask> getTasks() {
        return tasks == null ? null : Collections.unmodifiableList(tasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportContext that = (ImportContext) o;
        return Objects.equals(id, that.id)
                && Objects.equals(href, that.href)
                && Objects.equals(state, that.state)
                && Objects.equals(archive, that.archive)
                && Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, state, archive, tasks);
    }

    @Override
    public String toString() {
        return "ImportContext{" +
                "id=" + id +
                ", href=" + href +
                ", state=" + state +
                ", archive=" + archive +
                ", tasks=" + tasks +
                '}';
    }
}
