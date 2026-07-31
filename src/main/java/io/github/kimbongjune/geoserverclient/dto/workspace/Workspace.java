package io.github.kimbongjune.geoserverclient.dto.workspace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Full representation of a GeoServer workspace, as returned by single-item GET requests.
 * <p>
 * Undocumented fields confirmed via server verification (GeoServer 2.28.2):
 * <ul>
 *   <li>{@code dateCreated} — set automatically on POST</li>
 *   <li>{@code dateModified} — appears only after a PUT</li>
 *   <li>{@code wmtsStores} — present in response but missing from Swagger spec</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workspace {

    @JsonProperty("name")
    private String name;

    @JsonProperty("isolated")
    private Boolean isolated;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    @JsonProperty("dataStores")
    private String dataStores;

    @JsonProperty("coverageStores")
    private String coverageStores;

    @JsonProperty("wmsStores")
    private String wmsStores;

    @JsonProperty("wmtsStores")
    private String wmtsStores;

    public Workspace() {}

    public String getName() {
        return name;
    }
    public Boolean getIsolated() {
        return isolated;
    }
    public boolean isIsolated() {
        return Boolean.TRUE.equals(isolated);
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public String getDateModified() {
        return dateModified;
    }
    public String getDataStores() {
        return dataStores;
    }
    public String getCoverageStores() {
        return coverageStores;
    }
    public String getWmsStores() {
        return wmsStores;
    }
    public String getWmtsStores() {
        return wmtsStores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workspace that = (Workspace) o;
        return Objects.equals(name, that.name)
                && Objects.equals(isolated, that.isolated)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified)
                && Objects.equals(dataStores, that.dataStores)
                && Objects.equals(coverageStores, that.coverageStores)
                && Objects.equals(wmsStores, that.wmsStores)
                && Objects.equals(wmtsStores, that.wmtsStores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isolated, dateCreated, dateModified, dataStores, coverageStores, wmsStores, wmtsStores);
    }

    @Override
    public String toString() {
        return "Workspace{" +
                "name=" + name +
                ", isolated=" + isolated +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", dataStores=" + dataStores +
                ", coverageStores=" + coverageStores +
                ", wmsStores=" + wmsStores +
                ", wmtsStores=" + wmtsStores +
                '}';
    }
}
