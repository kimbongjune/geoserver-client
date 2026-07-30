package io.github.kimbongjune.geoserverclient.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/** Represents a GeoServer user as returned by the UserGroup REST API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUserInfo {

    private String userName;
    private Boolean enabled;

    public SecurityUserInfo() {}

    public SecurityUserInfo(String userName, Boolean enabled) {
        this.userName = userName;
        this.enabled  = enabled;
    }

    public String getUserName()             { return userName; }
    public void   setUserName(String u)     { this.userName = u; }
    public Boolean getEnabled()             { return enabled; }
    public void   setEnabled(Boolean e)     { this.enabled = e; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
