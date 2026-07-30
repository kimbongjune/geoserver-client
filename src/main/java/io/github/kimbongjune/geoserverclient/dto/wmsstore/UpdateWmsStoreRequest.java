package io.github.kimbongjune.geoserverclient.dto.wmsstore;

import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import java.util.Objects;

/**
 * Request DTO for updating a WMS store. Supports partial updates.
 *
 * <pre>{@code
 * // Change description
 * UpdateWmsStoreRequest.builder().description("updated").build()
 *
 * // Update multiple fields at once
 * UpdateWmsStoreRequest.builder()
 *     .description("updated")
 *     .enabled(false)
 *     .maxConnections(10)
 *     .build()
 * }</pre>
 */
public class UpdateWmsStoreRequest {

    private final String  description;
    private final Boolean enabled;
    private final String  capabilitiesURL;
    private final String  user;
    private final String  password;
    private final String  authKey;
    private final String  headerName;
    private final String  headerValue;
    private final Integer maxConnections;
    private final Integer readTimeout;
    private final Integer connectTimeout;
    private final Boolean disableOnConnFailure;

    private UpdateWmsStoreRequest(Builder b) {
        if (b.description == null && b.enabled == null && b.capabilitiesURL == null
                && b.user == null && b.password == null && b.authKey == null
                && b.headerName == null && b.headerValue == null
                && b.maxConnections == null && b.readTimeout == null
                && b.connectTimeout == null && b.disableOnConnFailure == null) {
            throw new InvalidParameterException("request",
                    "at least one field must be provided for update");
        }
        this.description          = b.description;
        this.enabled              = b.enabled;
        this.capabilitiesURL      = b.capabilitiesURL;
        this.user                 = b.user;
        this.password             = b.password;
        this.authKey              = b.authKey;
        this.headerName           = b.headerName;
        this.headerValue          = b.headerValue;
        this.maxConnections       = b.maxConnections;
        this.readTimeout          = b.readTimeout;
        this.connectTimeout       = b.connectTimeout;
        this.disableOnConnFailure = b.disableOnConnFailure;
    }

    public static Builder builder() { return new Builder(); }

    public String  getDescription()           { return description; }
    public Boolean getEnabled()               { return enabled; }
    public String  getCapabilitiesURL()       { return capabilitiesURL; }
    public String  getUser()                  { return user; }
    public String  getPassword()              { return password; }
    public String  getAuthKey()               { return authKey; }
    public String  getHeaderName()            { return headerName; }
    public String  getHeaderValue()           { return headerValue; }
    public Integer getMaxConnections()        { return maxConnections; }
    public Integer getReadTimeout()           { return readTimeout; }
    public Integer getConnectTimeout()        { return connectTimeout; }
    public Boolean getDisableOnConnFailure()  { return disableOnConnFailure; }

    public static class Builder {
        private String  description;
        private Boolean enabled;
        private String  capabilitiesURL;
        private String  user;
        private String  password;
        private String  authKey;
        private String  headerName;
        private String  headerValue;
        private Integer maxConnections;
        private Integer readTimeout;
        private Integer connectTimeout;
        private Boolean disableOnConnFailure;

        public Builder description(String v)           { this.description = v;           return this; }
        public Builder enabled(Boolean v)              { this.enabled = v;               return this; }
        public Builder capabilitiesURL(String v)       { this.capabilitiesURL = v;       return this; }
        public Builder user(String v)                  { this.user = v;                  return this; }
        public Builder password(String v)              { this.password = v;              return this; }
        public Builder authKey(String v)               { this.authKey = v;               return this; }
        public Builder headerName(String v)            { this.headerName = v;            return this; }
        public Builder headerValue(String v)           { this.headerValue = v;           return this; }
        public Builder maxConnections(Integer v)       { this.maxConnections = v;        return this; }
        public Builder readTimeout(Integer v)          { this.readTimeout = v;           return this; }
        public Builder connectTimeout(Integer v)       { this.connectTimeout = v;        return this; }
        public Builder disableOnConnFailure(Boolean v) { this.disableOnConnFailure = v;  return this; }
        public UpdateWmsStoreRequest build()           { return new UpdateWmsStoreRequest(this); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder that = (Builder) o;
            return Objects.equals(description, that.description)
                    && Objects.equals(enabled, that.enabled)
                    && Objects.equals(capabilitiesURL, that.capabilitiesURL)
                    && Objects.equals(user, that.user)
                    && Objects.equals(password, that.password)
                    && Objects.equals(authKey, that.authKey)
                    && Objects.equals(headerName, that.headerName)
                    && Objects.equals(headerValue, that.headerValue)
                    && Objects.equals(maxConnections, that.maxConnections)
                    && Objects.equals(readTimeout, that.readTimeout)
                    && Objects.equals(connectTimeout, that.connectTimeout)
                    && Objects.equals(disableOnConnFailure, that.disableOnConnFailure);
        }

        @Override
        public int hashCode() {
            return Objects.hash(description, enabled, capabilitiesURL, user, password, authKey, headerName, headerValue, maxConnections, readTimeout, connectTimeout, disableOnConnFailure);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "description=" + description +
                    ", enabled=" + enabled +
                    ", capabilitiesURL=" + capabilitiesURL +
                    ", user=" + user +
                    ", password=" + password +
                    ", authKey=" + authKey +
                    ", headerName=" + headerName +
                    ", headerValue=" + headerValue +
                    ", maxConnections=" + maxConnections +
                    ", readTimeout=" + readTimeout +
                    ", connectTimeout=" + connectTimeout +
                    ", disableOnConnFailure=" + disableOnConnFailure +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateWmsStoreRequest that = (UpdateWmsStoreRequest) o;
        return Objects.equals(description, that.description)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(capabilitiesURL, that.capabilitiesURL)
                && Objects.equals(user, that.user)
                && Objects.equals(password, that.password)
                && Objects.equals(authKey, that.authKey)
                && Objects.equals(headerName, that.headerName)
                && Objects.equals(headerValue, that.headerValue)
                && Objects.equals(maxConnections, that.maxConnections)
                && Objects.equals(readTimeout, that.readTimeout)
                && Objects.equals(connectTimeout, that.connectTimeout)
                && Objects.equals(disableOnConnFailure, that.disableOnConnFailure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, enabled, capabilitiesURL, user, password, authKey, headerName, headerValue, maxConnections, readTimeout, connectTimeout, disableOnConnFailure);
    }

    @Override
    public String toString() {
        return "UpdateWmsStoreRequest{" +
                "description=" + description +
                ", enabled=" + enabled +
                ", capabilitiesURL=" + capabilitiesURL +
                ", user=" + user +
                ", password=" + password +
                ", authKey=" + authKey +
                ", headerName=" + headerName +
                ", headerValue=" + headerValue +
                ", maxConnections=" + maxConnections +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                ", disableOnConnFailure=" + disableOnConnFailure +
                '}';
    }
}
