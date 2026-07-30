package io.github.kimbongjune.geoserverclient.dto.namespace;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a namespace. Supports partial updates.
 *
 * <p><b>Important:</b> the prefix cannot be changed — GeoServer returns 403 on PUT.
 * Only {@code uri} and {@code isolated} can be modified.</p>
 *
 * <pre>{@code
 * // Change URI only
 * UpdateNamespaceRequest.builder().uri("http://new.example.com").build()
 *
 * // Change isolated flag only
 * UpdateNamespaceRequest.builder().isolated(true).build()
 *
 * // Change both
 * UpdateNamespaceRequest.builder()
 *     .uri("http://new.example.com")
 *     .isolated(false)
 *     .build()
 * }</pre>
 */
public class UpdateNamespaceRequest {

    private final String  uri;
    private final Boolean isolated;

    private UpdateNamespaceRequest(String uri, Boolean isolated) {
        if (uri == null && isolated == null) {
            throw new InvalidParameterException("request",
                    "at least one of 'uri' or 'isolated' must be provided");
        }
        this.uri      = uri;
        this.isolated = isolated;
    }

    public static Builder builder() { return new Builder(); }

    public String  getUri()      { return uri; }
    public Boolean getIsolated() { return isolated; }

    public static class Builder {
        private String  uri;
        private Boolean isolated;

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }
        public Builder isolated(Boolean isolated) {
            this.isolated = isolated;
            return this;
        }
        public UpdateNamespaceRequest build() {
            return new UpdateNamespaceRequest(uri, isolated);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder that = (Builder) o;
            return Objects.equals(uri, that.uri)
                    && Objects.equals(isolated, that.isolated);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, isolated);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "uri=" + uri +
                    ", isolated=" + isolated +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateNamespaceRequest that = (UpdateNamespaceRequest) o;
        return Objects.equals(uri, that.uri)
                && Objects.equals(isolated, that.isolated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, isolated);
    }

    @Override
    public String toString() {
        return "UpdateNamespaceRequest{" +
                "uri=" + uri +
                ", isolated=" + isolated +
                '}';
    }
}
