package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for an import task (detail).
 *
 * <p>Maps the {@code "task"} object returned by
 * {@code GET /rest/imports/{id}/tasks/{taskId}}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTask {

    private long id;
    private String href;
    private String state;
    private String updateMode;
    private String progress;

    /** Constructs an empty {@code ImportTask} for deserialization. */
    public ImportTask() {}

    /** @return the task ID */
    public long getId() {
        return id;
    }
    /** @return the href to this task resource */
    public String getHref() {
        return href;
    }
    /** @return the task state (e.g. {@code "READY"}, {@code "COMPLETE"}) */
    public String getState() {
        return state;
    }
    /** @return the update mode (e.g. {@code "CREATE"}, {@code "REPLACE"}) */
    public String getUpdateMode() {
        return updateMode;
    }
    /** @return the progress URL for this task */
    public String getProgress() {
        return progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportTask that = (ImportTask) o;
        return Objects.equals(id, that.id)
                && Objects.equals(href, that.href)
                && Objects.equals(state, that.state)
                && Objects.equals(updateMode, that.updateMode)
                && Objects.equals(progress, that.progress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, state, updateMode, progress);
    }

    @Override
    public String toString() {
        return "ImportTask{" +
                "id=" + id +
                ", href=" + href +
                ", state=" + state +
                ", updateMode=" + updateMode +
                ", progress=" + progress +
                '}';
    }
}
