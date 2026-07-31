package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested security filter chain does not exist in GeoServer.
 */
public class FilterChainNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@code FilterChainNotFoundException} for the given chain name.
     * @param chainName the filter chain name
     */
    public FilterChainNotFoundException(String chainName) {
        super("FilterChain", chainName, null);
    }

    /**
     * Constructs a {@code FilterChainNotFoundException} with the raw response body.
     * @param chainName    the filter chain name
     * @param responseBody the raw HTTP response body, or {@code null}
     */
    public FilterChainNotFoundException(String chainName, String responseBody) {
        super("FilterChain", chainName, responseBody);
    }

    /**
     * Returns the filter chain name that was not found.
     * @return the chain name
     */
    public String getChainName() {
        return getResourceName();
    }
}
