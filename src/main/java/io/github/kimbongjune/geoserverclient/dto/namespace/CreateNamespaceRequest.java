package io.github.kimbongjune.geoserverclient.dto.namespace;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a namespace.
 *
 * <pre>{@code
 * // Basic creation
 * CreateNamespaceRequest.of("acme", "http://acme.example.com")
 *
 * // With isolated option
 * CreateNamespaceRequest.of("acme", "http://acme.example.com").isolated(true)
 * }</pre>
 *
 * <p>On POST, a workspace with the same prefix is automatically created alongside the namespace.
 */
public class CreateNamespaceRequest {

    private final String prefix;
    private final String uri;
    private Boolean isolated;

    private CreateNamespaceRequest(String prefix, String uri) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new InvalidParameterException("prefix", "namespace prefix must not be null or empty");
        }
        if (uri == null || uri.trim().isEmpty()) {
            throw new InvalidParameterException("uri", "namespace uri must not be null or empty");
        }
        this.prefix = prefix.trim();
        this.uri    = uri.trim();
    }

    /**
     * Creates a namespace request with the given prefix and URI.
     *
     * @param prefix namespace prefix (required) — matches the workspace name that will be created
     * @param uri    namespace URI (required)
     * @return a new request instance
     */
    public static CreateNamespaceRequest of(String prefix, String uri) {
        return new CreateNamespaceRequest(prefix, uri);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     *
     * @param prefix namespace prefix
     * @param uri    namespace URI
     * @return a new request instance
     */
    public static CreateNamespaceRequest builder(String prefix, String uri) {
        return of(prefix, uri);
    }

    /**
     * Sets whether this is an isolated namespace. Defaults to false.
     *
     * @param isolated {@code true} to isolate this namespace
     * @return this request
     */
    public CreateNamespaceRequest isolated(Boolean isolated) {
        this.isolated = isolated;
        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateNamespaceRequest build() {
        return this;
    }

    /** @return the namespace prefix */
    public String  getPrefix() {
        return prefix;
    }
    /** @return the namespace URI */
    public String  getUri() {
        return uri;
    }
    /** @return {@code true} if this namespace is isolated */
    public Boolean getIsolated() {
        return isolated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateNamespaceRequest that = (CreateNamespaceRequest) o;
        return Objects.equals(prefix, that.prefix)
                && Objects.equals(uri, that.uri)
                && Objects.equals(isolated, that.isolated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, uri, isolated);
    }

    @Override
    public String toString() {
        return "CreateNamespaceRequest{" +
                "prefix=" + prefix +
                ", uri=" + uri +
                ", isolated=" + isolated +
                '}';
    }
}
