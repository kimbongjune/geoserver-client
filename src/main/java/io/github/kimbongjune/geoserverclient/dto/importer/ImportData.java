package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import java.util.Objects;

/**
 * DTO for the data source backing an import or an import task, as returned by
 * {@code GET /rest/imports/{id}/data} and {@code GET /rest/imports/{id}/tasks/{taskId}/data}.
 *
 * <p>{@code type} is {@code "file"} or {@code "directory"} — the {@code /data/files} sub-resource
 * (see {@link io.github.kimbongjune.geoserverclient.api.importer.ImporterManager#listImportDataFiles})
 * only returns results when {@code type} is {@code "directory"}; GeoServer returns 400
 * ("Data is not a directory") otherwise. Verified against GeoServer 2.28.2.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportData {

    private String type;
    private String format;
    private String location;
    private String file;
    private String href;
    private String prj;
    private List<String> other;

    /** @return {@code "file"} or {@code "directory"} */
    public String getType() {
        return type;
    }
    /** @return the detected data format (e.g. {@code "Shapefile"}, {@code "GeoTIFF"}) */
    public String getFormat() {
        return format;
    }
    /** @return the server-side storage location */
    public String getLocation() {
        return location;
    }
    /** @return the primary file name */
    public String getFile() {
        return file;
    }
    /** @return the href to this data resource */
    public String getHref() {
        return href;
    }
    /** @return the associated {@code .prj} projection file name, if any */
    public String getPrj() {
        return prj;
    }
    /** @return other sidecar file names (e.g. {@code .dbf}, {@code .shx} for a Shapefile) */
    public List<String> getOther() {
        return other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportData that = (ImportData) o;
        return Objects.equals(type, that.type)
                && Objects.equals(format, that.format)
                && Objects.equals(location, that.location)
                && Objects.equals(file, that.file)
                && Objects.equals(href, that.href)
                && Objects.equals(prj, that.prj)
                && Objects.equals(other, that.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, format, location, file, href, prj, other);
    }

    @Override
    public String toString() {
        return "ImportData{" +
                "type=" + type +
                ", format=" + format +
                ", location=" + location +
                ", file=" + file +
                '}';
    }
}
