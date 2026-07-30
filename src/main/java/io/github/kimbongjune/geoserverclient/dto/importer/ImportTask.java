package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for an import task (detail).
 *
 * <p>Maps the {@code "task"} object returned by
 * {@code GET /rest/imports/{id}/tasks/{taskId}}.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTask {

    private long id;
    private String href;
    private String state;
    private String updateMode;
    private String progress;

    public ImportTask() {}

    public long getId()            { return id; }
    public String getHref()        { return href; }
    public String getState()       { return state; }
    public String getUpdateMode()  { return updateMode; }
    public String getProgress()    { return progress; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
