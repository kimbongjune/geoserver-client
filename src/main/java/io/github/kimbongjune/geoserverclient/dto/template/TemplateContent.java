package io.github.kimbongjune.geoserverclient.dto.template;
import java.util.Objects;

/**
 * DTO for raw FreeMarker (.ftl) template content. Used as the request/response body for
 * {@code GET/PUT /rest/.../templates/{name}}.
 */
public class TemplateContent {

    private final String body;

    private TemplateContent(String body) {
        this.body = body;
    }

    public static TemplateContent of(String body) {
        return new TemplateContent(body);
    }

    public String getBody() { return body; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateContent that = (TemplateContent) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "TemplateContent{" +
                "body=" + body +
                '}';
    }
}
