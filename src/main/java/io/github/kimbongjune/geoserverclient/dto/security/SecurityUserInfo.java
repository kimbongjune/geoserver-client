package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/** Represents a GeoServer user as returned by the UserGroup REST API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUserInfo {

    private String userName;
    private Boolean enabled;

    /** Constructs an empty {@code SecurityUserInfo} for deserialization. */
    public SecurityUserInfo() {}

    /**
     * Constructs a {@code SecurityUserInfo} with the given values.
     * @param userName the GeoServer username
     * @param enabled  {@code true} if the user account is enabled
     */
    public SecurityUserInfo(String userName, Boolean enabled) {
        this.userName = userName;
        this.enabled  = enabled;
    }

    /** @return the GeoServer username */
    public String getUserName() {
        return userName;
    }
    /**
     * Sets the GeoServer username.
     * @param u the username
     */
    public void   setUserName(String u) {
        this.userName = u;
    }
    /** @return {@code true} if the user account is enabled */
    public Boolean getEnabled() {
        return enabled;
    }
    /**
     * Sets whether the user account is enabled.
     * @param e {@code true} to enable the account
     */
    public void   setEnabled(Boolean e) {
        this.enabled = e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SecurityUserInfo that = (SecurityUserInfo) o;
        return Objects.equals(userName, that.userName)
                && Objects.equals(enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, enabled);
    }

    @Override
    public String toString() {
        return "SecurityUserInfo{" +
                "userName=" + userName +
                ", enabled=" + enabled +
                '}';
    }
}
