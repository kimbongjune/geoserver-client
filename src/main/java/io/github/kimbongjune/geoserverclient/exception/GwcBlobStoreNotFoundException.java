package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache blob store does not exist.
 */
public class GwcBlobStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public GwcBlobStoreNotFoundException(String blobStoreName) {
        super("GwcBlobStore", blobStoreName, null);
    }

    public GwcBlobStoreNotFoundException(String blobStoreName, String responseBody) {
        super("GwcBlobStore", blobStoreName, responseBody);
    }

    public String getBlobStoreName() {
        return getResourceName();
    }
}
