package io.github.kimbongjune.geoserverclient.dto.logging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Objects;

/**
 * DTO for GeoServer logging configuration.
 *
 * <p><b>Warning — stdOutLogging:</b> if this field is omitted (null) on PUT, the server-side
 * Jackson deserialises it as the primitive {@code false}, overwriting the existing value.
 * Always include all three fields explicitly when sending a PUT request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoggingInfo {

    private String level;
    private String location;
    private Boolean stdOutLogging;

    /** @return the logging level (e.g. {@code "DEFAULT_LOGGING.properties"}) */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the logging level.
     * @param level the logging level to set
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /** @return the logging output location */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the logging output location.
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** @return {@code true} if logging to stdout is enabled */
    public Boolean getStdOutLogging() {
        return stdOutLogging;
    }

    /**
     * Sets whether logging to stdout is enabled.
     * @param stdOutLogging {@code true} to enable stdout logging
     */
    public void setStdOutLogging(Boolean stdOutLogging) {
        this.stdOutLogging = stdOutLogging;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
