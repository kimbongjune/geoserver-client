package io.github.kimbongjune.geoserverclient.dto.urlcheck;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for URL checker details.
 * Maps the {@code "regexUrlCheck"} object in the {@code GET /rest/urlchecks/{name}} response.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlCheck {

    private String name;
    private String description;
    private boolean enabled;
    private String regex;

    public UrlCheck() {}

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public String getRegex() {
        return regex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrlCheck that = (UrlCheck) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(regex, that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, enabled, regex);
    }

    @Override
    public String toString() {
        return "UrlCheck{" +
                "name=" + name +
                ", description=" + description +
                ", enabled=" + enabled +
                ", regex=" + regex +
                '}';
    }
}
