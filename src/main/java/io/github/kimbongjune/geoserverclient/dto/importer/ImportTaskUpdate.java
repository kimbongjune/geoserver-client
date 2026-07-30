package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Request DTO for updating an import task.
 *
 * <p>Maps the {@code "task"} object in the request body for
 * {@code PUT /rest/imports/{id}/tasks/{taskId}}.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTaskUpdate {

    private String updateMode;

    public ImportTaskUpdate() {}

    public ImportTaskUpdate(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getUpdateMode() { return updateMode; }
    public void setUpdateMode(String updateMode) { this.updateMode = updateMode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportTaskUpdate that = (ImportTaskUpdate) o;
        return Objects.equals(updateMode, that.updateMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updateMode);
    }

    @Override
    public String toString() {
        return "ImportTaskUpdate{" +
                "updateMode=" + updateMode +
                '}';
    }
}
