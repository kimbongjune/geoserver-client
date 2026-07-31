package io.github.kimbongjune.geoserverclient.dto.importer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for an import transform.
 *
 * <p>Fields vary by transform type; unknown fields are ignored via
 * {@code @JsonIgnoreProperties(ignoreUnknown = true)}.
 *
 * <p>Known types and their fields:
 * <ul>
 *   <li>{@code ReprojectTransform} — source (EPSG code), target (EPSG code)</li>
 *   <li>{@code AttributeRemapTransform} — field, target (class name)</li>
 *   <li>{@code DateFormatTransform} — field, format</li>
 *   <li>{@code IntegerFieldToDateTransform} — field</li>
 *   <li>{@code CreateIndexTransform} — field</li>
 * </ul>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportTransform {

    private String type;
    private String href;
    /** ReprojectTransform: source CRS (EPSG code). */
    private String source;
    /** ReprojectTransform: target CRS (EPSG code). */
    private String target;
    /** AttributeRemapTransform, DateFormatTransform, etc.: target field name. */
    private String field;
    /** DateFormatTransform: date format string. */
    private String format;

    /** Constructs an empty {@code ImportTransform} for deserialization. */
    public ImportTransform() {}

    /**
     * Factory method for a ReprojectTransform.
     * @param source the source CRS (e.g. {@code "EPSG:4326"})
     * @param target the target CRS (e.g. {@code "EPSG:3857"})
     * @return a new {@code ImportTransform} configured as a ReprojectTransform
     */
    public static ImportTransform reproject(String source, String target) {
        ImportTransform t = new ImportTransform();
        t.type = "ReprojectTransform";
        t.source = source;
        t.target = target;
        return t;
    }

    /** @return the transform type name (e.g. {@code "ReprojectTransform"}) */
    public String getType() {
        return type;
    }
    /** @return the href to this transform resource */
    public String getHref() {
        return href;
    }
    /** @return the source CRS for a ReprojectTransform */
    public String getSource() {
        return source;
    }
    /** @return the target CRS or class name depending on transform type */
    public String getTarget() {
        return target;
    }
    /** @return the field name for field-based transforms */
    public String getField() {
        return field;
    }
    /** @return the date format string for a DateFormatTransform */
    public String getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportTransform that = (ImportTransform) o;
        return Objects.equals(type, that.type)
                && Objects.equals(href, that.href)
                && Objects.equals(source, that.source)
                && Objects.equals(target, that.target)
                && Objects.equals(field, that.field)
                && Objects.equals(format, that.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, href, source, target, field, format);
    }

    @Override
    public String toString() {
        return "ImportTransform{" +
                "type=" + type +
                ", href=" + href +
                ", source=" + source +
                ", target=" + target +
                ", field=" + field +
                ", format=" + format +
                '}';
    }
}
