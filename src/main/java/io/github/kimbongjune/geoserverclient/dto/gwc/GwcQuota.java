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

    /** Constructs an empty {@code GwcQuota} for deserialization. */
    public GwcQuota() {}

    private GwcQuota(Double value, String units) {
        this.value = value;
        this.units = units;
    }

    /**
     * Factory for a PUT quota value.
     * @param value the numeric quota value
     * @param units the quota unit (B, KiB, MiB, GiB, TiB, PiB, EiB)
     * @return a new {@code GwcQuota} instance
     */
    public static GwcQuota of(double value, String units) {
        return new GwcQuota(value, units);
    }

    /** @return the quota ID (from GET response) */
    public Long getId() {
        return id;
    }

    /** @return the quota in bytes (from GET response) */
    public Long getBytes() {
        return bytes;
    }

    /** @return the numeric quota value (for PUT) */
    public Double getValue() {
        return value;
    }

    /** @return the quota unit (for PUT; e.g. {@code "GiB"}) */
    public String getUnits() {
        return units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
