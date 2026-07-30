package io.github.kimbongjune.geoserverclient.dto.gwc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.Objects;

/**
 * DTO for a GWC file blob store. Maps {@code GET/PUT /gwc/rest/blobstores/{name}}.
 *
 * <p>This DTO covers {@code FileBlobStore} only (the most common type in GeoServer).
 * S3 and other blob store types are not modeled here.</p>
 *
 * <p><b>Known quirk (2026-07-29):</b> In JSON the default flag appears under {@code "@default"}
 * as a <b>String</b> (e.g. {@code "@default":"false"}). In XML it is
 * {@code <FileBlobStore default="true">} as expected.</p>
 *
 * <p>PUT requires XML — JSON PUT causes an XStream "Duplicate field" 500 error
 * (see GwcBlobStoreManager).</p>
 */
@JacksonXmlRootElement(localName = "FileBlobStore")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GwcFileBlobStore {

    private String id;
    private Boolean enabled;
    private String baseDirectory;
    private Integer fileSystemBlockSize;

    @JacksonXmlProperty(localName = "default", isAttribute = true)
    @JsonProperty("@default")
    private String defaultFlag;

    public GwcFileBlobStore() {}

    public GwcFileBlobStore(String id, String baseDirectory) {
        this.id = id;
        this.baseDirectory = baseDirectory;
        this.enabled = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getBaseDirectory() { return baseDirectory; }
    public void setBaseDirectory(String baseDirectory) { this.baseDirectory = baseDirectory; }

    public Integer getFileSystemBlockSize() { return fileSystemBlockSize; }
    public void setFileSystemBlockSize(Integer fileSystemBlockSize) { this.fileSystemBlockSize = fileSystemBlockSize; }

    /**   ("true"/"false")  —  boolean    . */
    public String getDefaultFlag() { return defaultFlag; }
    public void setDefaultFlag(String defaultFlag) { this.defaultFlag = defaultFlag; }

    public boolean isDefault() { return Boolean.parseBoolean(defaultFlag); }
    public GwcFileBlobStore setDefault(boolean isDefault) {
        this.defaultFlag = String.valueOf(isDefault);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwcFileBlobStore that = (GwcFileBlobStore) o;
        return Objects.equals(id, that.id)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(baseDirectory, that.baseDirectory)
                && Objects.equals(fileSystemBlockSize, that.fileSystemBlockSize)
                && Objects.equals(defaultFlag, that.defaultFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enabled, baseDirectory, fileSystemBlockSize, defaultFlag);
    }

    @Override
    public String toString() {
        return "GwcFileBlobStore{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", baseDirectory=" + baseDirectory +
                ", fileSystemBlockSize=" + fileSystemBlockSize +
                ", defaultFlag=" + defaultFlag +
                '}';
    }
}
