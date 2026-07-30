package io.github.kimbongjune.geoserverclient.dto.style;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * DTO for style details, as returned by {@code GET /rest/styles/{name}}.
 *
 * <pre>
 * {
 *   "style": {
 *     "name": "my_style",
 *     "workspace": {"name": "ws"},
 *     "format": "sld",
 *     "languageVersion": {"version": "1.0.0"},
 *     "filename": "my_style.sld",
 *     "legend": {"width":32,"height":32,"format":"image/png","onlineResource":"..."},
 *     "dateCreated": "...",
 *     "dateModified": "..."
 *   }
 * }
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Style {

    @JsonProperty("name")
    private String name;

    @JsonProperty("workspace")
    private WorkspaceRef workspace;

    @JsonProperty("format")
    private String format;

    @JsonProperty("languageVersion")
    private LanguageVersion languageVersion;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("legend")
    private Legend legend;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("dateModified")
    private String dateModified;

    public Style() {}

    public String getName() { return name; }
    public WorkspaceRef getWorkspace() { return workspace; }
    public String getFormat() { return format; }
    public LanguageVersion getLanguageVersion() { return languageVersion; }
    public String getFilename() { return filename; }
    public Legend getLegend() { return legend; }
    public String getDateCreated() { return dateCreated; }
    public String getDateModified() { return dateModified; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkspaceRef {
        @JsonProperty("name")
        private String name;

        public WorkspaceRef() {}
        public String getName() { return name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkspaceRef that = (WorkspaceRef) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "WorkspaceRef{" +
                    "name=" + name +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LanguageVersion {
        @JsonProperty("version")
        private String version;

        public LanguageVersion() {}
        public String getVersion() { return version; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LanguageVersion that = (LanguageVersion) o;
            return Objects.equals(version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(version);
        }

        @Override
        public String toString() {
            return "LanguageVersion{" +
                    "version=" + version +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Legend {
        @JsonProperty("width")
        private int width;

        @JsonProperty("height")
        private int height;

        @JsonProperty("format")
        private String format;

        @JsonProperty("onlineResource")
        private String onlineResource;

        public Legend() {}
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public String getFormat() { return format; }
        public String getOnlineResource() { return onlineResource; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Legend that = (Legend) o;
            return Objects.equals(width, that.width)
                    && Objects.equals(height, that.height)
                    && Objects.equals(format, that.format)
                    && Objects.equals(onlineResource, that.onlineResource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height, format, onlineResource);
        }

        @Override
        public String toString() {
            return "Legend{" +
                    "width=" + width +
                    ", height=" + height +
                    ", format=" + format +
                    ", onlineResource=" + onlineResource +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Style that = (Style) o;
        return Objects.equals(name, that.name)
                && Objects.equals(workspace, that.workspace)
                && Objects.equals(format, that.format)
                && Objects.equals(languageVersion, that.languageVersion)
                && Objects.equals(filename, that.filename)
                && Objects.equals(legend, that.legend)
                && Objects.equals(dateCreated, that.dateCreated)
                && Objects.equals(dateModified, that.dateModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, workspace, format, languageVersion, filename, legend, dateCreated, dateModified);
    }

    @Override
    public String toString() {
        return "Style{" +
                "name=" + name +
                ", workspace=" + workspace +
                ", format=" + format +
                ", languageVersion=" + languageVersion +
                ", filename=" + filename +
                ", legend=" + legend +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }
}
