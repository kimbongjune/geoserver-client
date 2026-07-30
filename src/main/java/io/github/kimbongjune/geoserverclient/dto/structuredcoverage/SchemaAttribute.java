package io.github.kimbongjune.geoserverclient.dto.structuredcoverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for a single attribute in a structured coverage index schema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaAttribute {

    @JsonProperty("name")
    private String name;

    @JsonProperty("minOccurs")
    private Integer minOccurs;

    @JsonProperty("maxOccurs")
    private Integer maxOccurs;

    @JsonProperty("nillable")
    private Boolean nillable;

    @JsonProperty("binding")
    private String binding;

    @JsonProperty("length")
    private Integer length;

    public String  getName()      { return name; }
    public Integer getMinOccurs() { return minOccurs; }
    public Integer getMaxOccurs() { return maxOccurs; }
    public Boolean getNillable()  { return nillable; }
    public String  getBinding()   { return binding; }
    public Integer getLength()    { return length; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaAttribute that = (SchemaAttribute) o;
        return Objects.equals(name, that.name)
                && Objects.equals(minOccurs, that.minOccurs)
                && Objects.equals(maxOccurs, that.maxOccurs)
                && Objects.equals(nillable, that.nillable)
                && Objects.equals(binding, that.binding)
                && Objects.equals(length, that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, minOccurs, maxOccurs, nillable, binding, length);
    }

    @Override
    public String toString() {
        return "SchemaAttribute{" +
                "name=" + name +
                ", minOccurs=" + minOccurs +
                ", maxOccurs=" + maxOccurs +
                ", nillable=" + nillable +
                ", binding=" + binding +
                ", length=" + length +
                '}';
    }
}
