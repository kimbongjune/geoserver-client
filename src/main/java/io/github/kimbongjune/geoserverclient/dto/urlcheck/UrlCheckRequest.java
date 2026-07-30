package io.github.kimbongjune.geoserverclient.dto.urlcheck;
import java.util.Objects;

/**
 * Request DTO for creating or updating a URL checker.
 *
 * <ul>
 *   <li>POST {@code /rest/urlchecks} — create</li>
 *   <li>PUT  {@code /rest/urlchecks/{name}} — update</li>
 * </ul>
 *
 * <p>Example:
 * <pre>
 * UrlCheckRequest req = new UrlCheckRequest(
 *     "myCheck",
 *     "My description",
 *     "^https?://.*\\.example\\.com/.*$",
 *     true
 * );
 * </pre>
 */
public class UrlCheckRequest {

    private String name;
    private String description;
    private String regex;
    private boolean enabled;

    public UrlCheckRequest() {}

    public UrlCheckRequest(String name, String description, String regex, boolean enabled) {
        this.name = name;
        this.description = description;
        this.regex = regex;
        this.enabled = enabled;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getRegex()       { return regex; }
    public boolean isEnabled()     { return enabled; }

    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setRegex(String regex)             { this.regex = regex; }
    public void setEnabled(boolean enabled)        { this.enabled = enabled; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlCheckRequest that = (UrlCheckRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(regex, that.regex)
                && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, regex, enabled);
    }

    @Override
    public String toString() {
        return "UrlCheckRequest{" +
                "name=" + name +
                ", description=" + description +
                ", regex=" + regex +
                ", enabled=" + enabled +
                '}';
    }
}
