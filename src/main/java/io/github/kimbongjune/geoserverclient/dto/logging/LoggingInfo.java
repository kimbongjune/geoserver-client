package io.github.kimbongjune.geoserverclient.dto.logging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

/**
 * DTO for GeoServer logging configuration.
 *
 * <p><b>Warning — stdOutLogging:</b> if this field is omitted (null) on PUT, the server-side
 * Jackson deserialises it as the primitive {@code false}, overwriting the existing value.
 * Always include all three fields explicitly when sending a PUT request.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoggingInfo {

    private String level;
    private String location;
    private Boolean stdOutLogging;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getStdOutLogging() {
        return stdOutLogging;
    }

    public void setStdOutLogging(Boolean stdOutLogging) {
        this.stdOutLogging = stdOutLogging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggingInfo that = (LoggingInfo) o;
        return Objects.equals(level, that.level)
                && Objects.equals(location, that.location)
                && Objects.equals(stdOutLogging, that.stdOutLogging);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, location, stdOutLogging);
    }

    @Override
    public String toString() {
        return "LoggingInfo{" +
                "level=" + level +
                ", location=" + location +
                ", stdOutLogging=" + stdOutLogging +
                '}';
    }
}
