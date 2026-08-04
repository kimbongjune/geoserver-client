package io.github.kimbongjune.geoserverclient.dto.resource;

import java.util.Objects;

/**
 * Metadata extracted from a {@code HEAD /rest/resource/{path}} response's headers.
 * <p>
 * Unlike {@link ResourceMetadata} (which comes from {@code ?operation=metadata} and only works
 * reliably for directories), this works for both files and directories since it reads GeoServer's
 * {@code Resource-Type} / {@code Last-Modified} / {@code Content-Type} / {@code Content-Disposition}
 * response headers rather than a JSON/XML body.
 */
public class ResourceHeadInfo {

    private final String resourceType;
    private final String lastModified;
    private final String contentType;
    private final String fileName;

    public ResourceHeadInfo(String resourceType, String lastModified, String contentType, String fileName) {
        this.resourceType = resourceType;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    /** @return the resource type ({@code "resource"} or {@code "directory"}), from the {@code Resource-Type} header */
    public String getResourceType() {
        return resourceType;
    }

    /** @return the last-modified timestamp, from the {@code Last-Modified} header */
    public String getLastModified() {
        return lastModified;
    }

    /** @return the MIME type, from the {@code Content-Type} header */
    public String getContentType() {
        return contentType;
    }

    /** @return the file name, parsed from the {@code Content-Disposition} header (may be {@code null} for directories) */
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceHeadInfo that = (ResourceHeadInfo) o;
        return Objects.equals(resourceType, that.resourceType)
                && Objects.equals(lastModified, that.lastModified)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, lastModified, contentType, fileName);
    }

    @Override
    public String toString() {
        return "ResourceHeadInfo{" +
                "resourceType=" + resourceType +
                ", lastModified=" + lastModified +
                ", contentType=" + contentType +
                ", fileName=" + fileName +
                '}';
    }
}
