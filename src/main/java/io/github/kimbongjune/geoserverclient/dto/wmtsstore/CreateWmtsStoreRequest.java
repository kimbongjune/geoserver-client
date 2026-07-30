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
 * This DTO serializes it automatically in the metadata.entry format.</p>
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

    public static CreateWmtsStoreRequest of(String name, String capabilitiesURL) {
        return new CreateWmtsStoreRequest(name, capabilitiesURL);
    }

    /**
     * Alias for {@link #of(String, String)}, for callers who prefer the
     * {@code builder(...)...build()} spelling used by every {@code UpdateXxxRequest} in this
     * library. {@link #build()} is a no-op terminal call.
     */
    public static CreateWmtsStoreRequest builder(String name, String capabilitiesURL) {
        return of(name, capabilitiesURL);
    }

    public CreateWmtsStoreRequest description(String description)               { this.description = description;             return this; }
    public CreateWmtsStoreRequest enabled(Boolean enabled)                      { this.enabled = enabled;                     return this; }
    public CreateWmtsStoreRequest defaultStore(Boolean defaultStore)            { this.defaultStore = defaultStore;           return this; }
    public CreateWmtsStoreRequest user(String user)                             { this.user = user;                           return this; }
    public CreateWmtsStoreRequest password(String password)                     { this.password = password;                   return this; }
    public CreateWmtsStoreRequest authKey(String authKey)                       { this.authKey = authKey;                     return this; }
    public CreateWmtsStoreRequest headerName(String headerName)                 { this.headerName = headerName;               return this; }
    public CreateWmtsStoreRequest headerValue(String headerValue)               { this.headerValue = headerValue;             return this; }
    public CreateWmtsStoreRequest maxConnections(Integer maxConnections)        { this.maxConnections = maxConnections;       return this; }
    public CreateWmtsStoreRequest readTimeout(Integer readTimeout)              { this.readTimeout = readTimeout;             return this; }
    public CreateWmtsStoreRequest connectTimeout(Integer connectTimeout)        { this.connectTimeout = connectTimeout;       return this; }
    public CreateWmtsStoreRequest disableOnConnFailure(Boolean disable)         { this.disableOnConnFailure = disable;        return this; }
    public CreateWmtsStoreRequest useConnectionPooling(Boolean pooling)         { this.useConnectionPooling = pooling;        return this; }

    /** Terminal no-op for {@code builder(...)...build()} chains — returns {@code this}. */
    public CreateWmtsStoreRequest build() { return this; }

    public String  getName()                   { return name; }
    public String  getCapabilitiesURL()        { return capabilitiesURL; }
    public String  getDescription()            { return description; }
    public Boolean getEnabled()                { return enabled; }
    public Boolean getDefaultStore()           { return defaultStore; }
    public String  getUser()                   { return user; }
    public String  getPassword()               { return password; }
    public String  getAuthKey()                { return authKey; }
    public String  getHeaderName()             { return headerName; }
    public String  getHeaderValue()            { return headerValue; }
    public Integer getMaxConnections()         { return maxConnections; }
    public Integer getReadTimeout()            { return readTimeout; }
    public Integer getConnectTimeout()         { return connectTimeout; }
    public Boolean getDisableOnConnFailure()   { return disableOnConnFailure; }
    public Boolean getUseConnectionPooling()   { return useConnectionPooling; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
