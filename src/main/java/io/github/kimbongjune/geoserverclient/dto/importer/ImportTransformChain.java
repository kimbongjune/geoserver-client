package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

/**
 * DTO for an import transform chain.
 *
 * <p>Maps the {@code "transformChain"} object returned by
 * {@code GET /rest/imports/{id}/tasks/{taskId}/transforms}.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTransformChain {

    /** Chain type: "vector" or "raster". */
    private String type;
    private List<ImportTransform> transforms;

    public ImportTransformChain() {}

    public String getType() { return type; }
    public List<ImportTransform> getTransforms() { return transforms == null ? null : Collections.unmodifiableList(transforms); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportTransformChain that = (ImportTransformChain) o;
        return Objects.equals(type, that.type)
                && Objects.equals(transforms, that.transforms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, transforms);
    }

    @Override
    public String toString() {
        return "ImportTransformChain{" +
                "type=" + type +
                ", transforms=" + transforms +
                '}';
    }
}
