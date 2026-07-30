package io.github.kimbongjune.geoserverclient.dto.about;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a single system metric entry in {@code GET /rest/about/system-status}.
 * <p>
 * In Docker environments all metrics return {@code available=false, value="NOT AVAILABLE"}.
 * Categories: SYSTEM | CPU | MEMORY | SWAP | FILE_SYSTEM | NETWORK | SENSORS | GEOSERVER
 * </p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemMetric {

    private String name;
    private String identifier;
    private String description;
    private String category;
    private String unit;
    private Integer priority;
    private Boolean available;
    private String value;

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getPriority() {
        return priority;
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemMetric that = (SystemMetric) o;
        return Objects.equals(name, that.name)
                && Objects.equals(identifier, that.identifier)
                && Objects.equals(description, that.description)
                && Objects.equals(category, that.category)
                && Objects.equals(unit, that.unit)
                && Objects.equals(priority, that.priority)
                && Objects.equals(available, that.available)
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, identifier, description, category, unit, priority, available, value);
    }

    @Override
    public String toString() {
        return "SystemMetric{" +
                "name=" + name +
                ", identifier=" + identifier +
                ", description=" + description +
                ", category=" + category +
                ", unit=" + unit +
                ", priority=" + priority +
                ", available=" + available +
                ", value=" + value +
                '}';
    }
}
