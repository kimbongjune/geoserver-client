package io.github.kimbongjune.geoserverclient.dto.urlcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * Summary entry returned when listing URL checkers.
 * Maps each item in the {@code urlChecks.urlCheck[]} array from {@code GET /rest/urlchecks}.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlCheckSummary {

    private String name;
    private String href;

    public UrlCheckSummary() {}

    public String getName() {
        return name;
    }
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
        UrlCheckSummary that = (UrlCheckSummary) o;
        return Objects.equals(name, that.name)
                && Objects.equals(href, that.href);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public String toString() {
        return "UrlCheckSummary{" +
                "name=" + name +
                ", href=" + href +
                '}';
    }
}
