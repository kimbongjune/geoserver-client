package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a GWC disk quota value. Field layout differs by usage:
 * <ul>
 *   <li>GET response: {@code {id, bytes}}</li>
 *   <li>PUT request: {@code {value, units}}</li>
 * </ul>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcQuota {

    private Long id;
    private Long bytes;
    private Double value;
    private String units;

    public GwcQuota() {}

    private GwcQuota(Double value, String units) {
        this.value = value;
        this.units = units;
    }

    /** Factory for a PUT quota value. units: B, KiB, MiB, GiB, TiB, PiB, EiB. */
    public static GwcQuota of(double value, String units) {
        return new GwcQuota(value, units);
    }

    public Long getId() { return id; }

    public Long getBytes() { return bytes; }

    public Double getValue() { return value; }

    public String getUnits() { return units; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcQuota that = (GwcQuota) o;
        return Objects.equals(id, that.id)
                && Objects.equals(bytes, that.bytes)
                && Objects.equals(value, that.value)
                && Objects.equals(units, that.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bytes, value, units);
    }

    @Override
    public String toString() {
        return "GwcQuota{" +
                "id=" + id +
                ", bytes=" + bytes +
                ", value=" + value +
                ", units=" + units +
                '}';
    }
}
