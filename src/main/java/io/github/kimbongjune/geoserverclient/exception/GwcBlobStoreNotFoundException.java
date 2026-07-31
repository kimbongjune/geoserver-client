package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested GeoWebCache blob store does not exist.
 */
public class GwcBlobStoreNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code GwcBlobStoreNotFoundException} for the given blob store name.
     * @param blobStoreName the blob store name
     */
    public GwcBlobStoreNotFoundException(String blobStoreName) {
        super("GwcBlobStore", blobStoreName, null);
    }

    /**
     * Constructs a {@code GwcBlobStoreNotFoundException} with the raw response body.
     * @param blobStoreName the blob store name
     * @param responseBody  the raw HTTP response body, or {@code null}
     */
    public GwcBlobStoreNotFoundException(String blobStoreName, String responseBody) {
        super("GwcBlobStore", blobStoreName, responseBody);
    }

    /**
     * Returns the blob store name that was not found.
     * @return the blob store name
     */
    public String getBlobStoreName() {
        return getResourceName();
    }
}
