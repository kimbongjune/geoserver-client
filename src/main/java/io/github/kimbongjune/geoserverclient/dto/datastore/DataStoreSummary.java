package io.github.kimbongjune.geoserverclient.dto.datastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Summary entry returned when listing data stores (name + href only).
 *
 * <p>Maps each item in the array returned by {@code GET /rest/workspaces/{ws}/datastores}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataStoreSummary {

    @JsonProperty("name")
    private String name;

    @JsonProperty("href")
    private String href;

    /** Constructs an empty {@code DataStoreSummary} for deserialization. */
    public DataStoreSummary() {}

    /** @return the data store name */
    public String getName() {
        return name;
    }
    /** @return the href to the full data store resource */
    public String getHref() {
        return href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataStoreSummary that = (DataStoreSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "DataStoreSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}
