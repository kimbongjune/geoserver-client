package io.github.kimbongjune.geoserverclient.dto.wmtsstore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for creating a WMTS (cascading) store.
 *
 * <pre>{@code
 * // Minimal (name + capabilitiesURL)
 * CreateWmtsStoreRequest.of("remote_wmts", "http://wmts-server/wmts?REQUEST=GetCapabilities")
 *
 * // All fields
 * CreateWmtsStoreRequest.of("remote_wmts", "http://wmts-server/wmts?REQUEST=GetCapabilities")
 *     .description("My cascading WMTS")
 *     .enabled(true)
 *     .user("remoteUser")
 *     .password("remotePass")
 *     .maxConnections(6)
 *     .readTimeout(60)
 *     .connectTimeout(30)
 *     .headerName("X-Custom-Header")
 *     .headerValue("my-value")
 *     .authKey("my-auth-key")
 *     .disableOnConnFailure(false)
 *     .useConnectionPooling(true)   // serialized as metadata.entry format
 * }</pre>
 *
 * <p><b>useConnectionPooling note:</b> WmtsStore does not accept this as a flat field.
 * This DTO serializes it automatically in the metadata.entry format.
 */
public class CreateWmtsStoreRequest {

    private final String name;
    private final String capabilitiesURL;
    private String description;
    private Boolean enabled;
    private Boolean defaultStore;
    private String user;
    private String password;
    private String authKey;
    private String headerName;
    private String headerValue;
    private Integer maxConnections;
    private Integer readTimeout;
    private Integer connectTimeout;
    private Boolean disableOnConnFailure;
    private Boolean useConnectionPooling;

    private CreateWmtsStoreRequest(String name, String capabilitiesURL) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidParameterException("name", "wmtsStore name must not be null or empty");
        }
        if (capabilitiesURL == null || capabilitiesURL.trim().isEmpty()) {
            throw new InvalidParameterException("capabilitiesURL", "capabilitiesURL must not be null or empty");
        }
        this.name = name.trim();
        this.capabilitiesURL = capabilitiesURL.trim();
    }

    /**
     * Creates a WMTS store request with the given name and capabilities URL.
     * @param name            the WMTS store name (must not be null or empty)
     * @param capabilitiesURL the WMTS GetCapabilities URL (must not be null or empty)
     * @return a new request instance
     */
    public static CreateWmtsStoreRequest of(String name, String capabilitiesURL) {
        return new CreateWmtsStoreRequest(name, capabilitiesURL);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     * @param name            the WMTS store name
     * @param capabilitiesURL the WMTS GetCapabilities URL
     * @return a new request instance
     */
    public static CreateWmtsStoreRequest builder(String name, String capabilitiesURL) {
        return of(name, capabilitiesURL);
    }

    /**
     * Sets the store description.
     * @param description the store description
     * @return this request
     */
    public CreateWmtsStoreRequest description(String description) {
        this.description = description;             return this;
    }
    /**
     * Sets whether this store is enabled.
     * @param enabled {@code true} to enable
     * @return this request
     */
    public CreateWmtsStoreRequest enabled(Boolean enabled) {
        this.enabled = enabled;                     return this;
    }
    /**
     * Sets whether this is the default store.
     * @param defaultStore {@code true} to set as default
     * @return this request
     */
    public CreateWmtsStoreRequest defaultStore(Boolean defaultStore) {
        this.defaultStore = defaultStore;           return this;
    }
    /**
     * Sets the remote service username.
     * @param user the username
     * @return this request
     */
    public CreateWmtsStoreRequest user(String user) {
        this.user = user;                           return this;
    }
    /**
     * Sets the remote service password.
     * @param password the password
     * @return this request
     */
    public CreateWmtsStoreRequest password(String password) {
        this.password = password;                   return this;
    }
    /**
     * Sets the authentication key.
     * @param authKey the authentication key
     * @return this request
     */
    public CreateWmtsStoreRequest authKey(String authKey) {
        this.authKey = authKey;                     return this;
    }
    /**
     * Sets the extra HTTP header name.
     * @param headerName the header name
     * @return this request
     */
    public CreateWmtsStoreRequest headerName(String headerName) {
        this.headerName = headerName;               return this;
    }
    /**
     * Sets the extra HTTP header value.
     * @param headerValue the header value
     * @return this request
     */
    public CreateWmtsStoreRequest headerValue(String headerValue) {
        this.headerValue = headerValue;             return this;
    }
    /**
     * Sets the maximum concurrent connections.
     * @param maxConnections maximum connections
     * @return this request
     */
    public CreateWmtsStoreRequest maxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;       return this;
    }
    /**
     * Sets the read timeout in seconds.
     * @param readTimeout the read timeout
     * @return this request
     */
    public CreateWmtsStoreRequest readTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;             return this;
    }
    /**
     * Sets the connect timeout in seconds.
     * @param connectTimeout the connect timeout
     * @return this request
     */
    public CreateWmtsStoreRequest connectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;       return this;
    }
    /**
     * Sets whether to disable on connection failure.
     * @param disable {@code true} to disable on connection failure
     * @return this request
     */
    public CreateWmtsStoreRequest disableOnConnFailure(Boolean disable) {
        this.disableOnConnFailure = disable;        return this;
    }
    /**
     * Sets whether to enable connection pooling.
     * @param pooling {@code true} to enable connection pooling
     * @return this request
     */
    public CreateWmtsStoreRequest useConnectionPooling(Boolean pooling) {
        this.useConnectionPooling = pooling;        return this;
    }

    /**
     * Terminal no-op for {@code builder(...)...build()} chains.
     * @return this request
     */
    public CreateWmtsStoreRequest build() {
        return this;
    }

    /** @return the store name */
    public String  getName() {
        return name;
    }
    /** @return the WMTS GetCapabilities URL */
    public String  getCapabilitiesURL() {
        return capabilitiesURL;
    }
    /** @return the store description */
    public String  getDescription() {
        return description;
    }
    /** @return {@code true} if enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /** @return {@code true} if this is the default store */
    public Boolean getDefaultStore() {
        return defaultStore;
    }
    /** @return the remote service username */
    public String  getUser() {
        return user;
    }
    /** @return the remote service password */
    public String  getPassword() {
        return password;
    }
    /** @return the authentication key */
    public String  getAuthKey() {
        return authKey;
    }
    /** @return the extra HTTP header name */
    public String  getHeaderName() {
        return headerName;
    }
    /** @return the extra HTTP header value */
    public String  getHeaderValue() {
        return headerValue;
    }
    /** @return the maximum concurrent connections */
    public Integer getMaxConnections() {
        return maxConnections;
    }
    /** @return the read timeout in seconds */
    public Integer getReadTimeout() {
        return readTimeout;
    }
    /** @return the connect timeout in seconds */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }
    /** @return {@code true} if disabled on connection failure */
    public Boolean getDisableOnConnFailure() {
        return disableOnConnFailure;
    }
    /** @return {@code true} if connection pooling is enabled */
    public Boolean getUseConnectionPooling() {
        return useConnectionPooling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateWmtsStoreRequest that = (CreateWmtsStoreRequest) o;
        return Objects.equals(name, that.name)
                && Objects.equals(capabilitiesURL, that.capabilitiesURL)
                && Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(defaultStore, that.defaultStore)
                && Objects.equals(user, that.user)
                && Objects.equals(password, that.password)
                && Objects.equals(authKey, that.authKey)
                && Objects.equals(headerName, that.headerName)
                && Objects.equals(headerValue, that.headerValue)
                && Objects.equals(maxConnections, that.maxConnections)
                && Objects.equals(readTimeout, that.readTimeout)
                && Objects.equals(connectTimeout, that.connectTimeout)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure)
                && Objects.equals(useConnectionPooling, that.useConnectionPooling);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capabilitiesURL, description, enabled, defaultStore, user, password, authKey, headerName, headerValue, maxConnections, readTimeout, connectTimeout, disableOnConnFailure, useConnectionPooling);
    }

    @Override
    public String toString() {
        return "CreateWmtsStoreRequest{" +
                "name=" + name +
                ", capabilitiesURL=" + capabilitiesURL +
                ", description=" + description +
                ", enabled=" + enabled +
                ", defaultStore=" + defaultStore +
                ", user=" + user +
                ", password=" + password +
                ", authKey=" + authKey +
                ", headerName=" + headerName +
                ", headerValue=" + headerValue +
                ", maxConnections=" + maxConnections +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                ", disableOnConnFailure=" + disableOnConnFailure +
                ", useConnectionPooling=" + useConnectionPooling +
                '}';
    }
}
