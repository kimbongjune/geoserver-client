package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * Request DTO for updating an import task.
 *
 * <p>Maps the {@code "task"} object in the request body for
 * {@code PUT /rest/imports/{id}/tasks/{taskId}}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTaskUpdate {

    private ImportUpdateMode updateMode;

    /** Constructs an empty {@code ImportTaskUpdate} for deserialization. */
    public ImportTaskUpdate() {}

    /**
     * Constructs an {@code ImportTaskUpdate} with the given update mode.
     * @param updateMode the update mode
     */
    public ImportTaskUpdate(ImportUpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    /** @return the update mode */
    public ImportUpdateMode getUpdateMode() {
        return updateMode;
    }

    /**
     * Sets the update mode.
     * @param updateMode the update mode to set
     */
    public void setUpdateMode(ImportUpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
